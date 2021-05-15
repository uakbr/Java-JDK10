/*
 * @(#)Process.java	1.7 95/12/22 Chris Warth
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
 * An instance of class Process is returned by variants of the exec ()
 * method in class System.  From the Process instance, it is
 * possible to: get the standin and/or standout of the subprocess,
 * kill the subprocess, wait for it to terminate, and to
 * retrieve the final exit value of the process.
 * <p>
 * Dropping the last reference to a Process instance does not
 * kill the subprocess.  There is no requirement that the
 * subprocess execute asynchronously with the existing Java process.
 */
public abstract class Process 
{
    /**
     * Returns a Stream connected to the input of the child process. 
     * This stream is traditionally buffered.
     */
    abstract public OutputStream getOutputStream();
    

    /** 
     * Returns a Stream connected to the output of the child process. 
     * This stream is traditionally buffered. 
     */
    abstract public InputStream getInputStream();

    /**
     * Returns the an InputStream connected to the error stream of the child process. 
     * This stream is traditionally unbuffered.
     */
    abstract public InputStream getErrorStream();

    /**
     * Waits for the subprocess to complete.  If the subprocess has
     * already terminated, the exit value is simply returned.  If the
     * subprocess has not yet terminated the calling thread will be
     * blocked until the subprocess exits.
     *
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    abstract public int waitFor() throws InterruptedException;

   /**
    * Returns the exit value for the subprocess.
    * @exception IllegalThreadStateException If the subprocess has not yet
    * terminated.
    */
    abstract public int exitValue();

   /**
    * Kills the subprocess.
    */
    abstract public void destroy();
}
