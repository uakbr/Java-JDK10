/*
 * @(#)File.java	1.24 95/02/13 Jonathan Payne, Arthur van Hoff
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

import java.util.Vector;

/**
 * This class represents a file name of the host file system.
 * The file name can be relative or absolute. It must use
 * the file name conventions of the host platform. <p>
 *
 * The intention is to provide an abstraction that deals
 * with most of the system-dependent file name features such
 * as the separator character, root, device name, etc.
 * Not all features are currently fully implemented.<p>
 *
 * Note that whenever a file name or path is  used it is
 * assumed that the host's file name conventions are used.
 *
 * @version 	1.24, 13 Feb 1995
 * @author	Jonathan Payne
 * @author	Arthur van Hoff
 */
public
class File {
    /**
     * The path of the file. The host's file separator is used.
     */
    protected String path;

    /**
     * Separator string. System dependent.
     */
    public static final String separator = getSeparator();

    /**
     * Separator character. System dependent.
     */
    public static final char separatorChar = separator.charAt(0);

    private static native String getSeparator();
    
    /**
     * Creates a File object.
     * @param path file path
     */
    public File(String path) {
	if (path == null) {
	    throw new NullPointerException();
	}
	this.path = path;
    }	

    /**
     * Creates a File object (given a directory).
     * @param path directory
     * @param name file name
     */
    public File(String path, String name) {
	this((path != null) ? path + separator + name : name);
    }

    /**
     * Creates a File object (given a directory File object).
     * @param dir the directory
     * @param name file name
     */
    public File(File dir, String name) {
	this(dir.getPath(), name);
    }

    /**
     * Gets the name of the file. Not including the
     * directory.
     */
    public String getName() {
	int index = path.lastIndexOf(separatorChar);
	return (index < 0) ? path : path.substring(index + 1);
    }

    /**
     * Gets the path of the file.
     */
    public String getPath() {
	return path;
    }

    /**
     * Gets the absolute path of the file.
     */
    public String getAbsolutePath() {
	return isAbsolute() ? path : System.getCWD() + separator + path;
    }

    /**
     * Gets the name of the parent directory.
     * @return the parent directory, or null if there isn't one
     */
    public String getParent() {
	int index = path.lastIndexOf(separatorChar);
	return (index <= 0) ? null : path.substring(0, index);
    }

    /**
     * Returns true if the file exists.
     */
    public native boolean exists();

    /**
     * Returns true if the file exists and is writable.
     */
    public native boolean canWrite();

    /**
     * Returns true if the file exists and is readable.
     */
    public native boolean canRead();

    /**
     * Returns true if the file exists and is a normal file.
     */
    public native boolean isFile();

    /**
     * Returns true if the file exists and is a directory.
     */
    public native boolean isDirectory();

    /**
     * Returns true if the file name is absolute.
     */
    public boolean isAbsolute() {
	return path.startsWith(separator);
    }

    /**
     * Returns last modification time. The return value should
     * only be used to compare modifications dates. It is meaningless
     * as an absolute time.
     */
    public native int lastModified();

    /**
     * Create a directory.
     * @return true if successfull
     */
    public native boolean mkdir();

    /**
     * Rename a file.
     * @return true if successfull
     */
    public native boolean renameTo(File dest);

    /**
     * Creates all directories in this path.
     */
    public boolean mkdirs() {

	if (mkdir()) {
 	    return true;
 	}

	String parent = getParent();
	return (parent != null) && (new File(parent).mkdirs() && mkdir());
    }

    /**
     * Lists the files in a directory. Works only works on directories.
     * @return An array of file names, this list will include all
     * files in the directory except the equivalent of "." and "..".
     */
    public native String list()[];

    /**
     * Lists the files in a directory Use the filter to select
     * which files.
     * @param filter the filter used to select file names
     * @see FilenameFilter
     */
    public String list(FilenameFilter filter)[] {
	String names[] = list();

	// Fill in the Vector
	Vector v = new Vector();
	for (int i = 0 ; i < names.length ; i++) {
	    if ((filter == null) || filter.accept(this, names[i])) {
		v.addElement(names[i]);
	    }
	}

	// Create the array
	String files[] = new String[v.size()];
	v.copyInto(files);

	return files;
    }

    /**
     * Computes a hashcode for the file.
     */
    public int hashCode() {
	return path.hashCode() ^ 1234321;
    }

    /**
     * Compares this object against some other object.
     * @param obj		the object to compare with
     * @return 		true if the object is the same
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof File)) {
	    return path.equals(((File)obj).path);
	}
	return false;
    }

    /**
     * Converts to a string.
     */
    public String toString() {
	return getPath();
    }
    
    /**
     * Initializes security ACL for reading files.
     * Can only be executed once.
     * @param path a list of directories separated by ':'s.
     */
    public static native void setReadACL(String path);

    /**
     * Get the value of the read ACL.
     */
    public static native String getReadACL();

    /**
     * Initializes security ACL for writing files. 
     * Can only be executed once.
     * @param path a list of directories separated by ':'s.
     */
    public static native void setWriteACL(String path);

    /**
     * Gets the value of the write ACL.
     */
    public static native String getWriteACL();

    /**
     * Initializes security ACL for executing files. 
     * Can only be executed once.
     * @param path a list of directories separated by ':'s.
     */
    public static native void setExecACL(String path);

    /**
     * Initializes the handler object which will get invoked if an
     * incorrect access is attempted. The handler can override the
     * acl. This method can only be invoked once.
     * @see AccessErrorHandler
     */
    public static native void setAccessErrorHandler(AccessErrorHandler h);
}
