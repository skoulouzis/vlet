*******************************************************************************
* The java.io.File Compatibility classes                                      *
*******************************************************************************

This package consists of wrapper classes for the following classes:

java.io.File
java.io.FileInputStream
java.io.FileOutputSream
java.io.FilenameFilter
java.io.FileFilter

The classes in this package are under construction and haven't been fully
tested. They only exists to ease the transition from a Java Legacy application to
the VFS.
Note that the VFS always needs an VRSContext and these classes use a static
method to get a VFSClient with a global initialized VRSContext. 
For most single user applications this suffices, but it is recommended to 
use a private VRSContext. 

Not all methods are implemented. These methods are tagged with the deprecated flag. 
Also Exceptions are wrapped as Errors in the case the original method doesn't declare 
an exception to be thrown to stay java.io.File compatible. 

Extra VFS methods have been added so corresponding VFS classes can be fetched to ease
transition to the VFS. 

Enhancements:
- new File(Sting name) supports URI string in the name as well. 
- new File(URI name) support all VRS Registered protocols. 
- File class has been enhanced with VFS Methods.

