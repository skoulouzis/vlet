/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: ImageViewer.java,v 1.5 2011-04-18 12:27:11 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:11 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.exception.ResourceException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.image.ImagePane;
import nl.uva.vlet.gui.image.ImagePane.ImageWaiter;
import nl.uva.vlet.vrl.VRL;


/**
 * Implementation of an Image Viewer.<br>
 */
public class ImageViewer extends InternalViewer 
{
    private static final long serialVersionUID = 5768234709523116729L;

    /** The mimetypes I can view */ 
    private static String mimeTypes[]={
        "image/gif",
        "image/jpeg",
        "image/bmp", 
        "image/png"
        };
    
    
    private static double zoomOutFactors[]=
	{
	    0.9,0.8,0.7,0.6,0.5,0.4,0.3,0.2,0.1 
	}; 
    
    private static double zoomInFactors[]=
	{
    	// 100,125,150,200,300,400,500,600,800,1000%
	    1.25,1.5,1.75,2,2.25,2.5,2.75,3,4,5,6,7,8,9,10
	}; 
    
	// ====================================================================
	//
	// ====================================================================
    ImagePane imagePane=null;
    // private ImageIcon icon=null;
	JScrollPane scrollPane;
	//private int offsetx;
	//private int offsety; 

	int zoomIndex = 0;
	double currentZoomFactor=1.0; 
	boolean fitToScreen=false;
	private Image orgImage; 
	
    //private JLabel imageLabel; // store image in Label Component
    
    @Override
    public String[] getMimeTypes()
    {
        return mimeTypes; 
    }
    
    public void initGui()
    {
        {
            this.setSize(800,600);
            
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout(thisLayout);
            //this.setLayout(null); // absolute layout
            
            {
            	this.scrollPane=new JScrollPane();
            	this.scrollPane.setSize(800,600);
                this.add(scrollPane,BorderLayout.CENTER); // addToRootPane(imagePane,BorderLayout.CENTER);
                {
                	this.imagePane=new ImagePane();
                    scrollPane.setViewportView(imagePane); 
                	this.imagePane.setSize(800,600);
                	this.imagePane.setLocation(0,0);
                }
            }
            
            //imagePane.setVisible(true);
            
            //this.imageLabel=new JLabel("ImageLabel:Loading image..."); 
            //this.add(this.imageLabel,BorderLayout.CENTER); 
            //this.imageLabel.setLocation(0,0);
            this.setToolTipText(getName());
        }

        // listeners: 
        {
        	new ImageController(this); 
        }
    }

    @Override
    public void initViewer()
    {
        initGui();
    }
    
    public static void viewStandAlone(VRL loc)
    {
        ImageViewer tv=new ImageViewer();
        
        try
        {
            tv.startAsStandAloneApplication(loc);
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:"+e); 
            e.printStackTrace();
        }
    }
    
    public void stopViewer()
    {
    	//this.muststop=true; 
        //this.imagePane.signalStop(); 
    }

    @Override
    public void disposeViewer()
    {
        // help the garbage collector, images can be big:
         this.imagePane.dispose(); 
         this.remove(imagePane);
         this.imagePane=null;
        
        //this.remove(imageLabel); 
        //this.imageLabel=null; 
    }
    
   
    @Override
    public String getName()
    {
    	// remove html color codes:
        return "ImageViewer"; 
    } 

    @Override
    public void startViewer(VRL loc) throws VlException
    {
        updateLocation(loc);
    }
    
    public void updateLocation(VRL location) throws VlException
    {
        debug("update location;"+location); 
        
    	// dynamic action ! 
    	if (location==null)
    		return; 
    	
       setBusy(true);
       
       try
       {
           
    	   // load image and wait: 
    	   //this.imagePane.loadImage(location,true); 
    	   this.imagePane.loadImage(location,false); 
           
    	   //keep original image for zoom purposes; 
    	   this.orgImage=imagePane.getImage();
    	   
    	   this.fitToScreen=false;
    	   
    	   this.zoomIndex=0; 
       }
       finally
       {
    	   setBusy(false);
       }
    }
    
    private void debug(String msg)
    {
        Global.debugPrintln(this, msg); 
    }
    
    /**
    // @Overide update&paint methods for speed: 
    public void update(Graphics g)
    {
        //this.setSize(imagePane.getSize()); 
        //this.setPreferredSize(imagePane.getSize());
        imagePane.paint(g); 
    }
    
    public void paint(Graphics g)
    {
        imagePane.paint(g); 
    }**/
    
    public Vector<ActionMenuMapping> getActionMappings()
	{
		ActionMenuMapping mapping=new ActionMenuMapping("viewImage", "View Image");
		// '/' is not a RE character
		Pattern patterns[]=new Pattern[mimeTypes.length];
		
		for (int i=0;i<mimeTypes.length;i++)
			patterns[i]=Pattern.compile(mimeTypes[i]); 
				
		mapping.addMimeTypeMapping(patterns);
		
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
		mappings.add(mapping); 
		return mappings; 
	}
	
	public void doMethod(String methodName, ActionContext actionContext) throws VlException
	{
		
		if (actionContext.getSource()!=null)
			this.updateLocation(actionContext.getSource());
		
	}
	
	

	private void Debug(String msg)
	{
		Global.debugPrintln(this,msg); 
	}

	public void componentShown(ComponentEvent e)
	{

	}
	
	/** I manage my own scrollpane for panning/autoscrolling */  
	public boolean haveOwnScrollPane()
	{
		return true; 
	}

	/** Get ScrollPane ViewPosition*/ 
	public Point getViewPosition()
	{
		JViewport viewP=this.scrollPane.getViewport(); 
		return viewP.getViewPosition(); 
	}
	
	/** Set ScrollPane ViewPosition*/
	public void setViewPosition(int newx,int newy)
	{
		Debug("moveViewPoint:"+newx+","+newy);

		JViewport viewP=this.scrollPane.getViewport();
		Dimension scrollPaneSize = scrollPane.getSize();
		
		// calculate maximum viewpiont size :
		Dimension maxP = scrollPane.getViewport().getView().getSize(); 
		maxP.width-=scrollPaneSize.width; 
		maxP.height-=scrollPaneSize.height; 
		
		if (newx>maxP.width)
			newx=maxP.width;
		
		if (newy>maxP.height)
			newy=maxP.height; 
			
		viewP.setViewPosition(new Point(newx,newy)); 
		
		//this.offsetx=newx;
		//this.offsety=newy; 
	}

	// ====
	// Zoom 
	// ==== 
	
	private Object zoomTaskMutex=new Object(); 

	private Runnable zoomTask=null; 
	
	private Thread zoomThread=null; 

	public void zoomIn()
	{
		this.fitToScreen=false; 
		if (zoomIndex<zoomInFactors.length)
		{
			zoomIndex++; 
			doZoom();
		}
	}


	public void zoomOut()
	{
		this.fitToScreen=false; 

		if (zoomIndex>-zoomOutFactors.length)
		{
			zoomIndex--; 
			doZoom();
		}
	}
	
	private boolean doMoreZoom=false; 
	
	/** perform Zoom: Schedule background task to do the zooming  */ 
	protected void doZoom()
	{
		double zoomFactor=1.0; 
		
		final Image sourceImage=this.orgImage; 
		if (sourceImage==null)
		{
		    // TBI: 
		    Global.errorPrintln(this,"doZoom(): NULL source image!");
		    return; 
		}
		
		int h=sourceImage.getHeight(null); 
		int w=sourceImage.getWidth(null); 

		
		if (fitToScreen==true)
		{
			double aspect=(double)w/(double)h; 
			
			Dimension targetSize=this.scrollPane.getSize();
			double targetAspect=((double)targetSize.width) /(double)targetSize.height;
			
			
			if (aspect>targetAspect)
			{
				// fit width
				w=targetSize.width; 
				h=(int)(targetSize.width/aspect);
			}
			else 
			{
				// fit height 
				h=targetSize.height; 
				w=(int)(targetSize.height*aspect);
			}
		}
		else
		{
			if (zoomIndex>0) 
				zoomFactor=zoomInFactors[zoomIndex-1];
			
			if (zoomIndex<0) 
				zoomFactor=zoomOutFactors[-zoomIndex-1]; 
			
			h=(int)(h*zoomFactor);  
			w=(int)(w*zoomFactor);
		}
		
		
		final int newHeight=h;
		final int newWidth=w; 
		
		Dimension currentSize = imagePane.getImageSize(); 
		
		// check bogus update ! (Current size already done) 
		if ((currentSize.width==newWidth) && (currentSize.height==newHeight))
			return; 
		
		synchronized(zoomTaskMutex)
		{
			if (zoomTask!=null)
			{
				// check if previous zoom thread is stil active: 
				if ((zoomThread!=null) && (zoomThread.isAlive()==true)) 
				{
					// flag zoom not done ! 
					this.doMoreZoom=true; 
					Global.infoPrintln(this,"Warning: Previous zoom task still running"); 
					return; 
				}

				// cleanup: 

				this.zoomTask=null;
				this.zoomThread=null; 
			}
			
			zoomTask=new Runnable()
			{
				public void run()
				{
					if ((newHeight<=0) || (newWidth<=0))
					{
						Global.infoPrintln(this,"*** Warning: zoomIn cancelled: image not ready");
						return; 
					}

					setBusy(true);

	                   
					Image tempImage=sourceImage.getScaledInstance(newWidth,
							newHeight, Image.SCALE_FAST);
					
					setBusy(false); 
					
                    ImageWaiter w=new ImageWaiter(tempImage);
                    
					try 
					{
					    // Wait for ALL bits doesn't work anymore ??? 
					    w.waitForCompletion(false);
						updateNewZoomImage(tempImage);
					}
					catch (ResourceException e) 
					{
						handle(e); 
					}
					
					//
					// check more zoom, but only AFTER this current thread
					// has fininshed !!! 
					//
					checkMoreZoom(); 
				}
			}; 

			zoomThread=new Thread(zoomTask);
			zoomThread.start(); 
		}// synchronized(zoomTaskMutex); 
	}

	private void checkMoreZoom()
	{
		Runnable checkZoomTask=new Runnable()
		{
			public void run()
			{
			
				synchronized(zoomTaskMutex)
				{
					if (doMoreZoom==true)
					{
						doMoreZoom=false; 
						doZoom(); 
					}
				}
			}
		};		
		UIGlobal.swingInvokeLater(checkZoomTask); 
	}
	
	protected void updateNewZoomImage(Image img) throws ResourceException
	{
		// new image, image should already be done ! 
		// no checking needed 
		
		this.imagePane.setImage(img,false); 
	}

	public void resetZoom()
	{
		this.zoomIndex=0; 
		this.fitToScreen=false; 
		this.doZoom(); 
	}

	public void toggleFitToScreen()
	{
		fitToScreen=(fitToScreen==false); 
		doZoom(); 
	}

	public ImagePane getImagePane()
	{
		return this.imagePane; 
	}

	public void reset() 
	{
		this.resetZoom(); 
	}
	
	// === Main === 
    
    public static void main(String args[])
    {
        Global.setDebug(true); 
        
        try
        {
            viewStandAlone(new VRL("file:///home/ptdeboer/images/galaxy1.jpg")); 
            
            //viewStandAlone(null);
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:"+e); 
            e.printStackTrace();
        }

    }
    
}

