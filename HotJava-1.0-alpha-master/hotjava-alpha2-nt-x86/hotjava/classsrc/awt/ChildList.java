/*
 * @(#)ChildList.java	1.6 95/01/31 Jon Payne
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

/*-
 *	Named Window/DisplayItem Container hierarchy.
 */

package awt;

import awt.*;
import java.util.*;

/**
 * A class used to map names to Layoutable objects in a container
 * hierarchy. 
 *
 * @version 1.6 31 Jan 1995
 * @author Jon Payne
 */
public class ChildList extends Hashtable {
    Vector    list = new Vector(0);

    /**
     * Adds a Layoutable with the given name to this ChildList object.
     */
    public void addChild(Layoutable child, String name) {
	if (name != null) {
	    if (containsKey(name) || list.indexOf(child) != -1)
		throw new Exception("Duplicate children in window: " + name);
	    put(name, child);
	}
	list.addElement(child);
    }

    /**
     * Return the Layoutable object with the given name.
     * @returns the Layoutable if found or null otherwise.
     */
    public Layoutable getChild(String name) {
	if (containsKey(name))
	    return (Layoutable) get(name);
	else
	    return null;
    }

    /**
     * Return the Layoutable child given an index.
     */
    public Layoutable getChild(int index) {
	//if (index < 0 || index >= list.size())
	//    return null;
	return (Layoutable) list.elementAt(index);
    }

    /**
     * Return the number of children.
     */
    public int length() {
	return list.size();
    }
}
