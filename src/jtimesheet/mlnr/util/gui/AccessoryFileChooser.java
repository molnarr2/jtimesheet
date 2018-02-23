/*
 * AccessoryFileChooser.java
 *
 * Created on September 15, 2006, 3:58 PM
 *
 */

package jtimesheet.mlnr.util.gui;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 *
 * @author Robert Molnar II
 */
abstract public class AccessoryFileChooser extends JComponent implements PropertyChangeListener {    
    abstract public void setJFileChooser(JFileChooser jfc);    
}
