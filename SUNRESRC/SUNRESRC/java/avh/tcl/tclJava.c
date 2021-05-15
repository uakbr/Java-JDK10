/*
 * %W% %E% Patrick Naughton
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
 *      Classtcl_TclInterpreter interface.
 */

#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
#include <string.h>

#include "tcl.h"

#include <oobj.h>
#include <interpreter.h>
#include <common_exceptions.h>
#include <javaString.h>
#include <typecodes.h>

#include "CClassHeaders/java_lang_String.h"
#include "CClassHeaders/tcl_TclInterpreter.h"


int Tcl_AppInit(Tcl_Interp * interp)
{
    if (Tcl_Init(interp) == TCL_ERROR) {
	return TCL_ERROR;
    }
    return TCL_OK;
}

static int
tcl_call(ClientData clientData, Tcl_Interp *interp,
	 int argc, char *argv[])
{
    Htcl_TclInterpreter *Tclh = (Htcl_TclInterpreter *) clientData;
    Classtcl_TclInterpreter *tcl = unhand(Tclh);
    Hjava_lang_String *ret;
    HArrayOfObject *java_args;
    struct execenv *ee = EE();
    int i, n;


    if (argc == 0) {
	interp->result = "needs a method to call";
	return TCL_ERROR;
    }

    java_args = (HArrayOfObject *) ArrayAlloc(T_CLASS, argc - 1);
    for (i = 0; i < argc-1; i++) {
	unhand(java_args)->body[i] = (Handle *)makeJavaString(argv[i], strlen(argv[i]));
    }

    tcl->status = TCL_OK;
    ret = (HString*)execute_java_dynamic_method(ee, (HObject *)Tclh, argv[0],
						"([Ljava/lang/String;)Ljava/lang/String;", 
						java_args);
    if (exceptionOccurred(ee)) {
	interp->result = "Exception";
	return TCL_ERROR;
    }

    interp->result = ret ? makeCString(ret) : 0;
    return tcl->status;
}

static int
tcl_bind(ClientData clientData, Tcl_Interp *interp,
	int argc, char *argv[])
{
    if (argc != 1) {
	interp->result = "needs a method to bind";
	return TCL_ERROR;
    }

    Tcl_CreateCommand(interp, argv[1], tcl_call,
			(ClientData) clientData, (Tcl_CmdDeleteProc *) NULL);
    return TCL_OK;
}

HString *
tcl_TclInterpreter_eval(Htcl_TclInterpreter *Tclh, HString *s)
{
    Classtcl_TclInterpreter *tcl = unhand(Tclh);
    Tcl_Interp *interp = (Tcl_Interp *) tcl->interp;
    tcl->status = TCL_OK;
    tcl->status = Tcl_Eval(interp, makeCString(s));
    return ((tcl->status == TCL_OK) && (interp->result != 0)) ?
	makeJavaString(interp->result, strlen(interp->result)) : 0;
}

void
tcl_TclInterpreter_bind(Htcl_TclInterpreter *Tclh, HString *s)
{
    Classtcl_TclInterpreter *tcl = unhand(Tclh);
    Tcl_Interp *interp = (Tcl_Interp *) tcl->interp;

    Tcl_CreateCommand(interp, makeCString(s), tcl_call,
			(ClientData) Tclh, (Tcl_CmdDeleteProc *) NULL);

}

void
tcl_TclInterpreter_create(Htcl_TclInterpreter *Tclh)
{
    Classtcl_TclInterpreter *tcl = unhand(Tclh);

    tcl->interp = (int) Tcl_CreateInterp();

    if (tcl->interp == 0) {
	SignalError(0, JAVAPKG "UnsatisfiedLinkException", "TCL");
	return;
    }
}
