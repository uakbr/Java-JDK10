/*
 * @(#)typedefs_md.h	1.7 95/01/31  
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
 * Solaris-dependent types for Green threads
 */

#ifndef _SOLARIS_TYPES_MD_H_
#define _SOLARIS_TYPES_MD_H_

#include <sys/types.h>
#include <bool.h>

#ifndef	_UINT64_T
#define	_UINT64_T
typedef unsigned long long uint64_t;
#endif

#ifndef	_INT64_T
#define	_INT64_T
typedef long long int64_t;
#endif

/* use these macros when the compiler supports the long long type */

#define ll_high(a)		((long)((a)>>32))
#define ll_low(a)		((long)(a))
#define int2ll(a)		((int64_t)(a))
#define ll2int(a)		((int)(a))
#define ll_add(a, b)	((a) + (b))
#define ll_and(a, b)	((a) & (b))
#define ll_div(a, b)	((a) / (b))
#define ll_mul(a, b)	((a) * (b))
#define ll_neg(a)		(-(a))
#define ll_not(a)		(~(a))
#define ll_or(a, b)		((a) | (b))
#define ll_shl(a, n)	((a) << (n))
#define ll_shr(a, n)	((a) >> (n))
#define ll_sub(a, b)	((a) - (b))
#define ll_ushr(a, n)	((unsigned long long)(a) >> (n))
#define ll_xor(a, b)	((a) ^ (b))
#define uint2ll(a)		((uint64_t)(unsigned long)(a))
#define ll_mod(a,b)		((a) % (b))

#define float2ll(f)		((int64_t) (f))
#define ll2float(a)		((float) (a))
#define ll2double(a)	((double) (a))
#define double2ll(f)	((int64_t) (f))

/* comparison operators */
#define ll_ltz(ll)		((ll)<0)
#define ll_gez(ll)		((ll)>=0)
#define ll_eqz(a)		((a) == 0)
#define ll_eq(a, b)		((a) == (b))
#define ll_ne(a,b)		!l_leq(a,b)
#define ll_ge(a,b)		ll_gez(ll_sub(a,b))
#define ll_le(a,b)		ll_gez(ll_sub(b,a))
#define ll_lt(a,b)		ll_ltz(ll_sub(a,b))
#define ll_gt(a,b)		ll_ltz(ll_sub(b,a))

#define ll_zero_const	((int64_t) 0)
#define ll_one_const	((int64_t) 1)

extern void ll2str(int64_t a, char *s, char *limit);

#endif /* !_SOLARIS_TYPES_MD_H_ */
