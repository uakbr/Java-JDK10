/*
 * @(#)mutex_md.h	1.8 95/11/18
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
 * Win32 implementation of mutexes. Here we use critical sections as
 * our mutexes. We could have used mutexes, but mutexes are heavier
 * weight than critical sections. Mutexes and critical sections are
 * semantically identical, the only difference being that mutexes
 * can operate between processes (i.e. address spaces).
 *
 * It's worth noting that the Win32 functions supporting critical
 * sections do not provide any error information whatsoever (i.e.
 * all critical section routines return (void)).
 */

#ifndef	_WIN32_MUTEX_MD_H_
#define	_WIN32_MUTEX_MD_H_

#include <windows.h>

typedef CRITICAL_SECTION mutex_t;

#define mutexInit(m)	InitializeCriticalSection(m)
#define mutexDestroy(m)	DeleteCriticalSection(m)
#define mutexLock(m)	EnterCriticalSection(m)
#define mutexUnlock(m)	LeaveCriticalSection(m)

#endif	/* !_WIN32_MUTEX_MD_H_ */
