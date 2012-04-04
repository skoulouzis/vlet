This is the experimental VBrowser applet. 
Is isn't finished since the applet can't really use the file system APIs. 

To install the demo type: 
	 ant applet-install -Dapplet.installdir=$INSTALL_DIR
	 
Then copy the VLET libraries to $INSTALLDIR/lib 

Only copy VLET_INSTALL/lib/*.jar and VLET_INSTALL/dist/lib/auxlib/* 

Then run "gen.sh" to generate the applet HTML page. 

Piter T. de Boer. 
