package nl.uva.vlet.gui.viewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuConstants;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.util.vlterm.VLTerm;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.io.VShellChannel;
import nl.uva.vlet.vrs.io.VShellChannelCreator;

public class VLTermStarter extends ViewerPlugin implements ActionListener
{
    public static void viewStandAlone(VRL loc)
    {
        VLTermStarter tv = new VLTermStarter();

        try
        {
            tv.startAsStandAloneApplication(loc); 
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }
    }
    
    public static void main(String args[])
    {
        try
        {
            viewStandAlone(new VRL("ssh://lena.nikhef.nl/user/ptdeboer")); 
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }
    
	private static final long serialVersionUID = 3709591457664399612L;
    private JPanel panel;
    private JButton okButton;
    private JTextField textF;
    private JPanel butPanel;

	@Override
	public String getName() 
	{
	    return "VLTerm";
	}
	
	@Override
	public String[] getMimeTypes() 
	{
		return new String[]{"application/ssh-location"}; 
	}
	
	@Override
	public void initViewer()
	{
		initGUI(); 
	}

	public boolean getAlwaysStartStandalone()
	{
		 return true;
	}
	 
	private void initGUI() 
	{
	    
	    
	    {
	        panel=new JPanel();
	        panel.setPreferredSize(new Dimension(250,60)); 
	        
	        this.add(panel); 
            panel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
	        panel.setLayout(new BorderLayout()); 
	        {
	            textF = new JTextField(); 
	            textF.setText("\nA VLTerm will be started.\n "); 
	            
	            panel.add(textF,BorderLayout.CENTER);
	        }
	        {
	            butPanel= new JPanel();
	            panel.add(butPanel,BorderLayout.SOUTH);
	            butPanel.setLayout(new FlowLayout()); 
	           // butPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));

    	        {
    	            okButton=new JButton(); 
    	            okButton.setText("OK"); 
    	            butPanel.add(okButton); 
    	            okButton.addActionListener(this); 
    	        }
	        }
	    }
	        
	}

	public void startViewer(VRL location, String optMethodName, ActionContext actionContext) throws VlException
    {
        setVRL(location);

        // update location
        if (location != null)
            updateLocation(location);
        
        // perform action method: 
        if (StringUtil.isNonWhiteSpace(optMethodName))
            this.doMethod(optMethodName,actionContext); 
    
     
    }

	
	@Override
	public void updateLocation(VRL loc) throws VlException 
	{
		startVLTerm(loc); 
	}

	

	@Override
	public void stopViewer()
	{
		
	}

	@Override
	public void disposeViewer()
	{
		
	}

	
	public void startVLTerm(VRL loc) 
	{
	    
	    VLTerm term=null; 
	    
        try
        {
            VFSNode node;
            node = UIGlobal.getVFSClient().openLocation(loc);
            
            if (node.isFile())
                node=node.getParent();
            
            VFileSystem vfs = node.getFileSystem();
            
            if (vfs instanceof VShellChannelCreator)
            {
                VShellChannel shellChan = ((VShellChannelCreator)vfs).createShellChannel(loc);
                term=VLTerm.newVLTerm(shellChan); 
            }
            else
            {
                term=VLTerm.newVLTerm(loc);
            }
        }
        catch (VlException e)
        {
            handle(e);
        }
        
        try
        {
            Thread.sleep(1);
        }
        catch (InterruptedException e)
        {
         //   e.printStackTrace();
        } 
        
        exitViewer(); 
        // this.disposeJFrame(); 
		 
	}
	
	public Vector<ActionMenuMapping> getActionMappings()
    {
        Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
        
        // LFC File mappings:
        ActionMenuMapping mapping;         
        
        // single selection action: 
        mapping=new ActionMenuMapping("openVLTerm","Open VLTerm");
        // Sftp Dirs: 
        mapping.addTypeSchemeMapping(VFS.DIR_TYPE,VFS.SFTP_SCHEME); 
        
        mapping.setMenuOptions(ActionMenuMapping.DEFAULT_MENU_OPTIONS 
        		| ActionMenuConstants.MENU_CANVAS_ACTION); 
        //mapping.addResourceMapping(fileTypes,schemes,null,null,ActionMenuConstants.SELECTION_ONE);        
        mappings.add(mapping);
        
        return mappings; 
    }
	
	/** Perform Dynamic Action Method */
    public void doMethod(String methodName, ActionContext actionContext)
            throws VlException
    {
    	// already handled by first openLocation. This Method will be called
    	// after the VLTermStarter already started for this location. 
    	//VRL loc=actionContext.getSource();
    	//System.err.println("location="+loc); 
    	//startVLTerm(loc); 
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.disposeJFrame(); 
    }
}
