/*
 * @(#)ObjectScope.java	1.7 95/01/31  
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

package java.util;

/**
 * Routines to be used by debuggers to examine objects.  This class
 * <em>definitely</em> needs access security. Fields are accessed by their
 * "slot number."  Routines which return arrays of strings are indexed
 *  by slot number.
 *
 * @version 	1.7, 31 Jan 1995
 * @author 	James Gosling
 */
public
class ObjectScope {
    /**
     * Gets the value in slot n of Object o.
     */
    public static native Object getObject(Object o, int n);
    
    /**
     * Gets the names of all the fields in an Object.
     */
    public static native String getFields(Object o)[];

    /**
     * Gets the names of all the methods in an Object.
     */
    public static native String getMethods(Object o)[];

    /**
     * Gets the values of all the fields in an Object.
     */
    public static native String getValues(Object o)[];
}
