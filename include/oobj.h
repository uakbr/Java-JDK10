/*
 * @(#)oobj.h	1.55 95/11/29  
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
 * Java object header format
 */

#ifndef _OOBJ_H_
#define _OOBJ_H_

#ifndef JAVA_CLASSFILE_MAGIC

#include <stddef.h>

#include "typedefs.h"
#include "debug.h"
#include "bool.h"
#include "oobj_md.h"
#include "signature.h"

#define JAVA_CLASSFILE_MAGIC	          0xCafeBabe

#define JAVASRCEXT "java"
#define JAVASRCEXTLEN 4
#define JAVAOBJEXT "class"
#define JAVAOBJEXTLEN 5

#define JAVA_VERSION     45
#define JAVA_MINOR_VERSION 3
#define ARRAYHEADER     long alloclen

#define HandleTo(T) typedef struct H##T { Class##T *obj; struct methodtable *methods;} H##T


typedef long OBJECT;
typedef OBJECT Classjava_lang_Object;
typedef OBJECT ClassObject;
HandleTo(java_lang_Object);
typedef Hjava_lang_Object JHandle;
typedef Hjava_lang_Object HObject;

typedef unsigned short unicode;

extern unicode	*str2unicode(char *, unicode *, long);
extern char	*int642CString(int64_t number, char *buf, int buflen);

#define ALIGN(n) (((n)+3)&~3)
#define UCALIGN(n) ((unsigned char *)ALIGN((int)(n)))

struct fieldblock {
    struct ClassClass *clazz;
    char *signature;
    char *name;
    unsigned long ID;
    unsigned short access;
    union {
	unsigned long offset;	/* info of data */	
	OBJECT static_value;
	void *static_address;
    } u;
};

#define fieldname(fb)    ((fb)->name)
#define fieldsig(fb)     ((fb)->signature)
#define fieldIsArray(fb) (fieldsig(fb)[0] == SIGNATURE_ARRAY)
#define fieldIsClass(fb) (fieldsig(fb)[0] == SIGNATURE_CLASS)
#define	fieldclass(fb)   ((fb)->clazz)

struct execenv;

struct methodblock {
    struct fieldblock fb;
    unsigned char       *code;	/* the code */
    struct CatchFrame   *exception_table;
    struct lineno       *line_number_table;
    struct localvar     *localvar_table;

    unsigned long        code_length;
    unsigned long        exception_table_length;
    unsigned long        line_number_table_length;
    unsigned long        localvar_table_length;

    bool_t  (*invoker)
      (JHandle *o, struct methodblock *mb, int args_size, struct execenv *ee);
    unsigned short args_size;	/* total size of all arguments */
    unsigned short maxstack;	/* maximum stack usage */
    unsigned short nlocals;	/* maximum number of locals */
    void *CompiledCode;		/* it's type is machine dependent */
    long CompiledCodeFlags;	/* machine dependent bits */
};

struct ClassClass;
struct HIOstream;

struct methodtable {
    struct ClassClass *classdescriptor;
    struct methodblock *methods[1];
};

typedef struct {
    char body[1];
} ArrayOfByte;
typedef ArrayOfByte ClassArrayOfByte;
HandleTo(ArrayOfByte);

typedef struct {
    unicode body[1];
} ArrayOfChar;
typedef ArrayOfChar ClassArrayOfChar;
HandleTo(ArrayOfChar);

typedef struct {
    signed short body[1];
} ArrayOfShort;
typedef ArrayOfShort ClassArrayOfShort;
HandleTo(ArrayOfShort);

typedef struct {
    long        body[1];
} ArrayOfInt;
typedef ArrayOfInt ClassArrayOfInt;
HandleTo(ArrayOfInt);

typedef struct {
    int64_t        body[1];
} ArrayOfLong;
typedef ArrayOfLong ClassArrayOfLong;
HandleTo(ArrayOfLong);

typedef struct {
    float       body[1];
} ArrayOfFloat;
typedef ArrayOfFloat ClassArrayOfFloat;
HandleTo(ArrayOfFloat);

typedef struct {
    double       body[1];
} ArrayOfDouble;
typedef ArrayOfDouble ClassArrayOfDouble;
HandleTo(ArrayOfDouble);

typedef struct {
    JHandle *(body[1]);
} ArrayOfArray;
typedef ArrayOfArray ClassArrayOfArray;
HandleTo(ArrayOfArray);

typedef struct {
    HObject *(body[1]);
} ArrayOfObject;
typedef ArrayOfObject ClassArrayOfObject;
HandleTo(ArrayOfObject);

typedef struct Hjava_lang_String HString;

typedef struct {
    HString  *(body[1]);
} ArrayOfString;
typedef ArrayOfString ClassArrayOfString;
HandleTo(ArrayOfString);


/* Note: any handles in this structure must also have explicit
   code in the ScanClasses() routine of the garbage collector
   to mark the handle. */
typedef struct ClassClass {
    /* Things following here are saved in the .class file */
    unsigned short	     major_version;
    unsigned short	     minor_version;
    char                    *name;
    char                    *super_name;
    char                    *source_name;
    struct Hjava_lang_Class  *superclass;
    struct Hjava_lang_Class  *HandleToSelf;
    HObject		    *loader;
    struct methodblock	    *finalizer;

    union cp_item_type      *constantpool;
    struct methodblock      *methods;
    struct fieldblock       *fields;
    short                   *implements;

    struct methodtable      *methodtable;
    struct fieldblock      **slottable;

    HArrayOfChar	    *classname_array;

    unsigned long            thishash;
    unsigned long            totalhash;

    unsigned short           constantpool_count;  /* number of items in pool */
    unsigned short           methods_count;       /* number of methods */
    unsigned short           fields_count;        /* number of fields */
    unsigned short           implements_count;    /* number of protocols */

    unsigned short           methodtable_size;    /* the size of method table */
    unsigned short           slottbl_size;        /* size of slottable */
    unsigned short           instance_size;       /* (bytes) of an instance */

    unsigned short access;           /* how this class can be accesses */
    unsigned short flags;	     /* see the CCF_* macros */
} ClassClass;

typedef ClassClass Classjava_lang_Class;
HandleTo(java_lang_Class);
typedef Hjava_lang_Class HClass;

extern bool_t	createInternalClass(unsigned char *bytes, unsigned char *limit,
				    ClassClass *);
extern void	FreeClass(ClassClass *cb);

#define classname(cb)   ((cb)->name)
#define classsupername(cb) ((cb)->super_name)
#define classsrcname(cb) ((cb)->source_name)
#define cbSuperclass(cb)   ((cb)->superclass)
#define cbHandle(cb)       ((cb)->HandleToSelf)
#define	cbLoader(cb)	((cb)->loader)

#define cbConstantPool(cb) ((cb)->constantpool)
#define	cbMethods(cb)      ((cb)->methods)
#define	cbFields(cb)       ((cb)->fields)
#define cbImplements(cb)   ((cb)->implements)
#define cbMethodTable(cb)  ((cb)->methodtable)
#define cbSlotTable(cb)    ((cb)->slottable)

#define cbThisHash(cb)     ((cb)->thishash)
#define cbTotalHash(cb)    ((cb)->totalhash)
#define cbSlotTableSize(cb) ((cb)->slottbl_size)
#define cbInstanceSize(cb) ((cb)->instance_size)
#define cbMethodTableSize(cb) ((cb)->methodtable_size)
#define cbClassnameArray(cb)  ((cb)->classname_array)
#define cbAccess(cb)       ((cb)->access)
#define cbIsInterface(cb)  ((cbAccess(cb) & ACC_INTERFACE) != 0)

extern char *classname2string(char *str, char *dst, int size);

#define twoword_static_address(fb) ((fb)->u.static_address)
#define normal_static_address(fb)  (&(fb)->u.static_value)

/* ClassClass flags */
#define CCF_IsResolved	  0x02	/* been resolved yet? */
#define CCF_IsError	  0x04	/* Resolution caused an error */
#define CCF_IsSoftRef	  0x08	/* whether this is class Ref or subclass */
#define CCF_IsInitialized 0x10	/* whether this is class has been inited */
#define CCF_IsLoaded      0x20	/* Is this really the class object or a stub.*/
#define CCF_IsVerified    0x40	/* Is this really the class object or a stub.*/

#define CCIs(cb,flag) (((cb)->flags & CCF_Is##flag) != 0)
#define CCSet(cb,flag) ((cb)->flags |= CCF_Is##flag)
#define CCClear(cb,flag) ((cb)->flags &= ~CCF_Is##flag)

/* map from pc to line number */
struct lineno {
    unsigned long pc, 
    line_number;
};

extern struct lineno *lntbl;
extern long lntsize, lntused;

/* Symbol table entry for local variables and parameters.
   pc0/length defines the range that the variable is valid, slot
   is the position in the local variable array in ExecEnv.
   nameoff and sigoff are offsets into the string table for the
   variable name and type signature.  A variable is defined with
   DefineVariable, and at that time, the node for that name is
   stored in the localvar entry.  When code generate is completed
   for a particular scope, a second pass it made to replace the
   src node entry with the correct length. */

struct localvar {
    long pc0;			/* starting pc for this variable */
    long length;		/* -1 initially, end pc - pc when we're done */
    short nameoff;		/* offset into string table */
    short sigoff;		/* offset into string table */
    long slot;			/* local variable slot */
};

/* Try/catch is implemented as follows.  On a per class basis,
   there is a catch frame handler (below) for each catch frame
   that appears in the source.  It contains the pc range of the
   corresponding try body, a pc to jump to in the event that that
   handler is chosen, and a catchType which must match the object
   being thrown if that catch handler is to be taken.

   The list of catch frames are sorted by pc.  If one range is
   inside another, then outer most range (the one that encompasses
   the other) appears last in the list.  Therefore, it is possible
   to search forward, and the first one that matches is the
   innermost one.

   Methods with catch handlers will layout the code without the
   catch frames.  After all the code is generated, the catch
   clauses are generated and table entries are created.

   When the class is complete, the table entries are dumped along
   with the rest of the class. */

struct CatchFrame {
    long    start_pc, end_pc;	/* pc range of corresponding try block */
    long    handler_pc;	        /* pc of catch handler */
    void*   compiled_CatchFrame; /* space to be used by machine code */
    short   catchType;	        /* type of catch parameter */
};

#define MC_SUPER        (1<<5)
#define MC_NARGSMASK    (MC_SUPER-1)
#define MC_INT          (0<<6)
#define MC_FLOAT        (1<<6)
#define MC_VOID         (2<<6)
#define MC_OTHER        (3<<6)
#define MC_TYPEMASK     (3<<6)

enum {
    CONSTANT_Utf8 = 1,
    CONSTANT_Unicode,		/* unused */
    CONSTANT_Integer,
    CONSTANT_Float,
    CONSTANT_Long,      
    CONSTANT_Double,
    CONSTANT_Class,
    CONSTANT_String,
    CONSTANT_Fieldref,
    CONSTANT_Methodref,
    CONSTANT_InterfaceMethodref,
    CONSTANT_NameAndType
};

union cp_item_type {
    int i;
    void *p;
    float f;
    char *cp;
};
typedef union cp_item_type cp_item_type;

#define CONSTANT_POOL_ENTRY_RESOLVED 0x80
#define CONSTANT_POOL_ENTRY_TYPEMASK 0x7F
#define CONSTANT_POOL_TYPE_TABLE_GET(cp,i) (((unsigned char *)(cp))[i])
#define CONSTANT_POOL_TYPE_TABLE_PUT(cp,i,v) (CONSTANT_POOL_TYPE_TABLE_GET(cp,i) = (v))
#define CONSTANT_POOL_TYPE_TABLE_SET_RESOLVED(cp,i) \
	(CONSTANT_POOL_TYPE_TABLE_GET(cp,i) |= CONSTANT_POOL_ENTRY_RESOLVED)
#define CONSTANT_POOL_TYPE_TABLE_IS_RESOLVED(cp,i) \
	((CONSTANT_POOL_TYPE_TABLE_GET(cp,i) & CONSTANT_POOL_ENTRY_RESOLVED) != 0)
#define CONSTANT_POOL_TYPE_TABLE_GET_TYPE(cp,i) \
        CONSTANT_POOL_TYPE_TABLE_GET(cp,i) & CONSTANT_POOL_ENTRY_TYPEMASK

#define CONSTANT_POOL_TYPE_TABLE_INDEX 0
#define CONSTANT_POOL_UNUSED_INDEX 1

/* The following are used by the constant pool of "array" classes. */

#define CONSTANT_POOL_ARRAY_DEPTH_INDEX 1
#define CONSTANT_POOL_ARRAY_TYPE_INDEX 2
#define CONSTANT_POOL_ARRAY_CLASS_INDEX 3
#define CONSTANT_POOL_ARRAY_CLASSNAME_INDEX 4
#define CONSTANT_POOL_ARRAY_LENGTH 5

#endif

#endif /* !_OOBJ_H_ */
