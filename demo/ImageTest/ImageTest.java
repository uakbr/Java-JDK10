/*
 * @(#)ImageTest.java	1.1 95/09/08
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */
import java.awt.*;
import java.awt.image.*;
import sun.awt.image.URLImageSource;
import java.applet.Applet;

public class ImageTest extends Applet {
    public void init() {
	setLayout(new BorderLayout());
	add("Center", new ImagePanel(this));
	add("North", new ImageHelp());
	reshape(0, 0, 800, 600);
	show();
    }
}

class ImageHelp extends Panel {
    public ImageHelp() {
	setLayout(new GridLayout(0, 2));
	add(new Label("Move the images using the arrow keys",
		      Label.CENTER));
	add(new Label("Resize the images using the PgUp/PgDn keys",
		      Label.CENTER));
	add(new Label("Toggle a red/blue color filter using the Home key",
		      Label.CENTER));
	add(new Label("Change the alpha using the shifted PgUp/PgDn keys",
		      Label.CENTER));
    }
}

class ImagePanel extends Panel {
    Applet applet;

    public ImagePanel(Applet app) {
	applet = app;
	setLayout(new BorderLayout());
	Panel grid = new Panel();
	grid.setLayout(new GridLayout(0, 2));
	add("Center", grid);
	grid.add(new ImageCanvas(applet, makeDitherImage(), 0.5));
	Image joe = applet.getImage(applet.getDocumentBase(),
				    "graphics/joe.surf.yellow.small.gif");
	grid.add(new ImageCanvas(applet, joe, 1.0));
	reshape(0, 0, 20, 20);
    }

    Image makeDitherImage() {
	int w = 100;
	int h = 100;
	int pix[] = new int[w * h];
	int index = 0;
	for (int y = 0; y < h; y++) {
	    int red = (y * 255) / (h - 1);
	    for (int x = 0; x < w; x++) {
		int blue = (x * 255) / (w - 1);
		pix[index++] = (255 << 24) | (red << 16) | blue;
	    }
	}
	return applet.createImage(new MemoryImageSource(w, h, pix, 0, w));
    }
}

class ImageCanvas extends Canvas implements ImageObserver {
    double 	hmult = 0;
    int		xadd = 0;
    int		yadd = 0;
    int		imgw = -1;
    int		imgh = -1;
    int		scalew = -1;
    int		scaleh = -1;
    boolean	focus = false;
    boolean	usefilter = false;
    static final int numalphas = 8;
    int		alpha = numalphas - 1;
    Image	imagevariants[] = new Image[numalphas * 2];
    ImageFilter colorfilter;
    Image	origimage;
    Image	curimage;
    Applet	applet;

    public ImageCanvas(Applet app, Image img, double mult) {
	applet = app;
	origimage = img;
	imagevariants[numalphas - 1] = origimage;
	hmult = mult;
	pickImage();
	reshape(0, 0, 100, 100);
    }

    public void gotFocus() {
	focus = true;
	repaint();
    }

    public void lostFocus() {
	focus = false;
	repaint();
    }

    public void paint(Graphics g) {
	Rectangle r = bounds();
	int hlines = r.height / 10;
	int vlines = r.width / 10;

	if (focus) {
	    g.setColor(Color.red);
	} else {
	    g.setColor(Color.darkGray);
	}
	g.drawRect(0, 0, r.width-1, r.height-1);
	g.drawLine(0, 0, r.width, r.height);
	g.drawLine(r.width, 0, 0, r.height);
	g.drawLine(0, r.height / 2, r.width, r.height / 2);
	g.drawLine(r.width / 2, 0, r.width / 2, r.height);
	if (imgw < 0) {
	    imgw = curimage.getWidth(this);
	    imgh = curimage.getHeight(this);
	    if (imgw < 0 || imgh < 0) {
		return;
	    }
	}
	if (scalew < 0) {
	    scalew = (int) (imgw * hmult);
	    scaleh = (int) (imgh * hmult);
	}
	if (imgw != scalew || imgh != scaleh) {
	    g.drawImage(curimage, xadd, yadd, scalew, scaleh, this);
	} else {
	    g.drawImage(curimage, xadd, yadd, this);
	}

    }

    static final long updateRate = 100;

    public synchronized boolean imageUpdate(Image img, int infoflags,
					    int x, int y, int w, int h) {
	if (img != curimage) {
	    return false;
	}
	boolean ret = true;
	boolean dopaint = false;
	long updatetime = 0;
	if ((infoflags & WIDTH) != 0) {
	    imgw = w;
	    dopaint = true;
	}
	if ((infoflags & HEIGHT) != 0) {
	    imgh = h;
	    dopaint = true;
	}
	if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
	    dopaint = true;
	    ret = false;
	} else if ((infoflags & SOMEBITS) != 0) {
	    dopaint = true;
	    updatetime = updateRate;
	}
	if ((infoflags & ERROR) != 0) {
	    ret = false;
	}
	if (dopaint) {
	    repaint(updatetime);
	}
	return ret;
    }

    public synchronized Image pickImage() {
	int index = alpha;
	if (usefilter) {
	    index += numalphas;
	}
	Image choice = imagevariants[index];
	if (choice == null) {
	    choice = imagevariants[alpha];
	    if (choice == null) {
		int alphaval = (alpha * 255) / (numalphas - 1);
		ImageFilter imgf = new AlphaFilter(alphaval);
		ImageProducer src = origimage.getSource();
		src = new FilteredImageSource(src, imgf);
		choice = applet.createImage(src);
		imagevariants[alpha] = choice;
	    }
	    if (usefilter) {
		if (colorfilter == null) {
		    colorfilter = new RedBlueSwapFilter();
		}
		ImageProducer src = choice.getSource();
		src = new FilteredImageSource(src, colorfilter);
		choice = applet.createImage(src);
	    }
	    imagevariants[index] = choice;
	}
	curimage = choice;
	return choice;
    }

    public synchronized boolean handleEvent(Event e) {
	switch (e.id) {
	  case Event.KEY_ACTION:
	  case Event.KEY_PRESS:
	    switch (e.key) {
	      case Event.HOME:
		usefilter = !usefilter;
		pickImage();
		repaint();
		return true;
	      case Event.UP:
		yadd -= 5;
		repaint();
		return true;
	      case Event.DOWN:
		yadd += 5;
		repaint();
		return true;
	      case Event.RIGHT:
	      case 'r':
		xadd += 5;
		repaint();
		return true;
	      case Event.LEFT:
		xadd -= 5;
		repaint();
		return true;
	      case Event.PGUP:
		if ((e.modifiers & Event.SHIFT_MASK) != 0) {
		    if (++alpha > numalphas - 1) {
			alpha = numalphas - 1;
		    }
		    pickImage();
		} else {
		    hmult *= 1.2;
		}
		scalew = scaleh = -1;
		repaint();
		return true;
	      case Event.PGDN:
		if ((e.modifiers & Event.SHIFT_MASK) != 0) {
		    if (--alpha < 0) {
			alpha = 0;
		    }
		    pickImage();
		} else {
		    hmult /= 1.2;
		}
		scalew = scaleh = -1;
		repaint();
		return true;
	      default:
		return false;
	    }

	  default:
	    return false;
	}
    }

}

class RedBlueSwapFilter extends RGBImageFilter {
    public RedBlueSwapFilter() {
	canFilterIndexColorModel = true;
    }

    public void setColorModel(ColorModel model) {
	if (model instanceof DirectColorModel) {
	    DirectColorModel dcm = (DirectColorModel) model;
	    int rm = dcm.getRedMask();
	    int gm = dcm.getGreenMask();
	    int bm = dcm.getBlueMask();
	    int am = dcm.getAlphaMask();
	    int bits = dcm.getPixelSize();
	    dcm = new DirectColorModel(bits, bm, gm, rm, am);
	    substituteColorModel(model, dcm);
	    consumer.setColorModel(dcm);
	} else {
	    super.setColorModel(model);
	}
    }

    public int filterRGB(int x, int y, int rgb) {
	return ((rgb & 0xff00ff00)
		| ((rgb & 0xff0000) >> 16)
		| ((rgb & 0xff) << 16));
    }
}

class AlphaFilter extends RGBImageFilter {
    ColorModel origmodel;
    ColorModel newmodel;

    int alphaval;

    public AlphaFilter(int alpha) {
	alphaval = alpha;
	canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
	int alpha = (rgb >> 24) & 0xff;
	alpha = alpha * alphaval / 255;
	return ((rgb & 0x00ffffff) | (alpha << 24));
    }
}
