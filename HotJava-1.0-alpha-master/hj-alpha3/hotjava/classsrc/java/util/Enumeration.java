/*
 * @(#)Enumeration.java	1.6 95/01/31  
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
 * The Enumeration interface specifies a set of methods that may be used
 * to enumerate, or count through, a set of values. The enumeration is
 * consumed by use; its values may only be counted once.<p>
 *
 * For example, to print all elements of a Vector v:
 * <pre>
 *	for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
 *	    System.out.println(e.nextElement());
 *	}
 * </pre>
 * @see Vector
 * @see Hashtable
 * @version 	1.6, 31 Jan 1995
 * @author	Lee Boynton
 */
public interface Enumeration {
    /**
     * Returns true if the there are more elements in the
     * enumeration.
     */
    public abstract boolean hasMoreElements();

    /**
     * Returns the next element of the enumeration. Calls to
     * nextElement() will enumerate successive element.
     * @exception NoSuchElementException No more elements
     */
    public abstract Object nextElement();
}


