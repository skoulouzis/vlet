#!/opt/vlet/bin/jython.sh
##
# VFS URILS script 
# Info :  
#    To start execute this jython file, use:
#         $VLET_INSTALL/bin/jython.sh <jython.file>
#      or use direct bootscript from this directory: 
#         ./jyurils [-l|-r] <uri>   
#

#import VRS objects + VFS Client: 
import sys
from java.util import Date,GregorianCalendar
from nl.uva.vlet.vrl import VRL
from nl.uva.vlet.vfs import VFile,VDir,VFSClient

#defaults: 
longlist=False;  

recurse=False;
printVrl=False;

#need at least one argument:
if (len(sys.argv)<=1) :
    print "usage: urils <uri>";
    sys.exit(1); 

#parse arguments, skipping first 
for arg in sys.argv[1:]: 
    if arg == "-l" : 
        longlist=True
    elif arg == "-r" :
        recurse=True
    elif arg == "-vrl" :
        printVrl=True
    else :
        vrlstr=arg 

def timeString(millis) : 
    months=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]
    cal=GregorianCalendar();
    cal.setTimeInMillis(millis);  
    yy=cal.get(GregorianCalendar.YEAR);
    mo=cal.get(GregorianCalendar.MONTH);
    da=cal.get(GregorianCalendar.DAY_OF_MONTH);
    ho=cal.get(GregorianCalendar.HOUR_OF_DAY);
    ms=cal.get(GregorianCalendar.MINUTE);
    sc=cal.get(GregorianCalendar.SECOND);
    return "%4d-%3s-%2d %2d:%2d:%2d" % (yy,months[mo],da,ho,ms,sc)

# creat new VFSClient
vfs=VFSClient()
vrl=VRL(vrlstr); 

# list directory: 
dirs=[vrl];
parent=vrl.getPath()+"/"; 
plen=len(parent); 


# while
while (len(dirs)>0) : 
   #pop head 
   dir=dirs.pop(0);

   contents=vfs.list(dir);
   #print list: 
   for file in contents: 
      name=file.getBasename(); 
      path=file.getPath(); 
      #strip parent path: 
      path=path[plen:]; 

      if file.isDir() : 
         path+="/"
         if recurse:
            # push 
            dirs.append(file.getVRL()); 

      if longlist == True : 
         permstr= file.getPermissionsString()
         if file.isFile() : 
            filelen=file.getLength(); 
         else:
            filelen=0; 
	 # format time from millies 
         modtime=file.getModificationTime(); 
         modtimestr=timeString(modtime); 
         attrs="%10s  %12d  %s " % (permstr,filelen,modtimestr);  
      else : 
         attrs=""; 
      print attrs+path

sys.exit(0);

