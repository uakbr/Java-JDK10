/*
 * @(#)standardlib.h	1.7 95/07/24  
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

#ifndef _STANDARDLIB_H_
#define _STANDARDLIB_H_

#include <sys/types.h>

#ifdef	BUILD_ON_SUNOS4X
#include <sys/socket.h>
#endif	/* BUILD_ON_SUNOS4X */

#ifdef BUILD_ON_SUNOS4X
#include <strings.h>
#define	_CTYPE_(i)	(_ctype_[(i)+1])
#else
#include <string.h>
#define	_CTYPE_(i)	((__ctype+1)[(i)])
#endif

/* interface to the EEPROM */
/* XXX Mondo unclean from the portability standpoint this will be a 
 * pain to port to anything other than SPARC (Sun SPARC!) boxes!
 */
#include <sys/types.h>
#ifdef BUILD_ON_SUNOS4X
#include <sundev/openpromio.h>
#endif

#ifdef BUILD_ON_SUNOS4X
extern int close(long);
extern int dup2(long, long);
extern int ftruncate(long, long);
extern int vfork(void);
extern int mkdir(char *, int);
extern munmap(void *, size_t);
extern double atof(char *);
extern char *strdup(char *);
extern int ioctl(int, int, void *);
extern int syscall(int, ...);
extern int socket(int domain, int type, int protocol);
extern int bind(int s, struct sockaddr *name, int namelen);
extern int connect(int s, struct sockaddr *name, int namelen);
extern int getsockname(int s, struct sockaddr *name, int *namelen);
extern int setsockopt(int s, int level, int optname, char *optval, int optlen);
extern int sendmsg(int s, struct msghdr *msg, int flags);
extern int recvmsg(int s, struct msghdr *msg, int flags);
#ifdef __GNUC__
extern void *valloc(unsigned int);
extern void *memalign(unsigned int, unsigned int);
#else
extern char *valloc(unsigned int);
extern char *memalign(unsigned int, unsigned int);
#endif
#else	/* BUILD_ON_SUNOS4X */
/* all defined in <stdlib.h> */
#endif	/* BUILD_ON_SUNOS4X */

#endif /* !_STANDARDLIB_H_ */
