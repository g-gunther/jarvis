package com.gguproject.jarvis.plugin.deepspeech.interpreter;

import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarConfigurationFileService;
import com.gguproject.jarvis.plugin.speech.interpreter.processor.TimeProcessor;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Time;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeProcessorTest {
    private TimeProcessor processor = new TimeProcessor();

    private static Grammar grammar = new Grammar();

    @BeforeAll
    public static void before() throws URISyntaxException {
        URL res = TimeProcessorTest.class.getClassLoader().getResource("grammar.json");
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();

        GrammarConfigurationFileService grammarService = new GrammarConfigurationFileService();
        grammar = grammarService.parseConfigurationFile(absolutePath);
    }

    @Test
    public void test() throws InterpreterException {
        this.doTest("allume la tv à 8 heures du soir", "allume la tv", Time.build().add(TimeUnit.HOUR, 20).setExactTime());
        this.doTest("allume la tv à 8 heures 30 du soir", "allume la tv", Time.build().add(TimeUnit.HOUR, 20).add(TimeUnit.MINUTE, 30).setExactTime());
        this.doTest("allume la tv dans 2h", "allume la tv", Time.build().add(TimeUnit.HOUR, 2));
        this.doTest("allume la tv de la cuisine dans 2h et 30 minutes", "allume la tv de la cuisine", Time.build().add(TimeUnit.HOUR, 2).add(TimeUnit.MINUTE, 30));
        this.doTest("dans 2h30 allume la tv", "allume la tv", Time.build().add(TimeUnit.HOUR, 2).add(TimeUnit.MINUTE, 30));
        this.doTest("éteint la musique dans 30 minutes et 50 secondes", "éteint la musique", Time.build().add(TimeUnit.MINUTE, 30).add(TimeUnit.SECOND, 50));
    }

    public void doTest(String speech, String remainingSpeech, Time time) throws InterpreterException {
        SpeechContext context = new SpeechContext(speech);
        processor.process(grammar, context);
        assertEquals(remainingSpeech, context.getRemainingSpeech());
        assertEquals(0, context.getWordIteratorIndex());
        assertEquals(time, context.getTime());
    }
}
