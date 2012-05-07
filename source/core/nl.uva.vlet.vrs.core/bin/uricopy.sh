#!/bin/bash 
##
# Project : VLET
# File    : uricopy.sh 
# Author  : P.T. de boer 
# Info    : 
#      script interface to the VFSClient of VLET.

##
#Usage: 
#
# uricopy.sh [-v] [-debug] -D<prop>=<value> [-move] [-force] <sourceVRL> <destinationVRL>
#
# Options:
#   -move            : delete source file or directory. 
#   -force           : always overwrite existing destination. 
#   -mkdirs          : create distination directory 
#   -result          : print resulting URI 
#   -v               : be verbose, specify twice for more. 
#   -debug           : print DEBUG information. 
#   -info            : print INFO  information 
#   
#   -D<prop>=<value> : set (java) system variable or (SRB) property

##
# === SRB NOTES ===
#
# Useing '~' as path ('srb:///~') will use 'mdasCollectionHome' as default directory
#
# Examples: 
#    copy to your default home at your default SRB server: 
#
#       uricopy "file://localhost/$HOME/tmp/MyFile" "srb:///~" 
#
#    Specify other user then default username: 
# 
#       uricopy -Dsrb.username=pipo.de.clown "file://localhost/$HOME/tmp/MyFile" "srb:///~" 
#
#    move from specific location: 
#
#      uricopy -move "srb://piter.de.boer.vlenl@srb.grid.sara.nl:50000/VLENL/home/piter.de.boer.vlenl/mp3/Bach/fugas/01-fuga.mp3" "file://localhost/home/ptdeboer/tmp"
#
# Options: 
#   most of the srb setting from your .srb/.MdasEnv file can be specified with
#   the -D option.
#   
# IMPORTANT: 
#   - To avoid name clashes with other system variables use "srb." as prefix to 
#     SRB properties.
#     For example: -Dsrb.username=pipo -Dsrb.mcatZone=MyZone
#   - Not all SRB settings have been tested yet.
#   - Spaces and other 'strange' character in URI's need to be encoded as '%20'
#     They will be decoded when doing the actual copy. 
#   - SRB does NOT except the "'" (single quote) character as part of a filename!
#   - Destination URIs *MUST* be an existing directory to avoid inconsistent behaviour. 
#     new directories and files will alway be created in this destination directory !  
#   - In the case of firewall problems, specify: GLOBUS_TCP_PORT_RANGE with the 
#     allowed port range settings. 
#     or use -DFirewallPortRange=<min>,<max> 

##
# settings 

##
# VLET_SYSCONFDIR 
# Set the following variable when the configuration files are NOT under $VLET_INSTALL/etc ! 
# This is automaticaly done when performing a binary installation 
#VLET_SYSCONFDIR=/etc/vlet


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
# java class to start
CLASS=nl.uva.vlet.vrs.tools.URICopy

# Start bootstrapper which does the rest 
"${JAVA}" ${VLET_JAVA_FLAGS} -Dvlet.install.sysconfdir=$VLET_SYSCONFDIR -jar $BASE_DIR/bin/bootstrapper.jar  $CLASS $@
# keep return value: 
RETVAL=$? 

#return exit code from VFSCopy
exit $RETVAL; 
