/*
 * @(#)Button.java	1.21 95/02/03 Sami Shaio
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

import java.lang.*;

/**
 * A class that produces a native Gui button. This button will
 * respond to callbacks supplied by its subclass.
 * For example, this button will print hello when pressed:
 * <pre>
 * class HelloButton extends Button {
 *      public HelloButton(String l, String n, Window p) {
 *		super(l, n, p);
 *	}
 *	public void selected(Component c, int pos) {
 *		System.out.println("Hello");
 *	}
 * }
 *
 *  HelloButton b = new HelloButton("Press Me", "", window);
 * </pre>
 *
 * @version 1.21 03 Feb 1995
 * @author Sami Shaio
 */
public class Button extends Component implements ChoiceHandler {
    private	WServer	wServer;

    /**
     * The string label for this button.
     */
    public String label = null;

    /**
     * The image label for this button when inactive.
     */
    public Image normalImage = null;

    /**
     * The image label for this button when pressed.
     */
    public Image pressedImage = null;

    /**
     * Constructs a Button with a string label.
     * @param pLabel the string label for the button
     * @param pName the name of the button, this name may be used by
     * the layout algorithm in pParent. 
     * @param pParent the parent window in which to put the button.
     */
    public Button(String pLabel, String pName, Container pParent) {
	super(pParent, pName);
	label = pLabel;
	Window win = Window.getWindow(parent);
	wServer = win.wServer;
	wServer.buttonCreate(this,
			     label,
			     null,
			     null,
			     win);
    }

    /**
     * Constructs a button given a Window as a parent rather than a
     * container. This is here for backward-compatibility with alpha1.
     */
    public Button(String pLabel, String pName, Window pParent) {
	this(pLabel, pName, (Container)pParent);
    }

    /**
     * Constructs an image Button.
     * @param normalImage the image to display when the button is inactive.
     * @param pressedImage the image to display when the button is
     * pressed. This may be null.
     * @param pName the name of the button, this name may be used by
     * the layout algorithm in pParent. 
     * @param pParent the parent window in which to put the button.
     * @see awt.Image
     * @see awt.GifImage
     */
    public Button(Image normalImage, Image pressedImage,
		  String pName, Container pParent) {
	super(pParent, pName);
	label = null;
	this.normalImage = normalImage;
	this.pressedImage = pressedImage;
	Window win = Window.getWindow(parent);
	wServer = win.wServer;
	wServer.buttonCreate(this,
			     null,
			     normalImage,
			     pressedImage,
			     win);
    }

   /**
     * Constructs a button given a Window as a parent rather than a
     * container. This is here for backward-compatibility with alpha1.
     */
    public Button(Image normalImage, Image pressedImage,
		  String pName, Window pParent) {
	this(normalImage, pressedImage, pName, (Container)pParent);
    }
   
    /**
     * Unused method. It is required by the ChoiceHandler interface.
     */
    public void doubleClick(Component c, int pos) {
    }

    /**
     * This method is invoked when the button has been selected.
     * Override this method in a subclass to do something useful.
     * @param c is the component being selected. This is useful if
     * another object handles the method for this button.
     * @param pos is undefined in this context.
     */
    public void selected(Component c, int pos) {
    }

    /**
     * Moves this button to the given x and y coordinates.
     */
    public void move(int X, int Y) {
	super.move(X,Y);
	wServer.buttonMoveTo(this, X, Y);
    }

    /**
     * Reshapes this button.
     */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.buttonReshape(this, x, y, w, h);
    }

    /**
     * Disposes of this button. The button cannot be used after being
     * disposed.
     */
    public void dispose() {
	wServer.buttonDispose(this);
    }

    /**
     * Shows this button.
     */
    public void map() {
	wServer.buttonShow(this);
	mapped = true;
    }

    /**
     * Hides this button.
     */
    public void unMap() {
	wServer.buttonHide(this);
	mapped = false;
    }
}
