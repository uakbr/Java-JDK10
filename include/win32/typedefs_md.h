/*
 * @(#)typedefs_md.h	1.18 95/11/20
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
 * Win32 dependent type definitions
 */

#ifndef _WIN32_TYPEDEF_MD_H_
#define _WIN32_TYPEDEF_MD_H_

#include <windows.h>

#include "bool.h"

typedef unsigned int uint_t;
typedef long int32_t;
typedef unsigned long uint32_t;
typedef __int64	int64_t;
typedef unsigned __int64 uint64_t;

/* use these macros when the compiler supports the long long type */

#define ll_high(a)	((long)((a)>>32))
#define ll_low(a)	((long)(a))
#define int2ll(a)	((int64_t)(a))
#define ll2int(a)	((int)(a))
#define ll_add(a, b)	((a) + (b))
#define ll_and(a, b)	((a) & (b))
#define ll_div(a, b)	((a) / (b))
#define ll_mul(a, b)	((a) * (b))
#define ll_neg(a)	(-(a))
#define ll_not(a)	(~(a))
#define ll_or(a, b)	((a) | (b))
/* THE FOLLOWING DEFINITION IS NOW A FUNCTION CALL IN ORDER TO WORKAROUND
   OPTIMIZER BUG IN MSVC++ 2.1 (see system_md.c)
   #define ll_shl(a, n)	((a) << (n)) */
#define ll_shr(a, n)	((a) >> (n))
#define ll_sub(a, b)	((a) - (b))
#define ll_ushr(a, n)	((uint64_t)(a) >> (n))
#define ll_xor(a, b)	((a) ^ (b))
#define uint2ll(a)	((uint64_t)(unsigned long)(a))
#define ll_rem(a,b)	((a) % (b))

#define float2ll(f)	((int64_t) (f))
#define ll2float(a)	((float) (a))
#define ll2double(a)	((double) (a))
#define double2ll(f)	((int64_t) (f))

/* comparison operators */
#define ll_ltz(ll)	((ll) < 0)
#define ll_gez(ll)	((ll) >= 0)
#define ll_eqz(a)	((a) == 0)
#define ll_eq(a, b)	((a) == (b))
#define ll_ne(a,b)	((a) != (b))
#define ll_ge(a,b)	((a) >= (b))
#define ll_le(a,b)	((a) <= (b))
#define ll_lt(a,b)	((a) < (b))
#define ll_gt(a,b)	((a) > (b))

#define ll_zero_const	((int64_t) 0)
#define ll_one_const	((int64_t) 1)

extern void ll2str(int64_t a, char *s, char *limit);
extern int64_t ll_shl(int64_t a, int bits);

#include <stdlib.h>	/* For malloc() and free() */

#endif /* !_WIN32_TYPEDEF_MD_H_ */
