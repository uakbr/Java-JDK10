/*
 * @(#)Math.java	1.19 95/10/23  
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

import java.util.Random;

/**
 * The standard Math library.  For the methods in this Class, error handling 
 * for out-of-range or immeasurable results are platform dependent.  
 * This class cannot be subclassed or instantiated because all methods and variables
 * are static.
 * @version 	1.19, 10/23/95
 */
public final
class Math {
    /**
     * Don't let anyone instantiate this class.
     */
    private Math() {}

    /**
     * The float representation of the value E.  E is equivalent to
     * 2.7182818284590452354f in Java.
     */
    public static final double E = 2.7182818284590452354;

    /**
     * The float representation of the value Pi.  Pi is equivalent
     * to 3.14159265358979323846f in Java.
     */
    public static final double PI = 3.14159265358979323846;


    /**
     * Returns the trigonometric sine of an angle.
     * @param a an assigned angle that is measured in radians
     */
    public static native double sin(double a);
    
    /**
     * Returns the trigonometric cosine of an angle.
     * @param a an assigned angle that is measured in radians
     */
    public static native double cos(double a);
   
    /**
     * Returns the trigonometric tangent of an angle.
     * @param a an assigned angle that is measured in radians 
     */
    public static native double tan(double a);

    /**
     * Returns the arc sine of a, in the range of -Pi/2 through Pi/2.
     * @param a (-1.0) <= a <= 1.0 
     */
    public static native double asin(double a);

    /**
     * Returns the arc cosine of a, in the range of 0.0 through Pi.
     * @param a (-1.0) <= a <= 1.0
     */
    public static native double acos(double a); 

    /**
     * Returns the arc tangent of a, in the range of -Pi/2 through Pi/2.
     * @param a an assigned value
     * @return the arc tangent of a.
     */
    public static native double atan(double a);

    /**
     * Returns the exponential number e(2.718...) raised to the power of a.
     * @param a an assigned value
     */
    public static native double exp(double a);

    /**
     * Returns the natural logarithm (base e) of a.
     * @param a a is a number greater than  0.0 
     * @exception ArithmeticException If a is less than 0.0 .
     */
    public static native double log(double a) throws ArithmeticException;

    /**
     * Returns the square root of a.
     * @param a a is a number greater than or equal to 0.0 
     * @exception ArithmeticException If a is a value less than 0.0 .
     */
    public static native double sqrt(double a) throws ArithmeticException;

    /**
     * Returns the remainder of f1 divided by f2 as defined by IEEE 754.
     * @param f1 the dividend
     * @param f2 the divisor
     */
    public static native double IEEEremainder(double f1, double f2);

    /**
     * Returns the "ceiling" or smallest whole number greater than or equal to a.
     * @param a an assigned value
     */
    public static native double ceil(double a);

    /**
     * Returns the "floor" or largest whole number less than or equal to a.
     * @param a an assigned value
     */
    public static native double floor(double a);

    /**
     * Converts a double value into an integral value in double format.
     * @param a an assigned double value
     */
    public static native double rint(double a);

    /**
     * Converts rectangular coordinates (a, b) to polar (r, theta).  This method
     * computes the phase theta by computing an arc tangent of b/a in
     * the range of -Pi to Pi.
     * @param a an assigned value
     * @param b an assigned value
     * @return the polar coordinates (r, theta).
     */
    public static native double atan2(double a, double b);


    /**
     * Returns the number a raised to the power of b.  If (a == 0.0), then b 
     * must be greater than 0.0; otherwise you will throw an exception. 
     * An exception will also occur if (a <= 0.0) and b is not equal to a  
     * whole number.
     * @param a an assigned value with the exceptions: (a == 0.0) -> (b > 0.0)
     * && (a <= 0.0) -> (b == a whole number)
     * @param b an assigned value with the exceptions: (a == 0.0) -> (b > 0.0)
     * && (a <= 0.0) -> (b == a whole number)
     * @exception ArithmeticException If (a == 0.0) and (b <= 0.0) .
     * @exception ArithmeticException If (a <= 0.0) and b is not equal to 
     * a whole number.
     */
    public static native double pow(double a, double b) throws ArithmeticException;

    /**
     * Rounds off a float value by first adding 0.5 to it and then returning the
     * largest integer that is less than or equal to this new value. 
     * @param a the value to be rounded off
     */
    public static int round(float a) {
	return (int)floor(a + 0.5f);
    }

    /**
     * Rounds off a double value by first adding 0.5 to it and then returning the
     * largest integer that is less than or equal to this new value. 
     * @param a the value to be rounded off
     */
    public static long round(double a) {
	return (long)floor(a + 0.5d);
    }


    private static Random randomNumberGenerator;

    /**
     * Generates a random number between 0.0 and 1.0. <p>
     *
     * Random number generators are often referred to as pseudorandom number 
     * generators because the numbers produced tend to repeat themselves after
     * a period of time.  
     * @return a pseudorandom double between 0.0 and 1.0.
     */
    public static synchronized double random() {
        if (randomNumberGenerator == null)
            randomNumberGenerator = new Random();
        return randomNumberGenerator.nextDouble();
    }

    /**
     * Returns the absolute integer value of a.
     * @param a an assigned integer value
     */
    public static int abs(int a) {
	return (a < 0) ? -a : a;
    }

    /**
     * Returns the absolute long value of a.
     * @param a an assigned long value.
     */
    public static long abs(long a) {
	return (a < 0) ? -a : a;
    }

    /**
     * Returns the absolute float value of a.
     * @param a an assigned float value
     */
    public static float abs(float a) {
	return (a < 0) ? -a : a;
    }
  
    /**
     * Returns the absolute double value of a.
     * @param a an assigned double value
     */
    public static double abs(double a) {
	return (a < 0) ? -a : a;
    }

    /**
     * Takes two int values, a and b, and returns the greater number of the two. 
     * @param a an integer value to be compared
     * @param b an integer value to be compared
     */
    public static int max(int a, int b) {
	return (a >= b) ? a : b;
    }

    /**
     * Takes two long values, a and b, and returns the greater number of the two. 
     * @param a a long value to be compared
     * @param b a long value to be compared
     */
    public static long max(long a, long b) {
	return (a >= b) ? a : b;
    }

    /**
     * Takes two float values, a and b, and returns the greater number of the two. 
     * @param a a float value to be compared
     * @param b a float value to be compared
     */
    public static float max(float a, float b) {
        if (a != a) return a;	// a is NaN
	return (a >= b) ? a : b;
    }

    /**
     * Takes two double values, a and b, and returns the greater number of the two. 
     * @param a a double value to be compared
     * @param b a double value to be compared
     */
    public static double max(double a, double b) {
        if (a != a) return a;	// a is NaN
	return (a >= b) ? a : b;
    }

    /**
     * Takes two integer values, a and b, and returns the smallest number of the two. 
     * @param a an integer value to be compared
     * @param b an integer value to be compared
     */
    public static int min(int a, int b) {
	return (a <= b) ? a : b;
    }

    /**
     * Takes two long values, a and b, and returns the smallest number of the two. 
     * @param a a long value to be compared
     * @param b a long value to be compared
     */
    public static long min(long a, long b) {
	return (a <= b) ? a : b;
    }

    /**
     * Takes two float values, a and b, and returns the smallest number of the two. 
     * @param a a float value to be compared
     * @param b a float value to be compared
     */
    public static float min(float a, float b) {
        if (a != a) return a;	// a is NaN
	return (a <= b) ? a : b;
    }

    /**
     * Takes two double values, a and b, and returns the smallest number of the two. 
     * @param a a double value to be compared
     * @param b a double value to be compared
     */
    public static double min(double a, double b) {
        if (a != a) return a;	// a is NaN
	return (a <= b) ? a : b;
    }

}
