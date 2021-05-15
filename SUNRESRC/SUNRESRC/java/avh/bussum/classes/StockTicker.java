/*
 * @(#)StockTicker.java	1.5 95/03/22 Jim Graham
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

import browser.Applet;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.Vector;
import java.util.StringTokenizer;
import net.www.html.URL;
import awt.Graphics;
import awt.Font;
import awt.Color;

/**
 * A simple class to read a stock quote data stream and display the most
 * recent quotes in a streaming stock ticker.
 *
 * @author	Jim Graham
 * @version 	1.5, 22 Mar 1995
 */
public
class StockTicker extends Applet implements Runnable {
    String dataURL = "http://benden:8888/stock.dat";
    Vector Symbols;
    int numSymbols;
    int delay = 10;
    int fetchdelay = 60;
    int deltat = 50;
    int scrolldelta;
    int scrollPos;

    QuoteQueue qq = new QuoteQueue();
    float quote[];
    float prevquote[];
    boolean feedError[];
    boolean quoteChange[];
    boolean quoteKnown[];
    boolean prevKnown[];

    Thread kicker;
    TickerPaintDaemon daemon;
    InputStream dataStream = null;

    Font symbolFont;
    Font fractFont;
    Font quoteFont;

    static Color errorColor = Color.red;
    static Color freshColor = Color.green;
    static Color staleColor = Color.white;
    static Color bgColor = Color.darkGray;

    private final int IGNORE = 0;
    private final int QUOTE = 1;
    private final int HIGH = 2;
    private final int LOW = 3;
    private final int CLOSE = 4;
    private final int ERROR = 5;

    /**
     * Initialize the Applet.  Get values from attributes.
     * @see browser.Applet
     */
    public void init() {
	String s;
	boolean fudge = false;
	boolean staticdata = false;

	s = getAttribute("data");
	if (s != null) dataURL = s;

	s = getAttribute("static");
	if (s != null) staticdata = s.equals("true");

	s = getAttribute("stocks");
	if (s == null)
	    s = "SUNW|HWP|SGI|MSFT|INTC|IBM|DEC|CY|ADBE|AAPL|SPX|ZRA";
	Symbols = new Vector(10);
	StringTokenizer st = new StringTokenizer(s, "|");
	String testString = "";
	while (st.hasMoreTokens()) {
	    String stockname = st.nextToken();
	    Symbols.addElement(stockname);
	    if (!staticdata) {
		dataURL = dataURL + "/" + stockname;
	    }
	    testString = testString + stockname + "99 3/4 +1/2 ";
	}
	numSymbols = Symbols.size();
	quote = new float[numSymbols];
	prevquote = new float[numSymbols];
	feedError = new boolean[numSymbols];
	quoteChange = new boolean[numSymbols];
	quoteKnown = new boolean[numSymbols];
	prevKnown = new boolean[numSymbols];

	s = getAttribute("fudge");
	if (s != null) fudge = s.equals("true");

	s = getAttribute("delay");
	if (s != null) fetchdelay = Integer.parseInt(s);

	if (!staticdata) {
	    dataURL = dataURL + (fudge ? "?fdel," : "?del,") + fetchdelay;
	}

	s = getAttribute("scrollt");
	if (s != null) delay = Integer.parseInt(s);

	s = getAttribute("deltat");
	if (s != null) deltat = Integer.parseInt(s);

	symbolFont = getFont("Helvetica", Font.BOLD, 18);
	quoteFont = getFont("Helvetica", 16);
	fractFont = getFont("Helvetica", 12);

	scrolldelta = font.stringWidth(testString) * deltat / (delay * 1000);
	scrollPos = width;
    }

    /**
     * Run the quote data fetcher.
     * @see java.lang.Thread
     */
    public void run() {
	Thread me = Thread.currentThread();
	try {
	    dataStream = new URL(documentURL, dataURL).openStream();
	    StreamTokenizer st = new StreamTokenizer(dataStream);
	    st.eolIsSignificant = false;
	    int nextval = IGNORE;
	    int nextindex = 0;
	    while (kicker == me) {
		switch (st.nextToken()) {
		  case st.TT_EOF:
		    System.out.println("EOF "+Symbols);
		    close();
		    kicker = null;
		    return;
		  case st.TT_NUMBER:
		    switch (nextval) {
		    case HIGH:
		    case LOW:
			break;
		    case CLOSE:
			prevquote[nextindex] = (float) st.nval;
			prevKnown[nextindex] = true;
			break;
		    case QUOTE:
			if (quote[nextindex] != (float) st.nval) {
			    quote[nextindex] = (float) st.nval;
			    quoteChange[nextindex] = true;
			}
			quoteKnown[nextindex] = true;
			break;
		    default:
			System.out.println("Ignoring "+st.nval);
			break;
		    }
		    nextval = IGNORE;
		    break;
		  case st.TT_WORD:
		    nextindex = Symbols.indexOf(st.sval);
		    if (nextindex < 0) {
			System.out.println("Unrequested quote received for "
					   +st.sval);
			nextval = IGNORE;
		    } else if (nextval == IGNORE) {
			nextval = QUOTE;
			feedError[nextindex] = false;
		    } else if (nextval == ERROR) {
			feedError[nextindex] = true;
			if (!prevKnown[nextindex]) {
			    prevquote[nextindex] = quote[nextindex];
			}
			nextval = IGNORE;
		    } else {
			feedError[nextindex] = false;
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
		    default:
			System.out.println("Ticker Unknown quote token: "+st);
			nextval = IGNORE;
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

    int nextQuote = 0;

    public void scroll() {
	if (qq.getHead() == null) {
	    scrollPos = width;
	} else {
	    scrollPos -= scrolldelta;
	}
	repaint();
    }

    private int lastScrollPos;

    /**
     * Update the current frame.
     */
    public synchronized void update(Graphics g, int startpos) {
	int pos = scrollPos;
	QuoteSnapshot qs = qq.getHead();
	int fallen = 0;
	while (qs != null) {
	    if (pos + qs.width > startpos) {
		pos = qs.paint(g, pos);
	    } else {
		pos += qs.width;
	    }
	    if (pos <= 0) {
		scrollPos = pos;
		fallen++;
	    }
	    qs = qs.next;
	}
	while (fallen-- > 0) {
	    qq.dequeue();
	}
	int numskipped = 0;
	while (pos <= width) {
	    Color color;
	    if (!feedError[nextQuote]
		&& (!quoteKnown[nextQuote] || !prevKnown[nextQuote]))
	    {
		if (++nextQuote == numSymbols) {
		    nextQuote = 0;
		}
		if (++numskipped == numSymbols) {
		    break;
		}
		continue;
	    }
	    numskipped = 0;
	    if (feedError[nextQuote]) {
		color = errorColor;
	    } else {
		if (quoteChange[nextQuote]) {
		    color = freshColor;
		    quoteChange[nextQuote] = false;
		} else {
		    color = staleColor;
		}
	    }
	    qs = qq.enqueue(this,
			    (String) Symbols.elementAt(nextQuote),
			    quote[nextQuote],
			    prevquote[nextQuote],
			    color);
	    if (++nextQuote == numSymbols) {
		nextQuote = 0;
	    }
	    pos = qs.paint(g, pos);
	}
	lastScrollPos = scrollPos;
    }

    /**
     * Update the current frame.
     */
    public synchronized void update(Graphics g) {
	if (qq.getHead() == null) {
	    scrollPos = width;
	    paint(g);
	} else {
	    int delta = lastScrollPos - scrollPos;
	    g.copyArea(delta, 0, width - delta, height, 0, 0);
	    g.clipRect(width - delta, 0, delta, height);
	    update(g, width - delta);
	}
    }

    /**
     * Update the current frame.
     */
    public synchronized void paint(Graphics g) {
	g.setForeground(bgColor);
	g.fillRect(0, 0, width, height);
	update(g, 0);
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
	if (kicker == null) {
	    kicker = new Thread(this);
	    kicker.start();
	}
	if (daemon == null) {
	    daemon = new TickerPaintDaemon(this);
	    daemon.start();
	}
    }

    /**
     * Stop the applet.  Setting kicker to null will tell the thread to exit.
     */
    public void stop() {
	try {
	    kicker.stop();
	} catch (Exception e) {
	}
	kicker = null;
	try {
	    daemon.stop();
	} catch (Exception e) {
	}
	daemon = null;
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

class TickerPaintDaemon extends Thread {
    StockTicker ticker;

    public TickerPaintDaemon(StockTicker ticker) {
	this.ticker = ticker;
    }

    public void run() {
	while (ticker.daemon == this) {
	    ticker.scroll();
	    sleep(ticker.deltat);
	}
    }
}

class QuoteSnapshot {
    StockTicker ticker;
    String symbol;
    float quote;
    float prev;
    Color color;
    int width;
    QuoteSnapshot next;

    String QuoteWholeStr;
    String QuoteFractStr;
    String DiffWholeStr;
    String DiffFractStr;

    static int margin = 15;

    public QuoteSnapshot(StockTicker t, String s, float q, float p, Color c) {
	set(t, s, q, p, c);
    }

    public void set(StockTicker t, String s, float q, float p, Color c) {
	ticker = t;
	symbol = s;
	quote = q;
	prev = p;
	color = c;
	QuoteWholeStr = formatwhole(quote);
	QuoteFractStr = formatfract(quote);
	DiffWholeStr = formatdiff(quote, prev);
	DiffFractStr = formatdifffract(quote, prev);
	width = margin +
	    ticker.symbolFont.stringWidth(symbol) +
	    ticker.quoteFont.stringWidth(QuoteWholeStr) +
	    ticker.fractFont.stringWidth(QuoteFractStr) +
	    ticker.quoteFont.stringWidth(DiffWholeStr) +
	    ticker.fractFont.stringWidth(DiffFractStr);
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

    public int paint(Graphics g, int pos) {
	int baseline = ticker.symbolFont.ascent;
	g.setForeground(ticker.bgColor);
	g.fillRect(pos, 0, width, ticker.symbolFont.actualHeight);
	g.setForeground(color);
	g.setFont(ticker.symbolFont);
	pos += g.drawStringWidth(symbol, pos, baseline);
	g.setFont(ticker.quoteFont);
	pos += g.drawStringWidth(QuoteWholeStr, pos, baseline);
	g.setFont(ticker.fractFont);
	pos += g.drawStringWidth(QuoteFractStr, pos, baseline);
	g.setFont(ticker.quoteFont);
	pos += g.drawStringWidth(DiffWholeStr, pos, baseline);
	g.setFont(ticker.fractFont);
	pos += g.drawStringWidth(DiffFractStr, pos, baseline);
	pos += margin;
	return pos;
    }
}

class QuoteQueue {
    QuoteSnapshot freeList;
    QuoteSnapshot queueHead;
    QuoteSnapshot queueTail;

    public QuoteQueue() {
    }

    public QuoteSnapshot enqueue(StockTicker t, String s,
				 float q, float p, Color c) {
	QuoteSnapshot qs;
	if (freeList == null) {
	    qs = new QuoteSnapshot(t, s, q, p, c);
	} else {
	    qs = freeList;
	    freeList = qs.next;
	    qs.set(t, s, q, p, c);
	}
	qs.next = null;
	if (queueTail == null) {
	    queueHead = qs;
	} else {
	    queueTail.next = qs;
	}
	queueTail = qs;
	return qs;
    }

    public void dequeue() {
	QuoteSnapshot qs = queueHead;
	if (queueTail == qs) {
	    queueHead = queueTail = null;
	} else {
	    queueHead = qs.next;
	}
	qs.next = freeList;
	freeList = qs;
    }

    public QuoteSnapshot getHead() {
	return queueHead;
    }
}
