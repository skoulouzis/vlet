package nl.uva.vlet.vrs.globusrs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

public class GlobusInfoNode extends VNode
{
	public static GlobusInfoNode createNode(VRSContext context,VRL vrl)
	{
		GlobusInfoNode node=new GlobusInfoNode(context,vrl);
		return node; 
	}	
	 
    public GlobusInfoNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
    }

	@Override
	public String getType() 
	{
		return "GlobusInfo"; 
	}

	@Override
	public boolean exists() throws VlException 
	{
		return true;
	}
	
	public String getName()
	{
		return "GlobusInfo";
	}
	
	//
	//public String getIconURL(int prefSize)
    //{
	//	return ;
    //}
    //

}

