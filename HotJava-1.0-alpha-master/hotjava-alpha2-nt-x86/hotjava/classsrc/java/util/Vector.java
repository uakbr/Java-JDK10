/*
 * @(#)Vector.java	1.19 95/02/11  
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
 * Vector class (a growable array).<p>
 * 
 * Each vector tries to optimize storage management by maintaining
 * a capacity and a capacityIncrement. The capacity is always at
 * least as large as the vector size; it is usually larger because
 * as elements are added to the vector, the vector's
 * storage increases in chunks the size of capacityIncrement. Setting
 * the capacity to what you want before inserting a large number of
 * objects will reduce the amount of incremental reallocation.
 * You can safely ignore the capacity and the vector will still work
 * correctly.
 *
 * @version 	1.19, 11 Feb 1995
 * @author	Jonathan Payne
 * @author	Lee Boynton
 */
public
class Vector {
    /**
     * The buffer.
     */
    protected Object elementData[];

    /**
     * The number of elements.
     */
    protected int elementCount;

    /**
     * The size of the increment. If it is 0 the size of the
     * the buffer is doubled everytime it needs to grow.
     */
    protected int capacityIncrement;

    /**
     * Constructs a vector with zero elements and the specified storage capacity
     * and the specified capacityIncrement.
     */
    public Vector(int initialCapacity, int capacityIncrement) {
	super();
	this.elementData = new Object[initialCapacity];
	this.capacityIncrement = capacityIncrement;
    }

    /**
     * Constructs an empty vector with the specified storage capacity.
     */
    public Vector(int initialCapacity) {
	this(initialCapacity, 0);
    }

    /**
     * Construct an empty vector.
     */
    public Vector() {
	this(10);
    }


    /**
     * Copies the elements of this vector into the specified array.
     */
    public final synchronized void copyInto(Object anArray[]) {
	int i = elementCount;
	while (i-- > 0) {
	    anArray[i] = elementData[i];
	}
    }

    /**
     * Trims the vector's capacity down to size. Use this operation to
     * minimize the storage of a vector. Subsequent insertions will
     * cause reallocation.
     */
    public final synchronized void trimToSize() {
	int oldCapacity = elementData.length;
	if (elementCount < oldCapacity) {
	    Object oldData[] = elementData;
	    elementData = new Object[elementCount];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }

    /**
     * Ensures that the vector has at least the specified capacity.
     * @param minCapacity the desired minimum capacity
     */
    public final synchronized void ensureCapacity(int minCapacity) {
	int oldCapacity = elementData.length;
	if (minCapacity > oldCapacity) {
	    Object oldData[] = elementData;
	    int newCapacity = (capacityIncrement > 0) ?
		(oldCapacity + capacityIncrement) : (oldCapacity * 2);
    	    if (newCapacity < minCapacity) {
		newCapacity = minCapacity;
	    }
	    elementData = new Object[newCapacity];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }

    /**
     * Sets the size of the vector. If the size shrinks, the extra elements
     * (at the end of the vector) are lost; if the size increases, the
     * new elements are set to null.
     */
    public final synchronized void setSize(int newSize) {
	if (newSize > elementCount) {
	    ensureCapacity(newSize);
	} else {
	    for (int i = newSize ; i < elementCount ; i++) {
		elementData[i] = null;
	    }
	}
	elementCount = newSize;
    }

    /**
     * Returns the current capacity of the vector.
     */
    public final int capacity() {
	return elementData.length;
    }

    /**
     * Returns the number of elements in the vector.
     * Note that this is not the same as the vector's capacity.
     */
    public final int size() {
	return elementCount;
    }

    /**
     * Returns true if the collection contains no values.
     */
    public final boolean isEmpty() {
	return elementCount == 0;
    }

    /**
     * Returns an enumeration of the elements. Use the Enumeration methods on
     * the returned object to fetch the elements sequentially.
     */
    public final synchronized Enumeration elements() {
	return new VectorEnumerator(this);
    }
    
    /**
     * Returns true if the specified object a a value of the collection.
     */
    public final boolean contains(Object elem) {
	return indexOf(elem, 0) >= 0;
    }

    /**
     * Searches for the specified object, starting from the first position
     * and returns an index to it.
     * @param elem the desired element
     * @return the index of the element, or -1 if it was not found
     */
    public final int indexOf(Object elem) {
	return indexOf(elem, 0);
    }

    /**
     * Searches for the specified object, starting from the specified position
     * and return an index to it.
     * @param elem the desired element
     * @param index the index where to start searching
     * @return the index of the element, or -1 if it was not found
     */
    public final synchronized int indexOf(Object elem, int index) {
	for (int i = index ; i < elementCount ; i++) {
	    if (elem.equals(elementData[i])) {
		return i;
	    }
	}
	return -1;
    }

    /**
     * Searches backwards for the specified object, starting from the last
     * position and returns an index to it. 
     * @param elem the desired element
     * @return the index of the element, or -1 if it was not found
     */
    public final int lastIndexOf(Object elem) {
	return lastIndexOf(elem, elementCount);
    }

    /**
     * Searches backwards for the specified object, starting from the specified
     * position and returns an index to it. 
     * @param elem the desired element
     * @param index the index where to start searching
     * @return the index of the element, or -1 if it was not found
     */
    public final synchronized int lastIndexOf(Object elem, int index) {
	for (int i = index ; --i >= 0 ; ) {
	    if (elem.equals(elementData[i])) {
		return i;
	    }
	}
	return -1;
    }

    /**
     * Returns the element at the specified index.
     * @param index the index of the desired element
     * @return the desired element
     * @exception ArrayIndexOutOfBoundsException Invalid index
     */
    public final synchronized Object elementAt(int index) {
	if (index >= elementCount) {
	    throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
	}
	/* Since try/catch is free, except when the exception is thrown,
	   put in this extra try/catch to catch negative indexes and
	   display a more informative error message.  This might not
	   be appropriate, especially if we have a decent debugging
	   environment - JP. */
	try {
	    return elementData[index];
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new ArrayIndexOutOfBoundsException(index + " < 0");
	}
    }

    /**
     * Returns the first element of the sequence.
     * @exception NoSuchElementException The sequence is empty.
     */
    public final synchronized Object firstElement() {
	if (elementCount == 0) {
	    throw new NoSuchElementException();
	}
	return elementData[0];
    }

    /**
     * Returns the last element of the sequence.
     * @exception NoSuchElementException The sequence is empty.
     */
    public final synchronized Object lastElement() {
	if (elementCount == 0) {
	    throw new NoSuchElementException();
	}
	return elementData[elementCount - 1];
    }

    /**
     * Sets the element at the specified index to be the specified object.
     * The previous element at that position is discarded.
     * @exception ArrayIndexOutOfBoundsException Invalid index
     */
    public final synchronized void setElementAt(Object obj, int index) {
	elementData[index] = obj;
    }

    /**
     * Deletes the element at the specified index. Elements with an index
     * greater than index are moved down.
     * @param index the element to remove
     * @exception ArrayIndexOutOfBoundsException Invalid index
     */
    public final synchronized void removeElementAt(int index) {
	if (elementCount > 0) {
	    int j = elementCount - index - 1;
	    if (j > 0) {
		System.arraycopy(elementData, index + 1, elementData, index, j);
	    }
	    elementCount--;
	    elementData[elementCount] = null; /* to let gc do its work */
	}
    }

    /**
     * Inserts the specified object as an element at the specified index.
     * Elements with an index greater or equal to the index are shifted up.
     * @param obj the element to insert
     * @param index where to insert the new element
     * @exception ArrayIndexOutOfBoundsException invalid index
     */
    public final synchronized void insertElementAt(Object obj, int index) {
	ensureCapacity(elementCount + 1);
	System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
	elementData[index] = obj;
	elementCount++;
    }

    /**
     * Adds the specified object as the last element of the vector.
     * @param obj the element to be added
     */
    public final synchronized void addElement(Object obj) {
	ensureCapacity(elementCount + 1);
	elementData[elementCount++] = obj;
    }

    /**
     * Removes the element from the vector. If the object occurs more
     * than once, only the first is removed. If the object is not an
     * element, returns false.
     * @param obj the element to be removed
     * @return true if the element was actually removed
     */
    public final synchronized boolean removeElement(Object obj) {
	int i = indexOf(obj);
	if (i >= 0) {
	    removeElementAt(i);
	    return true;
	}
	return false;
    }

    /**
     * Removes all elements of the vector. The vector becomes empty.
     */
    public final synchronized void removeAllElements() {
	for (int i = 0; i < elementCount; i++) {
	    elementData[i] = null;
	}
	elementCount = 0;
    }

    /**
     * Clone this vector. The elements are not cloned.
     */
    public synchronized Object clone() {
	Vector v = (Vector)super.clone();
	v.elementData = new Object[elementCount];
	System.arraycopy(elementData, 0, v.elementData, 0, elementCount);
	return v;
    }

    /**
     * Converts the vector to a string. Useful for debugging.
     */
    public final synchronized String toString() {
	int max = size() - 1;
	StringBuffer buf = new StringBuffer();
	Enumeration e = elements();
	buf.append("[");

	for (int i = 0 ; i <= max ; i++) {
	    String s = e.nextElement().toString();
	    buf.append(s);
	    if (i < max) {
		buf.append(", ");
	    }
	}
	buf.append("]");
	return buf.toString();
    }
}

final
class VectorEnumerator implements Enumeration {
    Vector vector;
    int count;

    VectorEnumerator(Vector v) {
	vector = v;
	count = 0;
    }

    public boolean hasMoreElements() {
	return count < vector.elementCount;
    }

    public Object nextElement() {
	synchronize (vector) {
	    if (count < vector.elementCount) {
		return vector.elementData[count++];
	    }
	}
	throw new NoSuchElementException("VectorEnumerator");
    }

}
