# A1_Parallel Programming makefile
# Tafadzwa Nyazenga
# 6 August 2022


JAVA=/usr/bin/java
JAVAC=/usr/bin/javac
JAVADOC=/usr/bin/javadoc
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
DOCDIR=doc

#.PHONY: default doc clean

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= MeanFilterSerial.class MedianFilterSerial.class
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

### Documentation
doc:
		$(JAVADOC) -d $(DOCDIR) $(SRCDIR)/*

clean:
	$(RM) $(BINDIR)/*.class


runMeanS: $(CLASS_FILES)
		$(JAVA) -cp $(BINDIR) MeanFilterSerial

runMedianS: $(CLASS_FILES)
		$(JAVA) -cp $(BINDIR) MedianFilterSerial


clean-doc:
		rm -r $(DOCDIR)/*
