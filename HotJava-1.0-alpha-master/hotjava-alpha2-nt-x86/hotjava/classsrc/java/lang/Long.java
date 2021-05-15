/*
 * @(#)Long.java	1.12 95/01/31  
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
 * The Long class is a wrapper for long values.
 * @version 	1.12, 31 Jan 1995
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */
public final
class Long extends Number {
    /**
     * The minimum value a long can have.
     */
    public static final long MIN_VALUE = 0x8000000000000000;

    /**
     * The maximum value a long can have.
     */
    public static final long MAX_VALUE = 0x7fffffffffffffff;

    /**
     * The value of the long.
     */
    private long value;

    /**
     * Constructs a Long object with the specified value.
     * @param value	the value of the long
     */
    public Long(long value) {
	this.value = value;
    }

    /**
     * Returns the value of the Long as an int.
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the value of the Long as a long.
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the value of the Long as a float.
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the value of the Long as a double.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a string object representing this integer's value.
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Computes a hashcode for this long.
     */
    public int hashCode() {
	return (int)value;
    }

    /**
     * Compares this object against some other object.
     * @param obj		the object to compare with
     * @return 		true if the object is the same
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Long)) {
	    return value == ((Long)obj).longValue();
	}
	return false;
    }

    /**
     * Returns the long that the string represents.
     * @param s		the string to be parsed
     * @return 		the value of the string
     * @exception NumberFormatException The string cannot be parsed.
     */
    public static native Long valueOf(String s);
}

