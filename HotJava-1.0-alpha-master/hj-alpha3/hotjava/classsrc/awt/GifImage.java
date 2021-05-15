/*
 * @(#)GifImage.java	1.24 95/05/11 Patrick Naughton, Arthur van Hoff
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
 *	Reads GIF images into a DIBitmap structure.
 *
 * The algorithm is copyright of CompuServe.
 */
package awt;

import java.io.*;
import browser.Observer;

/**
 * Gif Image converter
 * 
 * @version 1.24 11 May 1995
 * @author Arthur van Hoff
 */
public
class GifImage extends DIBitmap {
    private final boolean verbose = false;

    private static final int IMAGESEP 		= 0x2c;
    private static final int EXBLOCK 		= 0x21;
    private static final int EX_GRAPHICS_CONTROL= 0xf9;
    private static final int EX_COMMENT 	= 0xfe;
    private static final int EX_APPLICATION 	= 0xff;
    private static final int TERMINATOR 	= 0x3b;
    private static final int INTERLACEMASK 	= 0x40;
    private static final int COLORMAPMASK 	= 0x80;
    private static final int BUFSIZE 		= 2048;

    /**
     * An error has occurred. Throw an exception.
     */
    private static void error(String s1) {
	throw new Exception(s1);
    }

    /**
     * Read bytes
     */
    boolean readBytes(InputStream in, byte buf[], int off, int len) {
	while (len > 0) {
	    int n = in.read(buf, off, len);
	    if (n < 0) {
		return false;
	    }
	    off += n;
	    len -= n;
	}
	return true;
    }

    /**
     * Constructor, read the image from a file
     */
    public GifImage(String fname) {
	// Don't buffer this stream, most data is read in chunks anyway...
	this(new FileInputStream(fname), null);
    }

    public GifImage(InputStream in) {
	this(in, null);
    }

    /**
     * Constructor, read image from stream.
     */
    public GifImage(InputStream in, Observer o) {
	if (o != null) {
	    addObserver(o);
	}
	try {
	    readHeader(in);

	    while (true) {
		int code;

		switch (code = in.read()) {
		  case EXBLOCK:
		    switch (code = in.read()) {
		      case EX_GRAPHICS_CONTROL: {
			byte buf[] = new byte[6];
			if (!readBytes(in, buf, 0, 6)) {
			    return;//error("corrupt GIF file");
			}
			if ((buf[0] != 4) || (buf[5] != 0)) {
			    return;//error("corrupt GIF file (GCE size)");
			}
			// Get the index of the transparent color
			trans_index = buf[4] & 0xFF;
			break;
		      }

		      case EX_COMMENT:
		      case EX_APPLICATION:
		      default:
			while (true) {
			    int n = in.read();
			    if (n == 0) {
				break;
			    }
			    byte buf[] = new byte[n];
			    if (!readBytes(in, buf, 0, n)) {
				return;//error("corrupt GIF file");
			    }
			}
			break;
		    }
		    break;

		  case IMAGESEP:
		    try {
			readImage(in);
		    } catch (ArrayIndexOutOfBoundsException e) {
			if (verbose) {
			    e.printStackTrace();
			}
			return;//error("corrupt gif file");
		    }
		    break;

		  case TERMINATOR:
		    return;

		  case -1:
		    return;

		  default:
		    return;//error("corrupt GIF file (parse) [" + code + "].");
		    //break;
		}
	    }
	} finally {
	    in.close();
	}
    }

    /**
     * Read Image header
     */
    private void readHeader(InputStream in) {
	// Create a buffer
	byte buf[] = new byte[256*3];

	// Read the header
	if (!readBytes(in, buf, 0, 13)) {
	    throw new IOException();
	}

	// Check header
	if ((buf[0] != 'G') || (buf[1] != 'I') || (buf[2] != 'F')) {
	    error("not a GIF file.");
	}

	// colormap info
	int ch = buf[10] & 0xFF;
	if ((ch & COLORMAPMASK) == 0) {
	    error("no global colormap.");
	}
	num_colors = 1 << ((ch & 0x7) + 1);

	// supposed to be NULL
	if (buf[12] != 0) {
	    error("corrupt GIF file (nonull).");
	}

	// Read colors
	if (!readBytes(in, buf, 0, num_colors * 3)) {
	    throw new IOException();
	}

	// Allocate color map
	red = new byte[num_colors];
	green = new byte[num_colors];
	blue = new byte[num_colors];

	// Copy colors
	for (int i = 0 ; i < num_colors ; i++) {
	    red[i]   = buf[i*3 + 0];
	    green[i] = buf[i*3 + 1];
	    blue[i]  = buf[i*3 + 2];
	}

        // nothing transparent yet.
	trans_index = num_colors + 1;
    }

    private void readImage(InputStream in) {
	int tm = 0;

	if (verbose) {
	    tm = System.nowMillis();
	}

	// Allocate the buffer
	byte block[] = new byte[256 + 3];

	// Read the image descriptor
	if (!readBytes(in, block, 0, 10)) {
	    throw new IOException();
	}
	width = (block[4] & 0xFF) | (block[5] << 8);
	height = (block[6] & 0xFF) | (block[7] << 8);
	boolean interlace = (block[8] & INTERLACEMASK) != 0;
	int initCodeSize = block[9] & 0xFF;

	/* observers may want to know the image size */
	/* annoying I have to say setChanged */
	setChanged();
	notifyObservers();

	// allocate the raster data
	byte ras[] = raster = new byte[width * height];

	if (verbose) {
	    System.out.print("Reading a " + width + " by " + height + " " +
		      (interlace ? "" : "non-") + "interlaced image...");
	}

	// Patrick Naughton:
	// Note that I ignore the possible existence of a local color map.
	// I'm told there aren't many files around that use them, and the
	// spec says it's defined for future use.  This could lead to an
	// error reading some files.
     	//
	// Start reading the image data. First we get the intial code size
	// and compute decompressor constant values, based on this code
	// size.
	//
	// The GIF spec has it that the code size is the code size used to
	// compute the above values is the code size given in the file,
	// but the code size used in compression/decompression is the code
	// size given in the file plus one. (thus the ++).

	// Arthur van Hoff:
	// The following narly code reads LZW compressed data blocks and
	// dumps it into the image data. The input stream is broken up into
	// blocks of 1-255 characters, each preceded by a length byte.
	// 3-12 bit codes are read from these blocks. The codes correspond to
	// entry is the hashtable (the prefix, suffix stuff), and the appropriate
	// pixels are written to the image.

	int clearCode = (1 << initCodeSize);
	int eofCode = clearCode + 1;
	int bitMask = num_colors - 1;

	if (verbose) {
	    System.out.print("Decompressing...");
	    System.out.flush();
	}


	// Variables used to form reading data
	boolean blockEnd = false;
	int remain = 0;
	int byteoff = 0;
	int accumbits = 0;
	int accumdata = 0;

	// Variables used to decompress the data
	int codeSize = initCodeSize + 1;
	int maxCode = 1 << codeSize;
	int codeMask = maxCode - 1;
	int freeCode = clearCode + 2;
	int code = 0;
	int oldCode = 0;;
	byte prevChar = 0;

	// Temproray storage for decompression
	short prefix[] = new short[4096];
	byte suffix[] = new byte[4096];
	byte outCode[] = new byte[1025];

	// Variables used for writing pixels
	int x = width;
	int y = 0;
	int off = 0;
	int pass = 0;

	// Read codes until the eofCode is encountered
	while (true) {
	    
	    if (accumbits < codeSize) {
		// fill the buffer if needed
		remain -= 2;
		while (remain < 0 && !blockEnd) {
		    // move remaining bytes to the beginning of the buffer
		    block[0] = block[byteoff];
		    byteoff = 0;

		    // read the next block length
		    int blockLength = in.read();
		    if (blockLength < 0) {
//			throw new IOException();
			return;		// quietly accept truncated GIF images
		    }
		    if (blockLength == 0) {
			blockEnd = true;
		    }

		    // fill the block
		    if (!readBytes(in, block, remain + 2, blockLength)) {
//			throw new IOException();
			return;		// quietly accept truncated GIF images
		    }
		    remain += blockLength;
		}

		// 2 bytes at a time saves checking for accumbits < codeSize.
		// We know we'll get enough and also that we can't overflow
		// since codeSize <= 12.
		accumdata += (block[byteoff++] & 0xff) << accumbits;
		accumbits += 8;
		accumdata += (block[byteoff++] & 0xff) << accumbits;
		accumbits += 8;
	    }

	    // Compute the code
	    code = accumdata & codeMask;
	    accumdata >>= codeSize;
	    accumbits -= codeSize;

	    //
	    // Interpret the code
	    //
	    if (code == clearCode) {
		// Clear code sets everything back to its initial value, then
		// reads the immediately subsequent code as uncompressed data.
		if (verbose) {
		    System.out.print(".");
		    System.out.flush();
		}

		// Note that freeCode is one less than it is supposed to be,
		// this is because it will be incremented next time round the loop
		freeCode = clearCode + 1;
		codeSize = initCodeSize + 1;
		maxCode = 1 << codeSize;
		codeMask = maxCode - 1;

		// Continue if we've NOT reached the end, some Gif images contain bogus
		// codes after the last clear code.
		if (off < raster.length) {
		    continue;
		}

		// pretend we've reached the end of the data
		code = eofCode;
	    }

	    if (code == eofCode) {
		// make sure we read the whole block of pixels.
		if (!blockEnd) {
		    in.read();
		}
		
		if (verbose) {
		    System.out.println("done in " + (System.nowMillis() - tm) + "ms");
		}
		return;
	    } 

	    // It must be data: save code in CurCode
	    int curCode = code;
	    int outCount = outCode.length;

	    // If greater or equal to freeCode, not in the hash table
	    // yet; repeat the last character decoded
	    if (curCode >= freeCode) {
		curCode = oldCode;
		outCode[--outCount] = prevChar;
	    }

	    // Unless this code is raw data, pursue the chain pointed
	    // to by curCode through the hash table to its end; each
	    // code in the chain puts its associated output code on
	    // the output queue.
	    while (curCode > bitMask) {
		outCode[--outCount] = suffix[curCode];
		curCode = prefix[curCode];
	    }

	    // The last code in the chain is treated as raw data.
	    prevChar = (byte)curCode;
	    outCode[--outCount] = prevChar;

	    // Now we put the data out to the Output routine. It's
	    // been stacked LIFO, so deal with it that way...
	    int len = outCode.length - outCount;
	    if (len > 2 && len < x) {
		x -= len;
		System.arraycopy(outCode, outCount, ras, off, len);
		off += len;
	    } else while (--len >= 0) {
		ras[off++] = outCode[outCount++];

		// Update the X-coordinate, and if it overflows, update the
		// Y-coordinate
		if (--x == 0) {
		    // If a non-interlaced picture, just increment y to the next
		    // scan line.  If it's interlaced, deal with the interlace as
		    // described in the GIF spec.  Put the decoded scan line out
		    // to the screen if we haven't gone past the bottom of it
		    x = width;
		    if (interlace) {
			switch (pass) {
			  case 0:
			    y += 8;
			    if (y >= height) {
				pass++;
				y = 4;
			    }
			    break;
			  case 1:
			    y += 8;
			    if (y >= height) {
				pass++;
				y = 2;
			    }
			    break;
			  case 2:
			    y += 4;
			    if (y >= height) {
				pass++;
				y = 1;
			    }
			    break;
			  case 3:
			    y += 2;
			    break;
			}
			off = y * width;
		    }

		    // Some files overrun the end
		    if (off >= ras.length) {
			break;
		    }
		}
	    } 

	    // Build the hash table on-the-fly. No table is stored in
	    // the file.
	    prefix[freeCode] = (short)oldCode;
	    suffix[freeCode] = prevChar;
	    oldCode = code;

	    // Point to the next slot in the table.  If we exceed the
	    // maxCode, increment the code size unless
	    // it's already 12.  If it is, do nothing: the next code
	    // decompressed better be CLEAR
	    if (++freeCode >= maxCode) {
		if (codeSize < 12) {
		    codeSize++;
		    maxCode <<= 1;
		    codeMask = maxCode - 1;
		}
	    }
	}
    }

    /**
     * Testing... 1... 2...
     */
    public static void main(String args[]) {
	GifImage image = new GifImage(args[0]);
    }
}

