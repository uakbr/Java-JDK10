/*
 * @(#)UCEncoder.java	1.6 95/03/17 Chuck McManis
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

import java.io.OutputStream;
import java.io.OutputStreamBuffer;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * This class implements a robust character encoder. The encoder is designed
 * to convert binary data into printable characters. The characters are
 * assumed to exist but they are not assumed to be ASCII, the complete set
 * is 0-9, A-Z, a-z, "(", and ")".
 *
 * The basic encoding unit is a 3 character atom. It encodes two bytes
 * of data. Bytes are encoded into a 64 character set, the characters
 * were chosen specifically because they appear in all codesets.
 * We don't care what their numerical equivalent is because
 * we use a character array to map them. This is like UUencoding
 * with the dependency on ASCII removed.
 *
 * The three chars that make up an atom are encoded as follows:
 * <pre>
 *      00xxxyyy 00axxxxx 00byyyyy
 *      00 = leading zeros, all values are 0 - 63
 *      xxxyyy - Top 3 bits of X, Top 3 bits of Y
 *      axxxxx - a = X parity bit, xxxxx lower 5 bits of X
 *      byyyyy - b = Y parity bit, yyyyy lower 5 bits of Y
 * </pre>
 *
 * The atoms are arranged into lines suitable for inclusion into an
 * email message or text file. The number of bytes that are encoded
 * per line is 48 which keeps the total line length  under 80 chars)
 *
 * Each line has the form(
 * <pre>
 *  *(LLSS)(DDDD)(DDDD)(DDDD)...(CRC)
 *  Where each (xxx) represents a three character atom.
 *  (LLSS) - 8 bit length (high byte), and sequence number
 *           modulo 256;
 *  (DDDD) - Data byte atoms, if length is odd, last data 
 *           atom has (DD00) (high byte data, low byte 0)
 *  (CRC)  - 16 bit CRC for the line, includes length, 
 *           sequence, and all data bytes. If there is a 
 *           zero pad byte (odd length) it is _NOT_ 
 *           included in the CRC.
 * </pre>
 *
 * @version     1.6, 17 Mar 1995
 * @author      Chuck McManis
 * @see		CharacterEncoder
 * @see		UCDecoder
 */
public class UCEncoder extends CharacterEncoder {

    /** this clase encodes two bytes per atom */
    int bytesPerAtom() {
	return (2);	
    }

    /** this class encodes 48 bytes per line */
    int bytesPerLine() {
	return (48);
    }

    /* this is the UCE mapping of 0-63 to characters .. */
    private final static byte map_array[] = {
                //       0   1   2   3   4   5   6   7
                        '0','1','2','3','4','5','6','7', // 0
                        '8','9','A','B','C','D','E','F', // 1
                        'G','H','I','J','K','L','M','N', // 2
                        'O','P','Q','R','S','T','U','V', // 3
                        'W','X','Y','Z','a','b','c','d', // 4
                        'e','f','g','h','i','j','k','l', // 5
                        'm','n','o','p','q','r','s','t', // 6
                        'u','v','w','x','y','z','(',')'  // 7
                };

    private int sequence;
    private byte tmp[] = new byte[2];
    private CRC16 crc = new CRC16();

    /**
     * encodeAtom - take two bytes and encode them into the correct
     * three characters. If only one byte is to be encoded, the other
     * must be zero. The padding byte is not included in the CRC computation.
     */  
    void encodeAtom(OutputStream outStream, byte data[], int offset, int len) {
	int     i;
	int     p1, p2; // parity bits
	byte	a, b;

	a = data[offset];
	if (len == 2) {
	    b = data[offset+1];
	} else {
	    b = 0;
	}
	crc.update(a);
	if (len == 2) {
	    crc.update(b);
	}
	outStream.write(map_array[((a >>> 2) & 0x38) + ((b >>> 5) & 0x7)]);
	p1 = 0; p2 = 0;
	for (i = 1; i < 256; i = i * 2) {
	    if ((a & i) != 0) {
		p1++;
	    }
	    if ((b & i) != 0) {
		p2++;
	    }
	}
	p1 = (p1 & 1) * 32;
	p2 = (p2 & 1) * 32;
	outStream.write(map_array[(a & 31) + p1]);
	outStream.write(map_array[(b & 31) + p2]);
	return;
    }

    /**
     * Each UCE encoded line starts with a prefix of '*[XXX]', where
     * the sequence number and the length are encoded in the first
     * atom.
     */
    void encodeLinePrefix(OutputStream outStream, int length) {
	outStream.write('*');
	crc.value = 0;
	tmp[0] = (byte) length;
	tmp[1] = (byte) sequence;
	sequence = (sequence + 1) & 0xff;
	encodeAtom(outStream, tmp, 0, 2);
    } 


    /**
     * each UCE encoded line ends with YYY and encoded version of the
     * 16 bit checksum. The most significant byte of the check sum
     * is always encoded FIRST.
     */
    void encodeLineSuffix(OutputStream outStream) {
	tmp[0] = (byte) ((crc.value >>> 8) & 0xff);
	tmp[1] = (byte) (crc.value & 0xff);
	encodeAtom(outStream, tmp, 0, 2);
	super.pStream.println();
    }

    /**
     * The buffer prefix code is used to initialize the sequence number
     * to zero.
     */
    void encodeBufferPrefix(OutputStream a) { 
	sequence = 0;
	super.encodeBufferPrefix(a);
    }
}
