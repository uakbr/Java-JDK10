/*
 * @(#)TextField.java	1.16 95/12/14 Sami Shaio
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
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

import java.awt.peer.TextFieldPeer;

/**
 * TextField is a component that allows the editing of a single line of text.
 *
 * @version	1.16, 12/14/95
 * @author 	Sami Shaio
 */
public class TextField extends TextComponent {

    /**
     * The number of columns in the TextField.
     */
    int cols;

    /**
     * The echo character.
     */
    char echoChar;

    /**
     * Constructs a new TextField.
     */
    public TextField() {
	super("");
    }

    /**
     * Constructs a new TextField initialized with the specified columns.
     * @param cols the number of columns
     */
    public TextField(int cols) {
	super("");
	this.cols = cols;
    }

    /**
     * Constructs a new TextField initialized with the specified text.
     * @param text the text to be displayed
     */
    public TextField(String text) {
	super(text);
    }

    /**
     * Constructs a new TextField initialized with the specified text and columns.
     * @param text the text to be displayed
     * @param cols the number of columns
     */
    public TextField(String text, int cols) {
	super(text);
	this.cols = cols;
    }

    /**
     * Creates the TextField's peer.  The peer allows us to modify the appearance of 
     * the TextField without changing its functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createTextField(this);
	super.addNotify();
    }

    /**
     * Returns the character to be used for echoing.
     * @see #setEchoCharacter
     * @see #echoCharIsSet
     */
    public char getEchoChar() {
	return echoChar;
    }

    /**
     * Returns true if this TextField has a character set for
     * echoing.
     * @see #setEchoCharacter
     * @see #getEchoChar
     */
    public boolean echoCharIsSet() {
	return echoChar != 0;
    }

    /**
     * Returns the number of columns in this TextField.
     */
    public int getColumns() {
	return cols;
    }

    /**
     * Sets the echo character for this TextField. This is useful
     * for fields where the user input shouldn't be echoed to the screen,
     * as in the case of a TextField that represents a password.
     * @param c the echo character for this TextField
     * @see #echoCharIsSet
     * @see #getEchoChar
     */
    public void setEchoCharacter(char c) {
	echoChar = c;
	TextFieldPeer peer = (TextFieldPeer)this.peer;
	if (peer != null) {
	    peer.setEchoCharacter(c);
	}
    }

    /**
     * Returns the preferred size Dimensions needed for this TextField with the 
     * specified amount of columns.
     * @param cols the number of columns in this TextField
     */
    public Dimension preferredSize(int cols) {
	TextFieldPeer peer = (TextFieldPeer)this.peer;
	return (peer != null) ? peer.preferredSize(cols) : super.preferredSize();
    }

    /**
     * Returns the preferred size Dimensions needed for this TextField.
     */
    public Dimension preferredSize() {
	return (cols > 0) ? preferredSize(cols) : super.preferredSize();
    }

    /**
     * Returns the minimum size Dimensions needed for this TextField with the specified 
     * amount of columns.
     * @param cols the number of columns in this TextField
     */
    public Dimension minimumSize(int cols) {
	TextFieldPeer peer = (TextFieldPeer)this.peer;
	return (peer != null) ? peer.minimumSize(cols) : super.minimumSize();
    }

    /**
     * Returns the minimum size Dimensions needed for this TextField.
     */
    public Dimension minimumSize() {
	return (cols > 0) ? minimumSize(cols) : super.minimumSize();
    }

    /**
     * Returns the String of parameters for this TExtField.
     */
    protected String paramString() {
	String str = super.paramString();
	if (echoChar != 0) {
	    str += ",echo=" + echoChar;
	}
	return str;
    }
}
