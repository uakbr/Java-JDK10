/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)TextEditor.java	1.5 94/05/24 Feb 1994
 *
 *	Arthur van Hoff, Nov 1993
 *	Arthur van Hoff, March 1994
 */

package edit;

import awt.Frame;
import awt.Window;
import awt.Color;
import awt.Graphics;
import awt.Event;

public class TextEditor extends TextViewer {
    /** The start offset of the selection */
    int selStart;

    /** The end offset of the selection */
    int selEnd;

    /** Temporary place to keep the selection start */
    private int selPos;

    /** The color of the selection */
    Color selColor = new Color(TextStyle.ws, 100, 100, 100);

    /** Create a TextEditor */
    public TextEditor(Window win, String name, boolean scroll) {
	super(win, name, scroll);
	enablePointerMotionEvents();
    }
    public TextEditor(Frame frm, String name, boolean scroll) {
	super(frm, name, scroll);
	enablePointerMotionEvents();
    }

    /** Set the text of the editor */
    public synchronized void setText(Text pText) {
	selStart = selEnd = 0;
	super.setText(pText);
    }

    /** Format a line of text */
    TextViewLine formatLine(int pPos, int pWidth) {
	return new TextEditLine(this, pPos, pWidth);
    }
    
    /** Called by the model to notify this view that
     * some stuff has been inserted */
    public void notifyInsert(int pos, int len) {
	super.notifyInsert(pos, len);

	if (selStart > pos) {
	    selStart += len;
	}
	if (selEnd >= pos) {
	    selEnd += len;
	}
    }
    
    /** Called by the model to notify this view that
     * some stuff has been deleted */
    public void notifyDelete(int pos, int len) {
	super.notifyDelete(pos, len);

	if (selStart > pos) {
	    selStart = Math.max(pos, selStart - len);
	}
	if (selEnd > pos) {
	    selEnd = Math.max(pos, selEnd - len);
	}
    }
    
    /** Called when the selection has changed */
    public void notifySelect(int pos, int len) {
	int i, endPos = pos + len;

//	print("NotifySelect(" + pos + "," + len + ")\n");
	touch();

	// Correct the lines
	for (i = 0 ; i < nlines ; i++) {
	    TextViewLine ln = line[i];
	    if ((pos <= ln.end) && (endPos >= ln.start)) {
		ln.updated = true;
	    }
	}
    }

    /** Paint the selection */
    protected void paintSelect(Graphics g, int x, int y, int w, int h) {
	g.setForeground(selColor);
	g.fillRect(x, y, w, h);
    }

    /** Changing the selection */
    synchronized void setSelection(int startPos, int endPos) {
	if (startPos > endPos) {
	    int tmp = startPos;
	    startPos = endPos;
	    endPos = tmp;
	}
	if (startPos < 0) {
	    startPos = 0;
	}
	if (startPos > text.length) {
	    startPos = text.length;
	}
	if (endPos < startPos) {
	    endPos = startPos;
	}
	if (endPos > text.length) {
	    endPos = text.length;
	}

	if ((endPos <= selStart) || (startPos >= selEnd)) {
	    notifySelect(selStart, selEnd - selStart);
	    notifySelect(startPos, endPos - startPos);
	} else {
	    int start = Math.min(startPos, selStart), end = Math.max(startPos, selStart);
	    if (end > start) {
		notifySelect(start, end - start);
	    }
	    start = Math.min(endPos, selEnd), end = Math.max(endPos, selEnd);
	    if (end > start) {
		notifySelect(start, end - start);
	    }
	}

	selStart = startPos;
	selEnd = endPos;
    }

    /** Character was typed */
    synchronized protected void typeChar(char c) {
	int i;
	
	if (c < ' ') {
	    switch (c) {
	      case ' ':
	      case '\t':
	      case '\n':
		break;
	      case 1:   // Ctrl-A
		for (i = selStart - 1 ; (i > 0) && (text.data[i] != '\n') ; i--);
		if ((i < text.length) && (text.data[i] == '\n')) {
		    i++;
		}
		setSelection(i, i);
		return;
	      case 2:   // Ctrl-B
		setSelection(selStart - 1, selStart - 1);
		return;
	      case 5:   // Ctrl-E
		for (i = selEnd ; (i < text.length) && (text.data[i] != '\n') ; i++);
		setSelection(i, i);
		return;
	      case 6:   // Ctrl-F
		setSelection(selEnd + 1, selEnd + 1);
		return;
	      case 8:	// Delete
		if (selStart == selEnd) {
		    text.delete(selStart - 1, 1);
		} else {
		    text.delete(selStart, selEnd - selStart);
		}
		return;
	      case 14:  // Ctrl-N
		return;
	      case 16:  // Ctrl-P
		return;
	      default:
		System.out.println("char not handled = " + c + "\n");
		return;
	    }
	}
	if (selStart < selEnd) {
	    text.delete(selStart, selEnd - selStart);
	}
	text.insert(selStart, c);
	setSelection(selStart + 1, selStart + 1);
    }

    /** set the ruler of the text, for the current selection */
    public void setRuler(TextRuler r) {
	text.setRuler(r, selStart, selEnd - selStart);
    }

    /** set the style of the text, for the current selection */
    public void setStyle(TextStyle s) {
	text.setStyle(s, selStart, selEnd - selStart);
    }

    /** return the style of the current selection */
    public TextStyle getStyle() {
	return text.getStyle(selStart);
    }

    public void keyPressed(Event evt) {
	typeChar(evt.key);
    }

    public void mouseDown(Event evt) {
	selPos = xyToPos(evt.x, evt.y);
	setSelection(selPos, selPos);
    }
    public void mouseDrag(Event evt) {
	setSelection(selPos, xyToPos(evt.x, evt.y));
    }
    public void mouseUp(Event evt) {
	//System.out.print("Selection is " + selStart + " to " + selEnd + "\n");
    }

    public void clear() {
	text.delete(selStart, selEnd - selStart);
    }

    public void selectAll() {
	setSelection(0, text.length);
    }
}





