/*
 * @(#)AppletContext.java	1.13 95/12/14 Arthur van Hoff
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

import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.ColorModel;
import java.net.URL;
import java.util.Enumeration;

/**
 * This interface corresponds to an applet's environment. It can be
 * used by an applet to obtain information from the applet's
 * environment, which is usually the browser or the applet viewer.
 *
 * @version 	1.13, 12/14/95
 * @author 	Arthur van Hoff
 */
public interface AppletContext {
    /**
     * Gets an audio clip.
     */
    AudioClip getAudioClip(URL url);

    /**
     * Gets an image. This usually involves downloading it
     * over the net. However, the environment may decide to
     * cache images. This method takes an array of URLs,
     * each of which will be tried until the image is found.
     */
    Image getImage(URL url);

    /**
     * Gets an applet by name. 
     * @return null if the applet does not exist.
     */
    Applet getApplet(String name);

    /**
     * Enumerates the applets in this context. Only applets
     * that are accessible will be returned. This list always
     * includes the applet itself.
     */
    Enumeration getApplets();

    /**
     * Shows a new document. This may be ignored by
     * the applet context.
     */
    void showDocument(URL url);

    /**
     * Show a new document in a target window or frame. This may be ignored by
     * the applet context.
     *
     * This method accepts the target strings:
     *   _self		show in current frame
     *   _parent	show in parent frame
     *   _top		show in top-most frame
     *   _blank		show in new unnamed top-level window
     *   <other>	show in new top-level window named <other>
     */
    public void showDocument(URL url, String target);

    /**
     * Show a status string.
     */
    void showStatus(String status);
}
