###
# (C) VL-e consortium. 
# redistribution prohibited. 
#   
## 
# file   : Makefile 
# project: VL-e Toolkit (VLET) 
# author : Piter T. de Boer 
#
# info: 
#    Makefile Wrapper for the ant targets: 
#    The ant file in this toplevel distribution is also 
#    a wrapper to the mbuild component. 
#
# configuraton: 
#    This project uses ant as main build tools 
#    Project settings are taken from: project.prop and build.prop 

##
# Messages 

ANT_ERROR			="*** Error: ant not found or not on your PATH"
JAVA_ERROR			="*** Error: javac not found or not on your PATH"
JAVAC_ERROR			="*** Error: javac not found or not on your PATH"
JAVA_VERSION_ERROR	="*** Error: Wrong java version. Need >=1.6"

.PHONY: all configure build-check dist install clean

##
# Main Target: 

all: dist 

vlet: dist

vlet-all: dist-all

##
# Do some basic sanity checks, so user is informed at an early stage
# (this should be handled by ./configure) 

build-check:
	@echo "=== VLET Makefile ==="
	@echo " This is a wrapper for the ant build files" 
	@echo "===               ==="
	@which ant >/dev/null || (echo ${ANT_ERROR} ; exit 1)
	@which java >/dev/null || (echo ${JAVA_ERROR} ; exit 1) 
	@which javac >/dev/null || (echo ${JAVAC_ERROR} ; exit 1) 

#disabled: 	
#@[ `java -version 2>&1 | grep version | sed "s/[^0-9]*[0-9]\.\([0-9]\).*/\1/"` -ge 6 ] || (echo ${JAVA_VERSION_ERROR} && exit 1) 
	
##	
# Build distribution 
# The default build will build the distribution in 'dist'
# The 'make install' command will copy this installation in the 'prefix-dir'.  

dist: build-check
	ant -f build.xml dist-install
	
dist-all: build-check
	ant -f build.xml dist-install-all

##
# Optional configure (without, defaults will be assumed) 
 
configure:
	ant -f build.xml bin-install

##
# Check/Test
#

check: test-dist

test: test-dist

test-dist:
	ant -f build.xml test-dist


##
# install (binary) distribution into prefix dir. 
#
install:
	ant -Dproject.destdir=${DESTDIR} -Dproject.install.docdir=${docdir} -f build.xml bin-install


##
# clean targets 

clean:
	ant -f build.xml  clean
	
dist-clean:
	ant -f build.xml  dist-clean
	
	
