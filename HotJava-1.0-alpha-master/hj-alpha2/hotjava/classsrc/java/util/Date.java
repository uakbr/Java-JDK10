/*
 * @(#)Date.java	1.9 95/01/31 Arthur van Hoff
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

package java.util;

/**
 * A wrapper for a date. This class lets you manipulate
 * dates in a system independent way. To print today's
 * date use:
 * <pre>
 *	Date d = new Date();
 *	System.out.println("today = " + d);
 * </pre>
 * To find out what day I was born:
 * <pre>
 *	Date d = new Date(1963, 0, 16);
 *	System.out.println("Day of the week: " + d.day());
 * </pre>
 *
 * @version 	1.9, 31 Jan 1995
 * @author	Arthur van Hoff
 */
public
class Date {
    /**
     * The date is encoded into the bits of this 64 bit number.
     * This is done to make comparison of dates easy.
     */
    private long value;
    
    private static final int SEC_OFF 	= 0;
    private static final int SEC_BITS 	= 6;
    private static final int MIN_OFF 	= 6;
    private static final int MIN_BITS 	= 6;
    private static final int HRS_OFF 	= 12;
    private static final int HRS_BITS 	= 5;
    private static final int DAY_OFF 	= 17;
    private static final int DAY_BITS 	= 3;
    private static final int DATE_OFF 	= 20;
    private static final int DATE_BITS 	= 5;
    private static final int MONTH_OFF	= 25;
    private static final int MONTH_BITS = 4;
    private static final int YEAR_OFF 	= 29;
    private static final int YEAR_BITS 	= 12;

    /**
     * Creates today's date/time.
     */
    public Date() {
	value = nowValue();
    }

    /**
     * Creates a date. 
     * The fields are normalized before the Date object is created.
     * The arguments do not have to be in the correct range. For example,
     * the 32nd of January is correctly interpreted as the 1st of February.
     * You can use this to figure out what day a particular date falls on.
     * @param year	a year after 1900
     * @param month	a month between 0-11
     * @param date	day of the month between 1-31
     */
    public Date(int year, int month, int date) {
	this(year, month, date, 0, 0, 0);
    }
    /**
     * Creates a date. 
     * The fields are normalized before the Date object is created.
     * The arguments do not have to be in the correct range. For example,
     * the 32nd of January is correctly interpreted as the 1st of February.
     * You can use this to figure out what day a particular date falls on.
     * @param year	a year after 1900
     * @param month	a month between 0-11
     * @param date	day of the month between 1-31
     * @param hrs		hours between 0-23
     * @param min		hours between 0-59
     */
    public Date(int year, int month, int date, int hrs, int min) {
	this(year, month, date, hrs, min, 0);
    }
    /*
     * Creates a date. 
     * The fields are normalized before the Date object is created.
     * The arguments do not have to be in the correct range. For example,
     * the 32nd of January is correctly interpreted as the 1st of February.
     * You can use this to figure out what day a particular date falls on.
     * @param year	a year after 1900
     * @param month	a month between 0-11
     * @param date	day of the month between 1-31
     * @param hrs		hours between 0-23
     * @param min		hours between 0-59
     * @param sec		seconds between 0-59
     */
    public Date(int year, int month, int date, int hrs, int min, int sec) {
	value = dateValue(year, month, date, hrs, min, sec);
    }

    /** get/set a field */
    private int getField(int off, int bits) {
	return (int)((value >> off) & ((1 << bits) - 1));
    }
    private void setField(int val, int off, int bits) {
	value |= ((long)val & ((1 << bits) - 1)) << off;
    }

    /**
     * Returns the year. ie: 1995...
     */
    public int getYear() {
	return getField(YEAR_OFF, YEAR_BITS);
    }

    /**
     * Returns the month. ie: 0-11
     */
    public int getMonth() {
	return getField(MONTH_OFF, MONTH_BITS);
    }

    /**
     * Return the day of the month. ie: 1-31
     */
    public int getDate() {
	return getField(DATE_OFF, DATE_BITS);
    }

    /**
     * Returns the day of the week. ie: 0-6
     */
    public int getDay() {
	return getField(DAY_OFF, DAY_BITS);
    }

    /**
     * Returns the hour. ie: 0-23
     */
    public int getHours() {
	return getField(HRS_OFF, HRS_BITS);
    }

    /**
     * Returns the minute. ie: 0-59
     */
    public int getMinutes() {
	return getField(MIN_OFF, MIN_BITS);
    }

    /**
     * Returns the second. ie: 0-59
     */
    public int getSeconds() {
	return getField(SEC_OFF, SEC_BITS);
    }

    /**
     * Checks whether this date comes before another date.
     */
    public boolean before(Date when) {
	return value < when.value;
    }

    /**
     * Checks whether  this date comes after another date.
     */
    public boolean after(Date when) {
	return value > when.value;
    }

    /**
     * Compares this object against some other object.
     * @param obj		the object to compare with
     * @return 		true if the object is the same
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Date)) {
	    return value == ((Date)obj).value;
	}
	return false;
    }

    /**
     * Computes a hashCode.
     */
    public int hashCode() {
	return (int)value;
    }

    /**
     * Converts a date to a string, using the host's conventions.
     */
    public native String toString();

    /*
     * Gets date values.
     */
    private static native long nowValue();
    private static native long dateValue(int year, int month, int date, int hrs, int min, int sec);
}
