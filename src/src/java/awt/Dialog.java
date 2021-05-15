/*
 * @(#)Dialog.java	1.13 95/12/14 Arthur van Hoff
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

import java.awt.peer.DialogPeer;

/**
 * A class that produces a dialog - a window that takes input from the user.
 * The default layout for a dialog is BorderLayout.
 *
 * @version 	1.13, 12/14/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Dialog extends Window {
    boolean	resizable = true;

    /**
     * Sets to true if the Dialog is modal.  A modal
     * Dialog grabs all the input from the user.
     */
    boolean modal;

    /**
     * The title of the Dialog.
     */
    String title;

    /**
     * Constructs an initially invisible Dialog. A modal
     * Dialog grabs all the input from the user.
     * @param parent the owner of the dialog
     * @param modal if true, dialog blocks input to other windows when shown
     * @see Component#resize
     * @see Component#show
     */
    public Dialog(Frame parent, boolean modal) {
	super(parent);
	this.modal = modal;
    }

    /**
     * Constructs an initially invisible Dialog with a title. 
     * A modal Dialog grabs all the input from the user.
     * @param parent the owner of the dialog
     * @param title the title of the dialog
     * @param modal if true, dialog blocks input to other windows when shown
     * @see Component#resize
     * @see Component#show
     */
    public Dialog(Frame parent, String title, boolean modal) {
	this(parent, modal);
	this.title = title;
    }

    /**
     * Creates the frame's peer.  The peer allows us to change the appearance
     * of the frame without changing its functionality.
     */
    public synchronized void addNotify() {
	if (peer == null) {
	    peer = getToolkit().createDialog(this);
	}
	super.addNotify();
    }

    /**
     * Returns true if the Dialog is modal.  A modal
     * Dialog grabs all the input from the user.
     */
    public boolean isModal() {
	return modal;
    }

    /**
     * Gets the title of the Dialog.
     * @see #setTitle
     */
    public String getTitle() {
	return title;
    }

    /**
     * Sets the title of the Dialog.
     * @param title the new title being given to the Dialog
     * @see #getTitle
     */
    public void setTitle(String title) {
	this.title = title;
	DialogPeer peer = (DialogPeer)this.peer;
	if (peer != null) {
	    peer.setTitle(title);
	}
    }

    /**
     * Returns true if the user can resize the frame.
     */
    public boolean isResizable() {
	return resizable;
    }

    /**
     * Sets the resizable flag.
     * @param resizable true if resizable; false otherwise
     */
    public void setResizable(boolean resizable) {
	this.resizable = resizable;
	DialogPeer peer = (DialogPeer)this.peer;
	if (peer != null) {
	    peer.setResizable(resizable);
	}
    }

    /**
     * Returns the parameter String of this Dialog.
     */
    protected String paramString() {
	String str = super.paramString() + (modal ? ",modal" : ",modeless");
	if (title != null) {
	    str += ",title=" + title;
	}
	return str;
    }
}
