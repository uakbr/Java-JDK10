/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)Text.java	1.14 94/07/23 Feb 1994
 *
 *	Arthur van Hoff, Nov 1993
 *	Arthur van Hoff, March 1994
 */

package edit;

import awt.Font;

/**
 * The text consists of a unicode String (the actual text) an
 * array of styles and an array of rulers. Rulers and Styles
 * specify only a start position. This means that they extend
 * until either the next Ruler or DocStyle, or until the end of
 * the text. A Ruler can only span complete paragraphs.
 *
 * Still to do:
 * - Undo history
 * - MT issues with respect to views
 */

public class Text {
    /** The length of the text, (may be less than the actual data) */
    protected int length;

    /** The character data */
    protected char data[];

    /** The number of rulers */
    int nrulers;

    /** The array of rulers (at least one long) */
    TextRuler ruler[];

    /** The last ruler used */
    int prevruler;

    /** The number of styles */
    int	nstyles;

    /** The array of styles */
    TextStyle style[];

    /** The last style used */
    int prevstyle;

    /** The last line number */
    int linenr;

    /** The last line number position */
    int linenrpos;

    /** The number of views */
    int nviews;

    /** The array of views */
    TextView view[];
    
    /** Locate a text ruler given a character position. */
    int indexRuler(int pos) {
	int i = prevruler;
	while ((i > 0) && (pos < ruler[i].start)) {
	    i--;
	}
	while ((i + 1 < nrulers) && (pos >= ruler[i + 1].start)) {
	    i++;
	}
	return prevruler = i;
    }

    /** Locate a text style given a character position. */
    int indexStyle(int pos) {
	int i = prevstyle;
	while ((i > 0) && (pos < style[i].start)) {
	    i--;
	}
	while ((i + 1 < nstyles) && (pos >= style[i + 1].start)) {
	    i++;
	}
	return prevstyle = i;
    }

    /** Delete a number of rulers. */
    void deleteRulers(int index, int n) {
	nrulers -= n;
	if (index < nrulers) {
	    System.arraycopy(ruler, index + n, ruler, index, nrulers - index);
	}
    }

    /** Delete a number of styles. */
    void deleteStyles(int index, int n) {
//	print("delete styles at " + index + " " + n + "\n");
	nstyles -= n;
	if (index < nstyles) {
	    System.arraycopy(style, index + n, style, index, nstyles - index);
	}
    }

    /** Insert a ruler, given an index into the ruler array */
    void insertRuler(int index, TextRuler r) {
	if (nrulers + 1 > ruler.length) {
	    TextRuler newruler[] = new TextRuler[ruler.length + 32];
	    System.arraycopy(ruler, 0, newruler, 0, nrulers);
	    ruler = newruler;
	}
	System.arraycopy(ruler, index, ruler, index + 1, nrulers - index);
	nrulers += 1;
	ruler[index] = r;
    }

    /** Insert a style given an index into the style array. */
    void insertStyle(int index, TextStyle s) {
//	print("insert style " + s.tostring() + " at " + index + "\n");
	if (nstyles + 1 > style.length) {
	    TextStyle newstyle[] = new TextStyle[style.length + 32];
	    System.arraycopy(style, 0, newstyle, 0, nstyles);
	    style = newstyle;
	}
	System.arraycopy(style, index, style, index + 1, nstyles - index);
	nstyles += 1;
	style[index] = s;
    }

    /** Construct a Text object */
    public Text(char pData[]) {
	data = pData;
	length = data.length;
	nviews = 0;
	view = new TextView[4];

	ruler = new TextRuler[4];
	style = new TextStyle[10];

	linenr = 1;
	linenrpos = 0;

	insertRuler(0, new TextRuler());
	insertStyle(0, new TextStyle("Courier", 14, Font.PLAIN));
    }

    /** Construct a Text object */
    public Text() {
	this("");
    }

    /** Construct a Text object from a string*/
    public Text(String s) {
	this(s.toCharArray());
    }

    /** Get the text data */
    public char getData()[] {
	return data;
    }

    /** Get the text as a string */
    public String toString() {
	return new String(new String(data, 0, length).toCharArray());
    }

    /** Get the text length */
    public int getLength() {
	return length;
    }

    /**
     * Add a view. The view will be notified when the text is changed.
     */
    public synchronized void addView(TextView v) {
	if (nviews >= view.length) {
	    TextView newview[] = new TextView[nviews + 8];
	    System.arraycopy(view, 0, newview, 0, nviews);
	    view = newview;
	}
	view[nviews++] = v;
    }

    /**
     * Remove a view from the view list, the view is no longer notified
     * when the text changes
     */
    public synchronized void removeView(TextView v) {
	int i;
	for (i = 0 ; i < nviews ; i++)
	    if (view[i] == v) {
		System.arraycopy(view, i + 1, view, i, nviews - (i + 1));
		nviews--;
		break;
	    }
    }

    /**
     * Notify the views that some text was inserted
     */
    void notifyInsert(int pos, int len) {
	int i;

       	for (i = 0 ; i < nviews ; i++) {
	    view[i].notifyInsert(pos, len);
	}
    }

    /**
     * Notify the views that some text was deleted
     */
    void notifyDelete(int pos, int len) {
	int i;

       	for (i = 0 ; i < nviews ; i++) {
	    view[i].notifyDelete(pos, len);
	}
    }

    /**
     * Notify the views that some text has changed
     */
    void notifyChange(int pos, int len) {
	int i;

       	for (i = 0 ; i < nviews ; i++) {
	    view[i].notifyChange(pos, len);
	}
    }

    /**
     * Insert a String at a given character position.
     * The styles and rulers are adjusted and all the views
     * are notified of the change.
     */
    public synchronized void insert(int pos, String str) {
	int len = str.length();
	
	if (pos < 0) {
	    pos = 0;
	}
	if (pos > length) {
	    pos = length;
	}

	// Adjust the line number position
	if (pos <= linenrpos) {
	    linenrpos = 0;
	    linenr = 1;
	}
	
	// Make sure it is long enough
	if ((length + len) > data.length) {
	    char newdata[] = new char[length + len + 64];
	    System.arraycopy(data, 0, newdata, 0, length);
	    data = newdata;
	}

	// Insert the String
	System.arraycopy(data, pos, data, pos + len, length - pos);
	str.getChars(0, len, data, pos);
	length += len;

	// Update rulers
	int i;
	for (i = Math.max(1, indexRuler(pos)) ; i < nrulers ; i++) {
	    if (ruler[i].start > pos) {
		ruler[i].start += len;
	    }
	}

	// Update Styles
	for (i = Math.max(1, indexStyle(pos)) ; i < nstyles ; i++) {
	    if (style[i].start >= pos) {
		style[i].start += len;
	    }
	}

	notifyInsert(pos, len);
    }

    /**
     * Insert a character at a given position
     */
    public synchronized void insert(int pos, char c) {
	char str[] = new char[1];
	str[0] = c;
	insert(pos, new String(str));
    }

    /**
     * Append a String to the end of the text. Note that the
     * String will have the same style and ruler as the last
     * character of the text.
     */
    public synchronized void append(String str) {
	insert(length, str);
    }

    /**
     * Delete a part of the text. The styles and rulers are
     * automatically updated and the views are notified.
     */
    public synchronized void delete(int pos, int len) {
	if (pos < 0) {
	    len += pos;
	    pos = 0;
	}
	if (pos + len > length) {
	    len = length - pos;
	}
	if (len <= 0) {
	    return;
	}

	// Adjust the line number position
	if (pos <= linenrpos) {
	    linenrpos = 0;
	    linenr = 1;
	}

	System.arraycopy(data, pos + len, data, pos, length - (pos + len));
	length -= len;

	//
	// Update styles
	//
	int istart = indexStyle(pos - 1);
	int iend = indexStyle(pos + len);
	int i;

	if (iend > istart + 1) {
	    deleteStyles(istart + 1, iend - (istart + 1));
	    iend = istart + 1;
	}

	if ((iend > istart) && ((pos == length) || style[istart].equal(style[iend]))) {
	    deleteStyles(iend--, 1);
	}

	if (iend < nstyles) {
	    i = iend;
	    if (style[i].start < pos) {
		i++;
	    } else if (style[i].start <= pos + len) {
		style[i++].start = pos;
	    }
	    for(; i < nstyles ; i++) {
		style[i].start -= len;
	    }
	}

	//
	// Update rulers
	//
	istart = indexRuler(pos - 1);
	iend = indexRuler(pos + len);

	if (iend > istart + 1) {
	    deleteRulers(istart + 1, iend - (istart + 1));
	    iend = istart + 1;
	}

	if (iend < nrulers) {
	    i = iend;
	    if (ruler[i].start < pos) {
		i++;
	    } else if (ruler[i].start <= pos + len) {
		ruler[i++].start = pos;
	    }
	    for(; i < nrulers ; i++) {
		ruler[i].start -= len;
	    }
	}

	if ((iend > istart) && (pos == length)) {
	    deleteRulers(iend--, 1);
	}

	i = Math.max(0, pos - 1);

	if (iend > istart) {
	    while (i < length) {
		if (data[i++] == '\n') {
		    break;
		}
	    }
	    
	    if (i > pos) {
		if (i >= length) {
		    deleteRulers(iend, 1);
		} else if ((iend + 1 < nrulers) && (i >= ruler[iend + 1].start)) {
		    deleteRulers(iend, 1);
		} else {
		    ruler[iend].start = i;
		}
	    }
	}

	notifyDelete(pos, len);
    }

    /**
     * Change the style of some text. The style is array is
     * updated and the views are notified.
     */
    public synchronized void setStyle(TextStyle st, int pos, int len) {
	if (pos < 0) {
	    len += pos;
	    pos = 0;
	}
	if ((len <= 0) || (pos >= length)) {
	    return;
	}
	
	int istart = indexStyle(pos);
	int iend = indexStyle(pos + len);

	st.start = pos;

	if (istart == iend) {
	    if (style[istart].equal(st)) {
		return;
	    }
	    iend++;
	    insertStyle(iend, (TextStyle)style[istart].clone());
	}

	style[iend].start = pos + len;
	if (pos + len >= length) {
	    deleteStyles(iend--, 1);
	}

	if (iend > istart + 1) {
	    deleteStyles(istart + 1, iend - (istart + 1));
	}

	if (style[istart].start == pos) {
	    style[istart] = (TextStyle)st.clone();
	} else {
	    istart++;
	    insertStyle(istart, (TextStyle)st.clone());
	}

	if ((istart > 0) && style[istart].equal(style[istart - 1])) {
	    deleteStyles(istart--, 1);
	}

	if ((istart < nstyles - 1) && style[istart].equal(style[istart + 1])) {
	    deleteStyles(istart + 1, 1);
	}

	notifyChange(pos, len);
    }
    
    /**
     * Change the ruler of some text. The actual range of the
     * affected text is adjusted to cover a complete paragraph.
     * The views are notified of the change.
     */
    public synchronized void setRuler(TextRuler r, int pos, int len) {
	if (pos < 0) {
	    len += pos;
	    pos = 0;
	}

	//
	// Make sure the selection encloses a complete line
	//
	while ((pos > 0) && (data[pos - 1] != '\n')) {
	    pos--;
	    len++;
	}

	int i = pos + len;
	while ((i < length) && (data[i - 1] != '\n')) {
	    i++;
	}
	len = i - pos;;

	//
	// Now set the ruler
	//
	int istart = indexRuler(pos);
	int iend = indexRuler(pos + len);

	r.start = pos;

	if (istart == iend) {
	    iend++;
	    insertRuler(iend, (TextRuler)ruler[istart].clone());
	}

	if (iend > istart + 1) {
	    deleteRulers(istart + 1, iend - (istart + 1));
	}

	ruler[iend].start = pos + len;
	if (pos + len >= length) {
	    deleteRulers(iend--, 1);
	}

	if (ruler[istart].start == pos) {
	    ruler[istart] = r;
	} else {
	    istart++;
	    insertRuler(istart, r);
	}

	notifyChange(pos, len);
    }

    /** Return the style at the given position */
    public TextStyle getStyle(int pos) {
	return (TextStyle)style[indexStyle(pos)].clone();
    }

    /** Return the ruler at the given position */
    public TextRuler getRuler(int pos) {
	return (TextRuler)ruler[indexRuler(pos)].clone();
    }

    /**
     * Locate the start of a line of text
     */
    public synchronized int lineToOffset(int lnnr) {
	int i = Math.min(length, linenrpos);
	int n = linenr;

	while ((n < lnnr) && (i < length)) {
	    if (data[i++] == '\n') {
		n++;
	    }
	}

	while ((i > 0) && (n > lnnr)) {
	    if (data[--i] == '\n') {
		n--;
	    }
	}

	while ((i > 0) && (data[i - 1] != '\n')) {
	    i--;
	}

	linenr = n;
	return linenrpos = i;
    }

    /**
     * Locate the start of a line of text
     */
    public synchronized int offsetToLine(int pos) {
	int i = Math.min(length, linenrpos);
	int n = linenr;
	
	pos = Math.max(0,Math.min(pos, length));

	while (i < pos) {
	    if (data[i++] == '\n') {
		n++;
	    }
	}

	while (i > pos) {
	    if (data[--i] == '\n') {
		n--;
	    }
	}

	linenrpos = pos;
	return linenr = n;
    }

    /**
     * Count the lines in the text
     */
    public int countLines() {
	int n = offsetToLine(length);
	return ((length > 0) && (data[length - 1] == '\n')) ? n - 1 : n;
    }

}
