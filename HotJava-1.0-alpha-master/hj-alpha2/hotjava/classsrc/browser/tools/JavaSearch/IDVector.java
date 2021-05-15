/*
 * @(#)IDVector.java	1.10 95/03/14 David A. Brown
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

package browser.tools.JavaSearch;

/** 
 *  Vector of JavaSearch Doc IDs.  Doc IDs are chars.
 *  There are 'count' valid IDs in ids[].  The IDs in ids[]
 *  are always guaranteed to be in ascending order.
 *
 *  This class implements some basic functions 
 *  performed on lists of Doc IDs by the JavaSearch indexer
 *  and searcher.
 *  
 */
public class IDVector {

    /** Array of IDs */
    public char ids[];
    
    /** Current Number of IDs in the array */
    public int count = 0;

    /** Basic IDVector constructor */
    public IDVector() {
        ids = new char[64];
    }
 
    /** Make sure we have room to hold at least 'minimumCapacity' IDs */
    private synchronized void ensureCapacity(int minimumCapacity) {
        int maxCapacity = ids.length;
 
        if (minimumCapacity > maxCapacity) {
            int newCapacity = (maxCapacity + 1) * 2;
            if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }
 
            char newIDs[] = new char[newCapacity];
            System.arraycopy(ids, 0, newIDs, 0, count);
            ids = newIDs;
        }
    }
 
    /** 
     *  Append the specified Doc ID to our array of doc IDs.
     * 
     *  Note that any given Doc ID should only appear once
     *  in our array!  But since the indexer processes only one
     *  doc at a time, all we have to do is check 'id' against
     *  the *most recent* addition to our array.  If the specified
     *  id is equal to the id we last added, just do nothing.
     */
    public synchronized void appendID(char id) {
	if ((count > 0) && (ids[count-1] == id)) {
	    return;
	}
	ensureCapacity(count + 1);
	ids[count++] = id;
	//System.out.println("  appendDocId was successful: "+this);
    }

    /** Clear this IDVector; reset it to a count of zero. */
    public void clear() {
	count = 0;
    }


    //
    // Methods implementing boolean "Doc ID list merging".
    // These are how the Search engine implements AND, OR and NOT.
    //

    /**
     * Intersect this IDVector with another IDVector.
     * This IDVector is modified to contain only IDs
     * found in BOTH this and aVector.
     *
     * This vector's count is guaranteed to become at least
     * as small as min(count, aVector.count), ie. it certainly
     * will NOT become any larger.
     */
    public void intersectWith(IDVector aVector) {

	//System.out.println("IDVector.intersectWith...");
	//System.out.println("  this IDVector is: "+this);
	//System.out.println("        aVector is: "+aVector);

	// Allocate an array as large as we might possibly need
	char newids[] = new char[Math.min(count, aVector.count)];
	int newcount = 0;

	// Our IDs are in ids[], from 0 to count-1.
	// IDs to merge in are in aVector.ids[], from 0 to aVector.count-1.
	int aa = 0;
	int bb = 0;
	while (aa<count && bb<aVector.count) {

	    // Check both ID lists.  If both lists have the same
	    // ID, add it to newids.  Otherwise, advance the pointer
	    // of whichever list had the lower ID.
	    if (ids[aa] == aVector.ids[bb]) {
		newids[newcount++] = ids[aa++];
                bb++;
	    }
	    else if (ids[aa] < aVector.ids[bb]) {
		aa++;
	    }
	    else {  // ids[aa] > aVector.ids[bb]
		bb++;
	    }
	}

	// Done merging.  Install newids[] and newcount.
	ids = newids;
	count = newcount;
	//System.out.println("After INTERSECT, this IDVector is: "+this);
    }

    /**
     * Intersect this IDVector with the OPPOSITE of another IDVector.
     * This IDVector is modified to contain only IDs
     * found in this vector and NOT in aVector.
     *
     * This vector's count is guaranteed to NOT become any larger.
     */
    public void intersectWithNot(IDVector aVector) {

	//System.out.println("IDVector.intersectWithNot...");
	//System.out.println("  this IDVector is: "+this);
	//System.out.println("        aVector is: "+aVector);

	// Allocate an array as large as we might possibly need
	char newids[] = new char[count];
	int newcount = 0;

	// Our IDs are in ids[], from 0 to count-1.
	// IDs to NOT with are in aVector.ids[], from 0 to aVector.count-1.
	int aa = 0;
	int bb = 0;
	while (aa<count) {

	    // Make sure bb is pointing at the next possible conflict:
	    //   aVector.ids[bb] must be >= ids[aa]
	    while ((aVector.ids[bb]<ids[aa]) && (bb < aVector.count)) {
		bb++;
	    }
		   
	    if (bb == aVector.count) {
		// Ran out of entries in aVector.  Just take our IDs.
		newids[newcount++] = ids[aa++];
	    }
	    else {
		// If aVector.ids[bb] == ids[aa], DON'T include
		//   it in the result.  Otherwise, use one more
		//   entry from ids[].
		if (aVector.ids[bb] == ids[aa]) {
		    aa++;
		    bb++;
		}
		else {
		    newids[newcount++] = ids[aa++];
		}
	    }
	}

	// Done merging.  Install newids[] and newcount.
	ids = newids;
	count = newcount;
	//System.out.println("After NOT, this IDVector is: "+this);
    }

    /**
     * Union this IDVector with another IDVector.
     * This IDVector is modified to contain IDs
     * found in EITHER this or aVector.
     *
     * This vector's count may become larger than it was,
     * and may in fact become as large as (count + aVector.count).
     */
    public void unionWith(IDVector aVector) {
	
	//System.out.println("IDVector.unionWith...");
	//System.out.println("  this IDVector is: "+this);
	//System.out.println("        aVector is: "+aVector);

	// Allocate an array as large as we might possibly need
	char newids[] = new char[count + aVector.count];
	int newcount = 0;

	// Our IDs are in ids[], from 0 to count-1.
	// IDs to merge in are in aVector.ids[], from 0 to aVector.count-1.
	int aa = 0;
	int bb = 0;
	while (aa<count || bb<aVector.count) {

	    if (aa>=count) {
		// Hit end of our IDs, just take entries from aVector
		newids[newcount++] = aVector.ids[bb++];
	    }
	    else if (bb>=aVector.count) {
		// Hit end of aVector's IDs, just take entries from ids[]
                newids[newcount++] = ids[aa++];
            }
	    else {
		// Still processing both ID lists.  Take whichever's lower.
		if (ids[aa] < aVector.ids[bb]) {
		    newids[newcount++] = ids[aa++];
		}
		else if (ids[aa] > aVector.ids[bb]) {
		    newids[newcount++] = aVector.ids[bb++];
		}
		else {
		    // Both lists had the same ID!
		    newids[newcount++] = ids[aa++];
		    bb++;
		}
	    }

	}

	// Done merging.  Install newids[] and newcount.
	ids = newids;
	count = newcount;
	//System.out.println("After UNION, this IDVector is: "+this);
    }

    /** Generate a simple printed representation of this IDVector */
    public String toString() {
	String s = "IDVector ["+count+"]:";
	for (int i=0; i<count; i++) {
	    s += " " + (int)ids[i];
	}
	return s;
    }







}
