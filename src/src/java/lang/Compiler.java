/*
 * @(#)Compiler.java	1.1 95/11/29  
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

package java.lang;

/**
 * @version 	1.1, 11/29/95
 * @author	Frank Yellin
 */

public final class Compiler  {
    private Compiler() {}		// don't make instances
    
    private static native void initialize();

    static { 
	try { 
	    String library = System.getProperty("java.compiler");
	    if (library != null) {
		System.loadLibrary(library);
		initialize();
		}
	} catch (Throwable e) { 
	}
    }

    public static native boolean compileClass(Class clazz);
    public static native boolean compileClasses(String string);
    public static native Object command(Object any);
    public static native void enable();
    public static native void disable();
}
