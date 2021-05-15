/*
 * @(#)ThreadDeath.java	1.1 95/02/20  
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
 * An instance of ThreadDeath is thrown in the victim thread when
 * thread.stop() is called.  This is not a subclass of Exception
 * because too many people already catch Exception.  Only thopse
 * explicitly interested in cleaning up when being asynchronously
 * terminated should catch instances of this class, and then they
 * should be sure to rethrow the object when they are done.
 * @version 	1.1, 20 Feb 1995
 */
public class ThreadDeath extends Object {}
