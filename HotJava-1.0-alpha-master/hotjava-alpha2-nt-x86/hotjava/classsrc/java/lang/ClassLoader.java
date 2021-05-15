/*
 * @(#)ClassLoader.java	1.14 95/02/21 Arthur van Hoff
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

import java.io.InputStream;

/**
 * ClassLoader is an abstract class that can be used to define a policy
 * for loading Java classes into the runtime environment. By default,
 * the runtime system  loads classes that originate as files by reading 
 * them from the directory defined by the <tt>CLASSPATH</tt> environment
 * variable (this is platform dependent). The default mechanism does not involve
 * a class loader. <p>
 *
 * However, some classes may not originate from a file; they could be
 * loaded from some other source, e.g., the network. Classes loaded
 * from the network are an array of bytes. A ClassLoader can be used to
 * tell the runtime system to convert an array of bytes into an instance
 * of class Class.
 * This conversion information is passed to the runtime using the defineClass()
 * method.<p>
 *
 * Classes that are created through the defineClass() mechanism can 
 * reference other classes by name. To resolve those names the runtime
 * system calls to the ClassLoader that originally created the class.
 * The runtime system calls the abstract method loadClass() to load
 * the referenced classes.<p>
 * <pre>
 * 	ClassLoader loader = new NetworkClassLoader(host, port);
 *  	Object main = loader.loadClass("Main").newInstance();
 *	....
 * </pre>
 *
 * The NetworkClassLoader subclass must define the method loadClass() to 
 * load a class from the network. Once it has downloaded the bytes
 * that make up the class it should use definedClass to create a class
 * instance. A sample implementation could be:
 * <pre>
 *	class NetworkClassLoader {
 *	    String host;
 *	    int port;
 *	    Hashtable cache = new Hashtable();
 *
 *	    private byte loadClassData(String name)[] {
 *		// load the class data from the connection
 *		...
 *	    }
 *
 *	    public synchronized Class loadClass(String name) {
 *	        Class c = cache.get(name);
 *		if (c == null) {
 *		    byte data[] = loadClassData(name);
 *		    cache.put(name, defineClass(data, 0, data.length));
 *		}
 *		return c;
 *	    }
 *	}
 * </pre>
 * @see		Class
 * @version 	1.14, 21 Feb 1995
 * @author	Arthur van Hoff
 */


public
class ClassLoader {
    /**
     * Constructs a new class loader and initializes it.
     */
    protected ClassLoader() {
	init();
    }

    /**
     * Resolves the specified name to a class. loadClass() is called by the 
     * interpreter.
     * As an abstract method, loadClass() must be defined in a subclass of 
     * ClassLoader. Using a Hashtable avoids loading the same class more than
     * once. It is an abstract method, it must therefore be defined in a subclass.
     * @param	name	the name of the desired class
     * @param   resolve true if the class needs to be resolved
     * @return		the resulting class, or null if it was not found
     * @see		java.util.Hashtable
     */
    protected abstract Class loadClass(String name, boolean resolve);

    /**
     * Converts an array of bytes to an instance of class Class. Before the
     * class can be used it must be resolved.
     * @param	data	the bytes that make up the class
     * @param	offset	the start ofset of the class data
     * @param	length	the length of the class data
     * @return		the class object which was created from the data
     * @exception FileFormatException the data does not contain a valid class
     * @see 		ClassLoader#loadClass
     * @see 		ClassLoader#resolveClass
     */
    protected native final Class defineClass(byte data[], int offset, int length);

    /**
     * Resolves classes referenced by this class. This must be done before the
     * class can be used. Class names referenced by the resulting class are
     * resolved by calling loadClass().
     * @param	c	the class to be resolved
     * @see 		ClassLoader#defineClass
     */
    protected native final void resolveClass(Class c);

    /**
     * Returns the classes that are currently on the execution stack.
     * This is a hack and will probably not be supported in the future.
     */
    public static native Class getClassContext()[];

    /**
     * Initializes the class loader.
     */
    private native void init();
}
