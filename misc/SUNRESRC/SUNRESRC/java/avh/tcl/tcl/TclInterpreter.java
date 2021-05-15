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

/*-
 *	Tcl Interpreter hooks.
 */

import java.util.Linker;

package tcl;

/**
 * A Tcl interpreter. 
 *
 * @author Patrick Naughton
 * @author Arthur van Hoff
 */
public class TclInterpreter {
    /*
     * When a TCL command returns, it returns a string.
     * If something went wrong then status field indicates
     * what is going on. 
     */

    /**
     * TCL_OK Command completed normally; interp->result contains the
     * command's result.
     */
    public static final int TCL_OK = 0;

    /**
     * TCL_ERROR The command couldn't be completed successfully;
     * interp->result describes what went wrong.
     */
    public static final int TCL_ERROR = 1;

    /**
     * TCL_RETURN The command requests that the current procedure return;
     * interp->result contains the procedure's return value.
     */
    public static final int TCL_RETURN = 2;

    /**
     * TCL_BREAK The command requests that the innermost loop be exited;
     * interp->result is meaningless.
     */
    public static final int TCL_BREAK = 3;

    /**
     * TCL_CONTINUE Go on to the next iteration of the current loop;
     * interp->result is meaningless.
     */
    public static final int TCL_CONTINUE = 4;

    /**
     * The interpreter.
     */
    private int interp;

    /**
     * The status of the last command.
     */
    protected int status;

    /**
     * Return the status of the last command.
     */
    public int getStatus() {
	return status;
    }

    /**
     * Return the status of the last command.
     */
    public String getStatusString() {
	switch (status) {
	  case TCL_OK: 		return "OK";
	  case TCL_ERROR: 	return "ERROR";
	  case TCL_RETURN: 	return "RETURN";
	  case TCL_BREAK: 	return "BREAK";
	  case TCL_CONTINUE: 	return "CONTINUE";
	}
	return null;
    }

    /**
     * Bind a method of the current class to a
     * Tcl command.
     */
    protected native synchronized void bind(String s);

    /**
     * Evaluate a Tcl command.
     */
    public native synchronized String eval(String s);

    /**
     * Create the interpreter.
     */
    private native void create();

    /**
     * Construct a new Tcl interpreter.
     */
    public TclInterpreter () {
	create();
    }

    static {
	Linker.loadLibrary("tcl");
    }
}
