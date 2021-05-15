/*
 * @(#)IftpClient.java	1.8 95/02/10 Jonathan Payne
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

import java.io.*;
import net.*;

package net.ftp;

/**
 * Create an FTP client that uses a proxy server to cross a network
 * firewall boundary.
 *
 * @version 	1.8, 10 Feb 1995
 * @author	Jonathan Payne
 * @see FtpClient
 */
public class IftpClient extends FtpClient {
    /** The proxyserver to use */
    String  proxyServer = "sun-barr";
    String  actualHost = null;
    
    /**
     * Open an FTP connection to host <i>host</i>.
     */
    public void openServer(String host) {
	if (!serverIsOpen())
	    super.openServer(proxyServer,
			     InetAddress.getPortByName("ftp-passthru"));
	actualHost = host;
    }

    boolean checkExpectedReply() {
	return readReply() != FTP_ERROR;
    }

    /** 
     * login user to a host with username <i>user</i> and password 
     * <i>password</i> 
     */
    public void login(String user, String password) {
	if (!serverIsOpen()) {
	    throw new FtpLoginException("not connected to host");
	}
	user = user + "@" + actualHost;
	this.user = user;
	this.password = password;
	if (issueCommand("USER " + user) == FTP_ERROR ||
	    lastReplyCode == 220 && !checkExpectedReply()) {
	    throw new FtpLoginException("user");
	}
	if (password != null && issueCommand("PASS " + password) == FTP_ERROR) {
	    throw new FtpLoginException("password");
	}
    }

    /**
     * change the proxyserver from the default.
     */
    public void setProxyServer(String proxy) {
	if (serverIsOpen())
	    closeServer();
	proxyServer = proxy;
    }

    /**
     * Create a new IftpClient handle.
     */
    public IftpClient(String host) {
	super();
	openServer(host);
    }

    /** Create an uninitialized client handle */
    public IftpClient() {}
}
