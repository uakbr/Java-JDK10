/*
 * @(#)Class.java	1.26 95/12/21  
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

/**
 * Class objects contain runtime representations of classes.  Every
 * object in the system is an instance of some Class, and for each Class
 * there is one of these descriptor objects. A Class descriptor is not 
 * modifiable at runtime.<p>
 * The following example uses a Class object to print the Class name
 * of an object:
 * <pre>
 *	void printClassName(Object obj) {
 *	    System.out.println("The class of " + obj +
 *			       " is " + obj.getClass().getName());
 *	}
 * </pre>
 * @version 	1.26, 12/21/95
 */
public final
class Class {
    /**
     * Make sure nobody instantiates this class
     */
    private Class() {}
    
    /**
     * Returns the runtime Class descriptor for the specified Class.
     * For example, the following code fragment returns the runtime
     * Class descriptor for the Class named java.lang.Thread:
     * <pre>
     *		Class t = Class.forName("java.lang.Thread")
     * </pre>
     * @param className	the fully qualified name of the desired Class
     * @exception	ClassNotFoundException If the Class could not be found.
     */
    public static native Class forName(String className) throws ClassNotFoundException;

    /**
     * Creates a new instance of this Class.
     * @return 		the new instance of this Class.
     * @exception	InstantiationException If you try to instantiate
     *                  an abstract class or an interface, or if
     *			the instantiation fails for some other reason.
     * @exception       IllegalAccessException If the class or initializer
     *                  is not accessible.
     */
    public native Object newInstance() 
	 throws InstantiationException, IllegalAccessException;

    /**
     * Returns the name of this Class.
     */
    public native String getName();

    /**
     * Returns the superclass of this Class.
     */
    public native Class getSuperclass();

    /**
     * Returns the interfaces of this Class. An array 
     * of length 0 is returned if this Class implements no interfaces.
     */
    public native Class getInterfaces()[];

    /**
     * Returns the Class loader of this Class.  Returns null
     *		if this Class does not have a Class loader.
     * @see	ClassLoader
     */
    public native ClassLoader getClassLoader();

    /**
     * Returns a boolean indicating whether or not this Class is an 
     * interface.
     */
    public native boolean isInterface();

    /**
     * Returns the name of this class or interface. The word 
     * "class" is prepended if it is a Class; the word "interface"
     * is prepended if it is an interface.
     */
    public String toString() {
	return (isInterface() ? "interface " : "class ") + getName();
    }
}

