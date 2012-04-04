#!/bin/bash
##
#

#arguments: 

headerfile=etc/header.txt
 
if [ -n "$1" ] ; then
    headerfile="$1"
fi 

replaceHeader()
{
 if [ -z "$1" ] ; then
    echo "Usage $0: "'<java file> <header file>'
    exit 1
 fi

 if [ -z "$2" ] ; then
    echo "Usage $0: "'<java file> <header file>'
    exit 1
 fi

 # print header
 cat $2

 # print java source without (old) header.
 # Use 'package' as code start

 cat "$1" | sed -n '
  # from start to "package"
  0,/package/ {
                # remove lines exluding 'package'
                /package/ !{
                             x
                             d
                }
  }
  # print rest
  p
 '
} 

for file in `find . -name "*.java"` ; do 
  echo updating file: $file
  replaceHeader "$file" $headerfile  > "$file".new 
  res=$?

  if [ $res != 0 ]  ; then 
      echo "Error. replace script failed"
      exit 1 
  fi 

  diff "$file" "$file".new
  echo "proceed ? [Y/n/s/q]"
  read answer 
  case $answer in 
        ""|[yY]) 
              mv "$file" "$file".old 
              mv "$file".new "$file" 
              rm "$file".old
              ;; 
        [nN])
              exit  2  
              ;; 
        [sS])
              echo "Skipping:" $file 
	      rm "$file".new 
              ;; 
        [qQ])  
              exit 2
              ;;
        *) 
              echo "Error"
              ;; 
  esac
done  

