/*
 * Windosh JarLauncher. 
 *
 * <pre> 
 * (c) Piter.NL 2004-2010
 * (C) VL-e  2006-2010
 * Distribution Prohibited. 
 * </pre>
 *
 * @Author:  Piter T. de Boer, redistributed for the VL-e project. 
 */ 

#include <windows.h>
#include <string.h>
#include <stdio.h>
#include "ptc/ptcutil.hpp"
#include "debug.hpp" 

#define VMARGS_PROP   "vmargs"
#define JAVAEXE_PROP  "java.exe"
#define JAVA_HOME     "JAVA_HOME"
#define JAVAHOME_PROP "java.home"
#define JAVAW_EXE     "javaw.exe"

#define MAX_TXT 1313

/**
 * Enhanced Java Bootstrap Loader: 
 * - Takes basename of executable as "main" and starts ${main}.jar 
 * - optionally uses ${main}.ini as config file. 
 *   For example vbrowser.exe will start vbrowser.jar and optionally use vbrowser.ini 
 * - JAVA_HOME specifies the java location. Default is to the one from the 
 *   path.  
 */ 

char *vmargs=NULL; 
char *java_home=NULL; 
char *java_exe=NULL; 
char *main_ini=NULL;
char *main_jar=NULL;

// Read ${MAIN}.ini file and parse <prop>=<value>  lines. 

int readini(char *filename)
{
   FILE *fp=fopen(filename,"r"); 
   if (!fp) 
   {
      return 0;
   }

   char *line=NULL;
   debug("Reading ini file: %s\n",filename); 

   do
   {
      line=Freadline(fp);
      if (line==NULL) 
         break; //EOF 

      if (line[0]=='#')
         continue; //comments 

      if (line[0]=='[') 
         continue; // section 

      debug(" - parsing line: '%s'\n",line); 
      char *name=NULL; 
      char *value=NULL; 
      int pos=Ssplit(line,'=',&name,&value); 

      if (pos>0)
      {
         if (Scompare(name,VMARGS_PROP)==0) 
         {
             vmargs=value; 
             debug(" + Found: vmargs=%s\n",vmargs); 
         }
         else if (Scompare(name,JAVAEXE_PROP)==0) 
         {
             java_exe=value; 
             debug(" + Found: javaexe=%s\n",vmargs); 
         }
         else if (Scompare(name,JAVAHOME_PROP)==0) 
         {
             java_home=value; 
             debug(" + Found: jave.home=%s\n",vmargs); 
         }

      }

   } while(line!=NULL); 

   fclose(fp); 
}

const char *nonullstring(const char *string, const char *defaultString)
{
	if (string!=NULL)
			return string;

	if (defaultString!=NULL)
			return defaultString;

	return "<NULL>";
}
// ===
// Main 
// ===

int WINAPI WinMain(HINSTANCE hInstance,
			HINSTANCE hPrevInstance,
            LPSTR lpCmdLine,
            int nCmdShow)
{
 char jcmd[MAX_PATH+256];
 char exepath[MAX_PATH+256];
 char *dirname=NULL;
 char *basename=NULL; 

 //char *jcmd="javaw" 

 // *** 
 // Check Startup Environment 
 // *** 


 // get path of this executable  
 GetModuleFileName(hInstance, exepath, MAX_PATH);

 // strip last part of exepath (dirname); 
 char *sepc = strrchr(exepath, '\\');
 basename=strdup(sepc+1); 
 sepc[0] = '\0';
 dirname=strdup(exepath); 

 debug("dirname=%s\n",dirname); 
 debug("full basename=%s\n",basename); 

 // remove .exe: truncate 
 if (strlen(basename)>4)   
     basename[strlen(basename)-4]=0; 

 // truncate exepath! 

 main_jar=Sappend(basename,".jar"); 
 main_ini=Sappend(basename,".ini"); 
  
 // exepath now points to basedir of installation ! 
 // cd to basedir of installation. 
  
 SetCurrentDirectory(exepath);

 // ================
 // Read ${MAIN}.ini 
 // ================
 readini(main_ini); 

 // Defaults if not specified in config file
 if (java_home==NULL) 
    java_home=getenv(JAVA_HOME);
    
 // Environment Variable can have quotes: 
 java_home=SstripQuotes(java_home); 
 
 debug(" getenv(JAVA_HOME=%s\n",nonullstring(java_home,"<undefined>"));
 
 if (java_exe==NULL) 
    java_exe=Sduplicate(JAVAW_EXE);

 // ***
 // Create start command: 
 // *** 

 char *javaws_path=NULL; 

 // TODO: if $VLET_INSTALL/jre1.6/ exists use $VLET_INSTALL/jre1.6/
  
 if (java_home!=NULL) 
 {
      sprintf(jcmd,"%s\\bin\\%s ",java_home,java_exe); 
 }
 else
 {
      // No JAVA_HOME: use javaw only: 
      sprintf(jcmd,"%s ",java_exe); 
 }

 // keep copy of path 
 javaws_path=strdup(jcmd); 

 // Add optional VMArgs: 
 if (vmargs!=NULL)
 {
     strcat(jcmd, vmargs); 
     strcat(jcmd, " ");  // add space:
 }

 // add -jar command 
 {
     // start with hardcoded: -jar <MAINJAR> 
     strcat(jcmd,"-jar "); 
     strcat(jcmd, main_jar); 
     strcat(jcmd, " ");
 } 

 // append other command line options: 
 if ((lpCmdLine!=NULL) && (lpCmdLine[0]!=0))
 {
     strcat(jcmd, lpCmdLine);
 }

 fprintf(stderr," Jarlauncher cwd = %s\n",nonullstring(exepath,"<undefined>"));
 fprintf(stderr," Jarlauncher jar = %s\n",nonullstring(main_jar,"<undefined>"));
 fprintf(stderr," Jarlauncher ini = %s\n",nonullstring(main_ini,"<undefined>"));
 fprintf(stderr," JAVA_HOME       = %s\n",nonullstring(java_home,"<undefined>"));
 fprintf(stderr," vmarguments     = %s\n",nonullstring(vmargs,"")); // empty is allowed
 fprintf(stderr," arguments       = %s\n",nonullstring(lpCmdLine,""));
 fprintf(stderr," commandline     = %s\n",jcmd); // actual command
 fprintf(stderr,"\n"); 
 
 if (Scompare(lpCmdLine,"-info")==0)
 {
     // exit; 
     return 0; 
 }

 /* Check javaw.exe, but only if a hardcoded JAVA_HOME is given */ 
 if ((java_home!=NULL) && (Fexists(javaws_path)==0))
 {
     char *message=Sappend(
    		 "*** Jarlauncher Exception ***\n",
    		 "javaw.exe not found:",
    		 javaws_path);
     fprintf(stderr,"\n*** Jarlauncher javaw not found:%s***\n",javaws_path);
     MessageBox(NULL,message,"File Error", MB_OK);
     return 1;
 }

 if (Fexists(main_jar)==0)
 {  
     char *message=Sappend(
    		 "*** Jarlauncher Exception ***\n",
    		 "Jarfile doesn't exist or is unreadable:",
    		 main_jar);
     //strcat(message,"."); 
     fprintf(stderr,"\n*** Jarlauncher file open error:%s***\n",main_jar);
     MessageBox(NULL,message,"File Error", MB_OK);
     return 2;
 }

 // ***
 // exec
 // ***

 WinExec(jcmd, SW_SHOW);
 return 0;
}

