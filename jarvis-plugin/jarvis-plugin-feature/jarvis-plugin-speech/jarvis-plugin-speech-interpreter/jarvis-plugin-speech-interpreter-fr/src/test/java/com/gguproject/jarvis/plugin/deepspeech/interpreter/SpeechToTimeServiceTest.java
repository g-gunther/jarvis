package com.gguproject.jarvis.plugin.deepspeech.interpreter;

import com.gguproject.jarvis.plugin.speech.interpreter.processor.helper.SpeechToTimeService;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeElement;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeSpeechParserException;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class SpeechToTimeServiceTest {

    @Test
    public void test() throws TimeSpeechParserException {
        this.doTest("8 heures 30 du soir", new TimeElement(TimeUnit.HOUR, 20), new TimeElement(TimeUnit.MINUTE, 30));

        this.doTest("2h30", new TimeElement(TimeUnit.HOUR, 2), new TimeElement(TimeUnit.MINUTE, 30));
        this.doTest("2h30m40", new TimeElement(TimeUnit.HOUR, 2), new TimeElement(TimeUnit.MINUTE, 30), new TimeElement(TimeUnit.SECOND, 40));
        this.doTest("2 heures", new TimeElement(TimeUnit.HOUR, 2));
        this.doTest("2 heures et 29", new TimeElement(TimeUnit.HOUR, 2), new TimeElement(TimeUnit.MINUTE, 29));
        this.doTest("2 heures et 1 secondes", new TimeElement(TimeUnit.HOUR, 2), new TimeElement(TimeUnit.SECOND, 1));

        this.doTest("2 heures 46", new TimeElement(TimeUnit.HOUR, 2), new TimeElement(TimeUnit.MINUTE, 46));

        this.doTest("2 heures du matin", new TimeElement(TimeUnit.HOUR, 2));
        this.doTest("7 heures du soir et 30 minutes", new TimeElement(TimeUnit.HOUR, 19), new TimeElement(TimeUnit.MINUTE, 30));
        this.doTest("8 heures du soir", new TimeElement(TimeUnit.HOUR, 20));
        this.doTest("2 heures de l'après-midi", new TimeElement(TimeUnit.HOUR, 14));

        this.doTest("midi", new TimeElement(TimeUnit.HOUR, 12));
        this.doTest("midi 30", new TimeElement(TimeUnit.HOUR, 12), new TimeElement(TimeUnit.MINUTE, 30));
        this.doTest("minuit", new TimeElement(TimeUnit.HOUR, 0));
    }

    public void doTest(String value, TimeElement... elements) {
        List<TimeElement> timeElements = Arrays.asList(elements);
        try {
            List<TimeElement> results = new SpeechToTimeService().analyse(value);

            for (TimeElement expectedTimeElement : timeElements) {
                if (!results.contains(expectedTimeElement)) {
                    Assertions.fail("Time element: " + expectedTimeElement + " not found");
                }
            }

            for (TimeElement resultTimeElements : results) {
                if (!timeElements.contains(resultTimeElements)) {
                    Assertions.fail("Time element: " + resultTimeElements + " in result but not expected");
                }
            }
        } catch (TimeSpeechParserException e) {
            Assertions.fail(e.getMessage());
        }

    }
}
