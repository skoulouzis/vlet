package nl.uva.vlet.vfs.srm;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vrl.VRL;

public class DirQuery
{
    public static final String PAR_SRMCOUNT="srmCount";
    
    public static final String PAR_SRMOFFSET="srmOffset"; 
    
    private int srmCount=-1; 
    
    private int srmOffset=-1;

    public DirQuery(String[] qstrs)
    {   
        parse(qstrs); 
    }

    protected void parse(String[] qstrs)
    {
        // check for srmCount and srmOffset 
        for (String str:qstrs)
        {
            String strs[]=str.split("=");
            if ((strs!=null) && (strs.length>1))
            {
                String name=strs[0]; 
                String value=strs[1]; 
                
                // use error save StringUtil. 
                int intVal=StringUtil.parseInt(value,-1); 
                
                if (name.compareTo(PAR_SRMCOUNT)==0)
                {
                    this.srmCount=intVal; 
                }
                else if (name.compareTo(PAR_SRMOFFSET)==0)
                {
                    this.srmOffset=intVal;
                }
            }
        }
    }
    
    public int getCount()
    {
        return this.srmCount; 
    }
    
    public int getOffset()
    {
        return this.srmOffset; 
    }

    /** Check whether VRL has dir query parameters and return DirQuery object or null */
    public static DirQuery parseDirQuery(VRL vrl)
    {
        String qstrs[]=vrl.getQueryParts(); 
        
        if ((qstrs==null) || ( qstrs.length<0))
            return null; 
        
        boolean hasDirPar=false; 
           
        for (String qstr:qstrs)
        {
            if (qstr==null)
                continue;
            
            String vals[]=qstr.split("=");
                
            String par=vals[0]; 
            if (par==null)
               continue; 
               
            if (par.equals(PAR_SRMCOUNT))
                hasDirPar=true; 
                    
            if (par.equals(PAR_SRMOFFSET))
                hasDirPar=true;
        }
            
        if (hasDirPar==false)
            return null; 
            
        return new DirQuery(qstrs); 
    }
    
    public String toString()
    {
        String qstr="";
        
        if (srmOffset>=0) 
            qstr="srmOffset="+srmOffset+"&"; 
           
        if (srmCount>=0) 
            qstr="srmCount="+srmCount; // append ending '&' ? 
        
        return qstr; 
    }
}
