#!/opt/vlet/bin/jython.sh
##
# VFSClient jython interface example
# (C) www.vl-e.nl 
##
# File         : vfsclient.py 
# Vlet version : @VLET_VERSION@
# Author       : Piter T. de Boer 
#
# Info :  
#    VLET vfs jython interface example.
#    To start execute this jython file, use:
#        $VLET_INSTALL/bin/jython.sh <jython.file> 
##
import sys
#import VRS objects + VFS Client: 
from nl.uva.vlet.vrl import VRL
from nl.uva.vlet.vfs import VFile,VDir,VFSClient


### Helper methods
def boolstr(value):
  if value == True: 
     return "true" 
  else:
     return "false" 
     
# creat new VFSClient
vfs=VFSClient()

# get Virtual File object: 
dir=vfs.getDir("lfn://lfc.grid.sara.nl:5010/grid/pvier/"); 
print "Remote LFC directory = "+dir.toString()
print "--- Directory Attributes ---" 
print " Directory modification time  =",dir.getModificationTime()
print " Directory access permissions = \""+dir.getPermissionsString()+"\"" 

#get contents of remote Directory 
contents=dir.list()

# array acces: 
print "First file = ",contents [0];

# define print function: 
def PrintNode(prefix,node):
  print prefix+"["+node.getType()+"] "+node.toString();

# apply MyPrint on contents array
print "--- contents of remote directory ---"
[PrintNode(" - ", file) for file in contents]
print "--- ---"

# get local home 
home=vfs.getDir("file:///~"); 
print "Local home=",home; 

###
### remote navigation 
### 
# get remote file:
fileVRL="lfn://lfc.grid.sara.nl:5010/grid/pvier/piter/test.txt"; 
vrlObject=VRL(fileVRL) 

# dissect VRL (=URI)
print "-- VRL object ---"
print " VRL String    =",fileVRL; 
print " VRL Object    =",vrlObject; 
print " VRL Hostname  =",vrlObject.getHostname(); 
print " VRL Port      =",vrlObject.getPort()
print " VRL Path      =",vrlObject.getPath(); 
print " VRL Extension =",vrlObject.getExtension(); 
print "---"

if (vfs.existsFile(fileVRL) is True):
  print "File exists:"+fileVRL; 
else:
  print "*** Error: Remote file does not exists:"+fileVRL; 

# get/create Virtual File object of remote file
remoteFile=vfs.getFile(fileVRL); 
print "--- File Attributes ---" 
print " modification time  =",remoteFile.getModificationTime()
print " length             =",remoteFile.getLength()
print " access permissions = \""+remoteFile.getPermissionsString()+"\"" 
print " isReadable         =",boolstr(remoteFile.isReadable()) 
print " isWritable         =",boolstr(remoteFile.isWritable()) 
print " mimetype           =",remoteFile.getMimeType()

#i get contents of remote Directory 

# getContents() returns byte array, getContentsAsString returns String object 
text=remoteFile.getContentsAsString(); 
print "Contents of remote file = "+text; 

# Navigating example:  "cd .."  
remoteParent=remoteFile.getParent(); 
print "Remote parent = ", remoteParent; 

# example "VDir.hasFile()" (hasDir/hasChild)
if (remoteParent.hasFile(remoteFile.getBasename()) is True): 
   print "Remote parent reports that is has the remote file as child" 
else:
   print "*** Error: Parent directory of remote file reports is doesn't have the Child ! " 

# copy to local home directory, overwrite existing: 
resultFile=remoteFile.copyTo(home);
print "Copied remote file to:",resultFile; 
print "Check whether file is local:",resultFile.isLocal();
print "Local path of file is:",resultFile.getPath(); 

# get system temp dir: 
print "tempdir'=",vfs.getTempDir(); 

# create Unique Temp Dir() on local home:
tmpdir=vfs.createUniqueTempDir(); 
print "unique tempdir =",tmpdir;

print "end."

sys.exit(0); 
