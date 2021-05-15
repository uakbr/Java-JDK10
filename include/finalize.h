/*
 * @(#)finalize.h	1.9 95/11/29
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

#ifndef _FINALIZE_H_
#define _FINALIZE_H_

#include "oobj.h"
#include "sys_api.h"

/*
 * The HasFinalizerQ and FinalizeMeQ queues contain finalizer_t
 * structures.  The next field must be the first field in the struct,
 * and handles rather than objects are used to avoid relocating the
 * contents of the queues on GC.
 */
typedef struct finalizer_t {
    struct finalizer_t *next;	/* The next finalizer structure */
    JHandle *handle;		/* The handle of the object */
} finalizer_t;

extern finalizer_t *HasFinalizerQ;
extern finalizer_t *FinalizeMeQ;
extern finalizer_t *BeingFinalized;


/*
 * Locks for the finalization queues
 */
extern sys_mon_t *_hasfinalq_lock;
#define HASFINALQ_LOCK_INIT()	monitorRegister(_hasfinalq_lock, \
						"Has finalization queue lock")
#define HASFINALQ_LOCK()	sysMonitorEnter(_hasfinalq_lock)
#define HASFINALQ_LOCKED()	sysMonitorEntered(_hasfinalq_lock)
#define HASFINALQ_UNLOCK()	sysMonitorExit(_hasfinalq_lock)
#define HASFINALQ_NOTIFY()	sysMonitorNotify(_hasfinalq_lock)
#define HASFINALQ_WAIT()	sysMonitorWait(_hasfinalq_lock, \
					       TIMEOUT_INFINITY)
extern sys_mon_t *_finalmeq_lock;
#define FINALMEQ_LOCK_INIT()	monitorRegister(_finalmeq_lock, \
						"Finalize me queue lock")
#define FINALMEQ_LOCK()		sysMonitorEnter(_finalmeq_lock)
#define FINALMEQ_LOCKED()	sysMonitorEntered(_finalmeq_lock)
#define FINALMEQ_UNLOCK()	sysMonitorExit(_finalmeq_lock)
#define FINALMEQ_NOTIFY()	sysMonitorNotify(_finalmeq_lock)
#define FINALMEQ_WAIT()		sysMonitorWait(_finalmeq_lock, \
					       TIMEOUT_INFINITY)

extern void InitializeFinalizer(void);
extern void InitializeFinalizerThread(void);

#endif /* _FINALIZE_H_ */
