/*
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
class Filename {
    String fullpath;
    char pathseparator;

    Filename(String str, char sep) {
	fullpath = str;
	pathseparator = sep;
    }

    String extension() {
	int dot = fullpath.lastIndexOf('.');
	return fullpath.substring(dot + 1);
    }

    String filename() {
	int dot = fullpath.lastIndexOf('.');
	int sep = fullpath.lastIndexOf(pathseparator);
	return fullpath.substring(sep + 1, dot);
    }

    String path() {
	int sep = fullpath.lastIndexOf(pathseparator);
	return fullpath.substring(0, sep);
    }
}
