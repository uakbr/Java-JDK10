/*
 * @(#)DataInput.java	1.6 95/12/18 Arthur van Hoff
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

package java.io;

/**
 * DataInput is an interface describing streams that can read input
 * in a machine-independent format.
 *
 * @see DataInputStream
 * @see DataOutput
 * @version 	1.6, 12/18/95
 * @author	Frank Yellin
 */
public
interface DataInput {
    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    void readFully(byte b[]) throws IOException;

    /**
     * Reads bytes, blocking until all bytes are read.
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes to read
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    void readFully(byte b[], int off, int len) throws IOException;

    /**
     * Skips bytes, block until all bytes are skipped.
     * @param n the number of bytes to be skipped
     * @return	the actual number of bytes skipped.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    int skipBytes(int n) throws IOException;

    /**
     * Reads in a boolean.
     * @return the boolean read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    boolean readBoolean() throws IOException;

    /**
     * Reads an 8 bit byte.
     * @return the 8 bit byte read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    byte readByte() throws IOException;

    /**
     * Reads an unsigned 8 bit byte.
     * @return the 8 bit byte read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    int readUnsignedByte() throws IOException;

    /**
     * Reads a 16 bit short.
     * @return the 16 bit short read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    short readShort() throws IOException;

    /**
     * Reads an unsigned 16 bit short.
     * @return the 16 bit short read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    int readUnsignedShort() throws IOException;

    /**
     * Reads a 16 bit char.
     * @return the 16 bit char read. 
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    char readChar() throws IOException;

    /**
     * Reads a 32 bit int.
     * @return the 32 bit integer read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    int readInt() throws IOException;

    /**
     * Reads a 64 bit long.
     * @return the read 64 bit long.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    long readLong() throws IOException;

    /**
     * Reads a 32 bit float.
     * @return the 32 bit float read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    float readFloat() throws IOException;

    /**
     * Reads a 64 bit double.
     * @return the 64 bit double read.
     * @exception EOFException If end of file is reached.
     * @exception IOException If other I/O error has occurred.
     */
    double readDouble() throws IOException;

    String readLine() throws IOException;

    String readUTF() throws IOException;
}
