package pipe.modules.clientCommon;

/*
 * This class is based of the PetriNetChooser found in
 * pipe.gui.widget but has been updated to open files immediately upon selection
 * so that place data can be loaded.
 * Barry Kearns - July 2007
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import pipe.common.dataLayer.DataLayer;
import pipe.gui.widgets.FileBrowser;

/**
 * This class builds upon the existing PetriNetChooserPanel
 * the primary difference is that this Panel loads the Petri net from the file
 * immediatey when it is selected. This allows the UI to update with the currently
 * active Petri net.
 * Particular uses of this class can extend the updateUI method to suit their own UI requirements
 * 
 * @author Barry Kearns
 * @date August 2007
 */
public class PetriNetBrowsePanel extends JPanel
{
 
	private static final long serialVersionUID = 1L;
	protected JCheckBox  useCurrent;
	protected JLabel     label;
	protected JTextField textField;
	protected JButton    browse;
  
  /* Three reference are used: one for the current net from the editor,
   * one for the net loaded from the file and
   * a reference to one of the above depending on which is selected
   * This is useful for dynamic user interface updates
   */   
  protected DataLayer canvasNet, fileNet = null, selectedNet;
  
  
  public PetriNetBrowsePanel(String title,DataLayer currNet)
  {
    super();
    
    this.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
    
    selectedNet = canvasNet = currNet;
    if (currNet!=null)
    {
      useCurrent=new JCheckBox("Use current net",true);
      useCurrent.addActionListener(useCurrentClick);
      this.add(useCurrent);
      this.add(Box.createHorizontalStrut(10));
    }
    
    label=new JLabel("Filename:");
    this.add(label);
    this.add(Box.createHorizontalStrut(5));
    
    textField=new JTextField((currNet!=null?currNet.getURI():null),15);
    this.add(textField);
    this.add(Box.createHorizontalStrut(5));
    
    browse=new JButton("Browse");
    browse.addActionListener(browseButtonClick);
    this.add(browse);
    
    this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),title));
    
    this.setMaximumSize(new Dimension(Integer.MAX_VALUE,this.getPreferredSize().height));
    
    if(useCurrent!=null) useCurrent.getActionListeners()[0].actionPerformed(null); // update 
  }
  
  private ActionListener useCurrentClick=new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      boolean enabled=!useCurrent.isSelected();
      label.setEnabled(enabled);
      textField.setEnabled(enabled);
      browse.setEnabled(enabled);
      
      if(useCurrent.isSelected())
    	  selectedNet = canvasNet;
      else
    	  selectedNet = fileNet;
      
      updateUIList();
    }
  };
   

  
  private ActionListener browseButtonClick=new ActionListener() {
    public void actionPerformed(ActionEvent event)
    {
      File pnmlFile = new FileBrowser(textField.getText()).openFile();
      if(pnmlFile != null)
      {
    	  String fileName = pnmlFile.getAbsolutePath();
    	  textField.setText(fileName);
    	  
    	  try
    	  {
    	     selectedNet = fileNet=new DataLayer(fileName);
    	     updateUIList();
    	  }
    	  catch (Exception exp)
    	  {
    	      JOptionPane.showMessageDialog(null, "Error loading\n"+fileName+"\nPlease check it is a valid PNML file.","Error",JOptionPane.ERROR_MESSAGE);
    	  }    	  
      }
    }
  };
  
  // This method can overridden by extending the class - allowing specific updating
  protected void updateUIList(){};
  
  
  public DataLayer getDataLayer()
  {
    return selectedNet;    
  }
} 

