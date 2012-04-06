###
# (C) Piter.NL 2004-2010 
#
# Piter.NL has granted VL-e.nl the exclusive right to use this 
# source. 
#
# Makefile for linux to build a windows application 
# to start a Java Application !!! 
#
# Cross platform development. You gotta love it. 
# 
# Piter T. de Boer
# ---
###

DIST		=dist
BUILD 		=build
SRC			=src
ICONDIR		=icons

### ming32-gcc compiler: 
## i386
#WCC		=i386-mingw32-gcc
#WSTRIP		=i386-mingw32-strip
#WINDRES 	=i386-mingw32-windres
##i586
MINGW32     =i586-mingw32msvc
WCC         =${MINGW32}-gcc
WSTRIP      =${MINGW32}-strip
WINDRES     =${MINGW32}-windres
##i686
#WINGW32    =i686-pc-mingw32
#WCC        =${MINGW32}-gcc
#WSTRIP     =${MINGW32}-strip
#WINDRES    =${MINGW32}-windres

#FLAGS        =-g
COMPILE_FLAGS = -I${SRC}
LINK_FLAGS    =
FLAGS		  = ${COMPILE_FLAGS} ${LINK_FLAGS}

# -I${SRC}

#JARTARGETS	=bootstrapper vbrowser vlterm
JARTARGETS	=bootstrapper vbrowser

EXES		=$(JARTARGETS:=.exe)
JARS		=$(JARTARGETS:=.jar)
ICONS		=$(JARTARGETS:=.ico)

#TARGETS	=hellowin32.exe $(EXES)
TARGETS		=$(EXES)

#targets: 

all:init $(TARGETS) 

init:
	mkdir -p ${DIST}
	mkdir -p ${BUILD}
	
info:
	@echo "Jars ="$(JARS) 
	@echo "Exes ="$(EXES)
	@echo "Icons="$(ICONS)
	@echo "SRC="$(SRC)
	@echo "COMPILE_FLAGS ="$(COMPILE_FLAGS)
	@echo "LINK_FLAGS    ="$(LINK_FLAGS)
	@echo "FLAGS         ="$(FLAGS)
	
	
###
# make rules 
#

hellowin32.exe: $(SRC)/hellowin32.c
	$(WCC) ${COMPILE_FLAGS} $< -o $(DIST)/$@


$(ICONDIR)/%.ico:
	@echo "*** Error. Need icon file:"$@
	exit 1

#generate icon resource file:
$(BUILD)/%_icon.rc: ${ICONDIR}/%.ico 
	echo "1 ICON \"$<\"" > $@

#generate icon resource file:
#$(BUILD)/%_icon.rc: ${ICONDIR}/%.png 
#	echo "1 ICON \"$<\"" > $@


#compile icon resource file 
$(BUILD)/%_icon.o:$(BUILD)/%_icon.rc
	$(WINDRES) $< -o $@ -Iicons


$(BUILD)/%.o:$(SRC)/%.cpp
	$(WCC) ${COMPILE_FLAGS} $< -c -o $@

$(BUILD)/%.o:$(SRC)/ptc/%.cpp
	$(WCC) ${COMPILE_FLAGS} $< -c -o $@


OBJS=${BUILD}/ptcutil.o 

# Specify MAINJAR
%.exe: $(SRC)/jarlauncher.cpp ${OBJS} $(ICONDIR)/%.ico $(BUILD)/%_icon.o 
	$(WCC) ${FLAGS} -DMAIN=\"$(@:.exe=)\" ${OBJS} $(BUILD)/$(@:.exe=_icon.o) $< -o ${DIST}/$@
	$(WSTRIP) ${DIST}/$@

#	cp my3d.jar dist

clean: 
	rm -rf $(BUILD)
	rm -rf $(DIST)


