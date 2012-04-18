/*
 * Win/Dos JarLauncher.
 *
 * <pre> 
 * (c) Piter.NL 2004-2012
 * </pre>
 *
 * @Author:  Piter T. de Boer
 */ 

#include <windows.h>
#include <string.h>
#include <stdio.h>
#include "ptc/ptcutil.hpp"
#include "debug.hpp" 

#define JAVA_HOME         "JAVA_HOME"

// ============================================
// vbrowser.ini properties:
// ============================================

#define JAVA_VMARGS_PROP   "java.vmargs"
#define JAVA_EXE_PROP      "java.exe"
#define JAVA_HOME_PROP     "java.home"
// actual jarfile to start:
#define JAVA_MAINJAR_PROP "java.mainjar"

// Java commands:
#define JAVAW_EXE   "javaw.exe"

#define MAX_TXT 1313

/**
 * Enhanced Java Bootstrap Loader: 
 * - Takes basename of executable as "main" and starts ${main}.jar 
 * - optionally uses ${main}.ini as config file. 
 *   For example vbrowser.exe will start vbrowser.jar and optionally use vbrowser.ini 
 * - JAVA_HOME specifies the java location. Default is to the one from the 
 *   path.  
 */ 

char *java_vmargs=NULL;
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
   // ====================
   // Read "vbrowser.ini"
   // ====================

   char *line=NULL;
   DEBUGPRINTF("Reading ini file: %s\n",filename);

   do
   {
      line=Freadline(fp);
      if (line==NULL) 
         break; //EOF 

      if (line[0]=='#')
         continue; //comments 

      if (line[0]=='[') 
         continue; // section 

      DEBUGPRINTF(" - parsing line: '%s'\n",line);
      char *name=NULL; 
      char *value=NULL; 
      int pos=Ssplit(line,'=',&name,&value); 

      if (pos>0)
      {
         if (Scompare(name,JAVA_VMARGS_PROP)==0)
         {
             java_vmargs=value;
             DEBUGPRINTF(" + found: java.vmargs=%s\n",java_vmargs);
         }
         else if (Scompare(name,JAVA_EXE_PROP)==0)
         {
             java_exe=value; 
             DEBUGPRINTF(" + found: java.exe=%s\n",java_vmargs);
         }
         else if (Scompare(name,JAVA_HOME_PROP)==0)
         {
             java_home=value; 
             DEBUGPRINTF(" + found: java.home=%s\n",java_vmargs);
         }
         else if (Scompare(name,JAVA_MAINJAR_PROP)==0)
         {
        	 main_jar=value;
             DEBUGPRINTF(" + found: java.mainjar=%s\n",java_vmargs);
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

 DEBUGPRINTF("dirname=%s\n",dirname);
 DEBUGPRINTF("full basename=%s\n",basename);

 // remove .exe: truncate 
 if (strlen(basename)>4)   
     basename[strlen(basename)-4]=0; 

 // truncate exepath! 

 main_jar=Sappend(basename,".jar"); // default mainjar to exectuable name!
 main_ini=Sappend(basename,".ini"); 
  
 // exepath now points to basedir of installation ! 
 // cd to basedir of installation. 
  
 SetCurrentDirectory(exepath);

 // ================
 // Read ${MAIN}.ini 
 // ================
 // might change default settings!
 readini(main_ini); 

 // Defaults if not specified in config file
 if (java_home==NULL) 
    java_home=getenv(JAVA_HOME);
    
 // Environment Variable can have quotes: 
 java_home=SstripQuotes(java_home); 
 
 DEBUGPRINTF(" getenv(JAVA_HOME=%s\n",nonullstring(java_home,"<undefined>"));
 
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
 if (java_vmargs!=NULL)
 {
     strcat(jcmd, java_vmargs);
     strcat(jcmd, " ");  // add space:
 }

 // add -jar command 
 {
     // start with -jar <MAINJAR>
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
 fprintf(stderr," vmarguments     = %s\n",nonullstring(java_vmargs,"")); // empty is allowed
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
     fprintf(stderr,"\n*** Jarlauncher: javaw not found:%s***\n",javaws_path);
     MessageBox(NULL,message,"Java Error", MB_OK);
     return 101;
 }

 if (Fexists(main_jar)==0)
 {  
     char *message=Sappend(
    		 "*** Jarlauncher Exception ***\n",
    		 "Jarfile doesn't exist or is unreadable:",
    		 main_jar);
     //strcat(message,"."); 
     fprintf(stderr,"\n*** Jarlauncher: file open error:%s***\n",main_jar);
     MessageBox(NULL,message,"File Error", MB_OK);
     return 102;
 }

 // ***
 // exec
 // ***

 unsigned int result=WinExec(jcmd, SW_SHOW);
 
 if (result>31)
 {
	 DEBUGPRINTF(" Java started...\n");
 }
 else
 {
	 fprintf(stderr,"return code=%i\n",result);

	 if (result==2)
	 {
		 char *message=Sappend(
	    		 "*** Jarlauncher Exception ***\n",
	    		 "Java couldn't be started or Java isn't properly installed.");
	     //strcat(message,".");
	     fprintf(stderr,"\n*** Jarlauncher: Java couldn't be started:%s***\n",jcmd);
	     MessageBox(NULL,message,"Java Error", MB_OK);
	 }
	 else
	 {
		 char *message=Sappend(
	    		 "*** Jarlauncher Exception ***\n",
	    		 "Unknown error occured.");
	     //strcat(message,".");
	     fprintf(stderr,"\n*** Jarlauncher: Unknown error:%i***\n",result);
	     MessageBox(NULL,message,"Java Error", MB_OK);
	 }

	 return result;
 }

 return 0;
}

