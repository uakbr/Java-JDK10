/*
 * @(#)BounceDuke.java	1.13 95/01/31 Jonathan Payne
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

import java.util.Hashtable;
import java.io.*;
import awt.*;
import net.www.html.*;
import browser.*;
import browser.audio.*;

class BounceImage implements Runnable {
    static Image  bounceimages[] = new Image[7];
    static int maxWidth;
    static int maxHeight;
    static float inelasticity = .96;
    static float Ax = 0.0;
    static float Ay = 0.0002;
    static float Ar = 0.9;

    public float x = 0;
    public float y = 0;
    public int width;
    public int height;
    public float Vx = 0.1;
    public float Vy = 0.05;
    public int index;
    public float Vr = 0.005 + (float)Math.random() * 0.001;
    public float findex = 0;

    BounceDuke	parent;
    static boolean  imagesReadIn = false;

    public void play(int n) {
	parent.play(parent.sounds[n]);
    }

    private void initializeImage(int which) {
	//URL url = new URL(parent.appletURL, "images/coke/" + (which<10?"0":"") + which + ".gif");
	URL url = new URL(parent.appletURL, "images/dance/T" + (which+1) + ".gif");
	Image img = bounceimages[which] = parent.getImage(url);
	System.out.println("URL = " + url.toExternalForm());

	if (img.width > maxWidth) {
	    maxWidth = img.width;
	}
	if (img.height > maxHeight) {
	    maxHeight = img.height;
	}
    }

    public void run() {
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
	int i;

	for (i = 0; i < bounceimages.length; i++) {
	    initializeImage(i);
	}
    }

    public BounceImage(BounceDuke parent) {
	this.parent = parent;
	if (!imagesReadIn) {
	    imagesReadIn = true;
	    initializeImage(0);
	    (new Thread(this)).start();
	}
	this.parent = parent;
	resize(bounceimages[0].width + 2, bounceimages[0].height + 2);
    }

    public void move(float x1, float y1) {
	x = x1;
	y = y1;
    }

    public void resize(int w, int h) {
	width = w;
	height = h;
    }

    public void paint(Graphics g) {
	int i = index;

	if (bounceimages[i] == null) {
	    i = 0;
	}
	g.drawImage(bounceimages[i], (int)x, (int)y);
    }

    public void step(long deltaT) {
	boolean	collision_x = false;
	boolean	collision_y = false;

	float jitter = (float)Math.random() * .01 - .005;

	if (width < maxWidth || height < maxHeight) {
	    resize(maxWidth, maxHeight);
	}
	x += Vx * deltaT + (Ax / 2.0) * deltaT * deltaT;
	y += Vy * deltaT + (Ay / 2.0) * deltaT * deltaT;
	if (x <= 0.0) {
	    x = 0.0;
	    Vx = -Vx * inelasticity + jitter;
	    collision_x = true;
	    play((int)(Math.random() * 3));
	}
	if (x + width >= parent.width) {
	    x = parent.width - width;
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
	if (y + height >= parent.height) {
	    y = parent.height - height;
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
	    findex += bounceimages.length;
	}
	index = ((int) findex) % bounceimages.length;

    }
}

class BounceDuke extends Applet implements Runnable {
    boolean images_initialized = false;
    BounceImage images[];

    boolean time_to_die;
    InputStream music;
    AudioData sounds[];

    void makeImages(int nimages) {
	BounceImage	img;
	int i;

	images = new BounceImage[nimages];
	for (i = 0; i < nimages; i++) {
	    images[i] = img = new BounceImage(this);

	    img.move(1 + img.width * .8 * (i % 3) + i / 3 * .3 * img.width,
		     img.height * .3 + (i % 3) * .3 * img.height);
	}

	sounds = new AudioData[4];
	sounds[0] = getAudioData("audio/bubble1.au");
	sounds[1] = getAudioData("audio/bong.au");
	sounds[2] = getAudioData("audio/gong.au");
	sounds[3] = getAudioData("audio/train.au");
	music = getContinuousAudioStream(new URL(appletURL, "audio/spacemusic.au"));
    }

    public void run() {
	long lasttime;

	if (images == null) {
	    System.out.println("Making images ...");
	    makeImages(5);
	}

	startPlaying(music);
	Thread.sleep(1000);
	lasttime = System.nowMillis();
	while (!time_to_die) {
	    int i;
	    long now = System.nowMillis();
	    long deltaT = now - lasttime;
	    boolean active = false;

	    for (i = 0; i < images.length; i++) {
		BounceImage	    img = images[i];

		img.step(deltaT);
		if (img.Vy > .05 || -img.Vy > .05 || img.y + BounceImage.maxHeight < height - 10) {
		    active = true;
		}
	    }
	    if (!active && images.length != 0) {
		for (i = 0; i < images.length; i++) {
		    BounceImage img = images[i];

		    img.Vx = (float)Math.random() / 4. - 0.125;
		    img.Vy = -(float)Math.random() / 4. - 0.2;
		    img.Vr = 0.05 - (float)Math.random() * 0.1;
		}
		play(sounds[3]);
	    }
	    repaint();
	    lasttime = now;
	    Thread.sleep(100);
	}
    }

    public void init() {
	if ((width <= 100) || (height <= 100)) {
	    resize(500, 300);
	}
    }

    public void start() {
	time_to_die = false;
	(new Thread(this)).start();
    }

    public void stop() {
	time_to_die = true;
	stopPlaying(music);
    }

    public void paint(Graphics g) {
	g.setForeground(Color.gray);
	g.drawRect(0, 0, width - 1, height - 1);
	if (images != null) {
	    for (int i = 0; i < images.length; i++) {
		images[i].paint(g);
	    }
	}
    }
}
