/*
 * @(#)Basic.java	1.6 95/02/22 Jonathan Payne
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

package net.www.auth;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *  From RFC 1421
 *
 * @version 1.6 22 Feb 1995
 * @author Jonathan Payne
 */
public class Basic extends Authenticator {
    static private byte six2print[] = {
	'A','B','C','D','E','F','G','H','I','J','K','L','M',
	'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
	'a','b','c','d','e','f','g','h','i','j','k','l','m',
	'n','o','p','q','r','s','t','u','v','w','x','y','z',
	'0','1','2','3','4','5','6','7','8','9','+','/'
    };

    static private byte print2six[] = new byte[128];
    static final int	ILLEGAL = -1;

    static {
	int i, limit;

	limit = six2print.length;

	for (i = print2six.length; --i >= 0; ) {
	    print2six[i] = (byte) ILLEGAL;
	}

	for (i = 0; --limit >= 0; i++) {
	    print2six[six2print[i]] = (byte) i;
	}
    }

    public void encrypt(InputStream is, OutputStream os) {
	int chunk;
	int c = -1;
	int lineCnt = 64;
	int shift;

loop:
	do {
	    shift = 24;
	    chunk = 0;

	    while ((shift -= 8) >= 0) {
		switch (c = is.read()) {
		case -1:
		    break loop;

		default:
		    chunk |= (c << shift);
		    break;
		}
	    }
	    shift = 24;
	    while ((shift -= 6) >= 0) {
		os.write(six2print[(chunk >> shift) & 0x3f]);
	    }
	    if ((lineCnt -= 4) == 0) {
		os.write('\n');
		lineCnt = 64;
	    }
	} while (true);

	switch (shift) {
	case 16:    /* 0 bits left to process */
	    break;

	case 8:	    /* 8 bits (1 byte) left to process */
	    os.write(six2print[(chunk >> 18) & 0x3f]);
	    os.write(six2print[(chunk >> 12) & 0x3f]);
	    os.write('=');
	    os.write('=');
	    break;

	case 0:	    /* 16 bits (2 bytes) left to process */
	    os.write(six2print[(chunk >> 18) & 0x3f]);
	    os.write(six2print[(chunk >> 12) & 0x3f]);
	    os.write(six2print[(chunk >> 6) & 0x3f]);
	    os.write('=');
	    break;
	}
    }

    public void decrypt(InputStream is, OutputStream os) {
	int c;
	int shift;
	int chunk;
	int charCnt = 64;

loop:
	do {
	    shift = 24;
	    chunk = 0;
	    while ((shift -= 6) >= 0) {
		switch (c = is.read()) {
		case '=':
		    break loop;

		case '\n':
		    if (charCnt == 0) {
			charCnt = 64;
			continue loop;
		    }
		    throw new Exception("Invalid encryption");

		case -1:
		    if (shift == 18) {
			throw new Exception("Invalid encryption");
		    }
		    break loop;

		default:
		    {
			int cc = print2six[c];

			switch (cc) {
			case ILLEGAL:
			    throw new Exception("Invalid encryption");

			default:
			    chunk |= (cc << shift);
			    break;
			}
		    }
		    break;
		}
	    }
	    shift = 24;
	    while ((shift -= 8) >= 0) {
		os.write((chunk >> shift) & 0xff);
	    }
	    charCnt -= 4;
	} while (true);

	switch (shift) {
	case 18:	/* nothing to process */
	    break;

	case 12:	/* one 6 bit entity is illegal */
	    throw new Exception("Invalid encryption");

	case 6:		/* two 6 bit entities = one 8 bit byte */
	    os.write((chunk >> 16) & 0xff);
	    break;

	case 0:
	    os.write((chunk >> 16) & 0xff);
	    os.write(chunk & 0xff);
	    break;
	}	    
    }

    static public void main(String args[]) {
	Authenticator a = new Basic();
	FileOutputStream os = new FileOutputStream("/home/jpayne/livejava/test.uu");

	a.encrypt(new FileInputStream("/home/jpayne/livejava/test.ascii"), os);
	os.close();
	a.decrypt(new FileInputStream("/home/jpayne/livejava/test.uu"), System.out);
	System.out.flush();
    }
}
