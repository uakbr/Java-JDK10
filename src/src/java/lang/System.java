/*
 * @(#)System.java	1.55 95/11/13 Arthur van Hoff
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

import java.io.*;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This Class provides a system-independent interface to system 
 * functionality.  One of the more useful things provided by this Class
 * are the standard input and output streams. The standard input streams are
 * used for reading character data.  The standard output streams are used for
 * printing. For example:
 * <pre>
 *	System.out.println("Hello World!");
 * </pre>
 * This Class cannot be instantiated or subclassed because all of the methods
 * and variables are static.
 * @version 	1.55, 11/13/95
 * @author	Arthur van Hoff
 */
public final
class System {

    /** Don't let anyone instantiate this class */
    private System() {
    }

    /**
     * Standard input stream.  This stream is used for reading in character
     * data.
     */
    public static InputStream in;

    /**
     * Standard output stream. This stream is used for printing messages.
     */
    public static PrintStream out;

    /**
     * Standard error stream. This stream can be used to print error messages.
     * Many applications read in data from an InputStream and output messages via
     * the PrintStream out statement.  Often applications rely on command line
     * redirection to specify source and destination files.  A problem with redirecting
     * standard output is the incapability of writing messages to the screen if the
     * output has been redirected to a file.  This problem can be overcome by sending
     * some output to PrintStream out and other output to PrintStream err.  The
     * difference between PrintStream err and PrintStream out is that PrintStream
     * err is often used for displaying error messages but may be used for any purpose.
     */
    public static PrintStream err;

    static {
	try {
	    in = new BufferedInputStream(new FileInputStream(FileDescriptor.in), 128);
	    out = new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out), 128), true);
	    err = new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.err), 128), true);
	} catch (Exception e) {
	    throw new Error("can't initialize stdio");
	}
    }

    /* The security manager for the system.
     */
    private static SecurityManager security;

    /**
     * Sets the System security. This value can only be set once.
     * @param s the security manager
     * @exception SecurityException If the SecurityManager has already been set.
     */
    public static void setSecurityManager(SecurityManager s) {
	if (security != null) {
	    throw new SecurityException("SecurityManager already set");
	}
	security = s;
    }

    /**
     * Gets the system security interface.
     */
    public static SecurityManager getSecurityManager() {
	return security;
    }

    /**
     * Returns the current time in milliseconds GMT since the epoch (00:00:00
     * UTC, January 1, 1970).  It is a signed 64 bit integer, and so it will
     * not overflow until the year 292280995.
     * @see java.util.Date
     */
    public static native long currentTimeMillis();


    /** 
     * Copies an array from the source array, beginning at the
     * specified position, to the specified position of the destination array.
     * This method does not allocate memory for the destination array.  The
     * memory must already be allocated.
     * @param src	the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	the number of array elements to be copied
     * @exception ArrayIndexOutOfBoundsException If copy would cause
     *			access of data outside array bounds.
     * @exception ArrayStoreException If an element in the src array could
     *  		could not be stored into the destination array due
     *			to a type mismatch
     */
    public static native void arraycopy(Object src, int src_position,
                                        Object dst, int dst_position,
                                        int length);

    /**
     * System properties. The following properties are guaranteed to be defined:
     * <dl>
     * <dt>java.version		<dd>Java version number
     * <dt>java.vendor		<dd>Java vendor specific string
     * <dt>java.vendor.url	<dd>Java vendor URL
     * <dt>java.home		<dd>Java installation directory
     * <dt>java.class.version	<dd>Java class version number
     * <dt>java.class.path	<dd>Java classpath
     * <dt>os.name		<dd>Operating System Name
     * <dt>os.arch		<dd>Operating System Architecture
     * <dt>os.version		<dd>Operating System Version
     * <dt>file.separator	<dd>File separator ("/" on Unix)
     * <dt>path.separator	<dd>Path separator (":" on Unix)
     * <dt>line.separator	<dd>Line separator ("\n" on Unix)
     * <dt>user.name		<dd>User account name
     * <dt>user.home		<dd>User home directory
     * <dt>user.dir		<dd>User's current working directory
     * </dl>
     */
    private static Properties props;
    private static native Properties initProperties(Properties props);

    /**
     * Gets the System properties.
     */
    public static Properties getProperties() {
	if (props == null) {
	    props = initProperties(new Properties());
	}
	if (security != null) {
	    security.checkPropertiesAccess();
	}
	return props;
    }

    /**
     * Sets the System properties to the specified properties.
     * @param props the properties to be set
     */
    public static void setProperties(Properties props) {
	if (security != null) {
	    security.checkPropertiesAccess();
	}
	System.props = props;
    }
    
    /**
     * Gets the System property indicated by the specified key.
     * @param key the name of the system property
     */
    public static String getProperty(String key) {
	if (security != null) {
	    security.checkPropertyAccess(key);
	}
	if (props == null) {
	    props = initProperties(new Properties());
	}
	return props.getProperty(key);
    }
    
    /**
     * Gets the System property indicated by the specified key and def.
     * @param key the name of the system property
     * @param def the default value to use if this property is not set
     */
    public static String getProperty(String key, String def) {
	if (security != null) {
	    security.checkPropertyAccess(key); 
	}
	if (props == null) {
	    props = initProperties(new Properties());
	}
	return props.getProperty(key, def);
    }
    
    /** Obsolete. 
     * Gets an environment variable. An environment variable is a
     * system dependent external variable that has a string value.
     * @param name the name of the environment variable
     * @return 	the value of the variable, or null if the variable is
     *		not defined.
     * 
     */
    public static String getenv(String name) {
	throw new Error("getenv no longer supported, use properties and -D instead: " + name);
    }


    /**
     * Exits the virtual machine with an exit code. This method does
     * not return, use with caution.
     * @param status exit status, 0 if successful, other values indicate
     *        various error types. 
     * @see Runtime#exit
     */
    public static void exit (int status) {
	Runtime.getRuntime().exit(status);
    }

    /**
     * Runs the garbage collector.
     * @see Runtime#gc
     */
    public static void gc() {
	Runtime.getRuntime().gc();
    }

    /**
     * Runs the finalization methods of any objects pending finalization.
     * @see Runtime#gc
     */
    public static void runFinalization() {
	Runtime.getRuntime().runFinalization();
    }

    /**
     * Loads a dynamic library, given a complete path name. 
     * @param filename the file to load
     * @exception UnsatisfiedLinkError If the file does not exist.
     * @see Runtime#load
     */
    public static void load(String filename) {
	Runtime.getRuntime().load(filename);
    }

    /**
     * Loads a dynamic library with the specified library name. 
     * @param libname the name of the library
     * @exception UnsatisfiedLinkError If the library does not exist. 
     * @see Runtime#loadLibrary
     */
    public static void loadLibrary(String libname) {
	Runtime.getRuntime().loadLibrary(libname);
    }

}
