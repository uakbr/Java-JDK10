/*
 * @(#)CheckboxGroup.java	1.11 95/11/14 Sami Shaio
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

/**
 * This class is used to create a multiple-exclusion scope for a set
 * of Checkbox buttons. For example, creating a set of Checkbox buttons
 * with the same CheckboxGroup object means that only one of those Checkbox
 * buttons will be allowed to be "on" at a time.
 *
 * @version 	1.11 11/14/95
 * @author 	Sami Shaio
 */
public class CheckboxGroup {
    /**
     * The current choice.
     */
    Checkbox currentChoice = null;

    /**
     * Creates a new CheckboxGroup.
     */
    public CheckboxGroup() {
    }

    /**
     * Gets the current choice.
     */
    public Checkbox getCurrent() {
	return currentChoice;
    }

    /**
     * Sets the current choice to the specified Checkbox.
     * If the Checkbox belongs to a different group, just return.
     * @param box the current Checkbox choice
     */
    public synchronized void setCurrent(Checkbox box) {
	if (box != null && box.group != this) {
	    return;
	}
	Checkbox oldChoice = this.currentChoice;
	this.currentChoice = box;
	if ((oldChoice != null) && (oldChoice != box)) {
	    oldChoice.setState(false);
	}
	if (box != null && oldChoice != box && !box.getState()) {
	    box.setStateInternal(true);
	}
    }

    /**
     * Returns the String representation of this CheckboxGroup's values.
     * Convert to String.
     */
    public String toString() {
	return getClass().getName() + "[current=" + currentChoice + "]";
    }
}
