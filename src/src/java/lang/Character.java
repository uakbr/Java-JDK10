/*
 * W% 95/11/13  
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package java.lang;

/**
 * The Character class provides an object wrapper for Character data values
 * and serves as a place for character-oriented operations.  A wrapper is useful
 * because most of Java's utility classes require the use of objects.  Since characters
 * are not objects in Java, they need to be "wrapped" in a Character instance.
 * @version 	1.26, 11/13/95
 * @author	Lee Boynton
 */

public final
class Character extends Object {
    /**
     * The minimum radix available for conversion to and from Strings.  
     * The lowest minimum value that a radix can be is 2.
     * @see Integer#toString
     */
    public static final int MIN_RADIX = 2;

    /**
     * The maximum radix available for conversion to and from Strings.  The
     * largest maximum value that a radix can have is 36.
     * @see Integer#toString
     */
    public static final int MAX_RADIX = 36;

    static char downCase[]; /* case folding translation table upper => lower */
    static char upCase[]; /* case folding translation table lower => upper*/
    static {
        char down[] = new char[256];
        char up[] = new char[256];
        for (int i = 0 ; i < 256 ; i++) {
            down[i] = up[i] = (char) i;
	}
        for (int lower = 'a' ; lower <= 'z' ; lower++) {
            int upper = (lower + ('A' - 'a'));
            up[lower] = (char)upper;
            down[upper] = (char)lower;
	}

        for (int lower = 0xE0; lower <= 0xFE; lower++) {
            if (lower != 0xF7) { // multiply and divide
                int upper = (lower + ('A' - 'a'));
                up[lower] = (char)upper;
                down[upper] = (char)lower;
            }
        }
	downCase = down;
	upCase = up;
    }


    /**
     * Determines if the specified character is ISO-LATIN-1 lower case.
     * @param ch	the character to be tested
     * @return 	true if the character is lower case; false otherwise.
     */
    public static boolean isLowerCase(char ch) {
	// its a lower case if it has a different uppercase, or it is a
	// German double-S or Dutch ij.
	return ((ch <= '\u00FF') && 
		((upCase[ch] != ch) || (ch == '\u00df') || (ch == '\u00ff')));
    }
    
    /**
     * Determines if the specified character is ISO-LATIN-1 upper case.
     * @param ch	the character to be tested
     * @return 	true if the character is upper case; false otherwise.
     */
    public static boolean isUpperCase(char ch) {
	// it's upper case if it has a different lower case
        return (ch <= '\u00FF' && downCase[ch] != ch);
    }

    /**
     * Determines if the specified character is a ISO-LATIN-1 digit.
     * @param ch	the character to be tested
     * @return 	true if this character is a digit; false otherwise.
     */
    public static boolean isDigit(char ch) {
	return (ch >= '0') && (ch <= '9');
    }


    /**
     * Determines if the specified character is ISO-LATIN-1 white space according to Java.
     * @param ch		the character to be tested
     * @return  true if the character is white space; false otherwise.
     */
    public static boolean isSpace(char ch) {
	switch (ch) {
	case ' ':
	case '\t':
	case '\f': // form feed
	case '\n':
	case '\r':
		return (true);
	}
	return (false);
    }

    /**
     * Returns the lower case character value of the specified ISO-LATIN-1 
     * character. Characters that are not upper case letters are returned 
     * unmodified. 
     * @param ch	the character to be converted
     */
    public static char toLowerCase(char ch) {
	return (ch <= '\u00FF') ? downCase[ch] : ch;
    }

    /**
     * Returns the upper case character value of the specified ISO-LATIN-1 
     * character.
     * Characters that are not lower case letters are returned unmodified.
     *
     * Note that German ess-zed and latin small letter y diaeresis have no
     * corresponding upper case letters, even though they are lower case.
     * There is a capital y diaeresis, but not in ISO-LATIN-1...
     * @param ch	the character to be converted
     */
    public static char toUpperCase(char ch) {
	return (ch <= '\u00FF') ? upCase[ch] : ch;
    }

    /**
     * Returns the numeric value of the character digit using the specified
     * radix. If the character is not a valid digit, it returns -1.
     * @param ch		the character to be converted
     * @param radix 	the radix
     */
    public static int digit(char ch, int radix) {
	if (radix >= MIN_RADIX && radix <= MAX_RADIX) {
	    if (radix <= 10) {
		char max = (char)('0' + radix - 1);
		if ((ch >= '0') && (ch <= max)) {
		    return (ch - '0');
		}
	    } else {
		ch = toLowerCase(ch);
		if ((ch >= '0') && (ch <= '9')) {
		    return (ch - '0');
		} 

		char max = (char)('a' + radix - 11);
		if (ch >= 'a' && ch <= max) {
		    return (10 + ch - 'a');
		}
	    }
	}
        return -1;
    }

    /**
     * Returns the character value for the specified digit in the specified
     * radix. If the digit is not valid in the radix, the 0 character
     * is returned.
     * @param digit	the digit chosen by the character value
     * @param radix 	the radix containing the digit
     */
    public static char forDigit(int digit, int radix) {
	if ((digit >= radix) || (digit < 0)) {
	    return '\0';
	}
	if ((radix < MIN_RADIX) || (radix > MAX_RADIX)) {
	    return '\0';
	}
	if (digit < 10) {
	    return (char)('0' + digit);
	} 
	return (char)('a' + digit - 10);
    }

    /**
     * The value of the Character.
     */
    private char value;

    /**
     * Constructs a Character object with the specified value.
     * @param value value of this Character object
     */
    public Character(char value) {
	this.value = value;
    }

    /**
     * Returns the value of this Character object.
     */
    public char charValue() {
	return value;
    }

    /**
     * Returns a hashcode for this Character.
     */
    public int hashCode() {
	return (int)value;
    }

    /**
     * Compares this object against the specified object.
     * @param obj		the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Character)) {
	    return value == ((Character)obj).charValue();
	} 
	return false;
    }

    /**
     * Returns a String object representing this character's value.
     */
    public String toString() {
	char buf[] = {value};
	return String.valueOf(buf);
    }
}

