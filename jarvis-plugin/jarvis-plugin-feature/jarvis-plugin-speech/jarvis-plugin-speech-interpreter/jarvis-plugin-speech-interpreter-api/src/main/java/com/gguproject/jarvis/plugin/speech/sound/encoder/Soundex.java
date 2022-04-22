package com.gguproject.jarvis.plugin.speech.sound.encoder;

/**
 * https://www.javatips.net/api/data-quality-master/dataquality-record-linkage/src/main/java/org/talend/dataquality/record/linkage/contribs/algorithm/SoundexFR.java#
 * @author guillaumegunther
 *
 */
public class Soundex implements SoundEncoder {

	private static final String[] GROUP1_INPUT = { "GUI", "GUE", "GA", "GO", "GU", "CA", "CO", "CU", "Q", "CC", "CK" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$

    private static final String[] GROUP1_OUTPUT = { "KI", "KE", "KA", "KO", "K", "KA", "KO", "KU", "K", "K", "K" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$

    private static final String[] GROUP2_INPUT = { "MAC", "ASA", "KN", "PF", "SCH", "PH" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private static final String[] GROUP2_OUTPUT = { "MCC", "AZA", "NN", "FF", "SSS", "FF" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private static final char[] VOWELS = { 'E', 'I', 'O', 'U' };

    private static final int ONE_NINE_ONE = 191;

    private static final int FOUR = 4;


    /**
     * Encode a string phoneticaly with SoundEx.
     * 
     * This algorithm is sampled from the code-snippers on http://www.sourceforge.net/
     * 
     * @param str The string to encode
     * @return the encoded version of the strToEncode
     */
    public String encode(String str) {
        if (str == null) {
            return null;
        }

        // Trim the token from trailing spaces
        // str.trim(); // MOD scorreia 2010-02-18 replace str by trimmed str
        String tempStr = str.trim();
        int size = tempStr.length();
        char ch;
        int i;

        // If the token is empty, return an empty 4 character-long string
        if (size == 0) {
            return "    "; //$NON-NLS-1$
        }

        // Convert all charatcers to uppercase
        tempStr = tempStr.toUpperCase();

        // If the token is one character long, return the character and 3 spaces
        if (size == 1) {
            return tempStr + "   "; //$NON-NLS-1$
        }

        // Wrap the String into a Stringbuffer
        StringBuilder word = new StringBuilder(tempStr);

        // Remove all diacretical marks

        for (i = 0; i < size; i++) {
            ch = word.charAt(i);

            // Remove all special characters and numbers
            if (!Character.isLetter(ch)) {
                word.deleteCharAt(i);
                size--;
                i--;
                continue;
            }

            if (ch > ONE_NINE_ONE) {
                word.setCharAt(i, removeDiacriticalMark(ch));
            }
        }

        // Replace the group1 of substrings with their corresponding outputs
        size = GROUP1_INPUT.length;
        tempStr = word.toString();

        for (i = 0; i < size; i++) {
            tempStr = tempStr.replaceAll(GROUP1_INPUT[i], GROUP1_OUTPUT[i]);
        }

        // Replace all vowels (E,I,O,U) with A (excluding the fist character?)
        size = tempStr.length();

        for (i = 1; i < size; i++) {
            ch = tempStr.charAt(i);
            if (isVowel(ch)) {
                tempStr = tempStr.replace(ch, 'A');
            }
        }

        // Replace the group2 of substrings with their corresponding outputs
        size = GROUP2_INPUT.length;

        for (i = 0; i < size; i++) {
            tempStr = tempStr.replaceAll(GROUP2_INPUT[i], GROUP2_OUTPUT[i]);
        }

        // Remove all Hs if they are not preceeded by C or S
        size = tempStr.length();
        word.setLength(0);
        word.append(tempStr);

        for (i = 0; i < size; i++) {
            if (word.charAt(i) == 'H' && !(i > 0 && (word.charAt(i - 1) == 'C' || word.charAt(i - 1) == 'S'))) {
                word.deleteCharAt(i);
                size--;
                i--;
            }
        }

        // Remove all Ys, unless they are preceeded by As
        for (i = 0; i < size; i++) {
            if (word.charAt(i) == 'Y' && !(i > 0 && word.charAt(i - 1) == 'A')) {
                word.deleteCharAt(i);
                size--;
                i--;
            }
        }

        // MOD scorreia 2010-02-18 ensure that some characters remain
        if (size <= 0) { // could be zero when input is "y" for example then it would result in a out of bound index
            return "    "; //$NON-NLS-1$
        }

        // Remove the following characters (A, T, D, S) if they are the last character
        ch = word.charAt(size - 1);
        if (ch == 'A' || ch == 'T' || ch == 'D' || ch == 'S') {
            word.deleteCharAt(size - 1);
            size--;
        }

        // Remove all As. Keep it if it is the first character of the token
        for (i = 1; i < size; i++) {
            if (word.charAt(i) == 'A') {
                word.deleteCharAt(i);
                size--;
                i--;
            }
        }

        // Remove all subsets of similar successive characters
        removeSimilarGroupChars(word);

        // Keep only the first 4 characters (if the token is smaller, add spaces).
        size = word.length();

        if (word.length() >= FOUR) {
            word.setLength(FOUR);
        } else {
            while (size++ < FOUR) {
                word.append(" "); //$NON-NLS-1$
            }
        }

        return word.toString();
    }

    /**
     * Tests if the character is a vowel.
     * 
     * @param ch the character to test
     * @return true if character is a vowel, otherwise, false
     */
    private boolean isVowel(char ch) {
        int size = VOWELS.length;

        for (int i = 0; i < size; i++) {
            if (ch == VOWELS[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes duplicate characters in a word if they are in succession.
     * 
     * @param word the string buffer containing the word to check and modify
     */
    private void removeSimilarGroupChars(StringBuilder word) {
        int size = word.length();

        for (int i = 0; i < size; i++) {
            if (i < size - 1 && word.charAt(i) == word.charAt(i + 1)) {
                word.deleteCharAt(i);
                size--;
                i--;
            }
        }
    }
    
    /**
     * Removes diacritical mark from a character.
     * 
     * @param ch a character
     * @return the same input character without the diacritical mark if any.
     */
    public static char removeDiacriticalMark(char c) {

        if (c < 192)
            return c;
        if (c >= 192 && c <= 197)
            return 'A';
        if (c == 199)
            return 'C';
        if (c >= 200 && c <= 203)
            return 'E';
        if (c >= 204 && c <= 207)
            return 'I';
        if (c == 209)
            return 'N';
        if (c >= 210 && c <= 214)
            return 'O';
        if (c >= 217 && c <= 220)
            return 'U';
        if (c == 221)
            return 'Y';
        if (c >= 224 && c <= 229)
            return 'a';
        if (c == 231)
            return 'c';
        if (c >= 232 && c <= 235)
            return 'e';
        if (c >= 236 && c <= 239)
            return 'i';
        if (c == 241)
            return 'n';
        if (c >= 242 && c <= 246)
            return 'o';
        if (c >= 249 && c <= 252)
            return 'u';
        if (c == 253 || c == 255)
            return 'y';

        return c;
    }
}
