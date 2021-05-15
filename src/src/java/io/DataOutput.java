/*
 * @(#)DataOutput.java	1.4 95/12/18 Frank Yellin
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

package java.io;

/**
 * DataOutput is an interface describing streams that can write 
 * output in a machine-independent format.
 *
 * @see DataOutputStream
 * @see DataInput
 * @version 	1.4, 12/18/95
 * @author	Frank Yellin
 */

public
interface DataOutput {
    /**
     * Writes a byte. Will block until the byte is actually
     * written.
     * @param b the byte to be written
     * @exception IOException If an I/O error has occurred.
     */
    void write(int b) throws IOException;

    /**
     * Writes an array of bytes.  
     * @param b	the data to be written
     * @exception IOException If an I/O error has occurred.
     */
    void write(byte b[]) throws IOException;

    /**
     * Writes a subarray of bytes.  
     * @param b	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    void write(byte b[], int off, int len) throws IOException;


    /**
     * Writes a boolean.
     * @param v the boolean to be written
     */
    void writeBoolean(boolean v) throws IOException;

    /**
     * Writes an 8 bit byte.
     * @param v the byte value to be written
     */
    void writeByte(int v) throws IOException;

    /**
     * Writes a 16 bit short.
     * @param v the short value to be written
     */
    void writeShort(int v) throws IOException;

    /**
     * Writes a 16 bit char.
     * @param v the char value to be written
     */
    void writeChar(int v) throws IOException;

    /**
     * Writes a 32 bit int.
     * @param v the integer value to be written
     */
    void writeInt(int v) throws IOException;

    /**
     * Writes a 64 bit long.
     * @param v the long value to be written
     */
    void writeLong(long v) throws IOException;

    /**
     * Writes a 32 bit float.
     * @param v the float value to be written
     */
    void writeFloat(float v) throws IOException;

    /**
     * Writes a 64 bit double.
     * @param v the double value to be written
     */
    void writeDouble(double v) throws IOException;

    /**
     * Writes a String as a sequence of bytes.
     * @param s the String of bytes to be written
     */
    void writeBytes(String s) throws IOException;

    /**
     * Writes a String as a sequence of chars.
     * @param s the String of chars to be written
     */
    void writeChars(String s) throws IOException;

    /**
     * Writes a String in UTF format.
     * @param str the String in UTF format
     */
    void writeUTF(String str) throws IOException;
}
