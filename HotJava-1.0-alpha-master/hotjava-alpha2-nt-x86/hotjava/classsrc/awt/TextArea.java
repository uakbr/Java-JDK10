/*
 * @(#)TextArea.java	1.10 95/02/03 Sami Shaio
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

/**
 * A TextArea object is a multi-line area that displays text. It can
 * be made editable or read-only.
 *
 * @version 1.10 03 Feb 1995
 * @author Sami Shaio
 */
public class TextArea extends Component {
    private	WServer	wServer;
    private boolean		hFill = false;
    private boolean		vFill = false;
    boolean editable;

    /**
     * Constructs a TextArea.
     * @param p is the parent Window
     * @param pName is the name of the TextArea. Some layouts such as
     * 		    BorderLayout, use the name for layout.
     * @param f is the font to use. It can be null.
     * @param columns is the number of columns in terms of the current font.
     * @param rows is the number of rows in terms of the current font.
     */
    public TextArea(Container p, String pName, Font f, int columns, int rows) {
	super(p,pName);
	Window win = Window.getWindow(p);
	wServer = win.wServer;
	wServer.textAreaCreate(this, win, f, columns, rows);
	editable = true;
    }

    /**
     * Sets whether this TextArea is editable or not.
     */
    public void setEditable(boolean t) {
	wServer.textAreaSetEditable(this, t);
	editable = t;
    }

    /**
     * Return the cursor position.
     * @return the cursor position.
     */
    public int cursorPos() {
	return wServer.textAreaCursorPos(this);
    }

    /**
     * Sets the cursor position and makes that position visible.
     * @param pos is the position to set the cursor to.
     */
    public void setCursorPos(int pos) {
	wServer.textAreaSetCursorPos(this, pos);
    }

    /**
     * Return the last position in the TextArea.
     * @return the last position in the TextArea.
     */
    public int endPos() {
	return wServer.textAreaEndPos(this);
    }

    /**
     * @return whether this TextArea is editable or not.
     */
    public boolean isEditable() {
	return editable;
    }

    /**
     * Sets the text of this TextArea to the specified value.
     * @param text the text to set as the contents.
     */
    public void setText(String text) {
	wServer.textAreaSetText(this, text);
    }

    /**
     * Return the contents of this TextArea.
     * @return contents of the TextArea.
     */
    public String getText() {
	return wServer.textAreaGetText(this);
    }

    /**
     * Inserts text at the given position.
     * @param text the text to insert.
     * @param pos the position to insert at.
     */
    public void insertText(String text, int pos) {
	wServer.textAreaInsertText(this, text, pos);
    }

    /**
     * Replaces the text from start to end.
     * @param text the text to use as the replacement.
     * @param start the start position.
     * @param end the end position.
     */
    public void replaceText(String text, int start, int end) {
	wServer.textAreaReplaceText(this, text, start, end);
    }
    

    /** Sets whether or not this List stretches horizontally. */
    public void setHFill(boolean t) {
	hFill = t;
    }

    /** Sets whether or not this List stretches vertically. */
    public void setVFill(boolean t) {
	vFill = t;
    }

    /**
     * Disposes of this TextArea. It cannot be used after being disposed.
     */
    public void dispose() {
	wServer.textAreaDispose(this);
    }

    public Dimension getPreferredSize() {
	return new Dimension((hFill) ? parent.width : width,
			     (vFill) ? parent.height : height);
    }

    /**
     * Moves this TextArea to the given position.
     * @param X the x position to move to.
     * @param Y the y position to move to.
     */
    public void move(int X, int Y) {
	super.move(X,Y);
	wServer.textAreaMoveTo(this, X, Y);
    }

    /**
     * Reshapes this TextArea.
     * @param x the x position.
     * @param y the y position.
     * @param w the width in pixels.
     * @param h the height in pixels.
     */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.textAreaReshape(this, x, y, w, h);
    }

    /**
     * Makes this TextArea visible.
     */
    public void map() {
	wServer.textAreaShow(this);
	mapped = true;
    }

    /**
     * Hides this TextArea.
     */
    public void unMap() {
	wServer.textAreaHide(this);
	mapped = false;
    }
}
