/*
 * @(#)XbmImage.java	1.10 95/01/31 James Gosling
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

/*-
 *	Reads xbitmap format images into a DIBitmap structure.
 */
package awt;

import java.util.*;
import java.io.*;

/**
 * Parse files of the form:
 * 
 * #define foo_width w
 * #define foo_height h
 * static char foo_bits[] = {
 * 0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,
 * 0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,0xnn,
 * 0xnn,0xnn,0xnn,0xnn};
 * 
 * @version 1.10 31 Jan 1995
 * @author James Gosling
 */
public class XbmImage extends DIBitmap {

    private static final boolean verbose = true;
    private static final int BUFSIZE = 1024;

    private static void error(String s1) {
	throw new Exception(s1);
    }

    public XbmImage (String fname) {
	this(new BufferedInputStream(new FileInputStream(fname), BUFSIZE));
    }

    public XbmImage (InputStream s) {
	char nm[] = new char[80];
	int c;
	int i = 0;
	int state = 0;
	int H = 0;
	int W = 0;
	int x = 0;
	int y = 0;
	boolean start = true;
	int rpos = 0;
	while ((c = s.read()) != -1) {
	    if ('a' <= c && c <= 'z' ||
		    'A' <= c && c <= 'Z' ||
		    '0' <= c && c <= '9' || c == '#' || c == '_') {
		if (i < 78)
		    nm[i++] = (char) c;
	    } else if (i > 0) {
		int nc = i;
		i = 0;
		if (start) {
		    if (nc != 7 ||
			nm[0] != '#' ||
			nm[1] != 'd' ||
			nm[2] != 'e' ||
			nm[3] != 'f' ||
			nm[4] != 'i' ||
			nm[5] != 'n' ||
			nm[6] != 'e')
			throw new FileFormatException("Not an XBM file");
		    start = false;
		}
		if (nm[nc - 1] == 'h')
		    state = 1;	/* expecting width */
		else if (nm[nc - 1] == 't' && nc > 1 && nm[nc - 2] == 'h')
		    state = 2;	/* expecting height */
		else if (nc > 2 && state < 0 && nm[0] == '0' && nm[1] == 'x') {
		    int n = 0;
		    for (int p = 2; p < nc; p++) {
			c = nm[p];
			if ('0' <= c && c <= '9')
			    c = c - '0';
			else if ('A' <= c && c <= 'Z')
			    c = c - 'A' + 10;
			else if ('a' <= c && c <= 'z')
			    c = c - 'a' + 10;
			else
			    c = 0;
			n = n * 16 + c;
		    }
		    for (int mask = 1; mask <= 0x80; mask <<= 1) {
			if (x < W) {
			    if ((n & mask) != 0)
				raster[rpos] = 1;
			    rpos++;
			}
			x++;
		    }
		    if (x >= W) {
			x = 0;
			y++;
		    }
		} else {
		    int n = 0;
		    for (int p = 0; p < nc; p++)
			if ('0' <= (c = nm[p]) && c <= '9')
			    n = n * 10 + c - '0';
			else {
			    n = -1;
			    break;
			}
		    if (n > 0 && state > 0) {
			if (state == 1)
			    W = n;
			else
			    H = n;
			if (W == 0 || H == 0)
			    state = 0;
			else {
			    raster = new byte[W * H];
			    red = new byte[2];
			    green = red;
			    blue = red;
			    red[0] = (byte) 255;
			    red[1] = 0;
			    width = W;
			    height = H;
			    num_colors = 2;
			    state = -1;
			}
		    }
		}
	    }
	}
	s.close();
    }

}
