/*
 * Simple C(PP) utils. 
 * Since this code is cross compiled using mingw32, not much libraries can be used. 
 */ 

#include <windows.h>
#include <string.h>
#include <stdio.h>
#include "util.hpp"
#include "debug.hpp" 

#define MAX_TXT 1000
#define CHAR_CR 0x0a 
#define CHAR_LF 0x0d 

#define ASSERTNOTNULL(ptr,text) {if (ptr==NULL) { fprintf(stderr,"*** Null Pointer:%s\n",text); exit(-1); } }  

void error(const char *format,const char *arg) //HPP 
{
   fprintf(stderr,format,arg); 
}

unsigned int Slength(const char *str) //HPP
{
 if (str==NULL)
    return 0; 
 return strlen(str);
}

char *Salloc(int size) //HPP
{
 if (size<0) 
    return NULL; 
 return (char*)malloc(size); 
}

void Sfree(char *str) //HPP
{
 free(str); 
}

int Scompare(const char*s1,const char *s2) //HPP
{
 if (s1==NULL)
    if (s2==NULL) 
       return 0; 
     else
        return -1; // (s1==NULL) < (s2!=NULL) 
 else
     if (s2==NULL) 
        return 1; // (s1!=NULL) > (s2==NULL) 
     else
        return strcmp(s1,s2); // save compare  
}

char *Sduplicate(const char *str) //HPP
{
  if (str==NULL)
    return NULL;
    
  return strdup(str); 
}

int Ssplit(const char *source,const char sepC, char **left,char**right) //HPP
{
 if (source==NULL) 
     return -1; 

 ASSERTNOTNULL(left,"in Ssplit(), char **left may not be NULL"); 
 ASSERTNOTNULL(right,"in Ssplit(), char **right may not be NULL"); 

 int len=Slength(source); 

 for (int i=0;i<len;i++) 
 {
    if (source[i]==sepC)
    {
	    *left=Ssubstring(source,0,i); 
        *right=Ssubstring(source,i+1,len); 
        return i; 
    }
 }

 // not split char: 
 *left=Sduplicate(source); 
 *right=NULL;
  
 return len;  
}

char *Ssubstring(const char *str,int begin,int end)  //HPP
{
 if (str==NULL)
    return NULL;

 int len=end-begin; 
 char *newstr=Salloc(len+1); // +1 ending zero!  

 for (int i=0; i<len;i++)   
    newstr[i]=str[begin+i]; 

 newstr[len]=0; 
  
 return newstr;  
}

char *Sappend(const char *s1,const char *s2) //HPP
{
 if (s1==NULL) 
    if (s2==NULL) 
       return NULL; 
    else
       return Sduplicate(s2);  // value method: must return duplicate!
 else
    if (s2==NULL) 
       return Sduplicate(s1); // value method: duplicate!
    else 
       ; // continue 

 int len1=Slength(s1); 
 int len2=Slength(s1); 
 char *newstr=Salloc(len1+len2+1); 
 newstr[0]=0; 
 strcat(newstr,s1); 
 strcat(newstr,s2);   
  
 return newstr; 
}

int existsfile(char *filepath) //HPP
{
 FILE *fp = fopen(filepath,"r");
 if (fp) 
 {
    return 1; 
    fclose(fp);
 }
 else 
 {
    return 0;
 }
}

char *readline(FILE *fp) //HPP
{
 char *buf=Salloc(MAX_TXT+1);   
 int index=0; 

 if (feof(fp)) 
    return NULL; 
  
 while(!feof(fp) && (index<MAX_TXT))
 {	
    char c=fgetc(fp);
    if (c<0) 
	{    
       //empty line  
       if (index==0) 
           return NULL; 
   
       break; //EOF ! 
    }

    int eol=0; 
    switch(c)
    { 
          case 0: 
             eol=1; 
             break;
          case 0x0d:
             c=fgetc(fp); // Windows: parse 0x0d x0a; 
             if (c!=0x0a) 
                 error("Missing newline after:%s",Ssubstring(buf,0,index)); // memory leak  
             eol=1; 
             break; 
          case 0x0a: // Unix: newline or '\n'  
             eol=1; 
             break; 
    }
    if (eol==1) 
        break; 

        // append & continue
   	buf[index++]=c; 
 }

 buf[index]=0;  
 return Sduplicate(buf); 
}

char *readfile(const char *filepath) //HPP
{
 int index=0; 
 FILE *fp = NULL;
 char *txt=NULL; 

 // open file 
 fp=fopen(filepath,"r");
 if (!fp)
     return NULL; 

 txt=(char*)malloc(MAX_TXT+1);  // add 1 for zero termination character
 
 while(!feof(fp) && (index<MAX_TXT))
 {
     // readline 
     char c=fgetc(fp); 
     if (c<=0) 
	    break; 

     txt[index++]=c; 
       
     if(ferror(fp)) 
     {
        fprintf(stderr,"*** Error reading source file:%s\n",filepath);
        return NULL;
     }
 }

 txt[index]=0; 
 return txt; 
}

// Strip quotes environment variables which are allowed in WineDos 
char *SstripQuotes(char *str) //HPP
{
   if (str==NULL)
    	return NULL;

   int len=0;

   if ((len=Slength(str))<=1)
     return str;

   // Check double quotes: 
   if ((str[0]=='"') && (str[len-1]=='"') ) 
   {
	return Ssubstring(str,1,len-1); 
   }

   if ((str[0]=='\'') && (str[len-1]=='\'') ) 
   {
	return Ssubstring(str,1,len-1); 
   }

   return str; 
}

