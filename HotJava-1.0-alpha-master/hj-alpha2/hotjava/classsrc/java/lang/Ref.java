/*
 * @(#)Ref.java	1.5 95/02/16
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
 * A "Ref" is an indirect reference to an object that the garbage collector
 * knows about.  The application overrides the reconstitute() method with one
 * that will construct the object based on information in the Ref, often by
 * reading from a file.  The get() method retains a cache of the last call to
 * reconstitute() in the Ref.  When space gets tight, the garbage collector
 * will clear old Ref cache entries when there are no other pointers to the
 * object.
 * @version     1.5, 16 Feb 1995
 */

public class Ref {
    static int	    lruclock;
    private Object  thing;
    private long    priority;

    /**
     * Returns a pointer to the object referenced by this Ref.  If the object
     * has been thrown away by the garbage collector, it will be
     * reconstituted. Does everything necessary to ensure that the garbage
     * collector throws things away in LRU order.  Applications should never
     * need to override this method. get() effectively caches calls to
     * reconstitute().
     */
    public Object get() {
	Object p = thing;
	if (p == null) {
	    /* synchronize if thing is null, but then check again
	       in case somebody else beat us to it */
	    synchronize (this) {
		if (thing == null) {
		    p = reconstitute();
		    thing = p;
		}
	    }
	}
	priority = ++lruclock;
	return thing;
    }

    /**
     * Returns a pointer to the object referenced by this ref by 
     * reconstituting it from some external source.  It shouldn't bother with
     * caching since get() will deal with that.
     *
     * In normal usage, Ref will always be subclassed.  The subclass will add
     * the instance variables necessary for reconstitute() to work.  It will
     * also add a constructor to set them up, and write a version of
     * reconstitute().
     */
    public abstract Object reconstitute();

    /**
     * Flushes the cached object.  Forces the next invocation of get() to
     * invoke reconstitute().
     */
    public void flush() {
	thing = null;
    }

    public void setThing(Object thing) {
	this.thing = thing;
    }

    public Object check() {
	return thing;
    }

    /**
     * Constructs a new Ref.
     */
    public Ref() {
	priority = ++lruclock;
    }
}
