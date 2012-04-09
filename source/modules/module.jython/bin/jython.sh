#!/bin/bash 
##
# Project : VLET
# File    : jython.sh  
# Author  : P.T. de boer 
# Info    : 
#      Script interface to start jython files using VLET environment. 
#      Jython script for VLET version: @VLET_VERSION@
##
#Usage: 
#
# jython.sh <jythonfile> [jython options] 

##
## VLET Settings 
##

##
# VLET_SYSCONFDIR 
# Set the following variable when the configuration files are NOT under $VLET_INSTALL/etc ! 
# This is automaticaly done when performing a binary installation 
#VLET_SYSCONFDIR=/etc/vlet

###LOCAL SETTINGS

INTERACTIVE=false

# bootstrap startup directory:
if [ -n  "$VLET_INSTALL" ] ; then 
	BASE_DIR=$VLET_INSTALL
else
	# Check basedir of installation. Assume script is started from $VLET_INSTALL/bin
	DIRNAME=`dirname $0`
	BASE_DIR=`cd $DIRNAME/.. ; pwd`
fi

# default sysconfdir, use BASE_DIR/etc 
if [ -z "$VLET_SYSCONFDIR" ] ; then 
	VLET_SYSCONFDIR=$BASE_DIR/etc 
fi

# source installation settings: 
if [ -f "$VLET_SYSCONFDIR/vletenv.sh" ] ; then 
   source $VLET_SYSCONFDIR/vletenv.sh 
else
   echo "*** Error: couldn't find vletenv.sh (Misconfigured VLET installation ?)."
   exit 1; 
fi

##
## Jython Settings
##

##
# jython jar + file  to start (Settings from vletenv.sh!)
JYTHON_FILE="$1"
shift 
JYTHON_PROPERTIES="-Dpython.cachedir=$JYTHON_CACHE"

#Jython jar 
if [ -f "$JYTHON_JAR" ] ; then 
   verbose "Using jython: $JYTHON_JAR" 
else
   echo "*** Error: jython.jar not found: $JYTHON_JAR "
   exit 1; 
fi

# Jython file
if [ -z "$JYTHON_FILE" ] ; then 
   echo "*** Error: Jython file not specified. Please start with: jython.sh <jythonfile> "
   echo "           Or use jython.sh -i to explicitly start in interactive mode."
   exit 1; 
else
   verbose "Starting jython file:"$JYTHON_FILE
fi

# -i = start interactive. 

if [ "$JYTHON_FILE" == "-i" ] ; then 
   JYTHON_FILE=""
   INTERACTIVE="true"
fi 

##
## Bootstrapper 
##
	
##
# java class to start
CLASS=org.python.util.jython

# extra classpath for jython (might be outside of VLET_INSTALL!) 
CLASSPATH=$JYTHON_JAR:$CLASSPATH
export CLASSPATH

# Start bootstrapper which starts VRS enabled jython ! 
$JAVA -cp $CLASSPATH -Dvlet.install.sysconfdir=$VLET_SYSCONFDIR $JYTHON_PROPERTIES -jar $BASE_DIR/bin/bootstrapper.jar $CLASS $JYTHON_FILE $@
# keep return value:
RETVAL=$?

# return with exit code from java class
exit $RETVAL

