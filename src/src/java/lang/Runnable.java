/*
 * @(#)Runnable.java	1.10 95/07/25  
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

package java.lang;

/**
 * This interface is designed to provide a common protocol for Objects that wish to execute 
 * code while they are active. For example, Runnable is implemented by class Thread.  
 * Being active simply means that a thread has been started and has not yet been stopped. 
 * <p>
 * In addition, Runnable provides the means for a class to be active while 
 * not subclassing Thread. A class that implements Runnable can run without
 * subclassing Thread by instantiating a Thread instance and passing itself in
 * as the target.  In most cases, the Runnable interface should be used if you are only planning 
 * to override the run() method and no other Thread methods.  
 * This is important because classes should not be subclassed
 * unless the programmer intends on modifying or enhancing the fundamental
 * behavior of the class.
 * @see 	Thread
 * @version 	1.10, 07/25/95
 * @author	Arthur van Hoff
 */
public
interface Runnable {
    /**
     * The method that is executed when a Runnable object is activated.  The run() method
     * is the "soul" of a Thread.  It is in this method that all of the action of a 
     * Thread takes place.
     * @see Thread#run
     */
    public abstract void run();
}
