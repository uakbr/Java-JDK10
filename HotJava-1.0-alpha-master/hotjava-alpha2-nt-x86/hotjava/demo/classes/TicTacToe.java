/*
 * @(#)TicTacToe.java	1.9 95/03/14 Arthur van Hoff
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
import net.www.html.*;
import browser.*;
import browser.audio.*;

/**
 * A TicTacToe applet. A very simple, and mostly brain-dead
 * implementation of your favorite game! <p>
 *
 * In this game a position is represented by a white and black
 * bitmask. A bit is set if a position is ocupied. There are
 * 9 squares so there are 1<<9 possible positions for each
 * side. An array of 1<<9 booleans is created, it marks
 * all the winning positions.
 *
 * @author 	Arthur van Hoff
 * @version 	1.9, 14 Mar 1995
 */
class TicTacToe extends Applet {
    /**
     * White's current position. The computer is white.
     */
    int white;

    /**
     * Black's current position. The user is black.
     */
    int black;

    /**
     * The squares in order of importance...
     */
    final static int moves[] = {4, 0, 2, 6, 8, 1, 3, 5, 7};

    /**
     * The winning positions.
     */
    static boolean won[] = new boolean[1 << 9];
    static final int DONE = (1 << 9) - 1;
    static final int OK = 0;
    static final int WIN = 1;
    static final int LOSE = 2;
    static final int STALEMATE = 3;

    /**
     * Mark all positions with these bits set as winning.
     */
    static void isWon(int pos) {
	for (int i = 0 ; i < DONE ; i++) {
	    if ((i & pos) == pos) {
		won[i] = true;
	    }
	}
    }

    /**
     * Initialize all winning positions.
     */
    static {
	isWon((1 << 0) | (1 << 1) | (1 << 2));
	isWon((1 << 3) | (1 << 4) | (1 << 5));
	isWon((1 << 6) | (1 << 7) | (1 << 8));
	isWon((1 << 0) | (1 << 3) | (1 << 6));
	isWon((1 << 1) | (1 << 4) | (1 << 7));
	isWon((1 << 2) | (1 << 5) | (1 << 8));
	isWon((1 << 0) | (1 << 4) | (1 << 8));
	isWon((1 << 2) | (1 << 4) | (1 << 6));
    }

    /**
     * Compute the best move for white.
     * @return the square to take
     */
    int bestMove(int white, int black) {
	int bestmove = -1;
	
      outer:
	for (int i = 0 ; i < 9 ; i++) {
	    int mw = moves[i];
	    if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
		int pw = white | (1 << mw);
		if (won[pw]) {
		    // white wins, take it!
		    return mw;
		}
		for (int mb = 0 ; mb < 9 ; mb++) {
		    if (((pw & (1 << mb)) == 0) && ((black & (1 << mb)) == 0)) {
			int pb = black | (1 << mb);
			if (won[pb]) {
			    // black wins, take another
			    continue outer;
			}
		    }
		}
		// Neither white nor black can win in one move, this will do.
		if (bestmove == -1) {
		    bestmove = mw;
		}
	    }
	}
	if (bestmove != -1) {
	    return bestmove;
	}

	// No move is totally satisfactory, try the first one that is open
	for (int i = 0 ; i < 9 ; i++) {
	    int mw = moves[i];
	    if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
		return mw;
	    }
	}

	// No more moves
	return -1;
    }

    /**
     * User move.
     * @return true if legal
     */
    boolean yourMove(int m) {
	if ((m < 0) || (m > 8)) {
	    return false;
	}
	if (((black | white) & (1 << m)) != 0) {
	    return false;
	}
	black |= 1 << m;
	return true;
    }

    /**
     * Computer move.
     * @return true if legal
     */
    boolean myMove() {
	if ((black | white) == DONE) {
	    return false;
	}
	int best = bestMove(white, black);
	white |= 1 << best;
	return true;
    }

    /**
     * Figure what the status of the game is.
     */
    int status() {
	if (won[white]) {
	    return WIN;
	}
	if (won[black]) {
	    return LOSE;
	}
	if ((black | white) == DONE) {
	    return STALEMATE;
	}
	return OK;
    }

    /**
     * Who goes first in the next game?
     */
    boolean first = true;

    /**
     * The image for white.
     */
    Image notImage;

    /**
     * The image for black.
     */
    Image crossImage;

    /**
     * Initialize the applet. Resize and load images.
     */
    public void init() {
	resize(120, 120);
	notImage = getImage("images/not.gif");
	crossImage = getImage("images/cross.gif");
    }

    /**
     * Paint it.
     */
    public void paint(Graphics g) {
	int xoff = width / 3;
	int yoff = height / 3;
	g.drawLine(xoff, 0, xoff, height);
	g.drawLine(2*xoff, 0, 2*xoff, height);
	g.drawLine(0, yoff, width, yoff);
	g.drawLine(0, 2*yoff, width, 2*yoff);

	int i = 0;
	for (int r = 0 ; r < 3 ; r++) {
	    for (int c = 0 ; c < 3 ; c++, i++) {
		if ((white & (1 << i)) != 0) {
		    g.drawImage(notImage, c*xoff + 1, r*yoff + 1);
		} else if ((black & (1 << i)) != 0) {
		    g.drawImage(crossImage, c*xoff + 1, r*yoff + 1);
		}
	    }
	}
    }

    /**
     * The user has clicked in the applet. Figure out where
     * and see if a legal move is possible. If it is a legal
     * move, respond with a legal move (if possible).
     */
    public void mouseUp(int x, int y) {
	switch (status()) {
	  case WIN:
	  case LOSE:
	  case STALEMATE:
	    play("audio/return.au");
	    white = black = 0;
	    if (first) {
		white |= 1 << (int)(Math.random() * 9);
	    }
	    first = !first;
	    repaint();
	    return;
	}

	// Figure out the row/colum
	int c = (x * 3) / width;
	int r = (y * 3) / height;
	if (yourMove(c + r * 3)) {
	    repaint();

	    switch (status()) {
	      case WIN:
		play("audio/joy.au");
		break;
	      case LOSE:
		play("audio/joy.au");
		break;
	      case STALEMATE:
		break;
	      default:
		if (myMove()) {
		    repaint();
		    switch (status()) {
		      case WIN:
			play("audio/joy.au");
			break;
		      case LOSE:
			play("audio/joy.au");
			break;
		      case STALEMATE:
			break;
		      default:
			play("audio/ding.au");
		    }
		} else {
		    play("audio/beep.au");
		}
	    }
	} else {
	    play("audio/beep.au");
	}
    }
}
