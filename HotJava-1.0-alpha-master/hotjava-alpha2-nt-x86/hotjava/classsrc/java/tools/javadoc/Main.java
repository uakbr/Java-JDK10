/*
 * @(#)Main.java	1.5 95/01/31 Arthur van Hoff
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

package java.tools.javadoc;

import java.tools.java.*;
import java.tools.javac.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.OutputStreamBuffer;
import java.io.FileOutputStream;
import java.io.File;

/**
 * Main program of the Java compiler
 */

public final
class Main extends DocumentationGenerator implements Constants {
    /**
     * Name of the program
     */
    public static final String program = "javadoc";

    /**
     * Top level error message
     */
    static void error(String msg) {
	System.out.println(program + ": " + msg);
    }
    
    /**
     * Usage
     */
    static void usage() {
	System.out.println("use: " + program + " [-g][-O][-debug][-depend][-nowarn][-verbose][-classpath path][-nowrite][-d dir] class...");
    }
    
    /**
     * Main program
     */
    public static void main(String argv[]) {
	String classPathString = System.getenv("CLASSPATH");
	int flags = F_WARNINGS;
	int tm = System.nowMillis();
	Vector packages = new Vector();
	Vector classes = new Vector();
	boolean nowrite = false;

	// Parse arguments
	for (int i = 0 ; i < argv.length ; i++) {
	    if (argv[i].equals("-g")) {
		flags &= ~F_OPTIMIZE;
		flags |= F_DEBUG;
	    } else if (argv[i].equals("-O")) {
		flags &= ~F_DEBUG;
		flags |= F_OPTIMIZE | F_DEPENDENCIES;
	    } else if (argv[i].equals("-nowarn")) {
		flags &= ~F_WARNINGS;
	    } else if (argv[i].equals("-debug")) {
		flags |= F_DUMP;
	    } else if (argv[i].equals("-depend")) {
		flags |= F_DEPENDENCIES;
	    } else if (argv[i].equals("-verbose")) {
		flags |= F_VERBOSE;
	    } else if (argv[i].equals("-nowrite")) {
		nowrite = true;
	    } else if (argv[i].equals("-classpath")) {
		if ((i + 1) < argv.length) {
		    classPathString = argv[++i];
		} else {
		    error("-classpath requires argument");
		    usage();
		    return;
		}
	    } else if (argv[i].equals("-d")) {
		if ((i + 1) < argv.length) {
		    destDir = new File(argv[++i]);
		    if (!destDir.exists()) {
			error(destDir.getPath() + " does not exist");
			return;
		    }
		} else {
		    error("-d requires argument");
		    usage();
		    return;
		}
	    } else if (argv[i].startsWith("-")) {
		error("invalid flag: " + argv[i]);
	    } else if (argv[i].endsWith(".java")) {
		classes.addElement(argv[i]);
	    } else {
		packages.addElement(argv[i]);
	    }
	}
	if ((packages.size() == 0) && (classes.size() == 0)) {
	    usage();
	    return;
	}

	// Create class path and environment
	String classPath[] = ClassPath.path2list(classPathString == null ? "." : classPathString);
	ResourceStrings strings = new ResourceStrings(classPath);
	BatchEnvironment env = new BatchEnvironment(classPath, strings);
	env.flags |= flags;

	DocumentationGenerator.env = env;

	try {
	    // Parse class files
	    for (Enumeration e1 = classes.elements() ; e1.hasMoreElements() ;) {
		File file = new File((String)e1.nextElement());
		try {
		    env.parseFile(file);
		} catch (IOException ee) {
		    error("can't read: " + file.getPath());
		}
	    }

	    // Figure out which classes where encountered
	    classes = new Vector();
	    for (Enumeration e1 = env.getClasses() ; e1.hasMoreElements() ;) {
		ClassDeclaration decl = (ClassDeclaration)e1.nextElement();
		if (decl.isDefined() && (decl.getClassDefinition() instanceof SourceClass)) {
		    classes.addElement(decl.getClassDefinition());
		}
	    }

	    // Parse input files
	    for (Enumeration e1 = packages.elements() ; e1.hasMoreElements() ;) {
		Identifier pkg = Identifier.lookup((String)e1.nextElement());
		ClassPath p = new ClassPath(classPath, pkg);
		for (Enumeration e2 = p.getSourceFiles() ; e2.hasMoreElements() ;) {
		    File file = (File)e2.nextElement();
		    try {
			env.parseFile(file);
		    } catch (IOException ee) {
			error("can't read: " + file.getPath());
		    }
		}
	    }

	    // Generate documentation for packages
	    for (Enumeration e1 = packages.elements() ; e1.hasMoreElements() ;) {
		Identifier pkg = Identifier.lookup((String)e1.nextElement());
		genPackageDocumentation(pkg);
	    }

	    // Generate documentation for classes
	    for (Enumeration e1 = classes.elements() ; e1.hasMoreElements() ;) {
		genClassDocumentation(((ClassDefinition)e1.nextElement()), null, null);
	    }

	} catch (Exception ee) {
	    ee.printStackTrace();
	    error("an exception has occurred, please contact x47242.");
	    env.flushErrors();
	    System.exit(2);
	}

	env.flushErrors();

	int status = 0;
	if (env.nerrors > 0) {
	    System.out.print(env.nerrors);
	    if (env.nerrors > 1) {
		System.out.print(" errors");
	    } else {
		System.out.print(" error");
	    }
	    if (env.nwarnings > 0) {
		System.out.print(", " + env.nwarnings);
		if (env.nwarnings > 1) {
		    System.out.print(" warnings");
		} else {
		    System.out.print(" warning");
		}
	    }
	    System.out.println();
	    status = 1;
	} else {
	    if (env.nwarnings > 0) {
		System.out.print(env.nwarnings);
		if (env.nwarnings > 1) {
		    System.out.println(" warnings");
		} else {
		    System.out.println(" warning");
		}
	    }
	}

	// We're done
	if (env.verbose()) {
	    tm = System.nowMillis() - tm;
	    System.out.println("[done in " + tm + "ms]");
	}
	
	System.exit(status);
    }
}
