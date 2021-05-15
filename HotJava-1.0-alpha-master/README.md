# HotJava 1.0 alpha
This repository contains the files of HotJava 1.0 alpha, the predecessor of 
the Java programming language.

Contained are:
- [hj-alpha2](/hj-alpha2) ([HTML](https://marcono1234.github.io/HotJava-1.0-alpha/hj-alpha2/hotjava/index.html)): 
HotJava 1.0a2 for SunOS
- [hj-alpha3](/hj-alpha3) ([HTML](https://marcono1234.github.io/HotJava-1.0-alpha/hj-alpha3/hotjava/index.html)): 
HotJava 1.0a3 for SunOS
- [hotjava-alpha2-nt-x86](/hotjava-alpha2-nt-x86) ([HTML](https://marcono1234.github.io/HotJava-1.0-alpha/hotjava-alpha2-nt-x86/hotjava/index.html)): 
HotJava 1.0a2 for Windows NT

[*Source*][hotjava-source]

# History & background
In 1991 a small team of Sun Microsystems employees, known as the "Green Team", 
was chartered to plan for the "next wave" in computing. By the summer of 1992 
they had created a working demo of an interactive, handheld home-entertainment 
device controller. The programming language for this device was written by 
James Gosling and had the name "Oak". However, the team was unable to convince 
the TV set-top box and video-on-demand industry with their new device. But they 
noticed that due to the capabilities of the programming language, it was also 
well suited for the Internet which greatly grew in popularity at that time. 
Therefore they created a webbrowser based on their new programming language 
similar to the then popular Mosaic browser and called it "WebRunner" (later 
named "HotJava"). The first publicly released alpha version was HotJava 1.0a2.

[*Source*](https://web.archive.org/web/19990223195009/http://java.sun.com/features/1998/05/birthday.html)

----

Based on this information and the [changelog of 1.0a2](https://marcono1234.github.io/HotJava-1.0-alpha/hj-alpha2/hotjava/doc/changes/changes.html#Programmer), 
1.0a2 is the first version after the "Oak" → "Java" name change.

1.0a2 and 1.0a3 appear to be the only Java versions which produce class files of 
version major 45, minor 2. These class files are incompatible with any newer class 
files, starting with major 45, minor 3 (version 1.0b1?); read more [here](https://github.com/ItzSomebody/StopDecompilingMyJava/blob/master/decompiler-tool-bugs/Entry-011/Entry.md) 
and see the [VM spec](https://marcono1234.github.io/HotJava-1.0-alpha/hj-alpha2/hotjava/doc/vmspec/vmspec_39.html#HEADING243).

Back then the meaning of the field access flag `0x40` was reversed, instead of 
being "volatile" it was "threadsafe" (= can cache in registers), see [VM spec](https://marcono1234.github.io/HotJava-1.0-alpha/hj-alpha2/hotjava/doc/vmspec/vmspec_38.html#HEADING212).

The HotJava specific classes (networking, applets, browser interaction) 
were not in the `java` package back then, but had their own top level 
packages, see [package index](https://marcono1234.github.io/HotJava-1.0-alpha/hj-alpha2/hotjava/doc/api/packages.html).

The files in this repository come from [here][hotjava-source]. 
There are for sure more interesting files there, though sadly not all have been 
archived. Other old Java files can be found [here](https://web.archive.org/web/*/http://www.mcp.com/que/javarc/se_java/*). 
Also quite interesting is [this mention](https://groups.google.com/d/topic/comp.lang.misc/bmo0GZ5r_eY/discussion) 
of Oak / Java where the former Sun employee Jonathan Payne, who worked on Java, 
responded.

# HotJava issues
The HotJava versions are provided in the state in which they were archived<sup>[1](#extraction-note)</sup>.
It is (at least currently) not the goal of this repository to fix issues in 
the HotJava source code or documentation.

<a name="extraction-note">1</a>: Extracting the archives and storing their 
content with Git might have introduced some changes.

# License
These files are published based on the license included in the HotJava releases 
and copied as [License.html](https://marcono1234.github.io/HotJava-1.0-alpha/License.html), 
which states (excerpt):
>Sun grants to you ("Licensee") a non-exclusive license to use the 
Software for academic, research and internal business purposes only, 
without fee. Licensee may distribute the binary and source code 
to third parties provided that the copyright notice and this statement 
appears on all copies and that no charge is associated with such 
copies. Licensee agrees that the copyright notice and this 
statement will appear on all copies of the Software, or portions 
thereof. Sun retains exclusive ownership of the software.

# Disclaimer
This repository is neither officially affiliated with the Java programming 
language nor with Oracle or the OpenJDK team.


[hotjava-source]: https://web.archive.org/web/19961225173659/http://sunsite.unc.edu:80/pub/sun-info/hotjava/