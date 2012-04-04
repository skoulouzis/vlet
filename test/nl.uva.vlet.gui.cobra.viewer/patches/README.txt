#
# Patches for the Cobra/Lobo classes:
#

# patch for 0.98.1: 

ImgControl.java
===
When only a height or a width tag is specified, the
ImgControl render class doesn't respect the img's aspect ratio. 

# Update:
Seems to be patched in 0.98.4