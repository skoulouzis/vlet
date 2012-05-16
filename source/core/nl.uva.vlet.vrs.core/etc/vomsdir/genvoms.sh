#!/bin/bash
##
#
cat voms.xml.header
cat voms.inf | sed "s/\"//g" | awk -F "," '
 BEGIN { FS="," ; 
    print "<!-- generated VOMS.xml from voms.inf -->"
    print "<voms>"
    } 
 { if (match($0,"#.*")==0) { COMMENT=0 } else {COMMENT=1} } 
 (COMMENT==1) { print "###"$0}  
 (COMMENT==0) { 
     name=$1; 
     host=$2; 
     port=$3;
     DN=$4;
     VO=$5; 
     vonames[name]=name; 
     print " "
     print "  <vo>"
     print "    <name>"name"</name>"
     print "    <admin>https://"host":8443/voms/"VO"/webui/admin</admin> <!-- guessed... -->"
     print "    <server>"
     print "       <name>"host"</name>"
     print "       <port>"port"</port>"
     print "       <cert>"host".pem</cert>" 
     print "       <dn>"DN"</dn>"  
     print "    </server>"
     print "  </vo>"
     ;
 } 

 END { 
    print "</voms>" 
    print "<!-- END generated voms.xml  -->"
 } 
'



