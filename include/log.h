/*
 * @(#)log.h	1.6 95/07/17  
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
 * Logging utilities for debugging.
 */

#ifndef _LOG_H_
#define _LOG_H_

#ifdef LOGGING

#include <stdio.h>

/*
 * NOTE: I [Tim] changed command-line parsing of the -l flag to allow
 * -l0 to be passed in.  PERMANENT LOG STATEMENTS SHOULD NOT USE LEVEL 0!
 * It is intended to be used temporarily to limit logging output to
 * specific messages during debugging.  Otherwise even level 1 logging
 * buries you in output.
 */

extern int logging_level;

#define Log(level, message) {			\
    if (level <= logging_level)			\
	fprintf(stderr, message);		\
}

#define Log1(level, message, x1) {		\
    if (level <= logging_level)			\
	fprintf(stderr, message, (x1));		\
}

#define Log2(level, message, x1, x2) {		\
    if (level <= logging_level)			\
	fprintf(stderr, message, (x1), (x2));	\
}

#define Log3(level, message, x1, x2, x3) {		\
    if (level <= logging_level)				\
	fprintf(stderr, message, (x1), (x2), (x3));	\
}

#define Log4(level, message, x1, x2, x3, x4) {			\
    if (level <= logging_level)					\
	fprintf(stderr, message, (x1), (x2), (x3), (x4));	\
}

#else

#define Log(level, message)
#define Log1(level, message, x1)
#define Log2(level, message, x1, x2)
#define Log3(level, message, x1, x2, x3)
#define Log4(level, message, x1, x2, x3, x4)

#endif /* LOGGING */
#endif /* !_LOG_H_ */
