/*
 * @(#)Dictionary.java	1.1 95/08/07
 * 
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies. Please
 * refer to the file "copyright.html" for further important copyright and
 * licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 */

package java.util;

/**
 * The Dictionary class is the abstract parent of Hashtable, which maps
 * keys to values. Any object can be used as a key and/or value.  
 *
 * @see java.util.Hashtable
 * @see java.lang.Object#hashCode
 * @see java.lang.Object#equals
 * @version 	1.1, 07 Aug 1995
 */
public abstract
class Dictionary {
    /**
     * Returns the number of elements contained within the Dictionary. 
     */
    abstract public int size();

    /**
     * Returns true if the Dictionary contains no elements.
     */
    abstract public boolean isEmpty();

    /**
     * Returns an enumeration of the Dictionary's keys.
     * @see Dictionary#elements
     * @see Enumeration
     */
    abstract public Enumeration keys();

    /**
     * Returns an enumeration of the elements. Use the Enumeration methods 
     * on the returned object to fetch the elements sequentially.
     * @see Dictionary#keys
     * @see Enumeration
     */
    abstract public Enumeration elements();

    /**
     * Gets the object associated with the specified key in the Dictionary.
     * @param key the key in the hash table
     * @returns the element for the key, or null if the key
     * 		is not defined in the hash table.
     * @see Dictionary#put
     */
    abstract public Object get(Object key);

    /**
     * Puts the specified element into the Dictionary, using the specified
     * key.  The element may be retrieved by doing a get() with the same 
     * key.  The key and the element cannot be null.
     * @param key the specified hashtable key
     * @param value the specified element 
     * @return the old value of the key, or null if it did not have one.
     * @exception NullPointerException If the value of the specified
     * element is null.
     * @see Dictionary#get
     */
    abstract public Object put(Object key, Object value);

    /**
     * Removes the element corresponding to the key. Does nothing if the
     * key is not present.
     * @param key the key that needs to be removed
     * @return the value of key, or null if the key was not found.
     */
    abstract public Object remove(Object key);
}

