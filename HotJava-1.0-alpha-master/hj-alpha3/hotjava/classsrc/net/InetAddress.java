/*
 * @(#)InetAddress.java	1.18 95/05/15 Jonathan Payne
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

package net;

import java.util.Hashtable;
import java.io.*;
import java.util.Linker;
import net.Socket;

public final class InetAddress {

    /** Link libnet.so into the runtime -- must be first static routine! */
    static {
	Linker.loadLibrary("net");
    }

    /** Cached addresses - our own litle nis, not! */
    static Hashtable	    addressCache = new Hashtable();
    public static String    localHostName;
    static InetAddress	    unknownAddress = new InetAddress();
    static InetAddress	    anyLocalAddress = new InetAddress(null);

    static {
	Socket.initialize();
	localHostName = getLocalHostName();
    }

    /**
     * Private copy of the hostName. This is declared private so it
     * can be checked securely by the Firewall class.
     */
    private String	pHost;

    /** Hostname this address is for - also the key in the
	above hash table. */
    public String   hostName;

    /** Address number of this host in network byte order. */
    public int	    address;

    /* private copy of address */
    private int     pAddress = 0;

    /** Address family. */
    int		    family;

    public static InetAddress getByName(String host) {
	InetAddress addr;

	if (host == null) {
	    return anyLocalAddress;
	}

	if ((addr = (InetAddress) addressCache.get(host)) == null) {
	    try {
		addr = new InetAddress(host);
	    } catch (UnknownHostException e) {
		addr  = unknownAddress;
	    }
	    addressCache.put(host, addr);
	}
	if (addr == unknownAddress) {
	    throw new UnknownHostException(host);
	}
	return addr;
    }

    public static InetAddress getByName(String host, InetAddress DNSHost) {
	InetAddress addr;

	if (host == null) {
	    return anyLocalAddress;
	}

	try {
		addr = new InetAddress(host, DNSHost);
	    } catch (UnknownHostException e) {
		addr  = unknownAddress;
	    }
	    addressCache.put(host, addr);
	if (addr == unknownAddress) {
	    throw new UnknownHostException(host);
	}
	return addr;
    }


    public static native String getLocalHostName();

    private static synchronized native void lookup(InetAddress addr);

    private static synchronized native void lookupWithDNS(InetAddress addr, 
					InetAddress DNSServer);

    public static synchronized native int getPortByName(String name);

    /** Package private constructor for the Socket.accept() method.
	This creates an empty InetAddress, which is filled in by
	the accept() method.  This InetAddress, however, is not
	put in the address cache, since it is not created by name. */
    InetAddress() {}

    public InetAddress(String host) {
	pHost = host;
	hostName = host;
	lookup(this);
	pAddress = address;
    }

    public InetAddress(String host, InetAddress DNShost) {
	pHost = host;
	hostName = host;
	lookupWithDNS(this, DNShost);
	pAddress = address;
    }

    public String toString() {
	int shift;
	int addr = address;
	String cmd = "";

	for (shift = 32; (shift -= 8) >= 0; ) {
	    cmd = cmd + ((addr >>> shift) & 0xff);
	    if (shift > 0)
		cmd = cmd + ",";
	}

	return "InetAddress[" + cmd + "]";
    }

    public boolean equals(Object o) {
	return (o instanceof InetAddress
		&& ((InetAddress) o).address == address);
    }
}
