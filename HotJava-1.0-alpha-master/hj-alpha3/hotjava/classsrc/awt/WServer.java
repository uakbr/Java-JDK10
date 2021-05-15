/*
 * @(#)WServer.java	1.78 95/05/13 Sami Shaio
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

import awt.*;
import java.lang.*;
import java.util.Linker;
import java.util.Hashtable;

/**
 * WServer is the class that interacts directly with the native GUI
 * system. The set of native methods it contains defines the
 * platform-independent api that must be implemented for all the
 * platforms it is to run on. Because the platform may necessitate
 * a different division between what is native or not (and what is
 * synchronized or not), this file is platform-dependent. However, all
 * of the public awt classes are always the same and implemented only in
 * Java.
 *
 * @version 1.78 13 May 1995
 * @author Sami Shaio
 */
public class WServer extends Thread {
    public static WServer  theServer;

    /**
     * Use this field to create new fonts.
     * @see FontTable
     */
    public FontTable	fonts;

    /** Constructs a new WServer. */
    public WServer() {
	super("AWT WServer Thread");
	theServer = this;
    }

    /** Starts the thread but waits until it's done initializing */
    synchronized public void start() {
        super.start();
        wait();
	fonts = new FontTable();
    }

    public void run() {
	Linker.loadLibrary("awt");
	pInit();
	Color.initColors(this);
        eventLoop();
    }

    /* Initializes server data in the C-level code. */
    native void pInit();

    /** 
     * First notifies the thread that starts the server
     * that initialization is complete.
     * Then enters an infinite loop that retrieves and processes events.  
     */
    native void eventLoop();

/*  <<replaced the following code that also works on Chicago -chan>>
    public WServer() {
	super("AWT WServer Thread");
	pInit();
	Color.initColors(this);
	fonts = new FontTable();
    }

    public native void run();
    
    native synchronized void pInit();
*/

    public native synchronized void sync();

    /* Button methods */
    native synchronized void buttonMoveTo(Button b,
					  int x,
					  int y);

    native synchronized void buttonReshape(Button b,
					   int x,
					   int y,
					   int w,
					   int h);


    native synchronized void buttonCreate(Button b,
					  String label,
					  Image normalImage,
					  Image pressedImage,
					  Image disabledImage,
					  Window parent);

    native synchronized void buttonShow(Button b);    
    native synchronized void buttonHide(Button b);    
    native synchronized void buttonDispose(Button b);
    native synchronized void buttonEnable(Button b);
    native synchronized void buttonDisable(Button b);

    /* Color methods */
    native synchronized void colorCreate(Color c);
    
    /* File Dialog methods */
    native synchronized String fileDialogChooseFile(FileDialog f,
						    String initialValue);

    native synchronized void fileDialogCreate(FileDialog f,
					      String title,
					      Frame parent);

    native synchronized void fileDialogDispose(FileDialog f);    

    /* Font methods */
    native synchronized void fontDispose(Font f);

    native synchronized int fontStringWidth(WSFontMetrics fm, String s);

    native synchronized int fontCharsWidth(WSFontMetrics fm,
					   char data[],
					   int off,
					   int len);
    native synchronized int fontBytesWidth(WSFontMetrics fm,
					   byte data[],
					   int off,
					   int len);
    native synchronized void loadFontMetrics(Window w, WSFontMetrics fm);

    private Hashtable fontmetrics = new Hashtable();

    public FontMetrics getFontMetrics(Window w, Font f) {
	if (f == null) {
	    return null;
	}

	// REMIND: Hashing should include some component of the window.
	// Font Metrics may depend on the "class" of the window (printer
	// or screen, for instance).
	String fName = fonts.compoundName(f.family, f.style, f.size);
	FontMetrics fm = (FontMetrics) fontmetrics.get(fName);

	if (fm == null) {
	    fm = new WSFontMetrics(w, f);
	    fontmetrics.put(fName, fm);
	}

	return fm;
    }

    /* Frame methods */
    native synchronized void frameShow(Frame f);
    native synchronized void frameHide(Frame f);
    native synchronized void frameSetDefaultFont(Frame f, Font font);
    native synchronized void frameSetTitle(Frame f, String title);
    native synchronized void frameSetIconImage(Frame f, Image image);
    native synchronized void frameCreate(Frame f,
					 boolean hasTitleBar,
					 boolean isModal,
					 Frame parentFrame,
					 int width,
					 int height,
					 Color bg);
    native synchronized void frameDispose(Frame f);
    native synchronized void frameReshape(Frame f, int x, int y, int w, int h);
    native synchronized void frameSetMinSize(Frame f, int width, int height);
    native synchronized boolean frameHasStatusBar(Frame f);
    native synchronized void frameShowStatusBar(Frame f, boolean show);
    native synchronized boolean frameShowingStatusBar(Frame f);
    native synchronized void frameSetStatusMessage(Frame f, String message);

    /* Graphics methods */
    native synchronized void graphicsCreate(Graphics g, Window w);
    native synchronized void imageGraphicsCreate(Graphics g, Image im);
    native synchronized void graphicsDispose(Graphics g);
    native synchronized void graphicsSetFont(Graphics g, Font f);
    native synchronized void graphicsSetForeground(Graphics g, Color c);
    native synchronized void graphicsSetBackground(Graphics g, Color c);
    native synchronized void graphicsClearRect(Graphics g,
					       int X, int Y, int W, int H);
    native synchronized void graphicsFillRect(Graphics g,
					      int X, int Y, int W, int H);
    native synchronized void graphicsDrawRect(Graphics g,
					      int X, int Y, int W, int H);
    native synchronized void graphicsDrawString(Graphics g,
						String str, int x, int y);
    native synchronized void graphicsDrawChars(Graphics g, char chars[],
					       int offset, int length,
					       int x, int y);
    native synchronized void graphicsDrawBytes(Graphics g, byte bytes[],
					       int offset, int length,
					       int x, int y);
    native synchronized int graphicsDrawStringWidth(Graphics g,
						    String str, int x, int y);
    native synchronized int graphicsDrawCharsWidth(Graphics g, char chars[],
						   int offset, int length,
						   int x, int y);
    native synchronized int graphicsDrawBytesWidth(Graphics g, byte bytes[],
						   int offset, int length,
						   int x, int y);
    native synchronized void graphicsDrawLine(Graphics g,
					      int x1, int y1, int x2, int y2);

    native synchronized void graphicsClipRect(Graphics g,
					      int x, int y, int w, int h);
    native synchronized void graphicsClearClip(Graphics g);
    native synchronized void graphicsSetOrigin(Graphics g, int x, int y);
    native synchronized void graphicsSetScaling(Graphics g,
						float sx, float sy);
    native synchronized void graphicsCopyArea(Graphics g,
					      int x,
					      int y,
					      int width,
					      int height,
					      int dx,
					      int dy);

    native synchronized void graphicsDrawImage(Graphics g,
					       Image I,
					       int X, int Y);

    /* Image methods */
    native synchronized void imageCreate(Image I, DIBitmap dib);
    native synchronized void offscreenImageCreate(Image I, int w, int h);
    native synchronized void scaledImageCreate(Image I, DIBitmap dib,
					       int srcx, int srcy,
					       int srcw, int srch,
					       int dstw, int dsth);
    native synchronized void bitmapRetrieve(Image I, DIBitmap dib);
    native synchronized void imageDispose(Image i);

    /* Label methods */
    native synchronized void labelMoveTo(Label b,
					 int x,
					 int y);

    native synchronized void labelSetText(Label b,
					  String label);

    native synchronized void labelCreate(Label b,
					 String label,
					 Window parent,
					 Font font);

    native synchronized void labelDispose(Label b);    
    native synchronized void labelSetColor(Label b, Color c);    
    native synchronized void labelSetFont(Label b, Font f);    

    native synchronized void labelReshape(Label b,
					  int x,
					  int y,
					  int w,
					  int h);

    native synchronized void labelDimensions(Label s);

    native synchronized void labelShow(Label s);
    native synchronized void labelHide(Label s);

    /* MessageDialog methods */
    native synchronized void messageDialogCreate(MessageDialog m,
						 Frame f,
						 String title,
						 String message,
						 int dialogType,
						 int nButtons,
						 boolean isModal,
						 String okLabel,
						 String cancelLabel,
						 String helpLabel);

    native synchronized void messageDialogSetMessage(MessageDialog m,
						     String message);
    native synchronized int mesageDialogShow(MessageDialog m);
    native synchronized void mesageDialogHide(MessageDialog m);
    native synchronized void messageDialogDispose(MessageDialog m);

    /* Menu methods */
    native synchronized void menuCreate(Menu m,
					String title,
					MenuBar mb,
					boolean tearOff);
    native synchronized void menuDispose(Menu m);
    native synchronized void menuShow(Menu m);
    native synchronized void menuHide(Menu m);
    native synchronized void menuAddSeparator(Menu m);

    /* MenuBar methods */
    native synchronized void menuBarCreate(MenuBar mb, Frame f);
    native synchronized void menuBarDispose(MenuBar mb);

    /* MenuItem methods */
    native synchronized void menuItemEnable(MenuItem m);
    native synchronized void menuItemDisable(MenuItem m);
    native synchronized void menuItemSetMark(MenuItem m, boolean t);
    native synchronized boolean menuItemGetMark(MenuItem m);
    native synchronized void menuItemCreate(MenuItem m,
					    String label,
					    Menu menu,
					    boolean isToggle);
    native synchronized void menuItemDispose(MenuItem m);

    /* OptionMenu methods */
    native synchronized void optionMenuCreate(OptionMenu s,
					      Window p,
					      String l);
    
    native synchronized void optionMenuDispose(OptionMenu s);
    native synchronized void optionMenuMoveTo(OptionMenu s,
					      int x,
					      int y);
    native synchronized void optionMenuShow(OptionMenu s);
    native synchronized void optionMenuHide(OptionMenu s);
    native synchronized void optionMenuAddItem(OptionMenu s,
					       String item,
					       int index);
    native synchronized void optionMenuAddSeparator(OptionMenu s,
						    int sIndex);
    native synchronized void optionMenuSelect(OptionMenu s,
					      int index);
    native synchronized void optionMenuReshape(OptionMenu s,
					       int x,
					       int y,
					       int w,
					       int h);
    native synchronized void optionMenuDimensions(OptionMenu s);

    /* Scrollbar methods */
    native synchronized int scrollbarMinimum(Scrollbar s);
    native synchronized int scrollbarMaximum(Scrollbar s);
    native synchronized int scrollbarValue(Scrollbar s);
    native synchronized void scrollbarCreate(Scrollbar s,
					     Window parent,
					     int orientation,
					     boolean manageScrollbar);
    native synchronized void scrollbarDispose(Scrollbar s);
    native synchronized void scrollbarShow(Scrollbar s);
    native synchronized void scrollbarHide(Scrollbar s);
    native synchronized void scrollbarMoveTo(Scrollbar s, int x, int y);

    native synchronized void scrollbarSetValues(Scrollbar s,
						int newValue,
						int visible,
						int minimum,
						int maximum);

    native synchronized void scrollbarReshape(Scrollbar s, int x, int y,
					      int w, int h);
    

    /* TextArea methods */
    native synchronized void textAreaCreate(TextArea t,
					    Window p,
					    Font font,
					    int columns,
					    int rows);

    native synchronized void textAreaSetEditable(TextArea t,
						 boolean e);

    native synchronized void textAreaSetColor(TextArea t, Color c);

    native synchronized void textAreaSetBackColor(TextArea t, Color c);
    
    native synchronized int textAreaCursorPos(TextArea t);

    native synchronized void textAreaSetCursorPos(TextArea t,
						  int pos);

    native synchronized int textAreaEndPos(TextArea t);

    native synchronized void textAreaSetText(TextArea t,
					     String txt);

    native synchronized String textAreaGetText(TextArea t);

    native synchronized void textAreaInsertText(TextArea t,
						String txt,
						int pos);
    
    native synchronized void textAreaReplaceText(TextArea t,
						 String txt,
						 int start,
						 int end);

    native synchronized void textAreaDispose(TextArea t);

    native synchronized void textAreaMoveTo(TextArea t,
					    int x,
					    int y);

    native synchronized void textAreaReshape(TextArea t,
					     int x,
					     int y,
					     int w,
					     int h);

    native synchronized void textAreaShow(TextArea t);

    native synchronized void textAreaHide(TextArea t);
    
    /* List methods */
    native synchronized void listCreate(List l,
					Window parent,
					ChoiceHandler handler,
					int visibleLines,
					boolean multipleSelections,
					boolean resizable);

    native synchronized boolean listIsSelected(List l,int pos);
    native synchronized void listAddItem(List l, String item);
    
    native synchronized void listSetNVisible(List l, int nLines);

    native synchronized void listDelItems(List l, int start, int end);
    
    native synchronized void listSelect(List l, int pos);

    native synchronized void listDeselect(List l, int pos);

    native synchronized void listMakeVisible(List l,int pos);

    native synchronized void listMoveTo(List l, int x, int y);

    native synchronized void listReshape(List l,
					 int x,
					 int y,
					 int w,
					 int h);
    native synchronized void listDispose(List l);
    native synchronized void listShow(List l);
    native synchronized void listHide(List l);
    native synchronized void listDimensions(List l);

    /* TextField methods */
    native synchronized void textFieldCreate(TextField t,
					     String initValue,
					     Window parent,
					     boolean editable);

    native synchronized void textFieldSetEditable(TextField t,
						  boolean editable);

    native synchronized void textFieldDispose(TextField t);
    native synchronized void textFieldSetText(TextField t, String l);
    native synchronized void textFieldMoveTo(TextField t,
				int x,
				int y);
    native synchronized String textFieldGetText(TextField t);
    native synchronized void textFieldSetColor(TextField t, Color c);
    native synchronized void textFieldSetBackColor(TextField t, Color c);
    native synchronized void textFieldShow(TextField t);
    native synchronized void textFieldHide(TextField t);
    native synchronized void textFieldSetFont(TextField t, Font f);
    native synchronized void textFieldSetEchoCharacter(TextField t,
						       char c);
    native synchronized void textFieldReshape(TextField b,
					      int x,
					      int y,
					      int w,
					      int h);

    /* Toggle methods */
    public native synchronized void toggleSetState(Toggle t, boolean state);
    public native synchronized boolean toggleGetState(Toggle t); 
    public native synchronized void toggleMoveTo(Toggle t, int x, int y);
    native synchronized void toggleCreate(Toggle t,
					  String label,
					  Window parent,
					  RadioGroup group,
					  boolean initialState);
    native synchronized void toggleDispose(Toggle t);
    native synchronized void toggleShow(Toggle t);
    native synchronized void toggleHide(Toggle t);
    native synchronized void toggleReshape(Toggle b,
					   int x,
					   int y,
					   int w,
					   int h);

    /* Window methods */
    native synchronized void windowSetMargin(Window w, int margin);
    native synchronized int windowWidth(Window w);
    native synchronized int windowHeight(Window w);
    native synchronized void windowMoveTo(Window w, int x, int y);
    native synchronized void windowReshape(Window w, int x, int y,int wd, int ht);
    native synchronized void windowEnablePointerMotionEvents(Window w);
    native synchronized void windowDisablePointerMotionEvents(Window w);
    native synchronized void windowDimensions(Window s);

    native synchronized void windowCreate(Window w,
					  Window parent,
					  boolean trackMotion,
					  Color bg,
					  int width, int height);
    native synchronized void windowCreateInFrame(Window w,
						 Frame parent,
						 boolean trackMotion,
						 Color bg,
						 int width, int height);
    native synchronized void windowDispose(Window w);
    native synchronized void windowShow(Window w);
    native synchronized void windowHide(Window w);
    native synchronized void windowScrollWindow(Window w, int dx, int dy);
    
    static {
	/* <<moved code to run method -chan>>
             Linker.loadLibrary("awt"); */
    }

    int pData;
}
