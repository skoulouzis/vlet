package nl.uva.vlet.gui.viewers.grid.jobmonitor;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import nl.uva.vlet.gui.panels.resourcetable.ResourceTable;
import nl.uva.vlet.gui.panels.resourcetable.TablePopupMenu;

public class JobMonitorMenu extends TablePopupMenu
{
    public static String REFRESH="Refresh"; 
    
    private static final long serialVersionUID = 4730955073412271989L;
    private JMenuItem actionItem;

    public JobMonitorMenu(JobMonitorController controller)
    {
        super();
        
        actionItem=new JMenuItem("Refresh");
        this.add(actionItem);
        actionItem.setActionCommand(REFRESH); 
        
        this.add(new JSeparator());
        actionItem=new JMenuItem("Refresh");
        this.add(actionItem);
        actionItem.addActionListener(controller); 
        
        this.add(new JSeparator()); 
    }
    
    @Override
    public void updateFor(ResourceTable table, MouseEvent e,boolean canvasMenu)
    {
    }
    
}
