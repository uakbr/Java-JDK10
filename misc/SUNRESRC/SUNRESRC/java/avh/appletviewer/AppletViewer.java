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

import awt.*;
import browser.*;
import net.www.html.*;

class AppletViewerDisplayItem extends AppletDisplayItem {
    public AppletViewerDisplayItem(DisplayItemWindow parent, URL url, TagRef ref) {
	super(parent, url, ref);
    }

    public void resize(int w, int h) {
	super.resize(w, h);
	AppletViewer.frame.reshape(0, 0, w + 15, h + 15);
    }
}

class AppletViewer extends DisplayItemWindow {
    static WServer ws;
    static Frame frame;
    static AppletViewer viewer;
    
    AppletDisplayItem item;
    
    public AppletViewer(Frame frm) {
	super(frm, "Center");
	enablePointerMotionEvents();
    }

    public void initApplet(URL url, TagRef ref) {
	item = new AppletViewerDisplayItem(this, url, ref);
	addItem(item);
	item.load();
	item.init();
    }

    public void startApplet() {
	item.start();
    }

    public static void main(String argv[]) {
	if (argv.length == 0) {
	    System.out.println("use: document-url class=AppletClass att1=val att2=val...");
	    return;
	}

	URL url = new URL(null, argv[0]);
	TagRef atts = new TagRef(Tag.lookup("app"), 0, false);
	
	for (int i = 1 ; i < argv.length ; i++) {
	    String str = argv[i];
	    int j = str.indexOf('=');
	    if (j < 0) {
		System.out.println("Invalid argument: " + str);
		return;
	    }
	    atts.addAttribute(str.substring(0, j).toLowerCase(), str.substring(j+1));
	}
	if (atts.getAttribute("class") == null) {
	    System.out.println("class attribute missing");
	    return;
	}

	ws = new awt.WServer();
	ws.start();
	frame = new Frame(ws, true, null, 200, 200, Color.lightGray);
	frame.setTitle("Applet Viewer: " + atts.getAttribute("class"));
	viewer = new AppletViewer(frame);
	viewer.initApplet(url, atts);
	frame.map();
	viewer.startApplet();
    }
}
