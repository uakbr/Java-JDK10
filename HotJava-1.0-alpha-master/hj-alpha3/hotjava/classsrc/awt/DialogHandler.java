/*
 * @(#)DialogHandler.java	1.3 95/01/31 Sami Shaio
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

/**
 * DialogHandler is an interface used by users of the MessageDialog
 * class. It defines three callbacks which are invoked by MessageDialog
 * in response to clicking on one of the three buttons, "ok", "cancel",
 * and "help".
 *
 * @see MessageDialog
 * @version 1.3 31 Jan 1995
 * @author Sami Shaio
 */
public interface DialogHandler {
    /** Invoked when the user presses the "Ok" button. */
    abstract void okCallback(Dialog m);

    /** Invoked when the user presses the "Cancel" button. */
    abstract void cancelCallback(Dialog m);

    /** Invoked when the user presses the "Help" button. */
    abstract void helpCallback(Dialog m);
}

	 
