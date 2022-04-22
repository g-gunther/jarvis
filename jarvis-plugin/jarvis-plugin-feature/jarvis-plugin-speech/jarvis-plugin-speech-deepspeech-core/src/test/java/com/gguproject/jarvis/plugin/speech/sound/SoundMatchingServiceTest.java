package com.gguproject.jarvis.plugin.speech.sound;

import com.gguproject.jarvis.plugin.deepspeech.sound.service.SoundCleaningService;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarWord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SoundMatchingServiceTest {

    class SoundTest {
        private String value1;
        private String value2;
        private boolean match;

        public SoundTest(String value1, String value2, boolean match) {
            this.value1 = value1;
            this.value2 = value2;
            this.match = match;
        }

        public String getValue1() {
            return this.value1;
        }

        public String getValue2() {
            return this.value2;
        }
    }

    List<SoundTest> tests = new ArrayList<>();

    @Test
    public void test() {
        tests.add(new SoundTest("met la musique fourre-tout", "mais la musique fourtou", true));
        tests.add(new SoundTest("jarvis", "jardis", true));
        tests.add(new SoundTest("jarvis", "jadis", false));
        tests.add(new SoundTest("met le film fatal", "me le film fatal", true));
        tests.add(new SoundTest("met le film dikkenek", "melfi di cemec", false));
        tests.add(new SoundTest("met le film dikkenek", "male film di cenec", true));
        tests.add(new SoundTest("allume la lumière", "allumiere", false));
        tests.add(new SoundTest("met le film intouchable", "melfi intouchable", false));
        tests.add(new SoundTest("éteint la lumière", "étant la lumière", true));

        tests.add(new SoundTest("met la série family business", "mais la série familie baisse", true));
        tests.add(new SoundTest("met la série family business épisode un", "mais la série familie dines episode un", true));


        tests.add(new SoundTest("baisse le son de la télévision", "besse le son de la télévision", true));


        tests.add(new SoundTest("baisse le volume de la musique dans le salon", "besse le volume de la musique dans le salon", true));

        tests.add(new SoundTest("éteint toi", "tint toi", true));
        tests.add(new SoundTest("éteint toi", "éteint ou", true));
        tests.add(new SoundTest("éteint toi", "et un toit", false));

        tests.add(new SoundTest("met la musique légende du rock", "let la musique légende du rock", true));
        tests.add(new SoundTest("met la musique légende du rock", "mais la musique les gens de duroc", true));
        tests.add(new SoundTest("met la musique légende du rock", "me la musique légende du roc", true));

        tests.add(new SoundTest("met le film hors norme", "mais le fil hornorm", true));
        tests.add(new SoundTest("met le film hors norme", "ma le film honore", false));


        tests.add(new SoundTest("astérix mission cléopatre", "stérique se mission cleopatre", true));
        tests.add(new SoundTest("astérix mission cléopatre", "mêle film stériques mission claustre", false));

        SoundCleaningService service = new SoundCleaningService(null);

        tests.stream().forEach(values -> {
//			System.out.println(values.value1 + " -- " + values.value2);
//			System.out.println(SoundMatch.encode(values.value1));
//			System.out.println(SoundMatch.encode(values.value2));
//			System.out.println("distance: " + StringUtils.getLevenshteinDistance(SoundMatch.encode(values.value1), SoundMatch.encode(values.value2)));
//			System.out.println("threshold: " + SoundMatch.calculateThreshold(values.value1, values.value2));
//			System.out.println("---");
            assertEquals(values.match, GrammarWord.build(service.clean(values.value1)).match(service.clean(values.value2)));
        });
    }
}
