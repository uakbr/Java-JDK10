/*
 * @(#)Component.java	1.65 95/12/14 Arthur van Hoff
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
package java.awt;

import java.io.PrintStream;
import java.awt.peer.ComponentPeer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;

/**
 * A generic Abstract Window Toolkit component. 
 *
 * @version 	1.65, 12/14/95
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 */
public abstract class Component implements ImageObserver {
    /**
     * The peer of the component. The peer implements the component's
     * behaviour. The peer is set when the Component is added to a 
     * container that also is a peer.
     * @see #getPeer
     * @see #addNotify
     * @see #removeNotify
     */
    ComponentPeer peer;

    /**
     * The parent of the object. It may be null for toplevel components.
     * @see #getParent
     */
    Container parent;

    /**
     * The x position of the component in the parent's coordinate system.
     * @see #position
     */
    int x;

    /**
     * The y position of the component in the parent's coordinate system.
     * @see #position
     */
    int y;

    /**
     * The width of the component.
     * @see #size
     */
    int width;

    /**
     * The height of the component.
     * @see #size
     */
    int height;

    /**
     * The foreground color for this component.
     * @see #getForeground
     * @see #setForeground
     */
    Color	foreground;

    /**
     * The background color for this component.
     * @see #getBackground
     * @see #setBackground
     */
    Color	background;

    /**
     * The font used by this component.
     * @see #getFont
     * @see #setFont
     */
    Font	font;

    /**
     * True when the object is visible. An object that is not
     * visible is not drawn on the screen.
     * @see #isVisible
     * @see #show
     * @see #hide
     */
    boolean visible = true;

    /**
     * True when the object is enabled. An object that is not
     * enabled does not interact with the user.
     * @see #isEnabled
     * @see #enable
     * @see #disable
     */
    boolean enabled = true;

    /** 
     * True when the object is valid. An invalid object needs to
     * be layed out. This flag is set to false when the object
     * size is changed.
     * @see #isValid
     * @see #validate
     * @see #invalidate
     */
    boolean valid = false;

    /**
     * Constructs a new Component. Components should not be subclassed or 
     * instantiated directly.
     */
    Component() {
    }

    /**
     * Gets the parent of the component.
     */
    public Container getParent() {
	return parent;
    }

    /**
     * Gets the peer of the component.
     */
    public ComponentPeer getPeer() {
	return peer;
    }

    /**
     * Gets the toolkit of the component. This toolkit is
     * used to create the peer for this component.  Note that
     * the Frame which contains a Component controls which
     * toolkit is used so if the Component has not yet been
     * added to a Frame or if it is later moved to a different
     * Frame, the toolkit it uses may change.
     */
    public Toolkit getToolkit() {
	ComponentPeer peer = this.peer;
	if (peer != null) {
	    return peer.getToolkit();
	}
	Container parent = this.parent;
	if (parent != null) {
	    return parent.getToolkit();
	}
	return Toolkit.getDefaultToolkit();
    }

    /**
     * Checks if this Component is valid. Components are invalidated when
     * they are first shown on the screen.
     * @see #validate
     * @see #invalidate
     */
    public boolean isValid() {
	return (peer != null) && valid;
    }

    /**
     * Checks if this Component is visible. Components are initially visible 
     * (with the exception of top level components such as Frame).
     * @see #show
     * @see #hide
     */
    public boolean isVisible() {
	return visible;
    }

    /**
     * Checks if this Component is showing on screen. This means that the 
     * component must be visible, and it must be in a container that is 
     * visible and showing.
     * @see #show
     * @see #hide
     */
    public boolean isShowing() {
	if (visible && (peer != null)) {
	    Container parent = this.parent;
	    return (parent == null) || parent.isShowing();
	}
	return false;
    }

    /**
     * Checks if this Component is enabled. Components are initially enabled.
     * @see #enable
     * @see #disable
     */
    public boolean isEnabled() {
	return enabled;
    }

    /** 
     * Returns the current location of this component.
     * The location will be in the parent's coordinate space.
     * @see #move
     */
    public Point location() {
	return new Point(x, y);
    }

    /** 
     * Returns the current size of this component.
     * @see #resize
     */
    public Dimension size() {
	return new Dimension(width, height);
    }

    /** 
     * Returns the current bounds of this component.
     * @see #reshape
     */
    public Rectangle bounds() {
	return new Rectangle(x, y, width, height);
    }

    /**
     * Enables a component.
     * @see #isEnabled
     * @see #disable
     */
    public synchronized void enable() {
	if (!enabled) {
	    enabled = true;
	    if (peer != null) {
		peer.enable();
	    }
	}
    }

    /**
     * Conditionally enables a component.
     * @param cond if true, enables component; disables otherwise.
     * @see #enable
     * @see #disable
     */
    public void enable(boolean cond) {
	if (cond) {
	    enable();
	} else {
	    disable();
	}
    }

    /**
     * Disables a component.
     * @see #isEnabled
     * @see #enable
     */
    public synchronized void disable() {
	if (enabled) {
	    enabled = false;
	    if (peer != null) {
		peer.disable();
	    }
	}
    }

    /**
     * Shows the component. 
     * @see #isVisible
     * @see #hide
     */
    public synchronized void show() {
	if (!visible) {
	    visible = true;
	    if (peer != null) {
		peer.show();
		if (parent != null) {
		    parent.invalidate();
		}
	    }
	}
    }

    /**
     * Conditionally shows the component. 
     * @param cond if true, it shows the component; hides otherwise.
     * @see #show
     * @see #hide
     */
    public void show(boolean cond) {
	if (cond) {
	    show();
	} else {
	    hide();
	}
    }

    /**
     * Hides the component.
     * @see #isVisible
     * @see #hide
     */
    public synchronized void hide() {
	if (visible) {
	    visible = false;
	    if (peer != null) {
		peer.hide();
		if (parent != null) {
		    parent.invalidate();
		}
	    }
	}
    }

    /**
     * Gets the foreground color. If the component does
     * not have a foreground color, the foreground color
     * of its parent is returned.
     * @see #setForeground
     */
    public Color getForeground() {
	Color foreground = this.foreground;
	if (foreground != null) {
	    return foreground;
	}
	Container parent = this.parent;
	return (parent != null) ? parent.getForeground() : null;
    }

    /** 
     * Sets the foreground color.
     * @param c the Color
     * @see #getForeground
     */
    public synchronized void setForeground(Color c) {
	foreground = c;
	if (peer != null) {
	    c = getForeground();
	    if (c != null) {
		peer.setForeground(c);
	    }
	}
    }

    /**
     * Gets the background color. If the component does
     * not have a background color, the background color
     * of its parent is returned.
     * @see #setBackground
     */
    public Color getBackground() {
	Color background = this.background;
	if (background != null) {
	    return background;
	}
	Container parent = this.parent;
	return (parent != null) ? parent.getBackground() : null;
    }

    /** 
     * Sets the background color.
     * @param c the Color
     * @see #getBackground
     */
    public synchronized void setBackground(Color c) {
	background = c;
	if (peer != null) {
	    c = getBackground();
	    if (c != null) {
		peer.setBackground(c);
	    }
	}
    }

    /**
     * Gets the font of the component. If the component does
     * not have a font, the font of its parent is returned.
     * @see #setFont
     */
    public Font getFont() {
	Font font = this.font;
	if (font != null) {
	    return font;
	}
	Container parent = this.parent;
	return (parent != null) ? parent.getFont() : null;
    }

    /** 
     * Sets the font of the component.
     * @param f the font
     * @see #getFont
     */
    public synchronized void setFont(Font f) {
	font = f;
	if (peer != null) {
	    f = getFont();
	    if (f != null) {
		peer.setFont(f);
	    }
	}
    }

    /**
     * Gets the ColorModel used to display the component on the output device.
     * @see ColorModel
     */
    public synchronized ColorModel getColorModel() {
	if (peer == null) {
	    return getToolkit().getColorModel();
	} else {
	    return peer.getColorModel();
	}
    }

    /** 
     * Moves the Component to a new location. The x and y coordinates
     * are in the parent's coordinate space.
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #location
     * @see #reshape
     */
    public void move(int x, int y) {
	reshape(x, y, width, height);
    }

    /**
     * Resizes the Component to the specified width and height.
     * @param width the width of the component
     * @param height the height of the component
     * @see #size
     * @see #reshape
     */
    public void resize(int width, int height) {
	reshape(x, y, width, height);
    }

    /** 
     * Resizes the Component to the specified dimension.
     * @param d the component dimension
     * @see #size
     * @see #reshape
     */
    public void resize(Dimension d) {
	reshape(x, y, d.width, d.height);
    }

    /** 
     * Reshapes the Component to the specified bounding box.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the component
     * @param height the height of the component
     * @see #bounds
     * @see #move
     * @see #resize
     */
    public synchronized void reshape(int x, int y, int width, int height) {
	boolean resized = (this.width != width) || (this.height != height);

	if (resized || (this.x != x) || (this.y != y)) {
	    this.x = x;
	    this.y = y;
	    this.width = width;
	    this.height = height;
	    if (peer != null) {
		peer.reshape(x, y, width, height);
		if (resized) {
		    invalidate();
		}
		if (parent != null) {
		    parent.invalidate();
		}
	    }
	}
    }

    /** 
     * Returns the preferred size of this component.
     * @see #minimumSize
     * @see LayoutManager
     */
    public Dimension preferredSize() {
	ComponentPeer peer = this.peer;
	return (peer != null) ? peer.preferredSize() : minimumSize();
    }

    /**
     * Returns the minimum size of this component.
     * @see #preferredSize
     * @see LayoutManager
     */
    public Dimension minimumSize() {
	ComponentPeer peer = this.peer;
	return (peer != null) ? peer.minimumSize() : size();
    }

    /**
     * Lays out the component. This is usually called when the
     * component is validated.
     * @see #validate
     * @see LayoutManager
     */
    public void layout() {
    }

    /** 
     * Validates a component.  
     * @see #invalidate
     * @see #layout
     * @see LayoutManager
     */
    public void validate() {
	while ((!valid) && (peer != null)) {
	    valid = true;
	    layout();
	}
    }

    /** 
     * Invalidates a component.
     * @see #validate
     * @see #layout
     * @see LayoutManager
     */
    public void invalidate() {
	valid = false;
    }

    /**
     * Gets a Graphics context for this component. This method will
     * return null if the component is currently not on the screen.
     * @see #paint
     */
    public Graphics getGraphics() {
	ComponentPeer peer = this.peer;
	return (peer != null) ? peer.getGraphics() : null;
    }

    /**
     * Gets the font metrics for this component. This will
     * return null if the component is currently not on the screen.
     * @param font the font
     * @see #getFont
     */
    public FontMetrics getFontMetrics(Font font) {
	ComponentPeer peer = this.peer;
	return (peer != null)
	    ? peer.getFontMetrics(font)
	    : getToolkit().getFontMetrics(font);
    }

    /** 
     * Paints the component.
     * @param g the specified Graphics window
     * @see #update
     */
    public void paint(Graphics g) {
    }

    /** 
     * Updates the component. This method is called in
     * response to a call to repaint. You can assume that
     * the background is not cleared.
     * @param g the specified Graphics window
     * @see #paint
     * @see #repaint
     */
    public void update(Graphics g) {
	g.setColor(getBackground());
	g.fillRect(0, 0, width, height);
	g.setColor(getForeground());
	paint(g);
    }

    /**
     * Paints the component and its subcomponents.
     * @param g the specified Graphics window
     * @see #paint
     */
    public void paintAll(Graphics g) {
	ComponentPeer peer = this.peer;
	if (visible && (peer != null)) {
	    validate();
	    peer.paint(g);
	}
    }

    /** 
     * Repaints the component. This will result in a
     * call to update as soon as possible.
     * @see #paint
     */
    public void repaint() {
	repaint(0, 0, 0, width, height);
    }

    /** 
     * Repaints the component. This will result in a
     * call to update within <em>tm</em> milliseconds.
     * @param tm maximum time in milliseconds before update
     * @see #paint
     */
    public void repaint(long tm) {
	repaint(tm, 0, 0, width, height);
    }

    /** 
     * Repaints part of the component. This will result in a
     * call to update as soon as possible.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width 
     * @param height the height 
     * @see #repaint
     */
    public void repaint(int x, int y, int width, int height) {
	repaint(0, x, y, width, height);
    }

    /** 
     * Repaints part of the component. This will result in a
     * call to update width <em>tm</em> millseconds.
     * @param tm maximum time in milliseconds before update
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width 
     * @param height the height 
     * @see #repaint
     */
    public void repaint(long tm, int x, int y, int width, int height) {
	ComponentPeer peer = this.peer;
	if ((peer != null) && (width > 0) && (height > 0)) {
	    peer.repaint(tm, x, y, width, height);
	}
    }

    /**
     * Prints this component. The default implementation of this
     * method calls paint.
     * @param g the specified Graphics window
     * @see #paint
     */
    public void print(Graphics g) {
	paint(g);
    }

    /**
     * Prints the component and its subcomponents.
     * @param g the specified Graphics window
     * @see #print
     */
    public void printAll(Graphics g) {
	ComponentPeer peer = this.peer;
	if (visible && (peer != null)) {
	    validate();
	    peer.print(g);
	}
    }

    /**
     * Repaints the component when the image has changed.
     * @return true if image has changed; false otherwise.
     */
    public boolean imageUpdate(Image img, int flags,
			       int x, int y, int w, int h) {
	int rate = -1;
	if ((flags & (FRAMEBITS|ALLBITS)) != 0) {
	    rate = 0;
	} else if ((flags & SOMEBITS) != 0) {
	    String isInc = System.getProperty("awt.image.incrementaldraw");
	    if (isInc == null || isInc.equals("true")) {
		String incRate = System.getProperty("awt.image.redrawrate");
		try {
		    rate = (incRate != null) ? Integer.parseInt(incRate) : 100;
		    if (rate < 0)
			rate = 0;
		} catch (Exception e) {
		    rate = 100;
		}
	    }
	}
	if (rate >= 0) {
	    repaint(rate, 0, 0, width, height);
	}
	return (flags & (ALLBITS|ABORT)) == 0;
    }

    /**
     * Creates an image from the specified image producer.
     * @param producer the image producer
     */
    public Image createImage(ImageProducer producer) {
	ComponentPeer peer = this.peer;
	return (peer != null)
	    ? peer.createImage(producer)
	    : getToolkit().createImage(producer);
    }

    /**
     * Creates an off-screen drawable Image to be used for double buffering.
     * @param width the specified width
     * @param height the specified height
     */
    public Image createImage(int width, int height) {
	ComponentPeer peer = this.peer;
	return (peer != null) ? peer.createImage(width, height) : null;
    }
 
    /**
     * Prepares an image for rendering on this Component.  The image
     * data is downloaded asynchronously in another thread and the
     * appropriate screen representation of the image is generated.
     * @param image the Image to prepare a screen representation for
     * @param observer the ImageObserver object to be notified as the
     *        image is being prepared
     * @return true if the image has already been fully prepared
     * @see ImageObserver
     */
    public boolean prepareImage(Image image, ImageObserver observer) {
        return prepareImage(image, -1, -1, observer);
    }

    /**
     * Prepares an image for rendering on this Component at the
     * specified width and height.  The image data is downloaded
     * asynchronously in another thread and an appropriately scaled
     * screen representation of the image is generated.
     * @param image the Image to prepare a screen representation for
     * @param width the width of the desired screen representation
     * @param height the height of the desired screen representation
     * @param observer the ImageObserver object to be notified as the
     *        image is being prepared
     * @return true if the image has already been fully prepared
     * @see ImageObserver
     */
    public boolean prepareImage(Image image, int width, int height,
				ImageObserver observer) {
	ComponentPeer peer = this.peer;
	return (peer != null)
	    ? peer.prepareImage(image, width, height, observer)
	    : getToolkit().prepareImage(image, width, height, observer);
    }

    /**
     * Returns the status of the construction of a screen representation
     * of the specified image.
     * This method does not cause the image to begin loading. Use the
     * prepareImage method to force the loading of an image.
     * @param image the Image to check the status of
     * @param observer the ImageObserver object to be notified as the
     *        image is being prepared
     * @return the boolean OR of the ImageObserver flags for the
     *         data that is currently available
     * @see ImageObserver
     * @see #prepareImage
     */
    public int checkImage(Image image, ImageObserver observer) {
        return checkImage(image, -1, -1, observer);
    }

    /**
     * Returns the status of the construction of a scaled screen
     * representation of the specified image.
     * This method does not cause the image to begin loading, use the
     * prepareImage method to force the loading of an image.
     * @param image the Image to check the status of
     * @param width the width of the scaled version to check the status of
     * @param height the height of the scaled version to check the status of
     * @param observer the ImageObserver object to be notified as the
     *        image is being prepared
     * @return the boolean OR of the ImageObserver flags for the
     *         data that is currently available
     * @see ImageObserver
     * @see #prepareImage
     */
    public int checkImage(Image image, int width, int height,
			  ImageObserver observer) {
	ComponentPeer peer = this.peer;
	return (peer != null)
	    ? peer.checkImage(image, width, height, observer)
	    : getToolkit().checkImage(image, width, height, observer);
    }

    /**  
     * Checks whether a specified x,y location is "inside" this
     * Component. By default, x and y are inside an Component if
     * they fall within the bounding box of that Component.
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #locate
     */
    public synchronized boolean inside(int x, int y) {
	return (x >= 0) && ((x-this.x) < width) && (y >= 0) && ((y-this.y) < height);
    }

    /** 
     * Returns the component or subcomponent that contains the x,y location.
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #inside
     */
    public Component locate(int x, int y) {
	return inside(x, y) ? this : null;
    }

    /**
     * Delivers an event to this component or one of its sub components.
     * @param e the event
     * @see #handleEvent
     * @see #postEvent
     */
    public void deliverEvent(Event e) {
	postEvent(e);
    }

    /**
     * Posts an event to this component. This will result in a call
     * to handleEvent. If handleEvent returns false the event is
     * passed on to the parent of this component.
     * @param e the event
     * @see #handleEvent
     * @see #deliverEvent
     */
    public boolean postEvent(Event e) {
	ComponentPeer peer = this.peer;

	if (handleEvent(e)) {
	    return true;
	}

	Component parent = this.parent;
	if (parent != null) {
	    e.translate(x, y);
	    if (parent.postEvent(e)) {
		return true;
	    }
	}
	if (peer != null) {
	    return peer.handleEvent(e);
	}

	return false;
    }

    /**
     * Handles the event. Returns true if the event is handled and
     * should not be passed to the parent of this component. The default
     * event handler calls some helper methods to make life easier
     * on the programmer.
     * @param evt the event
     * @see #mouseEnter
     * @see #mouseExit
     * @see #mouseMove
     * @see #mouseDown
     * @see #mouseDrag
     * @see #mouseUp
     * @see #keyDown
     * @see #action
     */
    public boolean handleEvent(Event evt) {
	switch (evt.id) {
	  case Event.MOUSE_ENTER:
	    return mouseEnter(evt, evt.x, evt.y);

	  case Event.MOUSE_EXIT:
	    return mouseExit(evt, evt.x, evt.y);

	  case Event.MOUSE_MOVE:
	    return mouseMove(evt, evt.x, evt.y);

	  case Event.MOUSE_DOWN:
	    return mouseDown(evt, evt.x, evt.y);

	  case Event.MOUSE_DRAG:
	    return mouseDrag(evt, evt.x, evt.y);

	  case Event.MOUSE_UP:
	    return mouseUp(evt, evt.x, evt.y);

	  case Event.KEY_PRESS:
	  case Event.KEY_ACTION:
	    return keyDown(evt, evt.key);

	  case Event.KEY_RELEASE:
	  case Event.KEY_ACTION_RELEASE:
	    return keyUp(evt, evt.key);
	    
	  case Event.ACTION_EVENT:
	    return action(evt, evt.arg);
	  case Event.GOT_FOCUS:
	    return gotFocus(evt, evt.arg);
	  case Event.LOST_FOCUS:
	    return lostFocus(evt, evt.arg);
	}
	return false;
    }

    /**
     * Called if the mouse is down.
     * @param evt the event 
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #handleEvent
     */
    public boolean mouseDown(Event evt, int x, int y) {
	return false;
    }

    /**
     * Called if the mouse is dragged (the mouse button is down).
     * @param evt the event
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #handleEvent
     */
    public boolean mouseDrag(Event evt, int x, int y) {
	return false;
    }

    /**
     * Called if the mouse is up.
     * @param evt the event
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #handleEvent
     */
    public boolean mouseUp(Event evt, int x, int y) {
	return false;
    }

    /**
     * Called if the mouse moves (the mouse button is up).
     * @param evt the event
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #handleEvent
     */
    public boolean mouseMove(Event evt, int x, int y) {
	return false;
    }

    /**
     * Called when the mouse enters the component.
     * @param evt the event
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #handleEvent
     */
    public boolean mouseEnter(Event evt, int x, int y) {
	return false;
    }

    /**
     * Called when the mouse exits the component.
     * @param evt the event
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #handleEvent
     */
    public boolean mouseExit(Event evt, int x, int y) {
	return false;
    }

    /**
     * Called if a character is pressed.
     * @param evt the event
     * @param key the key that's pressed
     * @see #handleEvent
     */
    public boolean keyDown(Event evt, int key) {
	return false;
    }

    /**
     * Called if a character is released.
     * @param evt the event
     * @param key the key that's released
     * @see #handleEvent
     */
    public boolean keyUp(Event evt, int key) {
	return false;
    }

    /**
     * Called if an action occurs in the Component.
     * @param evt the event
     * @param what the action that's occuring
     * @see #handleEvent
     */
    public boolean action(Event evt, Object what) {
	return false;
    }

    /** 
     * Notifies the Component to create a peer.
     * @see #getPeer
     * @see #removeNotify
     */
    public void addNotify() {
	valid = false;
    }

    /** 
     * Notifies the Component to destroy the peer.
     * @see #getPeer
     * @see #addNotify
     */
    public synchronized void removeNotify() {
	if (peer != null) {
	    peer.dispose();
	    peer = null;
	}
    }

    /** 
     * Indicates that this component has received the input focus.
     * @see #requestFocus
     * @see #lostFocus
     */
    public boolean gotFocus(Event evt, Object what) {
	return false;
    }

    /** 
     * Indicates that this component has lost the input focus.  
     * @see #requestFocus
     * @see #gotFocus
     */
    public boolean lostFocus(Event evt, Object what) {
	return false;
    }

    /** 
     * Requests the input focus. The gotFocus() method will be called
     * if this method is successful.
     * @see #gotFocus
     */
    public void requestFocus() {
	ComponentPeer peer = this.peer;
	if (peer != null) {
	    peer.requestFocus();
	}
    }

    /**
     * Moves the focus to the next component.
     * @see #requestFocus
     * @see #gotFocus
     */
     public void nextFocus() {
	ComponentPeer peer = this.peer;
	 if (peer != null) {
	     peer.nextFocus();
	 }
     }

    /**
     * Returns the parameter String of this Component.
     */
    protected String paramString() {
	String str = x + "," + y + "," + width + "x" + height;
	if (!valid) {
	    str += ",invalid";
	}
	if (!visible) {
	    str += ",hidden";
	}
	if (!enabled) {
	    str += ",disabled";
	}
	return str;
    }

    /**
     * Returns the String representation of this Component's values.
     */
    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }

    /**
     * Prints a listing to a print stream.
     */
    public void list() {
	list(System.out, 0);
    }

    /**
     * Prints a listing to the specified print out stream.
     * @param out the Stream name
     */
    public void list(PrintStream out) {
	list(out, 0);
    }

    /**
     * Prints out a list, starting at the specified indention, to the specified 
     * print stream.
     * @param out the Stream name
     * @param indent the start of the list 
     */
    public void list(PrintStream out, int indent) {
	for (int i = 0 ; i < indent ; i++) {
	    out.print("  ");
	}
	out.println(this);
    }
}
