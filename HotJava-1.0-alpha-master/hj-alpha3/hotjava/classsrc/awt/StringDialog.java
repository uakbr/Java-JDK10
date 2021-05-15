/*
 * @(#)StringDialog.java	1.8 95/02/22 95/01/13 Herb Jellinek
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
 * StringDialog is a class that presents a dialog box to the
 * user.  It contains a picture (well, not yet!), a message, a text input
 * area, and up to three buttons: ok, cancel, and help.  The number
 * of buttons and the button labels can be controlled by the API.
 * The contents of the text field can be retrieved through it.
 *
 * @see DialogHandler
 * @version 1.8 22 Feb 1995
 * @author Herb Jellinek
 */
public
class StringDialog extends Frame implements Dialog, DialogHandler {

    public static int stdWidth  = 300;
    public static int stdHeight = 150;
    
    /** The handler object that gets invoked to handle actions. */
    DialogHandler handler;

    /** The "OK" button label */
    String okLabel;
    
    /** The "Cancel" button label */
    String cancelLabel;
    
    /** The "Help" button label */
    String helpLabel;

    /** Is this a modal dialog? */
    boolean isModal;

    /** This window's title */
    String title;

    /** The Label that holds the message. */
    Label message;

    /** The text field. */
    SelectingTextField textInput;    

    /** The OK button. */
    Button okButton = null;

    /** The Cancel button. */
    Button cancelButton = null;

    /** The Help button. */
    Button helpButton = null;

    /** The Button that was chosen. Only valid for modal dialogs.*/
    int chosenButton;

    /**
     * Constructs a new StringDialog.
     * @param f is the frame that is to be the parent of this StringDialog.
     * @param title is the title of the dialog. It can be null.
     * @param message is the message to display in the dialog. It can
     * be changed later with setMessage.
     * @param dialogType is one of INFO_TYPE (for information
     * dialogs), ERROR_TYPE (for an error dialog), or QUESTION_TYPE (for
     * a question dialog).  !! Currently ignored !!
     * @param nButtons is the number of buttons to use. It is a number
     * from 1 to 3. The number corresponds to whether the buttons should
     * be ok (1), ok and cancel (2), or ok, cancel, and help (3).
     * @param isModal determines whether the dialog will block all
     * user input until the dialog is disposed of by clicking one of
     * the ok or cancel buttons.  !! Currently ignored !!
     * @param okLabel is the label to use for the Ok button. If null,
     * then a default string will be chosen.
     * @param cancelLabel is the label to use for the Ok button. If null,
     * then a default string will be chosen.
     * @param helpLabel is the label to use for the Ok button. If null,
     * then a default string will be chosen.
     * @param initialText is the text to preload into the text input field.
     * If null, field will be empty.
     * @param handler is the object that will handle the callbacks for
     * the buttons listed in the dialog. It may be null in which case
     * a default action is taken for all the buttons.
     */
    public StringDialog(Frame f, Font font,
			String title, String messageStr,
			boolean wantHelp, boolean isModal,
			String okLabel, String cancelLabel, String helpLabel,
			String initialText, DialogHandler handler) {
	super(f.wServer, true, isModal, f, stdWidth, stdHeight, Color.lightGray);
	setTitle(title);
	setDefaultFont(font);
	this.isModal = isModal;
	this.handler = handler;
	
	Window guts = new Window(this, "North", background,
				 stdWidth, stdHeight);
	guts.setLayout(RowColLayout.oneColumn);
	
	message = new Label(messageStr, "message", guts);
	message.setHFill(true);

	textInput = new SelectingTextField((initialText == null) ?
					   "" : initialText,
					   "textInput", guts, this, this);
	textInput.setHFill(true);

	Window buttons = new Window(this, "South", background, stdWidth, 100);
	okButton = new DialogOKButton(okLabel, "okButton", buttons, this);
	cancelButton = new DialogCancelButton(cancelLabel, "cancelButton",
					      buttons, this);
	if (wantHelp) {
	    helpButton = new DialogHelpButton(helpLabel, "helpButton", buttons,
					      this);
	}
    }


    /** Set this dialog's handler. */
    public void setHandler(DialogHandler dh) {
	handler = dh;
    }

    /** Change the message associated with this dialog. */
    public void setMessage(String msg) {
	message.setText(msg);
    }

    /** Get the text from the text input field. */
    public String getText() {
	return textInput.getText();
    }

    /** Set the text in the text input field. */
    public void setText(String text) {
	textInput.setText(text);
    }	

    /** Show this dialog.
     * @returns the number of the button that was pressed if this
     * dialog is modal. Otherwise -1 is returned.
     */
    public int show() {
	map();
	if (isModal) {
	    return chosenButton;
	} else {
	    return -1;
	}
    }

    /** Hide this dialog */
    public void hide() {
	unMap();
    }

    /** Dispose of this dialog. */
    public void dispose() {
    }

    /* DialogHandler methods */

    /** Invoked when the user presses the "Ok" button. */
    public void okCallback(Dialog m) {
	chosenButton = 1;
	if (handler == null) {
	    m.hide();
	} else {
	    handler.okCallback(m);
	}
    }

    /** Invoked when the user presses the "Cancel" button. */
    public void cancelCallback(Dialog m) {
	chosenButton = 2;
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

    
class DialogOKButton extends Button {
    StringDialog dialog;
    
    public DialogOKButton(String label, String name, Window p,
			  StringDialog d) {
	super(label, name, p);
	dialog = d;
    }

    public void selected(Component c, int pos) {
	dialog.okCallback(dialog);
    }
}

class DialogCancelButton extends Button {
    StringDialog dialog;
    
    public DialogCancelButton(String label, String name, Window p,
			      StringDialog d) {
	super(label, name, p);
	dialog = d;
    }

    public void selected(Component c, int pos) {
	dialog.cancelCallback(dialog);
    }
}

class DialogHelpButton extends Button {
    StringDialog dialog;
    
    public DialogHelpButton(String label, String name, Window p,
			    StringDialog d) {
	super(label, name, p);
	dialog = d;
    }

    public void selected(Component c, int pos) {
	dialog.helpCallback(dialog);
    }
}
