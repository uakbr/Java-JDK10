/*
 * @(#)Event.java	1.32 95/12/14 Arthur van Hoff
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
package java.awt;

import java.io.*;

/**
 * Event is a platform-independent class that encapsulates events from
 * the local Graphical User Interface(GUI) platform.
 *
 * @version 1.32 12/14/95
 * @author Sami Shaio
 */
public class Event {
    private int data;

    /* Modifier constants */

    /**
     * The shift modifier constant.
     */
    public static final int SHIFT_MASK 		= 1 << 0;

    /**
     * The control modifier constant.
     */
    public static final int CTRL_MASK 		= 1 << 1;

    /** 
     * The meta modifier constant.
     */
    public static final int META_MASK 		= 1 << 2;

    /** 
     * The alt modifier constant.
     */
    public static final int ALT_MASK 		= 1 << 3;

    /* Action keys */
    /** 
     * The home key.
     */
    public static final int HOME 		= 1000;

    /** 
     * The end key. 
     */
    public static final int END 		= 1001;

    /**
     * The page up key.
     */
    public static final int PGUP 		= 1002;

    /**
     * The page down key.
     */
    public static final int PGDN 		= 1003;

    /**
     * The up arrow key.
     */
    public static final int UP 			= 1004;

    /**
     * The down arrow key.
     */
    public static final int DOWN 		= 1005;

    /**
     * The left arrow key.
     */
    public static final int LEFT 		= 1006;

    /**
     * The right arrow key.
     */
    public static final int RIGHT 		= 1007;

    /**
     * The F1 function key.
     */
    public static final int F1			= 1008;

    /**
     * The F2 function key.
     */
    public static final int F2			= 1009;

    /**
     * The F3 function key.
     */
    public static final int F3			= 1010;

    /**
     * The F4 function key.
     */
    public static final int F4			= 1011;

    /**
     * The F5 function key.
     */
    public static final int F5			= 1012;

    /**
     * The F6 function key.
     */
    public static final int F6			= 1013;

    /**
     * The F7 function key.
     */
    public static final int F7			= 1014;

    /**
     * The F8 function key.
     */
    public static final int F8			= 1015;

    /**
     * The F9 function key.
     */
    public static final int F9			= 1016;

    /**
     * The F10 function key.
     */
    public static final int F10			= 1017;

    /**
     * The F11 function key.
     */
    public static final int F11			= 1018;

    /**
     * The F12 function key.
     */
    public static final int F12			= 1019;


    /* Base for all window events. */
    private static final int WINDOW_EVENT 	= 200;

    /**
     * The destroy window event.
     */
    public static final int WINDOW_DESTROY 	= 1 + WINDOW_EVENT;

    /**
     * The expose window event. 
     */
    public static final int WINDOW_EXPOSE 	= 2 + WINDOW_EVENT;

    /** 
     * The iconify window event. 
     */
    public static final int WINDOW_ICONIFY	= 3 + WINDOW_EVENT;

    /** 
     * The de-iconify window event.
     */
    public static final int WINDOW_DEICONIFY	= 4 + WINDOW_EVENT;

    /**
     * The move window event.
     */
    public static final int WINDOW_MOVED	= 5 + WINDOW_EVENT;

    /* Base for all keyboard events. */
    private static final int KEY_EVENT 		= 400;

    /**
     * The key press keyboard event.
     */
    public static final int KEY_PRESS 		= 1 + KEY_EVENT;

    /**
     * The key release keyboard event.
     */
    public static final int KEY_RELEASE 	= 2 + KEY_EVENT;

    /** 
     * The key action keyboard event. 
     */
    public static final int KEY_ACTION 		= 3 + KEY_EVENT;

    /** 
     * The key action keyboard event. 
     */
    public static final int KEY_ACTION_RELEASE	= 4 + KEY_EVENT;

    /* Base for all mouse events. */
    private static final int MOUSE_EVENT 	= 500;

    /**
     * The mouse down event.
     */
    public static final int MOUSE_DOWN 		= 1 + MOUSE_EVENT;

    /**
     *  The mouse up event.
     */
    public static final int MOUSE_UP 		= 2 + MOUSE_EVENT;

    /**
     * The mouse move event.
     */
    public static final int MOUSE_MOVE	 	= 3 + MOUSE_EVENT;

    /**
     * The mouse enter event.
     */
    public static final int MOUSE_ENTER 	= 4 + MOUSE_EVENT;

    /**
     * The mouse exit event.
     */
    public static final int MOUSE_EXIT 		= 5 + MOUSE_EVENT;

    /** 
     * The mouse drag event.
     */
    public static final int MOUSE_DRAG 		= 6 + MOUSE_EVENT;


    /* Scrolling events */
    private static final int SCROLL_EVENT 	= 600;

    /** 
     * The line up scroll event. 
     */
    public static final int SCROLL_LINE_UP	= 1 + SCROLL_EVENT;

    /**
     * The line down scroll event.
     */
    public static final int SCROLL_LINE_DOWN	= 2 + SCROLL_EVENT;

    /**
     * The page up scroll event.
     */
    public static final int SCROLL_PAGE_UP	= 3 + SCROLL_EVENT;

    /**
     * The page down scroll event.
     */
    public static final int SCROLL_PAGE_DOWN	= 4 + SCROLL_EVENT;

    /**
     * The absolute scroll event.
     */
    public static final int SCROLL_ABSOLUTE	= 5 + SCROLL_EVENT;
    
    /* List Events */
    private static final int LIST_EVENT		= 700;

    /* Event sent when an item has been selected */
    public static final int LIST_SELECT		= 1 + LIST_EVENT;

    /* Event sent when an item has been deselected */
    public static final int LIST_DESELECT	= 2 + LIST_EVENT;

    /* Misc Event */
    private static final int MISC_EVENT		= 1000;

    /**
     * An action event.
     */
    public static final int ACTION_EVENT	= 1 + MISC_EVENT;

    /**
     * A file loading event.
     */
    public static final int LOAD_FILE		= 2 + MISC_EVENT;

    /**
     * A file saving event.
     */
    public static final int SAVE_FILE		= 3 + MISC_EVENT;

    /**
     * A component gained the focus.
     */
    public static final int GOT_FOCUS		= 4 + MISC_EVENT;

    /**
     * A component lost the focus.
     */
    public static final int LOST_FOCUS		= 5 + MISC_EVENT;
    
    /**
     * The target component.
     */
    public Object target;

    /**
     * The time stamp.
     */
    public long when;

    /**
     * The type of this event. 
     */
    public int id;

    /** 
     * The x coordinate of the event.
     */
    public int x;

    /** 
     * The y coordinate of the event. 
     */
    public int y;

    /** 
     * The key that was pressed in a keyboard event. 
     */
    public int key;

    /** 
     * The state of the modifier keys.
     */
    public int modifiers;

    /**
     * The number of consecutive clicks. This field is relevant only for
     * MOUSE_DOWN events. If the field isn't set it will be 0. Otherwise,
     * it will be 1 for single-clicks, 2 for double-clicks, and so on.
     */
    public int clickCount;

    /**
     * An arbitraty argument.
     */
    public Object arg;

    /**
     * The next event. Used when putting events into a linked list.
     */
    public Event evt;

    /**
     * Constructs an event with the specified target component, time stamp,
     * event type, x and y coordinates, keyboard key, state of the modifier
     * keys and argument.
     * @param target the target component
     * @param when the time stamp
     * @param id the event type
     * @param x the x coordinate
     * @param y the y coordinate
     * @param key the key pressed in a keyboard event
     * @param modifiers the state of the modifier keys
     * @param arg the specified argument
     */
    public Event(Object target, long when, int id, int x, int y, int key,
		 int modifiers, Object arg) {
	this.target = target;
	this.when = when;
	this.id = id;
	this.x = x;
	this.y = y;
	this.key = key;
	this.modifiers = modifiers;
	this.arg = arg;
	this.data = 0;
	this.clickCount = 0;
    }

    /**
     * Constructs an event with the specified target component, time stamp,
     * event type, x and y coordinates, keyboard key, state of the modifier
     * keys and an argument set to null. 
     * @param target the target component
     * @param when the time stamp
     * @param id the event type
     * @param x the x coordinate
     * @param y the y coordinate
     * @param key the key pressed in a keyboard event
     * @param modifiers the state of the modifier keys
     */
    public Event(Object target, long when, int id, int x, int y, int key, int modifiers) {
	this(target, when, id, x, y, key, modifiers, null);
    }

    /**
     * Constructs an event with the specified target component, 
     * event type, and argument. 
     * @param target the target component
     * @param id the event type
     * @param arg the specified argument
     */
    public Event(Object target, int id, Object arg) {
	this(target, 0, id, 0, 0, 0, 0, arg);
    }

    /** 
     * Translates an event relative to the given component. This
     * involves at a minimum translating the coordinates so they make
     * sense within the given component. It may also involve
     * translating a region in the case of an expose event.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void translate(int x, int y) {
	this.x += x;
	this.y += y;
    }

    /**
     * Checks if the shift key is down.
     * @see #modifiers
     * @see #controlDown
     * @see #metaDown
     */
    public boolean shiftDown() {
	return (modifiers & SHIFT_MASK) != 0;
    }

    /**
     * Checks if the control key is down.
     * @see #modifiers
     * @see #shiftDown
     * @see #metaDown
     */
    public boolean controlDown() {
	return (modifiers & CTRL_MASK) != 0;
    }

    /**
     * Checks if the meta key is down.
     * @see #modifiers
     * @see #shiftDown
     * @see #controlDown
     */
    public boolean metaDown() {
	return (modifiers & META_MASK) != 0;
    }

    /**
     * Returns the parameter String of this Event. 
     */
    protected String paramString() {
	String str = "id=" + id + ",x=" + x + ",y=" + y;
	if (key != 0) {
	    str += ",key=" + key;
	}
	if (shiftDown()) {
	    str += ",shift";
	}
	if (controlDown()) {
	    str += ",control";
	}
	if (metaDown()) {
	    str += ",meta";
	}
	if (target != null) {
	    str += ",target=" + target;
	}
	if (arg != null) {
	    str += ",arg=" + arg;
	}
	return str;
    }

    /**
     * Returns the String representation of this Event's values.
     */
    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }
}
