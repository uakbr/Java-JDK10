/*
 * @(#)SecurityDialog.java	1.16 95/05/11 Sami Shaio
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
package browser;

import awt.*;
import net.Firewall;
import java.util.Vector;

public class SecurityDialog extends Frame {
    private TextArea	text;
    OptionMenu		opt;
    OptionMenu		domainOpt;
    public static Font	dialogFont;
    Label		status;
    private String	securityBlurb = "\
HotJava allows executable content to be loaded\n\
across the net. This greatly expands the capabilities\n\
of a web browser. However, without any precautions, this\n\
feature could introduce security problems.\n\
\n\
HotJava has been written with these problems in \n\
mind and allows you to restrict the capabilities\n\
of incoming executable content.\n\
\n\
For a full explanation of the security mechanisms\n\
in HotJava and how to configure them using this\n\
dialog, please select the Help button below.\n\
\n\
=>If this is the first time this dialog has come up,\n\
=>hitting Apply leads to a safe default.";

    hotjava		target;
    Toggle	restrictApplets;

    public SecurityDialog(hotjava f) {
	super(f.wServer, true, false, f, 433, 460, Color.lightGray);

	setTitle("HotJava Security Dialog");
	target = f;
	Window cw = new Window(this, "Center", background, 300, 300);

	dialogFont = f.wServer.fonts.getFont("Dialog", Font.BOLD, 12);
	setDefaultFont(dialogFont);
	cw.setLayout(new ColumnLayout(false));
	Row	r = new Row(cw, null, true);
	text = new TextArea(r, null, null, 50, 15);
	text.setText(securityBlurb);
	text.setEditable(false);
	r = new Row(cw, null, true);
	new Label("Enter desired security mode:", null, r);
	r = new Row(cw, null, true);
	opt = new SecurityOptions(r, null, this);

	r = new Row(cw, null, true);
	restrictApplets = new SecurityToggle(r,
					     "Apply security mode to applet loading",
					     this);
	
	r = new Row(cw, null, true);
	new Label("Enter the kind of domain you're using:", null, r);
	r = new Row(cw, null, true);
	domainOpt = new OptionMenu(r, null, null);
	domainOpt.addItem("DNS");
	domainOpt.addItem("NIS");
	domainOpt.select(Firewall.getDomainType());

	r = new Row(cw, null, true);
	new FirewallButton(r, this, target);

	r = new Row(cw, null, true);
	new SecurityApplyButton(r, this);
	new SecurityDismissButton(r, this);
	new SecurityHelpButton(r, target);

	r = new Row(cw, null, false);
	status = new Label("Ready.", null, r);
	status.reshape(0, 0, 400, status.height);
	reset();
    }

    public void reset() {
	opt.select(Firewall.getAccessMode());
	restrictApplets.setState(Firewall.getAppletRestriction());
	opt.selected(opt.selectedIndex);
    }
}

class SecurityToggle extends Toggle {
    SecurityDialog target;

    public SecurityToggle(Container r, String label, SecurityDialog f){
	super(label, null, r, null, false);
	target = f;
	setState(Firewall.getAppletRestriction());
    }

    public void selected() {
	target.opt.selected(target.opt.selectedIndex);
    }
}

class SecurityOptions extends OptionMenu {
    SecurityDialog target;

    public SecurityOptions(Container r, String label, SecurityDialog  f) {
	super(r, label, null);
	target = f;
	// Note the order of items in this OptionMenu is the
	// same as the definition of the ACCESS_* constants
	// in net/Firewall.java
	addItem("No access");
	addItem("Applet host");
	addItem("Firewall");
	addItem("Unrestricted");

	select(Firewall.getAccessMode());
    }

    public void selected(int index) {
	if (target.restrictApplets.getState()) {
	    switch (index) {
	      case Firewall.ACCESS_NONE:
		target.status.setText("No applets will be loaded.");
		break;
	      case Firewall.ACCESS_SOURCE:
		target.status.setText("Only applets from the filesystem will be loaded.");
		break;
	      case Firewall.ACCESS_FIREWALL:
		target.status.setText("Only applets inside the firewall will be loaded.");
		break;
	      case Firewall.ACCESS_ALL:
		target.status.setText("All applets will be loaded.");
		break;
	    }
	} else {
	    switch (index) {
	      case Firewall.ACCESS_NONE:
		target.status.setText("Applets will be not be able to load information.");
		break;
	      case Firewall.ACCESS_SOURCE:
		target.status.setText("Only allow an applet to load info from its host.");
		break;
	      case Firewall.ACCESS_FIREWALL:
		target.status.setText("Outside applets can only load info outside the firewall.");
		break;
	      case Firewall.ACCESS_ALL:
		target.status.setText("Applets can load info from anywhere.");
		break;
	    }
	}
    }
}

    
class FirewallButton extends Button {
    private FirewallDialog fDialog;

    public FirewallButton(Container c, Frame f, hotjava w) {
	super("Configure firewall...", null, c);
	fDialog = new FirewallDialog(f, w);
    }
    public void selected(Component c, int pos) {
	fDialog.map();
	fDialog.resize();
    }
}

class FirewallDialog extends Frame implements ChoiceHandler {
    int		selectedPos = -1;
    List	flist;
    Vector	slist;
    TextField	field;
    hotjava	target;
    OptionMenu	opt;

    public FirewallDialog(Frame f, hotjava w) {
	super(f.wServer, true, false, f, 201, 301, f.background);
	setTitle("Configure Firewall List");

	target = w;
	setDefaultFont(SecurityDialog.dialogFont);
	Window cw = new Window(this, "Center", background, 300, 300);
	cw.setLayout(new ColumnLayout(false));

	slist = new Vector();
	flist = new List(cw, this, null, 10, false, false);
	flist.setHFill(true);
	flist.setVFill(true);
	int len = Firewall.nHosts();
	for (int i = 0; i < len; i++) {
	    String fhost = Firewall.getFirewallHost(i);
	    slist.addElement(fhost);
	    flist.addItem(fhost.substring(1));
	}
	cw = new Window(this, "South", background, 300, 100);
	cw.setLayout(new ColumnLayout(false));
	Row r = new Row(cw, null, true);
	field = new FirewallTextField(r,this);
	field.setHFill(true);
	r = new Row(cw, null, true);
	new AddButton(r, this);
	new ChangeButton(r, this);
	new FirewallDeleteButton(r, this);
	r = new Row(cw, null, true);
	new Label("Entry type:", null, r);
	r = new Row(cw, null, true);
	opt = new OptionMenu(r, null, null);
	opt.addItem("Domain");
	opt.addItem("Host");
	r = new Row(cw, null, true);
	new FirewallApplyButton(r, this);
	new FirewallDismissButton(r, this);
	new FirewallHelpButton(r, target);
    }

    public void reset() {
	if (slist.size() > 0) {
	    slist.removeAllElements();
	    flist.delItems(0, flist.nItems() - 1);

	    int len = Firewall.nHosts();
	    for (int i = 0; i < len; i++) {
		String fhost = Firewall.getFirewallHost(i);
		slist.addElement(fhost);
		flist.addItem(fhost.substring(1));
	    }
	}
	field.setText("");
    }

    public void addHost() {
	flist.addItem(field.getText());
	String s;
	if (opt.selectedIndex == 0) {
	    s = "D" + field.getText();
	    slist.addElement(s);
	} else {
	    s = "H" + field.getText();
	    slist.addElement(s);
	}
    }

    public void doubleClick(Component c, int pos) {
    }

    public void selected(Component c, int pos) {
	String h = (String)(slist.elementAt(pos));
	field.setText(flist.itemAt(pos));
	switch (h.charAt(0)) {
	  case 'D':
	  case 'd':
	    opt.select(0);
	    break;
	  case 'H':
	  case 'h':
	  default:
	    opt.select(1);
	    break;
	}
	    
	selectedPos = pos;
    }
}

class FirewallTextField extends TextField {
    FirewallDialog	target;

    public FirewallTextField(Container c, FirewallDialog f) {
	super("",null,c,true);
	target = f;
    }
    public void selected() {
	target.addHost();
	target.field.setText("");
    }
}

class FirewallDismissButton extends Button {
    Frame	frame;

    public FirewallDismissButton(Container w, Frame f) {
	this(w, f, "Cancel");
    }
    
    public FirewallDismissButton(Container w, Frame f, String label) {
	super(label, null, w);

	frame = f;
    }
    
    public void selected(Component c, int pos) {
	((FirewallDialog)frame).reset();
	frame.unMap();
    }
}

class SecurityDismissButton extends Button {
    Frame	frame;

    public SecurityDismissButton(Container w, Frame f) {
	this(w, f, "Cancel");
    }
    
    public SecurityDismissButton(Container w, Frame f, String label) {
	super(label, null, w);

	frame = f;
    }
    
    public void selected(Component c, int pos) {
	((SecurityDialog)frame).reset();
	frame.unMap();
    }
}


class AddButton extends Button {
    FirewallDialog	target;

    public AddButton(Container w, FirewallDialog f) {
	super("Add", null, w);
	target = f;
    }
    
    public void selected(Component c, int pos) {
	target.addHost();
    }
}


class ChangeButton extends Button {
    FirewallDialog	target;

    public ChangeButton(Container w, FirewallDialog f) {
	super("Change", null, w);
	target = f;
    }
    
    public void selected(Component c, int pos) {
	if (target.selectedPos != -1) {
	    target.flist.delItem(target.selectedPos);
	    target.slist.removeElementAt(target.selectedPos);
	    target.addHost();
	}
    }
}


class FirewallDeleteButton extends Button {
    FirewallDialog	target;

    public FirewallDeleteButton(Container w, FirewallDialog f) {
	super("Delete", null, w);
	target = f;
    }
    
    public void selected(Component c, int pos) {
	if (target.selectedPos != -1) {
	    target.flist.delItem(target.selectedPos);
	    target.slist.removeElementAt(target.selectedPos);
	    target.field.setText("");
	}
    }
}

class FirewallApplyButton extends Button {
    FirewallDialog	target;
    private MessageDialog  mDialog;

    public FirewallApplyButton(Container w, FirewallDialog f) {
	super("Apply", null, w);
	target = f;
	mDialog = new MessageDialog(f,
				    "Error: System Override",
				    "Sorry, can't override the system firewall",
				    MessageDialog.ERROR_TYPE,
				    1,
				    true,
				    "Ok",
				    null,
				    null,
				    null);
    }
    
    public void selected(Component c, int pos) {
	if (Firewall.canOverrideFirewall()) {
	    int len = target.slist.size();
	    Firewall.clearFirewallHosts();
	    for (int i=0;i<len;i++) {
		Firewall.addFirewallHost((String)(target.slist.elementAt(i)));
	    }
	    Firewall.writeFirewallHosts();
	} else {
	    mDialog.show();
	    target.reset();
	}
	target.unMap();
    }
}

class FirewallHelpButton extends Button {
    hotjava	target;

    public FirewallHelpButton(Container w, hotjava f) {
	super("Help", null, w);
	target = f;
    }
    
    public void selected(Component c, int pos) {
	target.go(hotjava.dochome + "FirewallHelp.html");
    }
}


class SecurityApplyButton extends Button {
    SecurityDialog	target;
    private MessageDialog  mDialog;

    public SecurityApplyButton(Container w, SecurityDialog f) {
	super("Apply", null, w);
	target = f;
	mDialog = new MessageDialog(f,
				    "Error: System Override",
				    "Sorry, can't override the system firewall",
				    MessageDialog.ERROR_TYPE,
				    1,
				    true,
				    "Ok",
				    null,
				    null,
				    null);
    }
    
    public void selected(Component c, int pos) {
	// check for a configured firewall list.
	// if none found warn the user
	// otherwise, write the access mode

	if (Firewall.setAccessMode(target.opt.selectedIndex,
				   target.restrictApplets.getState(),
				   target.domainOpt.selectedIndex)) {
	    Firewall.writeAccessMode();
	} else {
	    target.reset();
	    mDialog.show();
	}

	target.unMap();
    }
}

class SecurityHelpButton extends Button {
    hotjava	target;

    public SecurityHelpButton(Container w, hotjava f) {
	super("Help", null, w);
	target = f;
    }
    
    public void selected(Component c, int pos) {
	target.go(hotjava.dochome + "SecurityDialogHelp.html");
    }
}
