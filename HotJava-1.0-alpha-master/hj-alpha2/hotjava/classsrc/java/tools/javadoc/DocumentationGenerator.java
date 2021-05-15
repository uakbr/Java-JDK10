/*
 * @(#)DocumentationGenerator.java	1.23 95/03/20 Arthur van Hoff
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

import java.util.*;
import java.io.*;
import java.tools.java.*;
import java.tools.javac.SourceClass;
import java.tools.javac.BatchEnvironment;
import java.tools.tree.LocalField;

class DocumentationGenerator implements Constants {
    static ClassDeclaration currentClass;
    static ClassDeclaration exDecl;
    static BatchEnvironment env;
    static File destDir;

    static boolean shouldDocument(ClassDefinition c) {
	return true;
    }
    static boolean shouldDocument(FieldDefinition f) {
	return f.isPublic() || f.isProtected();
    }

    static void allMethods0(Vector v, ClassDefinition def) {
      outer:
	for (FieldDefinition f = def.getFirstField() ; f != null ; f = f.getNextField()) {
	    if (f.isMethod() && (!f.isConstructor()) && shouldDocument(f)) {
		for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
		    FieldDefinition vf = (FieldDefinition)e.nextElement();
		    if (vf.getName().equals(f.getName()) && vf.getType().equalArguments(f.getType())) {
			continue outer;
		    }
		}
		v.addElement(f);
	    }
	}
	if ((def.getSuperClass() != null) && (def.getSuperClass().getClassDefinition(env).getSuperClass() != null)) {
	    allMethods0(v, def.getSuperClass().getClassDefinition(env));
	}
	ClassDeclaration intf[] = def.getInterfaces();
	for (int i = intf.length ; i-- > 0 ;) {
	    allMethods0(v, intf[i].getClassDefinition(env));
	}
    }
    
    static Vector allMethods0(ClassDefinition def) {
	Vector v = new Vector();
	allMethods0(v, def);
	return v;
    }

    static void allVariables0(Vector v, ClassDefinition def) {
      outer:
	for (FieldDefinition f = def.getFirstField() ; f != null ; f = f.getNextField()) {
	    if (f.isVariable() && (!f.isConstructor()) && shouldDocument(f) && !v.contains(f)) {
		v.addElement(f);
	    }
	}
	if ((def.getSuperClass() != null) && (def.getSuperClass().getClassDefinition(env).getSuperClass() != null)) {
	    allVariables0(v, def.getSuperClass().getClassDefinition(env));
	}
	/*
	ClassDeclaration intf[] = def.getInterfaces();
	for (int i = intf.length ; i-- > 0 ;) {
	    allVariables0(v, intf[i].getClassDefinition(env));
	}
	*/
    }

    static Vector allVariables0(ClassDefinition def) {
	Vector v = new Vector();
	allVariables0(v, def);
	return v;
    }

    static Vector allVariables(ClassDefinition def) {
	Vector v = new Vector();
	for (FieldDefinition f = def.getFirstField() ; f != null ; f = f.getNextField()) {
	    if (f.isVariable() && shouldDocument(f)) {
		v.addElement(f);
	    }
	}
	return v;
    }

    static Vector allConstructors(ClassDefinition def) {
	Vector v = new Vector();
	for (FieldDefinition f = def.getFirstField() ; f != null ; f = f.getNextField()) {
	    if (f.isConstructor() && shouldDocument(f)) {
		v.addElement(f);
	    }
	}
	return v;
    }

    static Vector allMethods(ClassDefinition def) {
	Vector v = new Vector();
	for (FieldDefinition f = def.getFirstField() ; f != null ; f = f.getNextField()) {
	    if (f.isMethod() && (!f.isConstructor()) && shouldDocument(f)) {
		v.addElement(f);
	    }
	}
	return v;
    }

    static boolean isException(ClassDeclaration c) {
	if (exDecl == null) {
	    exDecl = env.getClassDeclaration(Identifier.lookup("java.lang.Exception"));
	}
	return c.getClassDefinition(env).subClassOf(env, exDecl);
    }
 
    static PrintStream openFile(Identifier pkg, String name) {
	if (destDir != null) {
	    name = destDir.getPath() + File.separator + name;
	}

	FileOutputStream file;
	try {
	    file = new FileOutputStream(name);
	} catch (IOException ee) {
	    new File(new File(name).getParent()).mkdirs();
	    file = new FileOutputStream(name);
	}
	return new PrintStream(new BufferedOutputStream(file));
    }

    static String firstSentence(ClassDefinition c) {
	return firstSentence(c.getDocumentation());
    }
    static String firstSentence(FieldDefinition f) {
	return firstSentence(getDocumentation(f));
    }
    static String firstSentence(String s) {
	if (s == null) {
	    return "";
	}

	int len = s.length();
	boolean period = false;
	for (int i = 0 ; i < len ; i++) {
	    switch (s.charAt(i)) {
	      case '.':
		period = true;
		break;
	      case '@':
		return s.substring(0, i);
	      case ' ':
	      case '\t':
	      case '\n':
		if (period) {
		    return s.substring(0, i);
		}
		break;
	      default:
		period = false;
	    }
	}
	return s;
    }

    static String getDocumentation(FieldDefinition f) {
	String doc = f.getDocumentation();
	if ((doc != null) || f.isVariable() || f.isConstructor()) {
	    return doc;
	}
	while ((f != null) && (doc == null)) {
	    ClassDeclaration sc = f.getClassDefinition().getSuperClass();
	    if (sc == null) {
		break;
	    }
	    f = sc.getClassDefinition(env).findMethod(env, f.getName(), f.getType());
	    if (f != null) {
		doc = f.getDocumentation();
	    }
	}
	return (doc != null) ? firstSentence(doc) : null;
    }

    static String refString(String ref, Object content) {
	return "<a href=\"" + ref + "\">" + content + "</a>";
    }

    static String classString(ClassDeclaration c) {
	return classString(c, "_top_", c.getName().getName());
    }
    static String classString(ClassDeclaration c, Object content) {
	return classString(c, "_top_", content);
    }
    static String classString(ClassDeclaration c, Object where, Object content) {
	if (c.equals(currentClass)) {
	    return refString("#" + where, content);
	} else {
	    return refString(c.getName().toString() + ".html#" + where, content);
	}
    }
    static String pkgString(Identifier pkg) {
	return pkgString(pkg, pkg);
    }
    static String pkgString(Identifier pkg, Object content) {
	return refString(pkg.toString() + ".html", content);
    }

    static String seeString(ClassDefinition c, String str) {
	while ((str.length() > 0) && (str.charAt(0) == ' ') || (str.charAt(0) == '\t')) {
	    str = str.substring(1);
	}
	if (str.startsWith("<")) {
	    return str;
	}
	String what = null;
	int i = str.indexOf('#');
	if (i >= 0) {
	    what = str.substring(i + 1);
	    str = str.substring(0, i);
	}

	ClassDeclaration decl;
	try {
	    decl = env.getClassDeclaration(((SourceClass)c).getImports().resolve(env, Identifier.lookup(str)));
	} catch (ClassNotFound ee) {
	    decl = env.getClassDeclaration(Identifier.lookup(str));
	}
	if (what == null) {
	    return classString(decl);
	}
	i = what.indexOf('(');
	if (i >= 0) {
	    return classString(decl, what, what.substring(0, i));
	}
	return classString(decl, what, what);
    }

    static String typeString(Type t) {
	switch (t.getTypeCode()) {
	  case TC_VOID:		return "void";
	  case TC_BOOLEAN:	return "boolean";
	  case TC_BYTE:		return "byte";
	  case TC_CHAR:		return "char";
	  case TC_SHORT:	return "short";
	  case TC_INT:		return "int";
	  case TC_LONG:		return "long";
	  case TC_FLOAT:	return "float";
	  case TC_DOUBLE:	return "double";
	  case TC_CLASS:	return classString(env.getClassDeclaration(t));
	  case TC_ARRAY:	return typeString(t.getElementType());
	}
	return "error";
    }

    static String imgString(String img, int width, int height) {
	return "<img src=\"images/" + img + "\" width=" + width + " height=" + height + ">";
    }
    static String imgString(String img) {
	return "<img src=\"images/" + img + "\">";
    }

    static String modString(FieldDefinition f) {
	String str;
	if (f.isPublic()) {
	    str = "  public";
	} else if (f.isProtected()) {
	    str = "  protected";
	} else if (f.isPrivate()) {
	    str = "  private";
	} else {
	    str = "";
	}
	if (f.isFinal()) {
	    str += (str.length() > 0) ? " final" : "final";
	}
	if (f.isStatic()) {
	    str += (str.length() > 0) ? " static" : "static";
	}
	if (f.isSynchronized()) {
	    str += (str.length() > 0) ? " synchronized" : "synchronized";
	}
	if (f.isAbstract()) {
	    str += (str.length() > 0) ? " abstract" : "abstract";
	}
	/*
	if (f.isNative()) {
	    str += (str.length() > 0) ? " native" : "native";
	}
	*/
	if (f.isThreadsafe()) {
	    str += (str.length() > 0) ? " threadsafe" : "threadsafe";
	}
	if (f.isTransient()) {
	    str += (str.length() > 0) ? " transient" : "transient";
	}
	return str;
    }

    static void genButtons(PrintStream out, ClassDefinition c, ClassDeclaration prev, ClassDeclaration next) {
	// Global references
	out.println("<pre>");
	out.print(refString("packages.html", "All Packages"));
	out.print("    ");
	out.print(pkgString(c.getName().getQualifier(), "This Package"));
	out.print("    ");
	if (prev != null) {
	    out.print(classString(prev, "Previous"));
	} else {
	    out.print(pkgString(c.getName().getQualifier(), "Previous"));
	}
	out.print("    ");
	if (next != null) {
	    out.print(classString(next, "Next"));
	} else {
	    out.print(pkgString(c.getName().getQualifier(), "Next"));
	}
	out.println("</pre>");
    }

    static void genVariableIndex(PrintStream out, ClassDefinition def, FieldDefinition f) {
	out.println("<dt>");

	if (f.isStatic()) {
	    out.println(imgString("blue-ball-small.gif", 6, 6));
	} else {
	    out.println(imgString("magenta-ball-small.gif", 6, 6));
	}
	
	out.println("<b>");
	out.println(classString(f.getClassDeclaration(), f.getName(), f.getName()));
	out.println("</b>");
	out.println("<dd>");
	if (!def.equals(f.getClassDefinition())) {
	    out.print("Inherited from ");
	    out.print(classString(f.getClassDeclaration()));
	    out.println(".");
	}
	out.println(firstSentence(f));
    }

    static void genVariableDocumentation(PrintStream out, FieldDefinition f) {
	if (f.isStatic()) {
	    out.println("<a name=\"" + f.getName() + "\">");
	    out.println(imgString("blue-ball.gif", 12, 12));
	    out.println("</a>");
	    out.println("<b>");
	    out.println(f.getName());
	    out.println("</b>");
	} else {
	    out.println("<a name=\"" + f.getName() + "\">");
	    out.println(imgString("magenta-ball.gif", 12, 12));
	    out.println("</a>");
	    out.println("<b>");
	    out.println(f.getName());
	    out.println("</b>");
	}

	out.println("<pre>");
	out.print(modString(f));
	out.print(" ");
	out.print(typeString(f.getType()));
	out.print(" ");
	out.print(f.getName());
	for (int i = f.getType().getArrayDimension() ; i > 0 ; i--) {
	    out.print("[]");
	}
	out.println();
	out.println("</pre>");

	String doc = f.getDocumentation();
	if (doc != null) {
	    out.println("<dl>");
	    out.println("<dd>");
	    int what = 0;
	    for (StringTokenizer e1 = new StringTokenizer(doc, "\n") ; e1.hasMoreTokens() ;) {
		String tok = e1.nextToken();
		if (tok.startsWith("@")) {
		    if (tok.startsWith("@see")) {
			if (what == 0) {
			    out.println("<dl>");
			}
			if (what != 1) {
			    what = 1;
			    out.println("<dt>");
			    out.println("<b>See Also:</b>");
			    out.println("<dd>");
			} else {
			    out.println(",");
			}
			out.print(seeString(f.getClassDefinition(), tok.substring(5)));
		    } else {
			System.out.println("oops: " + tok);
		    }
		} else {
		    out.println(tok);
		}
	    }
	    if (what == 1) {
		out.println();
	    }
	    if (what != 0) {
		out.println("</dl>");
	    }
	    out.println("</dl>");
	}
    }

    static void endSeeAlso(PrintStream out, boolean seenAlso, FieldDefinition f) {
	return;
    }

    static void genMethodIndex(PrintStream out, ClassDefinition def, FieldDefinition f) {
	Identifier cnm = f.getClassDeclaration().getName().getName();

	out.println("<dt>");
	if (f.isConstructor()) {
	    out.println(imgString("yellow-ball-small.gif", 6, 6));
	    out.println(f.getType().typeString(classString(f.getClassDeclaration(), f.getType().typeString(cnm.toString(), false, false), "<b>" + cnm + "</b>"), true, false));
	} else if (f.isStatic()) {
	    out.println(imgString("green-ball-small.gif", 6, 6));
	    out.println(f.getType().typeString(classString(f.getClassDeclaration(), f.getType().typeString(f.getName().toString(), false, false), "<b>" + f.getName() + "</b>"), true, false));
	} else {
	    out.println(imgString("red-ball-small.gif", 6, 6));
	    out.println(f.getType().typeString(classString(f.getClassDeclaration(), f.getType().typeString(f.getName().toString(), false, false), "<b>" + f.getName() + "</b>"), true, false));
	}
	out.println("<dd>");
	if (!def.equals(f.getClassDefinition())) {
	    out.print("Inherited from ");
	    out.print(classString(f.getClassDeclaration()));
	    out.println(".");
	}
	out.println(firstSentence(f));
    }
    
    static void genMethodDocumentation(PrintStream out, FieldDefinition f) {
	Identifier cnm = f.getClassDeclaration().getName().getName();
	if (f.isConstructor()) {
	    out.println("<a name=\"" + f.getType().typeString(cnm.toString(), false, false) + "\">");
	    out.println(imgString("yellow-ball.gif", 12, 12));
	    out.println("</a>");
	    out.println("<b>");
	    out.println(cnm);
	    out.println("</b>");
	} else if (f.isStatic()) {
	    out.println("<a name=\"" + f.getType().typeString(f.getName().toString(), false, false) + "\">");
	    out.println(imgString("green-ball.gif", 12, 12));
	    out.println("</a>");
	    out.println("<a name=\"" + f.getName() + "\">");
	    out.println("<b>");
	    out.println(f.getName());
	    out.println("</b>");
	    out.println("</a>");
	} else {
	    out.println("<a name=\"" + f.getType().typeString(f.getName().toString(), false, false) + "\">");
	    out.println(imgString("red-ball.gif", 12, 12));
	    out.println("</a>");
	    out.println("<a name=\"" + f.getName() + "\">");
	    out.println("<b>");
	    out.println(f.getName());
	    out.println("</b>");
	    out.println("</a>");
	}
	
	out.println("<pre>");
	String sig = modString(f) + " ";
	if (f.isConstructor()) {
	    sig = sig + cnm + "(";
	} else {
	    sig = sig + typeString(f.getType().getReturnType()) + " " + f.getName() + "(";
	}
	out.print(sig);

	// compute the ACTUAL length of the signature,
	// not counting the stuff in <>'s.
	boolean inbracket = false;
	int siglen = 0;
	for (int i = 0 ; i < sig.length() ; i++) {
	    if (inbracket) {
		if (sig.charAt(i) == '>') {
		    inbracket = false;
		}
	    } else {
		if (sig.charAt(i) == '<') {
		    inbracket = true;
		} else {
		    siglen++;
		}
	    }
	}
	Type args[] = f.getType().getArgumentTypes();
	if (f.getArguments() == null) {
	    System.out.println(f);
	}
	Enumeration e = f.getArguments().elements();
	if (!f.isStatic()) {
	    e.nextElement();
	}
	for (int i = 0; i < args.length ; i++) {
	    if (i > 0) {
		out.println(",");
		for (int j = siglen ; j > 0 ; j--) {
		    out.print(" ");
		}
	    }
	    out.print(typeString(args[i]));
	    LocalField l = (LocalField)e.nextElement();
	    out.print(" ");
	    out.print(l.getName());
	    for (int j = args[i].getArrayDimension() ; j > 0 ; j--) {
		out.print("[]");
	    }
	}
	out.print(")");
	for (int i = f.getType().getArrayDimension() ; i > 0 ; i--) {
	    out.print("[]");
	}
	out.println();
	out.println("</pre>");

	FieldDefinition overrides = null;
	if ((!f.isStatic()) && (!f.isConstructor()) && (f.getClassDefinition().getSuperClass() != null)) {
	    overrides = f.getClassDefinition().getSuperClass().getClassDefinition(env).findMethod(env, f.getName(), f.getType());
	}

	String doc = getDocumentation(f);
	if (doc != null) {
	    out.println("<dl>");
	    out.println("<dd>");
	    int what = 0;
	    for (StringTokenizer e1 = new StringTokenizer(doc, "\n") ; e1.hasMoreTokens() ;) {
		String tok = e1.nextToken();
		if (tok.startsWith("@")) {
		    if (tok.startsWith("@param")) {
			if (what == 0) {
			    out.println("<dl>");
			}
			if (what != 1) {
			    what = 1;
			    out.println("<dt>");
			    out.println("<b>Parameters:</b>");
			}
			what = 1;
			tok = tok.substring(7);

			int i = 0;
			for (; i < tok.length() ; i++) {
			    if ((tok.charAt(i) == ' ') || (tok.charAt(i) == '\t')) {
				break;
			    }
			}
			out.println("<dd>");
			out.println(tok.substring(0, i));
			out.println("-");
			out.println(tok.substring(i));
		    } else if (tok.startsWith("@return")) {
			if (what == 0) {
			    out.println("<dl>");
			}
			if (what != 2) {
			    what = 2;
			    out.println("<dt>");
			    out.println("<b>Returns:</b>");
			    out.println("<dd>");
			}
			out.println(tok.substring(8));
		    } else if (tok.startsWith("@see")) {
			if (what == 0) {
			    out.println("<dl>");
			}
			if (overrides != null) {
			    out.println("<dt>");
			    out.println("<b>Overrides:</b>");
			    out.println("<dd>");
			    out.print(classString(overrides.getClassDeclaration(), overrides.getType().typeString(overrides.getName().toString(), false, false), overrides.getName()) + " in class " + classString(overrides.getClassDeclaration()));
			    overrides = null;
			}
			if (what != 3) {
			    what = 3;
			    out.println("<dt>");
			    out.println("<b>See Also:</b>");
			    out.println("<dd>");
			} else {
			    out.println(",");
			}
			out.print(seeString(f.getClassDefinition(), tok.substring(5)));
		    } else if (tok.startsWith("@exception")) {
			if (what == 0) {
			    out.println("<dl>");
			}
			what = 1;
			tok = tok.substring(11);
			while ((tok.length() > 0) && ((tok.charAt(0) == ' ') || (tok.charAt(0) == '\t'))) {
			    tok = tok.substring(1);
			}

			int i = 0;
			for (; i < tok.length() ; i++) {
			    if ((tok.charAt(i) == ' ') || (tok.charAt(i) == '\t')) {
				break;
			    }
			}
			
			ClassDeclaration ee;
			try {
			    ee = env.getClassDeclaration(((SourceClass)f.getClassDefinition()).getImports().resolve(env, Identifier.lookup(tok.substring(0, i))));
			} catch (ClassNotFound eee) {
			    ee = env.getClassDeclaration(Identifier.lookup(tok.substring(0, i)));
			}
			out.println("<dt>");
			out.println("<b>Throws:</b> " + classString(ee));
			out.println("<dd>");
			out.println(tok.substring(i));
		    } else {
			System.out.println("oops: " + tok);
		    }
		} else {
		    out.println(tok);
		}
	    }

	    if (what == 3) {
		out.println();
	    }
	    if (overrides != null) {
		if (what == 0) {
		    out.println("<dl>");
		    what = 4;
		}
		out.println("<dt>");
		out.println("<b>Overrides:</b>");
		out.println("<dd>");
		out.print(classString(overrides.getClassDeclaration(), overrides.getType().typeString(overrides.getName().toString(), false, false), overrides.getName()) + " in class " + classString(overrides.getClassDeclaration()));
		overrides = null;
	    }
	    if (what != 0) {
		out.println("</dl>");
	    }
	    out.println("</dl>");
	    out.println("<p>");
	}
    }

    static void seeClasses(ClassDeclaration c, Vector v) {
	ClassDefinition def = c.getClassDefinition(env);
	if ((def.getSuperClass() != null) && !v.contains(def.getSuperClass())) {
	    v.addElement(def.getSuperClass());
	    seeClasses(def.getSuperClass(), v);
	}
	ClassDeclaration intf[] = def.getInterfaces();
	for (int i = 0 ; i < intf.length ; i++) {
	    if (!v.contains(intf[i])) {
		v.addElement(intf[i]);
		seeClasses(intf[i], v);
	    }
	}
    }

    static int endSeeAlso(PrintStream out, int what, ClassDefinition c) {
	if (c.getName().equals(idJavaLangObject)) {
	    return what;
	}
	if (what == 0) {
	    out.println("<dl>");
	}
	if (what != 3) {
	    out.println("<dt>");
	    out.println("<b>See Also:</b>");
	    out.println("<dd>");
	} 
	Vector v = new Vector();
	seeClasses(c.getClassDeclaration(), v);
	for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
	    if (what != 3) {
		what = 3;
	    } else {
		out.println(",");
	    }
	    out.print(classString((ClassDeclaration)e.nextElement()));
	}
	return what;
    }

    static int genSuperClasses(PrintStream out, ClassDeclaration from, ClassDeclaration c) {
	if (c == null) {
	    return 0;
	}
	int n = genSuperClasses(out, from, c.getClassDefinition(env).getSuperClass());
	if (n > 0) {
	    for (int i = 1 ; i < n ; i++) {
		out.print("        ");
	    }
	    out.println("   |");
	    for (int i = 1 ; i < n ; i++) {
		out.print("        ");
	    }
	    out.print("   +----");
	}
	if (from.equals(c)) {
	    out.println(c.getName());
	} else {
	    out.println(classString(c, "_top_", c.getName()));
	}
	return n + 1;
    }
    
    static void genClassDocumentation(ClassDefinition c, ClassDeclaration prev, ClassDeclaration next) {

	currentClass = c.getClassDeclaration();

	System.out.print("generating documentation for ");
	System.out.println(c);

	// open the file
	PrintStream out = openFile(c.getName().getQualifier(), c.getName() + ".html");

	// HTML Header
	out.println("<html>");
	out.println("<head>");
	// this anchor is a hack to stop HotJava from jumping down one line
	out.println("<a name=\"_top_\"></a>");
	out.println("<title>");
	out.print(c.isInterface() ? "Interface " : "Class ");
	out.println(c.getName());
	out.println("</title>");
	out.println("</head>");
	out.println("<body>");

	// Global references
	out.println("<a name=\"_top_\"></a>");
	if (!idNull.equals(c.getName().getQualifier())) {
	    genButtons(out, c, prev, next);
	    out.println("<hr>");
	}

	// Document header
	out.println("<h1>");
	out.print(c.isInterface() ? "Interface " : "Class ");
	out.println(c.getName());
	out.println("</h1>");
	if (!c.isInterface()) {
	    out.println("<pre>");
	    genSuperClasses(out, currentClass, currentClass);
	    out.println("</pre>");
	    out.println("<hr>");
	}
	
	out.println("<dl>");
	out.println("<dt>");
	if (c.isPublic()) {
	    out.print("public ");
	}
	if (c.isFinal()) {
	    out.print("final ");
	}
	out.print(c.isInterface() ? "interface " : "class ");
	out.println("<b>");
	out.print(c.getName().getName());
	out.println("</b>");
	ClassDeclaration sup  = c.getSuperClass();
	if (sup != null) {
	    out.println("<dt>");
	    out.print("extends ");
	    out.println(classString(sup));
	}
	ClassDeclaration intf[] = c.getInterfaces();
	if (intf.length > 0) {
	    out.println("<dt>");
	    out.print(c.isInterface() ? "extends " : "implements ");
	    for (int i = 0 ; i < intf.length ; i++) {
		if (i > 0) {
		    out.print(", ");
		}
		out.print(classString(intf[i]));
	    }
	    out.println();
	}
	out.println("</dl>");

	int what = 0;
	String cdoc = c.getDocumentation();
	if (cdoc != null) {
	    for (StringTokenizer e1 = new StringTokenizer(cdoc, "\n") ; e1.hasMoreTokens() ;) {
		String tok = e1.nextToken();
		if (tok.startsWith("@")) {
		    if (tok.startsWith("@author")) {
			if (what == 0) {
			    out.println("<dl>");
			}
			if (what != 1) {
			    what = 1;
			    out.println("<dt>");
			    out.println("<b>Author:</b>");
			    out.println("<dd>");
			} else {
			    out.println(",");
			}
			out.print(tok.substring(8));
		    } else if (tok.startsWith("@version")) {
			if (what == 0) {
			    out.println("<dl>");
			}
			if (what != 2) {
			    what = 2;
			    out.println("<dt>");
			    out.println("<b>Version:</b>");
			}
			out.println("<dd>");
			out.println(tok.substring(9));
		    } else if (tok.startsWith("@see")) {
			if (what == 0) {
			    out.println("<dl>");
			}
			if (what != 3) {
			    what = 3;
			    out.println("<dt>");
			    out.println("<b>See Also:</b>");
			    out.println("<dd>");
			} else {
			    out.println(",");
			}
			out.print(seeString(c, tok.substring(5)));
		    } else {
			System.out.println("oops: " + tok);
		    }
		} else {
		    out.println(tok);
		}
	    }
	}
	if (what != 0) {
	    out.println("</dl>");
	}

	if (c.isPublic() || idNull.equals(c.getName().getQualifier())) {

	    out.println("<hr>");
	    out.println("<a name=\"index\"></a>");

	    // Generate lists
	    Vector variables = allVariables(c);
	    Vector constructors = allConstructors(c);
	    Vector methods = allMethods(c);
	    int count;


	    // Variables index
	    if (variables.size() > 0) {
		FieldDefinition f[] = new FieldDefinition[variables.size()];
		variables.copyInto(f);
		sort(f);

		out.println("<h2>");
		out.println(imgString("variable-index.gif", 207, 38));
		out.println("</h2>");

		out.println("<dl>");
		for (int i = 0 ; i < f.length ; i++) {
		    genVariableIndex(out, c, f[i]);
		}
		out.println("</dl>");
	    }

	    // Constructor index
	    if (constructors.size() > 0) {
		FieldDefinition f[] = new FieldDefinition[constructors.size()];
		constructors.copyInto(f);

		out.println("<h2>");
		out.println(imgString("constructor-index.gif", 275, 38));
		out.println("</h2>");

		out.println("<dl>");
		for (int i = 0 ; i < f.length ; i++) {
		    genMethodIndex(out, c, f[i]);
		}
		out.println("</dl>");
	    }

	    // Method index
	    if (methods.size() > 0) {
		FieldDefinition f[] = new FieldDefinition[methods.size()];
		methods.copyInto(f);
		sort(f);

		out.println("<h2>");
		out.println(imgString("method-index.gif", 207, 38));
		out.println("</h2>");

		out.println("<dl>");
		for (int i = 0 ; i < f.length ; i++) {
		    genMethodIndex(out, c, f[i]);
		}
		out.println("</dl>");
	    }
	    
	    // Variables
	    count = 0;
	    for (Enumeration e = variables.elements() ; e.hasMoreElements() ;) {
		FieldDefinition f = (FieldDefinition)e.nextElement();
		if (f.getClassDefinition().equals(c)) {
		    if (count++ == 0) {
			out.println("<a name=\"variables\"></a>");
			out.println("<h2>");
			out.println(imgString("variables.gif", 153, 38));
			out.println("</h2>");

		    }
		    genVariableDocumentation(out, f);
		}
	    }

	    // Constructors
	    count = 0;
	    for (Enumeration e = constructors.elements() ; e.hasMoreElements() ;) {
		FieldDefinition f = (FieldDefinition)e.nextElement();
		if (f.getClassDefinition().equals(c)) {
		    if (count++ == 0) {
			out.println("<a name=\"constructors\"></a>");
			out.println("<h2>");
			out.println(imgString("constructors.gif", 231, 38));
			out.println("</h2>");
			out.println("<a name=\"" + c.getName().getName() + "\"></a>");

		    }
		    genMethodDocumentation(out, f);
		}
	    }

	    // Methods
	    count = 0;
	    for (Enumeration e = methods.elements() ; e.hasMoreElements() ;) {
		FieldDefinition f = (FieldDefinition)e.nextElement();
		if (f.getClassDefinition().equals(c)) {
		    if (count++ == 0) {
			out.println("<a name=\"methods\"></a>");
			out.println("<h2>");
			out.println(imgString("methods.gif", 151, 38));
			out.println("</h2>");

		    }
		    genMethodDocumentation(out, f);
		}
	    }
	} else {
	    out.println("<p>");
	    out.println("<em>");
	    out.println("This class is not public and can therefore not be used outside this package.");
	    out.println("</em>");
	}

	// Global references
	if (!idNull.equals(c.getName().getQualifier())) {
	    out.println("<hr>");
	    genButtons(out, c, prev, next);
	}

	// the end
	out.println("</body>");
	out.println("</html>");
	out.close();
    }

    static void sort(FieldDefinition f[]) {
	boolean done;
	do {
	    done = true;
	    for (int i = f.length - 1 ; i > 0 ; i--) {
		if (f[i - 1].getName().toString().compareTo(f[i].getName().toString()) > 0) {
		    FieldDefinition def = f[i];
		    f[i] = f[i-1];
		    f[i-1] = def;
		    done = false;
		}
	    }
	} while (!done);
    }

    static void sort(ClassDeclaration c[]) {
	boolean done;
	do {
	    done = true;
	    for (int i = c.length - 1 ; i > 0 ; i--) {
		if (c[i - 1].getName().toString().compareTo(c[i].getName().toString()) > 0) {
		    ClassDeclaration decl = c[i];
		    c[i] = c[i-1];
		    c[i-1] = decl;
		    done = false;
		}
	    }
	} while (!done);
    }

    static void genPackageDocumentation(Identifier pkg) {
	currentClass = null;

	// Generate pakage documentation
	PrintStream out = openFile(pkg, pkg.toString() + ".html");

	out.println("<html>");
	out.println("<head>");
	out.println("<title>");
	out.print("Package ");
	out.println(pkg);
	out.println("</title>");
	out.println("</head>");
	out.println("<body>");

	out.println("<a name=\"_top_\"></a>");
	out.println("<pre>");
	out.print(refString("packages.html", "All Packages"));
	out.println("</pre>");
	out.println("<hr>");

	out.println("<h1>");
	out.print("package ");
	out.println(pkg);
	out.println("</h1>");

	int intfCount = 0;
	int classCount = 0;
	int exceptCount = 0; 
	// Make sure that every class definition that ever is going to be 
	// loaded is loaded now.
	while (true) {
	    boolean changed = false;
	    for (Enumeration e = env.getClasses() ; e.hasMoreElements() ;) {
		ClassDeclaration decl = (ClassDeclaration)e.nextElement();
		if (!decl.isDefined()) {
		    decl.getClassDefinition(env);
		    changed = true;
		}
	    }
	    if (!changed) 
		break;
	}
	for (Enumeration e = env.getClasses() ; e.hasMoreElements() ;) {
	    ClassDeclaration decl = (ClassDeclaration)e.nextElement();
	    if (shouldDocument(decl.getClassDefinition(env)) &&
		decl.getName().getQualifier().equals(pkg)) {
		if (decl.getClassDefinition(env).isInterface()) {
		    intfCount++;
		} else if (isException(decl)) {
		    exceptCount++;
		} else {
		    classCount++;
		}
	    }
	}
	ClassDeclaration intfDecls[] = new ClassDeclaration[intfCount];
	ClassDeclaration classDecls[] = new ClassDeclaration[classCount];
	ClassDeclaration exceptDecls[] = new ClassDeclaration[exceptCount];
	for (Enumeration e = env.getClasses() ; e.hasMoreElements() ;) {
	    ClassDeclaration decl = (ClassDeclaration)e.nextElement();
	    if (shouldDocument(decl.getClassDefinition(env)) &&
		decl.getName().getQualifier().equals(pkg)) {
		if (decl.getClassDefinition(env).isInterface()) {
		    intfDecls[--intfCount] = decl;
		} else if (isException(decl)) {
		    exceptDecls[--exceptCount] = decl;
		} else {
		    classDecls[--classCount] = decl;
		}
	    }
	}

	if (intfDecls.length > 0) {
	    sort(intfDecls);

	    out.println("<h2>");
	    out.println(imgString("interface-index.gif", 257, 38));
	    out.println("</h2>");

	    out.println("<menu>");
	    for (int i = 0 ; i < intfDecls.length ; i++) {
		ClassDefinition def = intfDecls[i].getClassDefinition(env);
		if (def.isInterface() && def.isPublic()) {
		    out.println("<li>");
		    out.println(classString(intfDecls[i]));
		    genClassDocumentation(def, (i > 0) ? intfDecls[i-1] : null,
					  (i+1 < intfDecls.length) ? intfDecls[i+1] : null);
		}
	    }
	    out.println("</menu>");
	}
	if (classDecls.length > 0) {
	    sort(classDecls);
	    out.println("<h2>");
	    out.println(imgString("class-index.gif", 216, 37));
	    out.println("</h2>");
	    out.println("<menu>");
	    for (int i = 0 ; i < classDecls.length ; i++) {
		ClassDefinition def = classDecls[i].getClassDefinition(env);
		if ((!def.isInterface()) && def.isPublic() && !isException(classDecls[i])) {
		    out.println("<li>");
		    out.println(classString(classDecls[i]));
		    genClassDocumentation(def, (i > 0) ? classDecls[i-1] : null,
					  (i+1 < classDecls.length) ? classDecls[i+1] : null);
		}
	    }
	    out.println("</menu>");
	}
	if (exceptDecls.length > 0) {
	    sort(exceptDecls);
	    out.println("<h2>");
	    out.println(imgString("exception-index.gif", 284, 38));
	    out.println("</h2>");
	    out.println("<menu>");
	    for (int i = 0 ; i < exceptDecls.length ; i++) {
		ClassDefinition def = exceptDecls[i].getClassDefinition(env);
		if ((!def.isInterface()) && def.isPublic() && isException(exceptDecls[i])) {
		    out.println("<li>");
		    out.println(classString(exceptDecls[i]));
		    genClassDocumentation(def, (i > 0) ? exceptDecls[i-1] : null,
					  (i+1 < exceptDecls.length) ? exceptDecls[i+1] : null);
		}
	    }
	    out.println("</menu>");
	}

	// the end
	out.println("</body>");
	out.println("</html>");
	out.close();
    }
}
