/*
 * @(#)Date.java	1.11 95/05/12 Arthur van Hoff
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies. Please
 * refer to the file "copyright.html" for further important copyright and
 * licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
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
 * To find out what day corresponds to a particular date:
 * <pre>
 *	Date d = new Date(63, 0, 16);	// January 16, 1963
 *	System.out.println("Day of the week: " + d.getDay());
 * </pre>
 * The date can be set and examined broken down
 * according to the local time zone into
 * year, month, day, ...
 *
 * @version 	1.11, 12 May 1995
 * @author	James Gosling, Arthur van Hoff
 */
public
class Date {
    private long value;
    private boolean valueValid;
    private boolean expanded;

    private short tm_millis;	/* miliseconds within the second - [0,999] */
    private byte tm_sec;	/* seconds after the minute - [0, 61] for
				 * leap seconds */
    private byte tm_min;	/* minutes after the hour - [0, 59] */
    private byte tm_hour;	/* hour since midnight - [0, 23] */
    private byte tm_mday;	/* day of the month - [1, 31] */
    private byte tm_mon;	/* months since January - [0, 11] */
    private byte tm_wday;	/* days since Sunday - [0, 6] */
    private short tm_yday;	/* days since January 1 - [0, 365] */
    private int tm_year;	/* years since 1900 */
    private int tm_isdst;	/* flag for alternate daylight savings time */
    /**
     * Creates today's date/time.
     */
    public Date () {
	this(System.currentTimeMillis());
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
    public Date (long V) {
	value = V;
	valueValid = true;
	expanded = false;
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
    public Date (int year, int month, int date) {
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
     * @param hrs	hours between 0-23
     * @param min	minutes between 0-59
     */
    public Date (int year, int month, int date, int hrs, int min) {
	this(year, month, date, hrs, min, 0);
    }

    /**
     * Creates a date. The fields are normalized before the Date object is
     * created. The arguments do not have to be in the correct range. For
     * example, the 32nd of January is correctly interpreted as the 1st of
     * February. You can use this to figure out what day a particular date
     * falls on.
     * @param year	a year after 1900
     * @param month	a month between 0-11
     * @param date	day of the month between 1-31
     * @param hrs	hours between 0-23
     * @param min	minutes between 0-59
     * @param sec	seconds between 0-59
     */
    public Date (int year, int month, int date, int hrs, int min, int sec) {
	expanded = true;
	valueValid = false;
	tm_millis = 0;
	tm_sec = (byte) sec;
	tm_min = (byte) min;
	tm_hour = (byte) hrs;
	tm_mday = (byte) date;
	tm_mon = (byte) month;
	tm_wday = 0;
	tm_yday = 0;
	tm_year = year;
	computeValue();
	expand();
    }

    /**
     * Returns the year after 1900.
     */
    public int getYear() {
	if (!expanded)
	    expand();
	return tm_year;
    }

    /**
     * Sets the year.
     */
    public void setYear(int v) {
	tm_year = v;
	valueValid = false;
    }

    /**
     * Returns the month. ie: 0-11
     */
    public int getMonth() {
	if (!expanded)
	    expand();
	return tm_mon;
    }

    /**
     * Sets the month.
     */
    public void setMonth(byte v) {
	tm_mon = v;
	valueValid = false;
    }

    /**
     * Return the day of the month. ie: 1-31
     */
    public int getDate() {
	if (!expanded)
	    expand();
	return tm_mday;
    }

    /**
     * Sets the date.
     */
    public void setDate(byte v) {
	tm_mday = v;
	valueValid = false;
    }

    /**
     * Returns the day of the week. ie: 0-6
     */
    public int getDay() {
	if (!expanded)
	    expand();
	else if (tm_wday < 0 || !valueValid)
	    computeValue(), expand();
	return tm_wday;
    }

    /**
     * Sets the day of the week.
     */
    public void setDay(byte v) {
	tm_wday = v;
	valueValid = false;
    }

    /**
     * Returns the hour. ie: 0-23
     */
    public int getHours() {
	if (!expanded)
	    expand();
	return tm_hour;
    }

    /**
     * Sets the hours.
     */
    public void setHours(byte v) {
	tm_hour = v;
	valueValid = false;
    }

    /**
     * Returns the minute. ie: 0-59
     */
    public int getMinutes() {
	if (!expanded)
	    expand();
	return tm_min;
    }

    /**
     * Sets the minutes.
     */
    public void setMinutes(byte v) {
	tm_min = v;
	valueValid = false;
    }

    /**
     * Returns the second. ie: 0-59
     */
    public int getSeconds() {
	if (!expanded)
	    expand();
	return tm_sec;
    }

    /**
     * Sets the seconds.
     */
    public void setSeconds(byte v) {
	tm_sec = v;
	valueValid = false;
    }

    public long getTime() {
	if (!valueValid)
	    computeValue();
	return value;
    }

    /**
     * Checks whether this date comes before another date.
     */
    public boolean before(Date when) {
	return getTime() < when.getTime();
    }

    /**
     * Checks whether  this date comes after another date.
     */
    public boolean after(Date when) {
	return getTime() > when.getTime();
    }

    /**
     * Compares this object against some other object.
     * @param obj		the object to compare with
     * @return 		true if the object is the same
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Date)) {
	    return getTime() == ((Date) obj).getTime();
	}
	return false;
    }

    /**
     * Computes a hashCode.
     */
    public int hashCode() {
	return (int) getTime();
    }

    /**
     * Converts a date to a string, using the UNIX ctime conventions.
     */
    public native String toString();

    /**
     * Converts a date to a string, using the locale's conventions.
     */
    public native String toLocaleString();

    /**
     * Converts a date to a string, using the Internet GMT conventions.
     */
    public native String toGMTString();

    /*
     * Gets date values.
     */
    private native void expand();
    private native void computeValue();

}
