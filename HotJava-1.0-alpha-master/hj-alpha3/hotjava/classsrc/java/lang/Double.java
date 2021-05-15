/*
 * @(#)Double.java	1.17 95/05/18  
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
 * @version 	1.17, 18 May 1995
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */

public final
class Double extends Number {
    /**
     * Positive inifinity.
     */
    public static final double POSITIVE_INFINITY = 1.0d / 0.0;

    /**
     * Negative inifinity.
     */
    public static final double NEGATIVE_INFINITY = -1.0d / 0.0;

    /* REMIND: comment this in once the nightly can compile NANs
     * Not-a-Number. <em>Note: is not equal to anything, including
     * itself</em>
    public static final double NAN = 0.0d / 0.0;
     */

    /**
     * The maximum value a double can have.
     */
    public static final double MAX_VALUE = 1.79769313486231570e+308;

    /**
     * The minimum value a double can have.
     */
    public static final double MIN_VALUE = 4.94065645841246544e-324;

    /**
     * Returns true if the argument is the special Not-a-Number (NaN) value.
     * @param v	the value to be tested
     * @return	true if the value is NaN
     */
    static public boolean isNan(double v) {
	return (v != v);
    }

    /**
     * Returns true if the number is infinitely large in magnitude.
     * @param v	the value to be tested
     * @return	true if the value is infinitely large in magnitude
     */
    static public boolean isInfinite(double v) {
	return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

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
     * Returns true if this double value is Not-a-Number (NaN).
     */
    public boolean isNan() {
	return isNan(value);
    }

    /**
     * Returns true if this double value is infinitely large in magnitude.
     */
    public boolean isInfinite() {
	return isInfinite(value);
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
     *
     * <em>Note: To be useful in hashtables this method
     * considers two Nan double values to be equal. This
     * is not according to IEEE specification</em>
     *
     * @param obj		the object to compare with
     * @return 		true if the object is the same
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Double)) {
	    if ((value == ((Double)obj).value) || (isNan(value) && isNan(((Double)obj).value))) {
		return true;
	    }
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
