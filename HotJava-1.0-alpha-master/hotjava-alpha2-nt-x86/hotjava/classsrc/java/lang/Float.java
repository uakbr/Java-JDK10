/*
 * @(#)Float.java	1.13 95/01/31  
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
 * The Float class is a wrapper for float values.
 * @version 	1.13, 31 Jan 1995
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */
public final
class Float extends Number {
    /**
     * Limits and special values.
     */
    private static final float POSITIVE_INFINITY = 1.0 / 0.0;
    private static final float NEGATIVE_INFINITY = -1.0 / 0.0;

    /**
     * The minimum value a float can have.
     */
    public static final float MAX_VALUE = 3.40282346638528860e+38;

    /**
     * The maximum value a float can have.
     */
    public static final float MIN_VALUE = 1.40129846432481707e-45;

    /** pi = 3.1415... */
    public static final float PI = 3.14159265358979323846;

    /** e = 2.718... */
    public static final float E = 2.7182818284590452354;

    /**
     * Returns a string representation for the specified float value.
     * @param f	the float to be converted
     * @return 	a string representing the value
     */
    public static native String toString(float f);

    /**
     * Returns a floating point value that the string represents.
     * @param s		the string that is to be parsed
     * @return 		a float object representing the float value of the string
     * @exception	NumberFormatException The string does not contain a parsable float
     */
    public static native Float valueOf(String s);

    /**
     * Returns true if the argument is the special Not-a-Number (NaN) value.
     * @param v	the value to be tested
     * @return	true if the value is NaN
     */
    static public boolean isNan(float v) {
	return (v != v);
    }

    /**
     * Returns true if the number is infinitely large in magnitude.
     * @param v	the value to be tested
     * @return	true if the value is infinitely large in magnitude
     */
    static public boolean isInfinite(float v) {
	return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    /**
     * The value of the float.
     */
    private float value;

    /**
     * Constructs a wrapper for the given float value.
     * @param value the value of the Float
     */
    public Float(float value) {
	this.value = value;
    }

    /**
     * Returns a string representation of this Float object.
     * @return a string representation this float
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns the integer value of this float (by casting to an int).
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the long value of this float (by casting to a long).
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the float value
     */
    public float floatValue() {
	return value;
    }

    /**
     * Returns the double value
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a hashcode for this float.
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
	if ((obj != null) && (obj instanceof Float)) {
	    return value == ((Float)obj).floatValue();
	}
	return false;
    }

}
