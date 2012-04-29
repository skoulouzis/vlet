// start 
#ifndef _ptcutil_hpp_
#define _ptcutil_hpp_
#include "ptcdefs.hpp" 
// 
// ----------------------------------------------------------------
// The following headers are auto generated from:                  
//     - File: ptcutil.cpp =>: ptcutil.hpp (29-04-2012 10:56:32)                         
// ----------------------------------------------------------------
// 
/**
 * (C) Piter.nl 2010-2012 (http://www.piter.nl/source/ptcutil)
 * File: ptcutil.cpp
 * Info:
 * 		P.T.'s C(PP) Stuff
 *      Generic C wrapper methods to standard (gnu/posix) methods.
 *      The methods are NULL pointer safe and use Assertions where possible.
 *      If an assertion fails, the program will halt.
 *
 * 		Typed methods are 'prefixed' as follows:
 * 		- "M" memory methods
 * 		- "S" plain string methods using char* as type
 * 		- "F" plain file methods
 */
;

/**
 *============================================================================
 *Memory methods: See ptcdefs.hpp for alloc/free macro definitions
 *============================================================================
 */
;

/**
 * Copy memory.
 */
void* Mcopy(void* destination,const void* source,size_t len);

/**
 *=============================================================================
 * String/char* methods
 *=============================================================================
 */
;

/**
 * NULL pointer safe string length.
 */
size_t Slength(const char *str);

/**
 * NULL safe string comparer. Can compare NULL pointers as well
 */
int Scompare(const char*s1, const char *s2);

/**
 * String duplicate.
 * Also conveniant for converting (const char *) to (char *)!
 */
char *Sduplicate(const char *str);

/**
 * String splitter.
 * Splits 'source' at first separator char 'sepC' into **left and **right string.
 */
int Ssplit(const char *source, const char sepC, char **left, char**right);

/**
 * Safe substring from source[begin] upto (not including) source[end].
 */
char *Ssubstring(const char *source, int begin, int end);

/**
 * Safe string appender. Appends s2 at the end of s1 and returns a NEW string.
 */
char *Sappend(const char *s1, const char *s2);

/**
 * Appends string: s1+s2+s3 and returns new string.
 * Is NULL pointer save.
 */
char *Sappend(const char *s1, const char *s2,const char *s3);

/**
 * Append list of strings. Returns new allocated String.
 * The seperatorString sepString is optional (may be NULL)
 */
char *Sappend(int stringc,const char *stringv[],const char *sepString);

/**
 * Strip single or double quotes in environment variables which is allowed in WineDos.
 */
char *SstripQuotes(char *str);

/**
 *============================================================================
 *File methods
 *============================================================================
 */
;

/**
 * Check whether file <filepath> exists.
 * @return 1 if it exists,0 if not and <0 if and ERROR occured
 */
int Fexists(const char *filepath);

/**
 * Read single line from file pointer *fp.
 * Method changes state of FILE *fp
 */
char *Freadline(FILE *fp);

/**
 * Read complete contents and return as (ASCI) string (char*).
 * Return contents as (ASCI) string (char*).
 * This method is for small ASCI files and is not optimized.
 */
char *Fread_text(const char *filepath);

/**
 * Read contents up to a maximum of maxLength.
 * Return contents as (ASCI) string (char*).
 * This method is for small ASCI files and is not optimized.
 */
char *Fread_text(const char *filepath, int maxLength);

/**
 * File length.
 * If the value <0 the the file can't be found.
 * @param: const char *file - path of file .
 * @returns: off_t (64 bits) file length;
 */
file_size_t Flength(const char *file);

/**
 * File length.
 * If the value <0 the the file can't be found.
 * @param: FILE *fp file pointer
 * @returns: off_t (64 bits) file length;
 */
file_size_t Flength(FILE *fp);

/**
 * Write text (char*) to file.
 */
int Fwrite(const char *file,const char *text);

/**
 * Write memory (bytes) to file.
 */
int Fwrite(const char *file,const void *ptr,size_t numBytes);

/**
 * Write memory to file.
 * Size of memory written to file = elementSize*numElements.
 */
int Fwrite(const char *file,const void *ptr,size_t elementSize,size_t numElements);

#endif // _
