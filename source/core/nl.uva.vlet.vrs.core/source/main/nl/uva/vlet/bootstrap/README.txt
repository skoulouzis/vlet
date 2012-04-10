###
# Package: Bootstrap
#
#  (C) www.VL-e.NL  (2006,2009) 
#  (C) Piter.NL     (2004-2006)
# 

 ===
 Important 
 ===

 ALL packages in this class must not refer to other vlet.* packages as 
 this may trigger class loading of classes WHICH ARE NOT YET on the classpath. 
 The bootstrap classes dynamically add the jars to the 
 classpath and start (bootstrap) the desired class. 
 Also this class will be compiled as java 1.5 code, so the bootstrap
 class can check the Java version (which must be 1.6 or higher).

 