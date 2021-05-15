/*
 * @(#)Alignable.java	1.5 95/03/14 Jonathan Payne, Arthur van Hoff
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

package browser;

/**
 * If a DisplayItem implements this interface it can
 * be aligned like an img tag.
 *
 * @see		AppletDisplayItem
 * @see		WRImageItem
 * @author	Arthur van Hoff
 * @version 	1.5, 14 Mar 1995
 */
public interface Alignable {
    static final int A_NONE 		= 0;
    static final int A_TOP 		= 1;
    static final int A_TEXTTOP 		= 2;
    static final int A_MIDDLE 		= 3;
    static final int A_ABSMIDDLE 	= 4;
    static final int A_BASELINE 	= 5;
    static final int A_BOTTOM 		= 6;
    static final int A_ABSBOTTOM 	= 7;
    static final int A_NOIMAGE 		= 8;

    public abstract int getAlign();
}
