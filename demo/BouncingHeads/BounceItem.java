/*
 * %W% %E%
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

import java.util.Hashtable;
import java.applet.*;
import java.io.*;
import java.awt.*;
import java.net.*;

/**
 * @author	Jonathan Payne
 * @version 	%I%, %G%
 */

class BounceImage {
    static float inelasticity = .96f;
    static float Ax = 0.0f;
    static float Ay = 0.0002f;
    static float Ar = 0.9f;

    public float x = 0;
    public float y = 0;
    public int width;
    public int height;
    public float Vx = 0.1f;
    public float Vy = 0.05f;
    public int index;
    public float Vr = 0.005f + (float)Math.random() * 0.001f;
    public float findex = 0f;

    BounceItem	parent;
    static boolean  imagesReadIn = false;

    public void play(int n) {
	if (parent.sounds[n] != null) {
	    parent.sounds[n].play();
	}
    }

    public BounceImage(BounceItem parent) {
	this.parent = parent;
	width = 65;
	height = 72;
    }

    public void move(float x1, float y1) {
	x = x1;
	y = y1;
    }

    public void paint(Graphics g) {
	int i = index;

	if (parent.bounceimages[i] == null) {
	    i = 0;
	}
	g.drawImage(parent.bounceimages[i], (int)x, (int)y, null);
    }

    public void step(long deltaT) {
	boolean	collision_x = false;
	boolean	collision_y = false;

	float jitter = (float)Math.random() * .01f - .005f;

	x += Vx * deltaT + (Ax / 2.0) * deltaT * deltaT;
	y += Vy * deltaT + (Ay / 2.0) * deltaT * deltaT;
	if (x <= 0.0f) {
	    x = 0.0f;
	    Vx = -Vx * inelasticity + jitter;
	    collision_x = true;
	    play((int)(Math.random() * 3));
	}
	Dimension d = parent.size();
	if (x + width >= d.width) {
	    x = d.width - width;
	    Vx = -Vx * inelasticity + jitter;
	    collision_x = true;
	    play((int)(Math.random() * 3));
	}
	if (y <= 0) {
	    y = 0;
	    Vy = -Vy * inelasticity + jitter;
	    collision_y = true;
	    play((int)(Math.random() * 3));
	}
	if (y + height >= d.height) {
	    y = d.height - height;
	    Vx *= inelasticity;
	    Vy = -Vy * inelasticity + jitter;
	    collision_y = true;
	}
	move(x, y);
	Vy = Vy + Ay * deltaT;
	Vx = Vx + Ax * deltaT;

	findex += Vr * deltaT;
	if (collision_x || collision_y) {
	    Vr *= Ar;
	}

	while (findex <= 0.0) {
	    findex += parent.bounceimages.length;
	}
	index = ((int) findex) % parent.bounceimages.length;

    }
}

public class BounceItem extends Applet implements Runnable {
    boolean images_initialized = false;
    BounceImage images[];

    boolean time_to_die;
    AudioClip music;
    AudioClip sounds[];
    Image bounceimages[];

    public BounceItem() {
    }

    void makeImages(int nimages) {

	bounceimages = new Image[8];
	for (int i = 1 ; i <= 8 ; i++) {
	    bounceimages[i-1] = getImage(getCodeBase(), "images/jon/T" + i + ".gif");
	    //System.out.println("d = " + bounceimages[i-1].getWidth() + "," + bounceimages[i-1].getHeight());
	}

	images = new BounceImage[nimages];
	for (int i = 0; i < nimages; i++) {
	    BounceImage	img = images[i] = new BounceImage(this);
	    img.move(1 + img.width * .8f * (i % 3) + i / 3 * .3f * img.width,
		     img.height * .3f + (i % 3) * .3f * img.height);
	}

	sounds = new AudioClip[4];
	sounds[0] = getAudioClip(getCodeBase(), "audio/ooh.au");
	sounds[1] = getAudioClip(getCodeBase(), "audio/ah.au");
	sounds[2] = getAudioClip(getCodeBase(), "audio/dah.au");
	sounds[3] = getAudioClip(getCodeBase(), "audio/gong.au");
	music = getAudioClip(getCodeBase(), "audio/spacemusic.au");
    }

    public void run() {
	long lasttime;

	try {
	    if (images == null) {
		System.out.println("Making images ...");
		makeImages(4);
	    }

	    if (music != null) {
		music.loop();
	    }
	    lasttime = System.currentTimeMillis();
	    while (!time_to_die) {
		int i;
		long now = System.currentTimeMillis();
		long deltaT = now - lasttime;
		boolean active = false;
		Dimension d = size();

		for (i = 0; i < images.length; i++) {
		    BounceImage	    img = images[i];

		    img.step(deltaT);
		    if (img.Vy > .05 || -img.Vy > .05 || img.y + img.width < d.height - 10) {
			active = true;
		    }
		}
		if (!active && images.length != 0) {
		    for (i = 0; i < images.length; i++) {
			BounceImage img = images[i];

			img.Vx = (float)Math.random() / 4.0f - 0.125f;
			img.Vy = -(float)Math.random() / 4.0f - 0.2f;
			img.Vr = 0.05f - (float)Math.random() * 0.1f;
		    }
		    if (sounds[3] != null) {
			sounds[3].play();
		    }
		}
		repaint();
		lasttime = now;
		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) {
		    return;
		}
	    }
	} finally {
	    if (music != null) {
		music.stop();
	    }
	}
	    
    }

    public void init() {
	Dimension d = size();
	if ((d.width <= 100) || (d.height <= 100)) {
	    resize(500, 300);
	}
    }

    public void start() {
	time_to_die = false;
	(new Thread(this)).start();
    }

    public void stop() {
	time_to_die = true;
	music.stop();
    }

    public void paint(Graphics g) {
	Dimension d = size();
	g.setColor(Color.gray);
	g.drawRect(0, 0, d.width - 1, d.height - 1);
	if (images != null) {
	    for (int i = 0; i < images.length; i++) {
		if (images[i] != null) {
		    images[i].paint(g);
		}
	    }
	}
    }
}
