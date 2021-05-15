/*
 * @(#)Observable.java	1.13 95/12/15 Chris Warth
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

package java.util;

/*
 * This class is used to hold the set of observers of an observable
 * object whenever there is more than one observer.
 */
class ObserverList extends Vector {
    /** 
     * Notifies all the observers in the list.  This goes from
     * back to front, so that it is OK to remove Observers from
     * the list as a result of this call. 
     * @param who the list of observers
     * @param arg what is being notified
     */
    public void notifyObservers(Observable who, Object arg) {
	int i = size();

	while (--i >= 0) {
	    Observer o;

	    o = (Observer) elementAt(i);
	    o.update(who, arg);
	}
    }
}


/**
 * This class should be subclassed by observable object, or "data"
 * in the Model-View paradigm.  An Observable object may have any
 * number of Observers.  Whenever the Observable instance changes, it
 * notifies all of its observers.  Notification is done by calling
 * the update() method on all observers.
 *
 * @version 	1.13, 12/15/95
 * @author	Chris Warth
 */
public class Observable {
    private boolean changed = false;
    private Object obs;

    /**
     * Adds an observer to the observer list.
     * @param o the observer to be added
     */
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

    /**
     * Deletes an observer from the observer list.
     * @param o the observer to be deleted
     */
    public synchronized void deleteObserver(Observer o) {
	if (obs == o) {
	    obs = null;
	} else if (obs != null && obs instanceof ObserverList) {
	    ((ObserverList) obs).removeElement(o);
	} 
    }

    /**
     * Notifies all observers if an observable change occurs.
     */
    public void notifyObservers() {
	notifyObservers(null);
    }

    /**
     * Notifies all observers of the specified observable change
     * which occurred.
     * @param arg what is being notified
     */
    public synchronized void notifyObservers(Object arg) {
	if (hasChanged()) {
	    if (obs != null) {
		if (obs instanceof ObserverList) {
		    ((ObserverList) obs).notifyObservers(this, arg);
		} else {
		    ((Observer) obs).update(this, arg);
		}
	    }
	    clearChanged();
	}
    }

    /**
     * Deletes observers from the observer list.
     */
    public synchronized void deleteObservers() {
	obs = null;
    }

    /**
     * Sets a flag to note an observable change.
     */
    protected synchronized void setChanged() {
	changed = true;
    }

    /**
     * Clears an observable change.
     */
    protected synchronized void clearChanged() {
	changed = false;
    }

    /**
     * Returns a true boolean if an observable change has occurred.
     */
    public synchronized boolean hasChanged() {
	return changed;
    }

    /**
     * Counts the number of observers.
     */
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
