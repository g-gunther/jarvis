package com.gguproject.jarvis.plugin.deepspeech.interpreter;


import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarConfigurationFileService;
import com.gguproject.jarvis.plugin.speech.interpreter.processor.LocalizationProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class LocalizationProcessorTest {
    private LocalizationProcessor processor = new LocalizationProcessor();

    private static Grammar grammar = new Grammar();

    @BeforeAll
    public static void before() throws URISyntaxException {
        URL res = LocalizationProcessorTest.class.getClassLoader().getResource("grammar.json");
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();

        GrammarConfigurationFileService grammarService = new GrammarConfigurationFileService();
        grammar = grammarService.parseConfigurationFile(absolutePath);
    }

    @Test
    public void test() throws InterpreterException {
        this.doTest("allume la tv de la bibi", "allume la tv de la bibi");
        this.doTest("allume la tv de la chambre", "allume la tv", "ROOM");
        this.doTest("allume la tv de la salle à manger", "allume la tv", "LIVING");
        this.doTest("dans la salle à manger allume la tv", "allume la tv", "LIVING");
        this.doTest("allume la tv de la salle à manger et dans la cuisine", "allume la tv", "LIVING", "KITCHEN");
    }

    public void doTest(String speech, String remainingSpeech, String... localizations) throws InterpreterException {
        SpeechContext context = new SpeechContext(speech);
        processor.process(grammar, context);
        assertEquals(remainingSpeech, context.getRemainingSpeech());
        assertEquals(0, context.getWordIteratorIndex());

        for (String loc : localizations) {
            if (!context.getLocalizations().contains(loc)) {
                fail("Can't find localization: '" + loc + "' in result");
            }
        }

        List<String> expectedLocalizations = Arrays.asList(localizations);
        for (String loc : context.getLocalizations()) {
            if (!expectedLocalizations.contains(loc)) {
                fail("Found '" + loc + "' in result but wasn't expected");
            }
        }
    }
}
