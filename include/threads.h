/*
 * @(#)threads.h	1.28 95/11/29  
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

#ifndef _THREADS_H_
#define _THREADS_H_

#include "oobj.h"
#include "interpreter.h"
#include "timeval.h"
#include "monitor.h"
#include "bool.h"
#include "sys_api.h"

#include "java_lang_Thread.h"
#include "java_lang_ThreadGroup.h"

#define MinimumPriority	    java_lang_Thread_MIN_PRIORITY
#define MaximumPriority	    java_lang_Thread_MAX_PRIORITY
#define NormalPriority	    java_lang_Thread_NORM_PRIORITY

/*
 * Support for thread queue
 */

extern sys_mon_t *_queue_lock;

#define QUEUE_LOCK_INIT() monitorRegister(_queue_lock, "Thread queue lock")
#define QUEUE_LOCK()	  sysMonitorEnter(_queue_lock)
#define QUEUE_LOCKED()	  sysMonitorEntered(_queue_lock)
#define QUEUE_UNLOCK()	  sysMonitorExit(_queue_lock)
#define QUEUE_NOTIFY()	  sysMonitorNotify(_queue_lock)
#define QUEUE_WAIT()	  sysMonitorWait(_queue_lock, SYS_TIMEOUT_INFINITY)

/*
 * Thread-related data structures
 */

typedef struct Hjava_lang_Thread HThread;
typedef struct Hjava_lang_ThreadGroup HThreadGroup;
typedef struct Hjava_lang_Thread *TID;

typedef sys_thread_t ThreadPrivate;

/* Access to thread data structures */
#define THREAD(tid)	((struct Classjava_lang_Thread *) unhand(tid))
#define SYSTHREAD(tid)	((sys_thread_t *)THREAD(tid)->PrivateInfo)

#define THR_SYSTEM 0		/* System thread */
#define THR_USER   1		/* User thread */

extern int ActiveThreadCount;		/* All threads */
extern int UserThreadCount;		/* User threads */

/* The default Java stack size is legitimately platform-independent */
#define JAVASTACKSIZE (400 * 1024)     /* Default size of a thread java stack */

extern long ProcStackSize;		/* Actual size of thread C stack */
extern long JavaStackSize;		/* Actual maximum size of java stack */

extern stackp_t mainstktop;		/* Base of primordial thread stack */

/*
 * External interface to threads support
 */

void threadBootstrap(TID tid, stackp_t sb);
int  threadCreate(TID, unsigned int, size_t, void *(*)());
TID  threadSelf(void);
void threadSleep(int);
int  threadEnumerate(TID*, int);

void threadDumpInfo(TID, bool_t);        /* Debugging help in debug.c */

int threadPostException(TID tid, void *exc);

/*
 * There may be certain initialization that can't be done except by the
 * thread on itself, e.g. setting thread-local data in Solaris threads.
 * This function is called from ThreadRT0() when the thread starts up
 * to take care of such things.
 */
#define threadInit(tid, sb)		sysThreadInit(SYSTHREAD(tid), sb)

/*
 * Exit the current thread.  This function is not expected to return.
 */
#define threadExit()			sysThreadExit()

/*
 * Note that we do not check that priorities are within Java's limits down here.
 * In fact, we make use of that for things like the idle and clock threads.
 * This may change once we work out a portable priority model.
 */
#define threadSetPriority(tid, pri)	sysThreadSetPriority(SYSTHREAD(tid), pri)
#define threadGetPriority(tid, prip)	sysThreadGetPriority(SYSTHREAD(tid), prip)

#define threadYield()			sysThreadYield()
#define threadResume(tid)		sysThreadResume(SYSTHREAD(tid))
#define threadSuspend(tid)		sysThreadSuspend(SYSTHREAD(tid))

/*
 * Return information about this thread's stack.  This is used by
 * Garbage Collection code that needs to inspect the stack.
 *
 * It is permissable to return a null stack_base for those threads
 * that don't have a known stack (e.g. not allocated by the threads
 * package).  It is also permissable to return a somewhat bogus
 * stack_pointer for the current thread.
 */
#define threadStackBase(tid)		sysThreadStackBase(SYSTHREAD(tid))
#define threadStackPointer(tid)		sysThreadStackPointer(SYSTHREAD(tid))

#define threadCheckStack()		sysThreadCheckStack()

#endif /* !_THREADS_H_ */
