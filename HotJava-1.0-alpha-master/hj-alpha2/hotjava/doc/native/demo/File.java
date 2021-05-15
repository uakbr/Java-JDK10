/*
 * Copyright (c) 1994, 1995 by Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * @(#)File.java 95/02/01 1.4
 *
 * December 1994, Eugene Kuerner
 *
 *
 * This file defines the superclass for the classes used in the native
 * library example.  The File class defined here is loosely based on
 * the File classes provided as part of the Java base io classes.
 * This File superclass provides basic file and path manipulation with
 * the expectation that subclasses will provide the actual file
 * management code depending on the file semantics they want to present.
 * For example, read only input files and read and write output files.
 *
 * version 	1.0, 01 Dec 1994
 * author	Eugene Kuerner
 *
 */


/*
 * I wanted to create a package (refer to the Java language
 * specification for more information on packages) for all of the classes
 * used in this example.  Using the package statment in all of the
 * related classes, we can force all of these classes to be associated
 * together.
 *
 */
package demo;


/**
 * The File superclass defines an interface for manipulating path
 * and file names.  
 *
 */
public
class File {

    /**
     * The file path.  We want to use an abstract path separator
     * in Java that is converted to the system dependent path
     * separator.
     *
     */
    protected String path;

    /**
     * The class File's notion of a path separator character.  This
     * will be the Java path separator.  Note that this will be
     * converted to the system dependent path separator at runtime
     * by code in the native library.
     *
     */
    public static final char separatorChar = ':';

    /**
     * The constructor, initializes the class with the given path.  Note
     * that we use the String class found in the Java core classes.
     *
     */
    public File(String path) {
	if (path == null) {
	    throw new NullPointerException();
	}
	this.path = path;
    }	

    /**
     * Get the name of the file, not including the directory path.
     *
     */
    public String getFileName() {
	int index = path.lastIndexOf(separatorChar);
	return (index < 0) ? path : path.substring(index + 1);
    }

    /**
     * Get the name of the file including the full directory path.
     *
     */
    public String getPath() {
	return path;
    }
}
