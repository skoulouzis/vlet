#!/bin/bash
##
#
#


JARS=`ls lib/* | tr '\n\t ' '   ' | sed -e "s/jar[ ]*/jar,/g" `
echo $JARS

cat view.html.tem | sed "s@JARS@$JARS@" > view.html

