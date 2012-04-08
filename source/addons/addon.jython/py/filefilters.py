##
# VFSClient filter jython example
# (C) www.vl-e.nl 
#
# File : filefilters.py 
#
# Author: Piter T. de Boer 
#
# Info: 
#    VLET file filter + Regular Expressions example 
#    Note that Java Regular Expressions are used. 
##

#import VRS objects + VFS Client: 
from nl.uva.vlet.vrl import VRL
from nl.uva.vlet.vfs import VFile,VDir,VFSClient
from nl.uva.vlet import Global 

vfs=VFSClient()
home=vfs.getDir("file:///~") 

##
##wildcard examples: 
##
print "Files ending with .txt"
files=home.list("*.txt") 
for file in files: 
   print "["+file.getType()+"] "+file.getName() 

#wildcard examples: 
print "Files with letters 'test' in file name:" 
files=home.list("*test*")
for file in files: 
   print " ["+file.getType()+"] "+file.getName() 

##
## Regular Expressions  (supply isRegulareExpression==true) 
##

# 
print "Files with only lower case letters and NO extension:"
files=home.list("[a-z]*",True) # Create RE  
for file in files: 
   print " ["+file.getType()+"] "+file.getName() 

# 
print "Files which start with '.' and have lowercase letters only as RE: "
# dot needs to be escaped. In REs a '.' means match one character (any). 
files=home.list("\.[a-z]*",True) # Create RE 
for file in files: 
   print " ["+file.getType()+"] "+file.getName() 

#filter hidden files
print "Filter out unix hidden files:"
# dot inside a character set "[.]" means '.' itself. Dot outside set means match single character. 
files=home.list("[^.].*",True) # Create RE
for file in files: 
   print " ["+file.getType()+"] "+file.getName() 

print "End."


