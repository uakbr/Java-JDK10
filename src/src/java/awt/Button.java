/*
 * @(#)Button.java	1.16 95/08/17 Sami Shaio
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

import java.awt.peer.ButtonPeer;

/**
 * A class that produces a labeled button component.
 *
 * @version 	1.16 08/17/95
 * @author 	Sami Shaio
 */
public class Button extends Component {
    /**
     * The string label for this button.
     */
    String label;
    
    /**
     * Constructs a Button with no label.
     */
    public Button() {
	this("");
    }

    /**
     * Constructs a Button with a string label.
     * @param label the button label
     */
    public Button(String label) {
	this.label = label;
    }
    
    /**
     * Creates the peer of the button.  This peer allows us to
     * change the look of the button without changing its functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createButton(this);
	super.addNotify();
    }

    /**
     * Gets the label of the button.
     * @see #setLabel
     */
    public String getLabel() {
	return label;
    }

    /**
     * Sets the button with the specified label.
     * @param label the label to set the button with
     * @see #getLabel
     */
    public void setLabel(String label) {
	this.label = label;

	ButtonPeer peer = (ButtonPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /**
     * Returns the parameter String of this button.
     */
    protected String paramString() {
	return super.paramString() + ",label=" + label;
    }
}


