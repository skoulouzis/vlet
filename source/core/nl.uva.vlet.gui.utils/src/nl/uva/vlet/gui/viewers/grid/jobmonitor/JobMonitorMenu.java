package nl.uva.vlet.gui.viewers.grid.jobmonitor;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import nl.uva.vlet.gui.panels.resourcetable.ResourceTable;
import nl.uva.vlet.gui.panels.resourcetable.TablePopupMenu;

public class JobMonitorMenu extends TablePopupMenu
{
    private static final long serialVersionUID = 4730955073412271989L;
    private JMenuItem actionItem;

    private JMenuItem openJobItem;
    
    public JobMonitorMenu(JobMonitorController controller)
    {
        super();
        
        actionItem=new JMenuItem("Refresh All");
        this.add(actionItem);
        actionItem.setActionCommand(JobMonitor.ACTION_REFRESH_ALL); 
        actionItem.addActionListener(controller); 
        
        this.add(new JSeparator());
        openJobItem=new JMenuItem("Open Job");
        this.add(openJobItem);
        openJobItem.setActionCommand(JobMonitor.ACTION_OPEN_JOB); 
        openJobItem.addActionListener(controller); 
        openJobItem.setEnabled(false); 
        
        this.add(new JSeparator()); 
    }
    
    @Override
    public void updateFor(ResourceTable table, MouseEvent e,boolean canvasMenu)
    {
    	String rowKey=table.getKeyUnder(e.getPoint()); 
    	
    	if ((rowKey==null) || (canvasMenu==true))
    	{
    		openJobItem.setEnabled(false); 
    		rowKey="";
    	}
    	else
    	{
    		openJobItem.setEnabled(true); 
    	}
    	
		openJobItem.setActionCommand(JobMonitor.ACTION_OPEN_JOB+","+rowKey); 
    }
    
}
