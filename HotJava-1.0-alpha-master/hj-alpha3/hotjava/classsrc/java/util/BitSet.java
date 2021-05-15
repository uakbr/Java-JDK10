/*
 * @(#)BitSet.java	1.2 95/04/20  
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
 * A set of bits. The set is automatically grown as more bits are
 * needed. 
 *
 * @version 	1.2, 20 Apr 1995
 * @author Arthur van Hoff
 */
public final class BitSet {
    final static int BITS = 6;
    final static int MASK = (1<<BITS)-1;
    long bits[];

    /**
     * Create an empty set.
     */
    public BitSet() {
	this(1<<BITS);
    }

    /**
     * Create an empty set of a know size.
     */
    public BitSet(int nbits) {
	bits = new long[(nbits + MASK)>>BITS];
    }

    /**
     * Grow the set to a larger number of bits.
     */
    private void grow(int nbits) {
	long newbits[] = new long[Math.max(bits.length<<1, (nbits + MASK)>>BITS)];
	System.arraycopy(bits, 0, newbits, 0, bits.length);
	bits = newbits;
    }

    /**
     * Set a bit.
     */
    public void set(int bit) {
	int n = bit>>BITS;
	if (n >= bits.length) {
	    grow(bit);
	}
	bits[n] |= (1L << (bit & MASK));
    }

    /**
     * Clear a bit.
     */
    public void clear(int bit) {
	int n = bit>>BITS;
	if (n >= bits.length) {
	    grow(bit);
	}
	bits[n] &= ~(1L << (bit & MASK));
    }

    /**
     * Get a bit.
     */
    public boolean get(int bit) {
	int n = bit>>BITS;
	return (n < bits.length) ? ((bits[n] & (1L << (bit & MASK))) != 0) : false;
    }

    /**
     * Logical AND with another set of bits.
     */
    public void and(BitSet set) {
	int n = Math.min(bits.length, set.bits.length);
	for (int i = n ; i-- > 0 ; ) {
	    bits[i] &= set.bits[i];
	}
	for (; n < bits.length ; n++) {
	    bits[n] = 0;
	}
    }

    /**
     * Logical OR with another set of bits.
     */
    public void or(BitSet set) {
	for (int i = Math.min(bits.length, set.bits.length) ; i-- > 0 ;) {
	    bits[i] |= set.bits[i];
	}
    }

    /**
     * Logical XOR with another set of bits.
     */
    public void xor(BitSet set) {
	for (int i = Math.min(bits.length, set.bits.length) ; i-- > 0 ;) {
	    bits[i] ^= set.bits[i];
	}
    }

    /**
     * Compare two bit sets.
     */
    public boolean equals(BitSet set) {
	int n = Math.min(bits.length, set.bits.length);
	for (int i = n ; i-- > 0 ;) {
	    if (bits[i] != set.bits[i]) {
		return false;
	    }
	}
	if (bits.length > n) {
	    for (int i = bits.length ; i-- > n ;) {
		if (bits[i] != 0) {
		    return false;
		}
	    }
	} else if (set.bits.length > n) {
	    for (int i = set.bits.length ; i-- > n ;) {
		if (set.bits[i] != 0) {
		    return false;
		}
	    }
	}
	return true;
    }

    /**
     * Size, returns the number of bits.
     */
    public int size() {
	return bits.length << BITS;
    }

    /**
     * Compare against an object.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof BitSet)) {
	    return equals((BitSet)obj);
	}
	return false;
    }

    /**
     * Clone
     */
    public Object clone() {
	BitSet set = (BitSet)super.clone();
	set.bits = new long[bits.length];
	System.arraycopy(bits, 0, set.bits, 0, bits.length);
	return set;
    }

    /**
     * Convert to a string.
     */
    public String toString() {
	String str = "";
	for (int i = 0 ; i < (bits.length << BITS) ; i++) {
	    if (get(i)) {
		if (str.length() > 0) {
		    str += ", ";
		}
		str = str + i;
	    }
	}
	return "{" + str + "}";
    }
}
