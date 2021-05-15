/*
 * @(#)AppletStub.java	1.8 95/12/14 Arthur van Hoff
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
package java.applet;

import java.net.URL;

/**
 * This interface is used to implement an applet viewer. It is not
 * normally used by applet programmers.
 *
 * @version 	1.8, 12/14/95
 * @author 	Arthur van Hoff
 */
public interface AppletStub {
    /**
     * Returns true if the applet is active. 
     */
    boolean isActive();
    
    /**
     * Gets the document URL.
     */
    URL getDocumentBase();

    /**
     * Gets the base URL.
     */
    URL getCodeBase();

    /**
     * Gets a parameter of the applet.
     */
    String getParameter(String name);

    /**
     * Gets a handler to the applet's context.
     */
    AppletContext getAppletContext();

    /**
     * Called when the applet wants to be resized.
     */
    void appletResize(int width, int height);
}
