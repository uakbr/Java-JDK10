/*
 * @(#)Runtime.java	1.14 95/12/22 Frank Yellin
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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
import java.util.StringTokenizer;


public class Runtime {
    private static Runtime currentRuntime = new Runtime();
      

    /**
     * Returns the runtime.
     */
    public static Runtime getRuntime() { 
	return currentRuntime;
    }
    
    /** Don't let anyone else instantiate this class */
    private Runtime() {}

    /* Helper for exit
     */
    private native void exitInternal(int status);

    /**
     * Exits the virtual machine with an exit code. This method does
     * not return, use with caution.
     * @param status exit status, 0 if successful, other values indicate
     *        various error types. 
     */
    public void exit(int status) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkExit(status);
	}
	exitInternal(status);
    }

    /* Helper for exec
     */
    private native Process execInternal(String cmdarray[], String envp[]) 
	 throws IOException;

    /**
     * Executes the system command specified in the parameter.
     * Returns a Process which has methods for optaining the stdin,
     * stdout, and stderr of the subprocess.  This method fails if
     * executed by untrusted code.
     *
     * @param command a specified system command
     * @return an instance of class Process
     */
    public Process exec(String command) throws IOException {
	return exec(command, null);
    }

    /**
     * Executes the system command specified in the parameter.
     * Returns a Process which has methods for optaining the stdin,
     * stdout, and stderr of the subprocess.  This method fails if
     * executed by untrusted code.
     *
     * @param command a specified system command
     * @return an instance of class Process
     */
    public Process exec(String command, String envp[]) throws IOException {
	int count = 0;
	String cmdarray[];
 	StringTokenizer st;

	st = new StringTokenizer(command);
 	count = st.countTokens();

	cmdarray = new String[count];
	st = new StringTokenizer(command);
	count = 0;
 	while (st.hasMoreTokens()) {
 		cmdarray[count++] = st.nextToken();
 	}
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkExec(cmdarray[0]);
	}
	return execInternal(cmdarray, envp);
    }

    /**
     * Executes the system command specified by cmdarray[0] with arguments
     * specified by the strings in the rest of the array.
     * Returns a Process which has methods for optaining the stdin,
     * stdout, and stderr of the subprocess.  This method fails if
     * executed by untrusted code.
     *
     * @param an array containing the command to call and its arguments
     * @param envp array containing environment in format name=value
     * @return an instance of class Process
     */

    public Process exec(String cmdarray[]) throws IOException {
	return exec(cmdarray, null);
    }

    /**
     * Executes the system command specified by cmdarray[0] with arguments
     * specified by the strings in the rest of the array.
     * Returns a Process which has methods for optaining the stdin,
     * stdout, and stderr of the subprocess.  This method fails if
     * executed by untrusted code.
     *
     * @param an array containing the command to call and its arguments
     * @param envp array containing environment in format name=value
     * @return an instance of class Process
     */

    public Process exec(String cmdarray[], String envp[]) throws IOException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkExec(cmdarray[0]);
	}
	return execInternal(cmdarray, envp);
    }


    /**
     * Returns the number of free bytes in system memory. This number
     * is not always accurate because it is just an estimation of the available
     * memory.  More memory may be freed by calling System.gc() .
     */
    public native long freeMemory();

    /**
     * Returns the total number of bytes in system memory. 
     */
    public native long totalMemory();

    /**
     * Runs the garbage collector.
     */
    public native void gc();

    /**
     * Runs the finalization methods of any objects pending finalization.
     * Usually you will not need to call this method since finalization
     * methods will be called asynchronously by the finalization thread.
     * However, under some circumstances (like running out of a finalized
     * resource) it can be useful to run finalization methods synchronously.
     */
    public native void runFinalization();

    /**
     * Enables/Disables tracing of instructions.
     * @param on	start tracing if true
     */
    public native void traceInstructions(boolean on);

    /**
     * Enables/Disables tracing of method calls.
     * @param on	start tracing if true
     */
    public native void traceMethodCalls(boolean on);

    /**
     * Initializes the linker and returns the search path for shared libraries.
     */
    private synchronized native String initializeLinkerInternal();
    private native String buildLibName(String pathname, String filename);

    /* Helper for load and loadLibrary */
    private native boolean loadFileInternal(String filename);


    /** The paths searched for libraries */
    private String paths[];

    private void initializeLinker() {
	String ldpath = initializeLinkerInternal();
	char c = System.getProperty("path.separator").charAt(0);
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
    

    /**
     * Loads a dynamic library, given a complete path name. If you use this
     * from java_g it will automagically insert "_g" before the ".so".
     *
     * Example: <code>Runtime.getRuntime().load("/home/avh/lib/libX11.so");</code>
     * @param filename the file to load
     * @exception UnsatisfiedLinkError If the file does not exist.
     * @see #getRuntime
     */
    public synchronized void load(String filename) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkLink(filename);
	}
        if (!loadFileInternal(filename)) {
	    throw new UnsatisfiedLinkError(filename);
	}
    }

    /**
     * Loads a dynamic library with the specified library name. The 
     * call to LoadLibrary() should be made in the static 
     * initializer of the first class that is loaded. Linking in the 
     * same library more than once is ignored.
     * @param libname the name of the library
     * @exception UnsatisfiedLinkError If the library does not exist. 
     */
    public synchronized void loadLibrary(String libname) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkLink(libname);
	}
        if (paths == null) {
            initializeLinker();
	}
	for (int i = 0 ; i < paths.length ; i++) {
	    String tempname = buildLibName(paths[i], libname);
	    if (loadFileInternal(tempname)) {
	        return;
	    }
	}
	// Oops, it failed
        throw new UnsatisfiedLinkError("no " + libname + 
					   " in shared library path");
    }

    /**
     * Localize an input stream. A localized input stream will automatically
     * translate the input from the local format to UNICODE. 
     */
    public InputStream getLocalizedInputStream(InputStream in) {
	return in;
    }

    /**
     * Localize an output stream. A localized output stream will automatically
     * translate the output from UNICODE to the local format.
     */
    public OutputStream getLocalizedOutputStream(OutputStream out) {
	return out;
    }
}
