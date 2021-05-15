/*
 * @(#)Double.java	1.31 95/11/29  
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
 * The Double class provides an object wrapper for Double data values and serves 
 * as a place for double-oriented operations.  A wrapper is useful because most of
 * Java's utility classes require the use of objects.  Since doubles are not 
 * objects in Java, they need to be "wrapped" in a Double instance.
 * @version 	1.31, 11/29/95
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */

public final
class Double extends Number {
    /**
     * Positive infinity.
     */
    public static final double POSITIVE_INFINITY = 1.0 / 0.0;

    /**
     * Negative infinity.
     */
    public static final double NEGATIVE_INFINITY = -1.0 / 0.0;

    /** 
     * Not-a-Number. <em>Note: is not equal to anything, including
     * itself</em>
     */
    public static final double NaN = 0.0d / 0.0;

    /**
     * The maximum value a double can have.  The greatest maximum value that a 
     * double can have is 1.79769313486231570e+308d.
     */
    public static final double MAX_VALUE = 1.79769313486231570e+308;

    /**
     * The minimum value a double can have.  The lowest minimum value that a
     * double can have is 4.94065645841246544e-324d.
     */
    public static final double MIN_VALUE = 4.94065645841246544e-324;


    /**
     * Returns a String representation for the specified double value.
     * @param d	the double to be converted
     */
    public static native String toString(double d);

    /**
     * Returns a new Double value initialized to the value represented by the 
     * specified String.
     * @param s		the String to be parsed
     * @exception NumberFormatException If the String cannot be parsed.
     */
    public static native Double valueOf(String s) throws NumberFormatException;


    /**
     * Returns true if the specified number is the special Not-a-Number (NaN) value.
     * @param v	the value to be tested
     */
    static public boolean isNaN(double v) {
	return (v != v);
    }

    /**
     * Returns true if the specified number is infinitely large in magnitude.
     * @param v	the value to be tested
     */
    static public boolean isInfinite(double v) {
	return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    /**
     * The value of the Double.
     */
    private double value;

    /**
     * Constructs a Double wrapper for the specified double value.
     * @param value the initial value of the double
     */
    public Double(double value) {
	this.value = value;
    }

    /**
     * Constructs a Double object initialized to the value specified by the
     * String parameter. 
     * @param s		the String to be converted to a Double
     * @exception	NumberFormatException If the String does not contain a parsable number.
     */
    public Double(String s) throws NumberFormatException {
	// REMIND: this is inefficient
	this(valueOf(s).doubleValue());
    }

    /**
     * Returns true if this Double value is the special Not-a-Number (NaN) value.
     */
    public boolean isNaN() {
	return isNaN(value);
    }

    /**
     * Returns true if this Double value is infinitely large in magnitude.
     */
    public boolean isInfinite() {
	return isInfinite(value);
    }

    /**
     * Returns a String representation of this Double object.
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns the integer value of this Double (by casting to an int).
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the long value of this Double (by casting to a long).
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the float value of this Double.
     */
    public float floatValue() {
	return (float)value;
    }

    /**
     * Returns the double value of this Double.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a hashcode for this Double.
     */
    public int hashCode() {
	return (int)value;
    }

    /**
     * Compares this object against the specified object.
     * <p>
     * <em>Note: To be useful in hashtables this method
     * considers two NaN double values to be equal. This
     * is not according to IEEE specification</em>
     *
     * @param obj		the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	return (obj != null)
	       && (obj instanceof Double) 
	       && (doubleToLongBits(((Double)obj).value) == 
		      doubleToLongBits(value));
    }

    /**
     * Returns the bit represention of a double-float value
     */
    public static native long doubleToLongBits(double value);

    /**
     * Returns the double-float corresponding to a given bit represention.
     */
    public static native double longBitsToDouble(long bits);
}
