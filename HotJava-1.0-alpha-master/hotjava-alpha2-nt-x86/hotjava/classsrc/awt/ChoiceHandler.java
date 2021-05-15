/*
 * @(#)ChoiceHandler.java	1.5 95/01/31 Sami Shaio
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

/**
 * An interface used by various awt Components to map callbacks.
 *
 * @version 1.5 31 Jan 1995
 * @author Sami Shaio
 */
public interface ChoiceHandler {
    /**
     * Callback that denotes the user double-clicked on the given
     * component. If the component supports multiple items, pos will be
     * the index of the item that was selected.
     */
    abstract void doubleClick(Component c, int pos);

    /**
     * Callback that denotes the user selected the given component. If
     * the component supports multiple items, pos will be the index of
     * the item that was selected. 
     */
    abstract void selected(Component c, int pos);
}
