/*
 * Patrick Naughton
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

import tcl.*;

class TclTest extends TclInterpreter {

    TclTest() {
	bind("foo");
	bind("bar");
    }

    public String eval(String s) {
	String ret = super.eval(s);
	if (status != TCL_OK) {
	    System.out.println("eval(" + s + ") failed " + getStatusString());
	}
	return ret;
    }

    String foo(String args[]) {
	System.out.println("FOO!");
	for (int i = 0; i < args.length; i++)
	    System.out.println(i + ": " + args[i]);
	return "This is foo's result!";
    }

    String bar(String args[]) {
	System.out.println("BAR!!!");
	for (int i = 0; i < args.length; i++)
	    System.out.println(i + ": " + args[i]);
	return null;
    }

    public static void main(String args[]) {
	TclInterpreter tcl = new TclTest();

	tcl.eval("set a 33");
	tcl.eval("set b 22");
        tcl.eval("set c [expr $a + $b]");
	tcl.eval("puts $c");
	tcl.eval("puts [foo 1 2 3 4]");
	tcl.eval("baz not defined");
	tcl.eval("bar 5 6 7 8");
    }
}
