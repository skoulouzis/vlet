###
# Project : VL-e Toolkit
#
# Author  : P.T. de Boer  
#
# Info: 
#    Makefile for the UserGuide LaTeX document
#    uses pdflatex to create the pdf file: UserGuide.pdf
#    Autoconverts images from images/ to .eps files. 
#

.PHONY:	dist build dirs dvi pdfs pdflatex

# Build environment 
ROOT_DIR	=$(shell pwd)
DIST_DIR	=dist
BUILD_DIR	=build
FIG_DIR		=figs
GIF_DIR		=images
EPS_DIR		=images/eps
TEX_DIR		=tex
#absolute path needed:
STYLES_DIR	=$(ROOT_DIR)/styles

# Figures: scan fig/ and images/ directory  
#FIGS 		:= $(shell ls ${FIG_DIR}/*.fig)
# GIF and PNG are treated the same way.
GIFS 		:= $(shell ls ${GIF_DIR}/*.gif ${GIF_DIR}/*.png)
# already converted EPS files (legacy files) 
EPS 		:= $(shell ls ${EPS_DIR}/*.eps)

# Use ALL LaTeX files found in tex/ to trigger rebuilding the document 
TEX_SOURCES      := $(shell ls ${TEX_DIR}/*.tex)

# .fig,.gif,.png,.eps  => .eps  (.eps source is copied to build dir)
FIG2EPS		= $(FIGS:${FIG_DIR}/%.fig=${BUILD_DIR}/%.eps)
GIF2EPS		= $(GIFS:${GIF_DIR}/%.gif=${BUILD_DIR}/%.eps) $(GIFS:${GIF_DIR}/%.png=${BUILD_DIR}/%.eps) 
EPS2EPS		= $(EPS:${EPS_DIR}/%.eps=${BUILD_DIR}/%.eps)

# All eps files which are needed.  
ALL_EPS		= ${FIG2EPS} ${GIF2EPS} ${EPS2EPS} 

# .eps => .pdf All PDFs needed for pdflatex (note this line is NOT used to create PDF from EPS!)  
PDFS		= ${ALL_EPS:${BUILD_DIR}/%.eps=${BUILD_DIR}/%.pdf} 

#Paper Name/Main document: 
DOC		= UserGuide
DOC_TEX	= $(TEX_DIR)/${DOC}.tex
DOC_AUX = ${DOC}.aux
DOC_PDF	= $(DIST_DIR)/${DOC}.pdf
DOC_DVI	= $(BUILD_DIR)/${DOC}.dvi

# optional gvlam LaTeX templates
ifdef VLET_INSTALL
	PROJECT_TEX=$(VLET_INSTALL)/tex
endif 

# NOTE: end TEXINPUTS with ':' for latex to trigger appending its default texinputs to.
TEXINPUTS=.:$(ROOT_DIR)/$(TEX_DIR):$(STYLES_DIR):

# B
BIBLIO_BIB = $(TEX_DIR)/biblio.bib 


### 
# Make Targets  

default: dist 

# target: create both .dvi and pdf file
dist: info dirs ${DOC_DVI} ${DOC_PDF}  

#
info:
	@echo "PWD         = $(ROOT_DIR)"
	@echo "Main DOC    = $(DOC)"
	@echo "Tex Sources = $(TEX_SOURCES)"
	@echo "FIGS        = $(FIGS)"
	@echo "GIFS        = $(GIFS)"
	@echo "ALL_EPS     = $(ALL_EPS)"
	@echo "TEXINPUTS   = $(TEXINPUTS)"
#
dirs: 
	mkdir -pv ${DIST_DIR} ${BUILD_DIR} 

# 
dvi: ${DOC_DVI} 

pdfs: ${PDFS} 

pdflatex: ${DOC_PDF} 

###
# PDF: Use better pdflatex and faster command. 
# Note that the pdflatex command needs .pdf (or .png/.jpg) files, not .eps files. 
#   Make sure no (image) extension are used in the \includegraphics command. 
#   the latex and pdflatex command will search for .eps and .pdf automatically 



${DOC_PDF}: dirs ${PDFS} ${DOC_TEX} ${TEX_SOURCES}
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; pdflatex ${DOC}.tex )
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; cp ../${BIBLIO_BIB} . )
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; bibtex ${DOC_AUX} )
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; pdflatex ${DOC}.tex )
	cp -f ${BUILD_DIR}/${DOC}.pdf ${DIST_DIR} 

html: ${JPGS} 
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR}/pdf ; latex2html ${DOC}.tex )

### 
# Make Rules 
 
# 
#${DOC_PDF}: ${DOC_DVI}
#	# next command make take a while.
#	(cd ${BUILD_DIR} ; dvipdf ../$< ../$@)

${DOC_DVI}: dirs ${ALL_EPS} ${DOC_TEX} ${TEX_SOURCES} 
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; latex ../${DOC_TEX} )
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; cp ../${BIBLIO_BIB} . )
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; bibtex ${DOC_AUX} )
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; latex ../${DOC_TEX} )
	(export TEXINPUTS=${TEXINPUTS}; cd ${BUILD_DIR} ; latex ../${DOC_TEX} )


# Pattern Rules: Build needed grapichs format on the fly in ${BUILD_DIR}  

# Convert .fig -> .eps 
$(BUILD_DIR)/%.eps: $(FIG_DIR)/%.fig
	fig2dev -L eps $< $@

# Same for .gif -> .eps
$(BUILD_DIR)/%.eps: $(GIF_DIR)/%.gif
	convert $< $@
	
# Same for .png -> .eps
$(BUILD_DIR)/%.eps: $(GIF_DIR)/%.png
	convert $< $@

# Same to .gif -> .pdf 
$(BUILD_DIR)/%.pdf: $(GIF_DIR)/%.gif
	convert $< $@
	
# Same to .png -> .pdf 
$(BUILD_DIR)/%.pdf: $(GIF_DIR)/%.png
	convert $< $@

# Convert .eps to .pdf (legacy EPS files ) 
# Note: 
#$(BUILD_DIR)/%.pdf: $(EPS_DIR)/%.eps
#	epstopdf $< -outfile=$@

# Note: uses converted eps from the eps2eps command 
$(BUILD_DIR)/%.pdf: $(BUILD_DIR)/%.eps
	epstopdf $< -outfile=$@

# Copy legacy eps from eps dir to build dir. Use eps2eps to 'cleanup'  
$(BUILD_DIR)/%.eps: $(EPS_DIR)/%.eps 
	eps2eps $< $@

# clean
clean:
	rm -rf ${DIST_DIR} ${BUILD_DIR}
