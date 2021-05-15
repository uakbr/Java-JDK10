import browser.Applet;
import awt.DIBitmap;
import awt.Image;
import awt.Graphics;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import net.www.html.URL;

/**
 * An extensible ImageMap applet class.
 * The active areas on the image are controlled by ImageArea classes
 * that can be dynamically extended over the web.
 *
 * @author 	Jim Graham
 * @version 	1.1, 23 Mar 1995
 */
class ImageMap extends Applet {
    /**
     * The unhighlighted image being mapped.
     */
    Image baseImage;

    /**
     * The primary highlighted image for providing user feedback.
     */
    Image hlImage;

    /**
     * The list of image area handling objects;
     */
    ImageMapArea areas[];

    /**
     * The primary highlight mode to be used.
     */
    static final int BRIGHTER = 0;
    static final int DARKER = 1;

    int hlmode = BRIGHTER;

    /**
     * The percentage of highlight to apply for the primary highlight mode.
     */
    int hlpercent = 50;

    /**
     * A Hashtable of various highlighted images for ImageAreas
     * that want a custom highlight.
     */
    Hashtable hlImages = new Hashtable();

    /**
     * Get the primary highlighted version of the baseImage.
     */
    Image getHighlight() {
	return (hlImage == null) ? makeHighlight() : hlImage;
    }

    /**
     * Get a version of the baseImage with a specific highlight.
     * Create a new highlight image if necessary.
     */
    synchronized Image getHighlight(int mode, int percent) {
	Image img = (Image) hlImages.get("HL"+mode+","+percent);
	if (img == null) {
	    img = makeHighlight(mode, percent);
	}
	return img;
    }

    /**
     * Make the primary highlighted version of the baseImage.
     */
    Image makeHighlight() {
	return makeHighlight(hlmode, hlpercent);
    }

    /**
     * Create a highlighted image from a highlight mode and a percentage.
     */
    synchronized Image makeHighlight(int mode, int percent) {
	DIBitmap sourceBitmap = baseImage.getDIBitmap();

	byte newred[] = new byte[sourceBitmap.num_colors];
	byte newgreen[] = new byte[sourceBitmap.num_colors];
	byte newblue[] = new byte[sourceBitmap.num_colors];
	for (int i = 0; i < sourceBitmap.num_colors; i++) {
	    if (i == sourceBitmap.trans_index) {
		newred[i] = sourceBitmap.red[i];
		newgreen[i] = sourceBitmap.green[i];
		newblue[i] = sourceBitmap.blue[i];
	    } else {
		int oldred = (sourceBitmap.red[i] & 0xff);
		int oldgreen = (sourceBitmap.green[i] & 0xff);
		int oldblue = (sourceBitmap.blue[i] & 0xff);
		switch (mode) {
		case DARKER:
		    newred[i] = (byte) (oldred * (100 - percent) / 100);
		    newgreen[i] = (byte) (oldgreen * (100 - percent) / 100);
		    newblue[i] = (byte) (oldblue * (100 - percent) / 100);
		    break;
		case BRIGHTER:
		    newred[i] = (byte) (255 - ((255 - oldred)
					       * (100 - percent) / 100));
		    newgreen[i] = (byte) (255 - ((255 - oldgreen)
						 * (100 - percent) / 100));
		    newblue[i] = (byte) (255 - ((255 - oldblue)
						* (100 - percent) / 100));
		    break;
		}
	    }
	}
	DIBitmap newBitmap = new DIBitmap(sourceBitmap.width,
					  sourceBitmap.height,
					  sourceBitmap.num_colors,
					  newred, newgreen, newblue,
					  sourceBitmap.raster);
	newBitmap.trans_index = sourceBitmap.trans_index;
	Image newImage = item.parent.createImage(newBitmap);
	if (mode == hlmode && percent == hlpercent) {
	    hlImage = newImage;
	}
	hlImages.put("HL"+mode+","+percent, newImage);
	return newImage;
    }

    /**
     * Parse a string representing the desired highlight to be applied.
     */
    void parseHighlight(String s) {
	if (s == null) {
	    return;
	}
	if (s.startsWith("brighter")) {
	    hlmode = BRIGHTER;
	    if (s.length() > "brighter".length()) {
		hlpercent = Integer.parseInt(s.substring("brighter".length()));
	    }
	} else if (s.startsWith("darker")) {
	    hlmode = DARKER;
	    if (s.length() > "darker".length()) {
		hlpercent = Integer.parseInt(s.substring("darker".length()));
	    }
	}
    }

    /**
     * Initialize the applet. Get attributes.
     *
     * Initialize the ImageAreas.
     * Each ImageArea is a subclass of the class ImageArea, and is
     * specified with an attribute of the form:
     * 		areaN=ImageAreaClassName,arguments...
     * The ImageAreaClassName is parsed off and a new instance of that
     * class is created.  The initializer for that class is passed a
     * reference to the applet and the remainder of the attribute
     * string, from which the class should retrieve any information it
     * needs about the area it controls and the actions it needs to
     * take within that area.
     */
    public void init() {
	String s;

	parseHighlight(getAttribute("highlight"));
	baseImage = getImage(getAttribute("img"));
	makeHighlight();
	Vector areaVec = new Vector();
	int num = 1;
	while (true) {
	    ImageMapArea newArea;
	    s = getAttribute("area"+num);
	    if (s == null) {
		// Try rect for backwards compatibility.
		s = getAttribute("rect"+num);
		if (s == null) {
		    break;
		}
		String url = getAttribute("href"+num);
		if (url != null)
		    s += "," + url;
		newArea = new HrefArea();
	    } else {
		int classend = s.indexOf(",");
		newArea = (ImageMapArea) new (s.substring(0, classend));
		s = s.substring(classend+1);
	    }
	    newArea.init(this, s);
	    areaVec.addElement(newArea);
	    num++;
	}
	areas = new ImageMapArea[areaVec.size()];
	areaVec.copyInto(areas);
	resize(baseImage.width, baseImage.height);
    }

    /**
     * Paint the image and all active highlights.
     */
    public void paint(Graphics g) {
	g.drawImage(baseImage, 0, 0);
	for (int i = areas.length; --i >= 0; ) {
	    if (areas[i].active) {
		areas[i].setState(g, true);
	    }
	}
    }

    /**
     * Update the active highlights on the image.
     */
    public void update(Graphics g) {
	// First unhighlight all of the deactivated areas
	for (int i = areas.length; --i >= 0; ) {
	    if (areas[i].active && !areas[i].entered) {
		areas[i].setState(g, false);
	    }
	}
	// Then highlight all of the activated areas
	for (int i = areas.length; --i >= 0; ) {
	    if (areas[i].entered) {
		areas[i].setState(g, true);
	    }
	}
    }

    /**
     * Make sure that no ImageAreas are highlighted.
     */
    public void mouseExit() {
	boolean changed = false;

	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].active) {
		areas[i].entered = false;
		changed = true;
	    }
	}
	if (changed) {
	    repaint();
	}
    }

    /**
     * Find the ImageAreas that the mouse is in.
     */
    public void mouseMove(int x, int y) {
	boolean changed = false;
	boolean propagate = true;

	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(x, y)) {
		areas[i].entered = propagate;
		if (areas[i].terminal) {
		    propagate = false;
		}
	    } else {
		areas[i].entered = false;
	    }

	    if (areas[i].active != areas[i].entered) {
		changed = true;
	    }
	}

	if (changed) {
	    repaint();
	}
    }

    int pressX;
    int pressY;

    /**
     * Inform all active ImageAreas of a mouse press.
     */
    public void mouseDown(int x, int y) {
	pressX = x;
	pressY = y;

	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(x, y)) {
		areas[i].press(x, y);
		if (areas[i].terminal) {
		    break;
		}
	    }
	}
    }

    /**
     * Inform all active ImageAreas of a mouse release.
     * Only those areas that were inside the original mouseDown()
     * are informed of the mouseUp.
     */
    public void mouseUp(int x, int y) {
	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(pressX, pressY)) {
		areas[i].lift(x, y);
		if (areas[i].terminal) {
		    break;
		}
	    }
	}
    }

    /**
     * Inform all active ImageAreas of a mouse drag.
     * Only those areas that were inside the original mouseDown()
     * are informed of the mouseDrag.
     */
    public void mouseDrag(int x, int y) {
	mouseMove(x, y);
	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(pressX, pressY)) {
		areas[i].drag(x, y);
		if (areas[i].terminal) {
		    break;
		}
	    }
	}
    }
}

/**
 * The base ImageArea class.
 * This class performs the basic functions that most ImageArea
 * classes will need and delegates specific actions to the subclasses.
 *
 * @author 	Jim Graham
 * @version 	1.1, 23 Mar 1995
 */
class ImageMapArea {
    /** The Applet parent that contains this ImageArea. */
    ImageMap parent;

    /** The X location of the area (if rectangular). */
    int X;
    /** The Y location of the area (if rectangular). */
    int Y;
    /** The width of the area (if rectangular). */
    int W;
    /** The height of the area (if rectangular). */
    int H;

    /**
     * This flag indicates whether the user was in this area during the
     * last scan of mouse locations.
     */
    boolean entered = false;
    /** This flag indicates whether the area is currently highlighted. */
    boolean active = false;

    /**
     * This flag indicates whether the area is terminal.  Terminal areas
     * prevent any areas which are under them from being activated when
     * the mouse is inside them.  Some areas may wish to change this to
     * false so that they can augment other areas that they are on top of.
     */
    boolean terminal = true;

    /**
     * Initialize this ImageArea as called from the Applet.
     * If the subclass does not override this initializer, then it
     * will perform the basic functions of setting the parent applet
     * and parsing out 4 numbers from the argument string which specify
     * a rectangular region for the ImageArea to act on.
     * The remainder of the argument string is passed to the handleArg()
     * method for more specific handling by the subclass.
     */
    public void init(ImageMap parent, String args) {
	this.parent = parent;
	StringTokenizer st = new StringTokenizer(args, ", ");
	X = Integer.parseInt(st.nextToken());
	Y = Integer.parseInt(st.nextToken());
	W = Integer.parseInt(st.nextToken());
	H = Integer.parseInt(st.nextToken());
	if (st.hasMoreTokens()) {
	    // hasMoreTokens() Skips the trailing comma
	    handleArg(st.nextToken(""));
	} else {
	    handleArg(null);
	}
    }

    /**
     * This method handles the remainder of the argument string after
     * the standard initializer has parsed off the 4 rectangular
     * parameters.  If the subclass does not override this method,
     * the remainder will be ignored.
     */
    public void handleArg(String s) {
    }

    /**
     * This method tests to see if a point is inside this ImageArea.
     * The standard method assumes a rectangular area as parsed by
     * the standard initializer.  If a more complex area is required
     * then this method will have to be overridden by the subclass.
     */
    public boolean inside(int x, int y) {
	return (x >= X && x < (X + W) && y >= Y && y < (Y + H));
    }

    /**
     * This utility method draws a rectangular subset of a highlight
     * image.
     */
    public void drawImage(Graphics g, Image img, int x, int y, int w, int h) {
	g.clipRect(x, y, w, h);
	g.drawImage(img, 0, 0);
	g.clearClip();
    }

    /**
     * This method highlights the specified area when the user enters
     * it with his mouse.  The standard highlight method is to replace
     * the indicated rectangular area of the image with the primary
     * highlighted image.
     */
    public void highlight(Graphics g, boolean on) {
	drawImage(g, on ? parent.hlImage : parent.baseImage, X, Y, W, H);
    }

    /**
     * This method changes the active state of the ImageArea, which
     * indicates whether the user is currently "inside" this area.
     * It turns around and calls the highlight method which is likely
     * to have been overridden by subclasses seeking a custom highlight.
     */
    public void setState(Graphics g, boolean on) {
	highlight(g, on);
	active = on;
    }

    /**
     * The press method is called when the user presses the mouse
     * button inside the ImageArea.  The location is supplied, but
     * the standard implementation is to call the overloaded method
     * with no arguments.
     */
    public void press(int x, int y) {
	press();
    }

    /**
     * The overloaded press method is called when the user presses the
     * mouse button inside the ImageArea.  This method can be overridden
     * if the ImageArea does not need to know the location of the press.
     */
    public void press() {
    }

    /**
     * The lift method is called when the user releases the mouse button.
     * The location is supplied, but the standard implementation is to
     * call the overloaded method with no arguments.  Only those ImageAreas
     * that were informed of a press will be informed of the corresponding
     * release.
     */
    public void lift(int x, int y) {
	lift();
    }

    /**
     * The overloaded lift method is called when the user releases the
     * mouse button.  This method can be overridden if the ImageArea
     * does not need to know the location of the release.
     */
    public void lift() {
    }

    /**
     * The drag method is called when the user moves the mouse while
     * the button is pressed.  Only those ImageAreas that were informed
     * of a press will be informed of the corresponding mouse movements.
     */
    public void drag(int x, int y) {
    }
}

/**
 * The classic "Fetch a URL" ImageArea class.
 * This class extends the basic ImageArea Class to fetch a URL when
 * the user clicks in the area.
 *
 * @author 	Jim Graham
 * @version 	1.1, 23 Mar 1995
 */
class HrefArea extends ImageMapArea {
    /** The URL to be fetched when the user clicks on this area. */
    URL anchor;

    /**
     * The argument string is the URL to be fetched.
     */
    public void handleArg(String arg) {
	anchor = new URL(parent.documentURL, arg);
    }

    /**
     * The status message area is updated to show the destination URL.
     * The default graphics highlight feedback is used.
     */
    public void highlight(Graphics g, boolean on) {
	super.highlight(g, on);
	parent.showStatus(on ? "Go To " + anchor.toExternalForm() : null);
    }

    /**
     * The new URL is fetched when the user releases the mouse button
     * only if they are still in the area.
     */
    public void lift(int x, int y) {
	if (inside(x, y)) {
	    parent.showDocument(anchor);
	}
	// Note that we should not be active, so no repaint is necessary.
    }
}

/**
 * An audio feedback ImageArea class.
 * This class extends the basic ImageArea Class to play a sound each
 * time the user enters the area.
 *
 * @author 	Jim Graham
 * @version 	1.1, 23 Mar 1995
 */
class SoundArea extends ImageMapArea {
    /** The URL of the sound to be played. */
    String sound;

    /**
     * The argument is the URL of the sound to be played.
     * This method also sets this type of area to be non-terminal.
     */
    public void handleArg(String arg) {
	sound = arg;
	terminal = false;
    }

    /**
     * The highlight method plays the sound in addition to the usual
     * graphical highlight feedback.
     */
    public void highlight(Graphics g, boolean on) {
	super.highlight(g, on);
	if (on) {
	    parent.play(sound);
	}
    }
}

/**
 * A click feedback ImageArea class.
 * This class extends the basic ImageArea Class to show the locations
 * of clicks in the image in the status message area.  This ImageArea
 * utility class is useful when setting up ImageMaps.
 *
 * @author 	Jim Graham
 * @version 	1.1, 23 Mar 1995
 */
class ClickArea extends ImageMapArea {
    /** The X location of the last mouse press. */
    int startx;
    /** The Y location of the last mouse press. */
    int starty;

    /**
     * The argument is ignored, but we use this method to set this type
     * of area to be non-terminal.
     */
    public void handleArg(String arg) {
	terminal = false;
    }

    /** This class overrides the highlight method to prevent highlighting. */
    public void highlight(Graphics g, boolean on) {
    }

    String ptstr(int x, int y) {
	return "("+x+", "+y+")";
    }

    /**
     * When the user presses the mouse button, start showing coordinate
     * feedback in the status message line.
     */
    public void press(int x, int y) {
	parent.showStatus("Clicked at "+ptstr(x, y));
	startx = x;
	starty = y;
    }

    /**
     * Update the coordinate feedback every time the user moves the mouse
     * while he has the button pressed.
     */
    public void drag(int x, int y) {
	parent.showStatus("Rectangle from "+ptstr(startx, starty)
			  +" to "+ptstr(x, y)
			  +" is "+(x-startx)+"x"+(y-starty));
    }

    /**
     * Update the coordinate feedback one last time when the user releases
     * the mouse button.
     */
    public void lift(int x, int y) {
	drag(x, y);
    }
}

/**
 * A message feedback ImageArea class.
 * This class extends the basic ImageArea Class to show the a given
 * message in the status message area when the user enters this area.
 *
 * @author 	Jim Graham
 * @version 	1.1, 23 Mar 1995
 */
class NameArea extends ImageMapArea {
    /** The string to be shown in the status message area. */
    String name;

    /**
     * The argument is the string to be displayed in the status message
     * area.  This method also sets this type of area to be non-terminal.
     */
    public void handleArg(String arg) {
	name = arg;
	terminal = false;
    }

    /**
     * The highlight method displays the message in addition to the usual
     * graphical highlight feedback.
     */
    public void highlight(Graphics g, boolean on) {
	super.highlight(g, on);
	parent.showStatus(on ? name : null);
    }

}

/**
 * An improved "Fetch a URL" ImageArea class.
 * This class extends the basic ImageArea Class to fetch a URL when
 * the user clicks in the area.  In addition, special custom highlights
 * are used to make the area look and feel like a 3-D button.
 *
 * @author 	Jim Graham
 * @version 	1.1, 23 Mar 1995
 */
class HrefButtonArea extends ImageMapArea {
    /** The URL to be fetched when the user clicks on this area. */
    URL anchor;

    /** The highlight image for the bright parts of the 3-D effect. */
    Image brightImage;
    /** The highlight image for the dark parts of the 3-D effect. */
    Image darkImage;
    /** The highlight image for the depressed part of the 3-D "button". */
    Image pressImage;

    /** This flag indicates if the "button" is currently pressed. */
    boolean pressed = false;

    /** The border size for the 3-D effect. */
    int border = 5;

    /**
     * The argument string is the URL to be fetched.
     * This method also constructs the various highlight images needed
     * to achieve the 3-D effect.
     */
    public void handleArg(String arg) {
	anchor = new URL(parent.documentURL, arg);
	brightImage = parent.getHighlight(parent.BRIGHTER, parent.hlpercent);
	darkImage = parent.getHighlight(parent.DARKER, parent.hlpercent);
	pressImage = parent.getHighlight(parent.DARKER, parent.hlpercent/2);
	if (border * 2 > W || border * 2 > H) {
	    border = Math.min(W, H) / 2;
	}
    }

    /**
     * A utility function to draw a 3-D button wrapped with an image.
     */
    public void drawButton(Graphics g) {
	drawImage(g, pressed ? darkImage : brightImage, X, Y, W, border);
	drawImage(g, pressed ? darkImage : brightImage, X, Y, border, H);
	int R = X + W - 1;
	int B = Y + H - 1;
	if (pressed) {
	    drawImage(g, pressImage,
		      X+border, Y+border, W-border*2, H-border*2);
	}
	for (int i = 0; i < border; i++) {
	    drawImage(g, pressed ? brightImage : darkImage, X+i, B-i, W-i, 1);
	    drawImage(g, pressed ? brightImage : darkImage, R-i, Y+i, 1, H-i);
	}
    }

    /**
     * The status message area is updated to show the destination URL.
     * The graphical highlight is achieved using the drawButton method.
     */
    public void highlight(Graphics g, boolean on) {
	if (on) {
	    drawButton(g);
	} else {
	    super.highlight(g, false);
	}
	parent.showStatus(on ? "Go To " + anchor.toExternalForm() : null);
    }

    /**
     * Since the highlight changes when the button is pressed, we need
     * to record the "pressed" state and induce a repaint.
     */
    public void press() {
	parent.repaint();
	pressed = true;
    }

    /**
     * The new URL is fetched when the user releases the mouse button
     * only if they are still in the area.
     */
    public void lift(int x, int y) {
	pressed = false;
	if (inside(x, y)) {
	    parent.showDocument(anchor);
	}
	// Note that we should not be active, so no repaint is necessary.
    }
}
