#
# JDK checks.
#

#
# VLE_CHECK_JDK([MINIMAL VERSION], [IF-FOUND-PART], [IF-NOT-FOUND-PART])
#

AC_DEFUN([VLE_CHECK_JDK],
[
  JAVA_REQUIRED_VER="$1"

  AC_ARG_WITH(jdk,
              AC_HELP_STRING([--with-jdk=PATH],
                             [Try to use 'PATH' as the home of the Java SDK.
                              If PATH is not specified, look 
                              for a Java Development Kit at JAVA_HOME.]),
  [
    case "$withval" in
      "no")
        JDK_SUITABLE=no
      ;;
      "yes")
        VLE_FIND_JDK([check], [$JAVA_REQUIRED_VER])
      ;;
      *)
        VLE_FIND_JDK([$withval], [$JAVA_REQUIRED_VER])
      ;;
    esac
  ],
  [
    VLE_FIND_JDK([check], [$JAVA_REQUIRED_VER])
  ])
])



AC_DEFUN([VLE_FIND_JDK],
[
  where=$1
  JAVA_REQUIRED_VER="$2"

  JDK=none
  JAVA_BIN=none
  JAVAC=none
  JAVAH=none
  JAR=none

  JDK_SUITABLE=no
  AC_MSG_CHECKING([for JDK $JAVA_REQUIRED_VER])
  if test $where = check; then      
      if test -n $JAVA_HOME && test -x "$JAVA_HOME/bin/javac"; then
	  JDK="$JAVA_HOME"
      elif test -x "/Library/Java/Home/bin/javac"; then
	  JDK="/Library/Java/Home"
      elif test -x "/usr/bin/javac"; then
	  JDK="/usr"
      elif test -x "/usr/local/bin/javac"; then
	  JDK="/usr/local"
      else
	  JDK=""
      fi
  else
      JDK=$where
      if test -x "$JDK/bin/javac" ; then :; else
	  JDK=""
      fi
  fi

  # Correct for Darwin's odd JVM layout.  Ideally, we should use realpath,
  # but Darwin doesn't have that utility.  /usr/bin/java is a symlink into
  # /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Commands
  # See http://developer.apple.com/qa/qa2001/qa1170.html
  os_arch="`uname`"
  if test "$os_arch" = "Darwin" && test "$JDK" = "/usr" &&
     test -d "/Library/Java/Home"; then
      JDK="/Library/Java/Home"
  fi
  if test "$os_arch" = "Darwin" && test "$JDK" = "/Library/Java/Home"; then
      JRE_LIB_DIR="/System/Library/Frameworks/JavaVM.framework/Classes"
  else
      JRE_LIB_DIR="$JDK/jre/lib"
  fi

  JAVA_BIN="$JDK/bin"
  JAVA="$JAVA_BIN/java"
  JAVAC="$JAVA_BIN/javac"
  JAVAH="$JAVA_BIN/javah"
  JAR="$JAVA_BIN/jar"

  # Test if JDK is something sensible now
  no_jdk=
  no_java=
  test -z "$JDK" && no_jdk=1
  test -n "$JDK" && test -x "$JAVA" || no_java=1
  
  if test x"$no_jdk" = x1 || test x"$no_java" = x1; then
      AC_MSG_RESULT([no])
      AC_MSG_ERROR([cannot find a valid Java SDK; either set JAVA_HOME or
	      use --with-jdk=PATH.],[-1])
  fi

  # Test if JAVAC is an executable program

  if test -x "$JAVAC"; then :; else
      AC_MSG_RESULT([no])
      AC_MSG_ERROR([cannot find a Java Compiler in $JAVA_BIN; 
	      this software requires a full Java SDK, minimum version
	      $JAVA_REQUIRED_VER, 
	      which can be obtained from http://java.sun.com/.],[-1])
  fi

  # Test that the JDK is at least the required version.
  java_version=`"$JAVA" -version 2>&1 | grep "java version" | \
                sed -e 's/.*java version "\(.*\)".*/\1/'`
  if test $? -ne 0 || test -z "$java_version"; then
      AC_MSG_RESULT([no])
      AC_MSG_ERROR([cannot retrieve version information from "$JAVAC";
	      this software requires a full Java SDK, minimum version
	      $JAVA_REQUIRED_VER, 
	      which can be obtained from http://java.sun.com/.],[-1])	
  else
      goodversion=`expr $java_version ">=" $JAVA_REQUIRED_VER`
      if test $goodversion -eq 1; then
	  JDK_SUITABLE=yes
      fi
  fi

  AC_MSG_RESULT([$JDK_SUITABLE])

  if test "$JDK_SUITABLE" = "yes"; then
    dnl Add javac flags.
    # The release for "-source" could actually be greater than that
    # of "-target", if we want to cross-compile for lesser JVMs.
    JAVAC_FLAGS="-target $JAVA_REQUIRED_VER -source $JAVA_REQUIRED_VER"
    if test "$enable_debugging" = "yes"; then
      JAVAC_FLAGS="-g $JAVAC_FLAGS"
    fi
  else
      AC_MSG_ERROR([the Java SDK version is $java_version, but version
	      $JAVA_REQUIRED_VER is required. This can be obtained from 
	      http://java.sun.com/.],[-1])	
  fi


  AC_MSG_NOTICE([found Java SDK version $java_version.])
  JAVA_HOME="$JDK"
  AC_SUBST(JDK)
  AC_SUBST(JAVA)
  AC_SUBST(JAVA_HOME)
  AC_SUBST(JAVAC)
  AC_SUBST(JAVAC_FLAGS)
  AC_SUBST(JAVAH)
  AC_SUBST(JAR)
])


AC_DEFUN([VLET_ENABLE_USERGUIDE],
[
  AC_PROG_LATEX

  AC_ARG_ENABLE(userguide,
              AC_HELP_STRING([--disable-userguide],
                             [Disable building the UserGuide (default is enabled)]
                            ),
   [
    case "$enableval" in
      "no")
        build_doc_userguide=no
        AC_MSG_RESULT([vlet: --disable-userguide -> disabling userguide.]) 
      ;;
      "yes")
        build_doc_userguide=yes;
        AC_MSG_RESULT([vlet: --enable-userguide -> enabling userguide.]) 
      ;;
      *)
      AC_MSG_ERROR([vlet: --enable-userguide=... option not recognized: $enableval])
      ;;
    esac
   ],
   [
  	build_doc_userguide=yes;
	AC_MSG_RESULT([vlet: enabling userguide (disable with --disable-userguide).])
   ]
  )
  
  # 
  case "$build_doc_userguide" in 
     "yes")
	    case "$latex" in
		   "latex")
		       AC_MSG_RESULT([vlet: LaTeX found. UserGuide can be build. ] ) 
			   ;;    
		    "no") 
		       build_doc_userguide=no
		       AC_MSG_ERROR([vlet: LaTeX NOT found: Please disable building of userguide with --disable-userguide. ] )
		       ;; 
		esac
	   ;;
  esac

  AC_SUBST(build_doc_userguide)
   
])

AC_DEFUN([VLET_ENABLE_JYTHON],
[
  AC_ARG_ENABLE(jython,
              AC_HELP_STRING([--disable-jython],
                             [Disable jython (default is enabled)]
                            ),
  [
    case "$enableval" in
      "no")
        build_module_jython=no
        AC_MSG_RESULT([vlet: disabling jython.]) 
      ;;
      "yes")
        build_module_jython=yes;
        AC_MSG_RESULT([vlet: enabling jython.]) 
         
      ;;
      *)
      AC_MSG_ERROR([--enable-jython option not recognized: $enableval])
      ;;
    esac
  ],
  [
  	build_module_jython=yes;
	AC_MSG_RESULT([vlet: enabling jython (default, disable with --disable-jython)])
  ]
  )
   AC_SUBST(build_module_jython) 
])

#
#   Copyright (C) 2004  Boretti Mathieu
#
#   This program is free software; you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation; either version 2 of the License, or
#   (at your option) any later version.
#
#   This program is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.
#
#   You should have received a copy of the GNU General Public License
#   along with this program; if not, write to the Free Software
#   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
#
# AC_PROG_LATEX
#
# Test for latex or elatax or lambda
# and set $latex to the correct value.
#
#
dnl @synopsis AC_PROG_LATEX
dnl
dnl This macro test if latex is installed. If latex
dnl is installed, it set $latex to the right value
dnl
dnl @version 1.3
dnl @author Mathieu Boretti boretti@eig.unige.ch
dnl
AC_DEFUN([AC_PROG_LATEX],[
AC_CHECK_PROGS(latex,[latex elatex lambda],no)
export latex;
if test $latex = "no" ;
then
	AC_MSG_WARN([Unable to find a LaTeX application]);
fi
AC_SUBST(latex)
])

AC_DEFUN([CHECK_GLOBUS],
[
  AC_MSG_CHECKING([for Globus])
  if test -n "$GLOBUS_LOCATION" && test -d "$GLOBUS_LOCATION" ; then 
      globus_location="$GLOBUS_LOCATION"
      AC_MSG_RESULT([using GLOBUS_LOCATION="$GLOBUS_LOCATION" ])
  elif test -d "/opt/globus" ; then 
      globus_location=/opt/globus 
      AC_MSG_RESULT([using /opt/globus ])
  else 
      AC_MSG_RESULT([no])
      AC_MSG_WARN([Couldn't find globus, will not be able to build grid services])
  fi

  AC_SUBST(globus_location)
])

AC_DEFUN([VLET_ENABLE_GRID_SERVICES],
[
 CHECK_GLOBUS 
])
