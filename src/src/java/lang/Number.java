/*
 * @(#)Number.java	1.15 95/07/27  
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
 * Number is an abstract superclass for numeric scalar types.
 * Integer, Long, Float and Double are subclasses of Number that bind
 * to a particular numeric representation.<p>
 *
 * @see	Integer
 * @see	Long
 * @see	Float
 * @see	Double
 * @version 	1.15, 07/27/95
 * @author	Lee Boynton
 * @author	Arthur van Hoff
 */
public abstract class Number {
    /**
     * Returns the value of the number as an int.
     * This may involve rounding if the number is not already an integer.
     */
    public abstract int intValue();

    /**
     * Returns the value of the number as a long.  This may involve rounding
     * if the number is not already a long.
     */
    public abstract long longValue();

    /**
     * Returns the value of the number as a float.  This may involve rounding
     * if the number is not already a float.
     */
    public abstract float floatValue();

    /**
     * Returns the value of the number as a double.  This may involve rounding
     * if the number is not already a double.
     */
    public abstract double doubleValue();
}
