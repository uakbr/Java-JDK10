/*
 * @(#)monitor_cache.h	1.15 95/11/29
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
 * Monitor cache definitions
 */

#ifndef _MONITOR_CACHE_H_
#define _MONITOR_CACHE_H_

#include "monitor.h"		/* For SYS_TIMEOUT_INFINITY */

extern sys_mon_t *_moncache_lock;
#define CACHE_LOCK_INIT() monitorRegister(_moncache_lock, "Monitor cache lock");
#define CACHE_LOCK()	  sysMonitorEnter(_moncache_lock)
#define CACHE_NOTIFY()	  sysMonitorNotify(_moncache_lock)
#define CACHE_WAIT()	  sysMonitorWait(_moncache_lock, TIMEOUT_INFINITY)
#define CACHE_LOCKED()	  sysMonitorEntered(_moncache_lock)
#define CACHE_UNLOCK()	  sysMonitorExit(_moncache_lock)

/*
 * External routines.
 */
monitor_t *lookupMonitor(unsigned int);
monitor_t *createMonitor(unsigned int);
void monitorDestroy(monitor_t *, unsigned int);
void monitorEnumerate(void (*)(monitor_t *, void *), void *);

#endif /* !_MONITOR_CACHE_H_ */
