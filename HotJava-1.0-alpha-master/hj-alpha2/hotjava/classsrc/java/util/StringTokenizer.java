/*
 * @(#)StringTokenizer.java	1.8 95/01/31  
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

import java.lang.*;

/**
 * A class to tokenize a string.
 * StringTokenizer is a class that controls simple linear tokenization
 * of a string. The set of delimiters, which defaults to common whitespace
 * characters, may be specified at creation time or on a per-token basis.<p>
 *
 * Example usage:
 * <pre>
 *	String s = "this is a test";
 *	StringTokenizer st = new StringTokenizer(s);
 *	while (st.hasMoreTokens()) {
 *		println(st.nextToken());
 *	}
 * </pre>
 * Prints the following on the console:
 * <pre>
 *	this
 *	is
 *	a
 *	test
 * </pre>
 * @version 	1.8, 31 Jan 1995
 */
public
class StringTokenizer implements Enumeration {
    private int currentPosition;
    private int maxPosition;
    private String str;
    private String delimiters;

    /**
     * Constructs a StringTokenizer on the specified string, using the
     * specified delimiter set.
     * @param str	the input string
     * @param delim the delimiter string
     */
    public StringTokenizer(String str, String delim) {
	currentPosition = 0;
	this.str = str;
	maxPosition = str.length();
	delimiters = delim;
    }

    /**
     * Constructs a StringTokenizer on the specified string, using the
     * default delimiter set (which is "\t\n\r").
     */
    public StringTokenizer(String str) {
	this(str, " \t\n\r");
    }

    /**
     * Skip delimiters
     */
    private void skipDelimiters() {
	while ((currentPosition < maxPosition) && (delimiters.indexOf(str.charAt(currentPosition)) >= 0)) {
	    currentPosition++;
	}
    }

    /**
     * Returns true if more tokens exist.
     */
    public boolean hasMoreTokens() {
	skipDelimiters();
	return (currentPosition < maxPosition);
    }

    /**
     * Returns the next token of the string.
     * @exception NoSuchElementException there are no more tokens in the string
     */
    public String nextToken() {
	skipDelimiters();

	if (currentPosition >= maxPosition) {
	    throw new NoSuchElementException();
	}

	int start = currentPosition;
	while ((currentPosition < maxPosition) && (delimiters.indexOf(str.charAt(currentPosition)) < 0)) {
	    currentPosition++;
	}
	return str.substring(start, currentPosition);
    }

    /**
     * Returns the next token, after switching to the new delimiter set.
     * The new delimiter set remains the default after this call.
     * @param delim the new delimiters
     */
    public String nextToken(String delim) {
	delimiters = delim;
	return nextToken();
    }

    /**
     * Returns true if the Enumeration has more elements.
     */
    public boolean hasMoreElements() {
	return hasMoreTokens();
    }

    /**
     * Returns the next element in the Enumeration.
     * @exception NoSuchElementException There are no more elements in the enumeration
     */
    public Object nextElement() {
	return nextToken();
    }
}


