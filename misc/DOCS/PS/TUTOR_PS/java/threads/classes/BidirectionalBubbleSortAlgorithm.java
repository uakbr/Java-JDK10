/*
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
 * @(#)BidirectionalBubbleSortAlgorithm.java	1.6 95/01/31 James Gosling
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

/**
 * A bi-directional bubble sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author James Gosling
 * @version 	1.6, 31 Jan 1995
 */
class BidirectionalBubbleSortAlgorithm extends SortAlgorithm {
    void sort(int a[]) {
	int j;
	int limit = a.length;
	int st = -1;
	while (st < limit) {
	    boolean flipped = false;
	    st++;
	    limit--;
	    for (j = st; j < limit; j++) {
		if (stopRequested) {
		    return;
		}
		if (a[j] > a[j + 1]) {
		    int T = a[j];
		    a[j] = a[j + 1];
		    a[j + 1] = T;
		    flipped = true;
		    pause(st, limit);
		}
	    }
	    if (!flipped) {
		return;
	    }
	    for (j = limit; --j >= st;) {
		if (stopRequested) {
		    return;
		}
		if (a[j] > a[j + 1]) {
		    int T = a[j];
		    a[j] = a[j + 1];
		    a[j + 1] = T;
		    flipped = true;
		    pause(st, limit);
		}
	    }
	    if (!flipped) {
		return;
	    }
	}
	pause(st, limit);
    }
}
