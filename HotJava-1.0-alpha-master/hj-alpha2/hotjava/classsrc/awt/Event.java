/*
 * @(#)Event.java	1.11 95/01/31 Arthur van Hoff
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
package awt;

import java.io.*;
import java.lang.*;

/**
 * Event is a platform-independent class that encapsulates events from
 * the local GUI platform.
 *
 * @version 1.11 31 Jan 1995
 * @author Sami Shaio
 */
public class Event {
    /** Modifier constants */
    public static final int SHIFT_DOWN = 1;
    public static final int CTRL_DOWN = 2;

    /** Base for all Window events. */
    public static final int WINDOW_EVENT = 100;
    public static final int WINDOW_RESIZE = 1 + WINDOW_EVENT;
    public static final int WINDOW_EXPOSE = 2 + WINDOW_EVENT;
    public static final int WINDOW_DESTROY = 3 + WINDOW_EVENT;

    /** Base for all Frame events. */
    public static final int FRAME_EVENT = 200;
    public static final int FRAME_DESTROY = 1 + FRAME_EVENT;
    public static final int FRAME_EXPOSE = 2 + FRAME_EVENT;
    
    /** Base for all Pointer events. */
    public static final int POINTER_EVENT = 300;
    public static final int POINTER_MOTION = 1 + POINTER_EVENT;

    /** Base for all keyboard events. */
    public static final int KEY_EVENT = 400;
    public static final int KEY_PRESS = 1 + KEY_EVENT;
    public static final int KEY_RELEASE = 2 + KEY_EVENT;

    /** Base for all mouse events. */
    public static final int MOUSE_EVENT = 500;
    public static final int MOUSE_DOWN = 1 + MOUSE_EVENT;
    public static final int MOUSE_UP = 2 + MOUSE_EVENT;
    public static final int MOUSE_MOTION = 3 + MOUSE_EVENT;
    public static final int MOUSE_ENTER = 4 + MOUSE_EVENT;
    public static final int MOUSE_LEAVE = 5 + MOUSE_EVENT;
    public static final int MOUSE_DRAG = 6 + MOUSE_EVENT;

    /** the type of this event. */
    public int id;
    /** the x coordinate of the pointer. */
    public int x;
    /** the y coordinate of the pointer. */
    public int y;
    /** true if a keyboard event is ascii. */
    public boolean keyIsAscii;
    /** the key that was pressed in a keyboard event. */
    public char key;
    /** the object that is the target of an event. */
    public Object obj;

    /** the state of the modifier keys. See modifer constants above.  */
    public int modifiers;

    public Event() {
    }

    public Event(int pId,
		 Object pObj,
		 int pX,
		 int pY,
		 boolean pAscii,
		 char pk,
		 int modifiers) {
	id = pId;
	obj = pObj;
	x = pX;
	y = pY;
	keyIsAscii = pAscii;
	key = pk;
	this.modifiers = modifiers;
    }

    public Event(int pId,
		 Object pObj,
		 int pX,
		 int pY,
		 boolean pAscii,
		 char pk) {
	this(pId, pObj, pX, pY, pAscii, pk, 0);
    }

    public String toString() {
	return getClass().getName() + "(" + id + ")";
    }

    public void print() {
	System.out.println(toString());
    }

    public Object clone() {
	return super.clone();
    }
    public void copy(Object src) {
	super.copy(src);
    }
}
