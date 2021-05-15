/*
 * Copyright (c) 1994, 1995 by Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * @(#)OutputFile.java 95/01/31 1.3
 *
 * December 1994, Eugene Kuerner
 *
 * This file defines class OutputFile which implementa a write only
 * output file abstraction.  Like class InputFile, class OutputFile
 * subclasses of class File to obtain simple file and path name
 * functions.
 *
 * @version 	1.0, 01 Dec 1994
 * @author	Eugene Kuerner
 *
 */


/*
 * Because we depend on native code to implement some of the methods
 * in this class, we need to refer to the system's Linker object.
 *
 */
import java.util.Linker;


/*
 * Associate this class with classes File and InputFile (already part
 * of package demo) into package demo.
 *
 */
package demo;


/**
 * This class defines a simple writeonly file by extending class File.
 *
 */
public
class OutputFile extends File {

    /**
     * Link in the native library that this class depends on.  We
     * bind this class loading to the library loading by making the
     * Linker.loadLibrary() call part of the static initializer for
     * the class.  That is, if the loadLibary() call fails this class
     * fails to load.
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
     * Constructor for the output file object.  Initializes the
     * parent class with the path name.
     *
     */
    public OutputFile(String path) {
	super(path);
    }

    /**
     * Attempts to open the file for writing.  If the file does not
     * exist one is created.  Returns TRUE on success and FALSE on 
     * failure.
     *
     */
    public native boolean open();

    /**
     * Attempts to close the previously opened file.  Has
     * no return value.
     */
    public native void close();

    /**
     * Writes some number of bytes to the opened file.  Returns
     * the number of bytes written.
     *
     */
    public native int write(byte b[], int len);
}
