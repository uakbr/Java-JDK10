/*
 * @(#)TextArea.java	1.17 95/12/14 Sami Shaio
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
package java.awt;

import java.awt.peer.TextAreaPeer;

/**
 * A TextArea object is a multi-line area that displays text. It can
 * be set to allow editing or read-only modes.
 *
 * @version	1.17, 12/14/95
 * @author 	Sami Shaio
 */
public class TextArea extends TextComponent {

    /**
     * The number of rows in the TextArea.
     */
    int	rows;

    /**
     * The number of columns in the TextArea.
     */
    int	cols;

    /**
     * Constructs a new TextArea.
     */
    public TextArea() {
	super("");
    }

    /**
     * Constructs a new TextArea with the specified number of rows and columns.
     * @param rows the number of rows
     * @param cols the number of columns
     */
    public TextArea(int rows, int cols) {
	super("");
	this.rows = rows;
	this.cols = cols;
    }

    /**
     * Constructs a new TextArea with the specified text displayed.
     * @param text the text to be displayed 
     */
    public TextArea(String text) {
	super(text);
    }

    /**
     * Constructs a new TextArea with the specified text and number of rows 
     * and columns.
     * @param text the text to be displayed
     * @param rows the number of rows
     * @param cols the number of cols
     */
    public TextArea(String text, int rows, int cols) {
	super(text);
	this.rows = rows;
	this.cols = cols;
    }

    /**
     * Creates the TextArea's peer.  The peer allows us to modify the appearance of
     * the TextArea without changing any of its functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createTextArea(this);
	super.addNotify();
    }

    /**
     * Inserts the specified text at the specified position.
     * @param str the text to insert.
     * @param pos the position at which to insert.
     * @see TextComponent#setText
     * @see #replaceText
     */
    public void insertText(String str, int pos) {
	TextAreaPeer peer = (TextAreaPeer)this.peer;
	if (peer != null) {
	    peer.insertText(str, pos);
	} else {
	    text = text.substring(0, pos) + str + text.substring(pos);
	}
    }

    /**
     * Appends the given text to the end.
     * @param str the text to insert
     * @see #insertText
     */
    public void appendText(String str) {
	if (peer != null) {
	    insertText(str, getText().length());
	} else {
	    text = text + str;
	}
    }

    /**
     * Replaces text from the indicated start to end position with the
     * new text specified.

     * @param str the text to use as the replacement.
     * @param start the start position.
     * @param end the end position.
     * @see #insertText
     * @see #replaceText
     */
    public void replaceText(String str, int start, int end) {
	TextAreaPeer peer = (TextAreaPeer)this.peer;
	if (peer != null) {
	    peer.replaceText(str, start, end);
	} else {
	    text = text.substring(0, start) + str + text.substring(end);
	}
    }

    /**
     * Returns the number of rows in the TextArea.
     */
    public int getRows() {
	return rows;
    }

    /**
     * Returns the number of columns in the TextArea.
     */
    public int getColumns() {
	return cols;
    }

    /**
     * Returns the specified row and column Dimensions of the TextArea.
     * @param rows the preferred rows amount
     * @param cols the preferred columns amount
     */
    public Dimension preferredSize(int rows, int cols) {
	TextAreaPeer peer = (TextAreaPeer)this.peer;
	return (peer != null) ? 
	    peer.preferredSize(rows, cols) : super.preferredSize();
    }

    /**
     * Returns the preferred size Dimensions of the TextArea.
     */
    public Dimension preferredSize() {
	return ((rows > 0) && (cols > 0)) ? 
	    preferredSize(rows, cols) : super.preferredSize();
    }

    /**
     * Returns the specified minimum size Dimensions of the TextArea.
     * @param rows the minimum row size
     * @param cols the minimum column size
     */
    public Dimension minimumSize(int rows, int cols) {
	TextAreaPeer peer = (TextAreaPeer)this.peer;
	return (peer != null) ? 
	    peer.minimumSize(rows, cols) : super.minimumSize();
    }

    /**
     * Returns the minimum size Dimensions of the TextArea.
     */
    public Dimension minimumSize() {
	return ((rows > 0) && (cols > 0)) ? 
	    minimumSize(rows, cols) : super.minimumSize();
    }

    /**
     * Returns the String of parameters for this TextArea.
     */
    protected String paramString() {
	return super.paramString() + ",rows=" + rows + ",cols=" + cols;
    }
}
