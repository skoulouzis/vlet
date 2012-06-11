These are the Swing Compatible Drag and Drop classes 

 The VTransferHandler is the central class which controls 
 all the Drag and Drops between resources. 
 When performing a DnD between VComponent and/or VContainers, the 
 Master browser will be informed of the Drop and perform the 
 execution. <br>
 @see MasterBrowser.performDrop() 

 For the rest the default DnD methods are used to stay Swing, and 
 more important, platform DnD compatible. 
 
 These Classes are under construction as the Swing way to perform platform
 compatible DnD is not trivial. 
 
 Piter T. de Boer 
 
 
 