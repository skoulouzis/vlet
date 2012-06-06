package nl.uva.vlet.gui.proxynode;

import java.util.List;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.gui.view.ViewNode;

public class ViewNodeFactory
{

    public static ViewNode[] createFrom(ViewModel model,ProxyNode[] nodes)
    {
        return createFrom(model,nodes,ViewNode.DEFAULT_ICON_SIZE); 
    }
    
    public static ViewNode[] createFrom(ViewModel model,ProxyNode[] nodes, int size)
    {
        if (nodes==null)
            return null;
        
        ViewNode items[]=new ViewNode[nodes.length]; 
        
        for (int i=0;i<nodes.length;i++)
        {
            items[i]=createViewNode(nodes[i],size); 
        }
        
        return items; 
    }
    
    static public ViewNode[] createFrom(List<ProxyNode> nodes,int size)
    {
        ViewNode items[]=new ViewNode[nodes.size()]; 
        
        for (int i=0;i<nodes.size();i++)
        {
            items[i]=createViewNode(nodes.get(i),size); 
        }
        
        return items; 
    }
    
    static public ViewNode createViewNode(ProxyNode pnode) 
    {
        ViewNode viewNode=new ViewNode(pnode.getVRL(),pnode.getType(),pnode.isComposite());
        // use setters 
        viewNode.setName(pnode.getName());
        viewNode.setComposite(pnode.isComposite());
        viewNode.setBusy(pnode.isBusy());
        viewNode.setResourceLink(pnode.isResourceLink());
        viewNode.setMimeType(pnode.getMimeType());
        //this.aliasVrl=pnode.getAliasVRL();  
        
        //Experimental: Prefetch Target Link!
        if (viewNode.isResourceLink())
        {
            try
            {
                viewNode.setTargetVrl(pnode.getTargetVRL());
            }
            catch (VlException e)
            {
               // Global.warnPrintln(this,"Couldn't prefetch LinkTarget:"+pnode);
              //  Global.warnPrintln(this,"Exception="+e); 
                // error: keep as null; 
            }
        }
        
        initIcons(viewNode,pnode,ViewNode.DEFAULT_ICON_SIZE);
        
        return viewNode; 
    }
    
    static public ViewNode createViewNode(ProxyNode pnode,int defaultIconSize)
    {
        ViewNode viewNode=createViewNode(pnode);
        initIcons(viewNode, pnode,defaultIconSize);
        return viewNode;
    }
    
    static private void initIcons(ViewNode viewNode,ProxyNode pnode,int iconSize)
    {
        viewNode.setIcon(ViewNode.DEFAULT_ICON,pnode.getDefaultIcon(iconSize,false));
        viewNode.setIcon(ViewNode.SELECTED_ICON,pnode.getDefaultIcon(iconSize,true));
        viewNode.setIconSize(iconSize);
    }
    
   
    
}
