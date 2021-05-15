/*
 * @(#)ObserverList.java	1.5 95/03/14 Jonathan Payne
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

package browser;

import java.util.Vector;

/*
 * This class is used to hold the set of observers of an observable
 * object whenever there is more than one observer.
 */
public class ObserverList extends Vector {
    /** Notifies all the observers in the list.  This goes from
	back to front, so that it's OK to remove Observers from
	the list as a result of this call. */
    public void notifyObservers(Observable who) {
	int i = size();

	while (--i >= 0) {
	    Observer o;

	    o = (Observer) elementAt(i);
	    o.update(who);
	}
    }
}

