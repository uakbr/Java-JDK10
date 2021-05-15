/*
 * @(#)TextField.java	1.21 95/03/28 Sami Shaio
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
 * TextField is a gui element that allows a single line of text input.
 *
 * @version 1.21 28 Mar 1995
 * @author Sami Shaio
 */
public class TextField extends Component {
    private boolean hFill;
    private WServer wServer;
    public boolean echoSet = false;
    public char    echoChar;
    public boolean editable;

    /**
     * Constructs a TextField.
     * @param initValue is the initial value of the field. It may be null.
     * @param pName is the name of this component.
     * @param p is the parent window of this TextField.
     * @param editable is true if this TextField should allow editing.
     */
    public TextField(String initValue,
		     String pName,
		     Container p,
		     boolean editable) {
	super(p, pName);
	Window win = Window.getWindow(p);
	wServer = win.wServer;	
	wServer.textFieldCreate(this, initValue, win, editable);
	this.editable = editable;
	setBackColor(editable ? Color.editableText : Color.readOnlyText);
    }

    /**
     * If t is true then this TextField will stretch horizontally to
     * the width of its container.
     */
    public void setHFill(boolean t) {
	hFill = t;
    }

    /**
     * Sets the font for this TextField.
     */
    public void setFont(Font f) {
	wServer.textFieldSetFont(this, f);
    }

    /**
     * Sets the color of the text in this TextField.
     */
    public void setColor(Color c) {
	wServer.textFieldSetColor(this, c);
    }

    /**
     * Sets the color of this TextField's background.
     */
    public void setBackColor(Color c) {
	wServer.textFieldSetBackColor(this, c);
    }

    /**
     * Sets the echo character for this TextField. This is useful
     * for fields where the user input shouldn't be echoed to the screen,
     * as in the case of a TextField that represents a password.
     */
    public void setEchoCharacter(char c) {
	echoSet = true;
	echoChar = c;
	wServer.textFieldSetEchoCharacter(this, c);
    }

    /**
     * Sets whether or not this TextField should be editable.
     */
    public void setEditable(boolean t) {
	editable = t;
	wServer.textFieldSetEditable(this, t);
	setBackColor(t ? Color.editableText : Color.readOnlyText);
    }

    /**
     * Sets the text of this TextField.
     */
    public void setText(String t) {
	wServer.textFieldSetText(this, t);
    }

    /**
     * Returns the text contained in this TextField.
     */
    public String getText() {
	return wServer.textFieldGetText(this);
    }

    /**
     * Disposes of this TextField, rendering it useless.
     */
    public void dispose() {
	wServer.textFieldDispose(this);
    }

    /**
     * Moves this TextField to x,y
     */
    public void move(int x, int y) {
	super.move(x,y);
	wServer.textFieldMoveTo(this, x, y);
    }

    /**
     * Reshapes this TextField to the given dimensions.
     */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.textFieldReshape(this, x, y, w, h);
    }

    /**
     * Returns the preferred size of this TextField.
     */
    public Dimension getPreferredSize() {
	Dimension dim = new Dimension((hFill) ? parent.width : width, height);

	return dim;
    }

    /**
     * Shows this TextField.
     */
    public void map() {
	wServer.textFieldShow(this);
	mapped = true;
    }

    /**
     * Hides this TextField.
     */
    public void unMap() {
	wServer.textFieldHide(this);
	mapped = false;
    }


    /**
     * Override this method to take action when the user hits return
     * after entering a value in the TextField.
     */
    public void selected() {
    }

}
	    
