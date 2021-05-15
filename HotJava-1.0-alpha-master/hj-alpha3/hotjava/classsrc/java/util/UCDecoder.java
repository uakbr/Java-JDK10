/*
 * @(#)UCDecoder.java	1.2 95/03/17 Chuck McManis
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
 * This class implements a robust character decoder. The decoder will
 * converted encoded text into binary data.
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
 * If an error is encountered during decoding this class throws a 
 * CEFormatException. The specific detail messages are:
 *
 * <pre>
 *    "UCDecoder: High byte parity error."
 *    "UCDecoder: Low byte parity error."
 *    "UCDecoder: Out of sequence line."
 *    "UCDecoder: CRC check failed."
 * </pre>
 *
 * @version     1.2, 17 Mar 1995
 * @author      Chuck McManis
 * @see		CharacterEncoder
 * @see		UCEncoder
 */
public class UCDecoder extends CharacterDecoder {

    /** This class encodes two bytes per atom. */
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
     * Decode one atom - reads the characters from the input stream, decodes
     * them, and checks for valid parity.
     */
    void decodeAtom(InputStream inStream, OutputStream outStream, int l) {
	int i, p1, p2, np1, np2;
	byte a = -1, b = -1, c = -1;
	byte high_byte, low_byte;
	byte tmp[] = new byte[3];

	i = inStream.read(tmp);
	if (i != 3) {
		throw new CEStreamExhausted();
	}
	for (i = 0; (i < 64) && ((a == -1) || (b == -1) || (c == -1)); i++) {
	    if (tmp[0] == map_array[i]) {
		a = (byte) i;
	    }
	    if (tmp[1] == map_array[i]) {
		b = (byte) i;
	    }
	    if (tmp[2] == map_array[i]) {
		c = (byte) i;
	    }
	}
	high_byte = (byte) (((a & 0x38) << 2) + (b & 0x1f));
	low_byte = (byte) (((a & 0x7) << 5) + (c & 0x1f));
	p1 = 0;
	p2 = 0;
	for (i = 1; i < 256; i = i * 2) {
	    if ((high_byte & i) != 0)
		p1++;
	    if ((low_byte & i) != 0)
		p2++;
	}
	np1 = (b & 32) / 32;
	np2 = (c & 32) / 32;
	if ((p1 & 1) != np1) {
	    throw new CEFormatException("UCDecoder: High byte parity error.");
	}
	if ((p2 & 1) != np2) {
	    throw new CEFormatException("UCDecoder: Low byte parity error.");
	}
	outStream.write(high_byte);
	crc.update(high_byte);
	if (l == 2) {
	    outStream.write(low_byte);
	    crc.update(low_byte);
	}
    }
	
    private OutputStreamBuffer lineAndSeq = new OutputStreamBuffer(2);

    /**
     * decodeBufferPrefix initializes the sequence number to zero.
     */
    void decodeBufferPrefix(InputStream inStream, OutputStream outStream) {
	sequence = 0;
    }

    /**
     * decodeLinePrefix reads the sequence number and the number of
     * encoded bytes from the line. If the sequence number is not the
     * previous sequence number + 1 then an exception is thrown.
     * UCE lines are line terminator immune, they all start with *
     * so the other thing this method does is scan for the next line
     * by looking for the * character.
     *
     * @exception CEFormatException out of sequence lines detected.
     */
    int decodeLinePrefix(InputStream inStream, OutputStream outStream) {
	int 	i;
	int	nLen, nSeq;
	byte	xtmp[];
	int	c;

	crc.value = 0;
	while (true) {
	    c = inStream.read(tmp, 0, 1);
	    if (c == -1) {
		throw new CEStreamExhausted();
	    }
	    if (tmp[0] == '*') { 
		break;
	    }
	}
	lineAndSeq.reset();
	decodeAtom(inStream, lineAndSeq, 2);
	xtmp = lineAndSeq.toByteArray();
	nLen = xtmp[0] & 0xff;
	nSeq = xtmp[1] & 0xff;
	if (nSeq != sequence) {
	    throw new CEFormatException("UCDecoder: Out of sequence line.");
	}
	sequence = (sequence + 1) & 0xff;
	return (nLen);
    }


    /**
     * this method reads the CRC that is at the end of every line and
     * verifies that it matches the computed CRC. 
     *
     * @exception CEFormatException if CRC check fails.
     */
    void decodeLineSuffix(InputStream inStream, OutputStream outStream) {
	int i;
	int lineCRC = crc.value;
	int readCRC;
	byte tmp[];
    
	lineAndSeq.reset();
	decodeAtom(inStream, lineAndSeq, 2);
	tmp = lineAndSeq.toByteArray();
	readCRC = ((tmp[0] << 8) & 0xFF00) + (tmp[1] & 0xff);
	if (readCRC != lineCRC) {
	    throw new CEFormatException("UCDecoder: CRC check failed.");
	}
    }
}
