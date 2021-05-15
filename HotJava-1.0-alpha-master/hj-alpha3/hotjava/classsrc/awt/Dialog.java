/*
 * @(#)Dialog.java	1.4 95/01/31 Herb Jellinek
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
 * Dialog is an interface that factors out the common elements of 
 * all dialog boxes.  It's mainly here for users of the DialogHandler
 * callback methods.
 *
 * @see MessageDialog
 * @see DialogHandler
 * @version 1.4 31 Jan 1995
 * @author Herb Jellinek
 */

public interface Dialog {

    /** Dispose of this dialog. */
    abstract void dispose();

    /** Show this dialog. If modal, returns the number of the button pressed.
     * If not, returns -1.
     */
    abstract int show();

    /** Hide this dialog. */
    abstract void hide();
}
