##
# VRS Execution
# (C) www.vl-e.nl 
###
# Vlet version : @VLET_VERSION@
# Author       : Piter T. de Boer
# Info         :  
#    VLET vrs jython interface example.
#    To start execute this jython file, use:
#        $VLET_INSTALL/bin/jython.sh <jython.file> 
##

#import VLET Util classes 
from nl.uva.vlet.exec import LocalExec,LocalProcess
# use VLET Global class: 
from nl.uva.vlet import Global 

#python imports
import time

###
### Simple Local Execution using VL-e Toolkit: 
###

# Bash interface :

print "--- bash example ---" 
# command line + arguments to execute: 
commands=["/bin/bash","-c","echo hello world!; echo this is standard error >&2"]; 

#result is an String Array {stdout,stderr,status}
result=LocalExec.execute(commands)
stdout=result[0]; # string 
stderr=result[1]; # string
status=result[2]; # string 

print "--- result ---" 
print " Standard out = ", stdout;
print " Standard err = ", stderr;
print " Exit status  = ", status;

###
### non script command "ls" from command line: 
###

print "--- ls example ---" 

# use vlet Global: 
home=Global.getUserHome(); 

# command line + arguments to execute: 
commands=["ls","-a",home]

[stdout,stderr,status]=LocalExec.execute(commands)

print "--- result ---" 
print " Standard out = ", stdout;
print " Standard err = ", stderr;
print " Exit status  = ", status;

##
## Background process and monitoring example 
##
print "--- Background example ---" 

# command line + arguments to execute: 
commands=["/bin/bash","-c","for a in 1 2 3 4 5 6 7; do echo run-$a; sleep 1 ; done ; echo done"] 

#Start script in background and wait
process=LocalExec.execute(commands,False)
# do stuff 
print "Process isTerminated() I   =",process.isTerminated()
time.sleep(1)
print "Process isTerminated() II  =",process.isTerminated()

# wait for termination 
process.waitFor() 

# get result + exit status
print "Process isTerminated() III =",process.isTerminated()
print "Process exit value         =",process.getExitValue() 
print "--- Process standard out ---\n",process.getStdout()
print "---"
print "--- Process standard error ---\n",process.getStderr()
print "---"
print "End."


