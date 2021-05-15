/*
 * @(#)WWWClassLoader.java	1.36 95/04/04 Jonathan Payne
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

package net.www.html;

import java.util.*;
import java.io.*;
import awt.*;
import net.*;
import net.www.http.*;
import net.www.html.*;

public final
class WWWClassLoader extends ClassLoader {
    private static String urlpath[] = 
	{ /* "", */ "classes/", "/classes/" };
    private URL source;
    public URL ctx;		    
    Hashtable classes = new Hashtable();

    public WWWClassLoader(URL url) {
	ctx = url;
	source = new URL(new String(url.protocol),
			 new String(url.host),
			 new String(url.file));
    }

    /**
     * Load a class from an input stream
     */
    private Class loadClass(InputStream is) {
	byte	bytes[] = new byte[4096];
	byte	buffer[] = new byte[1024];
	int	n;
	int	totalBytes = 0;

	while ((n = is.read(buffer, 0, buffer.length)) >= 0) {
	    if (totalBytes + n >= bytes.length) {
		byte	newbytes[] = new byte[((bytes.length + n) * 3) / 2];

		System.arraycopy(bytes, 0, newbytes, 0, totalBytes);
		bytes = newbytes;
	    }
	    System.arraycopy(buffer, 0, bytes, totalBytes, n);
	    totalBytes += n;
	}

	return defineClass(bytes, 0, totalBytes);
    }

    private static Hashtable nonlocalPackages = new Hashtable();

    /**
     * Check if a package is local. The package name is
     * seperated by /'s.
     */
    public synchronized static boolean localPackage(String pkg) {


	if (nonlocalPackages.get(pkg) != null) {
	    return false;
	}

	// Check if the package occurs in the classpath
	String str = System.getenv("CLASSPATH");
	for (int i = str.indexOf(':') ; i >= 0 ; str = str.substring(i + 1), i = str.indexOf(':')) {
	    if (new File(str.substring(0, i) + "/" + pkg).isDirectory()) {
		return true;
	    }
	}
	if ((str.length() > 0) && new File(str + "/" + pkg).isDirectory()) {
		return true;
	}
	nonlocalPackages.put(pkg, pkg);
	return false;
    }

    /**
     * Get the actual name of the class. We need to do
     * some name mangling for backward compatibility.
     */
    String actualName(String name) {
	if (name.startsWith("oak.")) {
	    return "java." + name.substring(4);
	} else if (name.startsWith("webrunner.")) {
	    return "browser." + name.substring(10);
	}
	return name;
    }

    /**
     * Load a class from this class loader.
     */
    public Class loadClass(String name) {
	return loadClass(name, true);
    }

    /**
     * Load and resolve a class.
     */
    protected Class loadClass(String name, boolean resolve) {
	name = actualName(name);

	Class cl = (Class)classes.get(name);
	if (cl == null) {
	    try {
		return Class.forName(name);
	    } catch (Exception e) {}

	    cl = findClass(name);
	}

	if ((cl != null) && resolve) {
	    try {
		resolveClass(cl);
	    } catch (Exception e) {
		throw e;
	    }
	}
	return cl;
    }

    /**
     * This method finds a class. The returned class
     * may be unresolved. This method has to be synchronized
     * to avoid two threads loading the same class at the same time.
     * Must be called with the actual class name.
     */
    private synchronized Class findClass(String name) {
	Class cl = (Class)classes.get(name);
	if (cl == null) {
	    System.out.println(Thread.currentThread().getName() + " find class " + name);

	    int i = name.lastIndexOf('.');
	    if ((i >= 0) && localPackage(name.substring(0, i))) {
		throw new NoClassDefFoundException(name);
	    }

	    // skip the first entry if the context is "/" to avoid a
	    // duplicate probe
	    int start_index = ctx.file.equals("/") ? 1 : 0;

	    for (i = start_index; i < urlpath.length; i++) {
		String cpath = urlpath[i];
		InputStream is = null;

		URL url = new URL(ctx, cpath + name.replace('.', '/') + ".class");

		if (Firewall.getAppletRestriction() &&
		    !Firewall.verifyAppletLoading(this)) {
		    String msg = "Security restriction on " + url.toExternalForm();
		    throw new IOException(msg);
		}

		System.out.println("Opening stream to: " + url.toExternalForm() + " to get " + name);
		try {
		    is = url.openStream();
		} catch (FileNotFoundException e) {
		    // It is not an error to not find the class file here, 
		    // just try another path.
		} catch (IOException e) {
		}
		if (is != null) {
		    try {
			cl = loadClass(is);
			classes.put(name, cl);
			is.close();
			break;
		    } catch (FileFormatException e) {
			// There is very little we can do in this case
			// so we print a detailed message (with
			// corrective action) and rethrow the
			// exception.  
			System.err.println("File format exception when reading \"" + url.toExternalForm() + "\".");
			System.err.println("Try recompiling this class file from the source.");
			is.close();
			throw e;
		    } catch (Exception e) {
			is.close();
			throw e;
		    }
		}
	    }
	} 
	return cl;
    }

}
