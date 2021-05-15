/*
 * @(#)HistoryVector.java	1.9 95/03/14 Herb Jellinek
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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

public class HistoryVector extends Vector {

    /**
     * sizeRestriction: maximum number of elements this can have.
     */
    int sizeRestriction;

    /**
     * timeStamp: lets observers of this object know if it's been changed
     * since the last time they looked.
     */
    int timeStamp;

    /**
     * currentElement: index of the page we're now on, for highlighting.
     */
    int currentElement;

    /**
     * HistoryVector(maxSize): create a vector of maximum size maxSize.
     */
    public HistoryVector(int maxSize) {
	super(maxSize);
	sizeRestriction = maxSize;
	timeStamp = 0;
	currentElement = 0;
    }

    /**
     * HistoryVector(): create a HistoryVector that grows indefinitely.
     */
    public HistoryVector() {
	super(30);
	sizeRestriction = -1;	// no restriction
	timeStamp = 0;
	currentElement = 0;
    }

    /**
     * getTimeStamp: returns the current timeStamp.
     */
    public final synchronized int getTimeStamp() {
	return timeStamp;
    }

    /**
     * getCurrent: returns the current element to be highlighted
     */
    public final synchronized int getCurrent() {
	return currentElement;
    }

    
    /**
     * selectElement: adds delta to the current element.  Callers make sure
     * not to underflow or overflow.
     */
    public final synchronized void deltaSelect(int delta) {
	currentElement += delta;
	timeStamp++;
    }


    /**
     * pushElement: like addElement (which I can't override, since it's final!)
     * but throws out the first element when the vector reaches sizeRestriction
     * elements.  If sizeRestriction set to -1, list will grow forever.
     */
    public synchronized void pushElement(DocumentInfo info) {
	if (size() == sizeRestriction) {
	    removeElement(firstElement());
	}
	addElement(info);
	currentElement = size() - 1;
	timeStamp++;
    }
}

