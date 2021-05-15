/*
 * @(#)Xpm2Image.java	1.4 95/01/31 James Gosling
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies. Please
 * refer to the file "copyright.html" for further important copyright and
 * licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 */

/*-
 *	Reads xpixmap format images into a DIBitmap structure.
 */
package awt;

import java.util.*;
import java.io.*;

/**
 * Parse x pixmap (XPM2) image files and convert them into device
 * independent bitmaps.
 *
 * @version 1.4 31 Jan 1995
 * @author James Gosling
 */
public class Xpm2Image extends DIBitmap {

    public Xpm2Image (String fname) {
	this(new BufferedInputStream(new FileInputStream(fname)));
    }

    private int readline(InputStream s, byte dst[]) {
	int c;
	int i = 0;
	while ((c = s.read()) != '\n' && c >= 0)
	    if (i < dst.length)
		dst[i++] = (byte) c;
	return i > 0 ? i : c < 0 ? -1 : 0;
    }

    public Xpm2Image (InputStream s) {
	byte buf[] = new byte[25];
	int lwidth = 0;
	int c;
	int colors = 0;
	if (readline(s, buf) < 5
		|| buf[0] != '!'
		|| buf[1] != ' '
		|| buf[2] != 'X'
		|| buf[3] != 'P'
		|| buf[4] != 'M'
		|| (lwidth = readline(s, buf)) < 7)
	    throw new FileFormatException();
	for (int pos = 0, state = 0, n = 0; pos < lwidth; pos++)
	    if ((c = buf[pos]) >= '0' && c <= '9') {
		n = n * 10 + c - '0';
		switch (state) {
		  case 0:
		    width = n;
		    break;
		  case 1:
		    height = n;
		    break;
		  case 2:
		    colors = n;
		    break;
		}
	    } else if (c == ' ') {
		n = 0;
		state++;
	    } else
		throw new FileFormatException();
	if (width <= 0 || height <= 0 || colors <= 1
		|| colors > 128 || width > 3000 || height > 3000)
	    throw new FileFormatException();
	red = new byte[colors];
	green = new byte[colors];
	blue = new byte[colors];
	byte trt[] = new byte[128];
	for (int i = 0; i < colors; i++) {
	    lwidth = readline(s, buf);
	    if (lwidth < 11
		    || buf[1] != ' '
		    || buf[2] != 'c'
		    || buf[3] != ' '
		    || buf[4] != '#')
		throw new FileFormatException();
	    trt[buf[0] & 0x7F] = (byte) i;
	    int swidth = lwidth == 11 ? 2 : 4;
	    for (int j = 5, sw = 0, slot = 0, n = 0; j < lwidth; j++) {
		c = buf[j];
		if (c >= '0' && c <= '9')
		    c = c - '0';
		else if (c >= 'A' && c <= 'F')
		    c = c - 'A' + 10;
		else if (c >= 'a' && c <= 'f')
		    c = c - 'a' + 10;
		else {
		    throw new FileFormatException();
		}
		if (sw < 2)
		    n = (n << 4) | c;
		if (++sw >= swidth) {
		    sw = 0;
		    switch (slot) {
		      case 0:
			red[i] = (byte) n;
			break;
		      case 1:
			green[i] = (byte) n;
			break;
		      case 2:
			blue[i] = (byte) n;
			break;
		    }
		    slot++;
		    n = 0;
		}
	    }
	}
	raster = new byte[width * height];
	num_colors = colors;
	trans_index = -1;
	if (buf.length < width)
	    buf = new byte[width];
	int yi = 0;
	for (int y = 0; y < height; y++) {
	    lwidth = readline(s, buf);
	    if (lwidth > width)
		lwidth = width;
	    for (int x = 0; x < lwidth; x++) {
		raster[yi + x] = trt[buf[x] & 0x7F];
	    }
	    yi += width;
	}
	s.close();
    }

}
