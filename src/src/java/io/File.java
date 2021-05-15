/*
 * @(#)File.java	1.37 95/12/19 Jonathan Payne, Arthur van Hoff
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
 * @version 	1.37, 12/19/95
 * @author	Jonathan Payne
 * @author	Arthur van Hoff
 */
public
class File {
    /**
     * The path of the file. The host's file separator is used.
     */
    private String path;

    /**
     * The system dependent file separator String.
     */
    public static final String separator = System.getProperty("file.separator");

    /**
     * The system dependent file separator character.
     */
    public static final char separatorChar = separator.charAt(0);
    
    /**
     * The system dependent path separator string.
     */
    public static final String pathSeparator = System.getProperty("path.separator");

    /**
     * The system dependent path separator character.
     */
    public static final char pathSeparatorChar = pathSeparator.charAt(0);
    
    /**
     * Creates a File object.
     * @param path the file path
     * @exception NullPointerException If the file path is equal to 
     * null.
     */
    public File(String path) {
	if (path == null) {
	    throw new NullPointerException();
	}
	this.path = path;
    }	

    /**
     * Creates a File object from the specified directory.
     * @param path the directory path
     * @param name the file name
     */
    public File(String path, String name) {
	this((path != null) ? path + separator + name : name);
    }

    /**
     * Creates a File object (given a directory File object).
     * @param dir the directory
     * @param name the file name
     */
    public File(File dir, String name) {
	this(dir.getPath(), name);
    }

    /**
     * Gets the name of the file. This method does not include the
     * directory.
     * @return the file name.
     */
    public String getName() {
	int index = path.lastIndexOf(separatorChar);
	return (index < 0) ? path : path.substring(index + 1);
    }

    /**
     * Gets the path of the file.
     * @return the file path.
     */
    public String getPath() {
	return path;
    }

    /**
     * Gets the absolute path of the file.
     * @return the absolute file path.
     */
    public String getAbsolutePath() {
	return isAbsolute() ? path : System.getProperty("user.dir") + separator + path;
    }

    /**
     * Gets the name of the parent directory.
     * @return the parent directory, or null if one is not found.
     */
    public String getParent() {
	int index = path.lastIndexOf(separatorChar);
	return (index <= 0) ? null : path.substring(0, index);
    }

    private native boolean exists0();
    private native boolean canWrite0();
    private native boolean canRead0();
    private native boolean isFile0();
    private native boolean isDirectory0();
    private native long lastModified0();
    private native long length0();
    private native boolean mkdir0();
    private native boolean renameTo0(File dest);
    private native boolean delete0();
    private native String[] list0();

    /**
     * Returns a boolean indicating whether or not a file exists.
     */
    public boolean exists() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return exists0();
    }

    /**
     * Returns a boolean indicating whether or not a writable file 
     * exists. 
     */
    public boolean canWrite() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(path);
	}
	return canWrite0();
    }

    /**
     * Returns a boolean indicating whether or not a readable file 
     * exists.
     */
    public boolean canRead() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return canRead0();
    }

    /**
     * Returns a boolean indicating whether or not a normal file 
     * exists.
     */
    public boolean isFile() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return isFile0();
    }

    /**
     * Returns a boolean indicating whether or not a directory file 
     * exists.
     */
    public boolean isDirectory() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return isDirectory0();
    }

    /**
     * Returns a boolean indicating whether the file name is absolute.
     */
    public native boolean isAbsolute();

    /**
     * Returns the last modification time. The return value should
     * only be used to compare modification dates. It is meaningless
     * as an absolute time.
     */
    public long lastModified() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return lastModified0();
    }

    /**
     * Returns the length of the file. 
     */
    public long length() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return length0();
    }


    /**
     * Creates a directory and returns a boolean indicating the
     * success of the creation.
     */
    public boolean mkdir() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(path);
	}
	return mkdir0();
    }

    /**
     * Renames a file and returns a boolean indicating whether 
     * or not this method was successful.
     * @param dest the new file name
     */
    public boolean renameTo(File dest) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(path);
	    security.checkWrite(dest.path);
	}
	return renameTo0(dest);
    }

    /**
     * Creates all directories in this path.  This method 
     * returns true if all directories in this path are created.
     */
    public boolean mkdirs() {
	if (mkdir()) {
 	    return true;
 	}

	String parent = getParent();
	return (parent != null) && (new File(parent).mkdirs() && mkdir());
    }

    /**
     * Lists the files in a directory. Works only on directories.
     * @return an array of file names.  This list will include all
     * files in the directory except the equivalent of "." and ".." .
     */
    public String[] list() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return list0();
    }

    /**
     * Uses the specified filter to list files in a directory. 
     * @param filter the filter used to select file names
     * @return the filter selected files in this directory.
     * @see FilenameFilter
     */
    public String[] list(FilenameFilter filter) {
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
     * Deletes the specified file. Returns true
     * if the file could be deleted.
     */
    public boolean delete() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkDelete(path);
	}
	return delete0();
    }

    /**
     * Computes a hashcode for the file.
     */
    public int hashCode() {
	return path.hashCode() ^ 1234321;
    }

    /**
     * Compares this object against the specified object.
     * @param obj  the object to compare with
     * @return 	true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof File)) {
	    return path.equals(((File)obj).path);
	}
	return false;
    }

    /**
     * Returns a String object representing this file's path.
     */
    public String toString() {
	return getPath();
    }
}
