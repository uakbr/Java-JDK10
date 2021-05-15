/*
 * @(#)ThreadDeath.java	1.5 95/08/11  
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
 * thread.stop() is called.  This is not a subclass of Exception,
 * but rather a subclass of Error because too many people
 * already catch Exception.  Instances of this class should be caught
 * explicitly only if you are interested in cleaning up when being 
 * asynchronously terminated.  If ThreadDeath is caught, it is important
 * to rethrow the object so that the Thread will actually die.  The top-level
 * error handler will not print out a message if ThreadDeath falls through.
 * @version 	1.5, 08/11/95
 */

public class ThreadDeath extends Error {}
