/*
 * @(#)List.java	1.24 95/12/02 Sami Shaio
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
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

import java.util.Vector;
import java.awt.peer.ListPeer;

/**
 * A scrolling list of text items.
 *
 * @version 	1.24, 12/02/95
 * @author 	Sami Shaio
 */
public class List extends Component {
    Vector	items = new Vector();
    int		rows = 0;
    boolean	multipleSelections = false;
    int		selected[] = new int[0];
    int		visibleIndex = -1;

    /**
     * Creates a new scrolling list initialized with no visible Lines
     * or multiple selections.
     */
    public List() {
	this(0, false);
    }

    /**
     * Creates a new scrolling list initialized with the specified 
     * number of visible lines and a boolean stating whether multiple
     * selections are allowed or not.
     * @param rows the number of items to show.
     * @param multipleSelections if true then multiple selections are allowed.
     */
    public List(int rows, boolean multipleSelections) {
	this.rows = rows;
	this.multipleSelections = multipleSelections;
    }

    /**
     * Creates the peer for the list.  The peer allows us to modify the
     * list's appearance without changing its functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createList(this);
	super.addNotify();
	visibleIndex = -1;
    }

    /**
     * Removes the peer for this list.  The peer allows us to modify the
     * list's appearance without changing its functionality.
     */
    public synchronized void removeNotify() {
	if (peer != null) {
	    ListPeer peer = (ListPeer)this.peer;
	    selected = peer.getSelectedIndexes();
	}
	super.removeNotify();
    }
    
    /**
     * Returns the number of items in the list.
     * @see #getItem
     */
    public int countItems() {
	return items.size();
    }

    /**
     * Gets the item associated with the specified index.
     * @param index the position of the item
     * @see #countItems
     */
    public String getItem(int index) {
	return (String)items.elementAt(index);
    }

    /**
     * Adds the specified item to the end of scrolling list.
     * @param item the item to be added
     */
    public synchronized void addItem(String item) {
	addItem(item, -1);
    }

    /**
     * Adds the specified item to the end of scrolling list.
     * @param item the item to be added
     * @param index the position at which to put in the item. The
     * index is zero-based. If index is -1 then the item is added to
     * the end. If index is greater than the number of items in the
     * list, the item gets added at the end. 
     */
    public synchronized void addItem(String item, int index) {
	if (index < -1 || index >= items.size()) {
	    index = -1;
	}
	if (index == -1) {
	    items.addElement(item);
	} else {
	    items.insertElementAt(item, index);
	}
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.addItem(item, index);
	}
    }

    /**
     * Replaces the item at the given index.
     * @param newValue the new value to replace the existing item
     * @param index the position of the item to replace
     */
    public synchronized void replaceItem(String newValue, int index) {
	delItem(index);
	addItem(newValue, index);
    }

    /**
     * Clears the list.
     * @see #delItem
     * @see #delItems
     */
    public synchronized void clear() {
	if (peer != null) {
	    ((ListPeer)peer).clear();
	}
	items = new Vector();
	selected = new int[0];
    }

    /**
     * Delete an item from the list.
     */
    public synchronized void delItem(int position) {
	delItems(position, position);
    }

    /**
     * Delete multiple items from the list.
     */
    public synchronized void delItems(int start, int end) {
	for (int i=end; i >= start; i--) {
	    items.removeElementAt(i);
	}
	if (peer != null) {
	    ((ListPeer)peer).delItems(start, end);
	}
    }

    /**
     * Get the selected item on the list or -1 if no item is selected.
     * @see #select
     * @see #deselect
     * @see #isSelected
     */
    public synchronized int getSelectedIndex() {
	int sel[] = getSelectedIndexes();
	return (sel.length == 1) ? sel[0] : -1;
    }

    /**
     * Returns the selected indexes on the list.
     * @see #select
     * @see #deselect
     * @see #isSelected
     */
    public synchronized int[] getSelectedIndexes() {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    selected = peer.getSelectedIndexes();
	}
	return selected;
    }

    /**
     * Returns the selected item on the list or null if no item is selected.
     * @see #select
     * @see #deselect
     * @see #isSelected
     */
    public synchronized String getSelectedItem() {
	int index = getSelectedIndex();
	return (index < 0) ? null : getItem(index);
    }

    /**
     * Returns the selected items on the list.
     * @see #select
     * @see #deselect
     * @see #isSelected
     */
    public synchronized String[] getSelectedItems() {
	int sel[] = getSelectedIndexes();
	String str[] = new String[sel.length];
	for (int i = 0 ; i < sel.length ; i++) {
	    str[i] = getItem(sel[i]);
	}
	return str;
    }

    /**
     * Selects the item at the specified index.
     * @param index the position of the item to select
     * @see #getSelectedItem
     * @see #deselect
     * @see #isSelected
     */
    public synchronized void select(int index) {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.select(index);
	    return;
	}

	for (int i = 0 ; i < selected.length ; i++) {
	    if (selected[i] == index) {
		return;
	    }
	}
	if (!multipleSelections) {
	    selected = new int[1];
	    selected[0] = index;
	} else {
	    int newsel[] = new int[selected.length + 1];
	    System.arraycopy(selected, 0, newsel, 0, selected.length);
	    newsel[selected.length] = index;
	    selected = newsel;
	}
    }

    /**
     * Deselects the item at the specified index.
     * @param index the position of the item to deselect
     * @see #select
     * @see #getSelectedItem
     * @see #isSelected
     */
    public synchronized void deselect(int index) {
	ListPeer peer = (ListPeer)this.peer;
	if (peer != null) {
	    peer.deselect(index);
	}

	for (int i = 0 ; i < selected.length ; i++) {
	    if (selected[i] == index) {
		int newsel[] = new int[selected.length - 1];
		System.arraycopy(selected, 0, newsel, 0, i);
		System.arraycopy(selected, i+1, newsel, i, selected.length - (i+1));
		selected = newsel;
		return;
	    }
	}
    }

    /**
     * Returns true if the item at the specified index has been selected;
     * false otherwise.
     * @param index the item to be checked
     * @see #select
     * @see #deselect
     * @see #isSelected
     */
    public synchronized boolean isSelected(int index) {
	int sel[] = getSelectedIndexes();
	for (int i = 0 ; i < sel.length ; i++) {
	    if (sel[i] == index) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns the number of visible lines in this list.
     */
    public int getRows() {
	return rows;
    }

    /**
     * Returns true if this list allows multiple selections.
     * @see #setMultipleSelections
     */
    public boolean allowsMultipleSelections() {
	return multipleSelections;
    }

    /**
     * Sets whether this list should allow multiple selections or not.
     * @param v the boolean to allow multiple selections
     * @see #allowsMultipleSelections
     */
    public void setMultipleSelections(boolean v) {
	if (v != multipleSelections) {
	    multipleSelections = v;
	    ListPeer peer = (ListPeer)this.peer;
	    if (peer != null) {
		peer.setMultipleSelections(v);
	    }
	}
    }

    /**
     * Gets the index of the item that was last made visible by the method
     * makeVisible.
     */
    public int getVisibleIndex() {
	return visibleIndex;
    }

    /**
     * Forces the item at the specified index to be visible.
     * @param index the position of the item
     * @see #getVisibleIndex
     */
    public void makeVisible(int index) {
	ListPeer peer = (ListPeer)this.peer;
	visibleIndex = index;
	if (peer != null) {
	    peer.makeVisible(index);
	}
    }

    /**
     * Returns the preferred dimensions needed for the list with the specified
     * amount of rows.
     * @param rows amount of rows in list.
     */
    public Dimension preferredSize(int rows) {
	ListPeer peer = (ListPeer)this.peer;
	return (peer != null) ? peer.preferredSize(rows) : super.preferredSize();
    }

    /**
     * Returns the preferred dimensions needed for the list.
     * @return the preferred size with the specified number of rows if the 
     * row size is greater than 0. 
     * 
     */
    public Dimension preferredSize() {
	return (rows > 0) ? preferredSize(rows) : super.preferredSize();
    }

    /**
     * Returns the minimum dimensions needed for the amount of rows in the 
     * list.
     * @param rows minimum amount of rows in the list
     */
    public Dimension minimumSize(int rows) {
	ListPeer peer = (ListPeer)this.peer;
	return (peer != null) ? peer.minimumSize(rows) : super.minimumSize();
    }

    /**
     * Returns the minimum dimensions needed for the list.
     * @return the preferred size with the specified number of rows if
     * the row size is greater than zero.
     */
    public Dimension minimumSize() {
	return (rows > 0) ? minimumSize(rows) : super.minimumSize();
    }

    /**
     * Returns the parameter String of this list. 
     */
    protected String paramString() {
	return super.paramString() + ",selected=" + getSelectedItem();
    }
}
