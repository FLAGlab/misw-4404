package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * This class is used by the analysis modules to display the results
 * of their analysis as HTML.
 * 
 * @author unknown
 * @author David Patterson	(change only)
 * 
 * @throws RuntimeException if the parameter to the constructor is null
 * and a temporary file cannot be created. 
 * 
 * Changes:
 * 1) Jan 14, 2007 Changed the constructor so that it checks for a null
 *    parameter. It will get a null parameter if you draw a net rather than
 *    opening an XML file containing the net. In this case, the code now 
 *    creates a temporary file so it can have a valid path. The temp file
 *    is never opened. 
 */
public class ResultsHTMLPane extends JPanel implements HyperlinkListener {
  JEditorPane results;
  File defaultPath;
  boolean tempDefaultPath;
  Clipboard clipboard=this.getToolkit().getSystemClipboard();
  
  public ResultsHTMLPane(){
	  
  }
  
  public ResultsHTMLPane(String path) {
    super(new BorderLayout());
    
    // Change Jan 14, 2007 by David Patterson
    // When you have drawn a net and not saved it, the path
	// field is null at this point.
	if ( path == null )		
	{
		tempDefaultPath = true;
		try
		{
			defaultPath = File.createTempFile(  "PIPE", ".xml" ).getParentFile();
		}
		catch (IOException e)
		{
			throw new RuntimeException( 
				"Cannot create temp file. Save net before running analysis modules." );
		}
	}
	else
	{
		tempDefaultPath = false;
		defaultPath = new File(path);
		if (defaultPath.isFile())
		{
			defaultPath = defaultPath.getParentFile();
		}
	}

    results=new JEditorPane();
    results.setEditable(false);
    results.setMargin(new Insets(5,5,5,5));
    results.setContentType("text/html");
    results.addHyperlinkListener(this);
    JScrollPane scroller=new JScrollPane(results);
    scroller.setPreferredSize(new Dimension(400,300));
    scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
    this.add(scroller);
    this.add(new ButtonBar(new String[]{"Copy","Save"},new ActionListener[]{CopyHandler,SaveHandler}),BorderLayout.PAGE_END);
    this.setBorder(new TitledBorder(new EtchedBorder(),"Results"));
  }
  
  public void setText(String text) {
    results.setText("<html><head><style type=\"text/css\">" +
        "body{font-family:Arial,Helvetica,sans-serif;text-align:center;background:#ffffff}" +
        "td.colhead{font-weight:bold;text-align:center;background:#ffffff}" +
        "td.rowhead{font-weight:bold;background:#ffffff}"+
        "td.cell{text-align:center;padding:5px,0}" +
        "tr.even{background:#a0a0d0}" +
        "tr.odd{background:#c0c0f0}" +
        "td.empty{background:#ffffff}" +
        "</style></head><body>"+text+"</body></html>");
    results.setCaretPosition(0); // scroll to top
  }
  
  public String getText(){
  	return results.getText();
  }
  
  public static String makeTable(Object[] items,int cols,boolean showLines,boolean doShading,boolean columnHeaders,boolean rowHeaders) {
    String s="<table border="+(showLines?1:0)+" cellspacing=2>";
    int j=0;
    for(int i=0;i<items.length;i++) {
      if (j==0) s+="<tr"+(doShading?" class="+(i/cols%2==1?"odd":"even"):"")+">";
      s+="<td class=";
      if(i==0&&items[i]=="")s+="empty";
      else if((j==0)&&rowHeaders)s+="rowhead";
      else if((i<cols)&&columnHeaders)s+="colhead";
      else s+="cell";
      s+=">"+items[i]+"</td>";
      if (++j==cols) {
        s+="</tr>";
        j=0;
      } 
    }
    return s+"</table>";
  }
  private ActionListener CopyHandler=new ActionListener() {
    public void actionPerformed(ActionEvent arg0) {
      StringSelection data=new StringSelection(results.getText());
      try {
        clipboard.setContents(data,data);
			} catch (IllegalStateException e) {
        System.out.println("Error copying to clipboard, seems it's busy?");
			}
    }
  };
  private ActionListener SaveHandler=new ActionListener() {
  	public void actionPerformed(ActionEvent arg0) {
  		try
  		{
  			FileBrowser fileBrowser=new FileBrowser("HTML file","html", defaultPath.getPath());
  			String destFN=fileBrowser.saveFile();
  			if(!destFN.toLowerCase().endsWith(".html")) destFN+=".html";
  			FileWriter writer=new FileWriter(new File(destFN));
  			String output = "<html><head><style type=\"text/css\">" +
  	        "body{font-family:Arial,Helvetica,sans-serif;text-align:center;background:#ffffff}" +
	        "td.colhead{font-weight:bold;text-align:center;background:#ffffff}" +
	        "td.rowhead{font-weight:bold;background:#ffffff}"+
	        "td.cell{text-align:center;padding:5px,0}" +
	        "tr.even{background:#a0a0d0}" +
	        "tr.odd{background:#c0c0f0}" +
	        "td.empty{background:#ffffff}" +
	        "</style>"+results.getText();
  			writer.write(output);
  			writer.close();
  		} catch (Exception e) {
  			System.out.println("Error saving HTML to file");
  		}
  	}
  };
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
      if(e.getDescription().startsWith("#")) results.scrollToReference(e.getDescription().substring(1));
      else
        try {
          results.setPage(e.getURL());
        } catch (IOException ex) {
          System.err.println("Error changing page to "+e.getURL());
        }
  }
} 
