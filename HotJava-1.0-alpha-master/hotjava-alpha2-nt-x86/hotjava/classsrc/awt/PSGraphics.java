/*-
 * Copyright (c) 1994 by Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * @(#)PSGraphics.java	1.14 95/02/23 1/10/95
 *
 *      Jim Graham, 1/10/95
 */
package awt;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * PSGraphics is an object that encapsulates a graphics context for a
 * particular PostScript file.
 * 
 * @version 1.14 23 Feb 1995
 * @author Jim Graham
 */
public class PSGraphics extends Graphics {
    public static Dimension Letter = new Dimension((72 * 17)/2, 11 * 72);
    public static Dimension Legal = new Dimension((72 * 17)/2, 14 * 72);
    public static Dimension Executive = new Dimension((72 * 15)/2, 10 * 72);
    public static Dimension A4 = new Dimension((int) (72 * 210 / 25.4),
					       (int) (72 * 297 / 25.4));

    PrintStream		ps;
    public String	title;
    public Dimension	paperDim;
    public Dimension	layoutDim;
    public Dimension	outputDim;
    public int		marginTop = 72 / 2;
    public int		marginBottom = 72 / 2;
    public int		marginLeft = 72 / 2;
    public int		marginRight = 72 / 2;
    public int		pageNumber = 0;

    private PSGraphics	parent;

    private Color curColor;
    private Font curFont;
    private float scalefactor;
    private int adjustX;
    private int adjustY;
    private boolean prologSent = false;

    static private String SetColorName = "SC";
    static private String SetFontName = "SF";
    static private String RectClipName = "RC";
    static private String InitClipName = "IC";
    static private String RectFillName = "RF";
    static private String RectStrokeName = "RS";
    static private String DrawStringName = "DS";
    static private String DrawLineName = "DL";
    static private String ScaleName = "S";
    static private String StartPageName = "SP";
    static private String EndPageName = "EP";
    static private String GsaveName = "GS";
    static private String GrestoreName = "GR";
    static private String SetColormapName = "CM";
    static private String DrawImageName = "DI";

    static private String hexString = "0123456789ABCDEF";
    static private String testString =
	"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890";

    /**
     * Create a graphics context.
     */
    public PSGraphics(OutputStream os, String docTitle,
		      Dimension layout, Dimension paper) {
	ps = new PrintStream(os);
	title = docTitle;
	layoutDim = layout;
	setPaper(paper);
    }

    public PSGraphics(PSGraphics psg) {
	parent = psg;
	parent.gsave();
	title = psg.title;
	paperDim = psg.paperDim;
	layoutDim = psg.layoutDim;
	outputDim = psg.outputDim;
	marginTop = psg.marginTop;
	marginBottom = psg.marginBottom;
	marginLeft = psg.marginLeft;
	marginRight = psg.marginRight;
	pageNumber = psg.pageNumber;
	ps = psg.ps;
	curColor = psg.curColor;
	curFont = psg.curFont;
	scalefactor = psg.scalefactor;
	adjustX = psg.adjustX;
	adjustY = psg.adjustY;
	background = psg.background;
	foreground = psg.foreground;
	font = psg.font;
	originX = psg.originX;
	originY = psg.originY;
	scaleX = psg.scaleX;
	scaleY = psg.scaleY;
    }

    public Graphics createChild(int oX, int oY, float sX, float sY) {
	PSGraphics child = new PSGraphics(this);
	child.setOrigin(oX, oY);
	child.setScaling(sX, sY);

	return (Graphics) child;
    }

    public void dispose() {
	if (parent != null) {
	    parent.grestore();
	}
    }

    private void newAdjust() {
	adjustX = ((int) ((float) marginLeft / scalefactor)
		   + originX);
	adjustY = (outputDim.height
		   + (int) ((float) marginLeft / scalefactor)
		   - originY);
    }

    private void newDims() {
	int drawableWidth, drawableHeight;

	drawableWidth = paperDim.width - marginLeft - marginRight;
	drawableHeight = paperDim.height - marginTop - marginBottom;

	if (drawableWidth >= layoutDim.width)
	    scalefactor = 1.0;
	else
	    scalefactor = ((float) drawableWidth / (float) layoutDim.width);

	outputDim = new Dimension(layoutDim.width,
				  (int) ((float) drawableHeight/scalefactor));
	newAdjust();
    }

    public void setPaper(Dimension paper) {
	if (paper == null)
	    paper = Letter;
	paperDim = paper;
	newDims();
    }

    private void outRect(int X, int Y, int W, int H) {
	ps.print((adjustX + X)+" "+(adjustY - Y)+" "+W+" "+(-H)+" ");
    }

    private void outPoint(int X, int Y) {
	ps.print((adjustX + X)+" "+(adjustY - Y)+" ");
    }

    private void outPoint(float X, float Y) {
	ps.print(((float) adjustX + X)+" "+((float) adjustY - Y)+" ");
    }

    private void outColor(Color color) {
    	if (curColor == null || !curColor.equal(color)) {
	    ps.println(color.r+" "+color.g+" "+color.b+" "+SetColorName);
	    curColor = color;
	}
    }

    private void outFont(Font font) {
	// REMIND: all we check is the size and style for now...
	if (font != curFont) {
	    int desiredWidth = font.stringWidth(testString);
	    int fontIndex = font.style & ~Font.UNDERLINE;
	    if (!font.family.substring(0,5).equalsIgnoreCase("times"))
		fontIndex += 4;
	    ps.println("% Font["
		       +font.family+", "
		       +font.style+", "
		       +font.actualHeight
		       +"]");
	    ps.println(desiredWidth+" "+font.actualHeight+" "+fontIndex+" "
		       +SetFontName);
	    curFont = font;
	}
    }

    private void outScale(float sx, float sy) {
	ps.println(sx+" "+sy+" "+ScaleName);
    }

    private void outStr(String str) {
	StringBuffer outbuf = new StringBuffer();
	outbuf.appendChar('(');
	for (int i = 0; i < str.length(); i++) {
	    char ch = str.charAt(i);
	    if (ch == '(' || ch == ')' || ch == '\\')
		outbuf.appendChar('\\');
	    outbuf.appendChar(ch);
	}
	outbuf.appendChar(')');
	ps.print(outbuf.toString());
    }

    public synchronized void gsave() {
	ps.println(GsaveName);
    }

    public synchronized void grestore() {
	ps.println(GrestoreName);
    }

    /**
     * Sets the font for all subsequent text-drawing operations.
     */
    public void setFont(Font f) {
	font = f;
    }

    /**
     * Sets the foreground color.
     */
    public void setForeground(Color c) {
	foreground = c;
    }

    /**
     * Sets the background color.
     */
    public void setBackground(Color c) {
	background = c;
    }

    /** Sets the clipping rectangle for this Graphics context. */
    public synchronized void clipRect(int X, int Y, int W, int H) {
	outRect(X, Y, W, H);
	ps.println(RectClipName);
    }

    /** Clears the clipping region. */
    public synchronized void clearClip() {
	ps.println(InitClipName);
    }
    
    /** Fills the rectangle indicated by x,y,w,h in the given Color. */
    private synchronized void setRect(int X, int Y, int W, int H,
				      Color color) {
	outColor(color);
	outRect(X, Y, W, H);
	ps.println(RectFillName);
    }
    /** Clears the rectangle indicated by x,y,w,h. */
    public void clearRect(int X, int Y, int W, int H) {
	setRect(X, Y, W, H, background);
    }
    /** Fills the given rectangle with the foreground color. */
    public void fillRect(int X, int Y, int W, int H) {
	setRect(X, Y, W, H, foreground);
    }
    /** Draws the given rectangle. */
    public synchronized void drawRect(int X, int Y, int W, int H) {
	outColor(foreground);
	outRect(X, Y, W, H);
	ps.println(RectStrokeName);
    }
    /** Draws the given string. */
    public synchronized void drawString(String str, int x, int y) {
	outFont(font);
	outColor(foreground);
	outStr(str);
	ps.print(" "+font.stringWidth(str)+" ");
	outPoint(x, y);
	ps.println(DrawStringName);
    }
    /** Draws the given character array. */
    public void drawChars(char chars[], int offset, int length, int x, int y) {
	// REMIND: Check the current font.
	drawString(new String(chars, offset, length), x, y);
    }
    /** Draws the given byte array. */
    public void drawBytes(byte bytes[], int offset, int length, int x, int y) {
	// REMIND: Check the current font.
	drawString(new String(bytes, 0, offset, length), x, y);
    }
    /** Draws the given string and returns the length of the drawn
      string in pixels.  If font isn't set then returns -1. */
    public int drawStringWidth(String str, int x, int y) {
    	drawString(str, x, y);
	return font.stringWidth(str);
    }
    /** Draws the given character array and return the width in
      pixels. If font isn't set then returns -1. */
    public int drawCharsWidth(char chars[], int offset, int length,
			      int x, int y) {
    	drawChars(chars, offset, length, x, y);
	return font.charsWidth(chars, offset, length);
    }
    /** Draws the given byte array and return the width in
      pixels. If font isn't set then returns -1. */
    public int drawBytesWidth(byte bytes[], int offset, int length,
			      int x, int y) {
    	drawBytes(bytes, offset, length, x, y);
	return font.bytesWidth(bytes, offset, length);
    }
    /** Draws the given line. */
    public synchronized void drawLine(int x1, int y1, int x2, int y2) {
	outColor(foreground);
	outPoint(x1, y1);
	outPoint(x2, y2);
	ps.println(DrawLineName);
    }

    private int RGBtoGray(int r, int g, int b) {
	// Taken from the RGB to YIQ mapping (Foley & Van Dam, 2nd Ed.)
	float y = .299 * r + .587 * g + .114 * b;
	return (int) y;
    }

    /** Draws an image at x,y. */
    public synchronized void drawImage(Image I, int X, int Y) {
	DIBitmap dib = I.getDIBitmap();
	ps.println("%"+dib);

	ps.println(dib.num_colors + " " + SetColormapName);
	char outbuf[] = new char[72];
	int linepos = 0;
	for (int i = 0; i < dib.num_colors; i++) {
	    outbuf[linepos++] = hexString.charAt((dib.red[i] >>> 4) & 0xf);
	    outbuf[linepos++] = hexString.charAt((dib.red[i]) & 0xf);
	    outbuf[linepos++] = hexString.charAt((dib.green[i] >>> 4) & 0xf);
	    outbuf[linepos++] = hexString.charAt((dib.green[i]) & 0xf);
	    outbuf[linepos++] = hexString.charAt((dib.blue[i] >>> 4) & 0xf);
	    outbuf[linepos++] = hexString.charAt((dib.blue[i]) & 0xf);
	    if (linepos >= 72) {
		ps.println(outbuf);
		linepos = 0;
	    }
	}
	if (linepos > 0)
	    ps.println(new String(outbuf, 0, linepos));
	if (dib.trans_index < dib.num_colors) {
	    int startx = -1;
	    int trans_index = dib.trans_index;
	    int index = 0;

	    for (int j = 0; j < dib.height; j++) {
		for (int i = 0; i < dib.width; i++) {
		    if (dib.raster[index++] == trans_index) {
			if (startx >= 0) {
			    drawSubImage(dib,
					 X + startx, Y + j,
					 startx, j, i, j + 1);
			    startx = -1;
			}
		    } else {
			if (startx < 0)
			    startx = i;
		    }
		}
		if (startx >= 0) {
		    drawSubImage(dib,
				 X + startx, Y + j,
				 startx, j, dib.width, j + 1);
		    startx = -1;
		}
	    }
	} else
	    drawSubImage(dib, X, Y, 0, 0, dib.width, dib.height);
    }

    private void drawSubImage(DIBitmap dib,
			      int X, int Y, int x1, int y1, int x2, int y2) {
	int w = x2 - x1;
	int h = y2 - y1;
	int index = y1 * dib.width + x1;
	int linepos = 0;
	char outbuf[] = new char[72];

	outRect(X, Y, w, -h);
	ps.println(DrawImageName);
	for (int j = y1; j < y2; j++) {
	    for (int i = x1; i < x2; i++) {
		int pixel = dib.raster[index++] & 0xff;
		outbuf[linepos++] = hexString.charAt((pixel >>> 4) & 0xf);
		outbuf[linepos++] = hexString.charAt(pixel & 0xf);
		if (linepos >= 72) {
		    ps.println(outbuf);
		    linepos = 0;
		}
	    }
	    index += dib.width - w;
	}
	if (linepos > 0)
	    ps.println(new String(outbuf, 0, linepos));
    }

    /**
     * Copies an area of the window that this graphics context paints to.
     * @param X the x-coordinate of the source.
     * @param Y the y-coordinate of the source.
     * @param W the width.
     * @param H the height.
     * @param dx the x-coordinate of the destination.
     * @param dy the y-coordinate of the destination.
     */
    public void copyArea(int X, int Y, int W, int H, int dx, int dy) {
	// REMIND: Not allowed
	new Exception().printStackTrace();
    }

    /**
     * Sets the origin of this Graphics context. All subsequent
     * operations are relative to this origin.
     */
    public void setOrigin(int x, int y) {
	originX = x;
	originY = y;
	newAdjust();
    }

    /**
     * Sets the scaling factor for this Graphics context. Currently
     * only used for line and rectangle drawing operations.
     */
    public synchronized void setScaling(float sx, float sy) {
	if (sx == scaleX && sy == scaleY)
	    return;
	outScale(sx / scaleX, sy / scaleY);
	scaleX = sx;
	scaleY = sy;
    }

    public synchronized void startPage() {
	curFont = null;
	curColor = null;
	if (!prologSent)
	    outProlog();
	pageNumber += 1;
	ps.println("\n%%Page:  "+pageNumber+" "+pageNumber);
	ps.println(StartPageName);
    }

    public synchronized void endPage() {
	ps.println(EndPageName);
    }

    private void outProlog() {
	// Note: NewsPrinters don't reorder the pages if we use PS-Adobe-3.0!
	ps.println("%!PS-Adobe-2.0");
	ps.println("%%Title: " + title);
	ps.println("%%Pages: (atend)");
	ps.println("%%EndComments");
	ps.println("%%BeginProlog");
	ps.println("/D {def} def");
	ps.println("/ISOF {");
	ps.println("	dup findfont dup length 1 add dict begin {");
	ps.println("		1 index /FID eq {pop pop} {D} ifelse");
	ps.println("	} forall /Encoding ISOLatin1Encoding D");
	ps.println("	currentdict end definefont");
	ps.println("} D");
	ps.println("/F [");
	ps.println("	/Times-Roman ISOF");
	ps.println("	/Times-Bold ISOF");
	ps.println("	/Times-Italic ISOF");
	ps.println("	/Times-BoldItalic ISOF");
	ps.println("	/Courier ISOF");
	ps.println("	/Courier-Bold ISOF");
	ps.println("	/Courier-Oblique ISOF");
	ps.println("	/Courier-BoldOblique ISOF");
	ps.println("] D");
	ps.println("/R {4 2 roll moveto 1 index 0 rlineto");
	ps.println("	0 exch rlineto neg 0 rlineto closepath} D");
	ps.println("/"+SetColorName
		   +" {3 {255 div 3 1 roll} repeat setrgbcolor} D");
	ps.println("/"+SetFontName +" {");
	ps.println("	F exch get exch scalefont setfont");
	ps.println("	("+testString+") stringwidth pop div");
	ps.println("	currentfont exch scalefont setfont} D");
	ps.println("/"+RectClipName+" {R clip newpath} D");
	ps.println("/"+InitClipName+" {initclip} D");
	ps.println("/"+RectFillName+" {R fill} D");
	ps.println("/"+RectStrokeName+" {R stroke} D");
	ps.println("/NZ {dup 1 lt {pop 1} if} D");
	ps.println("/"+DrawStringName +" {");
	ps.println("	moveto 1 index stringwidth pop NZ sub");
	ps.println("	1 index length 1 sub NZ div 0");
	ps.println("	3 2 roll ashow} D");
	ps.println("/"+DrawLineName+" {moveto lineto stroke} D");
	ps.println("/"+ScaleName+" {scale} D");
	ps.println("/"+StartPageName+" {/P save D} D");
	ps.println("/"+EndPageName+" {showpage P restore} D");
	ps.println("/"+GsaveName+" {gsave} D");
	ps.println("/"+GrestoreName+" {grestore} D");
	ps.println("/"+SetColormapName+" {");
	ps.println("	/cmapr 1 index array D");
	ps.println("	/cmapg 1 index array D");
	ps.println("	/cmapb 1 index array D");
	ps.println("	/cmapgray null D");
	ps.println("	3 string exch 0 exch 1 exch 1 sub {");
	ps.println("		currentfile 2 index readhexstring pop");
	ps.println("		cmapr 2 index 2 index 0 get put");
	ps.println("		cmapg 2 index 2 index 1 get put");
	ps.println("		cmapb 2 index 2 index 2 get put");
	ps.println("	pop pop } for pop");
	ps.println("} D");
	ps.println("/"+DrawImageName+" {");
	ps.println("	GS");
	ps.println("	/imgsave save D");	// NeWSprint leaves files open!
	ps.println("	/imgh exch D");
	ps.println("	/imgw exch D");
	ps.println("	translate imgw imgh scale");
	ps.println("	/imgstr imgw string D");
	ps.println("	/colorimage where {");
	ps.println("		pop");
	ps.println("		/cimgstr imgw 3 mul string D");
	ps.println("		imgw imgh 8 [imgw 0 0 imgh neg 0 0] {");
	ps.println("			currentfile imgstr readhexstring pop");
	ps.println("			0 1 imgw 1 sub {");
	ps.println("				2 copy get");
	ps.println("				cmapr 1 index get");
	ps.println("				cimgstr exch 3 index");
	ps.println("				3 mul exch put");
	ps.println("				cmapg 1 index get");
	ps.println("				cimgstr exch 3 index");
	ps.println("				3 mul 1 add exch put");
	ps.println("				cmapb 1 index get");
	ps.println("				cimgstr exch 3 index");
	ps.println("				3 mul 2 add exch put");
	ps.println("			pop pop } for pop cimgstr");
	ps.println("		} false 3 colorimage");
	ps.println("	} {");
	ps.println("		cmapgray null eq {");
	ps.println("			/cmapgray cmapr length array D");
	ps.println("			0 1 cmapr length 1 sub {");
	ps.println("				cmapgray exch");
	ps.println("				cmapr 1 index get .299 mul");
	ps.println("				cmapg 2 index get .587 mul");
	ps.println("				cmapb 3 index get .114 mul");
	ps.println("				add add cvi put");
	ps.println("			} for");
	ps.println("		} if");
	ps.println("		imgw imgh 8 [imgw 0 0 imgh neg 0 0] {");
	ps.println("			currentfile imgstr readhexstring pop");
	ps.println("			0 1 imgw 1 sub {");
	ps.println("				imgstr exch 2 copy get");
	ps.println("				cmapgray exch get put");
	ps.println("			} for");
	ps.println("		} image");
	ps.println("	} ifelse");
	ps.println("	imgsave restore");
	ps.println("	GR");
	ps.println("} D");
	ps.println(scalefactor+" dup scale");
	ps.println("%%EndProlog");
	prologSent = true;
    }

    public synchronized void close() {
	ps.println("%%Trailer");
	ps.println("%%Pages: "+pageNumber);
	ps.println("%%EOF");
	ps.close();
    }
}
