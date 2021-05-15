/*
 * @(#)Float.java	1.30 95/11/13  
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
 * The Float class provides an object wrapper for Float data values, and serves as
 * a place for float-oriented operations.  A wrapper is useful because most of Java's
 * utility classes require the use of objects.  Since floats are not objects in 
 * Java, they need to be "wrapped" in a Float instance.
 * @version 	1.30, 11/13/95
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */
public final
class Float extends Number {
    /**
     * Positive infinity.
     */
    public static final float POSITIVE_INFINITY = 1.0f / 0.0f;

    /**
     * Negative infinity.
     */
    public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;

    /** 
     * Not-a-Number. <em>Note: is not equal to anything, including
     * itself</em>
     */
    public static final float NaN = 0.0f / 0.0f;


    /**
     * The maximum value a float can have.  The largest maximum value possible is  
     * 3.40282346638528860e+38.
     */
    public static final float MAX_VALUE = 3.40282346638528860e+38f;

    /**
     * The minimum value a float can have.  The lowest minimum value possible is 
     * 1.40129846432481707e-45.
     */
    public static final float MIN_VALUE = 1.40129846432481707e-45f;

    /**
     * Returns a String representation for the specified float value.
     * @param f	the float to be converted
     */
    public static native String toString(float f);

    /**
     * Returns the floating point value represented by the specified String.
     * @param s		the String to be parsed
     * @exception	NumberFormatException If the String does not contain a parsable 
     * Float.
     */
    public static native Float valueOf(String s) throws NumberFormatException;

    /**
     * Returns true if the specified number is the special Not-a-Number (NaN) value.
     * @param v	the value to be tested
     */
    static public boolean isNaN(float v) {
	return (v != v);
    }

    /**
     * Returns true if the specified number is infinitely large in magnitude.
     * @param v	the value to be tested
     */
    static public boolean isInfinite(float v) {
	return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    /**
     * The value of the Float.
     */
    private float value;

    /**
     * Constructs a Float wrapper for the specified float value.
     * @param value the value of the Float
     */
    public Float(float value) {
	this.value = value;
    }

    /**
     * Constructs a Float wrapper for the specified double value.
     * @param value the value of the Float
     */
    public Float(double value) {
	this.value = (float)value;
    }

    /**
     * Constructs a Float object initialized to the value specified by the
     * String parameter. 
     * @param s		the String to be converted to a Float
     * @exception	NumberFormatException If the String does not contain a parsable number.
     */
    public Float(String s) throws NumberFormatException {
	// REMIND: this is inefficient
	this(valueOf(s).floatValue());
    }

    /**
     * Returns true if this Float value is Not-a-Number (NaN).
     */
    public boolean isNaN() {
	return isNaN(value);
    }

    /**
     * Returns true if this Float value is infinitely large in magnitude.
     */
    public boolean isInfinite() {
	return isInfinite(value);
    }

    /**
     * Returns a String representation of this Float object.
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns the integer value of this Float (by casting to an int).
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the long value of this Float (by casting to a long).
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the float value of this Float object.
     */
    public float floatValue() {
	return value;
    }

    /**
     * Returns the double value of this Float.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a hashcode for this Float.
     */
    public int hashCode() {
	return (int)value;
    }

    /**
     * Compares this object against some other object.
     * <p>
     * <em>Note: To be useful in hashtables this method
     * considers two Nan floating point values to be equal. This
     * is not according to IEEE specification</em>
     *
     * @param obj		the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	return (obj != null)
	       && (obj instanceof Float) 
	       && (floatToIntBits(((Float)obj).value) == floatToIntBits(value));
    }

    /**
     * Returns the bit represention of a single-float value
     */
    public static native int floatToIntBits(float value);

    /**
     * Returns the single-float corresponding to a given bit represention.
     */
    public static native float intBitsToFloat(int bits);

}
