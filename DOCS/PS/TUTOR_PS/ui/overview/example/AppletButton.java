/*
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
import java.awt.*;
import java.util.*;
import java.applet.Applet;

public class AppletButton extends Applet implements Runnable {
    int frameNumber = 1;
    String windowClass = null;
    String buttonText = null;
    String windowText = null;
    Button button = null;
    Thread windowThread = null;
    Label label = null;
    boolean pleaseCreate = false;

    public void init() {
	windowClass = getParameter("WINDOWTYPE");
	if (windowClass == null) {
	    windowClass = "TestWindow";
	}

	buttonText = getParameter("BUTTONTEXT");
	if (buttonText == null) {
	    buttonText = "Click here to bring up a " + windowClass;
	}

	windowText = getParameter("WINDOWTEXT");
	if (windowText == null) {
	    windowText = windowClass;
	}

	setLayout(new GridLayout(2,0));
	add(button = new Button(buttonText));
        button.setFont(new Font("Helvetica", Font.PLAIN, 14));

	add(label = new Label("", Label.CENTER));
    }

    public void start() {
	if (windowThread == null) {
	    windowThread = new Thread(this, windowClass + " Bringup Thread");
	    windowThread.start();
	}
    }

    public synchronized void run() {
	Class windowClassObject = null;
	Class tmp = null;
	String name = null;
	
	// Make sure the window class exists.
	// This has the added benefit of pre-loading the class,
	// which makes it much quicker for the first window to come up.
	try {
	    windowClassObject = Class.forName(windowClass);
	} catch (Exception e) {
	    // The specified class isn't anywhere that we can find.
	    label.setText("Can't create window: Couldn't find class "
	    		  + windowClass);
	    button.disable();
	}

	// Make sure the class is a Frame.
	for (tmp = windowClassObject, name = tmp.getName();
	     !( name.equals("java.lang.Object") ||
	        name.equals("java.awt.Frame") ); ) {
	    tmp = tmp.getSuperclass();
	    name = tmp.getName();
	}
	if ((name == null) || name.equals("java.lang.Object")) {
	    //We can't run; ERROR; print status, never bring up window
	    label.setText("Can't create window: "
	    		  + windowClass +
			  " isn't a Frame subclass.");
	    button.disable();
	} else if (name.equals("java.awt.Frame")) { 

	    //Everything's OK. Wait until we're asked to create a window.
	    while (windowThread != null) {
	        while (pleaseCreate == false) {
		    try {
		        wait();
		    } catch (InterruptedException e) {
		    }
	        }

	        //We've been asked to bring up a window.
	        pleaseCreate = false;
	        Frame window = null;
	        try {
	            window = (Frame)windowClassObject.newInstance();
	        } catch (Exception e) {
		    label.setText("Couldn't create instance of class "
			          + windowClass);
	        }
		if (frameNumber == 1) {
	            window.setTitle(windowText);
		} else {
	            window.setTitle(windowText + ": " + frameNumber);
		}
	        frameNumber++;
	        window.pack();
	        window.show();
	        label.setText("");
	    }
	}
    }

    public void stop() {
        windowThread.stop();
        windowThread = null;
    }
		
    public synchronized boolean action(Event event, Object what) {
	if (event.target instanceof Button) {
	    //signal the window thread to build a window
	    label.setText("Please wait while the window comes up...");
	    pleaseCreate = true;
	    notify();
	} 
	return false;
    }
}

class TestWindow extends Frame {
    public TestWindow() {
	resize(300, 300);
    }
}
