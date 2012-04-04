#
#    Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
# 
#  Licensed under the Apache License, Version 2.0 (the "License").  
#  You may not use this file except in compliance with the License. 
#  You can obtain the Apache Licence at the following location: 
#        http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software 
#  distributed under the License is distributed on an "AS IS" BASIS, 
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
#  See the License for the specific language governing permissions and 
#  limitations under the License.
# 
#  See: http://www.vl-e.nl/ 
# 
###
# Version : @VLET_VERSION@
# Date    : @DATE@
#
# README


Info: 
=== 
 This is the experimental VL-e Toolkit (VLET). 
 For more information, mail: vlet-users@lists.vl-e.nl


Installation:
===
 Make sure Java 1.6 is installed on your system. 
 See the INSTALL.txt file for short instructions or the UserGuide
 documentation in the "doc" directory of this installation for more
 descriptive installation instructions. 
 See the BUGS.txt for currently known bugs and unimplemented features. 
 Checkout the Latest ReleaseNotes.txt for actual new about this distribution. 


Distribution structure: 
===
 README.txt       ; This file 
 INSTALL.txt      ; Quick Installation Notes
 LICENSE.txt      ; License information 
 ReleaseNotes.txt ; Latest news about this distribution 
 BUGS.txt         ; Known bugs and missing features
 doc/             ; Documentation 
 bin/             ; Current installed tools 
 etc/             ; Configuration files 
 etc/certificates ; Root CA certificates
 etc/vomsdir      ; VOMS configuration files 
 lib/             ; Libraries 
 lib/icons        ; Icons directory (put custom icons here).  
 lib/auxlibs      ; 3rd party libraries and tools
 lib/plugins      ; Plugins: Protocol driver (VDriver) and Viewers


Current installed tools (in ./bin/): 
===
 uricopy.sh      ; URI copy tool for all VFS supported filesystems. 
 urils.sh        ; List remote resources. 
 uristat.sh      ; Perform a 'stat' like command on a remote resource. 
 vbrowser.sh     ; Start the VBrowser for interactive filemanagement. 
 vbrowser.exe    ; Customized Windows startup executable.
 vbrowser.ini    ; Windows ini file for java startup configuration. 
 vbrowser.jar    ; platform independent Java Startup jar.
                   double click or start with: 'java -jar vbrowser.jar'
 jython.sh       ; Jyton (Java Python) startup script. 
 vterm.jar       ; Startup jar for a simple (beta version) of a VT100+
                   terminal emulator (with some xterm extension).

