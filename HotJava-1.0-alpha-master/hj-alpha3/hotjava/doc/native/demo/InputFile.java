/*
 * Copyright (c) 1994, 1995 by Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * @(#)InputFile.java 95/02/01 1.4
 *
 * December 1994, Eugene Kuerner
 *
 * This file extends the File class by adding file manipulation
 * methods creating a readonly input file abstraction.  We leverage
 * the path manipulation code provided in the File superclass and
 * extend that code with support for opening and reading files.
 *
 * version 	1.0, 01 Dec 1994
 * author	Eugene Kuerner
 *
 */


/*
 * Because this class depends on native methods (i.e. methods actually
 * implemented in some other language such as C), we need to ensure
 * that these functions are loaded into the Java interpreter at
 * runtime.  We use the Java Linker class for this task.
 * We need to "include" the class definitions for the Java
 * Linker object.  The Linker class gets loaded in by the import
 * statment.  And the loadLibrary call that's used later in this
 * class is a static method that operates on the global class and
 * not a specific object instance.  Please refer to the Java language
 * specification for more information on static methods and variables.
 *
 */
import java.util.Linker;


/*
 * Since we want this file to be part of the "demo" package we direct
 * the compiler to associate this class with the other "demo" package
 * classes.
 *
 */
package demo;


/**
 * Define class InputFile that presents a simple readonly input file 
 * abstraction.  Note that we use native or non-Java methods to
 * implement some of the methods.
 *
 */
public
class InputFile extends File {

    /**
     * Link in the native library that we depends on.  If we cannot
     * link this in, an exception is generated and the class loading
     * fails.  We have arbitrarily named the library "file" at the
     * Java level (or libfile.so at the solaris level).  Additionally,
     * the Linker call is part of the static initializer for the class.
     * Thus, the library is loaded as part of this class being loaded.
     *
     */
    static {
        Linker.loadLibrary("file");
    }

    /**
     * Holds the system dependent handle to the file resource.
     *
     */
    protected int fd;

    /**
     * Constructor for the input file object.  Initializes the
     * parent class with the path name.
     *
     */
    public InputFile(String path) {
	super(path);
    }

    /**
     * Attempts to open the file for reading.  Returns
     * TRUE on success and FALSE on failure.  Alternatively, we could
     * throw an exception and catch it.
     *
     */
    public native boolean open();

    /**
     * Attempts to close the previously opened file.  Has
     * no return value.
     */
    public native void close();

    /**
     * Reads some number of bytes from the opened file.  Returns
     * the number of bytes read or -1 when the file is empty.
     *
     */
    public native int read(byte b[], int len);
}
