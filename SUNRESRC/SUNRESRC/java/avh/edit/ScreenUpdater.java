/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)ScreenUpdater.java	1.14 94/07/19 Feb 1994
 *
 *	Arthur van Hoff, Feb 1994
 */

package edit;

/**
 * A seperate low priority thread that warns clients
 * when they need to update the screen. Clients that
 * need a wakeup call need to call Notify().
 */
public class ScreenUpdater extends Thread {
    UpdateClient clients[];
    int nclients;
    boolean updated;

    /**
     * The screen updater. There should be only
     * one of these.
     */
    public static ScreenUpdater updater = new ScreenUpdater();

    /**
     * Constructor. Starts the thread.
     */
    ScreenUpdater() {
	clients = new UpdateClient[10];
	nclients = 0;
	updated = false;
	start();
    }

    /**
     * Update the next client
     */
    synchronized UpdateClient nextClient() {
	UpdateClient client = null;
	if (nclients > 0) {
	    client = clients[0];
	    nclients--;
	    System.arraycopy(clients, 1, clients, 0, nclients);
	}
	return client;
    }

    /**
     * The main body of the screen updater.
     */
    public void run() {
	int i;
	
	setName("ScreenUpdater");
	setPriority(1);

	while (true) {
	    waitForUpdate();

	    UpdateClient client = nextClient();
	    while (client != null) {
		try {
		    client.update();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		client = nextClient();
	    }
	}
    }

    /**
     * Wait for an update
     */
    synchronized void waitForUpdate() {
	while (!updated) {
	    wait();
	}
	updated = false;
    }

    /**
     * Notify the screen updater that a client needs
     * updating. As soon as the screen updater is
     * scheduled to run it will ask all of clients that
     * need updating to update the screen.
     */
    public synchronized void notify(UpdateClient client) {
	int i;
	
	if (!updated) {
	    updated = true;
	    super.notify();
	}
	for (i = 0 ; i < nclients ; i++) {
	    if (clients[i] == client) {
		return;
	    }
	}
	if (nclients >= clients.length) {
	    UpdateClient newclients[] = new UpdateClient[clients.length * 2];
	    System.arraycopy(clients, 0, newclients, 0, nclients);
	    clients = newclients;
	}
	clients[nclients++] = client;
    }
}
