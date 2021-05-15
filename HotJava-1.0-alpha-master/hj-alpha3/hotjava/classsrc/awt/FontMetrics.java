/*
 * @(#)FontMetrics.java	1.2 95/04/10 Jim Graham
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

import java.lang.*;
import awt.*;

/** A font metrics object.
 * @version 1.2 10 Apr 1995
 * @author Jim Graham
 */
public class FontMetrics {
    public int widths[];

    /** The font for which these metrics are valid. */
    Font		font;

    /** The standard ascent of the font.  This is the logical height
     * above the baseline for the Alphanumeric characters and should
     * be used for determining line spacing.  Note, however, that some
     * characters in the font may extend above this height.
     */
    public int		ascent;

    /** The standard descent of the font.  This is the logical height
     * below the baseline for the Alphanumeric characters and should
     * be used for determining line spacing.  Note, however, that some
     * characters in the font may extend below this height.
     */
    public int		descent;

    /** The standard leading for the font.  This is the logical amount
     * of space to be reserved between the descent of one line of text
     * and the ascent of the next line.  The height metric is calculated
     * to include this extra space.
     */
    public int		leading;

    /** The standard height of a line of text in this font.  This is
     * the distance between the baseline of adjacent lines of text.
     * It is the sum of the ascent+descent+leading.  There is no
     * guarantee that lines of text spaced at this distance will be
     * disjoint; such lines may overlap if some characters overshoot
     * the standard ascent and descent metrics.
     */
    public int		height;

    /** The maximum ascent for all characters in this font.  No character
     * will extend further above the baseline than this metric.
     */
    public int		maxAscent;

    /** The maximum descent for all characters in this font.  No character
     * will descend further below the baseline than this metric.
     */
    public int		maxDescent;

    /** The maximum possible height of a line of text in this font.
     * Adjacent lines of text spaced this distance apart will be
     * guaranteed not to overlap.  Note, however, that many paragraphs
     * that contain ordinary alphanumeric text may look too widely
     * spaced if this metric is used to determine line spacing.  The
     * height field should be preferred unless the text in a given
     * line contains particularly tall characters.
     */
    public int		maxHeight;

    /** The maximum advance width of any character in this font. */
    public int		maxAdvance;

    /**
     * Store the Font object and rely on the subclass constructors to
     * calculate the metrics.
     */
    FontMetrics(Font f) {
	font = f;
    }

    /** Convert this object to a string representation. */
    public String toString() {
	return getClass().getName() + "[font=" + font + ", ascent=" +
	    ascent + ", descent=" + descent + ", height=" + height + "]";
    }

    /** Return the width of the specified character in this Font. */
    public abstract int charWidth(int ch);

    /** Return the width of the specified string in this Font. */
    public abstract int stringWidth(String s);

    /** Return the width of the specified char[] in this Font. */
    public abstract int charsWidth(char data[], int off, int len);

    /** Return the width of the specified byte[] in this Font. */
    public abstract int bytesWidth(byte data[], int off, int len);
}
