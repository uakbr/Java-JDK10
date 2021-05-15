/*
 * @(#)AudioStream.java	1.7 95/03/14 Arthur van Hoff
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

package browser.audio;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;

/**
 * Convert an InputStream to an AudioInputStream. It can only
 * read SUN .au files, 8 bit ulaw, 8000hz. one channel.
 * It eats the header information.
 *
 * @see AudioPlayer
 * @author Arthur van Hoff
 * @version 	1.7, 14 Mar 1995
 */
public
class AudioStream extends FilterInputStream {
    private final int MAGIC = 0x2e736e64;
    int length;

    /**
     * Read header, only sun 8 bit, ulaw encoded, single channel,
     * 8000hz is supported
     */
    public AudioStream(InputStream in) {
	super(in);
	DataInputStream data = new DataInputStream(in);
	if (data.readInt() != MAGIC) {
	    in.close();
	    return;
	}
	int hdr_size = data.readInt(); // header size
	if (hdr_size < 12) {
	    // According to file format the header should contain 6 integers:
	    // magic, hdr_size, data_size, encoding, sample_rate, channels
	    // But we only need the first 3, so we only require 12 bytes.
	    // In the future, we should verify or handle the others!
	    in.close();
	    return;
	}
	length = data.readInt();
	in.skip(hdr_size - 12);
    }

    /**
     * A blocking read.
     */
    public int read(byte buf[], int pos, int len) {
	int count = 0;
	while (count < len) {
	    int n = super.read(buf, pos + count, len - count);
	    if (n < 0) {
		return count;
	    }
	    count += n;
	    Thread.currentThread().yield();
	}
	return count;
    }

    /**
     * Get the data.
     */
    public AudioData getData() {
	byte buffer[] = new byte[length];
	int gotbytes = read(buffer, 0, length);
	close();
	if (gotbytes != length) {
	    throw new IOException("audio data read error");
	}
	return new AudioData(buffer);
    }
}
