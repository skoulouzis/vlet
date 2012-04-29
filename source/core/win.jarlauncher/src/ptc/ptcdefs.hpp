#ifndef _PTCDEFS_HPP_
#define _PTCDEFS_HPP_

/**
 * (C) Piter.nl 2010-2012 (http://www.piter.nl/source/ptcutil)
 * P.T. 's C(PP) Stuff.
 * Since this code is cross compiled using mingw32 amongst others, not much libraries can be used.
 * Keep dependencies to a minimum.
 * This file contains Standard C dependencies, but the C++ syntax and features are used.
 * Standard (plain) C files are in lower case. These are the C 'core' :
 * - ptcdefs.hpp
 * - ptcutils.hpp
 * - ptcutils.cpp
 *
 * The idea is to provide a standard "C" interface across different platforms.
 *
 */

#include <stdio.h>
#include <stdlib.h>
// use standard types !
#include <sys/types.h>

// Not All C compiler used to define this:
#ifndef NULL
#define NULL ((void*)0)
#endif

// ===================================
// Standard definitions.etc.
// ===================================

#define MAX_SHORT_TXT  1024        // default text buffer size for SHORT txt/messages.
#define MAX_BIG_TXT    1024*1024L  // default text buffer size for LONG strings.

// Default debug,warn,info and error Printf! -> redefine for different debug levels !
// #ifndef DEBUG

#define debugPrintf(format, args...)  fprintf (stderr,format, args)
#define warnPrintf(format, args...)   fprintf (stderr,format, args)
#define infoPrintf(format, args...)   fprintf (stderr,format, args)
#define errorPrintf(format, args...)  fprintf (stderr,format, args)
#define fatalPrintf(format, args...)  fprintf (stderr,format, args)
#define stdoutPrintf(format, args...) fprintf (stdout,format, args)

// Characters
#define CHAR_NUL 0x00
#define CHAR_CR  0x0a  // NEWLINE
#define CHAR_LF  0x0d  // LINEFEED
#define CHAR_TAB '\t'  // TAB
#define CHAR_ESC 0x1b  // ESC

// Control codes:
#define CTRL_ETX 0x03     // CTRL-C:
#define CTRL_C   CRTL_ETX
#define CTRL_EOT 0x04;    // CTRL-D:
#define CTRL_D   CTRL_EOT
#define CTRL_ESC CHAR_ESC

//typedefs, be more descriptive about the type it specifies
typedef  off_t file_size_t; //

// Plain Assertions. Print error and abort program.
#define ASSERT_NOTNULL(text,ptr)               { if (ptr==NULL)       { fatalPrintf("*** ASSERT::%s#%d:NULL Pointer:%s\n",__FILE__,__LINE__,text); exit(-1); } }
#define ASSERT_TRUE(text,value)                { if (!value)          { fatalPrintf("*** ASSERT::%s#%d:%s\n",__FILE__,__LINE__,text); exit(-1); } }
#define ASSERT_FALSE(text,value)               { if (value)           { fatalPrintf("*** ASSERT::%s#%d:%s\n",__FILE__,__LINE__,text); exit(-1); } }
#define ASSERT_EQUAL(text,expected,value)      { if (expected!=value) { fatalPrintf("*** ASSERT::%s#%d:%s\n",__FILE__,__LINE__,text); exit(-1); } }
#define ASSERT_LOWER(text,lower,higher)        { if (lower>=higher)   { fatalPrintf("*** ASSERT::%s#%d:%s\n",__FILE__,__LINE__,text); exit(-1); } }
#define ASSERT_HIGHER(text,higher,lower)       { if (higher<=lower)   { fatalPrintf("*** ASSERT::%s#%d:%s\n",__FILE__,__LINE__,text); exit(-1); } }
#define ASSERT_LOWEREQUAL(text,lower,higher)   { if (lower>higher)    { fatalPrintf("*** ASSERT::%s#%d:%s\n",__FILE__,__LINE__,text); exit(-1); } }
#define ASSERT_HIGHEREQUAL(text,higher,lower)  { if (higher<lower)    { fatalPrintf("*** ASSERT::%s#%d:%s\n",__FILE__,__LINE__,text); exit(-1); } }

#define FAIL(file,line,text,exitVal) { fatalPrintf("***FAIL:%s#%d%s\n",file,line,text); exit(exitVal); }

//=============================================================================
//Memory wrappers and macros
//=============================================================================

inline void* _alloc(const char *file,int line,size_t size)
{
	// optional allocate strings in special string/char memory.
	void* ptr = malloc(size);
	if (ptr==NULL)
	{
		FAIL(file,line,"_alloc(): Possible out of memory error. Got NULL from malloc.",-1);
	}
	// MALLOC_REGISTER(ptr,size);
	return ptr;
}

inline void _free(const char *file,int line, void *ptr)
{
	if (ptr==NULL)
		return;

	// optionally free strings in special string/char memory.
	// MALLOC_CHECK_FREE(str);
	free(ptr);
	// MALLOC_FREE(str);
}

// Macros to call _alloc and _free. Appear as methods, but implementation is 'inline'.
#define Salloc(size) (char*)_alloc(__FILE__,__LINE__,size)
#define Malloc(size) _alloc(__FILE__,__LINE__,size)
#define Sfree(ptr) _free(__FILE__,__LINE__,ptr)
#define Mfree(ptr) _free(__FILE__,__LINE__,ptr)
// allocated in 'static' memory, won't be released during lifetime of process.
#define MstaticAlloc(ptr) _alloc(__FILE__,__LINE__,size)

// ================================================
// C++ Bindings.
// ================================================

/// see PtcDefs.hpp ?

#endif // _PTCDEFS_HPP_
