/*
 * @(#)Integer.java	1.11 95/01/31  
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
 * The Integer class is a wrapper integer values.
 * @version 	1.11, 31 Jan 1995
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */
public final
class Integer extends Number {
    /**
     * The minimum value an integer can have.
     */
    public static final int   MIN_VALUE = 0x80000000;

    /**
     * The maximum value an integer can have.
     */
    public static final int   MAX_VALUE = 0x7fffffff;

    /**
     * Returns a string object representing the specified integer in
     * the specified radix.
     * @param i		the integer to be converted
     * @param radix	the radix
     * @return 		a string representation of the integer
     * @see Character#MIN_RADIX
     * @see Character#MAX_RADIX
     */
    public static String toString(int i, int radix) {
	StringBuffer buf;
	int digitCount = 0;
	boolean minval = false;
	boolean negative = false;
	if (i == 0x80000000) {
	    if (radix == 2)
		return "1000000000000000000000000000000";
	    minval = true;
	    negative = true;
	    i = MAX_VALUE;
	} else if (i < 0) {
	    i = -i;
	    negative = true;
	}
	buf = new StringBuffer(32);
	while (i > 0) {
	    if (i < radix) {
		buf.appendChar(Character.forDigit(i,radix));
		i = 0;
	    } else {
		int j = i % radix;
		i = i / radix;
		buf.appendChar(Character.forDigit(j,radix));
	    }
	    digitCount++;
	}
	if (digitCount > 0) {
	    int j = buf.length();
	    int k = 0;
	    char tmp[];
	    if (negative) {
		tmp = new char[j + 1];
		tmp[0] = '-';
		k = 1;
	    } else {
		tmp = new char[j];
	    }
	    i = 0;
	    while (j-- > 0) {
		tmp[j+k] = buf.charAt(i++);
	    }
	    if (minval) {
		tmp[tmp.length-1]++;
	    }
	    return String.valueOf(tmp);
	} else
	    return "0";
    }

    /**
     * Returns a String object representing an integer. The radix
     * is assumed to be 10.
     * @param i	the integer to be converted
     * @return 	a string representation of the integer
     */
    public static String toString(int i) {
	return toString(i,10);
    }
    
    /**
     * Assuming the string represents an integer, returns that integer's
     * value. Throws an error if the string cannot be parsed as an int.
     * @param s		the string containing the integer
     * @param radix 	the radix to be used
     * @return 		the integer value of the string
     * @exception	NumberFormatException The string does not contain a parsable integer
     */
    public static int parseInt(String s, int radix) {
	int result = 0;
	boolean negative = false;
	int i=0, max = s.length();
	if (max > 0) {
	    if (s.charAt(0) == '-') {
		negative = true;
		i++;
	    }
	    while (i < max) {
		int digit = Character.digit(s.charAt(i++),radix);
		if (digit < 0)
		    throw new NumberFormatException(s);
		result = result * radix + digit;
	    }
	} else
	    throw new NumberFormatException(s);
	if (negative)
	    return -result;
	else
	    return result;
    }

    /**
     * Assuming the string represents an integer, returns that integer's
     * value. Throws an error if the string cannot be parsed as an int.
     * The radix is assumed to be 10.
     * @param s		the string containing the integer
     * @return		the integer value of the string
     * @exception	NumberFormatException The string does not contain a parsable integer
     */
    public static int parseInt(String s) {
	return parseInt(s,10);
    }

    /**
     * Assuming the string represents an integer, returns a new Integer
     * object with that value. Throws an error if the string cannot be
     * parsed as an int.
     * @param s		the string containing the integer
     * @param radix 	the radix to be used
     * @return 		a integer object representing the integer value of the string
     * @exception	NumberFormatException The string does not contain a parsable integer
     */
    public static Integer valueOf(String s, int radix) {
	return new Integer(parseInt(s,radix));
    }

    /**
     * Assuming the string represents an integer, returns a new Integer
     * object with that value. Throws an error if the string cannot be
     * parsed as an int. The radix is assumed to be 10.
     * @param s		the string containing the integer
     * @param radix 	the radix to be used
     * @return 		a integer object representing the integer value of the string
     * @exception	NumberFormatException the string does not contain a parsable integer
     */
    public static native Integer valueOf(String s);

    /**
     * The value of the integer.
     */
    private int value;

    /**
     * Constructs an Integer object with the specified integer value.
     * @param value	the value of the integer
     */
    public Integer(int value) {
	this.value = value;
    }

    /**
     * Constructs an Integer object with the specified string value.
     * The radix is assumed to be 10.
     * @param s		the string to be converted
     * @exception	NumberFormatException the string does not contain a parsable integer
     */
    public Integer(String s) {
	this.value = parseInt(s, 10);
    }

    /**
     * Returns the int value of the Integer.
     */
    public int intValue() {
	return value;
    }

    /**
     * Returns the long value of the Integer.
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the float value of the Integer.
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the double value of the Integer.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a String object representing this integer's value.
     * @return a String representing the value of the integer.
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns a hashcode for this integer.
     * @return the hashcode
     */
    public int hashCode() {
	return value;
    }

    /**
     * Compares this object against some other object.
     * @param obj		the object to compare with
     * @return 		true if the object is the same
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Integer)) {
	    return value == ((Integer)obj).intValue();
	}
	return false;
    }

}

