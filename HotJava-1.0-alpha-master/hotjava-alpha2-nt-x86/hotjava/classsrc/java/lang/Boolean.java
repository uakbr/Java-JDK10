/*
 * @(#)Boolean.java	1.9 95/01/31  
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
 * A wrapper for booleans.
 * @version 	1.9, 31 Jan 1995
 * @author	Arthur van Hoff
 */
public final
class Boolean {
    /** */
    public static final Boolean TRUE = new Boolean(true);
    /** */
    public static final Boolean FALSE = new Boolean(false);
    
    /**
     * The value of the boolean
     */
    private boolean value;

    /**
     * Constructs an Boolean object with the specified boolean value.
     * @param value the value of the Boolean
     */
    public Boolean(boolean value) {
	this.value = value;
    }

    /**
     * Returns the value of the Boolean as a boolean.
     */
    public boolean booleanValue() {
	return value;
    }

    /**
     * Returns a string object representing this boolean's value.
     */
    public String toString() {
	return value ? "true" : "false";
    }

    /**
     * Compares this object against some other object.
     * @param obj		the object to compare with
     * @return 		true if the object is the same
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Boolean)) {
	    return (value == ((Boolean)obj).booleanValue());
	} 
	return false;
    }
}







