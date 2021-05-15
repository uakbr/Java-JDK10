/*
 * @(#)FocusManager.java	1.6 95/01/31 David Brown
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

import java.util.*;

/**
 * A class that distributes the focus to a set of focus clients.
 * In order to be able to call grabFocus, a DisplayItem should
 * register with the FocusManager using the addItem method.
 *
 * @see DisplayItem
 * @see FocusManager#addItem
 * @see FocusManager#grabFocus
 * @version 1.6 31 Jan 1995
 * @author David Brown
 */
public class FocusManager {

    public DisplayItemWindow diw;

    Vector itemlist = new Vector();
    int current_index = 0;

    DisplayItem oldFocusItem = null;	// See notifyFocusItems()

    public FocusManager (DisplayItemWindow theDiw) {
	diw = theDiw;
    }

    /** Add a DisplayItem to our list.  This should be an item
     *  that's interested in getting keyboard events, and should
     *  do something useful with got/lostFocus() events... */
    public void addItem(DisplayItem item) {
	itemlist.addElement(item);

	// If this is the very first guy being added to the list,
	// give him the focus.  Otherwise, don't affect the focus at all.
	if (itemlist.size() == 1) {
	    current_index = 0;
	    notifyFocusItems();
	}
        //System.out.println("FocusManager.addItem:  itemlist.size() now "+itemlist.size());
    }

    /** An item sends us this message to ask for the focus */
    public void grabFocus(DisplayItem item) {
	//System.out.println("FocusManager.grabFocus:  ");
	//System.out.println("  this FM is "+this);
	//System.out.println("  item is "+item);
	//System.out.println("  itemlist.size() is "+itemlist.size() );

	if (item != oldFocusItem) {
	    int found = itemlist.indexOf(item);
	    if (found == -1) {
		throw new Exception("grabFocus: item not in itemlist");
	    }
	    current_index = found;
	    notifyFocusItems();
	}
    }

    /** Advance the focus to the next item in the list */
    public void nextFocus() {
	if (++current_index >= itemlist.size())
	    current_index = 0;
	notifyFocusItems();
    }

    /** Back-up the focus to the previous item in the list */
    public void prevFocus() {
	if (--current_index < 0)
	    current_index = itemlist.size() - 1;
	if (current_index < 0)
	    current_index = 0;
	notifyFocusItems();
    }

    /** Reset the focus to the first item in the list */
    public void resetFocus() {
	current_index = 0;
	notifyFocusItems();
    }

    /** Return the item which currently has the input focus,
     *  or 0 if there is no item with the input focus. */
    public DisplayItem currentFocusItem() {
	if (itemlist.size() > 0)
	    return (DisplayItem) itemlist.elementAt(current_index);
	else
	    return (DisplayItem) null;
    }

    /** Notify the items that just lost and got the focus.
        This MUST be called any time current_index changes! */
    private void notifyFocusItems() {
	// System.out.println("FocusManager.notifyFocusItems: current_index "+current_index);

	if (itemlist.size() > 0) {
	    DisplayItem newFocusItem =
	    (DisplayItem) itemlist.elementAt(current_index);
	    if (newFocusItem != oldFocusItem) {
		if (oldFocusItem != null)
		    oldFocusItem.lostFocus();
		if (newFocusItem != null)
		    newFocusItem.gotFocus();
		oldFocusItem = newFocusItem;
	    }
	}
    }

}
