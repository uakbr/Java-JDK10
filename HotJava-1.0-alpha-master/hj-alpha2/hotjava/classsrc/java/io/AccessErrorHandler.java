/*
 * @(#)AccessErrorHandler.java	1.4 95/01/31 Sami Shaio
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
package java.io;


/**
 * This class is used by the File access-control mechanism. If an
 * access control exception occurs and an error handler was installed
 * using the File.setAccessErrorHandler method, then the handler will
 * get invoked to determine what course of action to take.
 *
 * @see File#setAccessErrorHandler
 * @see File
 * @version 1.4 31 Jan 1995
 * @author Sami Shaio
 */
public class AccessErrorHandler {
    /** Allow the current access and all subsequent ones. */
    public final static int ALLOW_ACCESS = 0;

    /** Allow the current access only. */
    public final static int ALLOW_THIS_ACCESS = 1;

    /** Deny the current access. */
    public final static int DENY_ACCESS = 2;

    /**
     * This method is invoked if a read operation was attempted on the
     * given file and the acl check failed.
     * @param f the File to be accessed.
     * @returns one of ALLOW_ACCESS, ALLOW_THIS_ACCESS, or DENY_ACCESS
     */
    public int readException(String f) {
	return DENY_ACCESS;
    }

    /**
     * This method is invoked if a write operation was attempted on the
     * given file and the acl check failed.
     * @param f the File to be accessed.
     * @returns one of ALLOW_ACCESS, ALLOW_THIS_ACCESS, or DENY_ACCESS
     */
    public int writeException(String f) {
	return DENY_ACCESS;
    }
}
	    
