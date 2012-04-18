// Debugging 

//#define DEBUG 1

#ifdef DEBUG
  #define DEBUGPRINTF(format, args...) fprintf(stderr, format, args)
#else
  #define DEBUGPRINTF(format, args...) /* debug */
#endif
