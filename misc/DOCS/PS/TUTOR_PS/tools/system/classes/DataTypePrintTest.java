/*
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
class DataTypePrintTest {
    public static void main(String args[]) {

	Thread ObjectData = new Thread();
	String StringData = "Java Mania";
	char CharArrayData[] = { 'a', 'b', 'c' };
	int IntegerData = 4;
	long LongData = Long.MIN_VALUE;
	float FloatData = Float.PI;
	double DoubleData = Double.MAX_VALUE;
	boolean BooleanData = true;

	System.out.println("object = " + ObjectData);
	System.out.println("string = " + StringData);
	System.out.println("character array = " + CharArrayData);
	System.out.println("integer = " + IntegerData);
	System.out.println("long = " + LongData);
	System.out.println("float = " + FloatData);
	System.out.println("double = " + DoubleData);
	System.out.println("boolean = " + BooleanData);
    }
}
