/*
 * @(#)BASE64Encoder.java	1.2 95/03/16 Chuck McManis
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
import java.io.InputStream;
import java.io.PrintStream;

/**
 * This class implements a BASE64 Character encoder as specified in RFC1113.
 * This RFC is part of the Privacy Enhanced Mail (PEM) specification as
 * published by the Internet Engineering Task Force (IETF). Unlike some
 * other encoding schemes there is nothing in this encoding that indicates
 * where a buffer starts or ends.
 *
 * This means that the encoded text will simply start with the first line
 * of encoded text and end with the last line of encoded text.
 *
 * @version	1.2, 16 Mar 1995
 * @author	Chuck McManis
 * @see		CharacterEncoder
 * @see		BASE64Decoder
 */

public class BASE64Encoder extends CharacterEncoder {
	
    /** this class encodes three bytes per atom. */
    int bytesPerAtom() {
	return (3);
    }

    /** this class encodes 48 bytes per line. */
    int bytesPerLine() {
	return (48);
    }

    /** This array maps the characters to their 6 bit values */
    private final static char pem_array[] = {
	//       0   1   2   3   4   5   6   7
		'A','B','C','D','E','F','G','H', // 0
		'I','J','K','L','M','N','O','P', // 1
		'Q','R','S','T','U','V','W','X', // 2
		'Y','Z','a','b','c','d','e','f', // 3
		'g','h','i','j','k','l','m','n', // 4
		'o','p','q','r','s','t','u','v', // 5
		'w','x','y','z','0','1','2','3', // 6
		'4','5','6','7','8','9','+','/'  // 7
	};

    /** 
     * enocodeAtom - Take three bytes of input and encode it as 4
     * printable characters. Note that if the length in len is less
     * than three is encodes either one or two '=' signs to indicate
     * padding characters.
     */
    void encodeAtom(OutputStream outStream, byte data[], int offset, int len) {
	byte a, b, c;

	if (len == 1) {
	    a = data[offset];
	    b = 0;
	    c = 0;
	    outStream.write(pem_array[(a >>> 2) & 0x3F]);
	    outStream.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
	    outStream.write('=');
	    outStream.write('=');
	} else if (len == 2) {
	    a = data[offset];
	    b = data[offset+1];
	    c = 0;
	    outStream.write(pem_array[(a >>> 2) & 0x3F]);
	    outStream.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
	    outStream.write(pem_array[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
	    outStream.write('=');
	} else {
	    a = data[offset];
	    b = data[offset+1];
	    c = data[offset+2];
	    outStream.write(pem_array[(a >>> 2) & 0x3F]);
	    outStream.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
	    outStream.write(pem_array[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
	    outStream.write(pem_array[c & 0x3F]);
	}
    }
}
