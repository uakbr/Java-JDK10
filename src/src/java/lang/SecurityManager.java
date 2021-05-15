/*
 * @(#)SecurityManager.java	1.26 95/12/22  
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

import java.io.FileDescriptor;

/**
 * An abstract class that can be subclassed
 * to implement a security policy. It allows the inspection of
 * the classloaders on the execution stack.
 *
 * @author	Arthur van Hoff
 * @version 	1.26, 12/22/95
 */
public abstract
class SecurityManager {
    protected boolean inCheck;

    /** 
     * Returns whether there is a security check in progress.
     */
    public boolean getInCheck() {
	return inCheck;
    }

    /**
     * Constructs a new SecurityManager.
     * @exception SecurityException If the security manager cannot be
     * created.
     */
    protected SecurityManager() {
	if (System.getSecurityManager() != null) {
	    throw new SecurityException("can't create SecurityManager");
	}
    }
    
    /**
     * Gets the context of this Class.  
     */
    protected native Class[] getClassContext();

    /**
     * The current ClassLoader on the execution stack.
     */
    protected native ClassLoader currentClassLoader();

    /**
     * Return the position of the stack frame containing the
     * first occurrence of the named class.
     * @param name classname of the class to search for
     */
    protected native int classDepth(String name);

    /**
     * 
     */
    protected native int classLoaderDepth();

    /**
     * Returns true if the specified String is in this Class. 
     * @param name the name of the class
     */
    protected boolean inClass(String name) {
	return classDepth(name) >= 0;
    }

    /**
     * Returns a boolean indicating whether or not the current ClassLoader
     * is equal to null.
     */
    protected boolean inClassLoader() {
	return currentClassLoader() != null;
    }

    /**
     * Returns an implementation-dependent Object which encapsulates
     * enough information about the current execution environment
     * to perform some of the security checks later.
     */
    public Object getSecurityContext() {
	return null;
    }

    /**
     * Checks to see if the ClassLoader has been created.
     * @exception SecurityException If a security error has occurred.
     */
    public void checkCreateClassLoader() {
	throw new SecurityException();
    }
    
    /**
     * Checks to see if the specified Thread is allowed to modify
     * the Thread group.
     * @param g the Thread to be checked
     * @exception SecurityException If the current Thread is not
     * allowed to access this Thread group.
     */
    public void checkAccess(Thread g) {
	throw new SecurityException();
    }

    /**
     * Checks to see if the specified Thread group is allowed to 
     * modify this group.
     * @param g the Thread group to be checked
     * @exception  SecurityException If the current Thread group is 
     * not allowed to access this Thread group.
     */
    public void checkAccess(ThreadGroup g) {
	throw new SecurityException();
    }

    /**
     * Checks to see if the system has exited the virtual 
     * machine with an exit code.
     * @param status exit status, 0 if successful, other values
     * indicate various error types.
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkExit(int status) {
	throw new SecurityException();
    }

    /**
     * Checks to see if the system command is executed by 
     * trusted code.
     * @param cmd the specified system command
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkExec(String cmd) {
	throw new SecurityException();
    }

    /**
     * Checks to see if the specified linked library exists.
     * @param lib the name of the library
     * @exception  SecurityException If the library does not exist.
     */
    public void checkLink(String lib) {
	throw new SecurityException();
    }

    /**
     * Checks to see if an input file with the specified
     * file descriptor object gets created.
     * @param fd the system dependent file descriptor
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkRead(FileDescriptor fd) {
	throw new SecurityException();
    }

    /**
     * Checks to see if an input file with the specified system dependent
     * file name gets created.
     * @param file the system dependent file name
     * @exception  SecurityException If the file is not found.
     */
    public void checkRead(String file) {
	throw new SecurityException();
    }

    /**
     * Checks to see if the current context or the indicated context are
     * both allowed to read the given file name.
     * @param file the system dependent file name
     * @param context the alternate execution context which must also
     * be checked
     * @exception  SecurityException If the file is not found.
     */
    public void checkRead(String file, Object context) {
	throw new SecurityException();
    }

    /**
     * Checks to see if an output file with the specified 
     * file descriptor object gets created.
     * @param fd the system dependent file descriptor
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkWrite(FileDescriptor fd) {
	throw new SecurityException();
    }

    /**
     * Checks to see if an output file with the specified system dependent
     * file name gets created.
     * @param file the system dependent file name
     * @exception  SecurityException If the file is not found.
     */
    public void checkWrite(String file) {
	throw new SecurityException();
    }

    /**
     * Checks to see if a file with the specified system dependent
     * file name can be deleted.
     * @param file the system dependent file name
     * @exception  SecurityException If the file is not found.
     */
    public void checkDelete(String file) {
	throw new SecurityException();
    }

    /**
     * Checks to see if a socket has connected to the specified port on the
     * the specified host.
     * @param host the host name port to connect to
     * @param port the protocol port to connect to
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkConnect(String host, int port) {
	throw new SecurityException();
    }

    /**
     * Checks to see if the current execution context and the indicated
     * execution context are both allowed to connect to the indicated
     * host and port.
     */
    public void checkConnect(String host, int port, Object context) {
	throw new SecurityException();
    }

    /**
     * Checks to see if a server socket is listening to the specified local
     * port that it is bounded to.
     * @param port the protocol port to connect to
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkListen(int port) {
	throw new SecurityException();
    }

    /**
     * Checks to see if a socket connection to the specified port on the 
     * specified host has been accepted.
     * @param host the host name to connect to
     * @param port the protocol port to connect to
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkAccept(String host, int port) {
	throw new SecurityException();
    }

    /**
     * Checks to see who has access to the System properties.
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkPropertiesAccess() {
	throw new SecurityException();
    }

    /**
     * Checks to see who has access to the System property named by <i>key</i>.
     * @param key the System property that the caller wants to examine
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkPropertyAccess(String key) {
	throw new SecurityException();
    }

    /**
     * Checks to see who has access to the System property named by <i>key</i>
     * and <i>def</i>.
     * @param key the System property that the caller wants to examine
     * @param def default value to return if this property is not defined
     * @exception  SecurityException If a security error has occurred.
     */
    public void checkPropertyAccess(String key, String def) {
	throw new SecurityException();
    }

    /**
     * Checks to see if top-level windows can be created by the
     * caller. A return of false means that the window creation is
     * allowed but the window should indicate some sort of visual
     * warning. Returning true means the creation is allowed with no
     * special restrictions. To disallow the creation entirely, this
     * method should throw a SecurityException.
     * @param window the new window that's being created.
     */
    public boolean checkTopLevelWindow(Object window) {
	return false;
    }

    /**
     * Checks to see if an applet can access a package.
     */
    public void checkPackageAccess(String pkg) {
	throw new SecurityException();
    }

    /**
     * Checks to see if an applet can define classes in a package.
     */
    public void checkPackageDefinition(String pkg) {
	throw new SecurityException();
    }

    /**
     * Checks to see if an applet can set a networking-related object factory.
     */
    public void checkSetFactory() {
	throw new SecurityException();
    }
}	
