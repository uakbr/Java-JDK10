/*
 * @(#)RadioGroup.java	1.6 95/01/31 Sami Shaio
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
 * This class is used to create a multiple-exclusion scope for a set
 * of Toggle buttons. For example, creating a set of toggle buttons
 * with the same RadioGroup object means that only one of those toggle
 * buttons will be allowed to be "on" at a time.
 *
 * @version 1.6 31 Jan 1995
 * @author Sami Shaio
 */
public class RadioGroup {
    public RadioGroup() {
    }

    public Toggle	getCurrent() {
	return currentChoice;
    }

    public void setCurrent(Toggle t) {
	if (currentChoice != null && currentChoice != t) {
	    currentChoice.setState(false);
	}
	currentChoice = t;
    }

    private Toggle	currentChoice = null;
}
