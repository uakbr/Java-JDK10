/*
 * @(#)System.java	1.22 95/05/10 Arthur van Hoff
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

/**
 * This class provides access to system functionality.
 * One of the more useful things provided by this class
 * are the standard i/o streams. They are used to do
 * printing. For example:
 * <pre>
 *	System.out.println("Hello World!");
 * </pre>
 * @version 	1.22, 10 May 1995
 * @author	Arthur van Hoff
 */
public final
class System {
    /** Don't let anyone instantiate this class */
    private System() {
    }

    /**
     * Standard input stream.
     */
    public static InputStream in =
        new BufferedInputStream(new FileInputStream(0), 128);

    /**
     * Standard output stream. This stream lets you print messages.
     */
    public static PrintStream out =
        new PrintStream(new BufferedOutputStream(new FileOutputStream(1), 128), true);

    /**
     * Standard error stream. This stream can be used to print error messages.
     */
    public static PrintStream err =
        new PrintStream(new BufferedOutputStream(new FileOutputStream(2), 128), true);

    /**
     * Returns the current time in milliseconds.
     * This is obsolete, use currentTimeMillis instead.
     * This method essentially always overflows.  It is only useful
     * for measuring intervals less that 1<<21 seconds long.
     * @return the time in milliseconds
     * @see java.util.currentTimeMillis
     */
    public static native int nowMillis();

    /**
     * Returns the current time in seconds GMT since the epoch (00:00:00
     * UTC, January 1, 1970).
     * This is obsolete, use currentTimeMillis instead.
     * This method is susceptible to overflow.  It's got until 2038,
     * but that's cutting things a bit fine: intermediate results in
     * time calculations occasionally go awry.
     * @see java.util.currentTimeMillis
     */
    public static native int currentTime();

    /**
     * Returns the current time in milliseconds GMT since the epoch (00:00:00
     * UTC, January 1, 1970).  It is a signed 64 bit integer, and so won't
     * overflow until the year 292280995.
     * @see java.util.Date
     */
    public static native long currentTimeMillis();

    /**
     * Returns number of free bytes in system memory. This number
     * is not always accurate.
     * @return bytes free in system memory
     */
    public static native int freeMemory();

    /**
     * Returns the total number of bytes in system memory. 
     * @return bytes in system memory
     */
    public static native int totalMemory();

    /**
     * Runs the garbage collector. Usually it is not necessary to call
     * this method since there is an asynchronous garbage collector which
     * runs whenever the system is idle.  The garbage collector also runs
     * automatically when object allocation fails.
     */
    public static native void gc();

    /**
     * Runs the finalization methods of any objects pending finalization.
     * Usually it is not necessary to call this method since finalization
     * methods will be called asynchronously by a finalization thread.
     * However, because finalized resources are of the user's choosing,
     * there is no built-in equivalent of the garbage collector running
     * when the system runs out of memory.  With runFinalization(), users
     * can build equivalent functionality into their resource allocators.
     */
    public static native void runFinalization();

    /**
     * Copies an array of booleans.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException Copy would cause
     *			access of data outside array bounds
     */
    public static void arraycopy(boolean src[], int srcpos, boolean dest[],
				 int destpos, int length) {
	boolean_arraycopy(src, srcpos, dest, destpos, length);
    }

    /**
     * Copies an array of bytes.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException Copy would cause
     *			access of data outside array bounds
     */
    public static void arraycopy(byte src[], int srcpos, byte dest[],
				 int destpos, int length) {
	byte_arraycopy(src, srcpos, dest, destpos, length);
    }

    /**
     * Copies an array of characters.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException Copy would cause
     *			access of data outside array bound
     */
    public static void arraycopy(char src[], int srcpos, char dest[],
				 int destpos, int length) {
	char_arraycopy(src, srcpos, dest, destpos, length);
    }

    /**
     * Copies an array of shorts.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException Copy would cause
     *			access of data outside array bounds
     */
    public static void arraycopy(short src[], int srcpos, short dest[],
				 int destpos, int length) {
	short_arraycopy(src, srcpos, dest, destpos, length);
    }

    /**
     * Copies an array of integers.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException Copy would cause
     *			access of data outside array bounds
     */
    public static void arraycopy(int src[], int srcpos, int dest[],
				 int destpos, int length) {
	int_arraycopy(src, srcpos, dest, destpos, length);
    }

    /**
     * Copies an array of longs.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException Copy would cause
     *			access of data outside array bounds
     */
    public static void arraycopy(long src[], int srcpos, long dest[],
				 int destpos, int length) {
	long_arraycopy(src, srcpos, dest, destpos, length);
    }

    /**
     * Copies an array of floats.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException Copy would cause
     *			access of data outside array bounds
     */
    public static void arraycopy(float src[], int srcpos, float dest[],
				 int destpos, int length) {
	float_arraycopy(src, srcpos, dest, destpos, length);
    }

    /**
     * Copies an array of doubles.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException Copy would cause
     *			access of data outside array bounds
     */
    public static void arraycopy(double src[], int srcpos, double dest[],
				 int destpos, int length) {
	double_arraycopy(src, srcpos, dest, destpos, length);
    }

    /**
     * Copies an array of objects.
     * @param src		the source data
     * @param srcpos	start position in the source data
     * @param dest	the destination
     * @param destpos	start position in the destination data
     * @param length	length of the data to be copied
     * @exception ArrayIndexOutOfBoundsException copy would cause
     *			access of data outside array bounds
     * @exception IncompatibleTypeException Source and destination array types
     *			do not match
     */
    public static void arraycopy(Object src[], int srcpos, Object dest[],
				 int destpos, int length) {
	object_arraycopy(src, srcpos, dest, destpos, length);
    }


    /* Yes we need these, the method names need to be different so that they
     * can resolve to different symbols. This will be much easier once we
     * have arrays as objects ;-)
     */
    private static native void boolean_arraycopy(boolean src[], int srcpos, boolean dest[],
						 int destpos, int length);
    private static native void byte_arraycopy(byte src[], int srcpos, byte dest[],
					      int destpos, int length);
    private static native void short_arraycopy(short src[], int srcpos, short dest[],
					       int destpos, int length);
    private static native void char_arraycopy(char src[], int srcpos, char dest[],
					      int destpos, int length);
    private static native void int_arraycopy(int src[], int srcpos, int dest[],
					     int destpos, int length);
    private static native void long_arraycopy(long src[], int srcpos, long dest[],
					      int destpos, int length);
    private static native void float_arraycopy(float src[], int srcpos, float dest[],
					       int destpos, int length);
    private static native void double_arraycopy(double src[], int srcpos, double dest[],
						int destpos, int length);
    private static native void object_arraycopy(Object src[], int srcpos, Object dest[],
						int destpos, int length);
    
    /**
     * Exits the interpreter with an exit code. This method does
     * not return, use with caution.
     * @param status	exit status, 0 is success
     */
    public static native void exit(int status);

    /* Obsolete...
     * Helper function for exec and execout
     */
    private static native int exec0(String command, int DoRead);

    /**
     * Executes a system command.  Returns a FileInputStream connected to the
     * standard output of the command.  Fails if executed directly or
     * indirectly by code loaded over the net.
     */
    public static InputStream execin(String command) {
	return new BufferedInputStream(new FileInputStream(exec0(command, 1)))
;
    }

    /**
     * Executes a system command.  Returns a FileOutputStream connected to the
     * standard input of the command.  Fails if executed directly or
     * indirectly by code loaded over the net.
     */
    public static OutputStream execout(String command) {
	return new BufferedOutputStream(new FileOutputStream(exec0(command, 0)));
    }

    /**
     * Executes a system command.  No redirection.
     */
    public static void exec(String command) {
	exec0(command, -1);
    }

    /**
     * Gets an environment variable. An environment variable is a
     * system dependent external variable that has a string value.
     * @param var	the name of an environment variable.
     * @return 	the value of the variable, or null if the variable is
     *		is not defined
     */
    public static native String getenv(String var);

    /**
     * Get the current working directory.
     * @return the system dependent name of the current directory.
     */
    public static native String getCWD();

    /**
     * Get the name of the operating system.  In the rare event that
     * you need to do something OS dependent, this is how you find
     * out what you're running on.
     * @return the system dependent name of current operating system.
     */
    public static native String getOSName();

    /**
     * Enables/Disables tracing of instructions.
     * @param on	start tracing if non-zero
     */
    public static native void traceInstructions(int on);

    /**
     * Enables/Disables tracing of method calls.
     * @param on	start tracing if non-zero
     */
    public static native void traceMethodCalls(int on);

    /**
     * Enables/Disables profiling of method calls.
     * @param on	start profiling if non-zero
     */
    public static native void profile(int on);

    /**
     * Dumps profiling data.
     */
    public static native void dumpProfile();

    /**
     * Dumps the state of all monitors.
     */
    public static native void dumpMon(Object obj);
}
