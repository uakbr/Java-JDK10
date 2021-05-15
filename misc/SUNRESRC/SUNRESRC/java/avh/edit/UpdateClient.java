/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)UpdateClient.java	1.7 94/07/23 Feb 1994
 *
 *	Arthur van Hoff, Feb 1994
 */

package edit;

/**
 * This interface is used to notify clients
 * that they need to update the screen. The client
 * may ignore the request if nothing has changed.
 */
public interface UpdateClient {
    abstract void update();
}
