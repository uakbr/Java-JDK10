/*
 * @(#)Math.java	1.9 95/02/09  
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
 * Math library.
 * @version 	1.9, 09 Feb 1995
 */
public final
class Math {
    /**
     * Don't let anyone instanciate this class
     */
    private Math() {}

    public static native double sin(double a);
    public static native double cos(double a);
    public static native double tan(double a);
    public static native double asin(double a);
    public static native double acos(double a);
    public static native double atan(double a);
    public static native double exp(double a);
    public static native double log(double a);
    public static native double sqrt(double a);
    public static native double ceil(double a);
    public static native double floor(double a);
    public static native double rint(double a);
    public static native double atan2(double a, double b);
    public static native double annuity(double a, double b);
    public static native double pow(double a, double b);

    public static int round(float a) {
	return (int)floor(a + 0.5);
    }
    public static int round(double a) {
	return (int)floor(a + (double)0.5);
    }

    /**
     * Set the seed of the random generator.
     */
    public static native void srandom(int seed);

    /**
     * Generate a random number between 0.0 and 1.0.
     * @return a number between 0.0 and 1.0.
     */
    public static native double random();

    public static int abs(int a) {
	return (a < 0) ? -a : a;
    }
    public static long abs(long a) {
	return (a < 0) ? -a : a;
    }
    public static float abs(float a) {
	return (a < 0) ? -a : a;
    }
    public static double abs(double a) {
	return (a < 0) ? -a : a;
    }

    public static int max(int a, int b) {
	return (a >= b) ? a : b;
    }
    public static long max(long a, long b) {
	return (a >= b) ? a : b;
    }
    public static float max(float a, float b) {
	return (a >= b) ? a : b;
    }
    public static double max(double a, double b) {
	return (a >= b) ? a : b;
    }

    public static int min(int a, int b) {
	return (a <= b) ? a : b;
    }
    public static long min(long a, long b) {
	return (a <= b) ? a : b;
    }
    public static float min(float a, float b) {
	return (a <= b) ? a : b;
    }
    public static double min(double a, double b) {
	return (a <= b) ? a : b;
    }

}







