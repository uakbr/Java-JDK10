/*
 * @(#)AudioPlayer.java	1.20 95/04/24 Arthur van Hoff
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

import java.util.Vector;
import java.util.Enumeration;
import java.util.Linker;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

/**
 * This classprovides an interface to play multiple
 * channels of 8 bit ulaw encoded, 8000hz, single channel audio.
 * Note that it provides a temporary and very Solaris
 * specific solution, the APIs are likely to change when this
 * is generalize to work on different platforms.<p>
 * To play an audio stream use:
 * <pre>
 *	AudioPlayer.player.start(audiostream);
 * </pre>
 * To stop playing an audio stream use:
 * <pre>
 *	AudioPlayer.player.stop(audiostream);
 * </pre>
 * To play an audio stream from a URL use:
 * <pre>
 *	AudioStream audiostream = new AudioStream(url.openStream());
 *	AudioPlayer.player.start(audiostream);
 * </pre>
 * To play a continuous sound you first have to
 * create an AudioData instance and use it to construct a
 * ContinuousAudioDataStream.
 * For example:
 * <pre>
 *	AudoData data = new AudioStream(url.openStream()).getData();
 *	ContinuousAudioDataStream audiostream = new ContinuousAudioDataStream(data);
 *	AudioPlayer.player.stop(audiostream);
 * </pre>
 *
 * @see AudioData
 * @see AudioDataStream
 * @see AudioStream
 * @see AudioStreamSequence
 * @see ContinuousAudioDataStream
 * @author Arthur van Hoff
 * @version 	1.20, 24 Apr 1995
 */
public
class AudioPlayer extends Thread {
    private static final int MSCLICK = 150;
    private static final int MSMARGIN = MSCLICK / 3;
    private static final int SAMPLE_RATE = 8000;
    private static final int BYTES_PER_SAMPLE = 1;

    private int dev;
    private Vector streams;
    private byte ulaw[];
    private int linear[];

    /**
     * The default audio player. This audio player is initialized
     * automatically.
     */
    public static final AudioPlayer player = new AudioPlayer();

    /*
     * ulaw stuff
     */

    /* define the add-in bias for 16 bit samples */
    private final static int ULAW_BIAS = 0x84;
    private final static int ULAW_CLIP = 32635;

    private final static int ULAW_TAB[] = {
	-32124, -31100, -30076, -29052, -28028, -27004, -25980, -24956,
	-23932, -22908, -21884, -20860, -19836, -18812, -17788, -16764,
	-15996, -15484, -14972, -14460, -13948, -13436, -12924, -12412,
	-11900, -11388, -10876, -10364,  -9852,  -9340,  -8828,  -8316,
	-7932,  -7676,  -7420,  -7164,  -6908,  -6652,  -6396,  -6140,
	-5884,  -5628,  -5372,  -5116,  -4860,  -4604,  -4348,  -4092,
	-3900,  -3772,  -3644,  -3516,  -3388,  -3260,  -3132,  -3004,
	-2876,  -2748,  -2620,  -2492,  -2364,  -2236,  -2108,  -1980,
	-1884,  -1820,  -1756,  -1692,  -1628,  -1564,  -1500,  -1436,
	-1372,  -1308,  -1244,  -1180,  -1116,  -1052,   -988,   -924,
	-876,   -844,   -812,   -780,   -748,   -716,   -684,   -652,
	-620,   -588,   -556,   -524,   -492,   -460,   -428,   -396,
	-372,   -356,   -340,   -324,   -308,   -292,   -276,   -260,
	-244,   -228,   -212,   -196,   -180,   -164,   -148,   -132,
	-120,   -112,   -104,    -96,    -88,    -80,    -72,    -64,
	-56,    -48,    -40,    -32,    -24,    -16,     -8,      0,
	32124,  31100,  30076,  29052,  28028,  27004,  25980,  24956,
	23932,  22908,  21884,  20860,  19836,  18812,  17788,  16764,
	15996,  15484,  14972,  14460,  13948,  13436,  12924,  12412,
	11900,  11388,  10876,  10364,   9852,   9340,   8828,   8316,
	7932,   7676,   7420,   7164,   6908,   6652,   6396,   6140,
	5884,   5628,   5372,   5116,   4860,   4604,   4348,   4092,
	3900,   3772,   3644,   3516,   3388,   3260,   3132,   3004,
	2876,   2748,   2620,   2492,   2364,   2236,   2108,   1980,
	1884,   1820,   1756,   1692,   1628,   1564,   1500,   1436,
	1372,   1308,   1244,   1180,   1116,   1052,    988,    924,
	876,    844,    812,    780,    748,    716,    684,    652,
	620,    588,    556,    524,    492,    460,    428,    396,
	372,    356,    340,    324,    308,    292,    276,    260,
	244,    228,    212,    196,    180,    164,    148,    132,
	120,    112,    104,     96,     88,     80,     72,     64,
        56,     48,     40,     32,     24,     16,      8,      0
    };
    private final static int ULAW_LUT[] = {
	0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3,
	4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
	5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
	5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7
    };

    /**
     * Construct an AudioPlayer.
     */
    private AudioPlayer() {
	super("Audio Player");
        try {
	    Linker.loadLibrary("mmedia");
        } catch (UnsatisfiedLinkException e) {
            System.out.println("could not find/load the mmedia library");
        }
	int bufferSize = ((SAMPLE_RATE * MSCLICK) / 1000) * BYTES_PER_SAMPLE;
	ulaw = new byte[bufferSize];
	linear = new int[bufferSize];
	streams = new Vector();
	setPriority(MAX_PRIORITY);
	setDaemon(true);
	start();
    }

    /**
     * Start playing a stream. The stream will continue to play
     * until the stream runs out of data, or it is stopped.
     * @see AudioPlayer#stop
     */
    public synchronized void start(InputStream in) {
	if (streams != null) {
	    streams.insertElementAt(in, 0);
	    notify();
	} else {
	    in.close();
	}
    }

    /**
     * Stop playing a stream. The stream will stop playing,
     * nothing happens if the stream wasn't playing in the
     * first place.
     * @see AudioPlayer#start
     */
    public synchronized void stop(InputStream in) {
	if (streams != null) {
	    if (streams.removeElement(in)) {
		in.close();
	    }
	}
    }

    private native int audioOpen();
    private native void audioClose();
    private synchronized native void audioWrite(byte buf[], int len);

    /**
     * Open the device (done automatically)
     */
    private synchronized void open() {
	int	ntries = 1;
	int	maxtries = 5;
	while (dev == 0) {
	    dev = audioOpen();
	    if (dev < 0) {
		System.out.println("no audio device");
		return;
	    }
	    if (dev == 0) {
		System.out.println("audio device busy (attempt " + ntries + " out of " + maxtries + ")");
		if ((streams.size() == 0) || (++ntries > maxtries)) {
		    // failed to open the device
		    // close all open streams, wait a while and return
		    closeStreams();
		    return;
		}

		// use wait instead of sleep because this unlocks the
		// current object during the wait.
		wait(3000);
	    }
	}
    }

    /**
     * Close the device (done automatically)
     */
    private synchronized void close() {
	if (dev != 0) {
	    audioClose();
	    dev = 0;
	}
    }

    /**
     * Mix one click of data
     */
    private synchronized void mix() {
	//System.out.println("MIX " + ulaw.length + " buffer");
	int len = ulaw.length;
	byte ubuf[] = ulaw;

	switch (streams.size()) {
	  case 0: {
	    // fill the buffer with silence
	    for (int n = len ; n-- > 0 ;) {
		ubuf[n] = 127;
	    }
	    break;
	  }

	  case 1: {
	    // read from the input stream
	    InputStream in = (InputStream)streams.elementAt(0);
	    int n = in.read(ubuf, 0, len);
	    if (n <= 0) {
		streams.removeElementAt(0);
		//System.out.println("remove " + "0");
		in.close();
		n = 0;
	    } 
	    // fill the rest of the buffer with silence
	    for (; n < len ; n++) {
		ubuf[n] = 127;
	    }
	    break;
	  }

	  default: {
	    int tab[] = ULAW_TAB;
	    int lbuf[] = linear;
	    int i = streams.size() - 1;

	    // fill linear buffer with the first stream
	    InputStream in = (InputStream)streams.elementAt(i);
	    int n = in.read(ubuf, 0, len);
	    if (n > 0) {
		for (int j = 0 ; j < n ; j++) {
		    lbuf[j] = tab[ubuf[j] & 0xFF];
		}
		for (; n < len ; n++) {
		    lbuf[n] = 0;
		}
	    } else {
		streams.removeElementAt(i);
		in.close();
		//System.out.println("remove " + i);
	    }

	    // mix the rest of the streams into the linear buffer
	    while (i-- > 0) {
		in = (InputStream)streams.elementAt(i);
		n = in.read(ubuf, 0, len);
		if (n > 0) {
		    while (n-- > 0) {
			lbuf[n] += tab[ubuf[n] & 0xFF];
		    }
		} else {
		    streams.removeElementAt(i);
		    in.close();
		    //System.out.println("remove " + i);
		}
	    }

	    // convert the linear buffer back to ulaw
	    int lut[] = ULAW_LUT;
	    for (n = len ; n-- > 0 ; ) {
		int sample = lbuf[n];

		/* Get the sample into sign-magnitude. */
		if (sample >= 0) {
		    if (sample > ULAW_CLIP) {
			sample = ULAW_CLIP;	/* clip the magnitude */
		    }
		   
		    /* Convert from 16 bit linear to ulaw. */
		    sample += ULAW_BIAS;
		    int exponent = lut[sample >> 7];
		    int mantissa = (sample >> (exponent + 3)) & 0x0F;
		    sample = ((exponent << 4) | mantissa) ^ 0xFF;
		} else {
		    sample = -sample;
		    if (sample > ULAW_CLIP) {
			sample = ULAW_CLIP;	/* clip the magnitude */
		    }
		    
		    /* Convert from 16 bit linear to ulaw. */
		    sample += ULAW_BIAS;
		    int exponent = lut[sample >> 7];
		    int mantissa = (sample >> (exponent + 3)) & 0x0F;
		    sample = ((exponent << 4) | mantissa) ^ 0x7F;
		}
		ubuf[n] = (byte)sample;
	    }
	  }
	}
    }

    /**
     * Wait for data
     */
    private synchronized void waitForData() {
	close();
	wait();
	open();
    }

    /**
     * Close streams
     */
    private synchronized void closeStreams() {
	// close the streams be garbage collected
	for (Enumeration e = streams.elements() ; e.hasMoreElements() ; ) {
	    ((InputStream)e.nextElement()).close();
	}
	streams = new Vector();
    }

    /**
     * Main mixing loop. This is called automatically when the AudioPlayer
     * is created.
     */
    public void run() {
	//System.out.println("run");
	if (streams.size() == 0) {
	    waitForData();
	}
	open();
	mix();
	int tm = System.nowMillis() - MSMARGIN;

	while (dev >= 0) {
	    //int adjust = System.nowMillis() - tm;
	    //System.out.println("adjust = " + adjust);
	    audioWrite(ulaw, ulaw.length);
	    
	    if (streams.size() == 0) {
		// wait for more data
		waitForData();
		mix();
		tm = System.nowMillis() - MSMARGIN;
	    } else {
		// mix the next bit
		mix();

		// wait for the time out
		tm += MSCLICK;
		int delay = tm - System.nowMillis();
		if (delay > 0) {
		    //System.out.println("delay1=" + delay);
		    sleep(delay);
		} else {
		    // We've lost it, reset the time..
		    //System.out.println("delay2=" + delay);
		    tm = System.nowMillis() - MSMARGIN;
		}
	    }
	}

	// close streams and exit
	closeStreams();
	streams = null;
	System.out.println("audio player exit");
    }
}
