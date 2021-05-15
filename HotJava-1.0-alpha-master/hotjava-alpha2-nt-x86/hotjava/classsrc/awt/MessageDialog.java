/*
 * @(#)MessageDialog.java	1.8 95/01/31 Sami Shaio
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
package awt;

import java.lang.*;


/**
 * MessageDialog is a class that allows a modal or non-modal dialog to
 * be presented to the user with a message. The dialog contains a
 * picture, a message, and three buttons: ok, cancel, and help. The
 * number of buttons as well as the labels they display can be
 * controlled by the api.
 *
 * @see DialogHandler
 * @version 1.8 31 Jan 1995
 * @author Sami Shaio
 */
public class MessageDialog implements Dialog, DialogHandler {
    private int			pData;

    public static final int INFO_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int QUESTION_TYPE = 2;

    /** The frame to which this dialog is attached to. */
    public Frame parent;

    /** The handler object that gets invoked to handle actions. */
    DialogHandler	handler;

    /**
     * Constructs a new MessageDialog.
     * @param f is the frame that is to be the parent of this MessageDialog.
     * @param title is the title of the dialog. It can be null.
     * @param message is the message to display in the dialog. It can
     * be changed later with setMessage.
     * @param dialogType is one of INFO_TYPE (for information
     * dialogs), ERROR_TYPE (for an error dialog), or QUESTION_TYPE (for
     * a question dialog).
     * @param nButtons is the number of buttons to use. It is a number
     * from 1 to 3. The number corresponds to whether the buttons should
     * be ok (1), ok and cancel (2), or ok, cancel, and help (3).
     * @param isModal determines whether the dialog will block all
     * user input until the dialog is disposed of by clicking one of
     * the ok or cancel buttons.
     * @param okLabel is the label to use for the Ok button. If null,
     * then a default string will be chosen.
     * @param cancelLabel is the label to use for the Cancel button. If null,
     * then a default string will be chosen.
     * @param helpLabel is the label to use for the Help button. If null,
     * then a default string will be chosen.
     * @param handler is the object that will handle the callbacks for
     * the buttons listed in the dialog. It may be null in which case
     * a default action is taken for all the buttons.
     */
    public MessageDialog(Frame f,
			 String title,
    			 String message,
			 int dialogType,
			 int nButtons,
			 boolean isModal,
			 String okLabel,
			 String cancelLabel,
			 String helpLabel,
			 DialogHandler handler)	{
	parent = f;
	this.handler = handler;
	parent.wServer.messageDialogCreate(this,
					   f,
					   title,
					   message,
					   dialogType,
					   nButtons,
					   isModal,
					   okLabel,
					   cancelLabel,
					   helpLabel);
    }

    /** Change the message associated with this dialog. */
    public void setMessage(String message) {
	parent.wServer.messageDialogSetMessage(this, message);
    }

    /** Show this dialog.
     * @returns the number of the button that was pressed if this
     * dialog is modal. Otherwise -1 is returned.
     */
    public int show() {
	return parent.wServer.mesageDialogShow(this);
    }

    /** Hide this dialog */
    public void hide() {
	parent.wServer.mesageDialogHide(this);
    }

    /** Dispose of this dialog. */
    public void dispose() {
	parent.wServer.messageDialogDispose(this);
    }

    /* DialogHandler methods */

    /** Invoked when the user presses the "Ok" button. */
    public void okCallback(Dialog m) {
	if (handler == null) {
	    m.hide();
	} else {
	    handler.okCallback(m);
	}
    }

    /** Invoked when the user presses the "Cancel" button. */
    public void cancelCallback(Dialog m) {
	if (handler == null) {
	    m.hide();
	} else {
	    handler.cancelCallback(m);
	}
    }

    /** Invoked when the user presses the "Help" button. */
    public void helpCallback(Dialog m) {
	if (handler == null) {
	    System.out.println("No help available.");
	} else {
	    handler.helpCallback(m);
	}
    }
}
