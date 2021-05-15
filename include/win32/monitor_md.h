/*
 * @(#)monitor_md.h	1.11 95/11/19
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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
 * Win32 implementation of Java monitors
 */

#ifndef	_WIN32_MONITOR_MD_H_
#define	_WIN32_MONITOR_MD_H_

#include <windows.h>

#include "threads_md.h"
#include "mutex_md.h"
#include "condvar_md.h"

#define SYS_TIMEOUT_INFINITY (-1)

typedef struct sys_mon {
    mutex_t mutex;	    /* Mutex for this monitor */
    condvar_t condvar;      /* Condition variable for this monitor */
    sys_thread_t *owner;    /* Thread currently owning this monitor */
    unsigned int depth;	    /* Monitor entry count */
} sys_mon_t;

#endif	/* !_WIN32_MONITOR_MD_H_ */
