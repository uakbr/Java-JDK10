/*
 * @(#)ListPeer.java	1.7 95/11/22 Sami Shaio
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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
package java.awt.peer;

import java.awt.Dimension;

public interface ListPeer extends ComponentPeer {
    int[] getSelectedIndexes();
    void addItem(String item, int index);
    void delItems(int start, int end);
    void clear();
    void select(int index);
    void deselect(int index);
    void makeVisible(int index);
    void setMultipleSelections(boolean v);
    Dimension preferredSize(int v);
    Dimension minimumSize(int v);
}    
