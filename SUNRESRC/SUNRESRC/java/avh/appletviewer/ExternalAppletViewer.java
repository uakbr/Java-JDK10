/*
 * Arthur van Hoff
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.io.File;
import java.io.AccessErrorHandler;
import java.io.DataInputStream;
import java.util.Hashtable;
import java.util.Enumeration;
import net.Firewall;
import awt.*;
import browser.*;
import net.www.html.*;

/**
 * A display item for applets.
 */
class ExternalAppletViewerDisplayItem extends AppletDisplayItem {
    ExternalAppletViewerFrame frame;

    public DisplayItemWindow setFrame(ExternalAppletViewerFrame frame) {
	this.frame = frame;
	return frame.win;
    }

    public ExternalAppletViewerDisplayItem(ExternalAppletViewerFrame frame, URL url, TagRef ref) {
	super(setFrame(frame), url, ref);
    }

    public void resize(int w, int h) {
	super.resize(w, h);
	frame.reshape(0, 0, w + 15, h + 15);
    }
}

/**
 * The frame in which the applet is shown.
 */
class ExternalAppletViewerFrame extends Frame implements Runnable {
    WServer ws;
    AppletDisplayItem item;
    DisplayItemWindow win;
    URL url;
    TagRef atts;
    String key;
    Thread initializer;
    boolean stopped;
    
    public ExternalAppletViewerFrame(WServer ws, String key, URL url, TagRef atts) {
	super(ws, true, false, null, 200, 200, Color.lightGray);
	this.ws = ws;
	this.key = key;
	this.url = url;
	this.atts = atts;
	this.win = new DisplayItemWindow(this, "Center");
	this.win.enablePointerMotionEvents();
	setTitle(atts.getAttribute("class"));
    }

    public void run() {
	item = new ExternalAppletViewerDisplayItem(this, url, atts);
	win.addItem(item);
	item.load();
	item.init();
	initializer = null;
	if (!stopped) {
	    startApplet();
	}
    }

    public void initApplet() {
	initializer = new Thread(this);
	initializer.start();
    }

    public void startApplet() {
	if (initializer == null) {
	    map();
	    item.start();
	} else {
	    stopped = false;
	}
    }

    public void stopApplet() {
	if (initializer == null) {
	    item.stop();
	    unMap();
	} else {
	    stopped = true;
	}
    }

    public void destroyApplet() {
	if (initializer != null) {
	    initializer.stop();
	}
	if (item != null) {
	    item.destroy();
	}
	unMap();
	dispose();
    }

    public void handleQuit() {
	ExternalAppletViewer.destroy(key);
    }
}

/**
 * File and Firewall access handlers.
 */
class ExternalAppletViewerFileAccessHandler extends AccessErrorHandler {
    public int readException(String f) {
	ExternalAppletViewer.error("*", "read access denied: f");
	return DENY_ACCESS;
    }
    public int writeException(String f) {
	ExternalAppletViewer.error("*", "write access denied: f");
	return DENY_ACCESS;
    }
}

/**
 * External applet viewer class.
 */
class ExternalAppletViewer {
    static WServer ws;
    static Hashtable applets = new Hashtable();

    /**
     * Output an error.
     */
    static synchronized void error(String key, String msg) {
	System.out.println("ERROR:" + key + ":" + msg);
    }

    /**
     * Get an applet given its key.
     */
    static ExternalAppletViewerFrame getApplet(String key) {
	ExternalAppletViewerFrame applet = (ExternalAppletViewerFrame)applets.get(key);
	if (applet == null) {
	    throw new Exception("key not found");
	}
	return applet;
    }

    /**
     * Initialize an applet.
     */
    static void init(String key, URL url, TagRef atts) {
	try {
	    ExternalAppletViewerFrame applet = getApplet(key);
	    applets.remove(applet.key);
	    applet.destroyApplet();
	} catch (Exception e) {
	}
	try {
	    ExternalAppletViewerFrame applet = new ExternalAppletViewerFrame(ws, key, url, atts);
	    applets.put(key, applet);
	    applet.initApplet();
	    applet.startApplet();
	} catch (Exception e) {
	    error(key, e.getMessage());
	}
    }

    static int next(String args, int i) {
	for (int len = args.length() ; i < len ; i++) {
	    switch (args.charAt(i)) {
	    case '\\': i++; break;
	    case ' ': return i;
	    }
	}
	return i;
    }

    static void init(String args) {
	int i = next(args, 0);
	String key = args.substring(0, i);
	int j = next(args, i+1);
	String url = args.substring(i+1, j);

	TagRef atts = new TagRef(Tag.lookup("app"), 0, false);
	for (i = j + 1 ; i < args.length() ; i = j + 1) {
	    j = next(args, i);    
	    String arg = args.substring(i, j);
	    i = arg.indexOf('=');
	    if (i < 0) {
		error(key, "invalid attribute value pair: " + arg);
	    } else {
		atts.addAttribute(arg.substring(0, i), arg.substring(i + 1));
	    }
	    
	}

	if (atts.getAttribute("class") == null) {
	    error(key, "class attribute missing");
	} else {
	    init(key, new URL(null, url), atts);
	}
    }

    /**
     * Start an applet, this is done automatically when
     * the applet is initialized.
     */
    static void start(String key) {
	try {
	    getApplet(key).startApplet();
	} catch (Exception e) {
	    error(key, e.getMessage());
	}
    }

    static void startAll(String url) {
	URL doc = new URL(null, url);
	for (Enumeration e = applets.elements() ; e.hasMoreElements() ; ) {
	    ExternalAppletViewerFrame applet = (ExternalAppletViewerFrame)e.nextElement();
	    if (applet.url.equals(doc)) {
		start(applet.key);
	    }
	}
    }

    /**
     * Stop an applet.
     */
    static void stop(String key) {
	try {
	    getApplet(key).stopApplet();
	} catch (Exception e) {
	    error(key, e.getMessage());
	}
    }

    static void stopAll(String url) {
	URL doc = new URL(null, url);
	for (Enumeration e = applets.elements() ; e.hasMoreElements() ; ) {
	    ExternalAppletViewerFrame applet = (ExternalAppletViewerFrame)e.nextElement();
	    if (applet.url.equals(doc)) {
		stop(applet.key);
	    }
	}
    }

    static void stopAll() {
	for (Enumeration e = applets.keys() ; e.hasMoreElements() ; ) {
	    stop((String)e.nextElement());
	}
    }

    /**
     * Destroy an applet.
     */
    static void destroy(String key) {
	try {
	    ExternalAppletViewerFrame applet = getApplet(key);
	    applets.remove(key);
	    applet.destroyApplet();
	} catch (Exception e) {
	    error(key, e.getMessage());
	}
    }

    static void destroyAll(String url) {
	URL doc = new URL(null, url);
	for (Enumeration e = applets.elements() ; e.hasMoreElements() ; ) {
	    ExternalAppletViewerFrame applet = (ExternalAppletViewerFrame)e.nextElement();
	    if (applet.url.equals(doc)) {
		destroy(applet.key);
	    }
	}
    }

    /**
     * List all applets.
     */
    public static void list() {
	for (Enumeration e = applets.keys() ; e.hasMoreElements() ; ) {
	    ExternalAppletViewerFrame applet = getApplet((String)e.nextElement());
	    System.out.println(applet.key + ": " + applet.url.toExternalForm() +
			       " class=" + applet.atts.getAttribute("class"));
	}
    }

    /**
     * Set the proxy.
     */
    public static void proxy(String arg) {
	int i = arg.indexOf(':');
	if (i < 0) {
	    error("*", "invalid proxy: " + arg);
	    return;
	}
	net.www.http.HttpClient.firewallProxyHost = arg.substring(0, i);
	net.www.http.HttpClient.firewallProxyPort = Integer.valueOf(arg.substring(i+1)).intValue();
    }

    /**
     * Main program.
     */
    public static void main(String argv[]) {
	// Setup file access
	try {
	    File.setReadACL("/tmp");
	    File.setWriteACL("*");
	} catch (IOException e) {
	    System.out.println("Couldn't initialize security ACLs.");
	    return;
	}

	// Setup the fire wall
	Firewall.setAccessMode(Firewall.ACCESS_ALL, false, Firewall.DNS_DOMAIN);

	// Setup file access handlers
	ExternalAppletViewerFileAccessHandler handler = new ExternalAppletViewerFileAccessHandler();
	File.setAccessErrorHandler(handler);
	Firewall.setHandler(handler);

	// Setup the window server
	try {
	    ws = new WServer();
	} catch(Exception e) {
	    error("*", "Couldn't open connection to window server");
	    System.exit(1);
	}
	ws.start();

	// Read commands from standard input
	DataInputStream in = new DataInputStream(System.in);
	while (true) {
	    System.out.print("> ");
	    System.out.flush();
	    String ln = in.readLine();
	    if (ln == null) {
	        stopAll();
	        System.exit(0);
	    }

	    if (ln.startsWith("init ")) {
		init(ln.substring(5));
	    } else if (ln.startsWith("start ")) {
		start(ln.substring(6));
	    } else if (ln.startsWith("startall ")) {
		startAll(ln.substring(9));
	    } else if (ln.startsWith("stop ")) {
		stop(ln.substring(5));
	    } else if (ln.startsWith("stopall ")) {
		stopAll(ln.substring(8));
	    } else if (ln.startsWith("destroy ")) {
		destroy(ln.substring(8));
	    } else if (ln.startsWith("destroyall ")) {
		destroyAll(ln.substring(11));
	    } else if (ln.startsWith("reload ")) {
		destroyAll(ln.substring(7));
		URL.flushClassLoader();
	    } else if (ln.startsWith("proxy ")) {
		proxy(ln.substring(6));
	    } else if (ln.equals("flush")) {
		URL.flushClassLoader();
		ImageCache.flush();
		browser.audio.AudioData.flushCache();
	    } else if (ln.equals("list")) {
		list();
	    } else if (ln.equals("quit")) {
		stopAll();
		System.exit(0);
	    } else if (ln.length() > 0) {
		if (!ln.equals("help")) {
		    error("*", "unknown command: " + ln);
		}
		System.out.println("-- command list --");
		System.out.println("proxy <hostname>:<port>");
		System.out.println("init <key> <document-url> class=<class> <att1>=<val1> ...");
		System.out.println("start <key>");
		System.out.println("stop <key>");
		System.out.println("destroy <key>");
		System.out.println("startall <document-url>");
		System.out.println("stopall <document-url>");
		System.out.println("destroyall <document-url>");
		System.out.println("reload <document-url>");
		System.out.println("flush");
		System.out.println("help");
		System.out.println("quit");
	    }
	}

    }
}
