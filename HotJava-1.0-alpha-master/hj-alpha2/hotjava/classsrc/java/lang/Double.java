/*
 * @(#)Double.java	1.13 95/01/31  
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
 * The Double class is a wrapper for double values.
 * @version 	1.13, 31 Jan 1995
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */

public final
class Double extends Number {
    /**
     * The minimum value a double can have.
     */
    public static final double MAX_VALUE = 1.79769313486231570e+308;

    /**
     * The maximum value a double can have.
     */
    public static final double MIN_VALUE = 4.94065645841246544e-324;

    /**
     * The value of the float.
     */
    private double value;

    /**
     * Constructs a wrapper for the given double value.
     * @param value the value of the Double
     */
    public Double(double value) {
	this.value = value;
    }

    /**
     * Returns a string representation of this Double object.
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns the integer value of this double (by casting to an int).
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the long value of this double (by casting to a long).
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the float value.
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the double value.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a hashcode for this double.
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
	if ((obj != null) && (obj instanceof Double)) {
	    return value == ((Double)obj).doubleValue();
	}
	return false;
    }

    /**
     * Returns the double that the string represents.
     * @param s		the string to be parsed
     * @return 		the value of the string
     * @exception NumberFormatException The string cannot be parsed.
     */
    public static native Double valueOf(String s);
}
