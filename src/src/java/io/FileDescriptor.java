/*
 * @(#)FileDescriptor.java	1.7 95/12/19 Pavani Diwanji
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

package java.io;

/*
 * The FileDescriptor object serves as an opaque handle to an open 
 * file or an open socket.
 * @see	FileInputStream
 * @see	FileOutputStream
 * @see SocketInputStream
 * @see SocketOutputStream
 * @version 	1.7, 12/19/95	
 * @author	Pavani Diwanji
 */


public final class FileDescriptor {

    private int fd; 

    /**
     * Handle to standard input.
     */    
    public static final FileDescriptor in 
	= initSystemFD(new FileDescriptor(),0);

 /**
     * Handle to standard output.
     */  
    public static final FileDescriptor out 
	= initSystemFD(new FileDescriptor(),1);

 /**
     * Handle to standard error.
     */  
    public static final FileDescriptor err 
	= initSystemFD(new FileDescriptor(),2);

    /**
     * Determines whether the file descriptor object is valid.
     */
    public native boolean valid();

    /**
     * This routine initializes in, out and err in a sytem dependent way.
     */
    private static native FileDescriptor initSystemFD(FileDescriptor fdObj, 
	int desc);
}




