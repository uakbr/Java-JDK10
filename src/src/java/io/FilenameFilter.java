/*
 * @(#)FilenameFilter.java	1.12 95/08/10 Jonathan Payne
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

/**
 * A filter interface for file names.
 *
 * @see File
 * @version 	1.12, 08/10/95
 * @author	Jonathan Payne
 * @author	Arthur van Hoff
 */
public
interface FilenameFilter {
    /**
     * Determines whether a name should be included in a file list.
     * @param dir	the directory in which the file was found
     * @param name the name of the file
     * @return true if name should be included in file list; false otherwise.
     */
    boolean accept(File dir, String name);
}
