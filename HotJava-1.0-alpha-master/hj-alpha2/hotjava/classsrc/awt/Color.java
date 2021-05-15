/*
 * @(#)Color.java	1.10 95/01/31 Sami Shaio
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

import java.io.*;
import java.lang.*;

/**
 * A class to encapsulate RGB Colors.
 *
 * @version 1.10 31 Jan 1995
 * @author Sami Shaio
 */
public class Color {
    private int pData;

    public int r;
    public int g;
    public int b;

    public static Color white;
    public static Color lightGray;
    public static Color gray;
    public static Color darkGray;
    public static Color black;
    
    public static Color red;
    public static Color pink;
    public static Color orange;
    public static Color yellow;
    public static Color green;
    public static Color magenta;
    public static Color blue;
    public static Color cyan;

    public static Color menuFore;
    public static Color menuBack;
    public static Color menuHighlight;
    public static Color menuBright;
    public static Color menuDim;

    /**
     * Creates a color with the given RGB values. The actual color
     * will depend on the server ws finding the best match given the
     * color space available.
     */
    public Color(WServer ws, int R, int G, int B) {
	r = R;
	g = G;
	b = B;
	ws.colorCreate(this);
    }

    
    static void initColors(WServer ws) {
	white		= new Color(ws, 255, 255, 255);
	lightGray 	= new Color(ws, 192, 192, 192);
	gray 		= new Color(ws, 128, 128, 128);
	darkGray 	= new Color(ws, 64, 64, 64);
	black 		= new Color(ws, 0, 0, 0);
    
	red 		= new Color(ws, 255, 0, 0);
	pink 		= new Color(ws, 255, 175, 175);
	orange 		= new Color(ws, 255, 200, 0);
	yellow 		= new Color(ws, 255, 255, 0);
	green 		= new Color(ws, 0, 255, 0);
	magenta 	= new Color(ws, 255, 0, 255);
	cyan		= new Color(ws, 0, 255, 255);
	blue 		= new Color(ws, 0, 0, 255);

	menuFore = Color.black;
	menuBack = new Color(ws, 204, 204, 204);
	menuHighlight = new Color(ws, 183, 183, 183);
	menuBright = new Color(ws,244, 244, 244);
	menuDim = new Color(ws, 102, 102, 102);
    }

    /**
     * Compare against another color
     */
    public boolean equal(Color col) {
	return (r == col.r) && (g == col.g) && (b == col.b);
    }

    /**
     * Convert to a String
     */
    public String toString() {
	return "rgb(" + r + "," + g + "," + b + ")";
    }

    /**
     * Print
     */
    public void print() {
	System.out.println(toString());
    }
}
