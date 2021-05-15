/*
 * @(#)TextInputStream.java	1.3 95/04/02 Chuck McManis
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

/*-
 * Originally I wanted to subclass BufferedInputStream but the requirement
 * to do lineNumber processing screwed me up eventually. This class tries
 * very hard to keep an accurate track of which line it is on.
 */

package java.io;

/**
 * An input stream for reading text files. This class is useful for reading
 * a text file and breaking it up into Java strings. Each string will contain
 * one line of text, lines with only a line terminator will be represented
 * by zero length Java strings. At any time you can request the line number
 * of the file you are on, but invoking the currentLine() method.
 *
 * <b>NOTE: This class reads ASCII date into Unicode strings, if you wish
 * to read UTF (UNICODE Text Format) strings you should use DataInputStream
 * instead.</b>
 *
 * @version 	1.3, 02 Apr 1995
 * @author	Chuck McManis
 * @see		String
 */
public
class TextInputStream extends FilterInputStream {
    /** The current line number. */
    protected int	lineNumber = 0;

    protected byte inputBuffer[];
    protected int curBufferPos;

    private byte stringBuffer[];
    protected boolean atEOF;


    /**
     * Create a textInputStream with a specific buffer size.
     */
    public TextInputStream(InputStream in, int bufSize) {
	super(in);
	inputBuffer = new byte[bufSize];
	stringBuffer = new byte[1024];
	curBufferPos = bufSize;
	atEOF = false;
    }

    /**
     * Creates a TextInputStream.
     * @param in the input stream
     */
    public TextInputStream(InputStream in) {
	super(in);
	inputBuffer = new byte[8192];
	stringBuffer = new byte[1024];
	curBufferPos = 8192;
	atEOF = false;
    }

    public boolean EOF() {
	return (atEOF);
    }

    /**
     * a private helper function to refill the buffer. The only tricky
     * part here is that if the read cannot completely fill the inputBuffer
     * it 'moves' what was read to the end of the input buffer and points
     * curBufferPos at it. This makes the end of file detection code easier.
     */
    private int fillBuffer() {
	int i, j;

	/* It wasn't a parsed string so refill the buffer. */
	j = super.in.read(inputBuffer);
	if (j == -1) {
	    atEOF = true;
	    return (-1);
	}
	if (j < inputBuffer.length) {
	    for (i = 0; i < j; i++) {
		inputBuffer[(inputBuffer.length - 1) - i] =
						    inputBuffer[(j - 1) - i];
	    }
	    curBufferPos = inputBuffer.length - j;
	} else {
	    curBufferPos = 0;
	}
	return (j);
    }

    /**
     * Read an ISO-Latin1 line of text from the input stream and return a 
     * Java String.
     *
     * The line terminator is stripped from the returned string. This 
     * method recognizes <CR>, <CR><LF>, and <LF> as the end of line 
     * sequence. <b>Note: it treats a <LF><CR> sequence as two lines.</b>
     * While this might be considered a bug, no current operating
     * system uses <LF><CR> to terminate lines in text files.
     *	 @return null on end-of-file.
     */
    public synchronized String readLine() {
	int i, j, stringlen;

	i = 0;
	stringlen =0;
	if (atEOF) {
	    return (null);
	}

	while (true) {
	    while (curBufferPos < inputBuffer.length) {
		if ((inputBuffer[curBufferPos] == '\n') ||
		    (inputBuffer[curBufferPos] == '\r')) {
		    break;
		}

		stringBuffer[stringlen] = inputBuffer[curBufferPos];
		stringlen++;
		curBufferPos++;
		currentReadLimit--;
		if (stringlen == 1024) {
		    throw new Exception(
				"TextInputStream: String Buffer Overflow!");
		}
	    }

	    /* 
	     * At this point we can either be buffer starved or ready
	     * to process a string that has been read in.
	     */
	    if (curBufferPos < inputBuffer.length) {
		byte	tmpChar;

		lineNumber++;

		/*
		 * Case 1:  Character at the current point is a line-feed
		 *          this is Guaranteed to be the end of a line.
		 */
		if (inputBuffer[curBufferPos] == '\n') {
		    curBufferPos++;
		    return (new String(stringBuffer, 0, 0, stringlen));
		}

		/*
		 * Case 2: Character at the current point is a <CR> and
		 *         the following character is in the buffer. Thus
		 *         we can check for <CR> only, and <CR><LF> without
		 * 	   reading any more data.
		 */
		if ((curBufferPos + 1) < inputBuffer.length) {
		    curBufferPos++;
		    if (inputBuffer[curBufferPos] == '\n') {
			curBufferPos++;
		    }
		    return (new String(stringBuffer, 0, 0, stringlen));
		}

		/*
		 * Case 3: We're at the end of the buffer and we've seen
		 *	   a <CR>. We read one byte from the file and
		 * 	   this generates its own sub cases.
		 */
	        tmpChar = (byte) super.in.read();

		/* Case 3a: End of file. Known good string so return it */
		if (tmpChar == -1) {
		    atEOF = true;
		    return (new String(stringBuffer, 0, 0, stringlen));
		}

		/*
		 * Case 3b: Next char was <lf> so we toss it and return
		 * 	    the string.
		 */
		if (tmpChar == '\n') {
		    return (new String(stringBuffer, 0, 0, stringlen));
		}

		/*
		 * Case 3c: It wasn't a <lf> so we stick it at the end
		 *	    of the buffer (will get processed on next
		 *	    read.)
		 */
		inputBuffer[curBufferPos] = tmpChar;
		return (new String(stringBuffer, 0, 0, stringlen));
	    }

	    /* It wasn't a parsed string so refill the buffer. */
	    j = fillBuffer();
	    if (j == -1) {
		if (stringlen != 0) {
		    lineNumber++;
		    return (new String(stringBuffer, 0, 0, stringlen));
		} else {
		    return (null);
		}
	    }
	}
    }

    private int EOLState = 0;

    /**
     * lineStateMachine - is a private function that keeps tracks of lines
     * that have been passed while reading through the read() interfaces.
     */
    void lineStateMachine(byte b) {
	switch (b) {
	    case '\n' : 
		EOLState = 0;
		lineNumber++;
		break;
	    case '\r' :
		EOLState = 1;	// Potential end of line
		break;
	    default:
		if (EOLState == 1) {
		    lineNumber++;
		    EOLState = 0;
		}
		break;
	}
    }

    
    /**
     * read a byte - reads a single byte from the input stream and returns
     * -1 on end of file. Internally the line number is tracked by watching
     * for line terminator characters to go by.
     */
    public synchronized int read() {
	int j;
	if (curBufferPos < inputBuffer.length) {
		lineStateMachine(inputBuffer[curBufferPos]);
		currentReadLimit--;
		return (inputBuffer[curBufferPos++]);
	}
	j = fillBuffer();
	if (j == -1) {
	    return (-1);
	}
	lineStateMachine(inputBuffer[curBufferPos]);
	currentReadLimit--;
	return (inputBuffer[curBufferPos++]);
    }

    /**
     * read into a byte array. This is augmented to manage the line number
     * state.
     */
    public synchronized int read(byte b[]) {
	int j, bytesFilled;

	j = 0;
	bytesFilled = 0;
	if (atEOF) {
	    return (-1);
	}
	while (true) {
	    while (curBufferPos < inputBuffer.length) {
		lineStateMachine(inputBuffer[curBufferPos]);
		b[bytesFilled++] = inputBuffer[curBufferPos++];
		currentReadLimit--;
		if (bytesFilled == b.length) {
		   return (bytesFilled);
		}
	    }
	    j = fillBuffer();
	    if (j == -1) {
		if (bytesFilled > 0) {
		    return (bytesFilled);
		} else {
		    return (-1);
		}
	    }
	}
    }
	    

    /**
     * Returns the current line number.
     */
    public int currentLine() {
	return lineNumber;
    }

    /**
     * Skip some number of bytes of input, note that this actually reads
     * the bytes that are skipped to keep track of the current line number.
     */
    public int skip(int n) {
	int	bytesSkipped = 0, j;

	while (n > 0) {
	    while (curBufferPos < inputBuffer.length) {
		lineStateMachine(inputBuffer[curBufferPos]);
		n--;
		bytesSkipped++;
		currentReadLimit--;
		curBufferPos++;
	    }
	    j = fillBuffer();
	    if (j == -1) {
		return (bytesSkipped);
	    }
	}
	return (bytesSkipped);
    }

    /**
     * Returns the number of bytes that can be read, this is the sum of
     * what is in the buffer and what the stream returns.
     */
    public synchronized int available() {
	return ((inputBuffer.length - curBufferPos) + in.available());
    }

    private int markedLineNumber = 0;
    private int markReadLimit = 0;
    private int markEOLState = 0;
    private int currentReadLimit = 0;

    /**
     * Set a mark on the InputStream, if that stream supports marks. In
     * this class we will keep track of the current line number as well.
     *
     * @see FilterInputStream
     */
    public synchronized void mark(int readlimit) {
	if (super.in.markSupported()) {
	    markedLineNumber = lineNumber;
	    markEOLState = EOLState;
	    markReadLimit = readlimit;
	    currentReadLimit = readlimit;
	    super.in.mark(readlimit);
	}
    }

    /**
     * Reposition the stream to the last marked position, restores the line
     * number to the number that existed when the mark was set.
     *
     * @see FilterInputStream
     */
    public synchronized void reset() {
	/*
	 * Check to see if the call to reset() will in fact reset to
	 * a mark, or if it will just reset to the beginning. If it
	 * will reset to a mark then we adjust the line number accordingly.
	 */
	if ((super.in.markSupported()) && (currentReadLimit > 0)) {
	    lineNumber = markedLineNumber;
	    EOLState = markEOLState;
	    currentReadLimit = markReadLimit;
	} else {
	    lineNumber = 0;
	}
	super.in.reset();
    }
}
