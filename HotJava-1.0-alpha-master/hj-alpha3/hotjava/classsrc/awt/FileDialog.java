/*
 * @(#)FileDialog.java	1.10 95/01/31 Sami Shaio
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
 * A class that brings up a file dialog using the native gui
 * platform's dialog.
 *
 * @version 1.10 31 Jan 1995
 * @author Sami Shaio
 */
public class FileDialog {
    int		pData;
    public String title;
    public Frame parent;


    /**
     * Create a FileDialog, str is the title of the FileDialog. It can
     * be null in which case a default title is chosen.
     */
    public FileDialog(String str, Frame p) {
	title = str;
	parent = p;
	parent.wServer.fileDialogCreate(this, title, parent);
    }


    /**
     * Put up a FileDialog and wait for the user to either select a
     * file or cancel. If a file was chosen it will be returned,
     * otherwise, null is returned. If initValue is not null, then it
     * will be used as the initial value for the file dialog.
     */
    public String chooseFile(String initValue) {
	return parent.wServer.fileDialogChooseFile(this, initValue);
    }

    /**
     * Disposes of this FileDialog.
     */
    public void dispose() {
	parent.wServer.fileDialogDispose(this);
    }
}
