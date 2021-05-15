/*
 * @(#)CRC16.java	1.4 95/03/17 Chuck McManis
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
 * The CRC-16 class calculates a 16 bit cyclic redundancy check of a set
 * of bytes. This error detecting code is used to determine if bit rot
 * has occured in a byte stream.
 */

public class CRC16 {

    /** value contains the currently computed CRC, set it to 0 initally */
    public int value;

    public CRC16() {
	value = 0;
    }

    /** update CRC with byte b */
    public void update(byte aByte) {
	int a, b;

	a = (int) aByte;
	for (int count = 7; count >=0; count--) {
	    a = a << 1;
            b = (a >>> 8) & 1;
	    if ((value & 0x8000) != 0) {
		value = ((value << 1) + b) ^ 0x1021;
	    } else {
		value = (value << 1) + b;
	    }
	}
	value = value & 0xffff;
	return;
    }

    /** reset CRC value to 0 */
    public void reset() {
	value = 0;
    }
}
