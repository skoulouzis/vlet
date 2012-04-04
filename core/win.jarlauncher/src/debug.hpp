// Debugging 

//#define DEBUG 1

#ifdef DEBUG
  #define debug(format, args...) fprintf (stderr, format, args)
#else
  #define debug(format, args...) /* debug */ 
#endif
