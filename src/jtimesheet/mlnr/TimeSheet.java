/*
 * TimeSheet.java
 *
 * Created on April 18, 2007, 5:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtimesheet.mlnr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import jtimesheet.mlnr.util.MolnarFormatter;
import jtimesheet.mlnr.util.gui.DialogFileChooser;
import jtimesheet.mlnr.util.gui.FileNameFilter;


/**
 *
 * @author Robert Molnar 2
 */
public class TimeSheet {
    /** List of job entries. */
    LinkedList<JobEntry> ltJobs = new LinkedList();
    
    
    /** Creates a new instance of TimeSheet */
    public TimeSheet() {
    }
    
    /** This will run the program.
     */
    public void run() {
        // Get the file.
        DialogFileChooser dialog = new DialogFileChooser("TimeSheet", new FileNameFilter("csv", "comma delimited file"), "Time Sheet");
        if (dialog.showLoadDialog(null, "", null) == false)
            return;
        
        // Load the file.
        try {
            loadTimesheet(dialog.getFile());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to load file, Exception: " + e);
            e.printStackTrace();
            return;
        }
        
        
        // Get the start and end dates.
        GregorianCalendar gStart = null;
        GregorianCalendar gUpto = null;
        try {
            String dateStart = new JOptionPane().showInputDialog("Begin Date: MM.DD.YYYY");
            String dateUpto = new JOptionPane().showInputDialog("Up to Date: MM.DD.YYYY");
            gStart = convertToDate(dateStart);
            gUpto = convertToDate(dateUpto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to convert Dates, Exception: " + e);
            e.printStackTrace();
            return;
        }
        
        // Name of output file.
        String outputFileName = "/Users/rmolnar/Documents/Timesheets/Timesheet from " + MolnarFormatter.format(gStart, MolnarFormatter.DATEFORMAT_4) + " up to "
            + MolnarFormatter.format(gUpto, MolnarFormatter.DATEFORMAT_4) + ".html";
        
        // Create report on it.
        try {
            ReportTimeSheet reportTimeSheet = new ReportTimeSheet(gStart, gUpto);
            reportTimeSheet.printReportDetail(new File(outputFileName));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to create report, Exception: " + e);
            e.printStackTrace();
        }
    }
    
    /** This will load the timesheet into this class.
     * @param fTimeSheet is the timesheet to load into this class.
     */
    private void loadTimesheet(File fTimeSheet) throws IOException {
        BufferedReader breader = new BufferedReader(new FileReader(fTimeSheet));
        
        // Make sure correct file type.
        String lineOfData = breader.readLine();
        if (lineOfData == null)
            return;
        StringTokenizer stringtok = new StringTokenizer(lineOfData, ",");
        if (!stringtok.hasMoreElements() || "ID".equals(stringtok.nextToken()) == false)
            throw new IllegalArgumentException("Incorrect file type.");
        if (!stringtok.hasMoreElements() || "JTIMESHEET10".equals(stringtok.nextToken()) == false)
            throw new IllegalArgumentException("Incorrect file type.");
        
        // Load in the time entries.
        while ((lineOfData = breader.readLine()) != null) {
            stringtok = new StringTokenizer(lineOfData, ",");
            
            // Get the string date.
            if (!stringtok.hasMoreElements())
                throw new IllegalArgumentException("Line [" + lineOfData + "]: incorrect format. ");
            String stringDate = stringtok.nextToken();
            
            // Get the start time.
            if (!stringtok.hasMoreElements())
                throw new IllegalArgumentException("Line [" + lineOfData + "]: incorrect format. ");
            String stringStartTime = stringtok.nextToken();
            
            // Get the end time.
            if (!stringtok.hasMoreElements())
                throw new IllegalArgumentException("Line [" + lineOfData + "]: incorrect format. ");
            String stringEndTime = stringtok.nextToken();
            
            // Get the job entry.
            if (!stringtok.hasMoreElements())
                throw new IllegalArgumentException("Line [" + lineOfData + "]: incorrect format. ");
            String stringJobEntry = stringtok.nextToken();
            
            // Add the entry.
            addJobEntry(stringDate, stringStartTime, stringEndTime, stringJobEntry);
        }
        
        breader.close();
    }
    
    /** This will add a job entry to the list of jobs.
     * @param date is the date of time entry.
     * @param startTime is the start time of the time entry.
     * @param endTime is the end time of the time entry.
     * @param jobEntry is the name of the job.
     */
    private void addJobEntry(String date, String startTime, String endTime, String jobEntry) {
        // Get the job for this time entry or add a new job.
        JobEntry job = getJobEntry(jobEntry);
        if (job == null) {
            job = new JobEntry(jobEntry);
            ltJobs.add(job);
        }
        
        // Add the time entry to the job.
        job.addTime(convertToDate(date, startTime), convertToDate(date, endTime));
    }
    
    /** This will convert the date string and time string into a GregorianCalendar.
     * @param date in the format of MM.DD.YYYY
     * @return the date and time as a GregorianCalendar.
     */
    private GregorianCalendar convertToDate(String date) {
        try {
            StringTokenizer tokenDate = new StringTokenizer(date, ".");
            int month = Integer.parseInt(tokenDate.nextToken()) - 1;
            int day = Integer.parseInt(tokenDate.nextToken());
            int year = Integer.parseInt(tokenDate.nextToken());
            
            // Now Convert to GregorianCalendar.
            return new GregorianCalendar(year, month, day);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("date: '" + date + " incorrect format.");
        }
    }
    
    /** This will convert the date string and time string into a GregorianCalendar.
     * @param date in the format of MM.DD.YYYY
     * @param time in the format of HH:MM AM/PM
     * @return the date and time as a GregorianCalendar.
     */
    private GregorianCalendar convertToDate(String date, String time) {
        try {
            StringTokenizer tokenDate = new StringTokenizer(date, ".");
            int month = Integer.parseInt(tokenDate.nextToken()) - 1;
            int day = Integer.parseInt(tokenDate.nextToken());
            int year = Integer.parseInt(tokenDate.nextToken());
            
            StringTokenizer tokenTime = new StringTokenizer(time, " :");
            if (tokenTime.countTokens() != 3)
                throw new IllegalArgumentException("count");
            int hour = Integer.parseInt(tokenTime.nextToken());
            int minute = Integer.parseInt(tokenTime.nextToken());
            String amPm = tokenTime.nextToken();
            if (amPm.equalsIgnoreCase("PM")) {
                if (hour != 12)
                    hour += 12;
            } else if (hour == 12) {
                hour = 0;
            }
            
            // Now Convert to GregorianCalendar.
            return new GregorianCalendar(year, month, day, hour, minute);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("date: '" + date + "' time: '" + time + "' incorrect format.");
        }
    }
    
    /** This will get the job entry.
     * @param jobEntry is the name of the job entry.
     * @return the job entry or null if not found.
     */
    private JobEntry getJobEntry(String jobEntry) {
        for (Iterator<JobEntry> itr = ltJobs.iterator(); itr.hasNext(); ) {
            JobEntry job = itr.next();
            if (job.getJobName().equals(jobEntry))
                return job;
        }
        
        return null;
    }
    
    /** This class is used to produce the report.
     */
    class ReportTimeSheet {
        /** Is the start of the report, uses date only. This date is included in the report.*/
        GregorianCalendar gStart;
        /* Is the end of the report, uses date only no time. This date is excluded in the report. */
        GregorianCalendar gUpTo;
        /** List of job entries included in this report. */
        LinkedList<JobEntry> ltReportJobs = new LinkedList();
        
        /**
         * @param gStart is the start of the report, uses date only. This date is included in the report.
         * @param gUpTo is the end of the report, uses date only no time. This date is excluded in
         *  the report.
         */
        ReportTimeSheet(GregorianCalendar gStart, GregorianCalendar gUpTo) {
            this.gStart = gStart;
            this.gUpTo = gUpTo;
            
            // Create a list of jobs and time entries.
            buildReport(gStart, gUpTo);
        }
        
        /** This will print out the report in detail.
         * @param fOutput is the file to print the report to.
         */
        private void printReportDetail(File fOutput) throws IOException {
            PrintWriter out = new PrintWriter(fOutput);
            sortReport();
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Time Tracker Report From " + MolnarFormatter.format(gStart, MolnarFormatter.DATEFORMAT_1)
            + " To " + MolnarFormatter.format(gUpTo, MolnarFormatter.DATEFORMAT_1) + "</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("Time Tracker Report From " + MolnarFormatter.format(gStart, MolnarFormatter.DATEFORMAT_1)
            + " To " + MolnarFormatter.format(gUpTo, MolnarFormatter.DATEFORMAT_1) + "<br><br>");
            
            // Print out the jobs.
            float total = 0.0f;
            out.println("<table>");
            for (Iterator<JobEntry> itr = ltReportJobs.iterator(); itr.hasNext(); ) {
                JobEntry entry = itr.next();
                
                // Print out the report on the job.
                entry.printReportDetail(out);
                
                out.println("<tr><td><br></td></tr>");
                
                // Tally up the total time hours.
                total += entry.getTimeHours();
            }
            out.println("</table>");
            
            out.println("<b>Total Time: " + String.format("%.3f", total) + " Hours</b>");
            out.println("</body>");
            out.println("</html>");
            
            out.close();
        }
        
        /** This will sort the report by the job name and each job will have its time sorted.
         */
        private void sortReport() {
            // Sort the reported jobs based on the name of the job.
            List<JobEntry> list = ltReportJobs.subList(0, ltReportJobs.size());
            if (list == null)
                return;
            Collections.sort(list);
        }
        
        /** This will build a report between the start and end dates.
         * @param gStart is the start of the report, uses date only. This date is included in the report.
         * @param gUpTo is the end of the report, uses date only no time. This date is excluded in
         *  the report.
         */
        private void buildReport(GregorianCalendar gStart, GregorianCalendar gUpTo) {
            ltReportJobs = new LinkedList();
            
            // Search through all jobs.
            for (Iterator<JobEntry> itr = ltJobs.iterator(); itr.hasNext(); ) {
                JobEntry job = itr.next();
                
                // Create a job entry of the current job and only select the times between gStart and
                // gUpTo. Return null if no times.
                JobEntry reportJob = job.buildReport(gStart, gUpTo);
                if (reportJob != null)
                    ltReportJobs.add(reportJob);
            }
        }
    }
}
