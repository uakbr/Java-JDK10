/*
 * @(#)Boolean.java	1.18 95/11/13  
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
 * The Boolean class provides an object wrapper for Boolean data values, and 
 * serves as a place for boolean-oriented operations.
 * A wrapper is useful because most of Java's utility classes require the use
 * of objects.  Since booleans are not objects in Java, they need to be
 * "wrapped" in a Boolean instance. 
 * @version 	1.18, 11/13/95
 * @author	Arthur van Hoff
 */
public final
class Boolean {
    /** 
     *  Assigns this Boolean to be true.
     */
    public static final Boolean TRUE = new Boolean(true);
    /** 
     * Assigns this Boolean to be false.
     */
    public static final Boolean FALSE = new Boolean(false);

    /**
     * The minimum value a Charater can have.  The lowest minimum value an
     * Integer can have is '\u0000'.
     */
    public static final char   MIN_VALUE = '\u0000';

    /**
     * The maximum value a Character can have.  The greatest maximum value an
     * Integer can have is '\uffff'.
     */
    public static final char   MAX_VALUE = '\uffff';
    
    /**
     * The value of the Boolean.
     */
    private boolean value;

    /**
     * Constructs a Boolean object initialized to the specified boolean 
     * value.
     * @param value the value of the boolean
     */
    public Boolean(boolean value) {
	this.value = value;
    }

    /**
     * Constructs a Boolean object initialized to the value specified by the
     * String parameter. 
     * @param s		the String to be converted to a Boolean
     */
    public Boolean(String s) {
	this((s != null) && s.toLowerCase().equals("true"));
    }

    /**
     * Returns the value of this Boolean object as a boolean.
     */
    public boolean booleanValue() {
	return value;
    }

    /**
     * Returns the boolean value represented by the specified String.
     * @param s		the String to be parsed
     */
    public static Boolean valueOf(String s) {
	return new Boolean((s != null) && s.toLowerCase().equals("true"));
    }

    /**
     * Returns a new String object representing this Boolean's value.
     */
    public String toString() {
	return value ? "true" : "false";
    }

    /**
     * Returns a hashcode for this Boolean.
     */
    public int hashCode() {
	return value ? 1231 : 1237;
    }

    /**
     * Compares this object against the specified object.
     * @param obj		the object to compare with
     * @return 		true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Boolean)) {
	    return value == ((Boolean)obj).booleanValue();
	} 
	return false;
    }

    /**
     * Gets a Boolean from the properties.
     * @param name the property name.
     */
    public static boolean getBoolean(String name) {
	return "true".equals(System.getProperty(name));
    }
}







