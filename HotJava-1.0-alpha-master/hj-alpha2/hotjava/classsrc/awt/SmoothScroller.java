/*
 * @(#)SmoothScroller.java	1.10 95/02/16 Jonathan Payne
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

package awt;


/**
 * SmootherScroller is a thread which calls back Scrollable
 * clients to generate a real-world feel to smoother scrolling.
 * This model includes applying thrust in one direction or
 * another and friction.  A variable amount of thrust can be
 * applied for a variable amount of time.  A notion of braking is
 * included, by temporarily upping the friction.
 *
 * Units of acceleration are in pixels/sec/sec.  Velocity is in
 * pixels/sec.  The constants below probably should not be
 * constants and rather should be configurable for each instance.
 * These constants work well for a text widget that calls the
 * thrust method with particular values.  Get my point?
 *
 * This thread is a daemon thread, so if all other user threads
 * die, this one will too.
 *
 * @version 1.10 16 Feb 1995
 * @author Jonathan Payne
 */
public class SmoothScroller extends Thread {
    final float	initFriction = 400;
    final float	brakeFriction = 1500;
    final float	maxVelocity = 1000;

    awt.Scrollable	client;			    /* client we're scrolling */
    float	friction = initFriction;    /* current friction */
    float	velocity;		    /* current velocity */
    float	thrust;			    /* current thrust */
    int		velocitySgn;		    /* sign (1, -1) of velocity at last thrust */
    int		thrustTime;		    /* time limit to apply thrust */
    int		brakeTime;		    /* time limit to apply brakes */
    int		lastTime;		    /* last time through loop (for dt calculation) */

    public SmoothScroller(awt.Scrollable client) {
	this.client = client;
	setDaemon(true);
	setPriority(Thread.currentThread().getPriority() + 1);
	start();
	Thread.currentThread().yield();
    }

    int sgn(float n) {
	return n < 0 ? -1 : 1;
    }

    synchronized void relax() {
	velocity = 0;
	wait();
	lastTime = System.nowMillis() - 100;
    }

    public synchronized void setThrust(int t, int millis) {
	thrust = t;
	thrustTime = System.nowMillis() + millis;
	if (velocity == 0)
	    velocitySgn = sgn(t);
	else
	    velocitySgn = sgn(velocity);
	notify();
    }

    public void brake(int millis) {
	brakeTime = System.nowMillis() + millis;
	friction = brakeFriction;
    }

    void callback(float dist) {
	if (client.scrollVertically((int)-dist)) {
	    velocity = -1 * velocitySgn;    /* this stops us in our tracks */
	}
    }

    public void run() {
	relax();
	while (true) {
	    int	    now = System.nowMillis();
	    float   dt = (now - lastTime) / 1000.0;

	    velocity += (thrust * dt);
	    if (velocity < 0) {
		velocity += (friction * dt);
		if (velocity > 0)
		    velocity = 0;
	    } else {
		velocity -= (friction * dt);
		if (velocity < 0)
		    velocity = 0;
	    }
	    if (Math.abs(velocity) > maxVelocity)
		velocity = velocitySgn * maxVelocity;
	    callback(velocity * dt);
	    lastTime = now;
	    if (now >= thrustTime)
		thrust = 0;
	    if (now >= brakeTime)
		friction = initFriction;
	    if (now >= thrustTime && velocity == 0) {
		relax();
	    } else {
		sleep(50);
	    }
	}
    }
}
