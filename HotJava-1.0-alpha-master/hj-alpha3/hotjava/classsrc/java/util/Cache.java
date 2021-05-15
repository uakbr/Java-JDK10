/*
 * @(#)Cache.java	1.2 95/04/04
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
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
 * Cache collision list.
 */
class CacheEntry extends Ref {
    int hash;
    Object key;
    CacheEntry next;
    public Object reconstitute() {
	return null;
    }
}

/**
 * Cache class. Maps keys to values. Any object can be used as
 * a key and/or value.  This is very similar to the Hashtable
 * class, except that after putting an object into the Cache,
 * it is not guranteed that a subsequent get will return it.
 * The cache will automatically remove entries if memory is
 * getting tight and the entry isn't referenced from outside
 * the cache.<p>
 *
 * To sucessfully store and retrieve objects from a hash table the
 * object used as the key must implement the hashCode() and equals()
 * methods.<p>
 *
 * This example creates a cache of numbers. It uses the names of
 * the numbers as keys:
 * <pre>
 *	Cache numbers = new Cache();
 *	numbers.put("one", new Integer(1));
 *	numbers.put("two", new Integer(1));
 *	numbers.put("three", new Integer(1));
 * </pre>
 * To retrieve a number use:
 * <pre>
 *	Integer n = (Integer)numbers.get("two");
 *	if (n != null) {
 *	    System.out.println("two = " + n);
 *	}
 * </pre>
 *
 * @see java.lang.Object#hashCode
 * @see java.lang.Object#equals
 * @see java.lang.Ref
 * @version 	1.20, 14 Mar 1995
 * @author	Arthur van Hoff
 */
public
class Cache {
    /**
     * The hash table data.
     */
    private CacheEntry table[];

    /**
     * The total number of entries in the hash table.
     */
    private int count;

    /**
     * Rehashes the table when count exceeds this threshold.
     */
    private int threshold;

    /**
     * The load factor for the hashTable.
     */
    private float loadFactor;

    /**
     * Construct a new, empty cache with the specified initial capacity
     * and the specified load factor.
     * @param initialCapacity the initial number of buckets
     * @param loadFactor a number between 0.0 and 1.0, it defines
     *		the threshold for rehashing the cache into
     *		a bigger one.
     */
    public Cache (int initialCapacity, float loadFactor) {
	if ((initialCapacity <= 0) || (loadFactor <= 0.0)) {
	    throw new IllegalArgumentException();
	}
	this.loadFactor = loadFactor;
	table = new CacheEntry[initialCapacity];
	threshold = (int) (initialCapacity * loadFactor);
    }

    /**
     * Constructs a new, empty cache with the specified initial capacity.
     * @param initialCapacity the initial number of buckets
     */
    public Cache (int initialCapacity) {
	this(initialCapacity, 0.75);
    }

    /**
     * Constructs a new, empty cache. A default capacity and load factor
     * is used. Note that the cache will automatically grow when it gets
     * full.
     */
    public Cache () {
	this(101, 0.75);
    }

    /**
     * Returns the cache's size (the number of elements it contains).
     */
    public int size() {
	return count;
    }

    /**
     * Returns true if the cache contains no elements.
     */
    public boolean isEmpty() {
	return count == 0;
    }

    /**
     * Returns an enumeration of the cache's keys.
     * @see Cache#elements
     * @see Enumeration
     */
    public synchronized Enumeration keys() {
	return new CacheEnumerator(table, true);
    }

    /**
     * Returns an enumeration of the elements. Use the Enumeration methods on
     * the returned object to fetch the elements sequentially.
     * @see Cache#keys
     * @see Enumeration
     */
    public synchronized Enumeration elements() {
	return new CacheEnumerator(table, false);
    }

    /**
     * Gets the object associated with a key in the cache.
     * @returns the element for the key or null if the key
     * 		is not defined in the hash table.
     * @see Cache#put
     */
    public synchronized Object get(Object key) {
	CacheEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;
	for (CacheEntry e = tab[index]; e != null; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		return e.check();
	    }
	}
	return null;
    }

    /**
     * Rehashes the content of the table into a bigger table.
     * This is method called automatically when the cache's
     * size exeeds a threshold.
     */
    protected void rehash() {
	int oldCapacity = table.length;
	CacheEntry oldTable[] = table;

	int newCapacity = oldCapacity * 2 + 1;
	CacheEntry newTable[] = new CacheEntry[newCapacity];

	threshold = (int) (newCapacity * loadFactor);
	table = newTable;

	// System.out.println("rehash old=" + oldCapacity + ", new=" +
	// newCapacity + ", thresh=" + threshold + ", count=" + count);

	for (int i = oldCapacity; i-- > 0;) {
	    for (CacheEntry old = oldTable[i]; old != null;) {
		CacheEntry e = old;
		old = old.next;
		if (e.check() != null) {
		    int index = (e.hash & 0x7FFFFFFF) % newCapacity;
		    e.next = newTable[index];
		    newTable[index] = e;
		} else
		    count--;	/* remove entries that have disappeared */
	    }
	}
    }

    /**
     * Puts the specified element into the cache, using the specified
     * key.  The element may be retrieved by doing a get() with the same key.
     * The key can't be null and the element can't be null.
     * @see Cache#get
     * @return the old value of the key, or null if it didn't have one
     */
    public synchronized Object put(Object key, Object value) {
	// Make sure the value is not null
	if (value == null) {
	    throw new NullPointerException();
	}
	// Makes sure the key is not already in the cache.
	CacheEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;
	CacheEntry ne = null;
	for (CacheEntry e = tab[index]; e != null; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		Object old = e.check();
		e.setThing(value);
		return old;
	    } else if (e.check() == null)
		ne = e;		/* reuse old flushed value */
	}

	if (count >= threshold) {
	    // Rehash the table if the threshold is exceeded
	    rehash();
	    return put(key, value);
	}
	// Creates the new entry.
	if (ne == null) {
	    ne = new CacheEntry ();
	    ne.next = tab[index];
	    tab[index] = ne;
	    count++;
	}
	ne.hash = hash;
	ne.key = key;
	ne.setThing(value);
	return null;
    }

    /**
     * Removes the element corresponding to the key. Does nothing if the
     * key isn't present.
     * @param key the key that needs to be removed
     * @return the value of key, or null if the key was not found
     */
    public synchronized Object remove(Object key) {
	CacheEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;
	for (CacheEntry e = tab[index], prev = null; e != null; prev = e, e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		if (prev != null) {
		    prev.next = e.next;
		} else {
		    tab[index] = e.next;
		}
		count--;
		return e.check();
	    }
	}
	return null;
    }
}

/**
 * A cache enumerator class.This class should remain opague
 * to the client. It will use the Enumeration interface.
 */
class CacheEnumerator implements Enumeration {
    boolean keys;
    int index;
    CacheEntry table[];
    CacheEntry entry;

    CacheEnumerator (CacheEntry table[], boolean keys) {
	this.table = table;
	this.keys = keys;
	this.index = table.length;
    }

    public boolean hasMoreElements() {
	while (index >= 0) {
	    while (entry != null)
		if (entry.check() != null)
		    return true;
		else
		    entry = entry.next;
	    while (--index >= 0 && (entry = table[index]) != null) ;
	}
	return false;
    }

    public Object nextElement() {
	while (index >= 0) {
	    if (entry == null)
		while (--index >= 0 && (entry = table[index]) == null) ;
	    if (entry != null) {
		CacheEntry e = entry;
		entry = e.next;
		if (e.check() != null)
		    return keys ? e.key : e.check();
	    }
	}
	throw new NoSuchElementException("CacheEnumerator");
    }

}
