/*
 * @(#)sys_api.h	1.41 95/11/29
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
 * System or Host dependent API.  This defines the "porting layer" for
 * POSIX.1 compliant operating systems.
 */

#ifndef _SYS_API_H_
#define _SYS_API_H_

/*
 * typedefs_md.h includes basic types for this platform;
 * any macros for HPI functions have been moved to "sysmacros_md.h"
 */
#include "typedefs_md.h"

/*
 * Miscellaneous system utility APIs that fall outside the POSIX.1
 * spec.
 *
 * Until POSIX (P1003.1a) formerly P.4 is standard, we'll use our
 * time struct definitions found in timeval.h.
 */
#include "timeval.h"
long 	sysGetMilliTicks(void);
long    sysTime(long *);
int64_t sysTimeMillis(void);

#include "time.h"
struct tm * sysLocaltime(time_t *, struct tm*);
struct tm * sysGmtime(time_t *, struct tm*);
void        sysStrftime(char *, int, char *, struct tm *);
time_t      sysMktime(struct tm*);


/*
 * System API for general allocations
 */
void *	sysMalloc(size_t);
void *	sysRealloc(void*, size_t);
void	sysFree(void*);
void *	sysCalloc(unsigned int, unsigned int);

/*
 * System API for dynamic linking libraries into the interpreter
 */
char *  sysInitializeLinker(void);
int     sysAddDLSegment(char *);
void    sysSaveLDPath(char *);
long    sysDynamicLink(char *);
void	sysBuildLibName(char *, int, char *, char *);

/*
 * System API for threads
 */
typedef struct  sys_thread sys_thread_t;
typedef struct  sys_mon sys_mon_t;
typedef void *  stackp_t;

int 	sysThreadBootstrap(sys_thread_t **);
void 	sysThreadInitializeSystemThreads(void);
int 	sysThreadCreate(long, uint_t flags, void *(*)(void *),
			sys_thread_t **, void *);
void	sysThreadExit(void);
sys_thread_t * sysThreadSelf(void);
void    sysThreadYield(void);
int	sysThreadSuspend(sys_thread_t *);
int	sysThreadResume(sys_thread_t *);
int	sysThreadSetPriority(sys_thread_t *, int);
int	sysThreadGetPriority(sys_thread_t *, int *);
void *  sysThreadStackPointer(sys_thread_t *); 
stackp_t sysThreadStackBase(sys_thread_t *); 
int	sysThreadSingle(void);
void	sysThreadMulti(void);
int     sysThreadEnumerateOver(int (*)(sys_thread_t *, void *), void *);
void	sysThreadInit(sys_thread_t *, stackp_t);
void	sysThreadSetBackPtr(sys_thread_t *, void *);
void *  sysThreadGetBackPtr(sys_thread_t *);
int     sysThreadCheckStack(void);
int     sysInterruptsPending(void);
void	sysThreadPostException(sys_thread_t *, void *);
void	sysThreadDumpInfo(sys_thread_t *);

/*
 * System API for monitors
 */
int     sysMonitorSizeof(void);
int     sysMonitorInit(sys_mon_t *, bool_t);
int     sysMonitorEnter(sys_mon_t *);
bool_t  sysMonitorEntered(sys_mon_t *);
int     sysMonitorExit(sys_mon_t *);
int     sysMonitorNotify(sys_mon_t *);
int     sysMonitorNotifyAll(sys_mon_t *);
int 	sysMonitorWait(sys_mon_t *, int);
int	sysMonitorDestroy(sys_mon_t *, sys_thread_t *);
void    sysMonitorDumpInfo(sys_mon_t *);

#define SYS_OK	        0
#define SYS_TIMEOUT     1
#define SYS_ERR	       -1
#define SYS_INVALID    -2    /* invalid arguments */
#define SYS_NOTHREAD   -3    /* no such thread */
#define SYS_MINSTACK   -4    /* supplied stack size less than min stack size */
#define SYS_NOMEM      -5
#define SYS_NORESOURCE -6
#define SYS_DESTROY    -7

/*
 * System API for raw memory allocation
 */
void *  sysMapMem(long, long *);
void *  sysUnmapMem(void *, long, long *);
void *  sysCommitMem(void *, long, long *);
void *  sysUncommitMem(void *, long, long *);

/*
 * System API for termination
 */
void	sysExit(int);
int	sysAtexit(void (*func)(void));
void	sysAbort(void);

/*
 * Include platform specific macros to override these
 */
#include "sysmacros_md.h"
 
#endif /* !_SYS_API_H_ */
