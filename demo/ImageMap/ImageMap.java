import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.net.URL;
import java.awt.image.*;
import java.net.MalformedURLException;

/**
 * An extensible ImageMap applet class.
 * The active areas on the image are controlled by ImageArea classes
 * that can be dynamically loaded over the net.
 *
 * @author 	Jim Graham
 * @version 	%I%, %G%
 */
public class ImageMap extends Applet {
    /**
     * The unhighlighted image being mapped.
     */
    Image baseImage;

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
     * Get a rectangular region of the baseImage highlighted according to
     * the primary highlight specification.
     */
    Image getHighlight(int x, int y, int w, int h) {
	return getHighlight(x, y, w, h, hlmode, hlpercent);
    }

    /**
     * Get a rectangular region of the baseImage with a specific highlight.
     */
    Image getHighlight(int x, int y, int w, int h, int mode, int percent) {
	return getHighlight(x, y, w, h, new HighlightFilter(mode == BRIGHTER,
							    percent));
    }

    /**
     * Get a rectangular region of the baseImage modified by an image filter.
     */
    Image getHighlight(int x, int y, int w, int h, ImageFilter filter) {
	Image cropped = makeImage(baseImage, new CropImageFilter(x, y, w, h));
	return makeImage(cropped, filter);
    }

    /**
     * Make the primary highlighted version of the baseImage.
     */
    Image makeImage(Image orig, ImageFilter filter) {
	return createImage(new FilteredImageSource(orig.getSource(), filter));
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

	parseHighlight(getParameter("highlight"));
	baseImage = getImage(getDocumentBase(), getParameter("img"));
	Vector areaVec = new Vector();
	int num = 1;
	while (true) {
	    ImageMapArea newArea;
	    s = getParameter("area"+num);
	    if (s == null) {
		// Try rect for backwards compatibility.
		s = getParameter("rect"+num);
		if (s == null) {
		    break;
		}
		String url = getParameter("href"+num);
		if (url != null)
		    s += "," + url;
		newArea = new HrefArea();
	    } else {
		int classend = s.indexOf(",");
		try {
		    String name = s.substring(0, classend);
		    newArea = (ImageMapArea) Class.forName(name).newInstance();
		} catch (Exception e) {
		    e.printStackTrace();
		    break;
		}
		s = s.substring(classend+1);
	    }
	    newArea.init(this, s);
	    areaVec.addElement(newArea);
	    num++;
	}
	areas = new ImageMapArea[areaVec.size()];
	areaVec.copyInto(areas);
	checkSize();
    }

    /**
     * Check the size of this applet while the image is being loaded.
     */
    synchronized void checkSize() {
	int w = baseImage.getWidth(this);
	int h = baseImage.getHeight(this);
	if (w > 0 && h > 0) {
	    resize(w, h);
	    repaintrect.x = repaintrect.y = 0;
	    repaintrect.width = w;
	    repaintrect.height = h;
	    fullrepaint = true;
	    repaint();
	}
    }

    private boolean fullrepaint = false;
    private Rectangle repaintrect = new Rectangle();
    private long lastupdate = 0;
    private final static long UPDATERATE = 100;

    /**
     * Handle updates from images being loaded.
     */
    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int width, int height) {
	if ((infoflags & (WIDTH | HEIGHT)) != 0) {
	    checkSize();
	}
	if ((infoflags & (SOMEBITS | FRAMEBITS | ALLBITS)) != 0) {
	    repaint(((infoflags & (FRAMEBITS | ALLBITS)) != 0)
		    ? 0 : UPDATERATE,
		    x, y, width, height);
	}
	return (infoflags & (ALLBITS | ERROR)) == 0;
    }

    /**
     * Paint the image and all active highlights.
     */
    public void paint(Graphics g) {
	synchronized(this) {
	    if (fullrepaint) {
		g = g.create();
		g.clipRect(repaintrect.x, repaintrect.y,
			   repaintrect.width, repaintrect.height);
		fullrepaint = false;
	    }
	}
	if (baseImage == null) {
	    return;
	}
	g.drawImage(baseImage, 0, 0, this);
	if (areas != null) {
	    for (int i = areas.length; --i >= 0; ) {
		if (areas[i].active || areas[i].entered) {
		    areas[i].setState(g, areas[i].entered);
		}
	    }
	}
    }

    /**
     * Update the active highlights on the image.
     */
    public void update(Graphics g) {
	if (fullrepaint) {
	    paint(g);
	    return;
	}
	if (baseImage == null) {
	    return;
	}
	g.drawImage(baseImage, 0, 0, this);
	if (areas == null) {
	    return;
	}
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
    public boolean mouseMove(java.awt.Event evt, int x, int y) {
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

	return true;
    }

    int pressX;
    int pressY;

    /**
     * Inform all active ImageAreas of a mouse press.
     */
    public boolean mouseDown(java.awt.Event evt, int x, int y) {
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

	return true;
    }

    /**
     * Inform all active ImageAreas of a mouse release.
     * Only those areas that were inside the original mouseDown()
     * are informed of the mouseUp.
     */
    public boolean mouseUp(java.awt.Event evt, int x, int y) {
	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(pressX, pressY)) {
		areas[i].lift(x, y);
		if (areas[i].terminal) {
		    break;
		}
	    }
	}

	return true;
    }

    /**
     * Inform all active ImageAreas of a mouse drag.
     * Only those areas that were inside the original mouseDown()
     * are informed of the mouseUp.
     */
    public boolean mouseDrag(java.awt.Event evt, int x, int y) {
	mouseMove(evt, x, y);
	for (int i = 0; i < areas.length; i++) {
	    if (areas[i].inside(pressX, pressY)) {
		areas[i].drag(x, y);
		if (areas[i].terminal) {
		    break;
		}
	    }
	}

	return true;
    }
}

/**
 * The base ImageArea class.
 * This class performs the basic functions that most ImageArea
 * classes will need and delegates specific actions to the subclasses.
 *
 * @author 	Jim Graham
 * @version 	%I%, %G%
 */
class ImageMapArea implements ImageObserver {
    /** The applet parent that contains this ImageArea. */
    ImageMap parent;
    /** The X location of the area (if rectangular). */
    int X;
    /** The Y location of the area (if rectangular). */
    int Y;
    /** The size().width of the area (if rectangular). */
    int W;
    /** The size().height of the area (if rectangular). */
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
     * This is the default highlight image if no special effects are
     * needed to draw the highlighted image.  It is created by the
     * default "makeImages()" method.
     */
    Image hlImage;

    /**
     * Initialize this ImageArea as called from the applet.
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
	makeImages();
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
     * This method sets the image to be used to render the ImageArea
     * when it is highlighted.
     */
    public void setHighlight(Image img) {
	hlImage = img;
    }

    /**
     * This method handles the construction of the various images
     * used to highlight this particular ImageArea when the user
     * interacts with it.
     */
    public void makeImages() {
	setHighlight(parent.getHighlight(X, Y, W, H));
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
    public void drawImage(Graphics g, Image img, int imgx, int imgy,
			  int x, int y, int w, int h) {
	Graphics ng = g.create();
	ng.clipRect(x, y, w, h);
	ng.drawImage(img, imgx, imgy, this);
    }

    /**
     * This method handles the updates from drawing the images.
     */
    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int width, int height) {
	if (img == hlImage) {
	    return parent.imageUpdate(img, infoflags, x + X, y + Y,
				      width, height);
	} else {
	    return false;
	}
    }

    /**
     * This utility method shows a string in the status bar.
     */
    public void showStatus(String msg) {
	parent.getAppletContext().showStatus(msg);
    }

    /**
     * This utility method tells the browser to visit a URL.
     */
    public void showDocument(URL u) {
	parent.getAppletContext().showDocument(u);
    }

    /**
     * This method highlights the specified area when the user enters
     * it with his mouse.  The standard highlight method is to replace
     * the indicated rectangular area of the image with the primary
     * highlighted image.
     */
    public void highlight(Graphics g, boolean on) {
	if (on) {
	    g.drawImage(hlImage, X, Y, this);
	} else {
	    drawImage(g, parent.baseImage, 0, 0, X, Y, W, H);
	}
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
 * @version 	%I%, %G%
 */
class HrefArea extends ImageMapArea {
    /** The URL to be fetched when the user clicks on this area. */
    URL anchor;

    /**
     * The argument string is the URL to be fetched.
     */
    public void handleArg(String arg) {
	try {
	    anchor = new URL(parent.getDocumentBase(), arg);
	} catch (MalformedURLException e) {
	    anchor = null;
	}
    }

    /**
     * The status message area is updated to show the destination URL.
     * The default graphics highlight feedback is used.
     */
    public void highlight(Graphics g, boolean on) {
	super.highlight(g, on);
	showStatus((on && anchor != null)
		   ? "Go To " + anchor.toExternalForm()
		   : null);
    }

    /**
     * The new URL is fetched when the user releases the mouse button
     * only if they are still in the area.
     */
    public void lift(int x, int y) {
	if (inside(x, y) && anchor != null) {
	    showDocument(anchor);
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
 * @version 	%I%, %G%
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
	if (on && !active) {
	    parent.play(parent.getDocumentBase(), sound);
	}
	super.highlight(g, on);
    }
}

/**
 * An click feedback ImageArea class.
 * This class extends the basic ImageArea Class to show the locations
 * of clicks in the image in the status message area.  This utility
 * ImageArea class is useful when setting up ImageMaps.
 *
 * @author 	Jim Graham
 * @version 	%I%, %G%
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
	showStatus("Clicked at "+ptstr(x, y));
	startx = x;
	starty = y;
    }

    /**
     * Update the coordinate feedback every time the user moves the mouse
     * while he has the button pressed.
     */
    public void drag(int x, int y) {
	showStatus("Rectangle from "+ptstr(startx, starty)
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
 * @version 	%I%, %G%
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
	showStatus(on ? name : null);
    }

}

/**
 * An improved "Fetch a URL" ImageArea class.
 * This class extends the basic ImageArea Class to fetch a URL when
 * the user clicks in the area.  In addition, special custom highlights
 * are used to make the area look and feel like a 3-D button.
 *
 * @author 	Jim Graham
 * @version 	%I%, %G%
 */
class HrefButtonArea extends ImageMapArea {
    /** The URL to be fetched when the user clicks on this area. */
    URL anchor;
    /** The highlight image for when the button is "UP". */
    Image upImage;
    /** The highlight image for when the button is "DOWN". */
    Image downImage;
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
	try {
	    anchor = new URL(parent.getDocumentBase(), arg);
	} catch (MalformedURLException e) {
	    anchor = null;
	}
	if (border * 2 > W || border * 2 > H) {
	    border = Math.min(W, H) / 2;
	}
    }

    public void makeImages() {
	upImage = parent.getHighlight(X, Y, W, H,
				      new ButtonFilter(false,
						       parent.hlpercent,
						       border, W, H));
	downImage = parent.getHighlight(X, Y, W, H,
					new ButtonFilter(true,
							 parent.hlpercent,
							 border, W, H));
    }

    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int width, int height) {
	if (img == (pressed ? downImage : upImage)) {
	    return parent.imageUpdate(img, infoflags, x + X, y + Y,
				      width, height);
	} else {
	    return (img == downImage || img == upImage);
	}
    }

    /**
     * The status message area is updated to show the destination URL.
     * The graphical highlight is achieved using the ButtonFilter.
     */
    public void highlight(Graphics g, boolean on) {
	if (on) {
	    setHighlight(pressed ? downImage : upImage);
	}
	super.highlight(g, on);
	showStatus((on && anchor != null)
		   ? "Go To " + anchor.toExternalForm()
		   : null);
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
	parent.repaint();
	if (inside(x, y) && anchor != null) {
	    showDocument(anchor);
	}
    }
}

/**
 * An improved, round, "Fetch a URL" ImageArea class.
 * This class extends the HrefButtonArea Class to make the 3D button
 * a rounded ellipse.  All of the same feedback and operational
 * charactistics as the HrefButtonArea apply.
 *
 * @author 	Jim Graham
 * @version 	%I%, %G%
 */
class RoundHrefButtonArea extends HrefButtonArea {
    public void makeImages() {
	upImage = parent.getHighlight(X, Y, W, H,
				      new RoundButtonFilter(false,
							    parent.hlpercent,
							    border, W, H));
	downImage = parent.getHighlight(X, Y, W, H,
					new RoundButtonFilter(true,
							      parent.hlpercent,
							      border, W, H));
    }
}

class HighlightFilter extends RGBImageFilter {
    boolean brighter;
    int percent;

    public HighlightFilter(boolean b, int p) {
	brighter = b;
	percent = p;
	canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
	int r = (rgb >> 16) & 0xff;
	int g = (rgb >> 8) & 0xff;
	int b = (rgb >> 0) & 0xff;
	if (brighter) {
	    r = (255 - ((255 - r) * (100 - percent) / 100));
	    g = (255 - ((255 - g) * (100 - percent) / 100));
	    b = (255 - ((255 - b) * (100 - percent) / 100));
	} else {
	    r = (r * (100 - percent) / 100);
	    g = (g * (100 - percent) / 100);
	    b = (b * (100 - percent) / 100);
	}
	if (r < 0) r = 0;
	if (r > 255) r = 255;
	if (g < 0) g = 0;
	if (g > 255) g = 255;
	if (b < 0) b = 0;
	if (b > 255) b = 255;
	return (rgb & 0xff000000) | (r << 16) | (g << 8) | (b << 0);
    }
}

class ButtonFilter extends RGBImageFilter {
    boolean pressed;
    int defpercent;
    int border;
    int width;
    int height;

    ColorModel models[] = new ColorModel[7];
    ColorModel origbuttonmodel;

    public ButtonFilter(boolean press, int p, int b, int w, int h) {
	pressed = press;
	defpercent = p;
	border = b;
	width = w;
	height = h;
    }

    public void setHints(int hints) {
	super.setHints(hints & (~ImageConsumer.COMPLETESCANLINES));
    }

    public void setColorModel(ColorModel model) {
	if (model instanceof IndexColorModel && true) {
	    IndexColorModel icm = (IndexColorModel) model;
	    models[0] = filterIndexColorModel(icm, false, false, 0);
	    models[1] = filterIndexColorModel(icm, true, !pressed, defpercent);
	    models[2] = null;
	    if (pressed) {
		models[3] = filterIndexColorModel(icm, true, false,
						  defpercent/2);
	    } else {
		models[3] = models[0];
	    }
	    models[4] = null;
	    models[5] = filterIndexColorModel(icm, true, pressed, defpercent);
	    models[6] = models[0];
	    origbuttonmodel = model;
	    consumer.setColorModel(models[3]);
	} else {
	    super.setColorModel(model);
	}
    }

    public IndexColorModel filterIndexColorModel(IndexColorModel icm,
						 boolean opaque,
						 boolean brighter,
						 int percent) {
	byte r[] = new byte[256];
	byte g[] = new byte[256];
	byte b[] = new byte[256];
	byte a[] = new byte[256];
	int mapsize = icm.getMapSize();
	icm.getReds(r);
	icm.getGreens(g);
	icm.getBlues(b);
	if (opaque) {
	    icm.getAlphas(a);
	    for (int i = 0; i < mapsize; i++) {
		int rgb = filterRGB(icm.getRGB(i), brighter, percent);
		a[i] = (byte) (rgb >> 24);
		r[i] = (byte) (rgb >> 16);
		g[i] = (byte) (rgb >> 8);
		b[i] = (byte) (rgb >> 0);
	    }
	}
	return new IndexColorModel(icm.getPixelSize(), mapsize, r, g, b, a);
    }

    /**
     * Define the ranges of varying highlight for the button.
     * ranges is an array of 8 values which split up a scanline into
     * 7 different regions of highlighting effect:
     *
     * ranges[0-1] = area outside of left edge of button
     * ranges[1-2] = area inside UpperLeft highlight region left of center
     * ranges[2-3] = area requiring custom highlighting left of center
     * ranges[3-4] = area inside center of button
     * ranges[4-5] = area requiring custom highlighting right of center
     * ranges[5-6] = area inside LowerRight highlight region right of center
     * ranges[6-7] = area outside of right edge of button
     *
     * Note that ranges[0-1] and ranges[6-7] are empty where the edges of
     * the button touch the left and right edges of the image (everywhere
     * on a square button) and ranges[2-3] and ranges[4-5] are only nonempty
     * in those regions where the UpperLeft highlighting has leaked over
     * the "top" of the button onto parts of its right edge or where the
     * LowerRight highlighting has leaked under the "bottom" of the button
     * onto parts of its left edge (can't happen on square buttons, happens
     * occasionally on round buttons).
     */
    public void buttonRanges(int y, int ranges[]) {
	ranges[0] = ranges[1] = 0;
	if (y < border) {
	    ranges[2] = ranges[3] = ranges[4] = ranges[5] = width - y;
	} else if (y > height - border) {
	    ranges[2] = ranges[3] = ranges[4] = ranges[5] = height - y;
	} else {
	    ranges[2] = ranges[3] = border;
	    ranges[4] = ranges[5] = width - border;
	}
	ranges[6] = ranges[7] = width;
    }

    public void setPixels(int x, int y, int w, int h,
			  ColorModel model, byte pixels[], int off,
			  int scansize) {
	if (model == origbuttonmodel) {
	    int ranges[] = new int[8];
	    int x2 = x + w;
	    for (int cy = y; cy < y + h; cy++) {
		buttonRanges(cy, ranges);
		for (int i = 0; i < 7; i++) {
		    if (x2 > ranges[i] && x < ranges[i+1]) {
			int cx1 = Math.max(x, ranges[i]);
			int cx2 = Math.min(x2, ranges[i+1]);
			if (models[i] == null) {
			    super.setPixels(cx1, cy, cx2 - cx1, 1,
					    model, pixels,
					    off + (cx1 - x), scansize);
			} else {
			    if (cx1 < cx2) {
				consumer.setPixels(cx1, cy, cx2 - cx1, 1,
						   models[i], pixels,
						   off + (cx1 - x), scansize);
			    }
			}
		    }
		}
		off += scansize;
	    }
	} else {
	    super.setPixels(x, y, w, h, model, pixels, off, scansize);
	}
    }

    public int filterRGB(int x, int y, int rgb) {
	boolean brighter;
	int percent;
	if ((x < border && y < height - x) || (y < border && x < width - y)) {
	    brighter = !pressed;
	    percent = defpercent;
	} else if (x >= width - border || y >= height - border) {
	    brighter = pressed;
	    percent = defpercent;
	} else if (pressed) {
	    brighter = false;
	    percent = defpercent / 2;
	} else {
	    return rgb & 0x00ffffff;
	}
	return filterRGB(rgb, brighter, percent);
    }

    public int filterRGB(int rgb, boolean brighter, int percent) {
	int r = (rgb >> 16) & 0xff;
	int g = (rgb >> 8) & 0xff;
	int b = (rgb >> 0) & 0xff;
	if (brighter) {
	    r = (255 - ((255 - r) * (100 - percent) / 100));
	    g = (255 - ((255 - g) * (100 - percent) / 100));
	    b = (255 - ((255 - b) * (100 - percent) / 100));
	} else {
	    r = (r * (100 - percent) / 100);
	    g = (g * (100 - percent) / 100);
	    b = (b * (100 - percent) / 100);
	}
	if (r < 0) r = 0;
	if (g < 0) g = 0;
	if (b < 0) b = 0;
	if (r > 255) r = 255;
	if (g > 255) g = 255;
	if (b > 255) b = 255;
	return (rgb & 0xff000000) | (r << 16) | (g << 8) | (b << 0);
    }
}

class RoundButtonFilter extends ButtonFilter {
    int Xcenter;
    int Ycenter;
    int Yradsq;
    int innerW;
    int innerH;
    int Yrad2sq;

    public RoundButtonFilter(boolean press, int p, int b, int w, int h) {
	super(press, p, b, w, h);
	Xcenter = w/2;
	Ycenter = h/2;
	Yradsq = h * h / 4;
	innerW = w - border * 2;
	innerH = h - border * 2;
	Yrad2sq = innerH * innerH / 4;
    }

    public void buttonRanges(int y, int ranges[]) {
	int yrel = Math.abs(Ycenter - y);
	int xrel = (int) (Math.sqrt(Yradsq - yrel * yrel) * width / height);
	int xslash = width - (y * width / height);
	ranges[0] = 0;
	ranges[1] = Xcenter - xrel;
	ranges[6] = Xcenter + xrel;
	ranges[7] = width;
	if (y < border) {
	    ranges[2] = ranges[3] = ranges[4] = Xcenter;
	    ranges[5] = ranges[6];
	} else if (y + border >= height) {
	    ranges[2] = ranges[1];
	    ranges[3] = ranges[4] = ranges[5] = Xcenter;
	} else {
	    int xrel2 = (int) (Math.sqrt(Yrad2sq - yrel * yrel)
			       * innerW / innerH);
	    ranges[3] = Xcenter - xrel2;
	    ranges[4] = Xcenter + xrel2;
	    if (y < Ycenter) {
		ranges[2] = ranges[3];
		ranges[5] = ranges[6];
	    } else {
		ranges[2] = ranges[1];
		ranges[5] = ranges[4];
	    }
	}
    }

    private int savedranges[];
    private int savedy;

    private synchronized int[] getRanges(int y) {
	if (savedranges == null || savedy != y) {
	    if (savedranges == null) {
		savedranges = new int[8];
	    }
	    buttonRanges(y, savedranges);
	    savedy = y;
	}
	return savedranges;
    }

    public int filterRGB(int x, int y, int rgb) {
	boolean brighter;
	int percent;
	int i;
	int ranges[] = getRanges(y);
	for (i = 0; i < 7; i++) {
	    if (x >= ranges[i] && x < ranges[i+1]) {
		break;
	    }
	}
	double angle;
	switch (i) {
	default:
	case 0:
	case 6:
	    return rgb & 0x00ffffff;
	case 1:
	    brighter = !pressed;
	    percent = defpercent;
	    break;
	case 5:
	    brighter = pressed;
	    percent = defpercent;
	    break;
	case 2:
	    angle = Math.atan2(y - Ycenter, Xcenter - x);
	    percent = defpercent - ((int) (Math.cos(angle) * 2 * defpercent));
	    if (!pressed) {
		percent = -percent;
	    }
	    if (percent == 0) {
		return rgb;
	    } else if (percent < 0) {
		percent = -percent;
		brighter = false;
	    } else {
		brighter = true;
	    }
	    break;
	case 4:
	    angle = Math.atan2(Ycenter - y, x - Xcenter);
	    percent = defpercent - ((int) (Math.cos(angle) * 2 * defpercent));
	    if (pressed) {
		percent = -percent;
	    }
	    if (percent == 0) {
		return rgb;
	    } else if (percent < 0) {
		percent = -percent;
		brighter = false;
	    } else {
		brighter = true;
	    }
	    break;
	case 3:
	    if (!pressed) {
		return rgb & 0x00ffffff;
	    }
	    brighter = false;
	    percent = defpercent;
	    break;
	}
	return filterRGB(rgb, brighter, percent);
    }
}
