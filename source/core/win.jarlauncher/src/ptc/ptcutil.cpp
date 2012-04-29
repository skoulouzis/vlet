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

// Since this code is cross compiled using mingw32 amongst others, not much libraries can be used.
// The aim of this code is to provide a stable and robust interface with as less dependencies as
// possible for plain "C" stuff.

#include <string.h>
#include <stdio.h>
#include <malloc.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

#include "ptc/ptcdefs.hpp"
#include "ptc/ptcutil.hpp"


// Local error printf
void error(const char *format, const char *arg)
{
	fprintf(stderr,"%s:",__FILE__);
	fprintf(stderr, format, arg);
}

// ===
// Low Level string (char*), memory and file methods.
// Although it is recommended using (C++) std::string methods, this isn't always possible
// or wanted.
// The following methods are as save as possible, allowing NULL pointer arguments and
// if possible, try to detect out-of-boundary access or other programming errors.
// If an error is detected the program is aborted.
// ===

/**
 *============================================================================
 *Memory methods: See ptcdefs.hpp for alloc/free macro definitions
 *============================================================================
 */

/**
 * Copy memory.
 */
void* Mcopy(void* destination,const void* source,size_t len)
{
	// MALLOC_VALID(destination,len);
	// MALLOC_VALID(source,len);

	ASSERT_NOTNULL("Mcopy():Destination can not be NULL.\n",destination);
	//ASSERT_NOTNULL("Mcopy:Source can not be NULL\n",source);
	if (source==NULL)
		return NULL;

	// check boundaries: {destination,source,len}
	return memcpy(destination,source,len);
}

/**
 *=============================================================================
 * String/char* methods
 *=============================================================================
 */

/**
 * NULL pointer safe string length.
 */
size_t Slength(const char *str)
{
	// MALLOC_VALID(str);
	if (str == NULL)
		return 0;
	return strlen(str);
}

/**
 * NULL safe string comparer. Can compare NULL pointers as well
 */
int Scompare(const char*s1, const char *s2)
{
	// MALLOC_VALID(s1);
	// MALLOC_VALID(s2);

	if (s1 == NULL)
	{
		if (s2 == NULL)
		{
			return 0;
		}
		else
		{
			return -1; // (s1==NULL) < (s2!=NULL)
		}
	}
	else if (s2 == NULL)
	{
		return 1; // (s1!=NULL) > (s2==NULL)
	}
	else
	{
		return strcmp(s1, s2); // save compare
	}
}

/**
 * String duplicate.
 * Also conveniant for converting (const char *) to (char *)!
 */
char *Sduplicate(const char *str)
{
	// MALLOC_VALID(str);

	if (str == NULL)
		return NULL;

	char *result=strdup(str);
	ASSERT_NOTNULL("Sduplicate():Possible out of memory error. Got NULL from strdup.", result);
	return result;
}

/**
 * String splitter.
 * Splits 'source' at first separator char 'sepC' into **left and **right string.
 */
int Ssplit(const char *source, const char sepC, char **left, char**right)
{
	if (source == NULL)
		return -1;

	// MALLOC_VALID(source);

	// Is possible, just not smart. Here is not the place to check.
	// ASSERT_NOTNULL("In Ssplit(), char **left may not be NULL\n",left);
	// ASSERT_NOTNULL("In Ssplit(), char **right may not be NULL\n",right);

	int len = Slength(source);

	for (int i = 0; i < len; i++)
	{
		if (source[i] == sepC)
		{
			if (left != NULL)
				*left = Ssubstring(source, 0, i);
			if (right != NULL)
				*right = Ssubstring(source, i + 1, len);
			return i;
		}
	}

	// Split char not found.
	if (left != NULL)
		*left = Sduplicate(source);

	if (right != NULL)
		*right = NULL;

	return len;
}

/**
 * Safe substring from source[begin] upto (not including) source[end].
 */
char *Ssubstring(const char *source, int begin, int end)
{
	if (source == NULL)
		return NULL;

	int sourceLen = Slength(source);
	if (end >= sourceLen)
		end = sourceLen;

	int len = end - begin;
	if (len < 0)
		return NULL;

	char *newstr = Salloc(len + 1); // +1 ending zero!
	Mcopy(newstr, &source[begin], len);
	// end string:
	newstr[len] = 0;

	return newstr;
}


/**
 * Safe string appender. Appends s2 at the end of s1 and returns a NEW string.
 */
char *Sappend(const char *s1, const char *s2)
{
	if (s1 == NULL)
		if (s2 == NULL)
			return NULL;
		else
			return Sduplicate(s2); // value method: must return duplicate!
	else if (s2 == NULL)
		return Sduplicate(s1); // value method: duplicate!
	else
		; // continue

	int len1 = Slength(s1);
	int len2 = Slength(s1);
	char *newstr = Salloc(len1 + len2 + 1);

	newstr[0] = 0;
	strcat(newstr, s1);
	strcat(newstr, s2);

	return newstr;
}


/**
 * Appends string: s1+s2+s3 and returns new string.
 * Is NULL pointer save.
 */
char *Sappend(const char *s1, const char *s2,const char *s3)
{
	if ((s1==NULL) && (s2==NULL) && (s3==NULL))
			return NULL;

	// null pointer proof:
	int len1 = Slength(s1);
	int len2 = Slength(s2);
	int len3 = Slength(s3);
	char *newstr = Salloc(len1 + len2 + len3+1); // do NOT forget ending zero.

	newstr[0] = 0;
	if (s1!=NULL)
		strcat(newstr, s1);
	if (s2!=NULL)
		strcat(newstr, s2);
	if (s3!=NULL)
		strcat(newstr, s3);

	return newstr;
}

/**
 * Append list of strings. Returns new allocated String.
 * The seperatorString sepString is optional (may be NULL)
 */
char *Sappend(int stringc,const char *stringv[],const char *sepString)
{
	// PRE
	if ((stringc<=0) || (stringv==NULL))
		return NULL;

	// BODY
	int len=0;
	int sepLen=0;

	if (sepString!=NULL)
		sepLen=Slength(sepString);

	// calculate space
	for (int i=0;i<stringc;i++)
	{
		// allocate enough space, including seperator string (one to many).
		if (stringv[i]!=NULL)
			len=Slength(stringv[i])+sepLen;
	}

	// catenate
	char *newstr=Salloc(len+1);
    newstr[0]=0; // start with empty string;

	for (int i=0;i<stringc;i++)
	{
		if (stringv[i]!=NULL)
		{
			strcat(newstr,stringv[i]);
			// Add seperator string between string elements
			// Note: last seperator string won't be added, but space is allocated
			if ((i+1<stringc) && (sepString!=NULL))
				strcat(newstr,sepString);
		}
	}
	return newstr;
}


/**
 * Strip single or double quotes in environment variables which is allowed in WineDos.
 */
char *SstripQuotes(char *str)
{
	if (str == NULL)
		return NULL;

	int len = 0;

	if ((len = Slength(str)) <= 1)
		return str;

	// Check double quotes:
	if ((str[0] == '"') && (str[len - 1] == '"'))
		return Ssubstring(str, 1, len - 1);

	// Single quotes:
	if ((str[0] == '\'') && (str[len - 1] == '\''))
		return Ssubstring(str, 1, len - 1);

	return str;
}

/**
 *============================================================================
 *File methods
 *============================================================================
 */

/**
 * Check whether file <filepath> exists.
 * @return 1 if it exists,0 if not and <0 if and ERROR occured
 */
int Fexists(const char *filepath)
{
	FILE *fp = fopen(filepath, "r");

	// fp ==NULL -> file not found;
	if (fp==NULL)
		return 0;

	fclose(fp);
	return 1;
}

/**
 * Read single line from file pointer *fp.
 * Method changes state of FILE *fp
 */
char *Freadline(FILE *fp)
{
	// ASSERT/PRE
	if (fp==NULL)
		return NULL;
	if (feof(fp))
		return NULL;

	int index = 0;
	char *buf = Salloc(MAX_SHORT_TXT + 1); // ALLOC buf

	// BODY
	while (!feof(fp) && (index < MAX_SHORT_TXT))
	{
		char c = fgetc(fp);
		if (c < 0)
		{
			//empty line
			if (index == 0)
			{
				Sfree(buf); // FREE buf
				return NULL;
			}
			break; //EOF !
		}

		int eol = 0;
		switch (c)
		{
			case 0:
				eol = 1;
				break;
			case 0x0d:
				c = fgetc(fp); // Windows: parse 0x0d x0a;
				if (c != 0x0a)
				{
					char *substr=Ssubstring(buf, 0, index); // ALLOC substr
					error("Missing newline after:%s", Ssubstring(buf, 0, index));
					Sfree(substr); // FREE substr
				}
				eol = 1;
				break;
			case 0x0a: // Unix: newline or '\n'
				eol = 1;
				break;
		}
		if (eol == 1)
			break;

		// append & continue
		buf[index++] = c;
	}

	// POST

	buf[index] = 0;
	char *line=Sduplicate(buf);
	Sfree(buf); // FREE buf
	return line;
}

/**
 * Read complete contents and return as (ASCI) string (char*).
 * Return contents as (ASCI) string (char*).
 * This method is for small ASCI files and is not optimized.
 */
char *Fread_text(const char *filepath)
{
	return Fread_text(filepath,-1);
}

/**
 * Read contents up to a maximum of maxLength.
 * Return contents as (ASCI) string (char*).
 * This method is for small ASCI files and is not optimized.
 */
char *Fread_text(const char *filepath, int maxLength)
{
	off_t index = 0;
	FILE *fp = NULL;
	char *buffer = NULL;

	// open file
	fp = fopen(filepath, "r");
	if (!fp)
		return NULL;

	off_t size=Flength(fp);

	if ((maxLength>0) && (size>maxLength))
	{
		errorPrintf("File to big (%li >= %li bytes). Truncating file:%s\n",size,(long)MAX_BIG_TXT,filepath);
		size=maxLength;
	}

	// one buffer:
	buffer = Salloc(size + 1); // ALLOC buffer: add 1 for zero termination character

	while (!feof(fp) && (index < size))
	{
		// Parse Characters
		char c = fgetc(fp);

		// NUL CHAR or EOF (nul char ends string!)
		// also prevent reading large binary files by accident.
		if (c <= 0)
			break;

		buffer[index] = c;

		if (ferror(fp))
		{
			fprintf(stderr, "*** Error reading source file:%s\n", filepath);
			Sfree(buffer); // FREE buffer
			return NULL;
		}

		index++;
	} // while not eof/end of buffer.

	// end string:
	buffer[index] = 0;

	if (index==size)
	{
		return buffer;
	}
	else
	{
		// create copy with exact size freeing rest of memory.
		char * newStr=Sduplicate(buffer);
		Sfree(buffer); // FREE buffer;
		return newStr;
	}
}

/**
 * File length.
 * If the value <0 the the file can't be found.
 * @param: const char *file - path of file .
 * @returns: off_t (64 bits) file length;
 */
file_size_t Flength(const char *file)
{
	FILE *fp=fopen(file,"r");

	if (fp==NULL)
		return -1; // file not found !

	file_size_t size=Flength(fp);
	fclose(fp);
	return size;
}

/**
 * File length.
 * If the value <0 the the file can't be found.
 * @param: FILE *fp file pointer
 * @returns: off_t (64 bits) file length;
 */
file_size_t Flength(FILE *fp)
{
	if (fp==NULL)
		return -1;

	int fd = fileno(fp); //if you have a stream (e.g. from fopen), not a file descriptor.
	struct stat buf;
	fstat(fd, &buf);
	file_size_t size = buf.st_size;

	return size;
}

/**
 * Write text (char*) to file.
 */
int Fwrite(const char *file,const char *text)
{
	return Fwrite(file,(void *)text,1,Slength(text));
}

/**
 * Write memory (bytes) to file.
 */
int Fwrite(const char *file,const void *ptr,size_t numBytes)
{
	return Fwrite(file,ptr,1,numBytes);
}

/**
 * Write memory to file.
 * Size of memory written to file = elementSize*numElements.
 */
int Fwrite(const char *file,const void *ptr,size_t elementSize,size_t numElements)
{
	if (file==NULL)
		return -1;

	if (ptr==NULL)
		return -1;

	FILE *fp=fopen(file,"w");
	if (fp==NULL)
		return -1;// file open error !

	size_t result=fwrite(ptr,elementSize,numElements,fp);
	fclose(fp);

	return result;
}






