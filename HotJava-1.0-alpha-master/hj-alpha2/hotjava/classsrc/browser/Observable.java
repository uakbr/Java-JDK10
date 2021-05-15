/*
 * @(#)Observable.java	1.7 95/03/14 Chris Warth
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

import browser.Observer;
import java.util.Enumeration;
import java.util.Vector;


/*
 * This class should be subclassed by observable objetc, or "data"
 * in the Model-View paradigm.  An Observable object may have any
 * number of Observers.  Whenever the Observable instance changes it
 * notifies all of its observers.  Notification is done by calling
 * the update() method on all observers.
 */
public class Observable {
    private boolean changed = false;
    private Object obs;

    public synchronized void addObserver(Observer o) {
	if (obs != null) {
	    if (obs instanceof ObserverList) {
		if (!((ObserverList) obs).contains(o)) {
		    ((ObserverList) obs).addElement(o);
		}
	    } else if (obs != o) {
		ObserverList tmp = new ObserverList();

		tmp.addElement(obs);
		tmp.addElement(o);
		obs = tmp;
	    }
	} else {
	    obs = o;
	}
    }

    public synchronized void deleteObserver(Observer o) {
	if (obs == o) {
	    obs = null;
	} else if (obs != null && obs instanceof ObserverList) {
	    ((ObserverList) obs).removeElement(o);
	} 
    }

    public synchronized void notifyObservers() {
	if (hasChanged()) {
	    if (obs != null) {
		if (obs instanceof ObserverList) {
		    ((ObserverList) obs).notifyObservers(this);
		} else {
		    ((Observer) obs).update(this);
		}
	    }
	    clearChanged();
	}
    }

    public synchronized void deleteObservers() {
	obs = null;
    }

    protected synchronized void setChanged() {
	changed = true;
    }

    protected synchronized void clearChanged() {
	changed = false;
    }

    public synchronized boolean hasChanged() {
	return changed;
    }

    public synchronized int countObservers() {
	if (obs != null) {
	    if (obs instanceof ObserverList) {
		return ((ObserverList)obs).size();
	    } else {
		return 1;
	    }
	}
	return 0;
    }
}
