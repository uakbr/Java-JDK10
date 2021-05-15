/*
 * @(#)QuoteChart.java	1.6 95/03/22 James Gosling and Jim Graham
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies. Please
 * refer to the file "copyright.html" for further important copyright and
 * licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 */

import java.io.InputStream;
import java.io.StreamTokenizer;
import java.lang.Math;
import java.util.Hashtable;
import awt.*;
import browser.*;
import browser.audio.AudioData;
import net.www.html.*;

/**
 * A simple class to read a stock quote data stream.
 *
 * @author 	James Gosling
 * @author	Jim Graham
 * @version 	1.6, 22 Mar 1995
 */
public
class QuoteChart extends Applet implements Runnable {
    float samples[] = new float[100];
    boolean valknown[] = new boolean[100];
    int samplepos = 0;
    boolean started = true;
    public String stockSymbol = "SUNW";

    private Thread kicker = null;
    private InputStream dataStream = null;

    private String dataURL;
    private float ymin = 1e20, ymax = -1e20;
    private float prevymin, prevymax;
    private float high, low, quote, yesterday;
    private boolean fudge = false;
    private int quoteTime = 0;
    private int yesterdayTime = System.currentTime();
    private Color graphbg;
    private Color graphfg;
    private Color quotefg;
    private Color zerofg;
    private Color tickfg;
    private Color legendfg;
    private Color errorfg;
    private int graphBorder = 2;
    private String graphLabel = "Latest quote";
    private static Hashtable colorCache = new Hashtable();
    private Font fractfont;
    private boolean feedError = false;
    private int delay = 60;
    private boolean staticdata = false;

    private String quoteWhole = "Fetching...";
    private String quoteFract = "";
    private String diffWhole = "";
    private String diffFract = "";
    private String maxWhole = "";
    private String maxFract = "";
    private String minWhole = "";
    private String minFract = "";

    private final int IGNORE = 0;
    private final int QUOTE = 1;
    private final int HIGH = 2;
    private final int LOW = 3;
    private final int CLOSE = 4;
    private final int HIST_TIME = 5;
    private final int HIST_VAL = 6;
    private final int ERROR = 7;

    static {
	colorCache.put("red", Color.red);
	colorCache.put("green", Color.green);
	colorCache.put("blue", Color.blue);
	colorCache.put("cyan", Color.cyan);
	colorCache.put("magenta", Color.magenta);
	colorCache.put("yellow", Color.yellow);
	colorCache.put("orange", Color.orange);
	colorCache.put("pink", Color.pink);
	colorCache.put("white", Color.white);
	colorCache.put("lightgray", Color.lightGray);
	colorCache.put("gray", Color.gray);
	colorCache.put("darkgray", Color.darkGray);
	colorCache.put("black", Color.black);
    }

    /**
     * Initialize the applet. Get attributes.
     */
    public void init() {
	String s;

	s = getAttribute("width");
	if (s != null) width = Integer.parseInt(s);
	s = getAttribute("height");
	if (s != null) height = Integer.parseInt(s);
	s = getAttribute("fudge");
	if (s != null) fudge = s.equals("true");
	s = getAttribute("static");
	if (s != null) staticdata = s.equals("true");
	dataURL = getAttribute("data");
	if (dataURL == null)
	    dataURL = "http://benden:8888/stock.dat";
	graphbg = getColorAttribute("graphbg", Color.lightGray);
	graphfg = getColorAttribute("graphfg", Color.black);
	quotefg = getColorAttribute("quotefg", Color.blue);
	zerofg = getColorAttribute("zerofg", Color.red);
	tickfg = getColorAttribute("tickfg", Color.cyan);
	legendfg = getColorAttribute("legendfg", getColor(0, 128, 64));
	errorfg = getColorAttribute("errorfg", Color.red);
	s = getAttribute("graphborder");
	if (s != null) graphBorder = Integer.valueOf(s).intValue();
	s = getAttribute("stock");
	if (s != null) stockSymbol = graphLabel = s;
	s = getAttribute("delay");
	if (s != null) delay = Integer.parseInt(s);
	s = getAttribute("label");
	if (s != null) graphLabel = s;

	fractfont = getFont(font.family, font.style, (font.height*3)/4);
    }

    private Color getColorAttribute(String name, Color defcolor) {
	String s = getAttribute(name);
	if (s == null)
	    return defcolor;
	s = s.toLowerCase();
	defcolor = (Color) colorCache.get(s);
	if (defcolor != null)
	    return defcolor;
	int colorval = java.lang.Integer.parseInt(s, 16);
	int r = (colorval >> 16) & 0xff;
	int g = (colorval >> 8) & 0xff;
	int b = colorval & 0xff;
	defcolor = getColor(r, g, b);
	colorCache.put(s, defcolor);
	return defcolor;
    }

    /**
     * Run the image loop. This methods is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread me = Thread.currentThread();
	try {
	    String sendURL;
	    if (staticdata) {
		sendURL = dataURL + "/" + stockSymbol;
	    } else {
		sendURL = dataURL
		    + "/" + stockSymbol
		    + (fudge ? "?f" : "?")
		    + ((ymin < ymax)
		       ? ("dle,"
			  + delay)
		       : ("dhle,"
			  + delay + ","
			  + ((samples.length + 1) * delay) + "s"));
	    }
	    dataStream = new URL(documentURL, sendURL).openStream();
	    StreamTokenizer st = new StreamTokenizer(dataStream);
	    st.eolIsSignificant = false;
	    int nextval = IGNORE;
	    int histTime = 0;
	    boolean newvals = false;
	    while (kicker == me) {
		if (started && newvals && dataStream.available() < 1) {
		    repaint();
		    newvals = false;
		}
		switch (st.nextToken()) {
		  case st.TT_EOF:
		    System.out.println("EOF "+stockSymbol);
		    close();
		    kicker = null;
		    return;
		  case st.TT_NUMBER:
		    switch (nextval) {
		    case HIGH:
			high = (float) st.nval;
			ymax = Math.max(high, ymax);
			ymin = Math.min(high, ymin);
			break;
		    case LOW:
			low = (float) st.nval;
			ymax = Math.max(low, ymax);
			ymin = Math.min(low, ymin);
			break;
		    case CLOSE:
			yesterday = (float) st.nval;
			yesterdayTime = 0;
			ymax = Math.max(yesterday, ymax);
			ymin = Math.min(yesterday, ymin);
			diffWhole = formatdiff(quote, yesterday);
			diffFract = formatdifffract(quote, yesterday);
			newvals = true;
			break;
		    case HIST_TIME:
			histTime = (int) st.nval;
			nextval = HIST_VAL;
			continue;
		    case HIST_VAL:
			int now = System.currentTime();
			float histval = (float) st.nval;
			if (histTime < 0) {
			    // Fake data files contain relative history.
			    histTime += now;
			}
			if (histTime < yesterdayTime) {
			    yesterdayTime = histTime;
			    yesterday = histval;
			}
			if (histTime > now) {
//			    System.out.println("Ignoring future history for "
//					       + stockSymbol + " from "
//					       + histTime + " > " + now);
			    break;
			}
			int posdiff = (now - histTime) / delay;
			if (posdiff > samples.length) {
//			    System.out.println("Ignoring ancient history for "
//					       + stockSymbol + " from "
//					       + histTime + " << " + now);
			    break;
			}
			int histpos = samplepos - posdiff;
			if (histpos < 0) {
			    histpos += samples.length;
			}
			if (valknown[histpos]) {
//			    System.out.println("Ignoring extra history for "
//					       + stockSymbol + " from "
//					       + histTime + " @ " + now);
			    break;
			}
			ymax = Math.max(histval, ymax);
			ymin = Math.min(histval, ymin);
			samples[histpos] = histval;
			valknown[histpos] = true;
			newvals = true;
			break;
		    case QUOTE:
			quote = (float) st.nval;
			if (yesterday == 0) {
			    yesterday = quote;
			}
			ymax = Math.max(quote, ymax);
			ymin = Math.min(quote, ymin);
			quoteWhole = formatwhole(quote);
			quoteFract = formatfract(quote);
			diffWhole = formatdiff(quote, yesterday);
			diffFract = formatdifffract(quote, yesterday);
			int newsamp = samplepos + 1;
			if (newsamp >= samples.length)
			    newsamp = 0;
			if (valknown[samplepos] && !valknown[newsamp]) {
			    samples[newsamp] = samples[samplepos];
			    valknown[newsamp] = true;
			}
			samples[samplepos] = quote;
			valknown[samplepos] = true;
			newvals = true;
			samplepos = newsamp;
			break;
		    default:
			System.out.println("Ignoring "+st.nval);
			break;
		    }
		    if (prevymax != ymax) {
			maxWhole = formatwhole(ymax);
			maxFract = formatfract(ymax);
			prevymax = ymax;
		    }
		    if (prevymin != ymin) {
			minWhole = formatwhole(ymin);
			minFract = formatfract(ymin);
			prevymin = ymin;
		    }
		    nextval = IGNORE;
		    break;
		  case st.TT_WORD:
		    if (!stockSymbol.equals(st.sval)) {
			System.out.println("Quote for "
					   +st.sval
					   +" instead of "
					   +stockSymbol);
			nextval = IGNORE;
		    } else if (nextval == IGNORE) {
			nextval = QUOTE;
		    } else if (nextval == ERROR) {
			feedError = true;
			nextval = IGNORE;
		    } else {
			feedError = false;
		    }
		    break;
		  default:
		    int prefix = st.ttype;
		    switch (prefix) {
		    case '^':
			nextval = HIGH;
			break;
		    case '_':
			nextval = LOW;
			break;
		    case '<':
			nextval = CLOSE;
			break;
		    case '#':
			nextval = ERROR;
			break;
		    case '@':
			nextval = HIST_TIME;
			break;
		    default:
			System.out.println("Chart Unknown quote token: "+st);
			break;
		    }
		    break;
		}
	    }
	} catch(ThreadDeath td) {
	} catch(IOException e) {
	} catch(Exception e) {
	    e.printStackTrace();
	}
	if (kicker == me) {
	    kicker = null;
	}
	close();
    }

    private String formatwhole(float q) {
	return Float.toString((float) Math.floor(q));
    }

    private String formatfract(float q) {
	int numerator = (int) ((q - Math.floor(q)) * 16);
	int denominator = 16;
	while (numerator != 0 && (numerator & 1) == 0) {
	    numerator >>= 1;
	    denominator >>= 1;
	}
	return (numerator == 0) ? "" : (numerator + "/" + denominator);
    }

    private String formatdiff(float now, float before) {
	float diff = now - before;
	if (diff == 0)
	    return "";
	int wholediff = (int) Math.floor(Math.abs(diff));
	String s = (diff < 0) ? "-" : "+";
	if (wholediff != 0)
	    s = s + wholediff;
	return s;
    }

    private String formatdifffract(float now, float before) {
	float diff = now - before;
	return (diff == 0) ? "" : formatfract(Math.abs(diff));
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	int sl = samples.length;
	int legendWidth =
	    font.stringWidth("9999") + fractfont.stringWidth("9/16");
	int legendLeft = width - legendWidth;
	int graphTop = font.height + graphBorder;
	int graphBottom = height - graphBorder - 1;
	int graphLeft = graphBorder;
	int graphRight = legendLeft - graphBorder - 1;
	int graphWidth = graphRight - graphLeft + 1;
	int graphHeight = graphBottom - graphTop + 1;

	try {
	    int sw;

	    for (int i = 1; i <= graphBorder; i++) {
		g.paint3DRect(graphLeft - i, graphTop - i,
			      graphWidth-1 + i * 2, graphHeight-1 + i * 2,
			      false, false);
	    }
	    g.setForeground(graphbg);
	    g.fillRect(graphLeft, graphTop, graphWidth, graphHeight);

	    g.setForeground(quotefg);
	    g.setFont(font);
	    sw = g.drawStringWidth(graphLabel + " = ", 0, font.ascent);
	    if (ymin > ymax) {
		g.drawString("Fetching...", sw, font.ascent);
	    } else {
		if (feedError) {
		    g.setForeground(errorfg);
		}
		sw += g.drawStringWidth(quoteWhole, sw, font.ascent);
		g.setFont(fractfont);
		sw += g.drawStringWidth(quoteFract, sw, font.ascent);
		sw += 20;
		g.setFont(font);
		sw += g.drawStringWidth(diffWhole, sw, font.ascent);
		g.setFont(fractfont);
		g.drawString(diffFract, sw, font.ascent);
	    }

	    if (ymin <= ymax) {
		float xf;
		float yo;
		float yf;
		int x, y;

		xf = (graphWidth - 1)/(float)sl;
		if (ymin < ymax) {
		    yo = ymax;
		    yf = (float) (graphHeight - 1) / (ymin - ymax);
		} else {
		    yo = ymax + 1;
		    yf = (float) (graphHeight - 1) / -2;
		}

		g.setForeground(legendfg);
		g.setFont(font);
		sw = g.drawStringWidth(maxWhole, legendLeft,
				       graphTop + font.ascent);
		g.setFont(fractfont);
		g.drawString(maxFract, legendLeft + sw,
			     graphTop + font.ascent);
		g.setFont(font);
		sw = g.drawStringWidth(minWhole, legendLeft,
				       graphBottom - font.descent);
		g.setFont(fractfont);
		g.drawString(minFract, legendLeft + sw,
			     graphBottom - font.descent);

		if (ymin < ymax) {
		    float tickScale;
		    tickScale = (float) Math.floor(Math.log(ymax - ymin)
						   / Math.log(2.0)) - 1.0;
		    float tickFreq = (float) Math.pow(2.0, tickScale);
		    g.setForeground(tickfg);
		    for (float ytick = (float) (Math.floor(ymin / tickFreq)
						* tickFreq);
			 ytick <= ymax; ytick += tickFreq) {
			if (ytick < ymin)
			    continue;
			y = (int)((ytick-yo)*yf) + graphTop;
			g.drawLine(graphLeft, y, graphRight, y);
		    }
		}
		y = (int)((yesterday-yo)*yf) + graphTop;
		g.setForeground(zerofg);
		g.drawLine(graphLeft, y, graphRight, y);

		g.setForeground(graphfg);
		int px = graphLeft;
		int py = 0;
		for (int i = 0, pos = samplepos; i < sl; i++) {
		    if (valknown[pos]) {
			y = (int)((samples[pos]-yo)*yf) + graphTop;
		    } else if (i == 0) {
			y = (int)((yesterday-yo)*yf) + graphTop;
		    }
		    if (i == 0) {
			py = y;
		    }
		    x = (int) (i * xf) + graphLeft;
		    g.drawLine(px, py, x, y);
		    px = x;
		    py = y;
		    if (++pos >= sl)
			pos = 0;
		}
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    public void mouseDown(int x, int y) {
	start();
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
	started = true;
	if (kicker == null) {
	    kicker = new Thread(this);
	    kicker.start();
	}
    }

    /**
     * Stop the applet.  Setting kicker to null will tell the thread to exit.
     */
    public void stop() {
	started = false;
	try {
	    kicker.stop();
	} catch (Exception e) {
	}
	kicker = null;
	close();
    }

    void close() {
	if (dataStream != null) {
	    try {
		dataStream.close();
	    } catch (Exception e) {
	    }
	    dataStream = null;
	}
    }
}
