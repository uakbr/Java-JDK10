/*
 * @(#)ConditionLock.java	1.7 95/01/31  
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

/**
 * ConditionLock is a Lock with a built in state variable.  This class
 * provides the ability to wait for the state variable to be set to a
 * desired value and then acquire the lock.<p>
 *
 * The lockWhen() and unlockWith() methods can be safely intermixed
 * with the lock() and unlock() methods. However if there is a thread
 * waiting for the state variable to become a particular value and you
 * simply call Unlock(), that thread will not be able to acquire the
 * lock until the state variable equals its desired value. <p>
 *
 * @version 	1.7, 31 Jan 1995
 * @author 	Peter King
 */
public final
class ConditionLock extends Lock {
    private int state = 0;

    /**
     * Creates a ConditionLock.
     */
    public ConditionLock () {
    }

    /**
     * Creates a ConditionLock in an initialState.
     */
    public ConditionLock (int initialState) {
	state = initialState;
    }

    /**
     * Acquires the lock when the state variable equals the desired
     * state.
     * @param desiredState the desired state
     */
    public synchronized void lockWhen(int desiredState) {
	while (state != desiredState) {
	    wait();
	}
	lock();
    }

    /**
     * Releases the lock, and sets the state to a new value.
     * @param newState the new state
     */
    public synchronized void unlockWith(int newState) {
	state = newState;
	unlock();
    }
}
