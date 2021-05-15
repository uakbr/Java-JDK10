/*
 * @(#)Random.java	1.5 95/08/10  
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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

package java.util;

/**
 * A Random class generates a stream of pseudo-random numbers.<p>
 *
 * To create a new random number generator, use one of the following methods:
 * <pre>
 *     new Random()       
 *     new Random(long seed)
 * </pre>
 * The form <CODE>new Random()</CODE> initializes the generator
 * to a value based on the current time.  The form 
 * <CODE>new Random(long seed)</CODE> seeds the random number generator with
 * a specific initial value;  use this if an application requires a repeatable
 * stream of pseudo-random numbers. <p>
 *
 * The random number generator uses a 48-bit seed, which is modified using
 * a linear congruential formula. See Donald Knuth, <CITE>The Art of Computer 
 * Programming, Volume 2</CITE>, Section 3.2.1.
 * The generator's seed can be reset with the following method:
 * <pre>
 *    setSeed(long seed)
 * </pre> <par>
 *
 * To create a pseudo-random number, use one of the following functions:
 * <pre>
 *    nextInt()
 *    nextLong()
 *    nextFloat()
 *    nextDouble()
 *    nextGaussian()
 * </pre>
 *
 * @see Math#random
 * @version 	1.1, 27 Jul 1995
 * @author	Frank Yellin

 */
public
class Random {
    private long seed;
    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask = (1L << 48) - 1;

    /** 
     * Creates a new random number generator.  Its seed will be 
     * initialized to a value based on the current time.
     */
    public Random() { this(System.currentTimeMillis()); }

    /** 
     * Creates a new random number generator using a single 
     * <CODE>long</CODE> seed.
     * @param seed the initial seed
     * @see Random#setSeed
     */
    public Random(long seed) {
        setSeed(seed);
    	haveNextNextGaussian = false;
    }


    /**
     * Sets the seed of the random number generator using a single 
     * <CODE>long</CODE> seed.
     * @param seed the initial seed
     */
    synchronized public void setSeed(long seed) {
        this.seed = (seed ^ multiplier) & mask;
    }


    /**
     * Generates the next pseudorandom number.
     * @param bits random bits
     */
    synchronized private int next(int bits) {
        long nextseed = (seed * multiplier + addend) & mask;
        seed = nextseed;
        return (int)(nextseed >>> (48 - bits));
    }

    /**
     * Generates a pseudorandom uniformally distributed 
     * <CODE>int</CODE> value.
     * @return an integer value.
     */
    public int nextInt() {  return next(32); }

    /**
     * Generate a pseudorandom uniformally distributed <CODE>long</CODE> value.
     * @return A long integer value
     */
    public long nextLong() {
        // it's okay that the bottom word remains signed.
        return (next(32) << 32L) + next(32);
    }

    /**
     * Generates a pseudorandom uniformally distributed 
     * <CODE>float</CODE> value between 0.0 and 1.0.
     * @return a <CODE>float</CODE> between 0.0 and 1.0 .
     */
    public float nextFloat() {
        int i = next(30);
        return i / ((float)(1 << 30));
    }

    /**
     * Generates a pseudorandom uniformally distributed 
     * <CODE>double</CODE> value between 0.0 and 1.0.
     * @return a <CODE>float</CODE> between 0.0 and 1.0 .
     */
    public double nextDouble() {
        long l = ((long)(next(27)) << 27) + next(27);
        return l / (double)(1L << 54);
    }

    private double nextNextGaussian;
    private boolean haveNextNextGaussian = false;

    /**
     * Generates a pseudorandom Gaussian distributed 
     * <CODE>double</CODE> value with mean 0.0 and standard 
     * deviation 1.0.
     * @return a Gaussian distributed <CODE>double</CODE>.
     */
    synchronized public double nextGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if (haveNextNextGaussian) {
    	    haveNextNextGaussian = false;
    	    return nextNextGaussian;
    	} else {
            double v1, v2, s;
    	    do { 
                v1 = 2 * nextDouble() - 1; // between -1 and 1
            	v2 = 2 * nextDouble() - 1; // between -1 and 1 
                s = v1 * v1 + v2 * v2;
    	    } while (s >= 1);
    	    double multiplier = Math.sqrt(-2 * Math.log(s)/s);
    	    nextNextGaussian = v2 * multiplier;
    	    haveNextNextGaussian = true;
    	    return v1 * multiplier;
        }
    }
}     




