/*
 * @(#)Character.java	1.13 95/01/31  
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
 * The Character class provides an object wrapper for character data values,
 * and serves as a place for character-oriented operations.
 * @version 	1.13, 31 Jan 1995
 * @author	Lee Boynton
 */

public final
class Character extends Object {
    /**
     * The minimum radix available for conversion to/from strings.
     * @see Integer#toString
     */
    public static final int MIN_RADIX = 2;

    /**
     * The maximum radix available for conversion to/from strings.
     * @see Integer#toString
     */
    public static final int MAX_RADIX = 36;

    /**
     * Determines if a character is lower case.
     * @param ch	the character to be tested
     * @return 	true if the character is lower case.
     */
    public static boolean isLowerCase(char ch) {
	return (ch >= 'a') && (ch <= 'z');
    }
    
    /**
     * Determines if a character is a digit.
     * @param ch	the character to be tested
     * @return 	true if the character is a digit.
     */
    public static boolean isDigit(char ch) {
	return (ch >= '0') && (ch <= '9');
    }

    /**
     * Determines if a character is upper case.
     * @param ch	the character to be tested
     * @return 	true if the character is upper case.
     */
    public static boolean isUpperCase(char ch) {
	return (ch >= 'A') && (ch <= 'Z');
    }

    /**
     * Returns the lower case character value of the specified character
     * value. Characters that are not upper case letters are returned
     * unmodified.
     * @param ch	the character to be converted
     * @return	the lower case version of ch
     */
    public static char toLowerCase(char ch) {
	if (isUpperCase(ch)) {
	    int offset = 'a' - 'A';
	    return (char)(ch + offset);
	}
	return ch;
    }

    /**
     * Returns the upper case character value of the specified character
     * value. Characters that are not lower case letters are returned
     * unmodified.
     * @param ch	the character to be converted
     * @return	the upper case version of ch
     */
    public static char toUpperCase(char ch) {
	if (isLowerCase(ch)) {
	    int offset = 'A' - 'a';
	    return (char)(ch + offset);
	}
	return ch;
    }

    /**
     * Returns the numeric value of the character digit using the specified
     * radix. If the character is not a valid digit, return -1.
     * @param ch		the character to be converted
     * @param radix 	the radix
     * @return		the corresponding numeric value
     */
    public static int digit(char ch, int radix) {
	if (radix >= MIN_RADIX && radix <= MAX_RADIX) {
	    if (radix <= 10) {
		char max = (char)('0' + radix - 1);
		if ((ch >= '0') && (ch <= max)) {
		    return (ch - '0');
		}
	    } else {
		char lowerChar = toLowerCase(ch);
		char max = (char)('a' + radix - 11);
		if ((ch >= '0') && (ch <= '9')) {
		    return (ch - '0');
		} else if (ch >= 'a' && ch <= max) {
		    return (10 + ch - 'a');
		}
	    }
	}
        return -1;
    }

    /**
     * Returns the character value for the specified digit in the specified
     * radix. If the digit is not valid in that radix, the 0 character
     * is returned.
     * @param digit	the digit
     * @param radix 	the radix
     * @return		the corresponding character
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
     * The value of the character.
     */
    private char value;

    /**
     * Constructs a Character object with the specified value.
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
     * Returns a String object representing this character's value.
     * @return a String representing the value of the character.
     */
    public String toString() {
	char buf[] = {value};
	return String.valueOf(buf);
    }
}

