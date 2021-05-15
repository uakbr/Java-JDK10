/*
 * @(#)oobj_md.h	1.6 95/02/22  
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
 * Solaris-dependent search paths and oobj defines
 */

#ifndef _SOLARIS_OOBJ_MD_H_
#define _SOLARIS_OOBJ_MD_H_

#define JAVA_EXECUTABLE_MAGIC    "#! " INSTALLPATH "/bin/java\n"
#include <unistd.h>

#define JAVAPKG  "java/lang/"

#endif /* !_SOLARIS_OOBJ_MD_H_ */
