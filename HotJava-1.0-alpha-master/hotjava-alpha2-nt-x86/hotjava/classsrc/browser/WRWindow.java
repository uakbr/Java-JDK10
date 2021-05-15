/*
 * @(#)WRWindow.java	1.82 95/03/20 Jonathan Payne
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
import java.util.*;
import java.io.*;
import net.www.html.TagRef;
import net.www.html.URL;
import net.www.html.Tag;

/**
 * A Window subclass that contains hotjava documents.
 * html.Document objects are assigned to WRWindow's, which causes
 * those documents to be displayed in the window.  WRWindow also
 * serves as the focal point for handling document actions, such as
 * following links, or moving up and down the document stack.
 * @see	    net.www.html.Document
 * @see	    browser.Document
 * @see	    Window
 * @version 1.82, 20 Mar 1995
 * @author  Jonathan Payne
 */

public class WRWindow extends TextWindow {
    /** Current document info. */
    DocumentInfo    currentDocumentInfo;

    /** Current document (from document info), stored here so that
	it cannot be reclaimed by the garbage collector. */
    Object	    currentContent;

    Stack	    docStack = new Stack();
    int		    stackIndex;
    HistoryVector   docHistory = new HistoryVector();
    DocumentManager manager = new DocumentManager(this);
    Frame	    frame;

    static boolean delayImageLoading = false;
    static boolean delayAppletLoading = false;

    /**
     * Constructs a new WRWindow as the named child of another
     * Window.
     */
    public WRWindow (Window parent, String client) {
	super(parent, client);
	setSticky(false);
	frame = parent.getFrame();
    }

    /**
     * Constructs a new WRWindow as the named child of a Frame.
     */
    public WRWindow (Frame parent, String client) {
	super(parent, client);
	setSticky(false);
	frame = parent;
    }

    /**
     * Display a status message in the frame that contains this
     * WRWindow.
     */
    public void status(String s) {
	((hotjava) parent).setMessage(s);
    }

    /**
     * Turn on or off delayed image loading.  If delayed image
     * loading is turned on, and there are currently delayed images
     * being fetched, they are flushed.  If delayed image loading is
     * turned off, and there are images that need loading in the
     * current document, they are fetched now.
     */
    public void setDelayImageLoading(boolean on) {
	if (on != delayImageLoading) {
	    delayImageLoading = on;
	    if (!on) {
		relayout();
	    }
	}
    }

    /**
     * Turn on or off delayed applet loading.  If delayed applet
     * loading is turned on, and there are currently delayed applets
     * being fetched, they are flushed.  If delayed applet loading is
     * turned off, and there are applets that need loading in the
     * current document, they are feteched now.
     */
    public void setDelayAppletLoading(boolean on) {
	if (on != delayAppletLoading) {
	    delayAppletLoading = on;
	    if (!delayAppletLoading) {
		DisplayItem items[] = getItems();
		// First load and initialize everyone
		for (int i = 0 ; i < items.length ; i++) {
		    if ((items[i] != null) && (items[i] instanceof AppletDisplayItem)) {
			AppletDisplayItem applet = (AppletDisplayItem)items[i];
			applet.load();
			applet.init();
		    }
		}

		// reformat the window
		relayout();

		// Now start them, they keep track of their state so it
		// is ok to start an item twice.
		for (int i = 0 ; i < items.length ; i++) {
		    if ((items[i] != null) && (items[i] instanceof AppletDisplayItem)) {
			AppletDisplayItem applet = (AppletDisplayItem)items[i];
			applet.start();
		    }
		}
	    }
	}
    }

    /**
     * Notice the completion of background image reading, and
     * reformat the current document if there are no more pending
     * images.
     */
    public void imageProgress(int pending) {
	if (pending > 0) {
	    // status("Reading images: " + pending + " left to read ...");
	} else if (currentDocumentInfo != null) {
	    relayout();
    	    // status("");
	}
    }

    /**
     * Find an intra-document reference as specified by the url
     * parameter.
     */
    synchronized void findRef(URL url) {
	Vector refs = ((Document) currentContent).getTags();
	int cnt = refs.size();
	int i = 0;
	Tag anchorTag = Tag.lookup("a");
	int pos = -1;

	while (--cnt >= 0) {
	    TagRef ref = (TagRef) refs.elementAt(i++);
	    String name;

	    if (ref.tag != anchorTag || ref.isEnd)
		continue;
	    if ((name = ref.getAttribute("name")) != null &&
		    name.equals(url.ref)) {
		pos = ref.pos;
		break;
	    }
	}
	if (pos >= 0) {
	    scrollToTextPosition(pos);
	} else {
	    System.out.print("Cannot find name ref: #" + url.ref + "\n");
	    scrollAbsolute(0, 0);
	}
    }

    public synchronized void scrollToTextPosition(int pos) {
	int i = count();

	while (--i >= 0) {
	    DisplayItem	di = nthItem(i);

	    if (di instanceof WRTextItem) {
		WRTextItem	wi = (WRTextItem) di;

		if ((wi.offset > 0) && (wi.offset <= pos)) {
		    scrollAbsolute(0, wi.y);
		    return;
		}
	    }
	}
	scrollAbsolute(0, 0);
    }

    boolean isHtmlDocument(Object content) {
	return (content != null && content instanceof Document);
    }

    protected void stopApplets() {
	if (isHtmlDocument(currentContent)) {
	    ((Document) currentContent).stopApplets();
	}
    }

    protected void startApplets() {
	if (isHtmlDocument(currentContent)) {
	    ((Document) currentContent).startApplets();
	}
    }

    protected void destroyApplets() {
	if (isHtmlDocument(currentContent)) {
	    ((Document) currentContent).destroyApplets();
	}
    }

    /**
     * Set the current document displayed by this window to the
     * specified DocumentInfo.  If this document has been visited
     * previously, this scrolls to the last position at which this
     * document was displayed.  If the document contains a #ref,
     * that is also handled here.
     * @see DocumentInfo
     * @see net.www.html.URL
     */
    protected boolean setDocument(DocumentInfo newDoc, boolean restore) {
	boolean	resident = newDoc.isResident();

	if (!resident) {
	    status("Fetching " + newDoc.url.toExternalForm() + " ...");
	    disablePointerMotionEvents();
	}
	Object	content = newDoc.getContent();

	if (!resident) {
	    manager.cacheDocument(newDoc);
	    enablePointerMotionEvents();
	}

	if (content == null || content instanceof net.www.html.MimeLauncher) {
	    newDoc.setCacheable(false);
	    unpushDocument(newDoc);
	    status("Done");
	    return false;
	} else if (content instanceof InputStream) {
	    /* A stream that the user might want saved */
	    new ContentSaver((InputStream) content, frame,
			     newDoc.url, this);
	    content = null;
	    newDoc.setCacheable(false);
	    unpushDocument(newDoc);
	    status("Done");
	    return false;
	} else if (content instanceof DIBitmap) {
	    content = new ImageDisplayItem(createImage((DIBitmap) content));
	}

	/* stop applets of current document, if current document is
	   actually an html page */
	stopApplets();

	/* set the url field in the main window */
	((hotjava) parent).setURL(newDoc.url.toExternalForm());

	/* close the current document */
	if (currentDocumentInfo != null) {
	    currentDocumentInfo.close(this);
	}

	/* set the title, if there is one */
	((hotjava) parent).setTitle(newDoc.getTitle());

	Object	previousContent = currentContent;

	currentDocumentInfo = newDoc;
	currentContent = content;

	if (!(content instanceof Document)) {
	    //setText(null);
	    invalidate();
	    if (restore) {
		setScrolling(0, newDoc.scrollY <= 0 ? newDoc.scrollY : 0);
	    } else {
		setScrolling(0, 0);
	    }
	    paint();
	} else {
	    if (currentDocumentInfo == null || content != previousContent) {
		invalidate();
		//setText(((Document) content).getText());
		if (!preserveScrollingAtNextValidate) {
		    setScrolling(0, 0);
		}
	    }
	    if (!restore && newDoc.url.ref != null) {
		/* Unfortunately, this causes a paint in one place
		   followed by a scroll to another */
		validate();
		findRef(newDoc.url);
	    } else {
		if (!valid) {
		    if (newDoc.scrollY <= 0) {
			setScrolling(0, newDoc.scrollY);
		    }
		    validate();
		} else {
		    scrollAbsolute(0, -newDoc.scrollY);
		}
	    }
	    startApplets();
	}
	return true;
    }

    /**
     * Returns the current document being displayed in this Window.
     */
    public Document document() {
	return isHtmlDocument(currentContent) ? (Document) currentContent : null;
    }

    synchronized public void pushURL(URL url) {
	if (stackIndex < docStack.size()) {
	    docStack.setSize(stackIndex);
	}

	DocumentInfo	info = manager.newDocument(url);

	if (!info.emptyDocument)
	    pushDocument(info);
	setDocumentInNewThread(info, false, true);
    }

    public void fetching(String msg) {
	status(msg);
    }

    /**
     * Deals with the fact that some document that was being fetched
     * in the background has now completed.  In general, this pushes
     * this document into the document stack, making it the new
     * current document.
     */
    void pushDocument(DocumentInfo info) {
	if (info != null) {
	    docStack.push(info);
	    stackIndex += 1;
	}
    }

    /**
     * Deals with the fact that some document may have been pushed on
     * the document stack before we knew what it was.  If it turned
     * out to be uncacheable, then we must remove it.
     */
    void unpushDocument(DocumentInfo info) {
	if (info != null) {
	    if (docStack.removeElement(info)) {
		stackIndex -= 1;
	    }
	}
    }

    void addToHistory(DocumentInfo info) {
	docHistory.pushElement(info);
	hotjava.history.addUrl(info.url);
    }

    /**
     * Handles the callback from pressing the reload button in the main
     * window.
     */
    public void reload() {
	if (currentDocumentInfo != null) {
	    uncacheCurrentDocument();
	    preserveScrollingAtNextValidate = true;
	    setDocumentInNewThread(currentDocumentInfo, true, false);
	}
    }

    /**
     * Handles the callback from pressing the Back button in the main
     * window.
     */
    public void backup() {
	if (stackIndex > 1) {
	    stackIndex -= 1;
	    setDocumentInNewThread((DocumentInfo) docStack.elementAt(stackIndex - 1),
				   true, false);
	}
    }

    /**
     * Handles the callback from pressing the Forward button in the main
     * window.
     */
    public void forward() {
	if (stackIndex < docStack.size()) {
	    setDocumentInNewThread((DocumentInfo) docStack.elementAt(stackIndex++),
				   true, false);
	}
    }

    Thread switcherThread = null;

    synchronized void setDocumentInNewThread(DocumentInfo info,
					     boolean restore,
					     boolean push) {
	if (switcherThread != null && switcherThread.isAlive()) {
	    switcherThread.stop();
	}
	switcherThread = new DocumentSwitcher(this, info, restore, push);
	switcherThread.start();
    }

    private boolean preserveScrollingAtNextValidate = false;

    public void uncacheCurrentDocument() {
	destroyApplets();
	URL.flushClassLoader();
	currentContent = null;

	DocumentInfo	di = currentDocumentInfo;

	if (di != null) {
	    di.clearDoc();
	}
    }

    /**
     * Relays out the current document, trying to preserve the current
     * position in the document.
     */
    public void relayout() {
	addUpdateRequest(new RelayoutUpdateRequest());
    }

    public void preserveScrolling() {
	preserveScrollingAtNextValidate = true;
    }

    public void handleResize() {
	preserveScrollingAtNextValidate = true;
	super.handleResize();
	relayout();
    }

    public void validate() {
	if (valid) {
	    return;
	}
	if (isHtmlDocument(currentContent)) {
	    status("Formatting ...");
	    setFormatter(new WRFormatter(this, (Document) currentContent));
	    layoutDocument();
	    valid = true;
	    preserveScrollingAtNextValidate = false;
	    status("Complete");
	} else if (currentContent != null
		   && currentContent instanceof DisplayItem) {
	    DisplayItem	di = (DisplayItem) currentContent;
	    int		x = (width-di.width)/2;
	    int		y = (height-di.height)/2;

	    clearItems();
	    startNewLine(0, y);
	    addItem(di);
	    di.move(x, y);
	    logicalHeight = di.height;
	}
	updateScrollbar();
    }
}

class ContentSaver extends Thread {
    InputStream is;
    URL u;
    Frame f;
    WRWindow status_target;

    private void status(String s) {
	if (status_target != null && s != null)
	    status_target.status(s);
    }

    ContentSaver(InputStream IS, Frame F, URL U, WRWindow st) {
	is = IS;
	u = U;
	f = F;
	status_target = st;
	start();
    }

    public void run() {
	FileDialog fd = new FileDialog("Save to file", f);
	String dfn = "file.out";
	String ofn = null;
	if (u != null && u.file != null) {
	    dfn = u.file;
	    int i = dfn.lastIndexOf('/');
	    if (i < 0)
		i = dfn.lastIndexOf(':');
	    if (i > 0)
		dfn = dfn.substring(i + 1);
	}
	OutputStream os = null;
	while (os == null) {
	    ofn = fd.chooseFile(dfn);
	    if (ofn == null)
		break;
	    try {
		os = new FileOutputStream(ofn);
	    } catch(Exception e) {
		status("Can't open file for write: " + ofn);
		os = null;
	    }
	    
	}
	if (os != null) {
	    status("Writing "+ofn);
	    try {
		byte buf[] = new byte[2048];
		int i;
		while ((i = is.read(buf)) >= 0) {
		    os.write(buf, 0, i);
		}
		status("Finished writing "+ofn);
	    } catch(Exception e) {
		status("Save to file failed: " + e);
	    }
	}
	if (os != null)
	    os.close();
	if (is != null)
	    is.close();
	fd.dispose();
    }
}


class RelayoutUpdateRequest extends DIWUpdateRequest {
    void execute(DisplayItemWindow w) {
	WRWindow    wr = (WRWindow) w;

	wr.invalidate();
	wr.preserveScrolling();
	wr.validate();
    }
}

class DocumentSwitcher extends Thread {
    WRWindow	    owner;
    DocumentInfo    info;
    boolean	    restore;
    boolean	    push;

    DocumentSwitcher(WRWindow owner, DocumentInfo info,
		     boolean restore, boolean push) {
	this.owner = owner;
	this.info = info;
	this.restore = restore;
	this.push = push;
    }

    public void run() {
	try {
	    setPriority(Thread.MIN_PRIORITY+2);
	    owner.setDocument(info, restore);

	    ((hotjava)(owner.parent)).pageHasSource(owner.document() != null);

	    if (push) {
		/* Add to history AFTER the document is fetched, because
		   only then do we know the title of the document.  If we
		   do this beforehand, the act of asking for the title
		   will cause the document to be fetched. */
		owner.addToHistory(info);
	    }
	    owner.switcherThread = null;
	} catch (ThreadDeath d) {
	    /*
	     * Try to stop a document's applets.  We use check()
	     * directly to avoid having the DocRef fetch the
	     * document if it isn't currently in memory.
	     */
	    Object o;
	    
	    try {
		o = owner.currentDocumentInfo.doc.check();
	    } catch (NullPointerException e) {
		// Some of the fields may not have been set yet.
		o = null;
	    }
	    if (o != null) {
		Document doc;

		try {
		    doc = (Document) o;
		    doc.stopApplets();
		} catch (ClassCastException e) {
		    // doc was not a Document; just press on.
		}
	    }
	}
    }
}
