/*
 * @(#)Date.java	1.21 95/11/21 James Gosling, Arthur van Hoff
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
 * The date can be set and examined
 * according to the local time zone into the
 * year, month, day, hour, minute and second.
 * <p>
 * While the API is intended to reflect UTC, Coordinated Universal Time,
 * it doesn't do so exactly.  This inexact behavior is inherited from
 * the time system of the underlying OS.  All modern OS's that I (jag)
 * am aware of assume that 1 day = 24*60*60 seconds.  In UTC, about once
 * a year there is an extra second, called a "leap second" added to
 * a day to account for the wobble of the earth.  Most computer clocks
 * are not accurate enough to be able to reflect this distinction.
 * Some computer standards are defined in GMT, which is equivalent
 * to UT, Universal Time.  GMT is the "civil" name for the standard,
 * UT is the "scientific" name for the same standard.  The distinction
 * between UTC and UT is that the first is based on an atomic clock and
 * the second is based on astronomical observations, which for all
 * practical purposes is an invisibly fine hair to split.
 * An interesting source of further information is the
 * US Naval Observatory, particularly the
 * <a href=http://tycho.usno.navy.mil>Directorate of Time</a>
 * and their definitions of
 * <a href=http://tycho.usno.navy.mil/systime.html>Systems of Time</a>.
 *
 * @version 	1.14, 28 Jul 1995
 * @author	James Gosling
 * @author	Arthur van Hoff
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
     * The argument does not have to be in the correct range. For
     * example, the 32nd of January is correctly interpreted as the
     * 1st of February.  You can use this to figure out what day a
     * particular date falls on.
     * @param date the value of the argument to be created
     */
    public Date (long date) {
	value = date;
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
     * Creates a date from a string according to the syntax
     * accepted by parse().
     */
    public Date (String s) {
	this(parse(s));
    }



    /**
     * Calculates a UTC value from YMDHMS. Interpretes
     * the parameters in UTC, <i>not<i> in the local time zone.
     * @param year	a year after 1900
     * @param month	a month between 0-11
     * @param date	day of the month between 1-31
     * @param hrs	hours between 0-23
     * @param min	minutes between 0-59
     * @param sec	seconds between 0-59
     */
    public static long UTC(int year, int month, int date,
			        int hrs, int min, int sec) {
	long day = (date
		    + monthOffset[month]
		    + ((year & 3) != 0
		       || year % 100 == 0 && (year + 300) % 400 != 0
		       || month < 2
		       ? -1 : 0)/* convert day-of-month to 0 based range,
				 * except following February in a leap year,
				 * in which case we skip the conversion to
				 * account for the extra day in February */
		    + (year - 70) * 365L	// days per year
		    + (year - 69) / 4	// plus leap days
		    - (year - 1) / 100	// no leap on century years
		    + (year + 299) / 400);	// except %400 years
	return (sec + 60 * (min + 60 * hrs)) * 1000 + (60 * 60 * 24 * 1000) * day;
    }

    private static short monthOffset[] = {
	0,			// 31	January
	31,			// 28	February
	59,			// 31	March
	90,			// 30	April
	120,			// 31	May
	151,			// 30	June
	181,			// 31	July
	212,			// 31	August
	243,			// 30	September
	273,			// 31	October
	304,			// 30	November
	334			// 31	December
	// 365
    };

    /**
     * Given a string representing a time, parse it and return the time value.
     * It accepts many syntaxes, but most importantly, in accepts the IETF
     * standard date syntax: "Sat, 12 Aug 1995 13:30:00 GMT".  It understands
     * the continental US time zone abbreviations, but for general use, a
     * timezone offset should be used: "Sat, 12 Aug 1995 13:30:00 GMT+0430"
     * (4 hours, 30 minutes west of the Greenwich meridian).
     * If no time zone is specified, the local time zone is assumed.
     * GMT and UTC are considered equivalent.
     */
    public static long parse(String s) {
	int year = -1;
	int mon = -1;
	int mday = -1;
	int hour = -1;
	int min = -1;
	int sec = -1;
	int millis = -1;
	int c = -1;
	int i = 0;
	int n = -1;
	int wst = -1;
	int tzoffset = -1;
	int prevc = 0;
syntax:
	{
	    if (s == null)
		break syntax;
	    int limit = s.length();
	    while (i < limit) {
		c = s.charAt(i);
		i++;
		if (c <= ' ' || c == ',' || c == '-')
		    continue;
		if (c == '(') {	// skip comments
		    int depth = 1;
		    while (i < limit) {
			c = s.charAt(i);
			i++;
			if (c == '(') depth++;
			else if (c == ')')
			    if (--depth <= 0)
			        break;
		    }
		    continue;
		}
		if ('0' <= c && c <= '9') {
		    n = c - '0';
		    while (i < limit && '0' <= (c = s.charAt(i)) && c <= '9') {
			n = n * 10 + c - '0';
			i++;
		    }
		    if (prevc == '+' || prevc == '-' && year>=0) {
			// timezone offset
			if (n < 24)
			    n = n * 60;	// EG. "GMT-3"
			else
			    n = n % 100 + n / 100 * 60;	// eg "GMT-0430"
			if (prevc == '+')	// plus means east of GMT
			    n = -n;
			if (tzoffset != 0 && tzoffset != -1)
			    break syntax;
			tzoffset = n;
		    } else if (n >= 70)
			if (year >= 0)
			    break syntax;
			else if (c <= ' ' || c == ',' || c == '/' || i >= limit)
			    year = n < 1900 ? n : n - 1900;
			else
			    break syntax;
		    else if (c == ':')
			if (hour < 0)
			    hour = (byte) n;
			else if (min < 0)
			    min = (byte) n;
			else
			    break syntax;
		    else if (c == '/')
			if (mon < 0)
			    mon = (byte) n;
			else if (mday < 0)
			    mday = (byte) n;
			else
			    break syntax;
		    else if (i < limit && c != ',' && c > ' ' && c != '-')
			break syntax;
		    else if (hour >= 0 && min < 0)
			min = (byte) n;
		    else if (min >= 0 && sec < 0)
			sec = (byte) n;
		    else if (mday < 0)
			mday = (byte) n;
		    else
			break syntax;
		    prevc = 0;
		} else if (c == '/' || c == ':' || c == '+' || c == '-')
		    prevc = c;
		else {
		    int st = i - 1;
		    while (i < limit) {
			c = s.charAt(i);
			if (!('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z'))
			    break;
			i++;
		    }
		    if (i <= st + 1)
			break syntax;
		    int k;
		    for (k = wtb.length; --k >= 0;)
			if (wtb[k].regionMatches(true, 0, s, st, i - st)) {
			    int action = ttb[k];
			    if (action != 0)
				if (action == 1)	// pm
				    if (hour > 12 || hour < 0)
					break syntax;
				    else
					hour += 12;
				else if (action <= 13)	// month!
				    if (mon < 0)
					mon = (byte) (action - 2);
				    else
					break syntax;
				else
				    tzoffset = action - 10000;
			    break;
			}
		    if (k < 0)
			break syntax;
		    prevc = 0;
		}
	    }
	    if (year < 0 || mon < 0 || mday < 0)
		break syntax;
	    if (sec < 0)
		sec = 0;
	    if (min < 0)
		min = 0;
	    if (hour < 0)
		hour = 0;
	    if (tzoffset == -1)	// no time zone specified, have to use local
		return new Date (year, mon, mday, hour, min, sec).getTime();
	    return UTC(year, mon, mday, hour, min, sec) + tzoffset * (60 * 1000);
	}
	// syntax error
	throw new IllegalArgumentException();
    }
    private final static String wtb[] = {
	"am", "pm",
	"monday", "tuesday", "wednesday", "thursday", "friday",
	"saturday", "sunday",
	"january", "february", "march", "april", "may", "june",
	"july", "august", "september", "october", "november", "december",
	"gmt", "ut", "utc", "est", "edt", "cst", "cdt",
	"mst", "mdt", "pst", "pdt"
	// this time zone table needs to be expanded
    };
    private final static int ttb[] = {
	0, 1, 0, 0, 0, 0, 0, 0, 0,
	2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
	10000 + 0, 10000 + 0, 10000 + 0,	// GMT/UT/UTC
	10000 + 5 * 60, 10000 + 4 * 60,	// EST/EDT
	10000 + 6 * 60, 10000 + 5 * 60,
	10000 + 7 * 60, 10000 + 6 * 60,
	10000 + 8 * 60, 10000 + 7 * 60
    };

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
     * @param year the year value
     */
    public void setYear(int year) {
	if (!expanded)
	    expand();
	tm_year = year;
	valueValid = false;
    }

    /**
     * Returns the month. This method assigns months with the
     * values 0-11, with January beginning at value 0.
     */
    public int getMonth() {
	if (!expanded)
	    expand();
	return tm_mon;
    }

    /**
     * Sets the month.
     * @param month the month value (0-11)
     */
    public void setMonth(int month) {
	if (!expanded)
	    expand();
	tm_mon = (byte) month;
	valueValid = false;
    }

    /**
     * Returns the day of the month. This method assigns days
     * with the values of 1 to 31.
     */
    public int getDate() {
	if (!expanded)
	    expand();
	return tm_mday;
    }

    /**
     * Sets the date.
     * @param date the day value
     */
    public void setDate(int date) {
	if (!expanded)
	    expand();
	tm_mday = (byte) date;
	valueValid = false;
    }

    /**
     * Returns the day of the week. This method assigns days
     * of the week with the values 0-6, with 0 being Sunday.
     */
    public int getDay() {
	if (!expanded) {
	    expand();
	} else if ((tm_wday < 0) || !valueValid) {
	    computeValue();
	    expand();
	}
	return tm_wday;
    }

    /**
     * Returns the hour. This method assigns the value of the
     * hours of the day to range from 0 to 23, with midnight equal
     * to 0.
     */
    public int getHours() {
	if (!expanded)
	    expand();
	return tm_hour;
    }

    /**
     * Sets the hours.
     * @param hours the hour value
     */
    public void setHours(int hours) {
	if (!expanded)
	    expand();
	tm_hour = (byte) hours;
	valueValid = false;
    }

    /**
     * Returns the minute. This method assigns the minutes of an
     * hour to be any value from 0 to 59.
     */
    public int getMinutes() {
	if (!expanded)
	    expand();
	return tm_min;
    }

    /**
     * Sets the minutes.
     * @param minutes the value of the minutes
     */
    public void setMinutes(int minutes) {
	if (!expanded)
	    expand();
	tm_min = (byte) minutes;
	valueValid = false;
    }

    /**
     * Returns the second. This method assigns the seconds of
     * a minute to values of 0-59.
     */
    public int getSeconds() {
	if (!expanded)
	    expand();
	return tm_sec;
    }

    /**
     * Sets the seconds.
     * @param seconds the second value
     */
    public void setSeconds(int seconds) {
	if (!expanded)
	    expand();
	tm_sec = (byte) seconds;
	valueValid = false;
    }

    /**
     * Returns the time in milliseconds since the epoch.
     */
    public long getTime() {
	if (!valueValid)
	    computeValue();
	return value;
    }

    /**
     * Sets the time.
     * @param time	The new time value in milliseconds since the epoch.
     */
    public void setTime(long time) {
	value = time;
	valueValid = true;
	expanded = false;
    }

    /**
     * Checks whether this date comes before the specified date.
     * @param when the date to compare
     * @return true if the original date comes before the specified
     * one; false otherwise.
     */
    public boolean before(Date when) {
	return getTime() < when.getTime();
    }

    /**
     * Checks whether this date comes after the specified date.
     * @param when the date to compare
     * @return true if the original date comes after the specified
     * one; false otherwise.
     */
    public boolean after(Date when) {
	return getTime() > when.getTime();
    }

    /**
     * Compares this object against the specified object.
     * @param obj the object to compare with
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	return obj != null && obj instanceof Date &&getTime() == ((Date) obj).getTime();
    }

    /**
     * Computes a hashCode.
     */
    public int hashCode() {
	long ht = getTime();
	return (int) ht ^ (int) (ht >> 32);
    }

    /**
     * Converts a date to a String, using the UNIX ctime conventions.
     */
    public native String toString();

    /**
     * Converts a date to a String, using the locale conventions.
     */
    public native String toLocaleString();

    /**
     * Converts a date to a String, using the Internet GMT conventions.
     */
    public native String toGMTString();

    /**
     * Return the time zone offset in minutes for the current locale that is appropriate
     * for this time.  This value would be a constant except for
     * daylight savings time.
     */
    public int getTimezoneOffset() {
	if (!expanded)
	    expand();
	return (int) ((getTime() - UTC(tm_year, tm_mon, tm_mday,
				   tm_hour, tm_min, tm_sec)) / (60 * 1000));
    }

    /*
     * Gets date values.
     */
    private native void expand();
    private native void computeValue();

}
