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
 * $Id: HexViewer.java,v 1.6 2011-05-09 14:25:26 ptdeboer Exp $  
 * $Date: 2011-05-09 14:25:26 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.util.TooManyListenersException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.font.FontInfo;
import nl.uva.vlet.gui.font.FontToolBar;
import nl.uva.vlet.gui.font.FontToolbarListener;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.util.MimeTypes;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;

/**
 * Implementation of a simple Binary Hex Viewer.<br>
 * Show the contents of in hexidecimal form
 *  
 * @author Code donated by Piter .T. de Boer (Piter.NL)
 */
public class HexViewer extends InternalViewer implements FontToolbarListener

{
	// todo: UTF-8 Char Mapping 
	public final String charMapping[]=
		{
	      "�"," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // 00 - 0f
	      " "," "," ","\u240d"," "," "," "," ",  " "," "," "," "," "," "," "," ", // 10 - 1f
	      " ","A","B","C"     ,"D","E","F","G",  " "," "," "," "," "," "," "," ", // 20 - 2f
	      "H","I","J","K"     ,"L","M","N","O",  " "," "," "," "," "," "," "," ", // 30 - 3f 
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // 40 - 4f
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // 50 - 5f
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // 60 - 6f
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // 70 - 7f 
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // 90 - 9f  
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // a0 - af  
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // b0 - bf  
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // c0 - cf  
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // d0 - df  
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // e0 - ef  
	      " "," "," "," "     ," "," "," "," ",  " "," "," "," "," "," "," "," ", // f0 - ff  

		};
	
	/** unicode for Carriage return C/R */ 
	
	public final static String CHAR_CR  = "\u240d";   
	//public final static String CHAR_TAB = "\u240d";   
	
	/**
	 * Needed by swing 
	 */
	private static final long serialVersionUID = 4959020834564707156L;
	
	/** The mimetypes I can view */ 
	private static String mimeTypes[]={
		"application/octet-stream",
	}; 
	
	static private boolean default_show_font_toolbar=false;
	
	public static enum UTFType{UTF8,UTF16}; 
	
	public static class UTFDecoder
	{
		public UTFDecoder(){}; 
		public UTFDecoder(UTFType type){};
	}
	
	// =======================================================================
	// 
	// =======================================================================
	
	//
	// Primary Fields: 
	//
	
	private VFile vfile;
	
	private long offset;
	
	private long fileOffset; // start of current buffer in file ! 
	
	private int maxBufferSize=1*1024*1024; 
	
	private byte buffer[]=new byte[0];
	
	private long length;
	
	 private int wordSize=2;

	/** Actual bytes per line (nrBytesPerLine) is nrWordPerLine*wordSize */ 
	private int minimumBytesPerLine=32;
	
	// == Swing == 
	//private ViewerDropTarget defaultDropTarget=null; 

	// ================================
	// Derived/Secondary fields: 
	// ================================
	
	private JToolBar toolBar;
	
	int nrBytesPerLine;
	
	private int nrWordsPerLine;
	
	private int maxRows;
	
	int nrBytesPerView;
	
	private long scrollMutiplier=1;
	
	// === GUI components: 
	
	JTextArea textArea=null;
	
	private JPanel mainPanel;
	
	private JScrollBar scrollbar;
	
	private HexViewController hexViewController;
	
	private JLabel offsetLabel;
	
	JTextField offsetField;
	
	private JLabel lengthLabel;
	
	private JTextField lengthField;
	
	private JLabel magicLabel;
	
	private JTextField magicField;
		
	private JPanel toolPanel;
	
	private FontToolBar fontToolBar;
	
	private JLabel encodingLabel;
	
	private JTextField encodingField;
	
	private ActionTask updateTask; 
	
	public void initGui()
	{
		this.hexViewController=new HexViewController(this); 
		
		{
			this.setLayout(new BorderLayout());
			this.setPreferredSize(new Dimension(800,600)); 
		}
		{
			toolPanel=new JPanel(); 
			toolPanel.setLayout(new FlowLayout()); 
			this.add(toolPanel,BorderLayout.NORTH);
			
			
			// ToolBAr
			{
				toolBar=new JToolBar(); 
				toolBar.setLayout(new FlowLayout(FlowLayout.LEFT)); 
				toolPanel.add(toolBar); 
				{
					offsetLabel=new JLabel("Offset:"); 
					toolBar.add(offsetLabel);
				}
				{
					offsetField=new JTextField("0");  
					toolBar.add(offsetField);
					offsetField.addActionListener(this.hexViewController); 
					//offsetField.setMinimumSize(new Dimension(200,30)); 
				}
				{
					lengthLabel=new JLabel("Size:"); 
					toolBar.add(lengthLabel);
				}
				{
					lengthField=new JTextField("?");  
					toolBar.add(lengthField);
					lengthField.setEditable(false); 
					
				}
				{
					magicLabel = new JLabel("MagicType:"); 
					toolBar.add(magicLabel);
				}
				{
					magicField=new JTextField("?");  
					toolBar.add(magicField);
					magicField.setEditable(false);
					magicField.setSize(120,32); 
					
				}
				{
					encodingLabel = new JLabel("Encoding:"); 
					toolBar.add(encodingLabel);
				}
				{
					encodingField=new JTextField("plain");  
					toolBar.add(encodingField);
					encodingField.setEditable(false);
					encodingField.setSize(120,32); 
					
				}
			}
			// FontToolBar
			{
				fontToolBar=new FontToolBar(this);
				toolPanel.add(fontToolBar); 
				fontToolBar.setFocusable(false); 
				fontToolBar.setVisible(default_show_font_toolbar);
			}
		}
		
		{
			mainPanel=new JPanel(); 
			this.add(mainPanel,BorderLayout.CENTER);
			mainPanel.setLayout(new BorderLayout());
            
            // TextArea
			{
				textArea=new JTextArea(); 
				
				mainPanel.add(textArea,BorderLayout.CENTER);
				
				textArea.setText("Initializing HexViewer...");
                // get default monospaced font style;
                
			
                
				textArea.setEditable(false); 
				textArea.addKeyListener(hexViewController);
			}
			{
				scrollbar=new JScrollBar(JScrollBar.VERTICAL,0,getMinimumBytesPerLine()*this.maxRows,0,10240); 
				mainPanel.add(scrollbar,BorderLayout.EAST);
				scrollbar.addAdjustmentListener(hexViewController);
				scrollbar.setBlockIncrement(1024-this.getMinimumBytesPerLine()*2);
				scrollbar.setFocusable(false); 
			}
		}
		// default frame key listener: 
		Frame frame=this.getJFrame(); 
		
		if (frame!=null)
			frame.addKeyListener(this.hexViewController);
		else
			this.addKeyListener(this.hexViewController);
		
        // update Font settings: fontToolbar & textArea
        FontInfo finfo=FontInfo.getFontInfo("Monospaced"); 
        this.fontToolBar.setFontInfo(finfo);
        finfo.updateComponentFont(this.textArea);
        
        // DROP TARGET
        {
            DropTarget dropTarget=new DropTarget(); 

            // canvas can receive drop events; 
            this.textArea.setDropTarget(dropTarget); 
            try
            {
                dropTarget.addDropTargetListener(new HexViewController.ViewerDropTargetListener(this.hexViewController)); 
            }
            catch (TooManyListenersException e)
            {
                System.err.println("***Error: Exception:"+e); 
                e.printStackTrace();
            }
        }
        
	}
	
	@Override
	public String[] getMimeTypes()
	{
		return mimeTypes; 
	}
	
	public void setText(String txt)
	{
		textArea.setText(txt);
	}
	
	
	public String getText()
	{
		return textArea.getText(); 
	}
	
	
	public void setContents(byte[] bytes) 
	{
		this.fileOffset=0; 
		this.offset=0; 
		this.buffer=bytes; 
		this.length=buffer.length; // update with nrBytes actual read
		updateMagic();
		redrawContents(); 
		
	}
	
	public void updateMagic()
	{
		try 
		{
			this.magicField.setText(MimeTypes.getDefault().getMagicMimeType(buffer));
		}
		catch (Exception e) 
		{
			Global.debugPrintStacktrace(e); // handle(e);
		}
		
	}
	
	public boolean isTool()
	{
		return true; 
	}
	
	public String getClassification()
	{
		return "test/Viewers"; 
	}
	
	public boolean haveOwnScrollPane()
	{
		return true; 
	}
	
	synchronized void  redrawContents() 
	{
		// ASSERT
		if (buffer==null)
		{
			nl.uva.vlet.Global.debugPrintln("HexViewer","Received NULL contents");
			return;
		}
		
		// Method globals: 
		updateSizes(); 
		
		
		// ================
		// Step II) 
		// 
		// Running Variables  
		// 
		
		int y=0;
		long index=this.offset;
		//String str=""; 
		String charStr="";
		String linestr=""; 
		StringBuilder builder=new StringBuilder(getText().length());
		
		//TermGlobal.debugPrintln("HexViewer","maxLenStr="+maxLenStr); 
		
		int startCharsString=0;
		String maxLenStr=Long.toHexString(length);
		startCharsString=maxLenStr.length()+1+(1+2*getWordSize())*nrWordsPerLine+2;
		
		while ((y<maxRows) && (index<length))
		{
			int bufferIndex=(int)(index-fileOffset); // index in buffer
			
			//Debug("index       ="+index); 
			//Debug("bufferIndex ="+bufferIndex); 
			
			linestr=getPosStr(index,maxLenStr.length())+" ";  
			
			for (int j=0;j<nrWordsPerLine;j++)
			{
				linestr+=hexStr(buffer,bufferIndex+j*getWordSize(),getWordSize())+" "; 
			}
			
			if (linestr.length()<startCharsString)
				linestr=fillWithSpaces(linestr,startCharsString); 
			
			charStr=decodeChars(buffer,bufferIndex,nrBytesPerLine,true); 
			
			//for (int j=0;(j<nrBytesPerLine)&&(bufferIndex+j<buffer.length);j++) 
			//	charStr+=saveChar(buffer[bufferIndex+j]);
			
			index+=nrBytesPerLine; 
			linestr+=charStr;
			
			if (y+1<maxRows)
				linestr+="\n";
			
			y++; 
			charStr="";
			builder.append(linestr); 
		}
		
		// 0 contents
		if (length==0)
		{
			builder.setLength(0);
			builder.append(getPosStr(0,8)+" ");  
		}
		
		// ===
		// Update GUI Componenets:
		// ===
		
		setText(builder.toString());
		//this.revalidate(); 
		//this.requestFrameResizeToPreferred();
		resetFocus();
	}
	
	private void updateSizes()
	{
		String maxLenStr=Long.toHexString(length);
		// ===============================================
		// Step I)
		// update sizes/ derived variables : 
		// ================================================
		
			// update form parent Port Size ? 
			//Dimension size=this.getViewPortSize();
			//if (size!=null)
			//{
			//	this.setSize(size); 
			//	this.validate();
			//}
			
			Dimension targetSize=textArea.getSize();  
			FontMetrics metrics = textArea.getFontMetrics(textArea.getFont());
			char chars[]={'w'};
			int charWidth=metrics.charsWidth(chars,0,1);
			int charHeight=metrics.getHeight();
			int maxLineChars=targetSize.width/(charWidth); 
			
			// calculate: 
			// width=m+1+(1+k)*n+c; k=2w; c=l=n*w
			// width=1+m+n+2nw+nw=1+m+n(1+3w) 
			// width-1-m=n(1+3w) => n=(width-1-m)/(1+3w)
			nrWordsPerLine=(maxLineChars-1-maxLenStr.length())/(1+3*getWordSize()); 
			maxRows=targetSize.height/(charHeight);
			
			// autocalculate has bugs, use fixed of 32 (is nicer):  
			//minimumBytesPerLine=nrWordsPerLine*wordSize;
			//minimumBytesPerLine=32; 
			nrWordsPerLine=(int)Math.ceil((float)getMinimumBytesPerLine()/(float)getWordSize());  
			nrBytesPerLine=nrWordsPerLine*getWordSize(); // actual bytes per line 
			nrBytesPerView=maxRows*nrBytesPerLine;
			
		
			
			long scrollMax=length-nrBytesPerView; 
			if (scrollMax<0) 
			   scrollMax=0; 
			
			if (scrollMax>Integer.MAX_VALUE) 
			{
			   this.scrollMutiplier=scrollMax/Integer.MAX_VALUE;
			   // use FLOOR value to divide to value less then integer max. 
			   scrollMax=scrollMax/scrollMutiplier; 
			}
			this.scrollbar.setMaximum((int)scrollMax); 
			this.scrollbar.setMinimum(0);
			this.scrollbar.setBlockIncrement(nrBytesPerView); 
			this.scrollbar.setUnitIncrement(this.nrBytesPerLine);
			this.scrollbar.setValue((int)this.offset);
			//this.scrollbar.setVisibleAmount(nrBytesPerView);
			/**
			 if (offset+nrBytesPerView>length)
			 offset=(length-nrBytesPerView); 
			 
			 //offset=offset-offset%wordSize;
			  
			  if (offset<0) 
			  offset=0;
			  */
			
			// update gui fields: 
			
			this.offsetField.setText(getPosStr(offset,maxLenStr.length()));  
			this.lengthField.setText(getPosStr(length,maxLenStr.length())); 
	
	}

	private String fillWithSpaces(String orgstr, int len)
	{
		String newstr=new String(orgstr);  
		
		for (int i=orgstr.length();i<len;i++)
			newstr+=" " ;
		
		return newstr; 
	}
	
	private String getPosStr(long index,int maxLen) 
	{
		// start of line: 
		String posStr=Long.toHexString(index);
		posStr="0x"+nulls(maxLen-posStr.length())+posStr;
		
		return posStr;  
	}
	private String nulls(int n)
	{
		// optimizations (?): 
		if (n<=0) return "";  
		else if (n==1) return "0"; 
		else if (n==2) return "00"; 
		else if (n==3) return "000"; 
		else if (n==4) return "0000"; 
		else if (n==5) return "00000"; 
		else if (n==6) return "000000"; 
		else if (n==7) return "0000000"; 
		else if (n==8) return "00000000"; 
		
		char chars[]=new char[n+1]; 
		for (int i=0;i<chars.length;i++)
			chars[i]='0';
		
		chars[n]=0; 
		
		return String.valueOf(chars);
	}
	
	// byte to hexstr ! 
	public String hexStr(byte[] word,int offset, int wordSize)
	{
		String str="";
		
		for (int i=0;(i<wordSize)&&(offset+i<word.length);i++)
		{
			int b=word[offset+i]; // Little Endian
			
			if (b<0) 
				b=b+128; // avoid negative hexadecimal
			
			// preprend
			if (b<16)
				str+="0"+Integer.toHexString(b);  
			else
				str+=Integer.toHexString(b);
		}
		
		return str; 
	}
	
	public String decodeChars(byte buffer[],int start,int len,boolean plainBytes) 
	{
		String charStr="";
		
		if (plainBytes) 
		{
			for (int i=start;(i<start+len)&& (i<buffer.length);i++)
			{
				charStr+=charStr((int)buffer[i]);
			}
		}
		
		return charStr; 
	}
	
	public String charStr(int val)
	{
		if (val<0) 
			val=val+128; 
		
		if ((val<0) || (val>255)) 
		{
			Global.errorPrintln(this,"Error: Character number out of bound:"+val); 
			return "?";
		}
				
		String str=null;
		str=charMapping[val];
		if ((val!=32) && StringUtil.equals(str," "))
		{
			switch(val)
			{
				case 0:    str=" "; break;   
				case 0x0d: str=" "; break;
				case '\n': str=CHAR_CR; break; 
				case '\t': str=" ";/*CHAR_TAB*/; break; 
				default:
					str=String.valueOf((char)val);  
				break; 
			}
		}
		
		return str;   
	}
	@Override
	public void stopViewer()
	{
	}
	
	public void disposeViewer()
	{
		this.textArea=null; 
	}
	@Override
    public void initViewer() 
	{
		initGui();
	}
	@Override
	public String getName()
	{
		return "Binary Viewer";
	}
	
	public void startViewer(VRL loc) throws VlException 
	{
		updateLocation(loc);
		this.validate();
		this.requestFrameResizeToPreferred();
	}
	
	@Override
	public void updateLocation(final VRL loc) 
	{
		debug("updateLocation:"+loc);
		
        setVRL(loc);
        
        if (loc==null)
           return;
        
        this.updateTask=new ActionTask(null,"loading:"+getVRL())
        {

            @Override
            protected void doTask() throws VlException
            {
                _update(loc); 
            }

            @Override
            public void stopTask()
            {
            }            
        };
        
        updateTask.startTask();
	}
	
	private synchronized void _update(final VRL loc) throws VlException
    {
		debug("_update:"+loc);
		
        VNode vnode = getVNode(getVRL());
        
    	if (vnode instanceof VFile) 
    	{
    			this.vfile=(VFile)vnode; 
            
        		// assume file length
        		this.length=vfile.getLength();
        		this.offset=0; 
        		// check buffered read
            
        		// Fill Buffer: use direct read (already in background) 
        		readBuffer(); 
        		updateMagic();
        		redrawContents();
    	}
        else
        {
            throw new VlException("Not supported","Sorry cannot read yet from:"+vnode); 
        }
    }
	
	public void moveToOffset(final long offset)
	{
	    if (this.offset==offset)
	    {
	        debug("Ignoring moveTo same offset"+offset);
	        return; 
	    }
	    
		if (updateTask!=null)
		{
			if (updateTask.isAlive())
			{
				debug("*** Already updating! Ingoring move to:"+offset); 
				return; 
			}
		}
		
	    this.updateTask=new ActionTask(null,"loading:"+getVRL())
	    {

            @Override
            protected void doTask() throws VlException
            {
                _moveToOffset(offset); 
            }

            @Override
            public void stopTask()
            {
            }
	    };
	    
	    updateTask.startTask();
	}
	
	private synchronized void readBuffer() 
	{
		debug("readBuffer:"+offset);
		
		// TODO: non VFile viewing
		if (vfile==null)
		{
			// no vfile, set to defaults: 
			fileOffset=0; 
			length=buffer.length;
			//offset=0; 
			return; 
		}
		
		int len=0; 
		len=maxBufferSize; 
		
		// check end of file 
		if (fileOffset+len>length)  
			len=(int)(length-fileOffset);  
		
		// check buffer size: 
		if ((buffer==null) || (len!=buffer.length)) 
			buffer=new byte[len];
		
		debug("read buffer. offset        ="+offset); 
		debug("read buffer. fileOffset    ="+fileOffset); 
		debug("read buffer. buffer length ="+buffer.length); 
		
		// fill buffer: 
		try 
		{
            setBusy(true); 

            this.setViewerTitle("Reading:"+getVRL()); 
			vfile.read(fileOffset,buffer,0,len);
            this.setViewerTitle("Inspecting:"+getVRL());
		} 
		catch (VlException e) 
		{
		    this.setViewerTitle("Error reading:"+getVRL());
			handle(e);
		}
		finally
		{
		    setBusy(false); 
		}
		
	
	}
	
//	public void postMoveTo()
//	{
//		debug("postMoveTo");
//		
//		redrawContents(); // (re)draw
//         
////		 
////		
////	    final HexViewer fviewer = this;
////	    Runnable updater=new Runnable()
////	    {
////	        public void run()
////	        {
////	           // fviewer.requestFrameResizeToPreferred(); 
////	            setBusy(false); 
////	        }
////	    };
////	    
////	   updater.run(); 
//	   // UIGlobal.swingInvokeLater(updater); 
//	}
	
	void debug(String msg) 
	{
		//Global.errorPrintln(this,msg);	
		Global.debugPrintln(this,msg); 
	}
	
	protected void handle(VlException ex)
	{
		ExceptionForm.show(ex);
	}
	
	public synchronized void _moveToOffset(long value) 
	{
	   debug("_moveToOffset:"+value); 
	    
		this.offset=value;
		
		if (offset>=length-nrBytesPerView)
			offset=length-nrBytesPerView;
		
		if (offset<0) 
			offset=0; 
		
		debug("new offset="+offset);
		
		// move buffer window up (only if buffer doesn't already contain the last 
        // part of the file. 
		if  ((offset>this.fileOffset+buffer.length-this.nrBytesPerView)
                && (fileOffset+this.maxBufferSize<length)) 
		{
        
			// read half buffer before and after current offset 
			fileOffset=offset-maxBufferSize/2;
			
			if (fileOffset<0) 
				fileOffset=0; 
			
			readBuffer(); 
		}
		
		// OR move buffer window down
		if (offset<this.fileOffset)
		{
			// read half buffer before and after current offset 
			fileOffset=offset-maxBufferSize/2;
			
			if (fileOffset<0) 
				fileOffset=0; 
			
			readBuffer(); 
		}
		
		// ===
		// POST MOVE TO
		// ===
		redrawContents(); // (re)draw
	}
	
	public void addOffset(int delta) 
	{
		moveToOffset(offset+delta); 
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		
	}
	
	public void updateFont(Font font, boolean useAntialising) 
	{
		GuiSettings.setAntiAliasing(this,useAntialising); 
		textArea.setFont(font); 
		redrawContents(); 
	}
	
	/** Resets focus to textArea for key commands */  
    public void resetFocus() 
    {
        textArea.requestFocus();
   }

    public void toggleFontToolBar() 
    {
        if (this.fontToolBar.isVisible()) 
            this.fontToolBar.setVisible(false);
        else
            this.fontToolBar.setVisible(true);
        
        this.validate();
        this.requestFrameResizeToPreferred();
    }
    
    public void doMethod(String methodName, ActionContext actionContext) throws VlException
    {
        if (actionContext.getSource()!=null)
            this.updateLocation(actionContext.getSource());
    }
    
    /*public Vector<ActionMenuMapping> getActionMappings()
    {
        ActionMenuMapping mapping=new ActionMenuMapping("viewBinary", "View Binary (Hex Viewer)","binary");
        
        // '/' is not a RE character
        Pattern patterns[]=new Pattern[mimeTypes.length];
        
        for (int i=0;i<mimeTypes.length;i++)
            patterns[i]=Pattern.compile(mimeTypes[i]); 
                    
        mapping.addMimeTypeMapping(patterns);
            
        Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
        mappings.add(mapping); 
        return mappings; 
    }*/
	
	//=========================================================================
	// Main 
	//=========================================================================
	
	
	public static void main(String args[])
	{
		//Global.setDebug(true); 
		
		try
		{
			if (Global.isWindows()==false)
				viewStandAlone(new VRL("file:///etc/passwd"));
			else
				viewStandAlone(new VRL("file:///e:/INSTALL.txt" ));
			//viewStandAlone(null);
			
			{
				HexViewer hex=new HexViewer();
				int len=65536; 
				byte bytes[]=new byte[len];
				
				for (int i=0;i<len/2;i++)
				{
					// big endian
					bytes[i*2+1]=(byte)(i%256); 
					bytes[i*2]=(byte)(i/256);
				}
				hex.startAsStandAloneApplication(null);
				//hex.setViewStandalone(true);  
				//hex.init(); 
				
				hex.setContents(bytes); 
			}
		}
		catch (VlException e)
		{
			System.out.println("***Error: Exception:"+e); 
			e.printStackTrace();
		}
	}
	
	public static void viewStandAlone(VRL loc)
    {
        HexViewer tv=new HexViewer(); 
       // tv.setViewStandalone(true);
        
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

	public long getOffset()
	{
		return offset; 
	}

	void setWordSize(int wordSize)
	{
		this.wordSize = wordSize;
	}

	int getWordSize() {
		return wordSize;
	}

	void setMinimumBytesPerLine(int minimumBytesPerLine)
	{
		this.minimumBytesPerLine = minimumBytesPerLine;
	}

	int getMinimumBytesPerLine() 
	{
		return minimumBytesPerLine;
	}

	public long getNrBytesPerLine() 
	{
		return this.nrBytesPerLine;
	}
}
