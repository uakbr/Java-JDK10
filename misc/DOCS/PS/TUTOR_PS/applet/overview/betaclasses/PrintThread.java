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

public class PrintThread extends java.applet.Applet {

    TextField field = new TextField(80);

    public void init() {
	field.setEditable(false);
	setLayout(new BorderLayout()); //workaround for chopped off sides
	add("Center", field);  
	show();
        addItem("init:" + Thread.currentThread().getName() + " ");
    }

    public void start() {
        addItem("start:" + Thread.currentThread().getName() + " ");
    }

    public void stop() {
        addItem("stop:" + Thread.currentThread().getName() + " ");
    }

    public void destroy() {
        addItem("destroy:" + Thread.currentThread().getName() + " ");
    }

    public void addItem(String newWord) {
	String t = field.getText();
	System.out.println(newWord);
	field.setText(t + newWord);
	field.repaint();
    }

    public synchronized void paint(Graphics g) {
        addItem("paint:" + Thread.currentThread().getName() + " ");
    }
}
