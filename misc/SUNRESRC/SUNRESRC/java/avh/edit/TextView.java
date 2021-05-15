/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)TextView.java	1.8 94/07/23 Feb 1994
 *
 *	Arthur van Hoff, Nov 1993
 *	Arthur van Hoff, March 1994
 */

package edit;

/**
 * These operations are used to notify a text view
 * when changes occur to a Text object.
 */

public interface TextView {
    abstract void notifyInsert(int pos, int len);
    abstract void notifyDelete(int pos, int len);
    abstract void notifyChange(int pos, int len);
}
