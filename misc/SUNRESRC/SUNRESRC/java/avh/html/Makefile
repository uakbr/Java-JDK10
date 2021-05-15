
all: classes test

classes:
	javac -d . html/*.java

test: test.dtd test.html

test.dtd:
	@echo
	@echo Testing DTD parser...
	@echo This should print out a complete DTD.
	@echo
	java html.DTD html2-net

test.html:
	@echo
	@echo Testing HTML parser...
	@echo This should print out an html error at line 18.
	@echo
	java html.Parser doc:index.html
	