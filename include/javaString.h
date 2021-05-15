/*
 * @(#)javaString.h	1.11 95/11/29  
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
 * Java string utilities
 */

#ifndef _JAVASTRING_H_
#define _JAVASTRING_H_

#include "oobj.h"

#include "java_lang_String.h"


/*
 * Print the String object with prints.
 */
void javaStringPrint(Hjava_lang_String *);

/*
 * Return the length of the String object.
 */
int javaStringLength(Hjava_lang_String *);


/*
 * Create and return a new Java String object, initialized from the C string.
 */
Hjava_lang_String *makeJavaString(char *, int);

/*
 * Create a new C string initialized from the specified Java string,
 * and return a pointer to it.
 * For makeCString, temporary storage is allocated and released automatically
 * when all references to the returned value are eliminated. WARNING: You 
 * must keep this pointer in a variable to prevent the storage from getting
 * garbage collected.
 * For allocCString, a "malloc" is used to get the storage; the caller is
 * responsible for "free"ing the pointer that is returned.
 * 
 */
char *makeCString(Hjava_lang_String *s);
char *allocCString(Hjava_lang_String *s);

/*
 * Get the characters of the String object into a unicode string buffer.
 * No allocation occurs. Assumes that len is less than or equal to
 * the length of the string, and that the buf is at least len+1 unicodes
 * in size. The unicode buffer's address is returned.
 */
unicode *javaString2unicode(Hjava_lang_String *, unicode *, int);

/*
 * Get the characters of the String object into a C string buffer.
 * No allocation occurs. Assumes that len is the size of the buffer.
 * The C string's address is returned.
 */
char *javaString2CString(Hjava_lang_String *, char *, int);

#endif /* !_JAVASTRING_H_ */
