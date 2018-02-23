/*
 * MolnarFormatter.java
 *
 * Created on December 14, 2006, 8:13 PM
 *
 */

package jtimesheet.mlnr.util;

import java.util.GregorianCalendar;

/**
 *
 * @author Robert Molnar II
 */
public class MolnarFormatter {
    
    /** Formats the date as: [mm/dd/yyyy]
     */
    public static final int DATEFORMAT_1 = 1;
    /** Formats the Gregorian as: [HH:MM AM|PM]
     */
    public static final int DATEFORMAT_2 = 2;
    /** Formats the date as: [mm/dd/yyyy HH:MM AM|PM]
     */
    public static final int DATEFORMAT_3 = 3;
    /** Formats the date as: [mm.dd.yyyy] 
     */
    public static final int DATEFORMAT_4 = 4;
    
    /** Creates a new instance of MolnarFormatter */
    public MolnarFormatter() {
    }
    
    /** This will convert the hour (1-12) and the amPm (0:am, 1:pm) to 24 hour.
     * @param hour is (1-12).
     * @param amPm 0: am, 1:pm.
     * @return 0-23 for the hour.
     */
    public static final int convertHour(int hour, int amPm) throws IllegalArgumentException {
        if (amPm == 0 && hour == 12)
            return 0;
        if (amPm == 1 && hour == 12)
            return 12;
        if (amPm == 1)
            return hour + 12;
        return hour;
    }
    
    /** This will format the GregorianCalender.
     *
     */
    public static final String format(GregorianCalendar gCal, int formatType) {
        switch (formatType) {
            case DATEFORMAT_1:
                return String.format("%02d/%02d/%d", (gCal.get(GregorianCalendar.MONTH) + 1), 
                        gCal.get(GregorianCalendar.DAY_OF_MONTH), gCal.get(GregorianCalendar.YEAR));
            case DATEFORMAT_2:
                String amPm = "AM";
                if (gCal.get(GregorianCalendar.AM_PM) == GregorianCalendar.PM)
                    amPm = "PM";
                int hour = gCal.get(GregorianCalendar.HOUR);
                if (hour == 0)
                    hour = 12;
                return String.format("%02d:%02d %s", hour, gCal.get(GregorianCalendar.MINUTE), amPm);
            case DATEFORMAT_3:
                return format(gCal, DATEFORMAT_1) + " " + format(gCal, DATEFORMAT_2);
            case DATEFORMAT_4:
                return String.format("%02d.%02d.%d", (gCal.get(GregorianCalendar.MONTH) + 1), 
                        gCal.get(GregorianCalendar.DAY_OF_MONTH), gCal.get(GregorianCalendar.YEAR));
        }
        
        throw new IllegalArgumentException("Unknown format type: " + formatType);
    }
}
