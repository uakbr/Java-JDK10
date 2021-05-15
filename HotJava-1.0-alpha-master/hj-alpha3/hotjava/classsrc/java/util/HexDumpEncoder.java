/*
 * @(#)HexDumpEncoder.java	1.1 95/04/02 Chuck McManis
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
import java.io.OuputStreamBuffer;
import java.io.PrintStream;
import java.io.OutputStream;

/**
 * This class encodes a buffer into the classic: "Hexadecimal Dump" format of
 * the past. It is useful for analyzing the contents of binary buffers.
 * The format produced is as follows:
 * <pre>
 * xxxx: 00 11 22 33 44 55 66 77   88 99 aa bb cc dd ee ff ................
 * </pre>
 * Where xxxx is the offset into the buffer in 16 byte chunks, followed
 * by ascii coded hexadecimal bytes followed by the ASCII representation of
 * the bytes or '.' if they are not valid bytes.
 *
 * @version	1.1, 02 Apr 1995
 * @author	Chuck McManis
 */

public class HexDumpEncoder extends CharacterEncoder {

    private int offset;
    private int thisLineLength;
    private int currentByte;
    private byte thisLine[] = new byte[16];

    static void hexDigit(PrintStream p, byte x) {
	char c;

	c = (char) ((x >> 4) & 0xf);
	if (c > 9)
	    c = (char) ((c-10) + 'A');
	else
	    c = (char)(c + '0');
	p.write(c);
	c = (char) (x & 0xf);
	if (c > 9)
	    c = (char)((c-10) + 'A');
	else
	    c = (char)(c + '0');
	p.write(c);
    }

    int bytesPerAtom() {
	return (1);
    }

    int bytesPerLine() {
	return (16);
    }

    void encodeBufferPrefix(OutputStream o) {
	offset = 0;
	super.encodeBufferPrefix(o);
    }

    void encodeLinePrefix(OutputStream o, int len) {
	hexDigit(pStream, (byte)((offset >>> 8) & 0xff));
	hexDigit(pStream, (byte)(offset & 0xff));
	pStream.print(": ");
	currentByte = 0;
	thisLineLength = len;
    }
	
    void encodeAtom(OutputStream o, byte buf[], int off, int len) {
	thisLine[currentByte] = buf[off];
	hexDigit(pStream, buf[off]);
	pStream.print(" ");
	currentByte++;
	if (currentByte == 8)
	    pStream.print("  ");
    }

    void encodeLineSuffix(OutputStream o) {
	if (thisLineLength < 16) {
	    for (int i = thisLineLength; i < 16; i++) {
		pStream.print("   ");
		if (i == 7)
		    pStream.print("  ");
	    }
	}
	pStream.print(" ");
	for (int i = 0; i < thisLineLength; i++) {
	    if ((thisLine[i] < ' ') || (thisLine[i] > 'z')) {
		pStream.print(".");
	    } else {
		pStream.write(thisLine[i]);
            }
	}
	pStream.println();
	offset += thisLineLength;
    }

}
