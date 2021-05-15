/*
 * @(#)Class.java	1.14 95/01/31  
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
 * object in the system is an instance of some class, and for each class
 * there is one of these descriptor objects. A class descriptor is not 
 * modifiable at runtime.<p>
 * The following example uses a Class object to print the class name
 * of an object:
 * <pre>
 *	void printClassName(Object obj) {
 *	    System.out.println("The class of " + obj +
 *			       " is " + obj.getClass().getName());
 *	}
 * </pre>
 * @version 	1.14, 31 Jan 1995
 */
public final
class Class {
    /**
     * Make sure nobody instantiates this class
     */
    private Class() {}
    
    /**
     * Returns the runtime class descriptor for the specified class.
     * For example the following code fragment returns the runtime
     * class descriptor for the class named java.lang.Thread:
     * <pre>
     *		Class t = Class.forName("java.lang.Thread")
     * </pre>
     * @param className	the fully qualified name of the desired class
     * @return 		the class
     * @exception	NoClassDefFoundException the class could not be found
     */
    public static native Class forName(String className);

    /**
     * Creates an new instance of this class.
     * @return 		the new instance
     * @exception	IncompatibleTypeException you can't instanciate interfaces
     * @exception	OutOfMemoryException out of memory
     */
    public native Object newInstance();

    /**
     * Returns the name of this class.
     */
    public native String getName();

    /**
     * Returns the superclass of this class.
     */
    public native Class getSuperclass();

    /**
     * Returns the interfaces of this class.
     * @return		the interfaces of this class, an array of length
     *			0 is returned if the class implements no interfaces.
     */
    public native Class getInterfaces()[];

    /**
     * Returns the class loader of this class.
     * @return	the class loader of this class, null is returned
     *		if the class has no class loader.
     * @see	ClassLoader
     */
    public native ClassLoader getClassLoader();

    /**
     * Returns true if this class is an interface, false otherwise.
     * @return	a boolean indicating whether this class is an interface
     */
    public native boolean isInterface();

    /**
     * Returns the name of this class or this interface. The word 
     * "class" is prepended if it is a class; the word "interface is
     * is prepended if it is an interface.
     * @return	a string representing this class or interface
     */
    public String toString() {
	return (isInterface() ? "interface " : "class ") + getName();
    }
}

