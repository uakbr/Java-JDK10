/*
 * @(#)Stack.java	1.12 95/08/11  
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
 * A Last-In-First-Out(LIFO) stack of objects.
 *
 * @version 	1.12, 08/11/95
 * @author 	Jonathan Payne
 */
public
class Stack extends Vector {
    /**
     * Pushes an item onto the stack.
     * @param item the item to be pushed on.
     */
    public Object push(Object item) {
	addElement(item);

	return item;
    }

    /**
     * Pops an item off the stack.
     * @exception EmptyStackException If the stack is empty.
     */
    public Object pop() {
	Object	obj;
	int	len = size();

	obj = peek();
	removeElementAt(len - 1);

	return obj;
    }

    /**
     * Peeks at the top of the stack.
     * @exception EmptyStackException If the stack is empty.
     */
    public Object peek() {
	int	len = size();

	if (len == 0)
	    throw new EmptyStackException();
	return elementAt(len - 1);
    }

    /**
     * Returns true if the stack is empty.
     */
    public boolean empty() {
	return size() == 0;
    }

    /**
     * Sees if an object is on the stack.
     * @param o the desired object
     * @return the distance from the top, or -1 if it is not found.
     */
    public int search(Object o) {
	int i = lastIndexOf(o);

	if (i >= 0) {
	    return size() - i;
	}
	return -1;
    }
}
