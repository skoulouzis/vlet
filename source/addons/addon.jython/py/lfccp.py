#!/opt/vlet/bin/jython.sh
##
# LFC jython interface example
# (C) www.vl-e.nl
##
# File         : lfccp.py
# Vlet version : @VLET_VERSION@
# Author       : Piter T. de Boer
#
# Info :
#    VLET LFC jython interface example.
#    To start execute this jython file, use:
#        $VLET_INSTALL/bin/jython.sh <jython.file>
##

#import VRS objects + VFS Client:
import sys 
from nl.uva.vlet import Global 
from nl.uva.vlet.vrl import VRL
from nl.uva.vlet.vfs import VFile,VDir,VFSClient
from nl.uva.vlet.vrs import VRSContext

#defaults 
source="";
dest="";
force=False;
mkdirs=False;
recurse=False; 
verboselvl=0; 

def usage() : 
   print "lfccp [-f] [-p] [-r] [-mkdir] <sourceURI> <destURI>" ;
   sys.exit(1);

def fatal(msg) : 
   print msg;
   sys.exit(2);

def verbose(lvl,msg) : 
   if (lvl<=verboselvl) : 
      print msg

#parse arguments, skipping first 
for arg in sys.argv[1:]: 
    if arg == "-f" : 
        force=True
    elif arg == "-mkdirs" :
        mkdirs=True
    elif arg == "-p" :
        mkdirs=True
    elif arg == "-v" :
        verboselvl=verboselvl+1
    elif arg == "-r" :
        recurse=True
    else :
        if (source=="") :
           source=arg 
        elif (dest=="") :
           dest=arg
        else :
           print "*** Invalid argument:"+arg 
           usage()

# create custom VFSClient: 
vfs=VFSClient()

verbose(1,"- source          ="+source);
verbose(1,"- dest            ="+dest);
verbose(1,"- pwd             ="+vfs.getWorkingDir().toString()); 

if ( (source=="") or (dest=="")) : 
   usage() 

sourceVrl=vfs.resolve(source);
destVrl=vfs.resolve(dest); 
verbose(1,"- resolved source ="+sourceVrl.toString());
verbose(1,"- resolved dest   ="+destVrl.toString());

# get VRS Context from VFSClient: 
context=vfs.getVRSContext() 

# Specify global LFC Server settings. 
# Global LFC properties are prepended with "lfc", after that the
# LFC server property name follows: 
context.setProperty("lfc.listPreferredSEs","srm.grid.sara.nl,tbn18.nikhef.nl")
context.setProperty("lfc.replicaCreationMode","Preferred")

node=vfs.getNode(sourceVrl); 
verbose(1,"Copying source (%s):%s" % (node.getType(), node.getVRL().toString()) ); 

destDir=vfs.getNode(destVrl); 
verbose(1,"Copying to destination (%s):%s" % (destDir.getType(), destDir.getVRL().toString()) ); 

if node.isDir() and recurse==False: 
   fatal("To copy a directory, use '-r' ") 
    
resultFile=vfs.copy(node,destDir); 
   
print "result="+ resultFile.getVRL().toString();

sys.exit(0); 
