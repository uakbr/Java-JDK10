/*
 * @(#)Linker.java	1.11 95/02/22 Arthur van Hoff
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
package java.util;

/**
 * A class to encapsulates dynamic linking.
 * This class allows the system to dynamically link in new libraries containing
 * the implementation of native methods. Here is an example of what such a
 * class may look like:
 * <pre>
 * 	class MPEGDecoder {
 *	    public native decode(InputStream s);
 *
 *	    static {
 *		Linker.loadLibrary("mpeg");
 *	    }    
 * 	}
 * </pre>
 * The functionality in this class is very system dependent.
 *
 * @version 	1.11, 22 Feb 1995
 * @author	Arthur van Hoff
 */

public final
class Linker {
    /** Makes sure nobody instantiates it */
    private Linker() {
    }
    
    /** The paths searched for libraries */
    private static String paths[];

    /**
     * Initializes the linker (adds the INSTALLPATH to the LD_LIBRARY_PATH),
     * and  returns the value of the environment variable LD_LIBRARY_PATH.
     */
    private static synchronized native String initialize();
    private static native int getSeparator();
    private static native String buildLibName(String pathname, String filename);

    static {
	String ldpath = initialize();
	int    c = getSeparator();
	int ldlen = ldpath.length();
	int i, j, n;
	// Count the separators in the path
	i = ldpath.indexOf(c);
	n = 0;
	while (i >= 0) {
	    n++;
	    i = ldpath.indexOf(c, i+1);
	}

	// allocate the array of paths - n :'s = n + 1 path elements
	paths = new String[n + 1];

	// Fill the array with paths from the ldpath
	n = i = 0;
	j = ldpath.indexOf(c);
	while (j >= 0) {
	    if (j - i > 0) {
		paths[n++] = ldpath.substring(i, j);
	    }
	    i = j + 1;
	    j = ldpath.indexOf(c, i);
	}
	paths[n] = ldpath.substring(i, ldlen);
    }
    
    /*
     * Loads a dynamic library, given a complete path name. If you use this
     * from java_g it will automagically insert "_g" before the ".so".
     * It will throw an UnsatisfiedLinkException if the file does not
     * exist.
     *
     * Example: Linker.load("/home/avh/lib/libX11.so");
     */
    public static synchronized native void load(String filename);

    /**
     * Loads a dynamic library given a library name. The call to LoadLibrary
     * should be made in the static initializer of the first class that is
     * loaded. Linking in the same library more than once is ignored.
     * @param libname the name of the library
     * @exception UnsatisfiedLinkException Something went wrong
     */
    public static synchronized void loadLibrary(String libname) {

	for (int i = 0 ; i < paths.length ; i++) {
	    try {
		String tempname = buildLibName(paths[i], libname);
		Linker.load(tempname);
		return;
	    } catch (UnsatisfiedLinkException e) {
	    }
	}

	// Oops, it failed
	UnsatisfiedLinkException e = new
	    UnsatisfiedLinkException("no " + libname + " in LD_LIBRARY_PATH");
	throw e;
    }
}
