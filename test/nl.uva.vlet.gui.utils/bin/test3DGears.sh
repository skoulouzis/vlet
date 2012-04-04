#/bin/bash
##
#

echo "VLET_INSTALL=$VLET_INSTALL"
source $VLET_INSTALL/bin/vletenv.sh
java -cp $CLASSPATH demos.jogl.Gears 