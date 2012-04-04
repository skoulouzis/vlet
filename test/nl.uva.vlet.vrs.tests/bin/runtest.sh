#!/bin/bash
##
#
# run tests. 
# 

# start testing: 
if [ -z "${VLET_INSTALL}" ] ; then 
     VLET_INSTALL=".."
fi

export CLASSPATH=junit.jar:vletTests.jar:ant-junit.jar 
ant -f runtest.xml $1 -Dvlet.install=.. 

