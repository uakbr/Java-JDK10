/*
 * @(#)FtpInputStream.java	1.2 95/03/18 Chris Warth
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

import java.io.FilterInputStream;
import java.io.InputStream;
import net.TelnetInputStream;

package net.ftp;

/*
 * The only purpose of this class is to hold onto an instance of
 * FtpClient to prevent the FtpClient from being garbage collected.
 * If the client gets collected it will close the command stream
 * which will cause the remote ftp daemon to close its outputstream
 * which will cause and EOF on this inputstream on our end.  
 *
 * The real answer is to re-architect all this stuff to not open an
 * ftp connection each time the user needs something from the remote
 * side.  The other correct thing it to totally ignore all the ftp
 * client stuff and just use the ftp proxy stuff in the CERN HTTP
 * server.
 *
 * @version	1.2, 18 Mar 1995
 * @author	Chris Warth
 */

public class FtpInputStream extends TelnetInputStream {
    FtpClient ftp;

    FtpInputStream(FtpClient ftp, InputStream fd, boolean binary) {
	super(fd, binary);
	this.ftp = ftp;
    }

    public void close() {
	ftp.closeServer();
	super.close();
    }
}
