/*
 * @(#)Formatter.java	1.29 95/02/23 Jonathan Payne
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
package awt;

import java.util.*;

/**
 * A class that knows how to layout a set of DisplayItems inside a
 * window.
 *
 * @version 1.29 23 Feb 1995
 * @author Jonathan Payne
 */
public class Formatter {
    final boolean   debug = false;

    static Hashtable	fonts = new Hashtable();
    static Hashtable	colors = new Hashtable();

    /** Current formatting parameters. */
    protected FormattingParameters  currentParameters;

    /** Current X coordinate, where next display item will
	be added. */
    protected int		    x;

    /** Current X limit for the right margin.  If a display item
	moves beyond xlimit, we wrap. */
    protected int		    xlimit;

    /** Current Y coordinate.  This is the value of Y for the top
	of the current line we're building. */
    protected int		    y;

    /** Current lineAscent for this line. */
    protected int		    lineAscent;

    /** Current lineDescent for this line. */
    protected int		    lineDescent;

    /** Current maximum font ascent. */
    protected int		    fontAscent;

    /** Current font as determined by the current FormattingParameters. */
    protected Font		    font;

    /** Current color as determined by the current FormattingParameters. */
    protected Color		    color;

    /** The display item index of the first item on this line.  This is
	used to go back to position all the items we accumulate on the
	line we're laying out right now. */
    protected int		    lineStartIndex;

    /** Total number of items we think we've added to the window. */
    protected int		    itemCount;

    /** This is the stack of formatting parameters. */
    protected Stack		    fpStack = new Stack();

    /** This is true when we're in a section that doesn't render. */
    protected int		    notRendering = 0;

    /** This is used to combine requests to break a paragraph.  This
	is how much of a break we have accumulated so far since we last
	wrapped.  If a request to break is greater than this number, we
	bump the new Y position by that much further. */
    protected int		    breakSoFar;

    /** This is the window we're laying objects out in. */
    public TextWindow	    win;

    private int	heightChunk;

    public Formatter(TextWindow w) {
	win = w;
    }

    private Font newFont() {
	Font	    font;
	String	    fontName;

	fontName = currentParameters.buildFontName();
	if ((font = (Font) fonts.get(fontName)) == null) {
	    font = win.wServer.fonts.getFont(fontName);
	    if (debug) {
		System.out.println("New font: " + font);
	    }
	    fonts.put(fontName, font);
	}
	return font;
    }

    private Color newColor() {
	Integer colorID = new Integer((currentParameters.r << 16) +
				      (currentParameters.g << 8) +
				      currentParameters.b);
	Color	color;

	if ((color = (Color) colors.get(colorID)) == null) {
	    color = new Color(win.wServer,
			      currentParameters.r,
			      currentParameters.g,
			      currentParameters.b);
	    colors.put(colorID, color);
	}
	return color;
    }

    public void setParameters(FormattingParameters fp) {
	if ((currentParameters = fp) != null) {
	    font = newFont();
	    color = newColor();
	    if (atBeginningOfLine()) {
		x = currentParameters.leftMargin;
	    }
	    xlimit = win.width - currentParameters.rightMargin;
	}
    }

    protected void finishCurrentLine() {
	int limit = itemCount;
	int offset;

	switch (currentParameters.alignment) {
	case FormattingParameters.ALIGN_LEFT:
	default:
	    offset = 0;
	    break;

	case FormattingParameters.ALIGN_RIGHT:
	    offset = xlimit - x;
	    break;

	case FormattingParameters.ALIGN_CENTER:
	    offset = (xlimit - x) / 2;
	    break;
	}

	while (lineStartIndex < limit) {
	    DisplayItem	di = win.nthItem(lineStartIndex++);

	    adjustItem(di, di.x + offset);
	}

	int height = lineAscent + lineDescent;

	if (height > 0) {
	    y += height + 1;
	} else if (font != null) {
	    y += font.height + 1;
	}
	((TextWindow) win).startNewLine(itemCount);
	win.logicalHeight = y;

	int hChunk = y >> 10;
	if (hChunk > heightChunk) {
	    win.updateScrollbar();
	    heightChunk = hChunk;
	}
    }

    protected void adjustItem(DisplayItem di, int x) {
	di.move(x, di.y);
	if (di instanceof TextDisplayItem) {
	    ((TextDisplayItem) di).moveBaseline(y + lineAscent);
	}
    }

    protected abstract int getLength();
    protected abstract int charAt(int i);
    protected abstract TextDisplayItem makeTextItem(int pos0, int pos1);

    protected boolean outputString(int pos0, int pos1, int width) {
	if (notRendering > 0) {
	    return true;
	}

	TextDisplayItem	t = makeTextItem(pos0, pos1);
	int ascent = font.ascent;
	int descent = font.descent;

	t.setup(font, color, x, y, width, font.height);
	if (ascent > lineAscent) {
	    lineAscent = ascent;
	}
	if (ascent > fontAscent) {
	    fontAscent = ascent;
	}
	if (descent > lineDescent) {
	    lineDescent = descent;
	}
	t.valid = true;
	return addDisplayItem(t, false);
    }

    /* Process all the style refs at position pos, and then return
       the next text position that we have to worry about style refs.
       By default, assume no style refs. */

    protected int processStyleRefs(int pos) {
	return getLength();
    }

    protected void setBreakSoFar(int value) {
//	System.out.println("breakSoFar: " + breakSoFar + " => " + value);
	breakSoFar = value;
    }

    protected void reset() {
	fpStack.setSize(0);
	heightChunk = -1;
	lineStartIndex = itemCount = win.count();
	y = x = 0;
	lineAscent = fontAscent = lineDescent = 0;

	/* setting this high causes all leading breaks to be ignored. */
	setBreakSoFar(10000);
    }

    public void layout() {
	reset();
	win.startNewLine(0);

	pushParameters(new FormattingParameters(currentParameters));

	int	lastSpaceX;
	int	lastSpace;
	int	x = this.x;
	int	xlimit = this.xlimit;
	int	posLimit = getLength();
	int	pos = 0;
	int	pos0 = 0;
	int	stylePos = 0;
	int	wrap = 0;
	int	widths[] = null;
	int	lastWrapPos = -1;

	stylePos = 0;
	lastSpaceX = -1;
	lastSpace = -1;

	try {
	    while (pos < posLimit) {
		if (pos >= stylePos) {
		    if (pos > pos0) {
			outputString(pos0, pos, x - this.x);
			x = this.x;
			pos0 = pos;
		    }
		    this.x = x;
		    stylePos = processStyleRefs(pos);
		    x = this.x;
		    widths = font.widths;
		    wrap = currentParameters.wrapStyle;
		    xlimit = this.xlimit;
		    lastSpace = pos;
		    if (win.formatter != this) {
			System.out.println("Formatter aborting!");
			return;
		    }
		} else if (notRendering > 0) {
		    pos++;
		    continue;
		}
		int	c = charAt(pos);
		int	newx;

		if (c == '\t') {
		    int tabWidth = font.widths[' '] * 8;

		    if (pos > pos0) {
			outputString(pos0, pos, x - this.x);
			pos0 = pos;
			x = this.x;
		    }
		    newx = (x += tabWidth
			    - ((x - currentParameters.leftMargin) % tabWidth));
		    this.x = newx;
		} else {
		    if (c == ' ') {
			lastSpace = pos;
			lastSpaceX = x;
		    }
		    newx = x + widths[c];
		} 
		if ((newx >= xlimit && wrap != FormattingParameters.WRAP_NOT) 
		    || c == '\n') {
		    if (c != '\n' && wrap == FormattingParameters.WRAP_WORD
			&& (lastSpace > pos0
			    || this.x != currentParameters.leftMargin)) {
			pos = lastSpace;
			x = lastSpaceX;
		    }
		    if (pos > pos0) {
			if (!outputString(pos0, pos, x - this.x)) {
			    System.out.println("Formatter aborting!");
			    return;
			}
		    }
		    if (c != '\n') {
			while (pos < posLimit && (c = charAt(pos)) == ' ')
			    pos += 1;
		    }
		    if (c == '\n') {
			pos += 1;
		    }
		    wrapLine();
		    lastSpace = pos0 = pos;
		    x = this.x;
		    if (pos == lastWrapPos) {
			System.out.println("Breaking: infinite loop");
			break;
		    }
		    lastWrapPos = pos;
		    continue;
		}
		x = newx;
		pos += 1;
	    }
	    if (pos > pos0) {
		outputString(pos0, pos, x - this.x);
	    }
	    this.x = x;
	    stylePos = processStyleRefs(pos);
	    breakLine(12);	/* add a newline to the end of the document */
	    popParameters();
	} finally {
	    win.logicalHeight = y;
	}
    }

    public void pushParameters(FormattingParameters p) {
	if (p == null) {
	    p = new FormattingParameters(currentParameters);
	}
	fpStack.push(currentParameters);
	setParameters(p);
    }

    public void popParameters() {
	FormattingParameters	fp;

	fp = (FormattingParameters) fpStack.pop();
	setParameters(fp);
    }

    public FormattingParameters getParameters() {
	return currentParameters;
    }

    public boolean addDisplayItem(DisplayItem di, boolean checkWrap) {
	if (win.formatter != this) {
	    return false;
	}
	win.addItem(di);
	if (checkWrap && x + di.width > xlimit) {
	    wrapLine();
	}
	/* display are aligned to the text baseline by default */
	lineAscent = Math.max(lineAscent, di.height - lineDescent);
	di.move(x, y);
	if (debug) {
	    System.out.println("Adding #" + itemCount + " = " + di);
	}
	x += di.width;
	itemCount += 1;
	setBreakSoFar(-1);
	return true;
    }

    public void setLeftMargin(int lm) {
	currentParameters.leftMargin = (short)lm;
	setParameters(currentParameters);
    }

    public boolean atBeginningOfLine() {
	return (lineStartIndex == itemCount
 		|| x == currentParameters.leftMargin);
    }

    public int getLeftMargin() {
	return currentParameters.leftMargin;
    }

    public int getRightMargin() {
	return win.width - currentParameters.rightMargin;
    }

    public void setXCoordinate(int x) {
	this.x = x;
    }

    public int getXCoordinate() {
	return x;
    }

    public int getYCoordinate() {
	return y;
    }

    public void addCharacterSpacing(char c) {
	int width = font.widths[c];

	if (x + width < xlimit) {
	    x += width;
	}
    }

    public void wrapLine() {
	if (debug) {
	    System.out.println("Wrapping");
	}
	setBreakSoFar(0);
	finishCurrentLine();
	x = currentParameters.leftMargin;
	/* At some point I felt the need to change the line below
	   to the lines that are commented out above.  That caused
	   other problems which I am fixing now.  Hopefully next time
	   I won't have to remember why I did this! */
	lineAscent = fontAscent = lineDescent = 0;
    }

    public void breakLine(int extra) {
	if (breakSoFar == -1) {
	    wrapLine();
	}
	if (extra > breakSoFar) {
	    y += (extra - breakSoFar);
	    setBreakSoFar(extra);
	}
    }

    public Font getFont() {
	return font;
    }

    public Color getColor() {
	return color;
    }

    public void stopRendering() {
	notRendering += 1;
    }

    public void startRendering() {
	notRendering -= 1;
    }
}
