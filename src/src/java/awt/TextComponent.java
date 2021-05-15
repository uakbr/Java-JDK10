/*
 * @(#)TextComponent.java	1.8 95/11/02 Arthur van Hoff
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
package java.awt;

import java.awt.peer.TextComponentPeer;

/**
 * A TextComponent is a component that allows the editing of some text.
 *
 * @version	1.8, 11/02/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class TextComponent extends Component {

    /**
     * The value of the text.
     */
    String text;

    /**
     * A boolean indicating whether or not this TextComponent is editable.
     */
    boolean editable = true;

    /**
     * The selection start.
     */
    int selStart;

    /**
     * The selection end.
     */
    int selEnd;

    /**
     * Constructs a new TextComponent initialized with the specified text.
     * @param text the initial text of the field.
     */
    TextComponent(String text) {
	this.text = text;
    }

    /**
     * Removes the TextComponent's peer.  The peer allows us to modify the appearance
     * of the TextComponent without changing its functionality.
     */
    public synchronized void removeNotify() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    text = peer.getText();
	    selStart = peer.getSelectionStart();
	    selEnd = peer.getSelectionEnd();
	}
	super.removeNotify();
    }

    /**
     * Sets the text of this TextComponent to the specified text.
     * @param t the new text to be set
     * @see #getText
     */
    public void setText(String t) {
	text = t;
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setText(t);
	}
    }

    /**
     * Returns the text contained in this TextComponent.
     * @see #setText
     */
    public String getText() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    text = peer.getText();
	}
	return text;
    }

    /**
     * Returns the selected text contained in this TextComponent.
     * @see #setText
     */
    public String getSelectedText() {
	return getText().substring(getSelectionStart(), getSelectionEnd());
    }

    /**
     * Returns the boolean indicating whether this TextComponent is editable or not.
     * @see #setEditable
     */
    public boolean isEditable() {
	return editable;
    }

    /**
     * Sets the specified boolean to indicate whether or not this TextComponent should be 
     * editable.
     * @param t the boolean to be set
     * @see #isEditable
     */
    public void setEditable(boolean t) {
	editable = t;
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setEditable(t);
	}
    }

    /**
     * Returns the selected text's start position.
     */
    public int getSelectionStart() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    selStart = peer.getSelectionStart();
	}
	return selStart;
    }

    /**
     * Returns the selected text's end position.
     */
    public int getSelectionEnd() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    selEnd = peer.getSelectionEnd();
	}
	return selEnd;
    }
    
    /**
     * Selects the text found between the specified start and end locations.
     * @param selStart the start position of the text
     * @param selEnd the end position of the text
     */
    public void select(int selStart, int selEnd) {
	String text = getText();
	if (selStart < 0) {
	    selStart = 0;
	}
	if (selEnd > text.length()) {
	    selEnd = text.length();
	}
	if (selEnd < selStart) {
	    selEnd = selStart;
	}
	if (selStart > selEnd) {
	    selStart = selEnd;
	}

	this.selStart = selStart;
	this.selEnd = selEnd;

	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.select(selStart, selEnd);
	}
    }

    /**
     * Selects all the text in the TextComponent.
     */
    public void selectAll() {
	String text = getText();
	this.selStart = 0;
	this.selEnd = getText().length();

	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.select(selStart, selEnd);
	}
    }

    /**
     * Returns the String of parameters for this TextComponent.
     */
    protected String paramString() {
	String str = super.paramString() + ",text=" + getText();
	if (editable) {
	    str += ",editable";
	}
	return str + ",selection=" + getSelectionStart() + "-" + getSelectionEnd();
    }
}
