/*
 * JobEntry.java
 *
 * Created on April 18, 2007, 4:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtimesheet.mlnr;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import jtimesheet.mlnr.util.MolnarFormatter;

/**
 *
 * @author Robert Molnar 2
 */
public class JobEntry implements Comparable {
    /** List of time entries. */
    LinkedList<TimeEntry> ltTimes = new LinkedList();
    /** Name of the job. */
    String jobName;
    
    /** Creates a new instance of JobEntry
     * @param jobName is the name of the job.
     */
    public JobEntry(String jobName) {
        this.jobName = jobName;
    }
    
    /** @return The name of the job.
     */
    public String getJobName() {
        return jobName;
    }
    
    /** This will add the time to this JobEntry.
     * @param gStartTime is the start time of the time entry.
     * @param gEndTime is the end time of the time entry.
     */
    public void addTime(GregorianCalendar gStartTime, GregorianCalendar gEndTime) {
        ltTimes.add(new TimeEntry(gStartTime, gEndTime));
    }
    
    public String toString() {
        return "{JobEntry: jobName[" + jobName + "] ltTimes [" + Arrays.toString(ltTimes.toArray())
        + "]}";
    }
    
    /** This will build a job report between the start and end dates.
     * @param gStart is the start of the report, uses date only. This date is included in the report.
     * @param gUpTo is the end of the report, uses date only no time. This date is excluded in
     *  the report.
     * @return null if no times are between gStart and gUpTo, else it will return a new JobEntry of all
     * times between gStart and gUpTo.
     */
    JobEntry buildReport(GregorianCalendar gStart, GregorianCalendar gUpTo) {
        JobEntry jobReport = new JobEntry(this.jobName);
        
        // Search for any times between gStart and gUpTo.
        for (Iterator<TimeEntry> itr = ltTimes.iterator(); itr.hasNext(); ) {
            TimeEntry time = itr.next();
            if (time.isBetweenDates(gStart, gUpTo))
                jobReport.addTime(time.getStartTime(), time.getEndTime());
        }
        
        // No times within the start-upto time.
        if (jobReport.ltTimes.size() == 0)
            return null;
        
        return jobReport;
    }

    /** This will print out the report in detail in html.
     */
    public void printReportDetail(PrintWriter out) throws IOException {
        out.println("<tr><td colspan=2><b><u>" + jobName + ": Total Time " + String.format("%.3f" , getTimeHours()) +  "</u></b></td></tr>");
        
        // Print out each time.
        for (Iterator<TimeEntry> itr = ltTimes.iterator(); itr.hasNext(); ) {
            TimeEntry time = itr.next();
            time.printReportDetail(out);
        }
        
    }
    
    /** @return the total time in hours.
     */
    public float getTimeHours() {
        long milliTotalTime = 0;
        
        // Calculate total time.
        for (Iterator<TimeEntry> itr = ltTimes.iterator(); itr.hasNext(); ) {
            TimeEntry time = itr.next();
            milliTotalTime += time.getWorkTime();
        }
       
        // Convert time from milliseconds to hours.
        double timeInHours = (double)milliTotalTime / 3600000.0;
        return (float)timeInHours;
    }    
    
    public int compareTo(Object o) {
        return this.jobName.compareTo(((JobEntry)o).jobName);
    }
}

class TimeEntry {
    private GregorianCalendar gStartTime;
    private GregorianCalendar gEndTime;
    
    /** Create a time entry.
     */
    TimeEntry(GregorianCalendar gStartTime, GregorianCalendar gEndTime) {
        this.gStartTime = gStartTime;
        this.gEndTime = gEndTime;
        
        if (gStartTime.getTimeInMillis() > gEndTime.getTimeInMillis()) {
            String startTime = MolnarFormatter.format(gStartTime, MolnarFormatter.DATEFORMAT_2);
            String endTime = MolnarFormatter.format(gEndTime, MolnarFormatter.DATEFORMAT_2);
            throw new IllegalArgumentException("Start time [" + startTime + "] is greater than End time [" + endTime + "]. ");
        } else if (getWorkTime() > 28800000L) {
            String startTime = MolnarFormatter.format(gStartTime, MolnarFormatter.DATEFORMAT_2);
            String endTime = MolnarFormatter.format(gEndTime, MolnarFormatter.DATEFORMAT_2);
            throw new IllegalArgumentException("Start time [" + startTime + "], End time [" + endTime + "]: Working time is greater than 8 hours. ");
        }        
    }
    
    /** @return the number of milliseconds from this TimeEntry.
     */
    public long getWorkTime() {
        return gEndTime.getTimeInMillis() - gStartTime.getTimeInMillis();
    }
    
    /** @return the time in hours.
     */
    public float getTimeHours() {
        // Convert time from milliseconds to hours.
        double timeInHours = (double)getWorkTime() / 3600000.0;
        return (float)timeInHours;
    }    
    
    /** This will see if the time of work is between these two dates.
     * @param gStart is the start of the report, uses date only. This date is included in the report.
     * @param gUpTo is the end of the report, uses date only no time. This date is excluded in
     *  the report.
     * @return true if the time of work is between these two dates.
     */
    public boolean isBetweenDates(GregorianCalendar gStart, GregorianCalendar gUpTo) {
        if (gStartTime.after(gStart) && gStartTime.before(gUpTo))
            return true;
        return false;
    }

    public GregorianCalendar getStartTime() {
        return gStartTime;
    }
    
    public GregorianCalendar getEndTime() {
        return gEndTime;
    }
    /** This will print out the report in detail in html.
     */
    public void printReportDetail(PrintWriter out) throws IOException {
        out.println("<tr><td>" + MolnarFormatter.format(gStartTime, MolnarFormatter.DATEFORMAT_3) + " to " 
            + MolnarFormatter.format(gEndTime, MolnarFormatter.DATEFORMAT_2) + "</td><td><b>" + String.format("%.3f" , getTimeHours()) + " Hours</b></td></tr>");
    }    
    
    public String toString() {
        return "{TimeEntry: startTime[" + MolnarFormatter.format(gStartTime, MolnarFormatter.DATEFORMAT_2)
        + "] endTime[" + MolnarFormatter.format(gEndTime, MolnarFormatter.DATEFORMAT_2) + "]";
    }
}

