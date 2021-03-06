package com.gguproject.jarvis.plugin.deepspeech.interpreter;

import com.gguproject.jarvis.core.ioc.utils.ReflectionUtils;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterServiceImpl;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarConfigurationFileService;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Time;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterServiceTest {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(InterpreterServiceTest.class);

    private static InterpreterServiceImpl service = new InterpreterServiceImpl(null, null);

    private static Grammar grammar = new Grammar();

    @BeforeAll
    public static void before() throws URISyntaxException {
        URL res = TimeProcessorTest.class.getClassLoader().getResource("grammar.json");
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();

        GrammarConfigurationFileService grammarService = new GrammarConfigurationFileService();
        grammar = grammarService.parseConfigurationFile(absolutePath);

        try {
            ReflectionUtils.setFieldByType(service, Grammar.class, grammar);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws InterpreterException {
        this.doTest("affiche la m??t??o", Command.builder("WEATHER", "DISPLAY").build());

        this.doTest("allume la tv", Command.builder("TV", "START").build());
        this.doTest("mets la deux", Command.builder("TV", "SET").data("deux").build());
        this.doTest("mets tf1", Command.builder("TV", "SET").data("tf1").build());
        this.doTest("??teint toi", Command.builder("SELF", "STOP").build());
        this.doTest("arr??te toi", Command.builder("SELF", "STOP").build());
        this.doTest("mets la musique l??gende du rock dans la cuisine", Command.builder("MUSIC", "SET").data("l??gende du rock").localization("KITCHEN").build());
        this.doTest("mets la playlist fourre-tout", Command.builder("MUSIC", "SET").data("fourre-tout").build());
        this.doTest("mets la musique", Command.builder("MUSIC", "SET").build());
        this.doTest("allume la musique", Command.builder("MUSIC", "START").build());
        this.doTest("mets la lumi??re dans la chambre", Command.builder("LIGHT", "SET").localization("ROOM").build());
        this.doTest("coupe le son de la tv", Command.builder("TV", "MUTE").build());
        this.doTest("mets le son de la tv", Command.builder("TV", "UNMUTE").build());
        this.doTest("mais on de la tv", Command.builder("TV", "UNMUTE").build());
        this.doTest("allume la t??l??", Command.builder("TV", "START").build());
        this.doTest("??teint le son de la tv", Command.builder("TV", "MUTE").build());
        this.doTest("??tant le son de la tv", Command.builder("TV", "MUTE").build());

        this.doTest("??teint la t??l??vision dans 30 minutes", Command.builder("TV", "STOP").time(Time.build().add(TimeUnit.MINUTE, 30)).build());
        this.doTest("??teint la musique dans 2h30", Command.builder("MUSIC", "STOP").time(Time.build().add(TimeUnit.HOUR, 2).add(TimeUnit.MINUTE, 30)).build());
        this.doTest("??teint la musique dans 30 minutes et 50 secondes", Command.builder("MUSIC", "STOP").time(Time.build().add(TimeUnit.MINUTE, 30).add(TimeUnit.SECOND, 50)).build());
        this.doTest("dans 2h allume la lumi??re", Command.builder("LIGHT", "START").time(Time.build().add(TimeUnit.HOUR, 2)).build());
        this.doTest("dans 10 secondes allume la lumi??re de la cuisine", Command.builder("LIGHT", "START").localization("KITCHEN").time(Time.build().add(TimeUnit.SECOND, 10)).build());
        this.doTest("allume la lumi??re dans la cuisine et la salle ?? manger", Command.builder("LIGHT", "START").localization("KITCHEN").localization("LIVING").build());
        this.doTest("dans 10 secondes allume la lumi??re dans la cuisine et la salle ?? manger", Command.builder("LIGHT", "START").localization("KITCHEN").localization("LIVING").time(Time.build().add(TimeUnit.SECOND, 10)).build());
        this.doTest("met le r??veil ?? 8 heures du matin", Command.builder("ALARM", "SET").time(Time.build().add(TimeUnit.HOUR, 8).setExactTime()).build());
        this.doTest("met le r??veil ?? 8 heures du soir", Command.builder("ALARM", "SET").time(Time.build().add(TimeUnit.HOUR, 20).setExactTime()).build());
        this.doTest("met le r??veil ?? 2 heures de l'apr??s-midi", Command.builder("ALARM", "SET").time(Time.build().add(TimeUnit.HOUR, 14).setExactTime()).build());
        this.doTest("met le minuteur dans 7 minutes 30", Command.builder("TIMER", "SET").time(Time.build().add(TimeUnit.MINUTE, 7).add(TimeUnit.SECOND, 30)).build());

        this.doTest("dis-moi l'heure", Command.builder("TIME", "SAY").build());
        this.doTest("donne-moi l'heure", Command.builder("TIME", "GIVE").build());

        this.doTest("affiche la m??t??o de demain", Command.builder("WEATHER", "DISPLAY").time(Time.build().add(TimeUnit.DAY, 1)).build());
        this.doTest("affiche la m??t??o de demain ?? Paris", Command.builder("WEATHER", "DISPLAY").time(Time.build().add(TimeUnit.DAY, 1)).data("?? Paris").build());
        this.doTest("affiche la m??t??o ?? F??camp", Command.builder("WEATHER", "DISPLAY").data("?? F??camp").build());
    }

    private void doTest(String speech, Command expectedComment) throws InterpreterException {
        Command c = service.interprets(speech);
        LOGGER.debug("{}", c);
        assertEquals(expectedComment.getAction(), c.getAction());
        assertEquals(expectedComment.getContext(), c.getContext());
        assertEquals(expectedComment.getData(), c.getData());
        assertEquals(expectedComment.getLocalizations(), c.getLocalizations());
        assertEquals(expectedComment.getTime(), c.getTime());
    }
}
