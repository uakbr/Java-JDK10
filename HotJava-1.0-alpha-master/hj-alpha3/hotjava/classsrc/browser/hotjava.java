/*
 * @(#)hotjava.java	1.181 95/05/15 Jonathan Payne and Patrick Naughton
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

package browser;

import awt.*;
import net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import net.www.html.URL;
import net.www.html.MeteredStream;
import net.www.html.ProgressReport;
import net.www.http.HttpClient;
import net.ftp.FtpClient;
import browser.tools.JavaSearch.SearchWindow;

/**
 * Class hotjava is really an awt.Frame, with code to create and
 * manage all the UI components of hotjava.
 * @version 1.181, 15 May 1995
 * @author Jonathan Payne
 * @author Sami Shaio
 * @author Arthur van Hoff
 */
public class hotjava extends Frame {
    static private boolean	securityConfigured = false;
    static WRAccessHandler	handler = null;
    static WServer	server = null;
    static Font		labelFont;
    static Font		inputFont;
    static Font		dialogFont;
    static Color	blue = null;
    static SecurityDialog	sDialog = null;
    static ProgressDialog	pDialog = null;
    public static Color	anchorColor = null;
    public static Color	visitedAnchorColor = null;
    public static Properties	props;
    static int		copiesRunning;
    public static String home;
    public static String dochome;
    public static String version = "1.0 alpha3";
    public static String programName = "HotJava";
    public static String untitledTitle = "Untitled";
    public static URLHistory	history;

    public static int windowWidth  = 641;
    public static int windowHeight = 729;
    
    StatusWindow	statusWindow;
    ToolBar		toolBar;
    InfoWindow		infoWindow;
    SearchWindow        searchWindow;
    HistoryWindow	histWindow;
    StringDialog        findDialog;

    MenuBar		mbar;
    public FileMenu	fileMenu;
    GoToMenu		gotoMenu;
    protected WRWindow	doc;
    HotList		hlist;
    String		urlSpec;
    String              pageTitle;
    Image		icon;

    static {
	home = System.getenv("WWW_HOME");
	if (home == null) {
	    home = "doc:index.html";
	}

	dochome = "doc:/doc/";

	initHistory();
    }

    static void initHistory() {
	history = new URLHistory();

	if (false) {
	    FileInputStream	is = null;
	    String	home = System.getenv("HOME");
	    String	hotjavaName;

	    hotjavaName = home + File.separator + ".hotjava"
			+ File.separator + ".hotjava-global-history";
	    try {
		is = new FileInputStream(hotjavaName);
		history.parseStream(is);
	    } catch (IOException e) {
	    } catch (Exception e) {
		System.err.println("Error " + e + " while reading your global history file!");
		System.err.println("Therefore, your global history will not be updated in this session.");
		return;
	    }
	    history.openOutputStream(hotjavaName);
	}
    }

    public static GifImage fetchGIF(String fromURL) {
	try {
	    URL imgUrl = new URL(null, fromURL);
	    InputStream iStream = imgUrl.openStream();
	    return new GifImage(iStream, null);
	} catch (Exception e) {
	    return null;
	}
    }

    public void go(String u) {
	doc.pushURL(new URL(null, u));
    }

    public void goHome() {
	doc.pushURL(new URL(null, home));
    }

    public void setTitle(String t) {
	if (t == null || t.equals("")) {
	    super.setTitle(programName + " " + version);
	    pageTitle = "";
	} else {
	    super.setTitle(programName+": "+t);
	    pageTitle = t;
	}
    }

    public void setURL(String u) {
	infoWindow.urlField.setText(u);
    }

    /**
     * Call this to say that this document has HTML source.  Can be used
     * to turn on/off various commands, etc.
     */
    public void pageHasSource(boolean source) {
	if (source) {
	    fileMenu.viewSourceItem.enable();
	} else {
	    fileMenu.viewSourceItem.disable();
	}
    }	

    public void handleQuit() {
	if (--hotjava.copiesRunning > 0) {
	    // XXX: should actually be destroying the window and
	    // recovering resources...
	    unMap();
	} else {
	    System.exit(0);
	}
    }

    public void setMessage(String msg) {
        if (statusWindow.message != null) {
	    statusWindow.message.setText(msg);
        } else {
	    setStatusMessage(msg);
        }
    }


    public void readProperties() {
	props = new Properties(System.getenv("HOME") + File.separator + ".hotjava" + File.separator + "properties");

	WRWindow.delayImageLoading = "true".equals(props.get("delayImageLoading"));
	WRWindow.delayAppletLoading = "true".equals(props.get("delayAppletLoading"));
	WRTextItem.underlineAnchors = ! "plain".equals(props.get("anchorStyle"));

	if (!version.equals(props.get("version"))) {
	    urlSpec = "doc:copyright.html";
	    props.put("version", version);
	    props.save();
	}

	Object pval;

	if ((pval = props.get("firewallSet")) != null) {
	    HttpClient.useProxyForFirewall = "true".equals(pval);
	}
	pval = props.get("firewallHost");

	if (pval != null) {
	    HttpClient.firewallProxyHost = (String)pval;
	}
	pval = props.get("firewallPort");

	if (pval != null) {
	    try {
		HttpClient.firewallProxyPort = Integer.parseInt((String)pval);
	    } catch (NumberFormatException e) {
		System.out.println("Error parsing firewallProxyPort: "
				   + (String)pval);
		HttpClient.useProxyForFirewall = false;
	    }
	}

	if ((pval = props.get("proxySet")) != null) {
	    HttpClient.useProxyForCaching = "true".equals(pval);
	}
	if ((pval = props.get("proxyHost")) != null) {
	    HttpClient.cachingProxyHost = (String)pval;
	}
	if ((pval = props.get("proxyPort")) != null) {
	    try {
		HttpClient.cachingProxyPort = Integer.parseInt((String)pval);
	    } catch (NumberFormatException e) {
		System.out.println("Error parsing cachingProxyPort: "
				   + (String)pval);
		HttpClient.useProxyForCaching = false;
	    }
	}

	if ((pval = props.get("useFtpProxy")) != null) {
	    FtpClient.useFtpProxy = "true".equals(pval);
	}
	if ((pval = props.get("ftpProxyHost")) != null) {
	    FtpClient.ftpProxyHost = (String)pval;
	}
	if ((pval = props.get("ftpProxyPort")) != null) {
	    try {
		FtpClient.ftpProxyPort = Integer.parseInt((String)pval);
	    } catch (NumberFormatException e) {
		System.out.println("Error parsing ftpProxyPort: "
				   + (String)pval);
		FtpClient.useFtpProxy = false;
	    }
	}
    }

    public hotjava(WServer serv, String args[]) {
	super(serv, true, null, windowWidth, windowHeight, Color.lightGray);
	setInsets(4,0,4,0);
	copiesRunning++;
	super.setTitle(programName);
	urlSpec = args.length > 0 ? args[0] : home;


	// Read properties and set defaults
	readProperties();
  
	if (blue == null) {
	    blue = Color.black;
	    anchorColor = new Color(serv, 0, 0, 192);
	    visitedAnchorColor = new Color(serv, 96, 32, 128);
	    labelFont = serv.fonts.getFont("Helvetica", Font.BOLD, 14);
	    inputFont = serv.fonts.getFont("DialogInput", Font.BOLD, 12);
	}

	dialogFont = serv.fonts.getFont("Dialog", Font.BOLD, 12);
	setDefaultFont(dialogFont);
	doc = new WRWindow(this, "Center");
	infoWindow = new InfoWindow(this);
	doc.setScrollbar(new Scrollbar(doc, "sbar", Scrollbar.VERTICAL, true));
	statusWindow = new StatusWindow(this);
	toolBar = statusWindow.getToolBar();

	mbar = new MenuBar(this);
	fileMenu = new FileMenu(mbar, this);
	new OptionsMenu(mbar, this);
	new NavigateMenu(mbar, this);
	gotoMenu = new GoToMenu(mbar, this);
	new HelpMenu(mbar, this);

	hlist = new HotList(serv, this);
	histWindow = new HistoryWindow(serv, this, doc.docHistory);
	findDialog = new StringDialog(this, hotjava.dialogFont,
				      "Find", "Find what:",
				      false, false, "Find", "Cancel",
				      "Help", "", null);

	// Set the HotJava icon
	// XXX: Note that this needs to be done before the frame is
	// mapped for it to take effect.
	//
	serv.sync();

	try {
	    GifImage gif = hotjava.fetchGIF("doc:demo/images/wricon.gif");
	    icon = doc.createImage(gif);
	    setIconImage(icon);
	} catch (Exception e) {
	    System.out.println("Couldn't set icon image");
	}

	if (sDialog == null) {
	    sDialog = new SecurityDialog(this);
	}
	if (pDialog == null) {
	    pDialog = new ProgressDialog(this);
	}

	map();

	if (! securityConfigured) {
	    sDialog.map();
	    sDialog.resize();
	}

	URL current_url = null;
	try {
	    current_url = new URL(current_url, urlSpec);
	    doc.pushURL(current_url);
	} catch(Exception e) {
	    System.out.println("Ignoring error " + e);
	}
    }

    public static void main(String args[]) {
	try {
	    File.setReadACL(System.getenv("HOTJAVA_READ_PATH"));
	    File.setWriteACL(System.getenv("HOTJAVA_WRITE_PATH"));
	} catch (IOException e) {
	    System.out.println("Couldn't initialize security ACLs.");
	    return;
	}
	Firewall.readFirewallHosts();
	if (!Firewall.readAccessMode()) {
	    securityConfigured = false;
	} else {
	    securityConfigured = true;
	}
	try {
	    server = new WServer();
	} catch(Exception e) {
	    System.out.println("Couldn't open connection to window server");
	    return;
	}
	server.start();
	hotjava wr = new hotjava(server, args);
	handler = new WRAccessHandler(wr);
	File.setAccessErrorHandler(handler);
	Firewall.setHandler(new WRSecurityHandler(wr));
    }

}

class WRAccessHandler extends AccessErrorHandler {
    MessageDialog	diag;

    WRAccessHandler(hotjava w) {
	diag = new MessageDialog(w,
				 "Access Exception",
				 "",
				 MessageDialog.ERROR_TYPE,
				 2,
				 true,
				 "Deny Access",
				 "Allow This Access",
				 null,
				 null);
    }

    public int readException(String path) {
	diag.setMessage("An applet has attempted to read " + path);
	switch (diag.show()) {
	  case 2:
	    return AccessErrorHandler.ALLOW_THIS_ACCESS;
	  case 1:
	  default:
	    return AccessErrorHandler.DENY_ACCESS;
	}
    }

    public int writeException(String path) {
	diag.setMessage("An applet has attempted to write " + path);
	switch (diag.show()) {
	  case 2:
	    return AccessErrorHandler.ALLOW_THIS_ACCESS;
	  case 1:
	  default:
	    return AccessErrorHandler.DENY_ACCESS;
	}	
    }
}


class WRSecurityHandler extends AccessErrorHandler {
    MessageDialog	diag;

    WRSecurityHandler(hotjava w) {
	diag = new MessageDialog(w,
				 "Security Exception",
				 "",
				 MessageDialog.ERROR_TYPE,
				 1,
				 true,
				 "Ok",
				 null,
				 null,
				 null);
    }

    public int readException(String msg) {
	diag.setMessage(msg);
	diag.show();
	return AccessErrorHandler.DENY_ACCESS;
    }
}


class ClearButton extends Button {
    hotjava target;

    public ClearButton(Container w, hotjava hj) {
	super("Document URL:", null, w);
	target = hj;
    }
    
    public ClearButton(Image i, Image iP, Container w,
		       hotjava hj) {
	super(i, iP, null, w);
	target = hj;
    }
    
    public void selected(Component c, int pos) {
	target.setURL("");
    }
}


class BackButton extends Button {
    hotjava target;

    public BackButton(Container w, hotjava mw) {
	super("Back", null, w);
	target = mw;
    }
    
    public BackButton(Image i, Image iP, Image dis, Container w,
		      hotjava mw) {
	super(i, iP, dis, null, w);
	target = mw;
    }
    
    public void selected(Component c, int pos) {
	target.doc.backup();
    }
}


class FwdButton extends Button {
    hotjava target;

    public FwdButton(Container w, hotjava mw) {
	super("Forward", null, w);
	target = mw;
    }

    public FwdButton(Image i, Image iP, Image dis, Container w,
		     hotjava mw) {
	super(i, iP, dis, null, w);
	target = mw;
    }
    
    public void selected(Component c, int pos) {
	target.doc.forward();
    }
}

class HomeButton extends Button {
    hotjava target;

    public HomeButton(Container w, hotjava mw) {
	super("Home", null, w);
	target = mw;
    }
    
    public HomeButton(Image i, Image iP, Container w,
		      hotjava mw) {
	super(i, iP, null, w);
	target = mw;
    }
    
    public void selected(Component c, int pos) {
	target.goHome();
    }
}


class ReloadButton extends Button {
    hotjava target;

    public ReloadButton(Container w, hotjava mw) {
	super("Reload", null, w);
	target = mw;
    }
    
    public ReloadButton(Image i, Image iP, Container w,
			hotjava mw) {
	super(i, iP, null, w);
	target = mw;
    }
    
    public void selected(Component c, int pos) {
	target.doc.reload();
    }
}


class StopButton extends Button {
    hotjava target;

    public StopButton(Container w, hotjava mw) {
	super("Stop", null, w);
	target = mw;
    }
    
    public StopButton(Image i, Image iP, Image dis, Container w,
		      hotjava mw) {
	super(i, iP, dis, null, w);
	target = mw;
    }
    
    public void selected(Component c, int pos) {
	target.doc.stopFetch();
    }
}



/*
 * This class displays control buttons and status messages for
 * hotjava.  It is an observer of the MeteredStream class, which
 * notifies it of progress while reading HTTP streams.
 */
class StatusWindow extends Window implements Runnable {
    Color	fg;
    Label	message;
    hotjava     hj;
    ToolBar     toolBar;

    final int sepBarHeight = 2;

    public StatusWindow(hotjava mw) {
	super(mw, "South", mw.background, 500, 30);
	setLayout(new ColumnLayout(true));

        hj = mw;
	toolBar = new ToolBar(this, this, hj);
	new Space(this, null, 0, sepBarHeight + 2, true, false);

	fg = Color.black;

	InputStream iStream;
	URL	    imgUrl;

	if (mw.hasStatusBar()) {
            mw.showStatusBar(true);
        } else {
	    message=new Label("", "status", this,hotjava.labelFont);
	    message.setColor(hotjava.blue);
	    message.setHFill(true);
        }

    	// Finally, fork a thread to execute the run() method defined below.
 	new Thread(this, "Progress Reporter").start();
    }

    public ToolBar getToolBar() {
	return toolBar;
    }

    public void paint() {
	paint3DRect(0, 0, width, sepBarHeight, false, true);
    }

    public void setMessage(String msg) {
        if (message != null) {
	    message.setText(msg);
        } else {
            hj.setStatusMessage(msg);
        }
    }

    private String plural(int n) {
	return (n == 1) ? "" : "s";
    }

    /*
     * Notify any observers that the amount expected or the amount
     * read has changed.  The notification only happens if the total
     * number of bytes (read and expected) has changed by more than
     * 3% of the last amount reported.  Make sure that this method
     * is only called from synchronized static methods.
     */
    public void run() {
	ProgressReport pr = new ProgressReport();
	int lastReport = 0;
	int connections = 0;

	Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 1);
	while (true) {
	    Thread.sleep(500);
	    MeteredStream.checkProgress(pr);
	    int total = pr.recvd + pr.expected;

	    if (connections != pr.connections || total != lastReport) {
		int diff = pr.expected - pr.recvd;
		int percent = (int) (((float)pr.recvd/pr.expected) * 100);
			
		if (pr.connections == 0 && diff == 0) {
                    String msg = hj.statusMessage;
                    if (message != null) 
                        msg = message.label;
		    if (msg.endsWith("% completed)]"))
			setMessage("");
		} else {
		    setMessage("[" + pr.connections + " connection"
		    + plural(pr.connections) + ": " + diff
			       + " bytes remaining (" + percent + "% completed)]");
		}
		lastReport = total;
		connections = pr.connections;
	    }
	}
    }
}

/**
 * This class displays control buttons for hotjava.
 */

class ToolBar extends Row {

    Button backButton;
    Button fwdButton;
    Button stopButton;
    
    public ToolBar(Container cont, Window win, hotjava hj) {
	super(cont, null, true);

	try {
	    final String base = "doc:demo/images/";
	    
	    GifImage bbm = hotjava.fetchGIF(base + "back.gif");
	    GifImage bbPm = hotjava.fetchGIF(base + "backP.gif");
	    GifImage bdbm = hotjava.fetchGIF(base + "backD.gif");
	    GifImage fbm = hotjava.fetchGIF(base + "fwd.gif");
	    GifImage fbPm = hotjava.fetchGIF(base + "fwdP.gif");
	    GifImage fdbm = hotjava.fetchGIF(base + "fwdD.gif");
	    GifImage hbm = hotjava.fetchGIF(base + "home.gif");
	    GifImage hbPm = hotjava.fetchGIF(base + "homeP.gif");
	    GifImage rbm = hotjava.fetchGIF(base + "reload.gif");
	    GifImage rbPm = hotjava.fetchGIF(base + "reloadP.gif");
	    GifImage stopbm = hotjava.fetchGIF(base + "stop.gif");
	    GifImage stopPbm = hotjava.fetchGIF(base + "stopP.gif");
	    GifImage stopDbm = hotjava.fetchGIF(base + "stopD.gif");
	    
	    if (bbm==null) {
		backButton = new BackButton(this, hj);
	    } else {
		Image bimg = win.createImage(bbm);
		Image bPimg = win.createImage(bbPm);
		Image bDisImg = win.createImage(bdbm);
		backButton = new BackButton(bimg, bPimg, bDisImg, this, hj);
	    }
	    
	    if (fbm==null) {
		fwdButton = new FwdButton(this, hj);
	    } else {
		Image fimg = win.createImage(fbm);
		Image fPimg = win.createImage(fbPm);
		Image fDisImg = win.createImage(fdbm);
		fwdButton = new FwdButton(fimg, fPimg, fDisImg, this, hj);
	    }
	    
	    if (hbm==null) {
		new HomeButton(this, hj);
	    } else {
		Image himg = win.createImage(hbm);
		Image hPimg = win.createImage(hbPm);
		new HomeButton(himg, hPimg, this, hj);
	    }
	    
	    if (rbm==null) {
		new ReloadButton(this, hj);
	    } else {
		Image rimg = win.createImage(rbm);
		Image rPimg = win.createImage(rbPm);
		new ReloadButton(rimg, rPimg, this, hj);
	    }
	    
	    if (stopbm==null) {
		new StopButton(this, hj);
	    } else {
		Image stopImg = win.createImage(stopbm);
		Image stopPImg = win.createImage(stopPbm);
		Image stopDImg = win.createImage(stopDbm);
		
		stopButton = new StopButton(stopImg, stopPImg, stopDImg,
					    this, hj);
	    }
	} catch (Exception e) {
	    System.err.println("Unable to read one or more button "+
			       "images - using text labels instead");
	    backButton = new BackButton(this, hj);
	    fwdButton = new FwdButton(this, hj);
	    new HomeButton(this, hj);
	    new ReloadButton(this, hj);
	    stopButton = new StopButton(this, hj);
	}

	backButton.disable();
	fwdButton.disable();
	stopButton.disable();
    }

    public void setButtonStates(int stkIndex, Stack docStack) {
	fwdButton.enable();
	backButton.enable();

	if (docStack.size() == 1) {
	    backButton.disable();
	    fwdButton.disable();
	} else if (stkIndex >= docStack.size()) {
	    fwdButton.disable();
	} else if (stkIndex == 1) {
	    backButton.disable();
	}
    }

    public void allowStop(boolean yes) {
	if (yes) {
	    stopButton.enable();
	} else {
	    stopButton.disable();
	}
    }

}


class FileMenu extends Menu {

    OpenDialog	openDialog;
    PrintDialog	printDialog;

    public MenuItem viewSourceItem;
    
    public FileMenu(MenuBar mbar, hotjava w) {
	super("File", mbar);

	mw = w;

	MenuItem	item;

	item = new MenuItem("New", this);
	item.disable();
	item = new MenuItem("Open...", this);
	item = new MenuItem("Reload", this);
	item = new MenuItem("Save", this);
	item.disable();
	item = new MenuItem("Save As...", this);
	item.disable();
	new MenuItem("Print...", this);
	viewSourceItem = new MenuItem("View Source...", this);
	viewSourceItem.disable();
	item = new MenuItem("Close", this);
	item.disable();
	new MenuItem("Quit", this);
	enabled = true;
    }
    
    public void selected(int index) {
	int	height;
	int	width;
	String  args[];

	switch (index) {
	  case 0: // New
	    args = new String[1];
	    args[0] = mw.urlSpec;
	    new hotjava(hotjava.server, args);
	    break;
	  case 1: // Open
	    if (openDialog == null) {
		openDialog = new OpenDialog(hotjava.server, mw);
	    }
	    openDialog.map();
	    break;
	  case 2: // Reload
	    mw.doc.reload();
	    break;
	  case 3: // Save
	    break;
	  case 4: // Save As
	    break;
	  case 5: // Print...
	    if (printDialog == null) {
		printDialog = new PrintDialog(mw, mw.doc.document());
	    }
	    printDialog.map();
	    break;
	  case 6: // View Source...
	    new SourceViewer(mw, mw.doc.document().thisURL);
	    break;
	  case 7: // Close Window
	    hotjava.copiesRunning--;
	    if (hotjava.copiesRunning > 0) {
		// XXX: should actually be destroying the window and
		// recovering resources...
		mw.unMap();
		break;
	    }
	    // else fall into quit case
	  case 8: // Quit
	    System.exit(0);
	    break;
	  default:
	    break;
	}
    }

    hotjava		mw;
    private boolean	enabled;
}

class OptionsMenu extends Menu {
    hotjava 		mw;
    MenuItem		showToggle;
    PropertySheet	propSheet = null;

    public OptionsMenu(MenuBar mbar, hotjava w) {
	super("Options", mbar);

	mw = w;

	MenuItem	item;

	item = new MenuItem("Font", this);
	item.disable();
	item = new MenuItem("Color", this);
	item.disable();
	new MenuItem("Security...", this);
	new MenuItem("Properties...", this);
	new MenuItem("Flush Cache", this);
	new MenuItem("Progress Monitor", this);
    }
    public void selected(int index) {
	boolean	st;

	switch (index) {
	  case 2: // Security...
	    hotjava.sDialog.map();
	    hotjava.sDialog.resize();
	    break;
	  case 3: // Properties...
	    if (propSheet == null) {
		propSheet = new PropertySheet(mw);
	    }
	    propSheet.show();
	    break;
	  case 4: // Flush Cache
	    ImageCache.flush();
	    browser.audio.AudioData.flushCache();
	    break;
	  case 5: // Progress
	    hotjava.pDialog.map();
	    hotjava.pDialog.resize();
	    break;
	  default:
	    break;
	}
    }
}

class HotlistMenuItem extends MenuItem {
    String url;
    hotjava mw;

    public HotlistMenuItem(String title, String url, Menu parent,
			   hotjava mw) {
	super(title, parent);
	this.url = url;
	this.mw = mw;
    }
    public void selected() {
	mw.doc.pushURL(new URL(null, url));
    }
}

class GoToMenu extends Menu {
    hotjava mw;

    public GoToMenu(MenuBar mbar, hotjava w) {
	super("Goto", mbar);
	mw = w;
	new MenuItem("Add Current", this);
	addSeparator();	
    }

    public void selected(int index) {
	switch (index) {
	  case 0:
	    mw.hlist.addItem(mw.pageTitle,
			     mw.infoWindow.urlField.getText(),
			     true,
			     true);
	    break;
	  default:
	    break;
	}
    }

    public HotlistMenuItem addHotItem(String title, String url) {
	if ((title == null) || (title.length() == 0)) {
	    title = hotjava.untitledTitle+" (" + url + ")";
	}
	return new HotlistMenuItem(title, url, this, mw);
    }
}


class NavigateMenu extends Menu {
    hotjava	mw;
    boolean	addedSeparator = false;

    public NavigateMenu(MenuBar mbar, hotjava w) {
	super("Navigate", mbar);

	mw = w;
	MenuItem	item;

	new MenuItem("Forward", this);
	new MenuItem("Back", this);
	new MenuItem("Home", this);
	addSeparator();
	new MenuItem("Show History...", this);
	addSeparator();
	new MenuItem("Add Current to Hotlist", this);
	new MenuItem("Show Hotlist...", this);
	addSeparator();
	(new MenuItem("Find...", this)).disable();
    }

    public void selected(int index) {
	String urlstring;

	switch (index) {
	  case 0:
	    // Forward
	    mw.doc.forward();
	    break;
	  case 1:
	    // Back
	    mw.doc.backup();
	    break;
	  case 2:
	    // Home
	    mw.goHome();
	    break;
	  case 3:
	    // History
	    mw.histWindow.showWindow();
	    break;
	  case 4:
	    // Add Current to HotList
	    mw.hlist.addItem(mw.pageTitle,
			     mw.infoWindow.urlField.getText(),
			     false,
			     false);
	    break;
	  case 5:
	    // Show HotList
	    mw.hlist.map();
	    mw.hlist.resize();
	    mw.hlist.wServer.sync();
	    break;
	  case 6:
	    mw.findDialog.setHandler(new FindHandler(mw));
	    mw.findDialog.show();
	  default:
	    break;
	}
    }
}

class HotlistGotoButton extends Button {
    public HotlistGotoButton(Container w, HotList hl) {
	super("Visit","", w);
	hlist = hl;
    }
    public void selected(Component c, int pos) {
	HotListItem	hItem = (HotListItem)hlist.urlList.elementAt(hlist.selectedItem);

	hlist.mw.doc.pushURL(new URL(null, hItem.url));
    }

    HotList	hlist;
}

class DismissButton extends Button {

    public DismissButton(Container w, Frame f) {
	this(w, f, "Cancel");
    }
    
    public DismissButton(Container w, Frame f, String label) {
	super(label, null, w);

	frame = f;
    }
    
    public void selected(Component c, int pos) {
	frame.unMap();
    }

    Frame	frame;
}

class PropertiesDismissButton extends Button {
    hotjava	  wr;
    PropertySheet props;

    public PropertiesDismissButton(Container w,
				   PropertySheet f,
				   hotjava wr) {
	super("Cancel", null, w);
	this.wr = wr;
	this.props = f;
    }
    
    public void selected(Component c, int pos) {
	wr.readProperties();
	props.setAllValues();
	props.unMap();
    }
}

class DeleteButton extends Button {
    public DeleteButton(Container w, HotList hl) {
	super("Delete",null,w);
	hlist = hl;
    }
    public void selected(Component c, int pos) {
	hlist.delete(hlist.selectedItem);
    }

    HotList	hlist;
}

class ApplyButton extends Button {
    public ApplyButton(Container w, PropertySheet f) {
	super("Apply",null, w);

	propSheet = f;
    }

    private void portError(String wouldBeNumber, String portName) {
	MessageDialog error = propSheet.errorDialog;
	
	error.setMessage("'"+wouldBeNumber+"' is not a valid "+portName+
			     " port number.  Try again.");
	error.show();
    }

    private void hostError(String wouldBeHost, String hostName) {
	MessageDialog error = propSheet.errorDialog;
	
	error.setMessage("'"+wouldBeHost+"' is not a valid "+hostName+
			     " host name.  Try again.");
	error.show();
    }
    
    public void selected(Component c, int pos) {
	boolean wasError = false;

	// Try to parse port numbers.  If we fail, pitch an error dialog,
	// and don't store any values.
	String firewallPort = propSheet.fProxyPort.getText();
	try {
	    HttpClient.firewallProxyPort = Integer.parseInt(firewallPort);
	} catch (NumberFormatException e) {
	    portError(firewallPort, "Firewall Proxy");
	    propSheet.fProxyPort.setText(HttpClient.firewallProxyPort+"");
	    wasError = true;
	}

	String proxyPort = propSheet.cProxyPort.getText();
	try {
	    HttpClient.cachingProxyPort = Integer.parseInt(proxyPort);
	} catch (NumberFormatException e) {
	    portError(proxyPort, "Caching Proxy");
	    propSheet.cProxyPort.setText(HttpClient.cachingProxyPort+"");
	    wasError = true;
	}

	String ftpProxyPort = propSheet.ftpProxyPort.getText();
	try {
	    FtpClient.ftpProxyPort = Integer.parseInt(ftpProxyPort);
	} catch (NumberFormatException e) {
	    portError(ftpProxyPort, "FTP Proxy");
	    propSheet.ftpProxyPort.setText(FtpClient.ftpProxyPort+"");
	    wasError = true;
	}

	String firewallProxy = propSheet.fProxyField.getText();
	try {
	    if (propSheet.fProxyToggle.getState()) {
		InetAddress	iaddr = InetAddress.getByName(firewallProxy);
	    }
	    HttpClient.firewallProxyHost = firewallProxy;
	} catch (UnknownHostException e) {
	    hostError(firewallProxy, "Firewall Proxy");
	    propSheet.fProxyField.setText(HttpClient.firewallProxyHost);
	    wasError = true;
	}

	String cachingProxy = propSheet.cProxyField.getText();
	try {
	    if (propSheet.cProxyToggle.getState()) {
		InetAddress	iaddr = InetAddress.getByName(cachingProxy);
	    }
	    HttpClient.cachingProxyHost = cachingProxy;
	} catch (UnknownHostException e) {
	    hostError(cachingProxy, "Caching Proxy");
	    propSheet.cProxyField.setText(HttpClient.cachingProxyHost);
	    wasError = true;
	}

	String ftpProxy = propSheet.ftpProxyField.getText();
	try {
	    if (propSheet.ftpProxyToggle.getState()) {
		InetAddress	iaddr = InetAddress.getByName(ftpProxy);
	    }
	    FtpClient.ftpProxyHost = ftpProxy;
	} catch (UnknownHostException e) {
	    hostError(ftpProxy, "FTP Proxy");
	    propSheet.ftpProxyField.setText(FtpClient.ftpProxyHost);
	    wasError = true;
	}
	

	if (! wasError) {
	    if (WRTextItem.underlineAnchors != propSheet.ulField.getState()) {
		WRTextItem.underlineAnchors = propSheet.ulField.getState();
		propSheet.mw.doc.paint();
	    }
	    propSheet.mw.doc.setDelayImageLoading(propSheet.dilField.getState());
	    propSheet.mw.doc.setDelayAppletLoading(propSheet.dalField.getState());
	    
	    HttpClient.useProxyForFirewall = propSheet.fProxyToggle.getState();
	    HttpClient.useProxyForCaching = propSheet.cProxyToggle.getState();
	    FtpClient.useFtpProxy = propSheet.ftpProxyToggle.getState();

	    propSheet.unMap();
	    
	    // set and save properties
	    propSheet.mw.props.put("anchorStyle",
				   WRTextItem.underlineAnchors ?
				   "underline" : "plain");
	    propSheet.mw.props.put("delayAppletLoading",
				   WRWindow.delayAppletLoading ?
				   "true" : "false");
	    propSheet.mw.props.put("delayImageLoading",
				   WRWindow.delayImageLoading ?
				   "true" : "false");
	    propSheet.mw.props.put("firewallSet",
				   HttpClient.useProxyForFirewall ?
				   "true":"false");
	    propSheet.mw.props.put("firewallHost",
				   HttpClient.firewallProxyHost);
	    propSheet.mw.props.put("firewallPort", firewallPort);
	    
	    propSheet.mw.props.put("proxySet",
				   HttpClient.useProxyForCaching ?
				   "true": "false");
	    propSheet.mw.props.put("proxyHost", HttpClient.cachingProxyHost);
	    propSheet.mw.props.put("proxyPort", proxyPort);

	    propSheet.mw.props.put("useFtpProxy",
				   FtpClient.useFtpProxy ?
				   "true": "false");
	    propSheet.mw.props.put("ftpProxyHost", FtpClient.ftpProxyHost);
	    propSheet.mw.props.put("ftpProxyPort", ftpProxyPort);
	    
	    propSheet.mw.props.save();
	}
    }

   PropertySheet	propSheet;
}


class SaveButton extends Button {
    public SaveButton(Container w,  SourceViewer f) {
	super("Save...",null, w);
	sViewer = f;
    }
    public void selected(Component c, int pos) {
	if (fdialog == null) {
	    fdialog = new FileDialog("Save HTML Source...", sViewer);
	}
	String path = fdialog.chooseFile(null);

	if (path != null) {
	    try {
		File fpath = new File(path);
		PrintStream pStr = new PrintStream(new FileOutputStream(fpath));
		pStr.print(sViewer.t.getText());
		pStr.close();
	    } catch (Exception e) {
		sViewer.status.setText("Error writing to " + path);
		return;
	    }
	    sViewer.status.setText("Wrote " + path);
	}
    }

    SourceViewer sViewer;
    FileDialog	fdialog = null;
}

class SourceViewer extends Frame implements Runnable {
    URL doc;
    TextArea t;
    Label	status;
    
    SourceViewer(hotjava wr, URL doc) {
	super(hotjava.server, true, wr, 725, 500, Color.lightGray);

	this.doc = doc;
	Font defaultFont = wServer.fonts.getFont("Courier", Font.PLAIN, 14);
	setDefaultFont(hotjava.dialogFont);
	setTitle("Source Viewer " + doc.toExternalForm());
	Window w = new Window(this, "Center", background, 700, 300);
	w.setLayout(new ColumnLayout(true));
	t = new TextArea(w, "Center", defaultFont, 80, 24);
	t.setHFill(true);
	t.setVFill(true);
	t.setEditable(false);
	w = new Window(this,"South",background,700,200);
	w.setLayout(new ColumnLayout(true));
	Row row = new Row(w,null,true);
	new SaveButton(row, this);
	new DismissButton(row, this);
	status = new Label("",null, w, defaultFont);
	status.setHFill(true);
	map();
	resize();
	new Thread(this, "SourceViewer").start();
    }

    public void run() {
	InputStream in = null;
	try {
	    in = doc.openStream();
	} catch (FileNotFoundException e) {
	    t.setText("");	// can you say HACK?
	    return;
	}
	OutputStreamBuffer out = new OutputStreamBuffer();
	byte buffer[] = new byte[1024];
	while (true) {
	    int n = in.read(buffer, 0, buffer.length);
	    if (n < 0) {
		break;
	    }
	    out.write(buffer, 0, n);
	}
	in.close();
	t.setText(out.toString());
    }
}

class PrintButton extends Button implements Runnable {
    PrintDialog printDialog;

    public PrintButton(Container w, PrintDialog f) {
	super("Print",null, w);

	printDialog = f;
    }

    public void selected(Component c, int pos) {
	new Thread(this, "Print Dialog").start();
    }

    public void run() {
	OutputStream os;
	Dimension paper = null;
	Properties props = printDialog.theWR.props;
	Object prop;
	String destType = null;
	String paperType = null;

	printDialog.theWR.setMessage("Printing...");
	prop = props.get("printerName");
	if (prop == null || !(prop instanceof String)
	    || !((String) prop).equals(printDialog.printerName.getText()))
	    props.put("printerName", printDialog.printerName.getText());
	prop = props.get("printCommand");
	if (prop == null || !(prop instanceof String)
	    || !((String) prop).equals(printDialog.commandName.getText()))
	    props.put("printCommand", printDialog.commandName.getText());
	prop = props.get("printFile");
	if (prop == null || !(prop instanceof String)
	    || !((String) prop).equals(printDialog.fileName.getText()))
	    props.put("printFile", printDialog.fileName.getText());
	if (printDialog.useFile.getState()){
	    destType = "file";
	    String file = printDialog.fileName.getText();
	    try {
		os = (OutputStream) new FileOutputStream(file);
	    } catch (Exception e) {
		printDialog.unMap();
		printDialog.theWR.setMessage("Printing: Bad File...Cancelled");
		return;
	    }
	} else if (printDialog.useCommand.getState()){
	    destType = "command";
	    String command = printDialog.commandName.getText();
	    os = System.execout(command);
	} else {
	    String printer;
	    if (printDialog.usePrinter.getState()) {
		destType = "printer";
		printer = printDialog.printerName.getText();
		if (!printer.equals(""))
		    printer = " -d " + printer;
	    } else {
		// assert(printDialog.useDefault.getState())
		destType = "default";
		printer = "";
	    }
	    os = System.execout("lp" + printer);
	}
	if (destType != null) {
	    prop = props.get("printDestination");
	    if (prop == null || !(prop instanceof String)
		|| !((String) prop).equals(destType))
		props.put("printDestination", destType);
	}
	if (printDialog.letterSheets.getState()) {
	    paperType = "letter";
	    paper = PSGraphics.Letter;
	} else if (printDialog.legalSheets.getState()) {
	    paperType = "legal";
	    paper = PSGraphics.Legal;
	} else if (printDialog.executiveSheets.getState()) {
	    paperType = "executive";
	    paper = PSGraphics.Executive;
	} else if (printDialog.A4Sheets.getState()) {
	    paperType = "A4";
	    paper = PSGraphics.A4;
	}
	if (paperType != null) {
	    prop = props.get("paperType");
	    if (prop == null || !(prop instanceof String)
		|| !((String) prop).equals(paperType))
		props.put("paperType", paperType);
	}
	if (props.changed)
	    props.save();

	Dimension layout = new Dimension(printDialog.theWin.width,
					 printDialog.theWin.height);
	BufferedOutputStream bos = new BufferedOutputStream(os);
	PSGraphics pg = new PSGraphics(printDialog.theWin, bos,
				       printDialog.theDoc.getTitle(),
				       layout, paper);
	String finalmsg = "Printing...Done";
	try {
	    printDialog.theWin.print(pg);
	} catch (Exception e) {
	    e.printStackTrace();
	    finalmsg = "Printing: I/O Error...Cancelled";
	}
	try {
	    pg.close();
	} catch (Exception e) {
	    finalmsg = "Printing: I/O Error...Cancelled";
	}
	printDialog.unMap();
	printDialog.theWR.setMessage(finalmsg);
	// REMIND: dispose is broken
	// printDialog.dispose();
    }
}

class DisposeButton extends Button {
    Frame	theFrame;

    public DisposeButton(Container w, Frame f) {
	this(w, f, "Cancel");
    }

    public DisposeButton(Container w, Frame f, String label) {
	super(label, null, w);

	theFrame = f;
    }

    public void selected(Component c, int pos) {
	theFrame.unMap();
	// REMIND: dispose is broken
	// theFrame.dispose();
    }
}

class PrintDialog extends Frame {
    hotjava theWR;
    Document theDoc;
    WRWindow theWin;

    RadioGroup paperSize = new RadioGroup();
    Toggle letterSheets;
    Toggle legalSheets;
    Toggle executiveSheets;
    Toggle A4Sheets;

    RadioGroup destType = new RadioGroup();
    Toggle useDefault;
    Toggle usePrinter;
    Toggle useFile;
    Toggle useCommand;
    TextField printerName;
    TextField fileName;
    TextField commandName;

    public PrintDialog(hotjava wr, Document doc) {
    	super(hotjava.server, true, wr, 400, 270, Color.lightGray);
	setDefaultFont(hotjava.dialogFont);
	setTitle("Print HotJava Document");
	theWR = wr;
	theWin = wr.doc;
	theDoc = doc;

    	Window w;
	RowColLayout r;
	Properties props = theWR.props;
	Object prop;
	String propStr;

	prop = props.get("paperType");
	if (prop != null && (prop instanceof String))
	    propStr = (String) prop;
	else
	    propStr = "";

	w = new Window(this, "Page Sizes", background, 400, 40);
	r = new RowColLayout(0, 7, true);
	r.setGaps(0,0,0,0);
	w.setLayout(r);
	new Label("Paper Size:", null, w);
	new Label("", null, w);	// Extra label provides gap before choices.
	letterSheets = new Toggle("Letter", null, w, paperSize,
				  propStr.equals("letter"));
	legalSheets = new Toggle("Legal", null, w, paperSize,
				 propStr.equals("legal"));
	executiveSheets = new Toggle("Executive", null, w, paperSize,
				     propStr.equals("executive"));
	A4Sheets = new Toggle("A4", null, w, paperSize,
			      propStr.equals("A4"));
	if (paperSize.getCurrent() == null)
	    letterSheets.setState(true);
	w.move(0, 0);

	prop = props.get("printDestination");
	if (prop != null && (prop instanceof String))
	    propStr = (String) prop;
	else
	    propStr = "default";
	String defPrinter = System.getenv("PRINTER");
	if (defPrinter == null)
	    defPrinter = "";

	w = new Window(this, "Destinations Title", background, 400, 30);
	new Label("Print Destinations", null, w);
	w.move(0, 40);

	w = new Window(this, "Default Destination", background, 400, 40);
	r = new RowColLayout(0, 2, true);
	r.setGaps(0,0,0,0);
	w.setLayout(r);
	useDefault = new Toggle(" System Default Printer:  " + defPrinter,
				null, w, destType, propStr.equals("default"));
	w.move(0, 70);

	w = new Window(this, "Other Destinations", background, 400, 120);
	r = new RowColLayout(0, 2, true);
	r.setGaps(0,0,0,4);
	w.setLayout(r);

	usePrinter = new Toggle("Printer:", null, w, destType,
				propStr.equals("printer"));
	prop = props.get("printerName");
	if (prop == null || !(prop instanceof String))
	    prop = defPrinter;
	printerName = new TextField((String) prop, null, w, true);

	useFile = new Toggle("File:", null, w, destType,
			     propStr.equals("file"));
	prop = props.get("printFile");
	if (prop == null || !(prop instanceof String))
	    prop = (Object) "/tmp/";
	fileName = new TextField((String) prop, null, w, true);

	useCommand = new Toggle(" Command:", null, w, destType,
				propStr.equals("command"));
	prop = props.get("printCommand");
	if (prop == null || !(prop instanceof String))
	    prop = (Object) "lp";
	commandName = new TextField((String) prop, null, w, true);

	if (destType.getCurrent() == null)
	    useDefault.setState(true);
	w.move(0, 110);

	w = new Window(this, "Buttons", background, 400, 40);
	new PrintButton(w, this);
	new DisposeButton(w, this);
	w.move(0, 230);

	resize();
    }
}

class PropertySheet extends Frame {
    public PropertySheet(hotjava wr) {
	super(hotjava.server, true, wr, 500, 446, Color.lightGray);
	setDefaultFont(hotjava.dialogFont);
	setTitle("HotJava Properties");
	mw = wr;

	RowColLayout	r;
	Window		w;
	int		h;
	String		acl;
	String path = System.getenv("HOME") + File.separator +
	    ".hotjava" + File.separator + "properties";
	File wpath = new File(System.getenv("HOME") +
			      File.separator + ".hotjava");


	wpath.mkdir();
	propFile = new File(path);
	w = new Window(this,"Center", background, 300, 130);
	r = new RowColLayout(0, 2, true);
	r.setGaps(0,0,0,0);
	w.setLayout(r);

	// XXX: this layout needs fixing...

	fProxyToggle = new Toggle("Firewall Proxy","proxy",w,
				  HttpClient.useProxyForFirewall);
	new Label("Port",null, w);
	fProxyField = new TextField(HttpClient.firewallProxyHost,
				    null,w,true);
	fProxyPort = new TextField("" + HttpClient.firewallProxyPort,
				   null,w,true);

	ftpProxyToggle = new Toggle("FTP Proxy",
				    null,w,FtpClient.useFtpProxy);
	new Label("Port",null, w);
	
	ftpProxyField = new TextField(FtpClient.ftpProxyHost,
				    null,w,true);
	ftpProxyPort = new TextField("" + FtpClient.ftpProxyPort,
				     null,w,true);

	cProxyToggle = new Toggle("Caching Proxy",null,w,
				  HttpClient.useProxyForCaching);
	new Label("Port",null, w);
	cProxyField = new TextField(HttpClient.cachingProxyHost,
				    null,w,true);
	cProxyPort = new TextField("" + HttpClient.cachingProxyPort,
				   null,w,true);


	new Label("Read Path:",null,w);
	acl = File.getReadACL();
	new TextField(acl,null,w,false);
	new Label("Write Path:",null,w);
	acl = File.getWriteACL();
	new TextField(acl,null,w,false);
	//new Label("Delay Image loading:","dil",w);
	new Label("Underline anchors:",null,w);
	ulField = new Toggle("", null,w,WRTextItem.underlineAnchors);
	new Label("Delay image loading:",null,w);
	dilField = new Toggle("", null,w,WRWindow.delayImageLoading);
	new Label("Delay applet loading:",null,w);
	dalField = new Toggle("", null,w,WRWindow.delayAppletLoading);
	w = new Window(this,"South",background,300,50);
	new ApplyButton(w, this);
	new PropertiesDismissButton(w, this, wr);

	errorDialog = new MessageDialog(this, "Error", null,
					MessageDialog.ERROR_TYPE, 1, true,
					null, null, null, null);
    }

    void setAllValues() {
	ulField.setState(WRTextItem.underlineAnchors);
	dilField.setState(mw.doc.delayImageLoading);
	dalField.setState(mw.doc.delayAppletLoading);
	    
	fProxyToggle.setState(HttpClient.useProxyForFirewall);
	fProxyField.setText(HttpClient.firewallProxyHost);
	fProxyPort.setText(HttpClient.firewallProxyPort+"");
	
	cProxyToggle.setState(HttpClient.useProxyForCaching);
	cProxyField.setText(HttpClient.cachingProxyHost);
	cProxyPort.setText(HttpClient.cachingProxyPort+"");

	ftpProxyToggle.setState(FtpClient.useFtpProxy);
	ftpProxyField.setText(FtpClient.ftpProxyHost);
	ftpProxyPort.setText(FtpClient.ftpProxyPort+"");
    }
    
    void show() {
	setAllValues();
	map();
	resize();
    }
    
    hotjava		mw;

    TextField		fProxyField;
    TextField		fProxyPort;
    Toggle		fProxyToggle;

    TextField		cProxyField;
    TextField		cProxyPort;
    Toggle		cProxyToggle;

    Toggle		ftpProxyToggle;    
    TextField		ftpProxyField;
    TextField		ftpProxyPort;

    File		propFile;
    Toggle		ulField;
    Toggle		dilField;
    Toggle		dalField;

    MessageDialog	errorDialog;
}

class OpenButton extends Button {
    public OpenButton(Container w,  OpenDialog d, hotjava mw) {
	super("Open",null, w);
	target = mw;
	dialog = d;
    }
    public void selected(Component c, int pos) {
	dialog.unMap();
	target.doc.pushURL(new URL(null, dialog.urlField.getText()));
    }

    hotjava target;
    OpenDialog	dialog;
}

class OpenTextField extends TextField {
    hotjava target;

    OpenTextField(Window w, hotjava mw) {
	super("",null,w,true);
	target = mw;
    }

    public void selected() {
	target.doc.pushURL(new URL(null, getText()));
    }
}

class OpenDialog extends Frame {
    TextField	urlField;

    public OpenDialog(WServer server, hotjava w) {
	super(server, true, w, 400, 100, Color.lightGray);
	setTitle("Open URL...");
	setDefaultFont(hotjava.dialogFont);
	Window cw = new Window(this, "Center", background, 300, 100);
	urlField = new OpenTextField(cw,w);
	urlField.setHFill(true);
	new OpenButton(cw, this, w);
	new DismissButton(cw, this);
    }
}

class HotListItem {
    public String	url;
    public boolean	inMenu;
    public MenuItem	menuItem;

    public HotListItem(String url, boolean inMenu) {
	this.url = url;
	this.inMenu = inMenu;
    }

    public String toString() {
	String m;

	if (menuItem != null) {
	    m = "<menuitem>";
	} else {
	    m = null;
	}
	    
	return "HotListItem[" + url + ", " + inMenu + ", " + m + "]";
    }
}

class MenuToggle extends Toggle {
    HotList target;

    public MenuToggle(Container parent, HotList target) {
	super("In Goto Menu",null,parent, false);
	this.target = target;
    }

    public void selected() {
	target.changeMenuStatus(getState(), target.selectedItem);
    }
}

class HotList extends Frame implements ChoiceHandler {
    String		hName;
    String		home;
    File		hotlist;
    Window		cw;
    int			selectedItem;
    HotlistGotoButton	gotoButton;
    List		slist;
    Vector		urlList;
    Vector		inMenuList;
    hotjava		mw;
    TextField		urlValue;
    Toggle		inMenuToggle;

    public HotList(WServer server, hotjava w) {
	super(server, true, w, 300, 400, Color.lightGray);
	setTitle("HotJava Hotlist");
	setDefaultFont(hotjava.dialogFont);

	Label l;

	mw = w;
	cw = new Window(this, "Center", background, 300, 200);
	cw.setLayout(new ColumnLayout(true));
	slist = new List(cw,this,null, 10, false);
	slist.setHFill(true);
	slist.setVFill(true);
	urlList = new Vector();
	inMenuList = new Vector();
	readHotList("default");
	cw = new Window(this, "South", background, 300, 100);
	cw.setLayout(new ColumnLayout(true));

	new Label("URL:", null, cw);
	urlValue = new TextField("",null,cw, false);
	urlValue.setHFill(true);

	Row row = new Row(cw, null, true);
	inMenuToggle = new MenuToggle(row,this);

	row = new Row(cw, null, true);
	gotoButton = new HotlistGotoButton(row, this);
	new DeleteButton(row, this);
	new DismissButton(row, this);
	selectedItem = 0;
    }

    /**
     * Change whether the given item is in the Goto menu or not.
     */
    public void changeMenuStatus(boolean inMenu, int index) {
	HotListItem hItem = (HotListItem)urlList.elementAt(index);
	String	    title = slist.itemAt(index);

	if (hItem.inMenu == inMenu) {
	    return;
	}
	if (inMenu) {
	    hItem.menuItem = mw.gotoMenu.addHotItem(title, hItem.url);
	} else {
	    if (hItem.menuItem != null) {
		hItem.menuItem.dispose();
	    }
	}
	hItem.inMenu = inMenu;
	if (index == selectedItem && inMenu != inMenuToggle.getState()) {
	    inMenuToggle.setState(inMenu);
	}
	write();
    }

    /**
     * Add an item to the hotlist
     */
    public void addItem(String title,
			String url,
			boolean inMenu,
			boolean checkMenu) {
	HotListItem	prev;
	int		i;
	int		nitems = urlList.size();

	for (i=0; i < nitems; i++) {
	    prev = (HotListItem)urlList.elementAt(i);
	    if (prev.url.equals(url)) {
		if (checkMenu) {
		    changeMenuStatus(inMenu, i);
		}
		return;
	    }
	}
	
	if (title.length() == 0) {
	    title = "Untitled (" + url + ")";
	}
	slist.addItem(title);
	urlList.addElement(prev = new HotListItem(url, false));
	if (checkMenu && inMenu) {
	    prev.inMenu = inMenu;
	    prev.menuItem = mw.gotoMenu.addHotItem(title, url);
	}
	write();
    }

    /**
     * write out this hotlist.
     */
    public void write() {
	FileOutputStream	outStr;
	PrintStream		pStr;
	int i;
	int nitems = urlList.size();
	File	hfile;
	HotListItem	hItem;

	hfile = new File(hotlist.getParent());
	hfile.mkdir();
	outStr = new FileOutputStream(hotlist);
	pStr = new PrintStream(outStr);
	pStr.println("hotjava-hotlist-version-1");
	pStr.println(hName);
	for (i = 0; i < nitems; i++) {
	    hItem = (HotListItem)urlList.elementAt(i);

	    if (hItem.inMenu) {
		pStr.println(hItem.url + " inMenu");
	    } else {
		pStr.println(hItem.url);
	    }
	    pStr.println(slist.itemAt(i));
	}
	outStr.close();
    }

    /**
     * Delete the given item from the hotlist.  Assumes item is the
     * current one selected, so clears the URL value.
     */
    public void delete(int item) {
	HotListItem	hItem = (HotListItem)urlList.elementAt(item);
	slist.delItem(item);
	
	if (hItem.inMenu && hItem.menuItem != null) {
	    hItem.menuItem.dispose();
	}
	urlList.removeElementAt(item);
	urlValue.setText("");
	write();
    }

    public void readHotList(String name) {
	FileInputStream	fstream;
	String		hlPath;
  
	hlPath = System.getenv("HOME") + File.separator + ".hotjava" +
	    File.separator + "hotlist-" + name;
	hName = name;
	try {
	    hotlist = new File(hlPath);
	    fstream = new FileInputStream(hotlist);
	} catch (Exception e) {
	    try {
		hotlist = new File(System.getenv("HOME") + File.separator + ".mosaic-hotlist-default");
		fstream = new FileInputStream(hotlist);
	    } catch (Exception ee) {
		return;
	    }
	}
	DataInputStream	dis = new DataInputStream(fstream);
	String	url;
	String	title;

	/* read version number and stupid line after it */
	dis.readLine();
	dis.readLine();

	try {
	    while (true) {
		url = dis.readLine();
		title = dis.readLine();
		if (url == null || title == null) {
		    break;
		}
		int	separator = url.indexOf(' ');
		boolean putInMenu = false;

		if (separator != -1) {
		    if (url.substring(separator).startsWith(" inMenu")) {
			putInMenu = true;
		    }
		    url = url.substring(0, separator);
		}

		HotListItem	item = new HotListItem(url, putInMenu);

		urlList.addElement(item);
		slist.addItem(title);
		if (putInMenu) {
		    item.menuItem = mw.gotoMenu.addHotItem(title, item.url);
		}
	    }
	} finally {
	    fstream.close();
	}
	hotlist = new File(hlPath);
    }
	
    public void selected(Component c, int pos) {
	HotListItem hItem;
	selectedItem = pos;
	hItem = (HotListItem)urlList.elementAt(pos);
	inMenuToggle.setState(hItem.inMenu);
	urlValue.setText(hItem.url);
    }

    public void doubleClick(Component c, int pos) {
	selectedItem = pos;
	gotoButton.selected(c, pos);
    }
}


class HelpMenu extends Menu {
    hotjava mw;

    static String topics[] = {
	"doc:readme.html",
	"doc:copyright.html",

	"doc:index.html",
	"doc:/doc/misc/using.html",
	"http://java.sun.com/",
	"doc:demo/index.html",
	"doc:/doc/misc/people.html",
	"doc:/doc/misc/BugReport.html",
	"doc:/doc/misc/Register.html",

	"doc:/doc/",
	"doc:/doc/misc/JavaSearchHelp.html"
    };
    
    public HelpMenu(MenuBar mbar, hotjava w) {
	super("Help", mbar);

	mw = w;
	MenuItem	item;

	new MenuItem("README!", this);
	new MenuItem("Copyright and License", this);

	addSeparator();
	new MenuItem("About HotJava", this);
	new MenuItem("Using HotJava", this);
	new MenuItem("Latest HotJava Info", this);
	new MenuItem("HotJava Demos", this);
	new MenuItem("Meet the People", this);
	addSeparator();
	new MenuItem("Submit a Bug Report", this);
	new MenuItem("Register", this);

	addSeparator();
	new MenuItem("HotJava Documentation", this);
	new MenuItem("How to Search the Documentation", this);
	new MenuItem("Search HotJava Documentation...", this);
    }
    
    public void selected(int index) {
        if (index >= topics.length) {
            if (mw.searchWindow == null) {
		mw.searchWindow = new SearchWindow(mw, mw.server);
            } 
	    mw.searchWindow.map();
            mw.searchWindow.resize();
        } else {
            mw.doc.pushURL(new URL(null, topics[index]));
        }
    }
}

class UrlField extends TextField {
    public UrlField(String value, Container parent, hotjava w) {
	super(value, null, parent, true);
	setColor(Color.black);	
	setFont(hotjava.inputFont);
	mw = w;
    }
    public void selected() {
	mw.doc.pushURL(new URL(null, getText()));
    }

    hotjava mw;
}

class InfoWindow extends Window {

    Label	urlTitle;
    TextField	urlField;

    final int sepBarHeight = 2;

    public InfoWindow(hotjava hj) {
	super(hj, "North", hj.background, 100, 10);
	setLayout(new RowLayout(true));
	
	Column col = new Column(this, null, false);
	new Space(col, null, 10,
		  9 + (hotjava.inputFont.ascent - hotjava.labelFont.ascent),
		  false, false);
	urlTitle = new Label("Document URL:", null, col, hotjava.labelFont);
	urlTitle.setColor(hotjava.blue);
	urlField = new UrlField("", this, hj);
	urlField.setHFill(true);

    }

    public void paint() {
	paint3DRect(0, height - sepBarHeight, width, sepBarHeight, false, true);
    }
}


class FindHandler implements DialogHandler {

    /** The HotJava in which we reside. */
    hotjava wr;
    
    /** The document window we're leaching off of. */
    WRWindow docWin;

    /** Where we should start (or resume) searching from. */
    int fromPos = 0;

    FindHandler(hotjava runner) {
	wr = runner;
	docWin = runner.doc;
    }
    
    public void okCallback(Dialog m) {
	String text = ""; //docWin.document().getText();
	StringDialog sd = (StringDialog)m;
	String lookFor = sd.getText();

	int index = text.indexOf(lookFor, fromPos);
	if (index != -1) {
	    docWin.scrollToTextPosition(index);
	    wr.setMessage("Found '"+lookFor+"' at character "+index+".");
	    fromPos = index + 1;
	} else {
	    String msg = "Not found.  Start again from beginning?";
	    MessageDialog md = new MessageDialog(sd, "Not found", msg,
						 MessageDialog.QUESTION_TYPE,
						 2, true, null, null, null,
						 null);
	    boolean startOver = (md.show() == 1); // user pressed OK button
	    if (startOver) {
		fromPos = 0;
		docWin.scrollToTextPosition(0);
		// do some sort of highlighting here, when possible.
	    } else {
		sd.hide();
	    }
	}

	// now change message to "find again" and let user do it again
	sd.setTitle("Find again");
    }
    
    /** Invoked when the user presses the "Cancel" button. */
    public void cancelCallback(Dialog m) {
	m.hide();
    }

    /** Invoked when the user presses the "Help" button. */
    public void helpCallback(Dialog m) {
    }
}


class HistoryItem {
    URL url;
    String title;

    HistoryItem(URL url, String title) {
	this.url = url;
	this.title = title;
    }
}


class HistoryWindow extends Frame implements ChoiceHandler, Runnable {

    hotjava       wRunner;
    TextField	  urlValue;
    HistoryVector historyVec;
    int           lastUpdated;

    int		  selectedItem;

    List	  scrollingList;
    Vector	  urlList;

    public synchronized void newElement() {
	notify();
    }
    
    public synchronized void run() {
	while (true) {
	    wait();

	    int time = historyVec.getTimeStamp();
	    if (mapped && (lastUpdated != time)) {
		stickHistoryOnList();
		lastUpdated = time;
	    }
	}
    }

    public HistoryWindow(WServer server, hotjava w, HistoryVector history) {
	super(server, true, w, 300, 400, Color.lightGray);
	setTitle(hotjava.programName+" History");
	setDefaultFont(hotjava.dialogFont);
	
	historyVec = history;
	urlList = new Vector();

	Label l;
	Window cw;

	cw = new Window(this, "Center", background, 300, 350);
	cw.setLayout(new ColumnLayout(true));
	scrollingList = new List(cw, this, "", 14, false, false);
	scrollingList.setHFill(true);
	scrollingList.setVFill(true);

	cw = new Window(this, "South", background, 300, 50);
	cw.setLayout(new ColumnLayout(true));

	urlValue = new TextField("destination URL", "urlValue", cw, false);
	urlValue.setHFill(true);

	Row row = new Row(cw, null, true);
	new HistoryGotoButton(row, this);
	new DismissButton(row, this);

	selectedItem = 0;
	wRunner = w;

	Thread th = new Thread(this);
	th.setName("History Updater");
	th.start();
    }

    private void showURL(int pos) {
	URL url = (URL)urlList.elementAt(pos);
	urlValue.setText(url.toExternalForm());
    }	

    public void selected(Component c, int pos) {
	selectedItem = pos;
	showURL(selectedItem);
    }

    public void doubleClick(Component c, int pos) {
	selectedItem = pos;
	gotoDocument();
    }

    public void showWindow() {
	map();
	resize();
	wServer.sync();
	int time = historyVec.getTimeStamp();
	if (lastUpdated != time) {
	    stickHistoryOnList();
	    lastUpdated = time;
	}
    }


    public void gotoDocument() {
	wRunner.doc.pushURL((URL)urlList.elementAt(selectedItem));
    }
    
    public void stickHistoryOnList() {
	scrollingList.clear();
	urlList.removeAllElements();

	for (int i = 0; i < historyVec.size(); i++) {
	    HistoryItem dInfo = (HistoryItem)historyVec.elementAt(i);
	    URL url = dInfo.url;
	    String title = dInfo.title;
	    
	    if (title == null || title.length() == 0) {
		title = hotjava.untitledTitle+" ("+url.toExternalForm()+")";
	    }
	    
	    scrollingList.addItem(title);
	    urlList.addElement(url);
	}

	if (historyVec.size() > 0) {
	    int showItem = historyVec.getCurrent();
	    scrollingList.makeVisible(showItem);
	    scrollingList.select(showItem);
	    showURL(showItem);
	}
    }
}


class HistoryGotoButton extends Button {

    public HistoryGotoButton(Container w, HistoryWindow hw) {
	super("Visit", null, w);
	histWin = hw;
    }
    
    public void selected(Component c, int pos) {
	histWin.gotoDocument();
    }

    HistoryWindow	histWin;
}
