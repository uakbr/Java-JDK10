/*
 * @(#)Handler.java	1.21 95/03/15 James Gosling
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

/*-
 *	http stream opener
 */

package net.www.protocol.http;

import java.io.*;
import net.www.http.*;
import net.TelnetInputStream;
import net.UnknownHostException;
import java.util.Hashtable;
import net.www.http.UnauthorizedHttpRequestException;
import net.www.http.AuthenticationInfo;
import net.www.html.URLStreamHandler;
import net.www.html.URL;
import net.www.html.MeteredStream;
import net.www.auth.Authenticator;
import awt.*;

/** open an http input stream given a URL */
public class Handler extends URLStreamHandler {
    String	proxy;
    int		proxyPort;

    public Handler() {
	proxy = null;
	proxyPort = -1;
    }

    public Handler(String proxy, int port) {
	this.proxy = proxy;
	this.proxyPort = port;
    }

    public InputStream openStream(URL u) {
	HttpClient  http;
	int	    count = 0;
	while (true) {
	    if (++count >= 4) {
		throw new FileNotFoundException(u.toExternalForm());
	    }
	    http = new HttpClient(u, proxy, proxyPort);
	    String newLocation = http.getHeaderField("location");
	    if (newLocation != null)
		u.copy(new URL(u, newLocation));
	    if (http.getStatus() == HttpClient.OK || newLocation == null)
		break;
	    http.closeServer();
	    if (!"http".equals(u.protocol))
		return u.openStream();	/* redirecting to some protocol other than http */
	}
	InputStream is = http.getInputStream();
	u.setType(getContentType(http, u));

	/*
	 * If the http stream returns a content-length attribute we
	 * wrap a MeteredStream around the network stream so we get
	 * some feedback about the progress of the connection.
	 */
	String ct = http.getHeaderField("content-length");
	int len = 0;
	if (ct != null && (len = Integer.parseInt(ct)) != 0) {
	    is = new MeteredStream(is, len);
	}
	return is;
    }

    static AuthorizationDialog	dialog = null;

    private static AuthenticationInfo getAuthentication(URL url, String scheme, String realm) {
	Authenticator	auth;
	OutputStreamBuffer  out = new OutputStreamBuffer(64);
	AuthenticationInfo  info;

	auth = (Authenticator) new("net.www.auth." + scheme);
	if (dialog == null) {
	    dialog = new AuthorizationDialog();
	}
	String	userAndPassword = dialog.getAuth(url, realm);

	if (userAndPassword == null) {
	    return null;
	}
	auth.encrypt(new InputStreamStringBuffer(userAndPassword), out);
	info = new AuthenticationInfo(url.host, url.getPort(), realm,
				      scheme + " " + out.toString());
	return info;
    }

    /**
     * Similar to openStream except that it will pop up a dialog box to
     * resolve authentication failures.     */
    public InputStream openStreamInteractively(URL u) {
	AuthenticationInfo  info = null;

	while (true) {
	    try {
		InputStream is = openStream(u);
		return is;
	    } catch (UnauthorizedHttpRequestException e) {
		HttpClient http = e.http;
		try {
		    String  scheme = http.getAuthenticationScheme();
		    String  realm = http.getAuthenticationRealm();

		    info = getAuthentication(u, scheme, realm);
		    if (info == null) {
			u.setType(getContentType(http, u));
			return http.getInputStream();
		    }
		    http.closeServer();
		    continue;
		} catch (Exception e1) {
		    http.closeServer();
		    throw e1;
		}
	    }
	}
    }


    private String getContentType(HttpClient http, URL u) {
	String ct = http.getHeaderField("content-type");
	String ce = http.getHeaderField("content-encoding");

	if (ct == null || (ct = u.mimeToContent(ct)) == null) {
	    if (u.file.endsWith("/")) {
		ct = URL.content_html;
	    } else {
		ct = formatFromName(u.file);
	    }
	}
	/*
	 * If the Mime header had a Content-encoding field and its
	 * value was not one of the values that essentially indicate
	 * no encoding, we force the content type to be unknown.
	 * This will cause a save dialog to be presented to the
	 * user.  It is not ideal but is better than what we were
	 * previously doing, namely bringing up an image tool for
	 * compressed tar files.
	 */

	if (ce != null && 
	       !(ce.equalsIgnoreCase("7bit") 
		 || ce.equalsIgnoreCase("8bit") 
		 || ce.equalsIgnoreCase("binary"))) {
	    ct = URL.content_unknown;
	}

	return ct;
    }
}

class AuthorizationDialog extends Frame {
    TextField	passwordField;
    TextField	userField;
//    Label	mainLabel;
    Window	cw;

    String	auth;
    boolean	authSet;

    public AuthorizationDialog() {
	super(true, 300, 150, Color.lightGray);
	setDefaultFont(wServer.fonts.getFont("Dialog", Font.BOLD, 12));

	Label l;

//	mainLabel = new Label("", null, cw, null);
//	mainLabel.setHFill(true);

	cw = new Window(this,"Center", background, 300, 100);
	cw.setLayout(new RowColLayout(0, 2, true));

	Column col = new Column(cw, null, false);

	new Space(col, null, 12, 5, false, false);
	l = new Label("User:",null, col, null);
	userField = new TextField("","",cw, true);

	col = new Column(cw, null, false);	
	new Space(col, null, 12, 5, false, false);
	l = new Label("Password:",null, col, null);

	passwordField = new TextField("","",cw, true);
	passwordField.setEchoCharacter('#');

	cw = new Window(this, "South", background, 100, 30);
	cw.setLayout(new RowLayout(true));
	new FrameButton("Login", cw, this);
	new FrameButton("Clear", cw, this);
	new FrameButton("Cancel", cw, this);
    }

    int	preferredHeight = 151;

    public synchronized String getAuth(URL url, String realm) {
	String	title = "Login to " + realm + " at " + url.host;
	int width = defaultFont.stringWidth(title);

	setTitle(title);
	authSet = false;
	reshape(x, y, width + 80, preferredHeight);
	resize();
	map();
	while (!authSet)
	    wait();
	return auth;
    }

    public void map() {
	userField.setText("");
	passwordField.setText("");
	super.map();
    }	

    private synchronized void setAuth(String auth) {
	this.auth = auth;
	authSet = true;
	notifyAll();
    }

    public void handleButtonClick(Button b) {
	String	label = b.label;

	if (label.equals("Login")) {
	    setAuth(userField.getText() + ":" + passwordField.getText());
	    unMap();
	} else if (label.equals("Clear")) {
	    userField.setText("");
	    passwordField.setText("");
	} else {
	    setAuth(null);
	    unMap();
	}
    }
}

class FrameButton extends Button {
    AuthorizationDialog	owner;

    public FrameButton(String name, Container cw, AuthorizationDialog d) {
	super(name, "", cw);
	owner = d;
    }

    public void selected(Component c, int pos) {
	owner.handleButtonClick(this);
    }
}
