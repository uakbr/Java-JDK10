/*
 * @(#)StringBuffer.java	1.26 96/01/11  
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
 * This Class is a growable buffer for characters. It is mainly used
 * to create Strings. The compiler uses it to implement the "+" operator.
 * For example:
 * <pre>
 *	"a" + 4 + "c"
 * </pre>
 * is compiled to:
 * <pre>
 *	new StringBuffer().append("a").append(4).append("c").toString()
 * </pre>
 * 
 * Note that the method toString() does not create a copy of the internal buffer. Instead
 * the buffer is marked as shared. Any further changes to the buffer will
 * cause a copy to be made. <p>
 *
 * @see		String
 * @see		java.io.ByteArrayOutputStream
 * @version 	1.26, 01/11/96
 * @author	Arthur van Hoff
 */
 
public final class StringBuffer {
    /** The value is used for character storage. */
    private char value[];

    /** The count is the number of characters in the buffer. */
    private int count;

    /** A flag indicating whether the buffer is shared */
    private boolean shared;

    /**
     * Constructs an empty String buffer.
     */
    public StringBuffer() {
	this(16);
    }

    /**
     * Constructs an empty String buffer with the specified initial length.
     * @param length	the initial length
     */
    public StringBuffer(int length) {
	value = new char[length];
	shared = false;
    }

    /**
     * Constructs a String buffer with the specified initial value.
     * @param str	the initial value of the buffer
     */
    public StringBuffer(String str) {
	this(str.length() + 16);
	append(str);
    }

    /**
     * Returns the length (character count) of the buffer.
     */
    public int length() {
	return count;
    }

    /**
     * Returns the current capacity of the String buffer. The capacity
     * is the amount of storage available for newly inserted
     * characters; beyond which an allocation will occur.
     */
    public int capacity() {
	return value.length;
    }

    /**
     * Copies the buffer value if it is shared.
     */
    private void copyWhenShared() {
	if (shared) {
	    char newValue[] = new char[value.length];
	    System.arraycopy(value, 0, newValue, 0, count);
	    value = newValue;
	    shared = false;
	}
    }

    /**
     * Ensures that the capacity of the buffer is at least equal to the
     * specified minimum.
     * @param minimumCapacity	the minimum desired capacity
     */
    public synchronized void ensureCapacity(int minimumCapacity) {
	int maxCapacity = value.length;

	if (minimumCapacity > maxCapacity) {
	    int newCapacity = (maxCapacity + 1) * 2;
	    if (minimumCapacity > newCapacity) {
		newCapacity = minimumCapacity;
	    }

	    char newValue[] = new char[newCapacity];
	    System.arraycopy(value, 0, newValue, 0, count);
	    value = newValue;
	    shared = false;
	}
    }

    /**
     * Sets the length of the String. If the length is reduced, characters
     * are lost. If the length is extended, the values of the new characters
     * are set to 0.
     * @param newLength	the new length of the buffer
     * @exception StringIndexOutOfBoundsException  If the length is invalid.
     */
    public synchronized void setLength(int newLength) {
	if (newLength < 0) {
	    throw new StringIndexOutOfBoundsException(newLength);
	}
	ensureCapacity(newLength);

	if (count < newLength) {
	    copyWhenShared();
	    for (; count < newLength; count++) {
		value[count] = '\0';
	    }
	}
	count = newLength;
    }

    /**
     * Returns the character at the specified index. An index ranges
     * from 0..length()-1.
     * @param index	the index of the desired character
     * @exception StringIndexOutOfBoundsException If the index is invalid.
     */
    public synchronized char charAt(int index) {
	if ((index < 0) || (index >= count)) {
	    throw new StringIndexOutOfBoundsException(index);
	}
	return value[index];
    }

    /**
     * Copies the characters of the specified substring (determined by
     * srcBegin and srcEnd) into the character array, starting at the
     * array's dstBegin location. Both srcBegin and srcEnd must be legal
     * indexes into the buffer.
     * @param srcBegin	begin copy at this offset in the String
     * @param srcEnd	stop copying at this offset in the String
     * @param dst		the array to copy the data into
     * @param dstBegin	offset into dst
     * @exception StringIndexOutOfBoundsException If there is an invalid index into the buffer.
     */
    public synchronized void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
	if ((srcBegin < 0) || (srcBegin >= count)) {
	    throw new StringIndexOutOfBoundsException(srcBegin);
	}
	if ((srcEnd < 0) || (srcEnd > count)) {
	    throw new StringIndexOutOfBoundsException(srcEnd);
	}
	if (srcBegin < srcEnd) {
	    System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
	}
    }

    /**
     * Changes the character at the specified index to be ch.
     * @param index	the index of the character
     * @param ch		the new character
     * @exception	StringIndexOutOfBoundsException If the index is invalid.
     */
    public synchronized void setCharAt(int index, char ch) {
	if ((index < 0) || (index >= count)) {
	    throw new StringIndexOutOfBoundsException(index);
	}
	copyWhenShared();
	value[index] = ch;
    }

    /**
     * Appends an object to the end of this buffer.
     * @param obj	the object to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public synchronized StringBuffer append(Object obj) {
	return append(String.valueOf(obj));
    }

    /**
     * Appends a String to the end of this buffer.
     * @param str	the String to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public synchronized StringBuffer append(String str) {
	if (str == null) {
	    str = String.valueOf(str);
	}

	int len = str.length();
	ensureCapacity(count + len);
	copyWhenShared();
	str.getChars(0, len, value, count);
	count += len;
	return this;
    }

    /**
     * Appends an array of characters to the end of this buffer.
     * @param str	the characters to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public synchronized StringBuffer append(char str[]) {
	int len = str.length;
	ensureCapacity(count + len);
	copyWhenShared();
	System.arraycopy(str, 0, value, count, len);
	count += len;
	return this;
    }

    /**
     * Appends a part of an array of characters to the end of this buffer.
     * @param str	the characters to be appended
     * @param offset	where to start
     * @param len	the number of characters to add
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public synchronized StringBuffer append(char str[], int offset, int len) {
	ensureCapacity(count + len);
	copyWhenShared();
	System.arraycopy(str, offset, value, count, len);
	count += len;
	return this;
    }

    /**
     * Appends a boolean to the end of this buffer.
     * @param b	the boolean to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public StringBuffer append(boolean b) {
	return append(String.valueOf(b));
    }

    /**
     * Appends a character to the end of this buffer.
     * @param ch	the character to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public synchronized StringBuffer append(char c) {
	ensureCapacity(count + 1);
	copyWhenShared();
	value[count++] = c;
	return this;
    }

    /**
     * Appends an integer to the end of this buffer.
     * @param i	the integer to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public StringBuffer append(int i) {
	return append(String.valueOf(i));
    }

    /**
     * Appends a long to the end of this buffer.
     * @param l	the long to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public StringBuffer append(long l) {
	return append(String.valueOf(l));
    }

    /**
     * Appends a float to the end of this buffer.
     * @param f	the float to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public StringBuffer append(float f) {
	return append(String.valueOf(f));
    }

    /**
     * Appends a double to the end of this buffer.
     * @param d	the double to be appended
     * @return 	the StringBuffer itself, NOT a new one.
     */
    public StringBuffer append(double d) {
	return append(String.valueOf(d));
    }

    /**
     * Inserts an object into the String buffer.
     * @param offset	the offset at which to insert
     * @param obj		the object to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset is invalid.
     */
    public synchronized StringBuffer insert(int offset, Object obj) {
	return insert(offset, String.valueOf(obj));
    }

    /**
     * Inserts a String into the String buffer.
     * @param offset	the offset at which to insert
     * @param str		the String to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset is invalid.
     */
    public synchronized StringBuffer insert(int offset, String str) {
	if ((offset < 0) || (offset > count)) {
	    throw new StringIndexOutOfBoundsException();
	}
	int len = str.length();
	ensureCapacity(count + len);
	copyWhenShared();
	System.arraycopy(value, offset, value, offset + len, count - offset);
	str.getChars(0, len, value, offset);
	count += len;
	return this;
    }

    /**
     * Inserts an array of characters into the String buffer.
     * @param offset	the offset at which to insert
     * @param str		the characters to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset is invalid.
     */
    public synchronized StringBuffer insert(int offset, char str[]) {
	if ((offset < 0) || (offset > count)) {
	    throw new StringIndexOutOfBoundsException();
	}
	int len = str.length;
	ensureCapacity(count + len);
	copyWhenShared();
	System.arraycopy(value, offset, value, offset + len, count - offset);
	System.arraycopy(str, 0, value, offset, len);
	count += len;
	return this;
    }

    /**
     * Inserts a boolean into the String buffer.
     * @param offset	the offset at which to insert
     * @param b		the boolean to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringBuffer insert(int offset, boolean b) {
	return insert(offset, String.valueOf(b));
    }

    /**
     * Inserts a character into the String buffer.
     * @param offset	the offset at which to insert
     * @param ch		the character to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset invalid.
     */
    public synchronized StringBuffer insert(int offset, char c) {
	ensureCapacity(count + 1);
	copyWhenShared();
	System.arraycopy(value, offset, value, offset + 1, count - offset);
	value[offset] = c;
	count += 1;
	return this;
    }

    /**
     * Inserts an integer into the String buffer.
     * @param offset	the offset at which to insert
     * @param i		the integer to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringBuffer insert(int offset, int i) {
	return insert(offset, String.valueOf(i));
    }

    /**
     * Inserts a long into the String buffer.
     * @param offset	the offset at which to insert
     * @param l		the long to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringBuffer insert(int offset, long l) {
	return insert(offset, String.valueOf(l));
    }

    /**
     * Inserts a float into the String buffer.
     * @param offset	the offset at which to insert
     * @param f		the float to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringBuffer insert(int offset, float f) {
	return insert(offset, String.valueOf(f));
    }

    /**
     * Inserts a double into the String buffer.
     * @param offset	the offset at which to insert
     * @param d		the double to insert
     * @return 		the StringBuffer itself, NOT a new one.
     * @exception	StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringBuffer insert(int offset, double d) {
	return insert(offset, String.valueOf(d));
    }

    /**
     * Converts to a String representing the data in the buffer.
     */
    public String toString() {
	return new String(this);
    }


    //
    // The following two methods are needed by String to efficiently
    // convert a StringBuffer into a String.  They are not public.
    // They shouldn't be called by anyone but String.
    void setShared() { shared = true; } 
    char[] getValue() { return value; }
}
