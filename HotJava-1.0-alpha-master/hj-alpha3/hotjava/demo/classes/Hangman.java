/*
 * @(#)Hangman.java	Patrick Chan
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

import awt.*;
import java.io.*;
import net.www.html.*;
import browser.*;
import browser.audio.*;

/**
 * @author 	Patrick Chan
 * @version 	1.0
 */
class Hangman extends Applet implements Runnable {
    /* This is the maximum number of incorrect guesses. */
    final int maxTries = 5;

    /* This is the maximum length of a secret word. */
    final int maxWordLen = 20;

    /* This buffer holds the letters in the secret word. */
    char secretWord[];

    /* This is the length of the secret word. */
    int secretWordLen;

    /* This buffer holds the letters which the user typed
       but don't appear in the secret word. */
    char wrongLetters[];

    /* This is the current number of incorrect guesses. */
    int wrongLettersCount;

    /* This buffer holds letters that the user has successfully
       guessed. */
    char word[];

    /* Number of correct letters in 'word'. */
    int wordLen;

    /* This is the font used to paint correctly guessed letters. */
    Font wordFont;

    /* This is the sequence of images for Duke hanging on the gallows. */
    Image hangImages[];

    // Dancing Duke related variables

    /* This thread makes Duke dance. */
    Thread danceThread;

    /* These are the images that make up the dance animation. */
    Image danceImages[];

    /* This variable holds the number of valid images in danceImages. */
    int danceImagesLen = 0;

    /* These offsets refer to the dance images.  The dance images
       are not of the same size so we need to add these offset 
       in order to make the images "line" up. */
    private int danceImageOffsets[] = { 8, 0, 0, 8, 18, 21, 27 };

    /* This represents the sequence to display the dance images
       in order to make Duke "dance".  */
    private int danceSequence[] = { 3, 4, 5, 6, 6, 5, 6, 6, 5, 4, 3, 
            2, 1, 0, 0, 1, 2, 2, 1, 0, 0, 1, 2 };

    /* This is the current sequence number.  -1 implies
       that Duke hasn't begun to dance. */
    int danceSequenceNum = -1;

    /* This is the maximum width and height of all the dance images. */
    int danceHeight = 0;

    /* This variable is used to adjust Duke's x-position while
       he's dancing. */
    int danceX = 0;

    /* This variable specifies the currently x-direction of
       Duke's dance.  1=>right and -1=>left. */
    int danceDirection = 1;

    /* This is the stream for the dance music. */
    InputStream danceMusic;

    /**
     * Initialize the applet. Resize and load images.
     */
    public void init() {
        int i;

        // load in dance animation
	danceMusic = getContinuousAudioStream(
	    new URL(appletURL, "audio/dance.au"));
	danceImages = new Image[40];

	for (i = 1; i < 8; i++) {
	    Image im = getImage("images/dancing-duke/T" + i + ".gif");

	    if (im == null) {
		break;
	    }
	    danceHeight = Math.max(danceHeight, im.height);
	    danceImages[danceImagesLen++] = im;
        }

        // load in hangman image sequnce
        hangImages = new Image[maxTries];
        for (i=0; i<maxTries; i++) {
	    hangImages[i] = getImage("images/hanging-duke/h"+(i+1)+".gif");
        }

        // initialize the word buffers.
        wrongLettersCount = 0;
        wrongLetters = new char[maxTries];

        secretWordLen = 0;
        secretWord = new char[maxWordLen];

        word = new char[maxWordLen];
        
        wordFont = getFont("Courier", Font.BOLD, 24);

	resize((maxWordLen+1) * wordFont.widths['M'] + maxWordLen * 3,
            hangImages[0].height * 2 + wordFont.height);
    }

    /**
     * Paint the screen.
     */
    public void paint(Graphics g) {
        int imageW = hangImages[0].width;
        int imageH = hangImages[0].height;
        int baseH = 10;
        int baseW = 30;
        Font font;
        int i, x, y;

        // draw gallows pole
        g.drawLine(baseW/2, 0, baseW/2, 2*imageH - baseH/2);
        g.drawLine(baseW/2, 0, baseW+imageW/2, 0);

        // draw gallows rope
        g.drawLine(baseW+imageW/2, 0, baseW+imageW/2, imageH/3);

        // draw gallows base
        g.fillRect(0, 2*imageH-baseH, baseW, baseH);


        // draw list of wrong letters
        font = getFont("Courier", Font.PLAIN, 15);
        x = imageW + baseW;
        y = font.height;
	g.setFont(font);
	g.setForeground(Color.red);
        for (i=0; i<wrongLettersCount; i++) {
            g.drawChars(wrongLetters, i, 1, x, y);
            x += font.widths[wrongLetters[i]] + font.widths[' '];
        }

        if (secretWordLen > 0) {
	    // draw underlines for secret word
	    int Mwidth = wordFont.widths['M'];
	    int Mheight = wordFont.height;
	    g.setFont(wordFont);
	    g.setForeground(Color.black);
	    x = 0;
	    y = height - 1;
	    for (i=0; i<secretWordLen; i++) {
		g.drawLine(x, y, x + Mwidth, y);
		x += Mwidth + 3;
	    }

	    // draw known letters in secret word
	    x = 0;
	    y = height - 3;
            g.setForeground(Color.blue);
	    for (i=0; i<secretWordLen; i++) {
		if (word[i] != 0) {
		    g.drawChars(word, i, 1, x, y);
		}
		x += Mwidth + 3;
	    }

            if (wordLen < secretWordLen && wrongLettersCount > 0) {
		// draw Duke on gallows
		g.drawImage(hangImages[wrongLettersCount-1], 
                    baseW, imageH/3);
	    }
        }

    }

    public void update(Graphics g) {
	if (wordLen == 0) {
	    g.clearRect(0, 0, width, height);
            paint(g);
	} else if (wordLen == secretWordLen) {
            if (danceSequenceNum < 0) {
		g.clearRect(0, 0, width, height);
		paint(g);
		danceSequenceNum = 0;
	    }
            updateDancingDuke(g);
        } else {
            paint(g);
        }
    }

    void updateDancingDuke(Graphics g) {
        int baseW = 30;
        int imageH = hangImages[0].height;
	int danceImageNum = danceSequence[danceSequenceNum];

	// first, clear Duke's current image
	g.clearRect(danceX+baseW, imageH*2 - danceHeight, 
            danceImageOffsets[danceImageNum]+danceImages[danceImageNum].width, 
            danceHeight);

        // update dance position
	danceX += danceDirection;
	if (danceX < 0) {
	    danceX = danceDirection = (int)Math.floor(Math.random() * 12) + 5;
	} else if (danceX + baseW > width / 2) {
	    //danceDirection = -(int)Math.floor(Math.random() * 12) - 5;
	    danceDirection *= -1;
	} else if (Math.random() > .9) {
	    danceDirection *= -1;
	}

        // update dance sequence
	danceSequenceNum++;
	if (danceSequenceNum >= danceSequence.length) {
	    danceSequenceNum = 0;
        }

	// now paint Duke's new image
	danceImageNum = danceSequence[danceSequenceNum];
	if ((danceImageNum < danceImagesLen) && (danceImages[danceImageNum] != null)) {
	    g.drawImage(danceImages[danceImageNum], 
		danceX+baseW+danceImageOffsets[danceImageNum], 
		imageH*2 - danceHeight);
	}
    }

    public void keyDown(int key) {
        int i;
        boolean found = false;

        // start new game if user has already won or lost.
        if (secretWordLen == wordLen || wrongLettersCount == maxTries) {
            newGame();
            return;
        }

        // check if valid letter
        if (key < 'a' || key > 'z') {
	    play("audio/beep.au");
            return;    
        }
        // check if already in secret word
        for (i=0; i<secretWordLen; i++) {
            if (key == word[i]) {
                found = true;
		play("audio/ding.au");
                return;
            }
        }
        // check if already in wrongLetters
        if (!found) {
	    for (i=0; i<maxTries; i++) {
		if (key == wrongLetters[i]) {
		    found = true;
		    play("audio/ding.au");
		    return;
		}
            }
        }
        // is letter in secret word? If so, add it.
        if (!found) {
            for (i=0; i<secretWordLen; i++) {
                if (key == secretWord[i]) {
                    word[i] = (char)key;
                    wordLen++;
                    found = true;
                }
            }
            if (found) {
                if (wordLen == secretWordLen) {
		    play("audio/whoopy.au");
                    startDukeDancing();
		} else {
		    play("audio/ah.au");
                }
            }
        }
        // wrong letter; add to wrongLetters
        if (!found) {
	    if (wrongLettersCount < wrongLetters.length) {
		wrongLetters[wrongLettersCount++] = (char)key;
                if (wrongLettersCount < maxTries) {
		    play("audio/ooh.au");
                } else {
                    // show the answer
                    for (i=0; i<secretWordLen; i++) {
                        word[i] = secretWord[i];
                    }
		    play("audio/scream.au");
                }
            }
        }
        if (wordLen == secretWordLen) {
            danceSequenceNum = -1;
        }
        repaint();
    }

    /**
     * Grab the focus and restart the game.
     */
    public void mouseDown(int x, int y) {
        int i;

        // grab focus to get keyDown events
        getFocus();

        if (secretWordLen > 0 && 
           (secretWordLen == wordLen || wrongLettersCount == maxTries)) {
	    newGame();
        } else {
	    play("audio/beep.au");
        }
    }

    /**
     * Starts a new game.  Chooses a new secret word
     * and clears all the buffers
     */
    public void newGame() {
        int i;

        // stop animation thread.
        danceThread = null;

        // pick secret word
        String s = wordlist[(int)Math.floor(Math.random() * wordlist.length)];
        
        secretWordLen = Math.min(s.length(), maxWordLen);
        for (i=0; i<secretWordLen; i++) {
            secretWord[i] = s.charAt(i);
        }

        // clear word buffers
        for (i=0; i<maxWordLen; i++) {
            word[i] = 0;
        }
        wordLen = 0;
        for (i=0; i<maxTries; i++) {
            wrongLetters[i] = 0;
        }
        wrongLettersCount = 0;

        repaint();
    }

    /**
     * Start the applet.
     */
    public void start() {
	getFocus();
        // Start a new game only if user has won or lost; otherwise
        // retain the same game.
        if (secretWordLen == wordLen || wrongLettersCount == maxTries) {
            newGame();
        }
    }

    /**
     * Stop the applet.  Stop the danceThread.
     */
    public void stop() {
	danceThread = null;
    }

    /**
     * Run Duke's dancing animation. This methods is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        // start the dancing music.
	startPlaying(danceMusic);

        // increment the sequence count and invoke the paint method.
	while (width > 0 && height > 0 && danceThread != null) {
	    repaint();
	    Thread.sleep(100);
	}

        // The dance is done so stop the music.
	stopPlaying(danceMusic);
    }

    /**
     * Starts Duke's dancing animation.
     */
    private void startDukeDancing () {
	if (danceThread == null) {
	    danceThread = new Thread(this);
	    danceThread.start();
	}
    }

    /* This is the hangman's limited word list. */
    String wordlist[] = {
        "abstraction",
        "ambiguous",
        "arithmetic",
        "backslash",
        "bitmap",
        "circumstance",
        "combination",
        "consequently",
        "consortium",
        "decrementing",
        "dependency",
        "disambiguate",
        "dynamic",
        "encapsulation",
        "equivalent",
        "expression",
        "facilitate",
        "fragment",
        "hexadecimal",
        "implementation",
        "indistinguishable",
        "inheritance",
        "internet",
        "java",
        "localization",
        "microprocessor",
        "navigation",
        "optimization",
        "parameter",
        "pickle",
        "polymorphic",
        "rigorously",
        "simultaneously",
        "specification",
        "structure",
        "lexical",
        "likewise",
        "management",
        "manipulate",
        "mathematics",
        "hotjava",
        "vertex",
        "unsigned",
        "traditional"};
}
