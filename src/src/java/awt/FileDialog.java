/*
 * @(#)FileDialog.java	1.13 95/12/14 Arthur van Hoff
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

import java.awt.peer.FileDialogPeer;
import java.io.FilenameFilter;

/**
 * The File Dialog class displays a file selection dialog. It is a
 * modal dialog and will block the calling thread when the show method
 * is called to display it, until the user has chosen a file.
 *
 * @see Window#show
 *
 * @version 	1.13, 12/14/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class FileDialog extends Dialog {
    
    /**
     * The file load variable.
     */
    public static final int LOAD = 0;

    /**
     * The file save variable.
     */
    public static final int SAVE = 1;

    int mode;
    String dir;
    String file;
    FilenameFilter filter;

    /**
     * Creates a file dialog for loading a file.
     * @param parent the owner of the dialog
     * @param title the title of the Dialog
     */
    public FileDialog(Frame parent, String title) {
	this(parent, title, LOAD);
    }

    /**
     * Creates a file dialog with the specified title and mode.
     * @param parent the owner of the dialog
     * @param title the title of the Dialog
     * @param mode the mode of the Dialog
     */
    public FileDialog(Frame parent, String title, int mode) {
	super(parent, title, true);
	this.mode = mode;
	setLayout(null);
    }

    /**
     * Creates the frame's peer.  The peer allows us to change the look
     * of the file dialog without changing its functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createFileDialog(this);
	super.addNotify();
    }

    /**
     * Gets the mode of the file dialog.
     */
    public int getMode() {
	return mode;
    }

    /**
     * Gets the directory of the Dialog.
     */
    public String getDirectory() {
	return dir;
    }

    /**
     * Set the directory of the Dialog to the specified directory.
     * @param dir the specific directory
     */
    public void setDirectory(String dir) {
	this.dir = dir;
	if (peer != null) {
	    ((FileDialogPeer)peer).setDirectory(dir);
	}
    }

    /**
     * Gets the file of the Dialog.
     */
    public String getFile() {
	return file;
    }

    /**
     * Sets the file for this dialog to the specified file. This will 
     * become the default file if set before the dialog is shown.
     * @param file the file being set
     */
    public void setFile(String file) {
	this.file = file;
	if (peer != null) {
	    ((FileDialogPeer)peer).setFile(file);
	}
    }
	
    /**
     * Gets the filter.
     */
    public FilenameFilter getFilenameFilter() {
	return filter;
    }

    /**
     * Sets the filter for this dialog to the specified filter.
     * @param filter the specified filter
     */
    public void setFilenameFilter(FilenameFilter filter) {
	this.filter = filter;
	FileDialogPeer peer = (FileDialogPeer)this.peer;
	if (peer != null) {
	    peer.setFilenameFilter(filter);
	}
    }

    /**
     * Returns the parameter String of this file dialog.
     * Parameter String.
     */
    protected String paramString() {
	String str = super.paramString();
	if (dir != null) {
	    str += ",dir= " + dir;
	}
	return str + ((mode == LOAD) ? ",load" : ",save");
    }
}
