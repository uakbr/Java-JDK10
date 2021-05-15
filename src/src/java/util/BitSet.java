/*
 * @(#)BitSet.java	1.12 95/12/01  
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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
 * A set of bits. The set automatically grows as more bits are
 * needed. 
 *
 * @version 	1.12, 12/01/95
 * @author Arthur van Hoff
 */
public final class BitSet implements Cloneable {
    final static int BITS = 6;
    final static int MASK = (1<<BITS)-1;
    long bits[];

    /**
     * Creates an empty set.
     */
    public BitSet() {
	this(1<<BITS);
    }

    /**
     * Creates an empty set with the specified size.
     * @param nbits the size of the set
     */
    public BitSet(int nbits) {
	bits = new long[(nbits + MASK)>>BITS];
    }

    /**
     * Grows the set to a larger number of bits.
     * @param nbits the number of bits to increase the set by
     */
    private void grow(int nbits) {
	long newbits[] = new long[Math.max(bits.length<<1, (nbits + MASK)>>BITS)];
	System.arraycopy(bits, 0, newbits, 0, bits.length);
	bits = newbits;
    }

    /**
     * Sets a bit.
     * @param bit the bit to be set
     */
    public void set(int bit) {
	int n = bit>>BITS;
	if (n >= bits.length) {
	    grow(bit);
	}
	bits[n] |= (1L << (bit & MASK));
    }

    /**
     * Clears a bit.
     * @param bit the bit to be cleared
     */
    public void clear(int bit) {
	int n = bit>>BITS;
	if (n >= bits.length) {
	    grow(bit);
	}
	bits[n] &= ~(1L << (bit & MASK));
    }

    /**
     * Gets a bit.
     * @param bit the bit to be gotten
     */
    public boolean get(int bit) {
	int n = bit>>BITS;
	return (n < bits.length) ? ((bits[n] & (1L << (bit & MASK))) != 0) : false;
    }

    /**
     * Logically ANDs this bit set with the specified set of bits.
     * @param set the bit set to be ANDed with
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
     * Logically ORs this bit set with the specified set of bits.
     * @param set the bit set to be ORed with
     */
    public void or(BitSet set) {
	for (int i = Math.min(bits.length, set.bits.length) ; i-- > 0 ;) {
	    bits[i] |= set.bits[i];
	}
    }

    /**
     * Logically XORs this bit set with the specified set of bits.
     * @param set the bit set to be XORed with
     */
    public void xor(BitSet set) {
	for (int i = Math.min(bits.length, set.bits.length) ; i-- > 0 ;) {
	    bits[i] ^= set.bits[i];
	}
    }

    /**
     * Gets the hashcode.
     */
    public int hashCode() {
	long h = 1234;
	for (int i = bits.length; --i >= 0; ) {
	    h ^= bits[i] * i;
	}
	return (int)((h >> 32) ^ h);
    }

    /**
     * Calculates and returns the set's size
     */
    public int size() {
	return bits.length << BITS;
    }

    /**
     * Compares this object against the specified object.
     * @param obj the object to commpare with
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof BitSet)) {
	    BitSet set = (BitSet)obj;

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
	return false;
    }

    /**
     * Clones the BitSet.
     */
    public Object clone() {
	try { 
	    BitSet set = (BitSet)super.clone();
	    set.bits = new long[bits.length];
	    System.arraycopy(bits, 0, set.bits, 0, bits.length);
	    return set;
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Converts the BitSet to a String.
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
