/* Header for class tcl_TclInterpreter */

#ifndef _Included_tcl_TclInterpreter
#define _Included_tcl_TclInterpreter
typedef struct Classtcl_TclInterpreter {
#define tcl_TclInterpreter_TCL_OK 0
#define tcl_TclInterpreter_TCL_ERROR 1
#define tcl_TclInterpreter_TCL_RETURN 2
#define tcl_TclInterpreter_TCL_BREAK 3
#define tcl_TclInterpreter_TCL_CONTINUE 4
    long interp;
    long status;
} Classtcl_TclInterpreter;

HandleTo(tcl_TclInterpreter);
extern void tcl_TclInterpreter_bind(struct Htcl_TclInterpreter *,struct Hjava_lang_String *);
extern struct Hjava_lang_String *tcl_TclInterpreter_eval(struct Htcl_TclInterpreter *,struct Hjava_lang_String *);
extern void tcl_TclInterpreter_create(struct Htcl_TclInterpreter *);
#endif
