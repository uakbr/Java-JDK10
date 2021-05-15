/*
 * @(#)AudioData.java	1.10 95/03/20 Arthur van Hoff
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

import java.util.Hashtable;
import net.www.html.URL;
import browser.hotjava;

/**
 * A clip of audio data, contains ulaw 8bit, 8000hz data.
 * This data can be used to construct and AudioDataStream,
 * which can be played. <p>
 * The getAudioData method gets an audio clip out of the cache.
 * @see AudioDataStream
 * @see AudioPlayer
 * @see AudioData#getAudioData
 * @author Arthur van Hoff
 * @version 	1.10, 20 Mar 1995
 */
public
class AudioData {
    /**
     * The data
     */
    byte buffer[];

    /**
     * Constructor
     */
    public AudioData(byte buffer[]) {
	this.buffer = buffer;
    }

    /**
     * Audio data cache.
     */
    private static Hashtable cache = new Hashtable();

    /**
     * Get audio data from the cache.
     */
    public static AudioData getAudioData(URL url) {
	String key = url.toExternalForm();
	AudioData data = (AudioData)cache.get(key);
	if (data == null) {
	    data = new AudioStream(url.openStreamInteractively()).getData();
	    cache.put(key, data);
	}
	return data;
    }

    /**
     * Flush the audio data cache.
     */
    public static void flushCache() {
	cache = new Hashtable();
    }
}
