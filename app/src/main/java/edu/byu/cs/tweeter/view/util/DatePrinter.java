package edu.byu.cs.tweeter.view.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

public class DatePrinter {

    private Date date;
    private Calendar calendar;
    private final int LAST_AM_HOUR  = 11;
    private final int NOON = 12;
    private final int MIDNIGHT = 0;
    private final int DOUBLE_DIGIT_NUMBER = 10;

    public DatePrinter(Calendar cal) {
        this.calendar = cal;
    }

    /**
     * Formats a date like this " Sep 15 2020 10:55 PM"
     * @return the perfect tweet time posting!!
     */
    public String toString() {
        Writer out = new StringWriter();
        //SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");

        try {
            out.write(getMonth() + " " + getDay() + " " + getYear() + " " + getTime() + " " + getAmPm());
        } catch (IOException e) {
            return "Error in DatePrinter.toString() function";
        }
        return out.toString();
    }

    /**
     * Converts a month as a number from the Calendar object to a string
     * @return The month as an abbreviated string
     */
    private String getMonth() {
        int month = calendar.get(Calendar.MONTH);
        switch (month) {
            case 0: return "Jan";
            case 1: return "Feb";
            case 2: return "Mar";
            case 3: return "Apr";
            case 4: return "May";
            case 5: return "Jun";
            case 6: return "Jul";
            case 7: return "Aug";
            case 8: return "Sep";
            case 9: return "Oct";
            case 10: return "Nov";
            default: return "Dec";
        }
    }

    /**
     * Converts a military hour and returns if it is in the AM or the PM
     * @return A string "AM" or "PM"
     */
    private String getAmPm() {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if(hourOfDay > LAST_AM_HOUR) {
            return "PM";
        }
        return "AM";
    }

    /**
     * Converts the day to a string to return
     * @return the day as a string
     */
    private String getDay() {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return Integer.toString(day);
    }

    /**
     * Converts the year to a string to return
     * @return the year as a string
     */
    private String getYear() {
        int year = calendar.get(Calendar.YEAR);
        return Integer.toString(year);
    }

    /**
     * Converts the hour:minute to a string to return
     * @return the time as a string
     */
    private String getTime() {
        String hour = getHour();
        String minutes = getMinutes();
        return hour + ":" + minutes;
    }

    /**
     * Converts a military hour to a string
     * @return hour as a string
     */
    private String getHour() {
        int iHour = 0;
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay > NOON) {
            iHour = hourOfDay - NOON;
        } else if (hourOfDay == MIDNIGHT) {
            iHour = 12;
        } else { // Military time agrees with normal time
            iHour = hourOfDay;
        }
        String sHour = Integer.toString(iHour);
        return sHour;
    }

    /**
     * Converts the minute to a string
     * @return minute as a string
     */
    private String getMinutes() {
        int iMinutes = calendar.get(Calendar.MINUTE);
        String sMinutes = Integer.toString(iMinutes);
        if (iMinutes < DOUBLE_DIGIT_NUMBER) {
            sMinutes = "0" + sMinutes;
        }
        return sMinutes;
    }
}
