/*
 * @(#)MessageDigest.java	1.1 95/04/02  
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

package java.crypt;

import java.util.*;
import java.lang.*;
import java.io.OutputStreamBuffer;
import java.io.PrintStream;


/**
 * The MessageDigest class defines a general class for computing digest
 * functions. It is defined as an abstract class that is subclassed by
 * message digest algorithms. In this way the PKCS classes can be built
 * to take a MessageDigest object without needing to know what 'kind'
 * of message digest they are computing.
 *
 * This class defines the standard functions that all message digest
 * algorithms share, and ways to put all Java fundamental types into
 * the digest. It does not define methods for digestifying either
 * arbitrary objects or arrays of objects however.
 *
 * @version 	02 Apr 1995, 1.1
 * @author 	Chuck McManis
 */
public class MessageDigest {

    /** the actual digest bits. */
    public byte digestBits[];

    /** status of the digest */
    public boolean digestValid;

    /**
     * This function is used to initialize any internal digest
     * variables or parameters.
     */
    public abstract void init();

    /**
     * The basic unit of digestifying is the byte. This method is
     * defined by the particular algorithim's subclass for that
     * algorithim. Subsequent versions of this method defined here
     * decompose the basic type into bytes and call this function.
     * If special processing is needed for a particular type your
     * subclass should override the method for that type. 
     */
    public abstract void update(byte aValue);

    public synchronized void update(boolean aValue) {
	byte	b;

	if (aValue)
	    b = 1;
	else
	    b = 0;
	update(b);
    }

    public synchronized void update(short aValue) {
	byte	b1, b2;
	
	b1 = (byte)((aValue >>> 8) & 0xff);
	b2 = (byte)(aValue & 0xff);
	update(b1);
	update(b2);
    }

    public synchronized void update(int aValue) {
	byte	b;
	
	for (int i = 3; i >= 0; i--) {
	    b = (byte)((aValue >>> (i * 8)) & 0xff);
	    update(b);
	}
    }

    public synchronized void update(long aValue) {
	byte	b;
	
	for (int i = 7; i >= 0; i--) {
	    b = (byte)((aValue >>> (i * 8)) & 0xff);
	    update(b);
	}
    }

    public synchronized void update(byte input[]) {
	for (int i = 0; i < input.length; i++) {
	    update(input[i]);
	}
    }

    public synchronized void update(short input[]) {
	for (int i = 0; i < input.length; i++) {
	    update(input[i]);
	}
    }

    public synchronized void update(int input[]) {
	for (int i = 0; i < input.length; i++) {
	    update(input[i]);
	}
    }

    public synchronized void update(long input[]) {
	for (int i = 0; i < input.length; i++) {
	    update(input[i]);
	}
    }

    /**
     * Add the bytes in the String 'input' to the current digest.
     * Note that the string characters are treated as unicode chars
     * of 16 bits each. To digestify ISO-Latin1 strings (ASCII) use
     * the updateASCII() method.
     */
    public void update(String input) {
	int	i, len;
	short	x;

	len = input.length();
	for (i = 0; i < len; i++) {
	    x = (short) input.charAt(i);
	    update(x);
	}
    }

    /**
     * Treat the string as a sequence of ISO-Latin1 (8 bit) characters.
     */
    public void updateASCII(String input) {
	int	i, len;
	byte	x;

	len = input.length();
	for (i = 0; i < len; i++) {
	    x = (byte) (input.charAt(i) & 0xff);
	    update(x);
	}
    }

    /**
     * Perform the final computations and cleanup.
     */
    public abstract void finish();

    /**
     * Complete digest computation on an array of bytes.
     */
    public void computeDigest(byte source[]) {
	init();
	update(source);
	finish();
    }

    /**
     * helper function that prints unsigned two character hex digits.
     */
    private void hexDigit(PrintStream p, byte x) {
	char c;
	
	c = (char) ((x >> 4) & 0xf);
	if (c > 9)
		c = (char) ((c - 10) + 'A');
	else
		c = (char) (c + '0');
	p.write(c);
	c = (char) (x & 0xf);
	if (c > 9)
		c = (char)((c-10) + 'A');
	else
		c = (char)(c + '0');
	p.write(c);
    }

    /**
`    * Return a string representation of this object.
     */
    public String toString() {
	OutputStreamBuffer ou = new OutputStreamBuffer();
	PrintStream p = new PrintStream(ou);
		
	p.print(this.getClass().getName()+" Message Digest ");
	if (digestValid) {
	    p.print("<");
	    for(int i = 0; i < digestBits.length; i++)
 	        hexDigit(p, digestBits[i]);
	    p.print(">");
	} else {
	    p.print("<incomplete>");
	}
	p.println();
	return (ou.toString());
    }

    /**
     * Compare two digests for equality. Simple byte compare.
     */
    public static boolean isEqual(byte digesta[], byte digestb[]) {
	int	i;
		
	if (digesta.length != digestb.length)
	    return (false);

	for (i = 0; i < digesta.length; i++) {
	    if (digesta[i] != digestb[i]) {
		return (false);
	    }
	}
	return (true);
    }

    /**
     * Non static version that compares this digest to one passed.
     */
    public boolean isEqual(byte otherDigest[]) {
	return (MessageDigest.isEqual(digestBits, otherDigest));
    }
}
