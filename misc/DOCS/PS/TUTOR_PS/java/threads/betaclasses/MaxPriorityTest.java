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
class MaxPriorityTest {
    public static void main(String args[]) {

	ThreadGroup groupNORM = new ThreadGroup(
				"A group with normal priority");
	Thread priorityMAX = new Thread(groupNORM, 
				"A thread with maximum priority");

    // set Thread's priority to max (10)
	priorityMAX.setPriority(Thread.MAX_PRIORITY);

    // set ThreadGroup's max priority to normal (5)
	groupNORM.setMaxPriority(Thread.NORM_PRIORITY);

	System.out.println("Group's maximum priority = " +
		groupNORM.getMaxPriority());
	System.out.println("Thread's priority = " +
		priorityMAX.getPriority());
    }
}
