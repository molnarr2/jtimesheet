/*
 * Main.java
 *
 * Created on April 18, 2007, 4:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtimesheet;

import jtimesheet.mlnr.TimeSheet;

/**
 *
 * @author Robert Molnar 2
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        TimeSheet timesheet = new TimeSheet();
        timesheet.run();
    }
    
}
