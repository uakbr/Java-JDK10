/*
 * @(#)interpreter.h	1.37 95/02/22  
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

/*
 * Definitions for the interperter	6/27/91
 */

#ifndef _INTERPRETER_H_
#define _INTERPRETER_H_

#include <standardlib.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <bool.h>
#include <config.h>

extern char *progname;
extern unsigned char *codebuf;
extern unsigned char **mainstktop;
extern int verbose;
extern int verbosegc;
extern int noasyncgc;
extern ClassClass *classclass;	/* class java/lang/Class */
extern ClassClass *classobject;	/* class java/lang/Object */

enum { VERIFY_NONE, VERIFY_REMOTE, VERIFY_ALL };

extern int verifyclasses;

#define FINALIZER_METHOD_NAME "finalize"
#define FINALIZER_METHOD_SIGNATURE "()V"

#ifdef TRACING
 extern int trace;
 extern int tracem;
 extern char *opnames[256];
#else
# define trace  0
# define tracem 0
#endif


/* Get a constant pool index, from a pc */
#define GET_INDEX(ptr) (((int)((ptr)[0]) << 8) | (ptr)[1])

extern char *Object2CString(Handle *);

#define METHOD_FLAG_BITS 5
#define FLAG_MASK       ((1<<METHOD_FLAG_BITS)-1)  /* valid flag bits */
#define METHOD_MASK     (~FLAG_MASK)  /* valid mtable ptr bits */
#define LENGTH_MASK     METHOD_MASK

#define obj_flags(o) \
    (((unsigned long) (o)->methods) & FLAG_MASK)
#define obj_length(o)   \
    (((unsigned long) (o)->methods) >> METHOD_FLAG_BITS)

#define mkatype(t,l) ((struct methodtable *) (((l) << METHOD_FLAG_BITS)|(t)))
#define atype(m) ((m) & FLAG_MASK)


#define obj_methodtable(obj) ((obj)->methods)
#define obj_classblock(obj) ((obj)->methods->classdescriptor)

#define obj_array_methodtable(obj) \
    ((obj_flags((obj)) == T_NORMAL_OBJECT) ? obj_methodtable((obj))      \
                                           : cbMethodTable(classobject))
#define obj_array_classblock(obj) \
    ((obj_flags((obj)) == T_NORMAL_OBJECT) ? (obj)->methods->classdescriptor \
                                           : classobject)

#define mt_slot(methodtable, slot) (methodtable)->methods[slot]

#define uobj_getslot(o, slot) (o)[slot]
#define uobj_setslot(o, slot, v) (uobj_getslot(o, slot) = (v))

#define obj_getslot(o, slot) uobj_getslot(unhand(o), slot)
#define obj_setslot(o, slot, v) (obj_getslot(o, slot) = (v))

#define obj_monitor(handlep) ((int) handlep)


struct arrayinfo {
    int index;
    char sig;      /* type signature. */
    char *name;
    int factor;
};


typedef union stack_item {
    /* Non pointer items */
    int            i;
    float          f;
    OBJECT         o;
    /* Pointer items */
    Handle        *h;
    void          *p;
    unsigned char *addr;
} stack_item;

struct execenv {
    struct javastack  *initial_stack;
    struct javaframe  *current_frame; 
    Handle           *thread;	    /* vague type to avoid include files */
    char              exceptionKind;
    union {
	Handle	      *exc;	    /* holds exception object */
	unsigned char *addr;	    /* holds pc for stack overflow */
    } exception;
};

typedef struct execenv ExecEnv;

#define JAVASTACK_CHUNK_SIZE 2000
struct javastack {
    struct execenv  *execenv;	    /* execenv we belong to */
    struct javastack *prev;          /* previous stack of this execenv */
    struct javastack *next;          /* next stack of this execenv */
    stack_item      *end_data;      /* address of end of data */
    unsigned int     stack_so_far;  /* total space used by this chunk and
				     * all previous chunks. */
    stack_item       data[JAVASTACK_CHUNK_SIZE];    /* actual data */

};

typedef struct javastack JavaStack;


struct javaframe {
    stack_item         *optop;	      /* current top of stack */
    struct javaframe    *prev;   /* previous java frame. */
    struct javastack    *javastack;					  
    unsigned char      *lastpc;	       /* pc of last instruction */
    unsigned char      *returnpc;      /* pc of next instruction */
    struct methodblock *current_method;/* method currently executing */
    cp_item_type       *constant_pool; /* constant_pool of this method */
    Handle             *monitor;       /* object locked by this method */
    int	                mon_starttime; /* time this method began */
    stack_item         *vars;	       /* pointer to this frame's vars */
    stack_item ostack[1];	       /* start of this frame's stack */
};

typedef struct javaframe JavaFrame; 


/*
 * Javaframe.exceptionKind is used to signal why the interpreter
 * loop was exited.
 */
#define EXCKIND_NONE            0               /* return */
#define EXCKIND_THROW		1		/* throw */
#define EXCKIND_STKOVRFLW       2               /* stack overflow */


#define exceptionClear(ee)	\
    ((ee)->exceptionKind = EXCKIND_NONE);

#define exceptionOccurred(ee)	\
    ((ee)->exceptionKind != EXCKIND_NONE)

#define exceptionThrow(ee, obj)	\
    (ee)->exceptionKind = EXCKIND_THROW;		    \
    (ee)->exception.exc = (obj);


extern long nbinclasses, sizebinclasses;
extern ClassClass **binclasses;

/* stuff for dealing with handles */
#define unhand(o) ((o)->obj)


/* gc.c */

extern HObject *AllocHandle(struct methodtable *, ClassObject *);
extern struct arrayinfo arrayinfo[];

/* interpreter.c */

/* SignalError() -- Instantiate an object of the specified class. 
 * Indicate that that error occurred.
 */
void SignalError(struct execenv *, char *, char *);

JavaStack *CreateNewJavaStack(ExecEnv *ee, JavaStack *previous_stack);

void InitializeExecEnv(ExecEnv *ee, Handle *thread);
void DeleteExecEnv(ExecEnv *ee, Handle *thread);
extern ExecEnv *DefaultExecEnv;


HObject *execute_java_constructor(ExecEnv *,
				 char *classname,
				 ClassClass *cb,
				 char *signature, ...);
long execute_java_static_method(ExecEnv *, ClassClass *cb,
			       char *method_name, char *signature, ...);
long execute_java_dynamic_method(ExecEnv *, HObject *obj,
				char *method_name, char *signature, ...);
     
long do_execute_java_method(ExecEnv *ee, void *obj, 
			   char *method_name, char *signature, 
			   struct methodblock *mb,
			   bool_t isStaticCall, ...);

long do_execute_java_method_vararg(ExecEnv *ee, void *obj, 
				  char *method_name, char *signature, 
				  struct methodblock *mb,
				  bool_t isStaticCall, va_list args);
     
long now();
void InitializeInterpreter(void);
bool_t IsInstanceOf(Handle * h, ClassClass *dcb, ExecEnv *ee);
bool_t ImplementsInterface(ClassClass *cb, ClassClass *icb, ExecEnv *ee);


struct stat;

int main(int, char **);
bool_t dynoLink(struct methodblock *);
char *str2rd(char *);
char *unicode2rd(unicode *, long);

/* classruntime.c */
void ClassDefHook(ClassClass *);
HArrayOfChar *MakeString(char *, long);
extern int ImportAcceptable;

ClassClass *FindClass(struct execenv *, char *, bool_t resolve);
ClassClass *FindClassFromClass(struct execenv *, char *, bool_t resolve, ClassClass *from);
char *ResolvePrototype(struct execenv *, ClassClass *cb);
void RunStaticInitializers(ClassClass *cb);
void InitializeInvoker(ClassClass *cb);
int MethodMatches(char *caller, struct methodblock *mb);

void LoadClassConstants(ClassClass *cb);
bool_t ResolveClassStringConstant(ClassClass *, unsigned, struct execenv *);
bool_t ResolveClassConstant(cp_item_type *, unsigned index, struct execenv *ee,
			    unsigned mask);
bool_t ResolveClassConstantFromClass(ClassClass *, unsigned index, 
				     struct execenv *ee, unsigned mask);

bool_t VerifyClassAccess(ClassClass *, ClassClass *, bool_t);
bool_t VerifyFieldAccess(ClassClass *, ClassClass *, int, bool_t);
bool_t IsSameClassPackage(ClassClass *class1, ClassClass *class2); 

char *GetClassConstantClassName(cp_item_type *constant_pool, int index);
unsigned NameAndTypeToHash(char *name, char *type);
HObject *newobject(ClassClass *cb, unsigned char *pc, struct execenv *ee);


char *pc2string(unsigned char *pc, struct methodblock *mb, char *buf, char *limit);
int InstallClass(struct execenv *, ClassClass *, char *);

extern int InhibitExecute;
Handle *ArrayAlloc(int, int);
Handle *ObjAlloc(ClassClass *, long);
int sizearray(int, int);
void InitializeAlloc(long);
void InitializeRPC();
extern char *remote_classname(Handle *);
extern Handle *remote_clone(struct execenv *);
extern long remote_cast(Handle *, ClassClass *);
int pc2lineno(struct methodblock *, unsigned int);

/* from profiler.c */
extern int java_monitor;
void javamon(int i);
void java_mon(struct methodblock *caller, struct methodblock *callee, int time);
void java_mon_dump();

/* from classloader.c */
void AddBinClass(ClassClass * cb);
void DelBinClass(ClassClass * cb);
int LoadFile(char *fn, char *dir, char *SourceHint);
int DoImport(char *name, char *SourceHint);
ClassClass* createFakeArrayClass(char *name);


long SeenFile(char *fn, long state);
unsigned Signature2ArgsSize(char *method_signature);
long SeenFile(char *, long state);
/* SeenFile states: */
#define sfs_query 0
#define sfs_loaded 1
#define sfs_compiling 2
#define sfs_forget_everything -1

/* from classresolver.c */
char *InitializeClass(ClassClass * cb, char **detail);
char *ResolveClass(ClassClass * cb, char **detail);
ClassClass *FindClass(struct execenv *ee, char *name, bool_t resolve);
ClassClass *FindClassFromClass(struct execenv *ee, char *name, 
			       bool_t resolve, ClassClass *from);
void makeslottable(ClassClass * clb);

/* from import_md.c  */
int import_md(char *name, char *hint);

/* from path_md.c */
char **CLASSPATH();

/* from seenfile_md.c */
extern long SeenFile_md(char *, long);

/* from threadruntime.c */
long *getclassvariable(struct ClassClass *cb, char *fname);
struct Hjava_lang_Thread;
char *thread_name(struct Hjava_lang_Thread *tid); 

void setThreadName(struct Hjava_lang_Thread *ht, HArrayOfChar *newName);
HArrayOfChar *getThreadName();

/* from threads_md.c */
struct Hjava_lang_Thread;
void SetCurrentThread(struct Hjava_lang_Thread * t);


/* from CompiledClasses.c */
void InstallCompiledClasses();
void InstallCompiledStrings();


/* from CompSupport.c */
long CallInterpreted(register struct methodblock * mb, void *obj,...);

/* used to indicate of an object or remote or local */
extern struct methodtable *remote_methodtable;

/* Default size of the memory pool */
#ifndef MEMPOOLSIZE
#define MEMPOOLSIZE ((int) (3 * (1024*1024)))
#endif

void unicode2str(unicode *, char *, long);
unicode *str2unicode(char *, unicode *, long);

/* string hash support */
struct StrIDhash;
unsigned short Str2ID(struct StrIDhash **, char *, void ***, int);
char *ID2Str(struct StrIDhash *, unsigned short, void ***);
void Str2IDFree(struct StrIDhash **);
ExecEnv *EE();

/* Miscellaneous functions in util.c */
char *unicode2rd(unicode *s, long len);
void out_of_memory();
void prints(char *s);
void printus(unicode *str, long len);

#endif /* ! _INTERPRETER_H_ */

