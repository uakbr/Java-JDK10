/*
 * @(#)String.java	1.36 95/05/09  
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
 * A general class of objects to represent character strings.
 * Strings are constant, their values cannot be changed after creation.
 * The compiler makes sure that each string constant actually results
 * in a String object. Because string objects are immutable they can
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
 *	String c = "abc".substring(2,1);
 *	String d = cde.substring(1, 1);
 * </pre>
 * @see		StringBuffer
 * @version 	1.36, 09 May 1995
 * @author 	Lee Boynton
 * @author	Arthur van Hoff
 */
public final
class String extends Object {
    /** The value is used for character storage. */
    private char value[];

    /** The offset is the first index of the storage that is used. */
    private int offset;

    /** The count is the number of characters in the string. */
    private int count;

    /**
     * Constructs a new empty string.
     */
    public String() {
	value = new char[0];
    }

    /**
     * Constructs a new string that is a copy of the specified string.
     * @param value the initial value of the String
     */
    public String(String value) {
	count = value.length();
	this.value = new char[count];
	value.getChars(0, count, this.value, 0);
    }

    /**
     * Constructs a new string from an array of characters as its initial value.
     * The character array is NOT copied. <em>Do not modify the array after
     * the string is created</em>.
     * @param value the initial value of the String
     */
    public String(char value[]) {
	this.value = value;
	count = value.length;
    }

    /**
     * Constructs a new string from a sub array of characters.
     * The value of the new string will be count characters
     * starting at offset.
     * The character array is NOT copied. <em>Do not modify the array after
     * the string is created</em>.
     * @param value	the initial value of the String, an array of characters
     * @param offset	the offset into the value of the string
     * @param count 	the length of the value of the string
     */
    public String(char value[], int offset, int count) {
	this.value = value;
	this.offset = offset;
	this.count = count;
    }

    /**
     * Constructs a new string from a sub array of bytes.
     * The high-byte of each character can be specified, it should usually be 0.
     * The value of the new string will be count characters
     * starting at offset.
     * @param ascii	the bytes that will be converted to characters
     * @param hibyte	the high byte of each UNICODE character
     * @param offset	the offset into the ascii array
     * @param count 	the length of the string
     */
    public String(byte ascii[], int hibyte, int offset, int count) {
	this.count = count;
	value = new char[count];
	hibyte <<= 8;
	for (int i = 0 ; i < count ; i++) {
	    value[i] = (char) (hibyte | (ascii[i + offset] & 0xff));
	}
    }

    /**
     * Constructs a new string from an array of bytes.
     * The byte array transformed into UNICODE chars using hibyte
     * as the upper byte of each character.
     * @param ascii	the byte that will be converted to characters
     * @param hibyte	the top 8 bits of each 16 bit UNICODE character
     */
    public String(byte ascii[], int hibyte) {
	this(ascii, hibyte, 0, ascii.length);
    }

    /**
     * Returns the length of the string.
     * The length of the string is equal to the number of 16 bit
     * UNICODE characters in the string.
     * @return the length of the string
     */
    public int length() {
	return count;
    }

    /**
     * Returns the character at the specified index. An index ranges
     * from <tt>0</tt> to <tt>length() - 1</tt>.
     * @param index	the index of the desired character
     * @return 		the desired character
     * @exception	StringIndexOutOfRangeException The index is not
     *			in the range <tt>0</tt> to <tt>length()-1</tt>.
     */
    public char charAt(int index) {
	if (index < 0 || index >= count) {
	    throw new StringIndexOutOfRangeException(index);
	}
	return value[index + offset];
    }

    /**
     * Copies characters from the string into an character array.
     * The characters of the specified substring (determined by
     * srcBegin and srcEnd) are copied into the character array,
     * starting at the array's dstIndex location.
     * @param srcBegin	index of the first character in the string
     * @param srcEnd	end of the characters that are copied
     * @param dst		the destination array
     * @param dstBegin	the start offset in the destination array
     */
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
	System.arraycopy(value, offset + srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    /**
     * Copies characters from the string into a byte array.
     * Copies the characters of the specified substring (determined by
     * srcBegin and srcEnd) into the byte array, starting at the
     * array's dstIndex location.
     * @param srcBegin	index of the first character in the string
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
     * Compares a string against another object.
     * Returns true if the object is equal to this string; that is,
     * has the same length and the same characters in the same sequence.
     * @param anObject	the object to compare this string against
     * @return 		true if they are equal
     */
    public boolean equals(Object anObject) {
	if ((anObject != null) && (anObject instanceof String)) {
	    String anotherString = (String)anObject;
	    if (anotherString.count != count) {
		return false;
	    }
	    return startsWith(anotherString, 0);
	}
	return false;
    }

    /**
     * Compares the string against another object.
     * Returns true if the object is equal to this string; that is,
     * has the same length and the same characters in the same sequence.
     * Upper case character are folded to lower case character before
     * they are compared.
     * @param anObject	the object to compare this string against
     * @return 		true if they are equal, ignoring case
     */
    public boolean equalsIgnoreCase(Object anObject) {
	if ((anObject != null) && (anObject instanceof String)) {
	    String anotherString = (String) anObject;
	    return anotherString.count == count &&
		regionMatches(true, 0, anotherString, 0, count);
	}
	return false;
    }

    /**
     * Compares this string against another string.
     * Returns an integer that is less than, equal to, or greater than zero,
     * depending on whether this string is less than, equal to, or greater
     * than anotherString.
     * @return 		a number less than zero if this string is lexically
     *			smaller, 0 if the string is equal or a number greater
     *			than 0 if this string is lexically greater.
     */
    public int compareTo(String anotherString) {
	int len1 = length(), len2 = anotherString.length();
	int i, len = Math.min(len1, len2);
	for (i = 0; i < len; i++) {
	    char c1 = charAt(i);
	    char c2 = anotherString.charAt(i);
	    if (c1 != c2) {
		return c1 - c2;
	    }
	}
	return len1 - len2;
    }

    /**
     * Determines whether a region of one string matches a region
     * of another string.
     * @param toffset	where to start looking in this string
     * @param other     the other string
     * @param ooffset	where to start looking in the other string
     * @param len       the number of characters to match
     * @return          true if the region matches with the other
     */
    public boolean regionMatches(int toffset, String other, int ooffset, int len) {
	char ta[] = value;
	int to = offset + toffset;
	int tlim = offset + count;
	char pa[] = other.value;
	int po = other.offset + ooffset;
	int plim = po + other.count;
	if (ooffset < 0 || toffset < 0 ||
            to + len > tlim || po + len > plim)
	    return false;
	while (--len >= 0)
	    if (ta[to++] != pa[po++])
	        return false;
	return true;
    }

    private static char UTRT[]; /* case folding translation table lower=>upper */
    private static char LTRT[]; /* case folding translation table upper=>lower */
    static {
        char utrt[] = new char[256];
        char ltrt[] = new char[256];
        int i;
        for (i = 0; i<256; i++) {
            utrt[i] = (char) i;
	    ltrt[i] = (char) i;
	}
        for (i = 'a'; i<='z'; i++)
            utrt[i] = (char) (i + ('A' - 'a'));
        /* get the accented iso-latin-1 characters right */
	utrt[0xC1] = 0xE1;	/* A WITH ACUTE */
	utrt[0xC2] = 0xE2;	/* A WITH CIRCUMFLEX */
	utrt[0xC4] = 0xE4;	/* A WITH DIAERESIS */
	utrt[0xC0] = 0xE0;	/* A WITH GRAVE */
	utrt[0xC5] = 0xE5;	/* A WITH RING ABOVE */
	utrt[0xC3] = 0xE3;	/* A WITH TILDE */
	utrt[0xC7] = 0xE7;	/* C WITH CEDILLA */
	utrt[0xC9] = 0xE9;	/* E WITH ACUTE */
	utrt[0xCA] = 0xEA;	/* E WITH CIRCUMFLEX */
	utrt[0xCB] = 0xEB;	/* E WITH DIAERESIS */
	utrt[0xC8] = 0xE8;	/* E WITH GRAVE */
	utrt[0xD0] = 0xF0;	/* ETH */
	utrt[0xCD] = 0xED;	/* I WITH ACUTE */
	utrt[0xCE] = 0xEE;	/* I WITH CIRCUMFLEX */
	utrt[0xCF] = 0xEF;	/* I WITH DIAERESIS */
	utrt[0xCC] = 0xEC;	/* I WITH GRAVE */
	utrt[0xD1] = 0xF1;	/* N WITH TILDE */
	utrt[0xD3] = 0xF3;	/* O WITH ACUTE */
	utrt[0xD4] = 0xF4;	/* O WITH CIRCUMFLEX */
	utrt[0xD6] = 0xF6;	/* O WITH DIAERESIS */
	utrt[0xD2] = 0xF2;	/* O WITH GRAVE */
	utrt[0xD8] = 0xF8;	/* O WITH STROKE */
	utrt[0xD5] = 0xF5;	/* O WITH TILDE */
	utrt[0xDE] = 0xFE;	/* THORN */
	utrt[0xDA] = 0xFA;	/* U WITH ACUTE */
	utrt[0xDB] = 0xFB;	/* U WITH CIRCUMFLEX */
	utrt[0xDC] = 0xFC;	/* U WITH DIAERESIS */
	utrt[0xD9] = 0xF9;	/* U WITH GRAVE */
	utrt[0xDD] = 0xFD;	/* Y WITH ACUTE */
	for (i = 0; i<256; i++) {
	    int c = utrt[i];
	    if (c != i)
		ltrt[c] = (char) i;	/* inverse map */
	}
	UTRT = utrt;
	LTRT = ltrt;
    }

    /**
     * Determines whether a region of one string matches a region
     * of another string.  Upper case characters are considered equivalent
     * to lower case letters.
     * @param ignoreCase if true, case is ignored
     * @param toffset	where to start looking in this string
     * @param other     the other string
     * @param ooffset	where to start looking in the other string
     * @param len       the number of characters to match
     * @return          true if the region matches with the other
     */
    public boolean regionMatches(boolean ignoreCase,
				         int toffset,
			               String other, int ooffset, int len) {
	char ta[] = value;
	int to = offset + toffset;
	int tlim = offset + count;
	char pa[] = other.value;
	char trt[] = UTRT;
	int po = other.offset + ooffset;
	int plim = po + other.count;
	if (ooffset < 0 || toffset < 0 ||
		to + len > tlim || po + len > plim)
	    return false;
	while (--len >= 0) {
	    int c1 = ta[to++];
	    int c2 = pa[po++];
	    if (c1 != c2
		    && (!ignoreCase ||
			c1 > 256 || c2 > 256 ||
			trt[c1] != trt[c2]))
		return false;
	}
	return true;
    }

    /**
     * Determines whether a string starts with some prefix.
     * @param prefix	the prefix
     * @param toffset	where to start looking
     * @return 		true if the string starts with the prefix
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
     * Determines whether a string starts with some prefix.
     * @param prefix	the prefix
     * @return 		true if the string starts with the prefix
     */
    public boolean startsWith(String prefix) {
	return startsWith(prefix, 0);
    }

    /**
     * Determines whether a string ends with some suffix.
     * @param suffix	the suffix
     * @return 		true if the string ends with the suffix
     */
    public boolean endsWith(String suffix) {
	return startsWith(suffix, count - suffix.count);
    }

    /**
     * Returns a hashcode of the string. This is a large
     * number composed of the character values in the string.
     * @return the hash code
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
     * Returns the index of the first occurrence of aCharacter.
     * @param ch	the character
     * @return 	the index if the character is found, -1 otherwise
     */
    public int indexOf(int ch) {
	return indexOf(ch, 0);
    }

    /**
     * Returns the index of the first occurrence of ch, starting
     * the search at fromIndex.
     * @param ch	the character
     * @param fromIndex	the index to start the search from
     * @return 	the index if the character is found, -1 otherwise
     */
    public int indexOf(int ch, int fromIndex) {
	int i, max = length();
	for (i = fromIndex; i < max; i++) {
	    if (charAt(i) == ch) {
		return i;
	    }
	}
	return -1;
    }

    /**
     * Returns the index of the last occurrence of ch in a string.
     * The string is searched backwards starting at the last character.
     * @param ch	the character
     * @return 	the index if the character is found, -1 otherwise
     */
    public int lastIndexOf(int ch) {
	return lastIndexOf(ch, length() - 1);
    }

    /**
     * Returns the index of the last occurrence of ch in a string.
     * The string is searched backwards starting at fromIndex.
     * @param ch	the character
     * @param fromIndex	the index to start the search from
     * @return 	the index if the character is found, -1 otherwise
     */
    public int lastIndexOf(int ch, int fromIndex) {
	for (int i = fromIndex; i >= 0; i--) {
	    if (charAt(i) == ch) {
		return i;
	    }
	}
	return -1;
    }

    /**
     * Returns the index of the first occurrence of a substring.
     * @param str 	the substring
     * @return 		the index if the substring is found, -1 otherwise
     */
    public int indexOf(String str) {
	return indexOf(str, 0);
    }

    /**
     * Returns the index of the first occurrence of a substring. The
     * search is started at fromIndex.
     * @param str 	the substring
     * @param fromIndex	the index to start the search from
     * @return 		the index if the substring is found, -1 otherwise
     */
    public int indexOf(String str, int fromIndex) {
	int max1 = str.length();
	int max2 = length();
	if (max1 <= 0) {
	    return fromIndex;
	}

	int c0 = str.charAt(0);
 test:	for (int i = fromIndex; i + max1 <= max2; i++) {
	    if (charAt(i) == c0) {
		for (int k = 1; k<max1; k++)
		    if (charAt(i+k) != str.charAt(k)) {
		        continue test;
		    }
		return i;
	    }
	}
	return -1;
    }

    /**
     * Returns the index of the last occurrence of a substring.
     * The string is searched backwards.
     * @param str 	the substring
     * @return 		the index if the substring is found, -1 otherwise
     */
    public int lastIndexOf(String str) {
	return lastIndexOf(str, length() - 1);
    }

    /**
     * Returns the index of the last occurrence of a substring.
     * The string is searched backwards starting at fromIndex.
     * @param str 	the substring
     * @param fromIndex	the index to start the search from
     * @return 		the index if the substring is found, -1 otherwise
     */
    public int lastIndexOf(String str, int fromIndex) {
	int max1 = str.length();
	int i = fromIndex, max2 = length();
	int result = -1;
	while (i - max1 + 1 >= 0) {
	    int j = i--;
	    int k = max1 - 1;
	    char c = str.charAt(k--);
	    result = lastIndexOf(c, j--);
	    if (result >= 0) {
		while (k >= 0) {
		    if (str.charAt(k--) != charAt(j--)) {
			k++;
			j++;
			break;
		    }
		}
		if (k == -1 || j == -1) {
		    return result - max1 + 1;
		}
	    }
	}
	return -1;
    }

    /**
     * Returns the substring of a String. The substring is specified
     * by a beginIndex (inclusive) and the end of the string.
     * @param beginIndex begin index, inclusive
     * @return the substring upto the end of the string
     */
    public String substring(int beginIndex) {
	return substring(beginIndex, length());
    }

    /**
     * Returns the substring of a String. The substring is specified
     * by a beginIndex (inclusive) and an endIndex (exclusive).
     * @param beginIndex begin index, inclusive
     * @param endIndex end index, exclusive
     * @return the substring
     */
    public String substring(int beginIndex, int endIndex) {
	if (beginIndex > endIndex) {
	    int tmp = beginIndex;
	    beginIndex = endIndex;
	    endIndex = tmp;
	}
	if (beginIndex < 0) {
	    throw new StringIndexOutOfRangeException(beginIndex);
	} else if (endIndex > count) {
	    throw new StringIndexOutOfRangeException(endIndex);
	}
	return (beginIndex == 0 && endIndex == count
		? this
		: new String(value, offset + beginIndex, endIndex - beginIndex));
    }

    /**
     * Concatenates a string.
     * @param str	the string which is concatenated to the end of this string
     * @return the resulting string
     */
    public String concat(String str) {
	int otherLen = str.length();
	if (otherLen == 0)
	    return this;
	char buf[] = new char[count + otherLen];
	getChars(0, count, buf, 0);
	str.getChars(0, otherLen, buf, count);
	return new String(buf);
    }

    /**
     * Converts a string by replacing occurences of oldChar with newChar.
     * @param oldChar	the old character
     * @param newChar	the new character
     * @return		the resulting string
     */
    public String replace(char oldChar, char newChar) {
	if (oldChar != newChar) {
	    int len = count;
	    int i = -1;
	    while (++i < len)
		if (value[offset + i] == oldChar)
		    break;
	    if (i < len) {
		char buf[] = new char[len];
		for (int j = 0; j<i; j++)
		    buf[j] = value[offset+j];
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
     * Converts a string to lower case.
     * @return the string, converted to lowercase
     * @see Character#toLowerCase
     * @see String#toUpperCase
     */
    public String toLowerCase() {
	int len = count;
	char trt[] = LTRT;
	int i, c;
	for (i = 0; i<len; i++) {
	    c = value[offset+i];
	    if (c<256 && trt[c] != c)
		break;
	}
	if (i>=len)
	    return this;
	char buf[] = new char[len];
	for (i = 0; i < len; i++) {
	    c = value[offset+i];
	    buf[i] = c < 256 ? trt[c] : (char) c;
	}
	return new String(buf);
    }

    /**
     * Converts a string to upper case.
     * @return the string, converted to lowercase
     * @see Character#toUpperCase
     * @see String#toLowerCase
     */
    public String toUpperCase() {
	int len = count;
	char trt[] = UTRT;
	int i, c;
	for (i = 0; i<len; i++) {
	    c = value[offset+i];
	    if (c<256 && trt[c] != c)
		break;
	}
	if (i>=len)
	    return this;
	char buf[] = new char[len];
	for (i = 0; i < len; i++) {
	    c = value[offset+i];
	    buf[i] = c < 256 ? trt[c] : (char)c;
	}
	return new String(buf);
    }

    /**
     * Trims leading and trailing whitespace from a string.
     * @return the string, with whitespace removed.
     */
    public String trim() {
	int len = count;
	int st = 0;
	while (st < len && value[offset+st] <= ' ')
	    st++;
	while (st < len && value[offset+len-1] <= ' ')
	    len--;
	return st>0 || len<count ? substring(st, len) : this;
    }

    /**
     * Converts this string to a string.
     * @return the string itself
     */
    public String toString() {
	return this;
    }

    /**
     * Converts to a character array. This creates a new array.
     * @return 	an array of characters
     */
    public char toCharArray()[] {
	int i, max = length();
	char result[] = new char[max];
	getChars(0, max, result, 0);
	return result;
    }

    /**
     * Returns a string that represents the string value of the object.
     * The object may choose how to represent itself by implementing
     * a "toString()" method.
     * @param obj	the object to be converted
     * @return 	the resulting string
     */
    public static String valueOf(Object obj) {
	return (obj == null) ? "<Null Object>" : obj.toString();
    }

    /**
     * Returns a string that is equivalent to the given character array.
     * Uses the original array as the body of the string (ie. it doesn't
     * copy it to a new array).
     * @param data	the character array
     * @return 	the resulting string
     */
    public static String valueOf(char data[]) {
	return new String(data);
    }

    /**
     * Returns a string that is equivalent to the given character array.
     * Uses the original array as the body of the string (ie. it doesn't
     * copy it to a new array).
     * @param data	the character array
     * @param offset	the offset into the value of the string
     * @param count 	the length of the value of the string
     * @return 	the resulting string
     */
    public static String valueOf(char data[], int offset, int count) {
	return new String(data, offset, count);
    }

    /**
     * Returns a string that is equivalent to the given character array.
     * It creates a new array and copies the characters into it.
     * @param data	the character array
     * @param offset	the offset into the value of the string
     * @param count 	the length of the value of the string
     * @return 	the resulting string
     */
    public static String copyValueOf(char data[], int offset, int count) {
	char str[] = new char[count];
	System.arraycopy(data, offset, str, 0, count);
	return new String(str);
    }

    /**
     * Returns a string that is equivalent to the given character array.
     * It creates a new array and copies the characters into it.
     * @param data	the character array
     * @return 	the resulting string
     */
    public static String copyValueOf(char data[]) {
	return copyValueOf(data, 0, data.length);
    }

    /**
     * Returns a string object that represents the state of a boolean.
     * @param b	the boolean
     * @return 	the resulting string
     */
    public static String valueOf(boolean b) {
	return b ? "true" : "false";
    }

    /**
     * Returns a string object that represents an integer.
     * @param i	the integer
     * @return 	the resulting string
     */
    public static String valueOf(int i) {
	return int2String(i);
    }

    /**
     * Returns a string object that represents a long.
     * @param l	the long
     * @return 	the resulting string
     */
    public static String valueOf(long l) {
	return long2String(l);
    }

    /**
     * Returns a string object that represents a float.
     * @param f	the float
     * @return 	the resulting string
     */
    public static String valueOf(float f) {
	return float2String(f);
    }

    /**
     * Returns a string object that represents a double.
     * @param d	the double
     * @return 	the resulting string
     */
    public static String valueOf(double d) {
	return double2String(d);
    }

    /**
     * The set of internalized strings.
     */
    private static Hashtable InternSet;

    /**
     * Returns a string that is equal to the current string
     * bug which is guaranteed to be from the unique string pool.
     * s1.intern() == s2.intern() <=> s1.equals(s2).
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

    private static native String int2String(int i);
    private static native String long2String(long l);
    private static native String float2String(float f);
    private static native String double2String(double d);
}



