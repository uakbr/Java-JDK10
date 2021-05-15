/*
 * @(#)String.java	1.54 95/12/07  
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

import java.util.Hashtable;

/**
 * A general class of objects to represent character Strings.
 * Strings are constant, their values cannot be changed after creation.
 * The compiler makes sure that each String constant actually results
 * in a String object. Because String objects are immutable they can
 * be shared. For example:
 * <pre>
 *	String str = "abc";
 * </pre>
 * is equivalent to:
 * <pre>
 *	char data[] = {'a', 'b', 'c'};
 *	String str = new String(data);
 * </pre>
 * Here are some more examples of how strings can be used:
 * <pre>
 * 	System.out.println("abc");
 * 	String cde = "cde";
 * 	System.out.println("abc" + cde);
 *	String c = "abc".substring(2,3);
 *	String d = cde.substring(1, 2);
 * </pre>
 * @see		StringBuffer
 * @version 	1.54, 12/07/95
 * @author 	Lee Boynton
 * @author	Arthur van Hoff
 */
public final
class String {
    /** The value is used for character storage. */
    private char value[];

    /** The offset is the first index of the storage that is used. */
    private int offset;

    /** The count is the number of characters in the String. */
    private int count;

    /**
     * Constructs a new empty String.
     */
    public String() {
	value = new char[0];
    }

    /**
     * Constructs a new String that is a copy of the specified String.
     * @param value the initial value of the String
     */
    public String(String value) {
	count = value.length();
	this.value = new char[count];
	value.getChars(0, count, this.value, 0);
    }

    /**
     * Constructs a new String whose initial value is the specified array 
     * of characters.
     * @param value the initial value of the String
     */
    public String(char value[]) {
	this.count = value.length;
	this.value = new char[count];
	System.arraycopy(value, 0, this.value, 0, count);
    }

    /**
     * Constructs a new String whose initial value is the specified sub array of characters.
     * The length of the new string will be count characters
     * starting at offset within the specified character array.
     * @param value	the initial value of the String, an array of characters
     * @param offset	the offset into the value of the String
     * @param count 	the length of the value of the String
     * @exception StringIndexOutOfBoundsException If the offset and count arguments are invalid.
     */
    public String(char value[], int offset, int count) {
	if (offset < 0) {
	    throw new StringIndexOutOfBoundsException(offset);
	}
	if (count < 0) {
	    throw new StringIndexOutOfBoundsException(count);
	}
	if (offset + count > value.length) {
	    throw new StringIndexOutOfBoundsException(offset + count);
	}

	this.value = new char[count];
	this.count = count;
	System.arraycopy(value, offset, this.value, 0, count);
    }

    /**
     * Constructs a new String whose initial value is the specified sub array of bytes.
     * The high-byte of each character can be specified, it should usually be 0.
     * The length of the new String will be count characters
     * starting at offset within the specified character array.  
     * @param ascii	the bytes that will be converted to characters
     * @param hibyte	the high byte of each Unicode character
     * @param offset	the offset into the ascii array
     * @param count 	the length of the String
     * @exception StringIndexOutOfBoundsException If the offset and count arguments are invalid.
     */
    public String(byte ascii[], int hibyte, int offset, int count) {
	if (offset < 0) {
	    throw new StringIndexOutOfBoundsException(offset);
	}
	if (count < 0) {
	    throw new StringIndexOutOfBoundsException(count);
	}
	if (offset + count > ascii.length) {
	    throw new StringIndexOutOfBoundsException(offset + count);
	}

	char value[] = new char[count];
	this.count = count;
	this.value = value;

	if (hibyte == 0) {
	    for (int i = count ; i-- > 0 ;) {
		value[i] = (char) (ascii[i + offset] & 0xff);
	    }
	} else {
	    hibyte <<= 8;
	    for (int i = count ; i-- > 0 ;) {
		value[i] = (char) (hibyte | (ascii[i + offset] & 0xff));
	    }
	}
    }

    /**
     * Constructs a new String whose value is the specified array of bytes.
     * The byte array transformed into Unicode chars using hibyte
     * as the upper byte of each character.
     * @param ascii	the byte that will be converted to characters
     * @param hibyte	the top 8 bits of each 16 bit Unicode character
     */
    public String(byte ascii[], int hibyte) {
	this(ascii, hibyte, 0, ascii.length);
    }

     
    /**
     * Construct a new string whose value is the current contents of the
     * given string buffer
     * @param buffer     the stringbuffer to be converted
     */
    public String (StringBuffer buffer) { 
	synchronized(buffer) { 
	    buffer.setShared();
	    this.value = buffer.getValue();
	    this.offset = 0;
	    this.count = buffer.length();
	}
    }
    

    /**
     * Returns the length of the String.
     * The length of the String is equal to the number of 16 bit
     * Unicode characters in the String.
     */
    public int length() {
	return count;
    }

    /**
     * Returns the character at the specified index. An index ranges
     * from <tt>0</tt> to <tt>length() - 1</tt>.
     * @param index	the index of the desired character
     * @exception	StringIndexOutOfBoundsException If the index is not
     *			in the range <tt>0</tt> to <tt>length()-1</tt>.
     */
    public char charAt(int index) {
	if ((index < 0) || (index >= count)) {
	    throw new StringIndexOutOfBoundsException(index);
	}
	return value[index + offset];
    }

    /**
     * Copies characters from this String into the specified character array.
     * The characters of the specified substring (determined by
     * srcBegin and srcEnd) are copied into the character array,
     * starting at the array's dstBegin location.
     * @param srcBegin	index of the first character in the string
     * @param srcEnd	end of the characters that are copied
     * @param dst		the destination array
     * @param dstBegin	the start offset in the destination array
     */
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
	System.arraycopy(value, offset + srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    /**
     * Copies characters from this String into the specified byte array.
     * Copies the characters of the specified substring (determined by
     * srcBegin and srcEnd) into the byte array, starting at the
     * array's dstBegin location.
     * @param srcBegin	index of the first character in the String
     * @param srcEnd	end of the characters that are copied
     * @param dst		the destination array
     * @param dstBegin	the start offset in the destination array
     */
    public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
	int j = dstBegin;
	int n = offset + srcEnd;
	int i = offset + srcBegin;
	while (i < n) {
	    dst[j++] = (byte)value[i++];
	}
    }

    /**
     * Compares this String to the specified object.
     * Returns true if the object is equal to this String; that is,
     * has the same length and the same characters in the same sequence.
     * @param anObject	the object to compare this String against
     * @return 	true if the Strings are equal; false otherwise.
     */
    public boolean equals(Object anObject) {
	if ((anObject != null) && (anObject instanceof String)) {
	    String anotherString = (String)anObject;
	    int n = count;
	    if (n == anotherString.count) {
		char v1[] = value;
		char v2[] = anotherString.value;;
		int i = offset;
		int j = anotherString.offset;
		while (n-- != 0) {
		    if (v1[i++] != v2[j++]) {
			return false;
		    }
		}
		return true;
	    }
	}
	return false;
    }

    /**
     * Compares this String to another object.
     * Returns true if the object is equal to this String; that is,
     * has the same length and the same characters in the same sequence.
     * Upper case characters are folded to lower case before
     * they are compared.
     * @param anotherString	the String to compare this String against
     * @return 	true if the Strings are equal, ignoring case; false otherwise.
     */
    public boolean equalsIgnoreCase(String anotherString) {
	return (anotherString != null) && (anotherString.count == count) &&
		regionMatches(true, 0, anotherString, 0, count);
    }

    /**
     * Compares this String to another specified String.
     * Returns an integer that is less than, equal to, or greater than zero.
     * The integer's value depends on whether this String is less than, equal to, or greater
     * than anotherString.
     * @param anotherString the String to be compared
     */
    public int compareTo(String anotherString) {
	int len1 = count;
	int len2 = anotherString.count;
	int n = Math.min(len1, len2);
	char v1[] = value;
	char v2[] = anotherString.value;
	int i = offset;
	int j = anotherString.offset;

	while (n-- != 0) {
	    char c1 = v1[i++];
	    char c2 = v2[j++];
	    if (c1 != c2) {
		return c1 - c2;
	    }
	}
	return len1 - len2;
    }

    /**
     * Determines whether a region of this String matches the specified region
     * of the specified String.
     * @param toffset	where to start looking in this String
     * @param other     the other String
     * @param ooffset	where to start looking in the other String
     * @param len       the number of characters to compare
     * @return          true if the region matches with the other; false otherwise.
     */
    public boolean regionMatches(int toffset, String other, int ooffset, int len) {
	char ta[] = value;
	int to = offset + toffset;
	int tlim = offset + count;
	char pa[] = other.value;
	int po = other.offset + ooffset;
	int plim = po + other.count;
	if ((ooffset < 0) || (toffset < 0) || (to + len > tlim) || (po + len > plim)) {
	    return false;
	}
	while (--len >= 0) {
	    if (ta[to++] != pa[po++]) {
	        return false;
	    }
	}
	return true;
    }

    /**
     * Determines whether a region of this String matches the specified region
     * of the specified String.  If the boolean ignoreCase is true, upper case characters are 
     * considered equivalent to lower case letters.
     * @param ignoreCase if true, case is ignored
     * @param toffset	where to start looking in this String
     * @param other     the other String
     * @param ooffset	where to start looking in the other String
     * @param len       the number of characters to compare
     * @return          true if the region matches with the other; false otherwise.
     */
    public boolean regionMatches(boolean ignoreCase,
				         int toffset,
			               String other, int ooffset, int len) {
	char ta[] = value;
	int to = offset + toffset;
	int tlim = offset + count;
	char pa[] = other.value;
	char trt[] = Character.upCase;
	int po = other.offset + ooffset;
	int plim = po + other.count;
	if ((ooffset < 0) || (toffset < 0) || (to + len > tlim) || (po + len > plim)) {
	    return false;
	}
	while (--len >= 0) {
	    int c1 = ta[to++];
	    int c2 = pa[po++];
	    if ((c1 != c2)
		    && (!ignoreCase ||
			(c1 > 256) || (c2 > 256) ||
			(trt[c1] != trt[c2]))) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Determines whether this String starts with some prefix.
     * @param prefix	the prefix
     * @param toffset	where to begin looking in the the String
     * @return 		true if the String starts with the specified prefix; false otherwise.
     */
    public boolean startsWith(String prefix, int toffset) {
	char ta[] = value;
	int to = offset + toffset;
	int tlim = offset + count;
	char pa[] = prefix.value;
	int po = prefix.offset;
	int pc = prefix.count;
	int plim = po + pc;
	if ((toffset < 0) || (to + pc > tlim)) {
	    return false;
	}
	while (--pc >= 0) {
	    if (ta[to++] != pa[po++]) {
	        return false;
	    }
	}
	return true;
    }

    /**
     * Determines whether this String starts with some prefix.
     * @param prefix	the prefix
     * @return 		true if the String starts with the specified prefix; false otherwise. 
     */
    public boolean startsWith(String prefix) {
	return startsWith(prefix, 0);
    }

    /**
     * Determines whether the String ends with some suffix.
     * @param suffix	the suffix
     * @return 		true if the String ends with the specified suffix; false otherwise.
     */
    public boolean endsWith(String suffix) {
	return startsWith(suffix, count - suffix.count);
    }

    /**
     * Returns a hashcode for this String. This is a large
     * number composed of the character values in the String.
     */
    public int hashCode() {
	int h = 0;
	int off = offset;
	char val[] = value;
	int len = count;

	if (len < 16) {
	    for (int i = len ; i > 0; i--) {
		h = (h * 37) + val[off++];
	    }
	} else {
	    // only sample some characters
	    int skip = len / 8;
	    for (int i = len ; i > 0; i -= skip, off += skip) {
		h = (h * 39) + val[off];
	    }
	}
	return h;
    }

    /**
     * Returns the index within this String of the first occurrence of the specified 
     * character.  This method returns -1 if the index is not found.
     * @param ch	the character to search for
     */
    public int indexOf(int ch) {
	return indexOf(ch, 0);
    }

    /**
     * Returns the index within this String of the first occurrence of the specified 
     * character, starting the search at fromIndex.  This method 
     * returns -1 if the index is not found.
     * @param ch	the character to search for
     * @param fromIndex	the index to start the search from
     */
    public int indexOf(int ch, int fromIndex) {
	int max = offset + count;
	char v[] = value;

	for (int i = offset + fromIndex ; i < max ; i++) {
	    if (v[i] == ch) {
		return i - offset;
	    }
	}
	return -1;
    }

    /**
     * Returns the index within this String of the last occurrence of the specified character.
     * The String is searched backwards starting at the last character.
     * This method returns -1 if the index is not found.
     * @param ch	the character to search for
     */
    public int lastIndexOf(int ch) {
	return lastIndexOf(ch, count - 1);
    }

    /**
     * Returns the index within this String of the last occurrence of the specified character.
     * The String is searched backwards starting at fromIndex.
     * This method returns -1 if the index is not found.
     * @param ch	the character to search for
     * @param fromIndex	the index to start the search from
     */
    public int lastIndexOf(int ch, int fromIndex) {
	int min = offset;
	char v[] = value;
	
	for (int i = offset + ((fromIndex >= count) ? count - 1 : fromIndex) ; i >= min ; i--) {
	    if (v[i] == ch) {
		return i - offset;
	    }
	}
	return -1;
    }

    /**
     * Returns the index within this String of the first occurrence of the specified substring.
     * This method returns -1 if the index is not found.
     * @param str 	the substring to search for
     */
    public int indexOf(String str) {
	return indexOf(str, 0);
    }

    /**
     * Returns the index within this String of the first occurrence of the specified substring.
     * The search is started at fromIndex.
     * This method returns -1 if the index is not found.
     * @param str 	the substring to search for
     * @param fromIndex	the index to start the search from
     */
    public int indexOf(String str, int fromIndex) {
	char v1[] = value;
	char v2[] = str.value;
	int max = offset + (count - str.count);
      test:
	for (int i = offset + ((fromIndex < 0) ? 0 : fromIndex); i <= max ; i++) {
	    int n = str.count;
	    int j = i;
	    int k = str.offset;
	    while (n-- != 0) {
		if (v1[j++] != v2[k++]) {
		    continue test;
		}
	    }
	    return i - offset;
	}
	return -1;
    }

    /**
     * Returns the index within this String of the last occurrence of the specified substring.
     * The String is searched backwards.
     * This method returns -1 if the index is not found.
     * @param str 	the substring to search for
     */
    public int lastIndexOf(String str) {
	return lastIndexOf(str, count - 1);
    }

    /**
     * Returns the index within this String of the last occurrence of the specified substring.
     * The String is searched backwards starting at fromIndex.
     * This method returns -1 if the index is not found.
     * @param str 	the substring to search for
     * @param fromIndex	the index to start the search from
     */
    public int lastIndexOf(String str, int fromIndex) {
	char v1[] = value;
	char v2[] = str.value;
	int min = offset;
      test:
	for (int i = offset + ((fromIndex >= count) ? count - 1 : fromIndex); i >= min ; i--) {
	    int n = str.count;
	    int j = i;
	    int k = str.offset;
	    while (n-- != 0) {
		if (v1[j++] != v2[k++]) {
		    continue test;
		}
	    }
	    return i - offset;
	}
	return -1;
    }

    /**
     * Returns the substring of this String. The substring is specified
     * by a beginIndex (inclusive) and the end of the string.
     * @param beginIndex the beginning index, inclusive
     */
    public String substring(int beginIndex) {
	return substring(beginIndex, length());
    }

    /**
     * Returns the substring of a String. The substring is specified
     * by a beginIndex (inclusive) and an endIndex (exclusive).
     * @param beginIndex the beginning index, inclusive
     * @param endIndex the ending index, exclusive
     * @exception StringIndexOutOfBoundsException If the beginIndex or the endIndex is out 
     * of range.
     */
    public String substring(int beginIndex, int endIndex) {
	if (beginIndex > endIndex) {
	    int tmp = beginIndex;
	    beginIndex = endIndex;
	    endIndex = tmp;
	}
	if (beginIndex < 0) {
	    throw new StringIndexOutOfBoundsException(beginIndex);
	} 
	if (endIndex > count) {
	    throw new StringIndexOutOfBoundsException(endIndex);
	}
	return ((beginIndex == 0) && (endIndex == count)) ? this :
		   new String(value, offset + beginIndex, endIndex - beginIndex);
    }

    /**
     * Concatenates the specified string to the end of this String.
     * @param str	the String which is concatenated to the end of this String
     */
    public String concat(String str) {
	int otherLen = str.length();
	if (otherLen == 0) {
	    return this;
	}
	char buf[] = new char[count + otherLen];
	getChars(0, count, buf, 0);
	str.getChars(0, otherLen, buf, count);
	return new String(buf);
    }

    /**
     * Converts this String by replacing all occurences of oldChar with newChar.
     * @param oldChar	the old character
     * @param newChar	the new character
     */
    public String replace(char oldChar, char newChar) {
	if (oldChar != newChar) {
	    int len = count;
	    int i = -1;
	    while (++i < len) {
		if (value[offset + i] == oldChar) {
		    break;
		}
	    }
	    if (i < len) {
		char buf[] = new char[len];
		for (int j = 0 ; j < i ; j++) {
		    buf[j] = value[offset+j];
		}
		while (i < len) {
		    char c = value[offset + i];
		    buf[i] = (c == oldChar) ? newChar : c;
		    i++;
		}
		return new String(buf);
	    }
	}
	return this;
    }

    /**
     * Converts all of the characters in this String to lower case.
     * @return the String, converted to lowercase.
     * @see Character#toLowerCase
     * @see String#toUpperCase
     */
    public String toLowerCase() {
	int len = count;
	char trt[] = Character.downCase;
	int i, c;
	for (i = 0 ; i < len ; i++) {
	    c = value[offset+i];
	    if ((c < 256) && (trt[c] != c)) {
		break;
	    }
	}
	if (i >= len) {
	    return this;
	}
	char buf[] = new char[len];
	for (i = 0 ; i < len ; i++) {
	    c = value[offset+i];
	    buf[i] = (c < 256) ? trt[c] : (char)c;
	}
	return new String(buf);
    }

    /**
     * Converts all of the characters in this String to upper case.
     * @return the String, converted to uppercase.
     * @see Character#toUpperCase
     * @see String#toLowerCase
     */
    public String toUpperCase() {
	int len = count;
	char trt[] = Character.upCase;
	int i, c;
	for (i = 0 ; i < len ; i++) {
	    c = value[offset+i];
	    if ((c < 256) && (trt[c] != c)) {
		break;
	    }
	}
	if (i >= len) {
	    return this;
	}
	char buf[] = new char[len];
	for (i = 0 ; i < len ; i++) {
	    c = value[offset+i];
	    buf[i] = (c < 256) ? trt[c] : (char)c;
	}
	return new String(buf);
    }

    /**
     * Trims leading and trailing whitespace from this String.
     * @return the String, with whitespace removed.
     */
    public String trim() {
	int len = count;
	int st = 0;
	while ((st < len) && (value[offset + st] <= ' ')) {
	    st++;
	}
	while ((st < len) && (value[offset + len - 1] <= ' ')) {
	    len--;
	}
	return ((st > 0) || (len < count)) ? substring(st, len) : this;
    }

    /**
     * Converts this String to a String.
     * @return the String itself.
     */
    public String toString() {
	return this;
    }

    /**
     * Converts this String to a character array. This creates a new array.
     * @return 	an array of characters.
     */
    public char[] toCharArray() {
	int i, max = length();
	char result[] = new char[max];
	getChars(0, max, result, 0);
	return result;
    }

    /**
     * Returns a String that represents the String value of the object.
     * The object may choose how to represent itself by implementing
     * the toString() method.
     * @param obj	the object to be converted
     */
    public static String valueOf(Object obj) {
	return (obj == null) ? "null" : obj.toString();
    }

    /**
     * Returns a String that is equivalent to the specified character array.
     * Uses the original array as the body of the String (ie. it does not
     * copy it to a new array).
     * @param data	the character array
     */
    public static String valueOf(char data[]) {
	return new String(data);
    }

    /**
     * Returns a String that is equivalent to the specified character array.
     * @param data	the character array
     * @param offset	the offset into the value of the String
     * @param count 	the length of the value of the String
     */
    public static String valueOf(char data[], int offset, int count) {
	return new String(data, offset, count);
    }

    
    /**
     * Returns a String that is equivalent to the specified character array.
     * It creates a new array and copies the characters into it.
     * @param data	the character array
     * @param offset	the offset into the value of the String
     * @param count 	the length of the value of the String
     */
    public static String copyValueOf(char data[], int offset, int count) {
	char str[] = new char[count];
	System.arraycopy(data, offset, str, 0, count);
	return new String(str);
    }

    /**
     * Returns a String that is equivalent to the specified character array.
     * It creates a new array and copies the characters into it.
     * @param data	the character array
     */
    public static String copyValueOf(char data[]) {
	return copyValueOf(data, 0, data.length);
    }

    /**
     * Returns a String object that represents the state of the specified boolean.
     * @param b	the boolean
     */
    public static String valueOf(boolean b) {
	return b ? "true" : "false";
    }

    /**
     * Returns a String object that contains a single character
     * @param c the character
     * @return 	the resulting String.
     */
    public static String valueOf(char c) {
	char data[] = {c};
	return new String(data);
    }

    /**
     * Returns a String object that represents the value of the specified integer.
     * @param i	the integer
     */
    public static String valueOf(int i) {
        return Integer.toString(i, 10);
    }

    /**
     * Returns a String object that represents the value of the specified long.
     * @param l	the long
     */
    public static String valueOf(long l) {
        return Long.toString(l, 10);
    }

    /**
     * Returns a String object that represents the value of the specified float.
     * @param f	the float
     */
    public static String valueOf(float f) {
	return Float.toString(f);
    }

    /**
     * Returns a String object that represents the value of the specified double.
     * @param d	the double
     */
    public static String valueOf(double d) {
	return Double.toString(d);
    }


    /**
     * The set of internalized Strings.
     */
    private static Hashtable InternSet;

    /**
     * Returns a String that is equal to this String
     * but which is guaranteed to be from the unique String pool.  For example:
     * <pre>s1.intern() == s2.intern() <=> s1.equals(s2).</pre>
     */
    public String intern() {
	if (InternSet == null) {
	    InternSet = new Hashtable();
	}
	String s = (String) InternSet.get(this);
	if (s != null) {
	    return s;
	}
	InternSet.put(this, this);
	return this;
    }

    /**
     * Compute the length of this string's UTF encoded form.
     */
    int utfLength() {
	int limit = offset + count;
	int utflen = 0;
	for (int i = offset; i < limit; i++) {
	    int c = value[i];
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		utflen++;
	    } else if (c > 0x07FF) {
		utflen += 3;
	    } else {
		utflen += 2;
	    }
	}
	return utflen;
    }
}
