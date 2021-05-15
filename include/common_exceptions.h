/*
 * @(#)common_exceptions.h	1.7 95/08/11  
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
 * Common exceptions thrown from within the interpreter (and
 * associated libraries).
 */

#ifndef	_COMMON_EXCEPTIONS_H_
#define	_COMMON_EXCEPTIONS_H_

/*
 * The following routines will instantiate a member of a specific
 * subclass of Exception, and fill in the stack backtrace
 * information from the current thread's ExecEnv stack .
 */
extern void OutOfMemoryError(void);

#endif	/* !_COMMON_EXCEPTIONS_H_ */
