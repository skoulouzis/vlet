The acacia jar file has been patched. 

The original jar file contained hardcoded setting for log4j which created  a /tmp/acacia.log file. 
This made the code unusuable for multi user environments since the above file was created 
with default user permissions blocking other users to create this file. 

Piter T. de Boer 
