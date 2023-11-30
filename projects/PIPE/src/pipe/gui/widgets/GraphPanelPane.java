/*
 * Created on 10-Mar-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import pipe.gui.CreateGui;
import pipe.gui.Export;

/**
 * @author mjg103
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GraphPanelPane extends JPanel {
  GraphPanel graph;
  public GraphPanelPane() {
    super(new BorderLayout());
    setBorder(new TitledBorder(new EtchedBorder(),"Results graph"));
    add(graph=new GraphPanel(),BorderLayout.CENTER);
    add(new ButtonBar("Save",saveButtonClick),BorderLayout.PAGE_END);
  }  
  
  private ActionListener saveButtonClick=new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      String filename=null;
      if(CreateGui.getFile()!=null) filename=CreateGui.getFile()+" DNAmaca output.png";
      filename=new FileBrowser("PNG image","png",filename).saveFile();
      if(filename!=null) try {
        graph.setIgnoreRepaint(true);
        Border b=graph.getBorder();
        graph.setBorder(null);
        Dimension d=graph.getPreferredSize(); // Export uses preferred size for exported file dimensions
        graph.setSize(640,480);
        graph.setPreferredSize(graph.getSize());
        Export.toPNG(graph,filename);
        graph.setPreferredSize(d);
        graph.setBorder(b);
        graph.setIgnoreRepaint(false);
      } catch (IOException ex) {
      }
    }
  };
  
  public synchronized GraphPanel getGraph() {
    return graph;
  }
}
