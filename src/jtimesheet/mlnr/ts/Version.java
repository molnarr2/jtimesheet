/*
 * Version.java
 *
 * Created on December 9, 2006, 10:20 PM
 *
 */

package jtimesheet.mlnr.ts;

import jtimesheet.mlnr.ts.v1.Version_v1;


/**
 *
 * @author Robert Molnar II
 */
public class Version {
    
    /** Creates a new instance of Version */
    public Version() {
    }
    
    /** This will get the current version used in this build. This is done so that when
     * version 2,3,4,etc.. comes then there will not be any conflicts with data stored in
     * the Windows Registry. Version 1,2,3, etc.. will use different places to store there
     * data.
     * @return the class that represents the current version in this build.
     */
    static public Class getVersion() {
        return Version_v1.class;
    }
    
}
