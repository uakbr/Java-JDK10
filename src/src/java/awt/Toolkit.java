/*
 * @(#)Toolkit.java	1.46 95/12/14 Sami Shaio
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

import java.awt.peer.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;
import java.net.URL;

/**
 * An AWT toolkit. It is used to bind the abstract AWT classes
 * to a particular native toolkit implementation.
 *
 * @version 	1.46, 12/14/95
 * @author	Sami Shaio
 * @author	Arthur van Hoff
 */
public abstract class  Toolkit {

    /**
     * Uses the specified Peer interface to create a new Button.
     * @param target the Button to be created
     */
    protected abstract ButtonPeer 	createButton(Button target);

    /**
     * Uses the specified Peer interface to create a new TextField.      
     * @param target the TextField to be created
     */
    protected abstract TextFieldPeer 	createTextField(TextField target);

    /**
     * Uses the specified Peer interface to create a new Label.      
     * @param target the Label to be created
     */
    protected abstract LabelPeer 	createLabel(Label target);

    /**
     * Uses the specified Peer interface to create a new List.      
     * @param target the List to be created
     */
    protected abstract ListPeer 	createList(List target);

    /**
     * Uses the specified Peer interface to create a new Checkbox.      
     * @param target the Checkbox to be created
     */
    protected abstract CheckboxPeer 	createCheckbox(Checkbox target);

    /**
     * Uses the specified Peer interface to create a new Scrollbar.      
     * @param target the Scrollbar to be created
     */
    protected abstract ScrollbarPeer 	createScrollbar(Scrollbar target);

    /**
     * Uses the specified Peer interface to create a new TextArea.      
     * @param target the TextArea to be created
     */
    protected abstract TextAreaPeer  	createTextArea(TextArea target);

    /**
     * Uses the specified Peer interface to create a new Choice.      
     * @param target the Choice to be created
     */
    protected abstract ChoicePeer	createChoice(Choice target);

    /**
     * Uses the specified Peer interface to create a new Frame.
     * @param target the Frame to be created
     */
    protected abstract FramePeer  	createFrame(Frame target);

    /**
     * Uses the specified Peer interface to create a new Canvas.
     * @param target the Canvas to be created
     */
    protected abstract CanvasPeer 	createCanvas(Canvas target);

    /**
     * Uses the specified Peer interface to create a new Panel.
     * @param target the Panel to be created
     */
    protected abstract PanelPeer  	createPanel(Panel target);

    /**
     * Uses the specified Peer interface to create a new Window.
     * @param target the Window to be created
     */
    protected abstract WindowPeer  	createWindow(Window target);

    /**
     * Uses the specified Peer interface to create a new Dialog.
     * @param target the Dialog to be created
     */
    protected abstract DialogPeer  	createDialog(Dialog target);

    /**
     * Uses the specified Peer interface to create a new MenuBar.
     * @param target the MenuBar to be created
     */
    protected abstract MenuBarPeer  	createMenuBar(MenuBar target);

    /**
     * Uses the specified Peer interface to create a new Menu.
     * @param target the Menu to be created
     */
    protected abstract MenuPeer  	createMenu(Menu target);

    /**
     * Uses the specified Peer interface to create a new MenuItem.
     * @param target the MenuItem to be created
     */
    protected abstract MenuItemPeer  	createMenuItem(MenuItem target);

    /**
     * Uses the specified Peer interface to create a new FileDialog.
     * @param target the FileDialog to be created
     */
    protected abstract FileDialogPeer	createFileDialog(FileDialog target);

    /**
     * Uses the specified Peer interface to create a new CheckboxMenuItem.
     * @param target the CheckboxMenuItem to be created
     */
    protected abstract CheckboxMenuItemPeer	createCheckboxMenuItem(CheckboxMenuItem target);

    /**
     * Gets the size of the screen.
     */
    public abstract Dimension getScreenSize();

    /**
     * Returns the screen resolution in dots-per-inch.
     */
    public abstract int getScreenResolution();

    /**
     * Returns the ColorModel of the screen.
     */
    public abstract ColorModel getColorModel();

    /**
     * Returns the names of the available fonts.
     */
    public abstract String[] getFontList();

    /**
     * Returns the screen metrics of the font.
     */
    public abstract FontMetrics getFontMetrics(Font font);

    /**
     * Syncs the graphics state; useful when doing animation.
     */
    public abstract void sync();

    /**
     * The default toolkit.
     */
    private static Toolkit toolkit;

    /**
     * Returns the default toolkit. This is controlled by the
     * "awt.toolkit" property.
     * @exception AWTError Toolkit not found or could not be instantiated.
     */
    public static synchronized Toolkit getDefaultToolkit() {
	if (toolkit == null) {
	    String nm = System.getProperty("awt.toolkit", "sun.awt.motif.MToolkit");
	    try {
		toolkit = (Toolkit)Class.forName(nm).newInstance();
	    } catch (ClassNotFoundException e) {
		throw new AWTError("Toolkit not found: " + nm);
	    } catch (InstantiationException e) {
		throw new AWTError("Could not instantiate Toolkit: " + nm);
	    } catch (IllegalAccessException e) {
		throw new AWTError("Could not access Toolkit: " + nm);
	    }
	}
	return toolkit;
    }

    /**
     * Returns an image which gets pixel data from the specified file.
     * @param filename the file containing the pixel data in one of
     * the recognized file formats
     */
    public abstract Image getImage(String filename);

    /**
     * Returns an image which gets pixel data from the specified URL.
     * @param url the URL to use in fetching the pixel data
     */
    public abstract Image getImage(URL url);

    /**
     * Prepares an image for rendering on the default screen at the
     * specified width and height.
     */
    public abstract boolean prepareImage(Image image, int width, int height,
					 ImageObserver observer);

    /**
     * Returns the status of the construction of the indicated method
     * at the indicated width and height for the default screen.
     */
    public abstract int checkImage(Image image, int width, int height,
				   ImageObserver observer);

    /**
     * Creates an image with the specified image producer.
     * @param producer the image producer to be used
     */
    public abstract Image createImage(ImageProducer producer);
}
