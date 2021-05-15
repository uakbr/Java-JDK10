/*
 * @(#)GridBagConstraints.java	1.5 95/12/01 Doug Stein
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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
package java.awt;

/**
 * GridBagConstraints is used to specify constraints for components
 * laid out using the GridBagLayout class.
 *
 * @see java.awt.GridBagLayout
 * @version 1.5, 12/01/95
 * @author Doug Stein
 */
public class GridBagConstraints implements Cloneable {
  public static final int RELATIVE = -1;
  public static final int REMAINDER = 0;

  public static final int NONE = 0;
  public static final int BOTH = 1;
  public static final int HORIZONTAL = 2;
  public static final int VERTICAL = 3;

  public static final int CENTER = 10;
  public static final int NORTH = 11;
  public static final int NORTHEAST = 12;
  public static final int EAST = 13;
  public static final int SOUTHEAST = 14;
  public static final int SOUTH = 15;
  public static final int SOUTHWEST = 16;
  public static final int WEST = 17;
  public static final int NORTHWEST = 18;

  public int gridx, gridy, gridwidth, gridheight;
  public double weightx, weighty;
  public int anchor, fill;
  public Insets insets;
  public int ipadx, ipady;

  int tempX, tempY;
  int tempWidth, tempHeight;
  int minWidth, minHeight;

  public GridBagConstraints () {
    gridx = RELATIVE;
    gridy = RELATIVE;
    gridwidth = 1;
    gridheight = 1;

    weightx = 0;
    weighty = 0;
    anchor = CENTER;
    fill = NONE;

    insets = new Insets(0, 0, 0, 0);
    ipadx = 0;
    ipady = 0;
  }

  public Object clone () {
      try { 
	  GridBagConstraints c = (GridBagConstraints)super.clone();
	  c.insets = (Insets)insets.clone();
	  return c;
      } catch (CloneNotSupportedException e) { 
	  // this shouldn't happen, since we are Cloneable
	  throw new InternalError();
      }
  }
}
