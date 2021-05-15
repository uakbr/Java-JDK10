/*
 * @(#)Applet.java	1.32 95/03/20 Chris Warth, Arthur van Hoff
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

import java.io.*;
import java.util.*;
import awt.*;
import net.www.html.*;
import browser.audio.*;

/**
 * Base applet class. All applets are subclasses of this class.
 * Once the applet is loaded it is associated with an AppletDisplayItem
 * in which it can draw. An applet's life is summarized by four methods:
 * <dl>
 * <dt>init()<dd>This method is called after the applet is created.
 * The applet can use this method to resize itself, download resources, get fonts,
 * get colors, etc.
 * <dt>start()<dd>This method is called when the applet's document is visited.
 * The applet can use this method to start a background thread for animation, to  play
 * a sound etc.
 * <dt>stop()<dd>This method is called when the applet's document is no longer on
 * the screen. The applet should stop any thread it has forked and any long sounds
 * that are playing. It is garanteed to be called before the applet is destroyed.
 * <dt>destroy()<dd>This method is called when the applet is discarded. It is the
 * last opportunity for the applet to clean up its act! Calls stop() if the applet
 * is still active.
 * </dl>
 * When an applet is started it cannot directly draw to the screen. Instead it needs
 * to call repaint(). This will cause the paint(Graphics) method of the applet to be
 * called as soon as possible. This mechanism is needed so that the applet will not
 * get confused when the document in which it is embedded is scrolled or resized.
 * <p>
 * The applet class contains various method that help you get images, get fonts,
 * play audio, etc. If you can use these methods instead of doing the work yourself,
 * they are more likely to be supported in the future.
 * <p>
 * Here is a very simple example of an applet.
 * <pre>
 *	import awt.*;
 *	import browser.*;
 * 	public class HelloInternet extends Applet {
 *	    public void init() {
 *		resize(150, 25);
 *	    }
 *	    public void paint(Graphics g) {
 *		g.drawString("Hello Internet!", 5, 20);
 *	    }
 *	}
 * </pre>
 * To try it out you need to add this in an html file.
 * <pre>
 * 	&lt;app class="HelloInternet"&gt;
 * </pre>
 * @see AppletDisplayItem
 * @author Chris Warth
 * @author Arthur van Hoff
 * @version 	1.32, 20 Mar 1995
 */
public
class Applet {
    boolean	inFocusList = false;
      
    /**
     * The display item in which this applet is being displayed.
     * Don't modify it.
     */
    public AppletDisplayItem item;

    /**
     * The URL of the document in which the applet is embedded.
     * Don't modify it.
     */
    public URL documentURL;

    /**
     * The URL of the applet. This can differ from the documentURL
     * when a "src" attribute is specified in the app tag.
     * Don't modify it.
     */
    public URL appletURL;

    /**
     * The tag. Use getAttribute() to get attributes in the tag.
     * Don't modify it.
     * @see Applet#getAttribute
     */
    public TagRef tag;

    /**
     * The width of the applet. Use resize() to change the size of
     * the applet and the display item in which it is located.
     * You can change it by calling resize().
     * @see Applet#resize
     */
    public int width;

    /**
     * The height of the applet. Use resize() to change the size of
     * the applet and the display item in which it is located.
     * You can change it by calling resize().
     * @see Applet#resize
     */
    public int height;

    /**
     * The font of the applet. This font will be set when the paint()
     * method is called. If you want to modify it, you need to do this
     * in the init() method. This variable is initialized to an initial
     * value, feel free to modify it.
     * @see Applet#paint
     * @see Applet#init
     */
    public Font font;

    /**
     * The foreground color of the applet. This color will be the foreground
     * color when the paint() method is called. This variable is initialized
     * to an initial value, feel free to modify it.
     * @see Applet#paint
     */
    public Color fgColor;

    /**
     * The background color of the applet. This color is the background
     * color of the window the applet is in when the applet was created.
     *
     * @see Applet#paint
     */
    public Color bgColor;

    /**
     * Gets the parent, I know this is not great, but the formatter
     * sometimes nukes the display item parent. I don't have time
     * to fix the formatter so this is the best I can do!
     */
    private Window getParent() {
	Window w = null;
	while (((w = item.parent) == null) &&
	       ((item.getStatus() == AppletDisplayItem.STARTED) ||
		(item.getStatus() == AppletDisplayItem.INITIALIZING))) {
	    Thread.currentThread().sleep(10);
	}
	return w;
    }

    /**
     * Repaints the applet, this will actually happen at
     * some later time. To actually paint the applet HotJava
     * will call the paint() method.
     * @see Applet#paint
     */
    public void repaint() {
	item.requestUpdate();
    }

    /**
     * Resizes the applet. You should resize the applet in the init() method.
     * Resizing the applet at another time may cause the document to be reformatted.
     * Don't worry about calling repaint(), resize will do the right thing for you.
     * @see Applet#repaint
     */
    public void resize(int width, int height) {
	if (this.width == width && this.height == height &&
	    item.width == width && item.height == height) {
	    // Avoid unnecessary relayouts.
	    // Some badly written Applets may accidentally set the
	    // width and height fields directly so both the Applet
	    // and the item demensions are checked.
	    return;
	}
	item.resize(width, height);
	this.width = width;
	this.height = height;

	// Any time other than during the init() method this
	// will cause the document to be relayedout.
	if ((item.getStatus() != AppletDisplayItem.INITIALIZING) && (item.parent != null)) {
	    ((browser.WRWindow)item.parent).relayout();
	}
    }

    /**
     * Returns true if the applet is started. This will be true from just before
     * start() is called until just after stop() is called.
     */
    public boolean isActive() {
	return item.getStatus() == AppletDisplayItem.STARTED;
    }

    /**
     * Gets an attribute out of theapplet's app tag. Note that the
     * width and height attributes are used to determine the initial
     * dimensions of the applet.
     * @return the attribute value or null if the attribute is not defined.
     */
    public String getAttribute(String name) {
	return tag.getAttribute(name);
    }

    /**
     * Gets an image given an image name. The name is assumed to be
     * relative to the appletURL. If the image can't be found there,
     * the documentURL is  used.
     * @return the image or null if something went wrong.
     */
    public Image getImage(String name) {
	Image img = getImage(new URL(appletURL, name));
	return (img != null) ? img : getImage(new URL(documentURL, name));
    }

    /**
     * Gets an image given a URL.
     * @return the image or null if something went wrong.
     */
    public Image getImage(URL url) {
	Window parent = getParent();
	if (parent != null) {
	    try {
		ImageHandle h = ImageCache.lookupHandle(parent, url);
		Object img;

		img = h.getImage(null, true);
		if (img instanceof Image) {
		    return (Image) img;
		}
	    } catch (IOException ex) {
	    } catch (FileNotFoundException ex) {
	    }
	}
	return null;
    }

    /**
     * Gets audio data given a name. The name is assumed to be
     * relative to the appletURL. If the data can't be found there,
     * the documentURL is  used.
     * @return the audio data or null if it could not be found.
     * @see Applet#play
     */
    public AudioData getAudioData(String name) {
	AudioData data = getAudioData(new URL(appletURL, name));
	if (data == null) {
	    data = getAudioData(new URL(documentURL, name));
	}
	return data;
    }

    /**
     * Gets audio data given a url.
     * @return the audio data or null if the URL is invalid.
     * @see Applet#play
     */
    public AudioData getAudioData(URL url) {
	try {
	    return AudioData.getAudioData(url);
	} catch (IOException ex) {
	    return null;
	} catch (FileNotFoundException ex) {
	    return null;
	}
    }

    /**
     * Gets an audio stream given a url. The actual audio data
     * can be very large because it will be read as the audio
     * is being played.
     * @return the stream or null if the URL is invalid.
     * @see Applet#startPlaying
     */
    public InputStream getAudioStream(URL url) {
	try {
	    return new AudioStream(url.openStream());
	} catch (IOException ex) {
	    return null;
	} catch (FileNotFoundException ex) {
	    return null;
	}
    }

    /**
     * Gets a continuous audio stream given a URL. Note that
     * all of the data will read before the stream can be used.
     * @return the stream or null if the URL is invalid.
     * @see Applet#startPlaying
     */
    public InputStream getContinuousAudioStream(URL url) {
	AudioData data = getAudioData(url);
	return (data != null) ? new ContinuousAudioDataStream(getAudioData(url)) : null;
    }
    
    /**
     * Plays an audio sample. The data is obtained using
     * getAudioData(). Nothing happens if the data could not
     * be found.
     * @see Applet#getAudioData
     */
    public void play(String name) {
	play(getAudioData(name));
    }
    
    /**
     * Plays an audio sample.
     */
    public void play(AudioData data) {
	if (data != null) {
	    AudioPlayer.player.start(new AudioDataStream(data));
	}
    }
    
    /**
     * Starts playing a stream of audio data. Use stopPlaying to
     * stop the audio from playing.
     * @see Applet#getAudioStream
     * @see Applet#stopPlaying
     */
    public void startPlaying(InputStream stream) {
	if (stream != null) {
	    AudioPlayer.player.start(stream);
	}
    }
    
    /**
     * Stops playing a stream of audio data.
     * @see Applet#startPlaying
     */
    public void stopPlaying(InputStream stream) {
	if (stream != null) {
	    AudioPlayer.player.stop(stream);
	}
    }

    /**
     * Shows a status string at the bottom of the HotJava window.
     */
    public void showStatus(String msg) {
	if (item.parent != null) {
	    ((WRWindow)item.parent).status((msg != null) ? msg : "");
	}
    }

    /**
     * Shows a Document.
     */
    public void showDocument(URL doc) {
	if (item.parent != null) {
	    ((WRWindow)item.parent).pushURL(doc);
	}
    }

    /**
     * Gets a font with the given  font name and size.
     * @see awt.Font
     */
    public Font getFont(String name, int size) {
	return getFont(name, Font.PLAIN, size);
    }

    /**
     * Gets a font with the given  font name, style, and size.
     * @see awt.Font
     */
    public Font getFont(String name, int style, int size) {
	Window parent = getParent();
	if (parent != null) {
	    return parent.wServer.fonts.getFont(name, style, size);
	}
	return null;
    }

    /**
     * Gets a Color.
     * @see awt.Color
     */
    public Color getColor(int r, int g, int b) {
	Window parent = getParent();
	if (parent != null) {
	    return new Color(parent.wServer, r, g, b);
	}
	return null;
    }

    /**
     * Initializes the applet.
     * You never need to call this directly, it is called automatically
     * by HotJava once the applet is created.
     */
    protected void init() {
    }

    /**
     * Called to start the applet. You never need to call this method
     * directly it is called when the applet's document is visited.
     * @see #stop
     */
    protected void start() {
    }

    /**
     * Called to stop the applet. It is called when the applet's document is
     * no longer on the screen. It is guaranteed to be called before destroy()
     * is called. You never need to call this method directly.
     * @see #start
     * @see #destroy
     */
    protected void stop() {
    }

    /**
     * Cleans up whatever resources are being held. If the applet is active
     * it is first stopped.
     * @see #stop
     */
    protected void destroy() {
    }

    /**	
     * Paints the applet, given a graphics context.
     * The origin will be in the topleft corner of the applet.
     * The clipping area is set to the exact size of the applet.
     * The font, foreground color and  background color are set to
     * default values.<p>
     * <em>You never have to call this method explicitly!</e>
     * It will be called automatically by HotJava in response
     * to damage or expose events, or when you call repaint().
     * @see Applet#repaint
     */
    public void paint(Graphics g) {
	g.paint3DRect(0, 0, width, height, false, true);
    }

    public void update(Graphics g) {
	g.clearRect(0, 0, width, height);
	paint(g);
    }

    /**
     * Mouse down. The x,y coordinates are relative to the
     * applet's top left corner. A call to mouseUp() is
     * guaranteed to follow when the mouse is released.
     * mouseDrag is called when the mouse is moved.
     * @see #mouseUp
     * @see #mouseDrag
     */
    public void mouseDown(int x, int y) {
    }

    /**
     * Mouse drag (the mouse button is down). The x,y coordinates
     * are relative to the applet's top left corner.
     */
    public void mouseDrag(int x, int y) {
    }

    /**
     * Mouse up. The x,y coordinates are relative to the
     * applet's top left corner. This must have been preceded by
     * a call to mouseDown().
     * @see #mouseDown
     */
    public void mouseUp(int x, int y) {
    }

    /**
     * Mouse move (the mouse button is up). The x,y coordinates
     * are relative to the applet's top left corner.
     */
    public void mouseMove(int x, int y) {
    }

    /**
     * Called when the mouse enters the applet (regardless of
     * the mouse button state). A call to mouseExit is guaranteed
     * to follow.
     * @see #mouseExit
     */
    public void mouseEnter() {
    }

    /**
     * Called when the mouse exits the applet (regardless of
     * the mouse button state). Must have been preceded by a
     * call to mouseEnter().
     * @see #mouseEnter
     */
    public void mouseExit() {
    }

    /**
     * Gets the focus. This is usually called in the
     * mouseDown() method. A gotFocus() call will follow.
     * @see Applet#gotFocus
     * @see Applet#mouseDown
     */
    public void getFocus() {
	if (! inFocusList) {
	    item.parent.fm.addItem(item);
	    inFocusList = true;
	}
	(item.parent.fm).grabFocus(item);
    }

    /**
     * Got focus. The user can now type into the applet.
     * @see Applet#keyDown
     */
    public void gotFocus() {
    }

    /**
     * Lost focus.
     */
    public void lostFocus() {
    }

    /**
     * A character is typed inside the applet and it has
     * the focus.
     */
    public void keyDown(int key) {
    }
}
