/*
 * @(#)Firewall.java	1.20 95/03/20 Sami Shaio
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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

import java.lang.String;
import java.util.*;
import java.io.*;
import net.www.html.*;
import net.www.http.HttpClient;

/**
 * @version 1.20 20 Mar 1995
 * @author Sami Shaio
 */
public final class Firewall {
    private static final boolean	debug = false;

    private static Vector firewallHosts = new Vector();

    // ACCESS_* constants are arranged numerically from most
    // restrictive to least restrictive.
    public static final int ACCESS_NONE		= 0;
    public static final int ACCESS_SOURCE	= 1;
    public static final int ACCESS_FIREWALL	= 2;
    public static final int ACCESS_ALL		= 3;

    public static final int DNS_DOMAIN		= 0;
    public static final int NIS_DOMAIN		= 1;

    private static int accessMode = ACCESS_SOURCE;
    private static int sysAccessMode = ACCESS_ALL;

    private static boolean restrictApplets = false;
    private static boolean sysRestrictApplets = false;

    private static int domainType = NIS_DOMAIN;

    private static boolean usingSystemFirewall = false;
    private static AccessErrorHandler handler = null;

    static {
	Linker.loadLibrary("net");
    }

    /**
     * append the domain name if not already present in the host name.
     * we do this by appending the largest component of the domain
     * name that is not already at the end of the host name. For
     * example, to append "Foo.Bar.Com" to a host given as "host.Foo"
     * we end up with "host.Foo.Bar.Com".
     */
    private static String appendDomain(String host, String domain) {
	int dot;
	String restOfDomain;

	restOfDomain = "";
	while (domain.length() > 0) {
	    if (host.endsWith(domain)) {
		host = host + restOfDomain;
		break;
	    }
	    dot = domain.lastIndexOf('.');
	    if (dot == -1) {
		host = host + "." + domain + restOfDomain;
		break;
	    }
	    restOfDomain = domain.substring(dot) + restOfDomain;
	    domain = domain.substring(0, dot);
	}

	return host;
    }

    public static void securityError(String msg) {
	// XXX: should log the error somewhere and put up a dialog...
	if (handler != null) {
	    handler.readException(msg);
	}
	throw new SecurityException(msg);
    }

    public static native Object getClassLoader();
    private static native URL	 getSourceURL(WWWClassLoader l);
    private static native int compareAddresses(InetAddress a,
					       InetAddress b);

    public static void setHandler(AccessErrorHandler f) {
	checkFirewallMethodAccess("applet attempted to override security handler");
	handler = f;
    }

    private static native String getDomainName();

    /*
     * Compare a firewall host against a host to determine if there's
     * a match.
     * The rules are:
     * - if the firewall host is a domain name then append the domain
     *   name to the given host (if it isn't there already) and resolve
     *   the host to an IP address. If the resolve succeds return true.
     * - if the firewall host is a hostname, and the hosts match
     *   symbolically, then return true. Otherwise resolve the
     *   firewall host to an IP address and resolve the given host to
     *   an IP address. Return true if both addresses match.
     */
    private static int compareHosts(String fHost, String host) {
	String rest;

	if (debug) {
	    System.out.println("compareHosts " + fHost + " " + host);
	}
	rest = fHost.substring(1).toLowerCase();
	switch (fHost.charAt(0)) {
	  case 'D': // domain name
	  case 'd':
	    {
		int len = host.length();
		int rLen = rest.length();
		int dot;
		String restOfDomain;
		String fullHost;
		InetAddress addr;		

		host = host.toLowerCase();
		dot = host.indexOf('.');
		if (dot == -1) {
		    if (rest.equals(".")) {
			// A domain of "." means accept local hosts
			// which are hosts with no domain name
			try {
			    addr = InetAddress.getByName(host);
			} catch (UnknownHostException e) {
			    return -1;
			}
			return 1;
		    }
		}

		if (rest.equals(".")) {
		    return 0;
		}

		if (! host.endsWith(rest)) {
		    // host has no domain name. append the
		    // default domain and attempt to resolve
		    // it. If we succeed, then we set the
		    // host to its fully qualified domain
		    restOfDomain = getDomainName().toLowerCase();

		    fullHost = appendDomain(host, restOfDomain);
		    try {
			addr = InetAddress.getByName(fullHost);
			// set host to be the fully-qualified host
			host = fullHost;
			if (debug) {
			    System.out.println("...rewriting host as " + host);
			}
		    } catch (UnknownHostException e) {
			switch (domainType) {
			  case NIS_DOMAIN:
			    // we got an NIS domain in which case the
			    // first component might be bogus.
			    dot = restOfDomain.indexOf('.');
			    if (dot != -1) {
				restOfDomain = restOfDomain.substring(dot);
				if (restOfDomain.length() > 0) {
				    fullHost = appendDomain(host,restOfDomain);
				    try {
					addr = InetAddress.getByName(fullHost);
					host = fullHost;
					if (debug) {
					    System.out.println("...rewriting host as " + host);
					}
				    } catch (UnknownHostException e2) {
				    }
				}
			    }
			    break;
			  case DNS_DOMAIN:
			  default:
			    break;
			}
		    }
		}

		host = appendDomain(host, rest);
		    
		try {
		    if (debug) {
			System.out.println("... resolving " + host);
		    }
		    addr = InetAddress.getByName(host);
		    if (debug) {
			System.out.println("==> 1");
		    }
		} catch (UnknownHostException e) {
		    if (debug) {
			System.out.println("==> 0");
		    }
		    return 0;
		}
	    }
	    return 1;
	  case 'H': // hostname
	  case 'h':
	    {
		int rval;

		if (rest.equals(host)) {
		    if (debug) {
			System.out.println("==> 1");
		    }
		    return 1;
		}

		InetAddress addr;
		InetAddress faddr;
		
		try {
		    faddr = InetAddress.getByName(rest);
		    addr = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
		    if (debug) {
			System.out.println("==> -1");
		    }
		    return -1;
		}
		rval = Firewall.compareAddresses(faddr,addr);
		if (debug) {
		    System.out.println("==> " + rval);
		}
		return rval;
	    }
	  default:
	    if (debug) {
		System.out.println("==> 0");
	    }
	    return 0;
	}
    }

   
    /*
     * Returns true if the applet coming from the given class loader is
     * inside the firewall. This classification is done by scanning
     * the firewall list and comparing the addresses with the host
     * address of the class loader. In cases where the host address of
     * the class loader can't be determined the applet is classified
     * as "outside". 
     */
    private static boolean isInsideApplet(WWWClassLoader loader) {
	URL	source = getSourceURL(loader);
	int	len = firewallHosts.size();

	if (source == null || len == 0) {
	    return false;
	}

	if (debug) {
	    System.out.println("isInsideApplet: " + source.host);
	}
	if (source.protocol.equals("file")) {
	    return true;
	}
	for (int i=0; i < len; i++) {
	    String host = (String)(firewallHosts.elementAt(i));

	    switch (Firewall.compareHosts(host, source.host)) {
	      case 0:
		break;
	      case 1:
		if (debug) {
		    System.out.println("==> true");
		}
		return true;
	      case -1:
		break;
	    }
	}

	if (debug) {
	    System.out.println("==> false");
	}

	return false;
    }

    /*
     * Returns 1 if the destination given is inside the firewall.
     * Otherwise, it returns 0. This classification is done by scanning
     * the firewall list and comparing the addresses with the address of the
     * given host. If the address of the host can't be resolved then it is
     * classified as "inside" by default and 1 is returned.
     */
    private static boolean isInsideDestination(String host, long port) {
	int	len = firewallHosts.size();
	boolean maybe = false;

	if (host == null || len == 0) {
	    return false;
	}
	if (debug) {
	    System.out.println("isInsideDestination: " + host);
	}
	for (int i=0; i < len; i++) {
	    String fhost = (String)(firewallHosts.elementAt(i));


	    switch (Firewall.compareHosts(fhost, host)) {
	      case 0:
		break;
	      case 1:
		if (debug) {
		    System.out.println("==> true");
		}
		return true;
	      case -1:
		maybe = true;
		break;
	    }
	}

	if (debug) {
	    if (maybe) {
		System.out.println("==> true [maybe]");
	    } else {
		System.out.println("==> false");
	    }
	}
	return (maybe) ? true : false;
    }

    private native static boolean isTrustedClass();
	
    private static boolean isProxyRequest(String host, int port) {
	return (port == HttpClient.firewallProxyPort &&
		host.equals(HttpClient.firewallProxyHost));
    }

    private static void checkFirewallMethodAccess(String msg) {
	Object l = Firewall.getClassLoader();

	if (l != null) {
	    Firewall.securityError(msg);
	}
    }

    public static boolean canOverrideFirewall() {
	checkFirewallMethodAccess("applet attempted to inquire about the firewall(canOverrideFirewall)");
	return !usingSystemFirewall;
    }

    public static void clearFirewallHosts() {
	checkFirewallMethodAccess("applet attempted to add a host to the firewall(clearFirewallHosts)");
	if (!usingSystemFirewall) {
	    firewallHosts.removeAllElements();
	}
    }

    public static void addFirewallHost(String host) {
	checkFirewallMethodAccess("applet attempted to add a host to the firewall");
	if (!usingSystemFirewall) {
	    firewallHosts.addElement(host);
	}
    }

    public static void setDomainType(int domainType) {
	checkFirewallMethodAccess("applet attempted to change the domain type");	

    }

    public static int  nHosts() {
	checkFirewallMethodAccess("applet attempted to inquire about the firewall(nHosts)");
	return firewallHosts.size();
    }

    public static String getFirewallHost(int index) {
	checkFirewallMethodAccess("applet attempted to inquire about the firewall(getFirewallHost)");
	return (String)(firewallHosts.elementAt(index));
    }

    public static boolean setAccessMode(int mode,
					boolean applyToApplets,
					int domainType) {
	checkFirewallMethodAccess("applet attempted to change access mode");

	int oldMode = accessMode;
	boolean success = true;

	switch (mode) {
	  case ACCESS_NONE:
	  case ACCESS_SOURCE:
	  case ACCESS_FIREWALL:
	  case ACCESS_ALL:
	    accessMode = mode;
	    break;
	  default:
	    throw new IllegalArgumentException("illegal access mode");
	}
	switch (domainType) {
	  case DNS_DOMAIN:
	  case NIS_DOMAIN:
	    Firewall.domainType = domainType;
	    break;
	  default:
	    accessMode = oldMode;
	    throw new IllegalArgumentException("illegal domain type");
	}
	restrictApplets = applyToApplets;
	if (sysAccessMode < accessMode) {
	    accessMode = sysAccessMode;
	    success = false;
	}
	if (sysRestrictApplets && !applyToApplets) {
	    restrictApplets = true;
	    success = false;
	}

	return success;
    }

    public static int  getAccessMode() {
	return accessMode;
    }

    public static int  getDomainType() {
	checkFirewallMethodAccess("applet attempted to inquire about the firewall(getDomainType)");
	return domainType;
    }

    public static boolean  getAppletRestriction() {
	return restrictApplets;
    }

    public static boolean verifyAppletLoading(WWWClassLoader wl) {
	if (wl == null) {
	    return false;
	}

	switch (accessMode) {
	  case ACCESS_NONE:
	    return false;
	  case ACCESS_SOURCE:
	    {
		URL	source = Firewall.getSourceURL(wl);

		return source.protocol.equals("file");
	    }
	  case ACCESS_FIREWALL:
	    return isInsideApplet(wl);
	  case ACCESS_ALL:
	    return true;
	  default:
	    return false;
	}
    }

    public static String verifyAccess(String host, int port) {
	Object		loader = Firewall.getClassLoader();
	WWWClassLoader	wLoader;
	URL		source;

	if (loader == null) {
	    return null;
	}
	if (! (loader instanceof WWWClassLoader)) {
	    // don't know what to do about other class loaders right now
	    return "<unknown>";
	}
	wLoader = (WWWClassLoader)loader;

	switch (accessMode) {
	  case Firewall.ACCESS_ALL:
	    return null;
	  case Firewall.ACCESS_SOURCE:
	    {
		source = Firewall.getSourceURL(wLoader);

		if (source.host.equals(host)) {
		    return null;
		}

		if (source.protocol.equals("file")) {
		    return null;
		}
		if (isProxyRequest(host, port) &&
		    isTrustedClass()) {
		    return null;
		}
		InetAddress a;
		InetAddress b;
		try {
		    a = InetAddress.getByName(source.host);
		    b = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
		    return source.toExternalForm();
		}
		
		if (compareAddresses(a, b) == 1) {
		    return null;
		} else {
		    return source.toExternalForm();
		}
	    }
	  case Firewall.ACCESS_FIREWALL:
	    /*
	     * classify the applet as "inside" or "outside":
	     *   if the source of the applet is subsumed by the firewall list,
	     *   then it is an "inside" applet.
	     *   Otherwise, even if the host is unresolvable, classify the
	     *   applet as "outside".
	     * classify the destination as "inside" or "outside":
	     *   if the destination is subsumed by the firewall list,
	     *   then classify it as "inside".
	     *   if the destination can't be resolved to an address,
	     *   classify it as "inside".
	     *   otherwise, classify the destination as "outside".
	     * if the applet is "inside" allow the access. Otherwise,
	     * allow the access only if the destination is "outside".
	     */
	    source = Firewall.getSourceURL(wLoader);

	    if (source.protocol.equals("file")) {
		return null;
	    }

	    if (isInsideApplet(wLoader)) {
		return null;
	    }
	    if (isProxyRequest(host, port) &&
		isTrustedClass()) {
		return null;
	    }
	    if (isInsideDestination(host, port)) {
		return source.toExternalForm();
	    }
	    return null;
	  case Firewall.ACCESS_NONE:
	  default:
	    source = Firewall.getSourceURL(wLoader);
	    return source.toExternalForm();
	}
    }

    public static void writeFirewallHosts() {
	checkFirewallMethodAccess("applet attempted to overwrite firewall hosts");
	int			len = firewallHosts.size();

	if (len > 0) {
	    String path = System.getenv("HOME") + File.separator +
		".hotjava" + File.separator + "firewall_hosts";
	    FileOutputStream	outStr;
	    PrintStream		pStr;

	    pStr = new PrintStream(outStr=new FileOutputStream(path));
	    for (int i=0; i < len; i++) {
		pStr.println(firewallHosts.elementAt(i));
	    }
	    outStr.close();
	}	
    }

    private static boolean readHostsFrom(String dir) {
	String path = dir + File.separator + "firewall_hosts";

	FileInputStream		inStr;
	DataInputStream		pStr;
	Vector			v = new Vector();
	String			host;

	try {
	    pStr = new DataInputStream(inStr=new FileInputStream(path));
	    while ((host = pStr.readLine()) != null) {
		if (host.length() > 1) {
		    v.addElement(host.toLowerCase());
		}
	    }
	    inStr.close();
	} catch (Exception e) {
	    return false;
	}
	firewallHosts = v;
	return true;
    }

    public static boolean readFirewallHosts() {
	checkFirewallMethodAccess("applet attempted to read firewall hosts");

	if (readHostsFrom(System.getenv("HOTJAVA_HOME") +
			  File.separator + "security_config")) {
	    usingSystemFirewall = true;
	    return true;
	}
	if (readHostsFrom(System.getenv("HOME") +
			  File.separator + ".hotjava")) {
	    return true;
	}

	return false;
    }

    public static void writeAccessMode() {
	checkFirewallMethodAccess("applet attempted to write access mode");

	String path = System.getenv("HOME") + File.separator +
	    ".hotjava" + File.separator + "access_mode";
	FileOutputStream	outStr;
	PrintStream		pStr;
	pStr = new PrintStream(outStr=new FileOutputStream(path));
	switch (accessMode) {
	  case ACCESS_NONE:
	  default:
	    pStr.println("ACCESS_NONE");
	    break;
	  case ACCESS_SOURCE:
	    pStr.println("ACCESS_SOURCE");
	    break;
	  case ACCESS_FIREWALL:
	    pStr.println("ACCESS_FIREWALL");
	    break;
	  case ACCESS_ALL:
	    pStr.println("ACCESS_ALL");
	    break;
	}
	pStr.println(restrictApplets);
	switch (domainType) {
	  case DNS_DOMAIN:
	  default:
	    pStr.println("DNS_DOMAIN");
	    break;
	  case NIS_DOMAIN:
	    pStr.println("NIS_DOMAIN");
	    break;
	}
	outStr.close();
    }

    public static boolean readAccessModeFrom(String dir, boolean isSystem) {
	String path = dir + File.separator + "access_mode";

	FileInputStream		inStr;
	DataInputStream		pStr;
	String			input;
	int			newMode, newDomain;
	boolean			newAppRestriction;
	int			mode = (isSystem) ? sysAccessMode : accessMode;

	try {
	    pStr = new DataInputStream(inStr=new FileInputStream(path));
	    input = pStr.readLine();
	    if (input.equals("ACCESS_NONE")) {
		newMode = ACCESS_NONE;
	    } else if (input.equals("ACCESS_SOURCE")) {
		newMode = ACCESS_SOURCE;
	    } else if (input.equals("ACCESS_FIREWALL")) {
		newMode = ACCESS_FIREWALL;
	    } else if (input.equals("ACCESS_ALL")) {
		newMode = ACCESS_ALL;
	    } else {
		newMode = ACCESS_NONE;
	    }
	    input = pStr.readLine();
	    newAppRestriction = input.equals("true");
	    input = pStr.readLine();
	    if (input.equals("DNS_DOMAIN")) {
		newDomain = DNS_DOMAIN;
	    } else if (input.equals("NIS_DOMAIN")) {
		newDomain = NIS_DOMAIN;
	    } else {
		newDomain = NIS_DOMAIN;
	    }
	    setAccessMode(newMode,
			  newAppRestriction,
			  newDomain);
	    if (isSystem) {
		sysAccessMode = accessMode;
		sysRestrictApplets = restrictApplets;
	    }
	    inStr.close();
	} catch (Exception e) {
	    if (isSystem) {
		sysAccessMode = mode;
	    } else {
		accessMode = mode;
	    }
	    return false;
	}
	return true;
    }

    public static boolean readAccessMode() {
	checkFirewallMethodAccess("applet attempted to read access mode");
	boolean success = false;

	success = readAccessModeFrom(System.getenv("HOTJAVA_HOME") +
				     File.separator + "security_config",
				     true);

	success = readAccessModeFrom(System.getenv("HOME") +
				     File.separator + ".hotjava",
				     false);

	if (sysAccessMode < accessMode) {
	    accessMode = sysAccessMode;
	}
	if (sysRestrictApplets) {
	    restrictApplets = true;
	}

	return success;
    }
}

