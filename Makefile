#
# WipeFreeSpaceGUI2 hand-made Makefile for creating distribution packages.
# Best with GNU make.
# Copyright (C) 2022-2024 Bogdan 'bogdro' Drozdowski, bogdro (at) users . sourceforge . net
#
# Project homepage: https://wipefreespace.sourceforge.io
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

###########################################################################
# General variables
###########################################################################

NAME = WipeFreeSpaceGUI2

# core system utilities
COPY		= /bin/cp -fr
DEL		= /bin/rm -fr
MOVE		= /bin/mv -f
MKDIR		= /bin/mkdir
LS		= /bin/ls
# Use the GNU tar format
# ifneq ($(shell tar --version | grep -i bsd),)
# PACK_GNUOPTS	= --format gnutar
# endif
PACK		= tar $(PACK_GNUOPTS) zcf
PACK_EXT	= tar.gz
SED		= sed
SED_OPTS	= -i
SHELL		= sh
TOUCH		= touch

# gpgsignsoft.sh is a wrapper aroun GnuPG (www.gnupg.org) that selects the
# default key and options
GNUPG_SIGNER	= gpgsignsoft.sh

# Apache Ant (ant.apache.org)
ANT		= ant

# URL of a Timestamping Authority
#TSAURL		= http://timestamp.comodoca.com
#TSAURL		= http://timestamp.digicert.com
#TSAURL		= http://tsa.izenpe.com/
#TSAURL		= http://tsa.starfieldtech.com/
#TSAURL		= http://timestamp.globalsign.com/scripts/timstamp.dll
#TSAURL		= http://dse200.ncipher.com/TSS/HttpTspServer
#TSAURL		= http://time.certum.pl/
#TSAURL		= https://timestamp.geotrust.com/tsa
TSAURL		= https://freetsa.org/tsr

# Java jar signer
JARSIGN		= jarsigner
JARSIGN_ALIAS	= 'bogdro-soft'
JARSIGN_OPTS	= -digestalg SHA-512 -sigalg SHA512withRSA -tsa $(TSAURL)

# image converters: Inkscape (https://inkscape.org) and
# ImageMagick (http://www.imagemagick.org/)
SVG2PNG		= inkscape

###########################################################################
# Internal variables
###########################################################################

# WipeFreeSpaceGUI2 version - from a "properties" file, also used from Java
#VER		= X.X
include src/BogDroSoft/wfsgui/rsrc/version.properties

SED_FIX_POM_VERSION = 's|<version>[^<]*</version>\s*<!--\s*WFSGUI2_VERSION\s*-->|<version>$(VER)</version> <!-- WFSGUI2_VERSION -->|'

FILE_ARCH_SRC = $(NAME)-$(VER)-src.$(PACK_EXT)
FILE_ARCH_BIN = $(NAME)-$(VER)-bin.$(PACK_EXT)
FILE_ARCH_JAVADOC = $(NAME)-$(VER)-javadoc.$(PACK_EXT)

FILE_PROGRAM = dist/$(NAME).jar
FILE_PROGRAM_SIGNED = dist/$(NAME)-signed.jar

FILE_ANY_SOURCE_FILE = src/BogDroSoft/wfsgui/Starter.java

DIR_TMP_DIST = $(NAME)-$(VER)

###########################################################################
# Targets
###########################################################################

all:	jar

###########################################################################
# Distribution packages
###########################################################################

# pack-src should be last, because it deletes elements used by other targets
pack:	pack-javadoc pack-bin pack-src

pack-src:	$(FILE_ARCH_SRC)

$(FILE_ARCH_SRC): clean Makefile
	$(DEL) $(DIR_TMP_DIST) $(FILE_ARCH_SRC) $(FILE_ARCH_SRC).asc
	$(SED) $(SED_OPTS) $(SED_FIX_POM_VERSION) pom.xml
	$(MKDIR) ../$(DIR_TMP_DIST)
	$(COPY) * ../$(DIR_TMP_DIST)
	$(MOVE) ../$(DIR_TMP_DIST) .
	$(PACK) $(FILE_ARCH_SRC)			\
		$(DIR_TMP_DIST)/AUTHORS			\
		$(DIR_TMP_DIST)/build.xml		\
		$(DIR_TMP_DIST)/ChangeLog		\
		$(DIR_TMP_DIST)/COPYING			\
		$(DIR_TMP_DIST)/dist			\
		$(DIR_TMP_DIST)/INSTALL			\
		$(DIR_TMP_DIST)/Makefile		\
		$(DIR_TMP_DIST)/manifest.mf		\
		$(DIR_TMP_DIST)/nbproject		\
		$(DIR_TMP_DIST)/NEWS			\
		$(DIR_TMP_DIST)/pom.xml			\
		$(DIR_TMP_DIST)/README			\
		$(DIR_TMP_DIST)/run*.bat		\
		$(DIR_TMP_DIST)/run*.sh			\
		$(DIR_TMP_DIST)/src			\
		$(DIR_TMP_DIST)/test
	$(DEL) $(DIR_TMP_DIST)
	$(GNUPG_SIGNER) $(FILE_ARCH_SRC)

pack-bin:	$(FILE_ARCH_BIN) test

$(FILE_ARCH_BIN):	jar-signed Makefile
	$(DEL) dist/javadoc
	$(DEL) $(DIR_TMP_DIST) $(FILE_ARCH_BIN) $(FILE_ARCH_BIN).asc
	$(SED) $(SED_OPTS) $(SED_FIX_POM_VERSION) pom.xml
	$(MKDIR) ../$(DIR_TMP_DIST)
	$(COPY) * ../$(DIR_TMP_DIST)
	$(MOVE) ../$(DIR_TMP_DIST) .
	$(PACK) $(FILE_ARCH_BIN)			\
		$(DIR_TMP_DIST)/AUTHORS			\
		$(DIR_TMP_DIST)/ChangeLog		\
		$(DIR_TMP_DIST)/COPYING			\
		$(DIR_TMP_DIST)/dist			\
		$(DIR_TMP_DIST)/INSTALL			\
		$(DIR_TMP_DIST)/pom.xml			\
		$(DIR_TMP_DIST)/README			\
		$(DIR_TMP_DIST)/run*.sh			\
		$(DIR_TMP_DIST)/run*.bat
	$(DEL) $(DIR_TMP_DIST)
	$(GNUPG_SIGNER) $(FILE_ARCH_BIN)

pack-javadoc:	$(FILE_ARCH_JAVADOC)

$(FILE_ARCH_JAVADOC):	dist/javadoc Makefile
	$(DEL) $(DIR_TMP_DIST) $(FILE_ARCH_JAVADOC) $(FILE_ARCH_JAVADOC).asc
	$(SED) $(SED_OPTS) $(SED_FIX_POM_VERSION) pom.xml
	$(MKDIR) ../$(DIR_TMP_DIST)
	$(COPY) * ../$(DIR_TMP_DIST)
	$(MOVE) ../$(DIR_TMP_DIST) .
	$(PACK) $(FILE_ARCH_JAVADOC) $(DIR_TMP_DIST)/dist/javadoc
	$(DEL) $(DIR_TMP_DIST)
	$(GNUPG_SIGNER) $(FILE_ARCH_JAVADOC)

dist/javadoc:	$(shell find src) Makefile
	$(DEL) dist/javadoc
	$(ANT) javadoc

###########################################################################
# Application
###########################################################################

jar: jar-clean $(FILE_PROGRAM)

$(FILE_PROGRAM):	$(shell find src) icons build.xml \
	nbproject/build-impl.xml Makefile
	$(ANT) jar
	$(TOUCH) $(FILE_PROGRAM)

jar-clean:
	$(ANT) clean

# The signed installer uses the signed JARs, so touch the respective
# source files after building, so that building the unsigned installer will
# use the unsigned files again

jar-signed: jar $(FILE_PROGRAM_SIGNED)

$(FILE_PROGRAM_SIGNED): jar Makefile
	$(JARSIGN) -signedjar $@ $(JARSIGN_OPTS) $(FILE_PROGRAM) $(JARSIGN_ALIAS)
	$(MOVE) $@ $(FILE_PROGRAM)
	$(TOUCH) $(FILE_ANY_SOURCE_FILE)

###########################################################################
# Icons
###########################################################################

# icons: src/BogDroSoft/wfsgui/rsrc/*.png
# Generate the icons for each SVG file found. This way we generate also the
# missing PNG files and not just the ones that exist but are older.
SVG_ICONS = $(shell $(LS) src/BogDroSoft/wfsgui/rsrc/*.svg)
PNG_ICONS = $(SVG_ICONS:%.svg=%.png)

icons:	$(PNG_ICONS)

%.png: %.svg Makefile
	$(SVG2PNG) --export-filename=$@ $<

###########################################################################
# Other targets
###########################################################################

javadoc-clean:
	$(DEL) dist/javadoc

check:	test

test:
	$(ANT) test

clean:	javadoc-clean jar-clean

.PHONY:	all pack pack-src pack-bin pack-javadoc \
	jar jar-clean jar-signed \
	icons \
	clean javadoc-clean \
	check test
