#
# @(#)Makefile 95/03/05 1.7
#
# Makefile for dynamically loaded native method example.
#
# NOTE: Change JAVAHOME to indicate the top level of your JAVA top
#	level directory before building
#
# version 	1.0, 01 Dec 1994
# author	Eugene Kuerner
# 

all		: library applet

JAVAC		= javac
JAVAH		= javah
#
# Default JAVAHOME
#
DEFAULT_JAVAHOME=../..
JAVAHOME=$(DEFAULT_JAVAHOME$(ALT_JAVAHOME))$(ALT_JAVAHOME)
JAVASHAREINC	= $(JAVAHOME)/include
JAVASOLARISINC	= $(JAVAHOME)/include/solaris
JAVASOLARISBLD	= $(JAVAHOME)/include/CClassHeaders
LIBRARY		= libfile
CLASSSTUBDIR	= stubs
CHEADERDIR	= CClassHeaders
PACKAGEDIR	= demo
STUBPREAMBLE	= $(JAVASHAREINC)/StubPreamble.h

CC		= cc
LD		= /usr/ccs/bin/ld
INCLUDES	= -I. -I$(CHEADERDIR) -I$(JAVASHAREINC) -I$(JAVASOLARISINC) \
		  -I$(JAVASOLARISBLD)
CFLAGS		= $(INCLUDES) -c 

FILES.c		= libstubs.c		\
		  file.c

FILES.o		= libstubs.o		\
		  file.o

FILES.java	= File.java		\
		  InputFile.java		\
		  OutputFile.java

FILES.class	= File.class		\
		  InputFile.class	\
		  OutputFile.class

EXPORTEDCLASSES	= $(PACKAGEDIR).InputFile 		\
		  $(PACKAGEDIR).OutputFile


%.class		: $(PACKAGEDIR)/%.java
		  $(JAVAC) $<

%.o		: %.c
		  $(CC) $(CFLAGS) $<


library		: classheaders $(FILES.o)
		  $(LD) -G -o $(LIBRARY).so $(FILES.o)

applet		: library Replace.java
		  $(JAVAC) Replace.java


classheaders	: $(FILES.class)
		  for i in $(EXPORTEDCLASSES) ;					\
		  do							\
		      $(JAVAH) -classpath $(CLASSPATH) -d $(CHEADERDIR)/ $$i ;\
		  done

libstubs.c	: classheaders $(STUBPREAMBLE)
		  $(JAVAH) -classpath $(CLASSPATH) -d $(CLASSSTUBDIR)/	\
		      -stubs $(EXPORTEDCLASSES)
		  @ cat $(STUBPREAMBLE) $(CLASSSTUBDIR)/*.stubs > $@

clobber		: 
		  rm -rf $(CHEADERDIR) $(CLASSSTUBDIR) *.so *.class *.o \
		  $(PACKAGEDIR)/*.class libstubs.c
