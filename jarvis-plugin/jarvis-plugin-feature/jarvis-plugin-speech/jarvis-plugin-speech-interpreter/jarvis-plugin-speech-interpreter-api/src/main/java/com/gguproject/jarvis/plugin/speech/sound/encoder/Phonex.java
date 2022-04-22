package com.gguproject.jarvis.plugin.speech.sound.encoder;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * https://github.com/NicolasGeraud/solr-phonex/blob/master/src/main/java/org/apache/commons/codec/language/Phonex.java
 * @author guillaumegunther
 */
public class Phonex implements SoundEncoder{

	private static final Map<String, String> replacements = new LinkedHashMap<>();
	static {
		// Remove special chars
		replacements.put("\\*", "");
		replacements.put("\\?", "");
		
		// 1. Replace 'y' by 'i'
		replacements.put("Y", "I");

		// 2. Delete 'h' if not after 'c', 's' or 'p'
		replacements.put("([^CSP])H", "$1");
				
		// 3. Replace 'ph' by 'f'
		replacements.put("PH", "F");
		
		// 4. Replace gan / gam / gain / gaim (g by k)
		replacements.put("G(AN|AM|AIN|AIM)", "K$1");
		
		// 5. Replace ain / ein / aim / eim by 'yn' if followed by 'a', 'e', 'i', 'o' or 'u'
		replacements.put("((A|E)I(N|M))([AEIOU]{1})", "YN$4");
		
		// 6. Replace 3 letters groups (sound 'eau', 'oua', 'ein', 'eim', 'ain', 'aim')
		replacements.put("EAU", "O");
		replacements.put("OUA", "2");
		replacements.put("(A|E)I(N|M)", "4");

		// 7. Replace sound 'é'
		replacements.put("(É|È|Ê|EI|AI)", "Y");
		replacements.put("E(R|SS|T)", "Y$1");

		replacements.put("(M)+M", "$1");
		replacements.put("(N)+N", "$1");
			
		// 8. Replace 'ean' by 1 (except if followed by 'a', 'e', 'i', 'o' ,'u', '1', '2', '3', '4')
		replacements.put("(?:A|E)(?:N|M)([^AEIOU1-4]|[ ]|\\Z)", "1$1");
		
		// 9. Replace 'in' by 4
		replacements.put("IN", "4");

		// 9.
		replacements.put("([AEIOU1-4]{1})S([AEIOU1-4]{1})", "$1Z$2");

		// 10.
		replacements.put("(OE|EU)", "E");
		replacements.put("AU", "O");
		replacements.put("O(I|Y)", "2");
		replacements.put("OU", "3");

		// Replace 'sh', 'ch', 'sch' by 5
		replacements.put("(S|C|SC)H", "5");
		
		// Replace 'ss', 'sc' by 's'
		replacements.put("S(S|C)", "S");

		// 12.
		replacements.put("C(E|I)", "S");

		// 13.
		replacements.put("(C|QU|GU|Q)", "K");
		replacements.put("G(A|O|Y)", "K$1");

		// 14.
		replacements.put("A", "O");
		replacements.put("D", "T");
		replacements.put("P", "T");
		replacements.put("J", "G");
		replacements.put("B", "F");
		replacements.put("V", "F");
		replacements.put("M", "N");

		// 15.
		replacements.put("(.)\\1*", "$1");
	}
	
	public String encode(String txt) {
		if (StringUtils.isEmpty(txt)) {
			return StringUtils.EMPTY;
		}

		String chaine = txt.toUpperCase(Locale.FRENCH);

		// remove H if first leter 
		if (chaine.startsWith("H")) {
			chaine = chaine.substring(1, chaine.length());
		}

		for(Entry<String, String> entry : replacements.entrySet()) {
			chaine = chaine.replaceAll(entry.getKey(), entry.getValue());
		}

		// 16. Remove ending chars
		if (chaine.endsWith("T") || chaine.endsWith("X") || chaine.endsWith("E") || chaine.endsWith("S") || chaine.endsWith("Z"))
			chaine = chaine.substring(0, chaine.length() - 1);
		
		return chaine;
	}
}
