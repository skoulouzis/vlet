#!/bin/bash 
##
#                (C) WWW.VL-E.NL
#
# Project : VLET
# File    : vletenv.sh
# Author : Piter T. de Boer 
# Info:
#           This script sets the VLeT environment. 
#           Most functionality has moved to the bootstrap class
#           which finds the libraries and sets the classpath. 
#           See: nl.uva.vlet.bootstrap.* package 
#           Note: this file is sourced, do not use 'exit' 
#
 
###
# Default Settings: 
# 
cygwin=false;
linux=false; 
VLET_DEBUG=""; 

###
#
# functions

println()
{
  # copy to stderr ! 
  echo "$@" >&2
}

debug()
{
  if [ -n "$VLET_DEBUG" ] ; then 
     # copy to stderr ! 
     echo "DEBUG:$@" >&2
  fi
}

verbose()
{
  # copy to stderr ! 
  echo "$@" >&2
}

##

##
# VLET_SYSCONFDIR 
# Set the following variable when the configuration files or NOT under $VLET_INSTALL/etc ! 
# This is automaticaly done when performing a binary installation (gnu/linux style).
# Typically this file is already located in the SYSCONFDIR ! 
# Specifying here has no effect, It should already (at an earlier stage) be set. 
#VLET_SYSCONFDIR=/etc/vlet

### 
# Runtime enviroment
# Local: cygwin 

case `uname` in 
 CYGWIN*) 
      cygwin=true; 
      arch=cygwin
      ;; 
 Linux*|linux*)
      linux==true; 
      arch=linux
      ;; 
 *) 
 	# Windows has its own startup vbrowser.exe 
      ;; 
esac

###
# startup directory: 
# Global: BINDIR VLET_INSTALL

# I) check VLET_SYSCONFDIR
# II) check VLET_INSTALL
# III) try to get installation directory from startup script. 
	
if [ -n "$VLET_SYSCONFDIR" ] ; then
    # use settings from VLET_SYSCONFDIR
    VLET_INSTALL=`grep -v "#" "$VLET_SYSCONFDIR/vletrc.prop" | grep 'vlet.install=' | cut -d '=' -f 2` 
    BINDIR=`grep -v "#" "$VLET_SYSCONFDIR/vletrc.prop" | grep 'vlet.install.bindir=' | cut -d '=' -f 2`
elif [ -n  "$VLET_INSTALL" ] ; then
    # use VLET_INSTALL 
    BINDIR="${VLET_INSTALL}/bin"
fi

#defaults: fallback to script startup environment  
if [ -z "$BINDIR" ] ; then 
    BINDIR=`dirname "$0"`
    # resolve optional relative path !
    BINDIR=`cd "$BINDIR" ; pwd`
fi

if [ -z "$VLET_INSTALL" ] ; then  
    BASE_DIR=`dirname "$BINDIR"`
    VLET_INSTALL="$BASE_DIR"
fi

export VLET_INSTALL BINDIR 

##
# VBrowser jar
# 
# vbrowser.jar is a subclass of the bootstrap class which does now
# the most configuration it muse be in the same location as this script

VBROWSER_JAR="$BINDIR/vbrowser.jar" 
 
###
# CLASSPATH
# Global: CLASSPATH 

# CLASSPATH not needed anymore: browser.jar/bootstrap.jar now configures java environment. 
# CLASSPATH="$CLASSPATH:$VLET_INSTALL:$VLET_SYSCONFDIR/etc"

###
# Libraries
# Global: LD_LIBRARY_PATH
# to be moved (currently not needed) to bootstrap class. 
#LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$VLET_INSTALL/lib/linux:$VLET_INSTALL/lib/auxlib/linux"
#export LD_LIBRARY_PATH

###
# Convert CYGWIN paths: 
#

if [ $cygwin == "true" ] ; then 
   # convert unix style paths to windows compatible paths 
   println "--- Cygwin detected ---" 
   # cehck optional empty CLASSPATH
   if [ -n "$CLASSPATH" ] ; then 
       CLASSPATH=`cygpath -m -p $CLASSPATH` 
   fi
   VLET_INSTALL=`cygpath -m $VLET_INSTALL`
   VLET_SYSCONFDIR=`cygpath -m $VLET_SYSCONFDIR`
   # PATCH: to avoid the use of windows' find.exe make 
   # sure the Cygwin path is searched first.
   PATH="/usr/bin:$PATH"
   # windows does not have a LD_LIBRARY_PATH, it uses PATH instead:
   PATH="$PATH:$VLET_INSTALL/lib/win:$VLET_INSTALL/lib/auxlib/win"
   VBROWSER_JAR=`cygpath -w "$VBROWSER_JAR"`  
fi

###
# Check java version 
# Global: JAVA_HOME, JAVA 

if [ -n "$JAVA_HOME" ] ; then 
   # let JAVA_HOME override the default 'java' command 
   JAVA="$JAVA_HOME/bin/java"
   debug "java=$JAVA_HOME/bin/java"
else
   # use default java command from path
   if which java ; then 
       debug "java:using default java command from path." 
   else 
       println "*** Error: No Java Found ***"
       println " Java was not found on your path."
       println " Please set JAVA_HOME or put JAVA_HOME/bin on your PATH"
       println ""
       exit
   fi 
    JAVA=java
fi 

##
# Check java

export JAVA 

VERSION=`"$JAVA" -version 2>&1 | grep "version" |  sed "s/.*\([0-9].[0-9_]*\.[0-9_]*\).*/\1/"` 
MINOR=`echo $VERSION | cut -d '.' -f 2`

if [ "$MINOR" -lt 6 ] ; then 
   println "*** Wrong java version ***"
   println " You need java 1.6. Current version="$VERSION
   println " Current used java location="$JAVA 
   println " You can specify the java location by seting JAVA_HOME" 
   exit 1
fi  

###
# JYTHON settings: 
JYTHON_JAR=$VLET_INSTALL/auxtools/jython/jython.jar
JYTHON_CACHE=$HOME/.jythoncache

###
#Exit: 
#When sourced from another script, no return status must be given!
VLETENV_SH=1
export VLETENV_SH
