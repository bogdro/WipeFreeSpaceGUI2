#!/bin/sh
#
# WipeFreeSpaceGUI2 startup shell script for starting in English.
#
# Copyright (C) 2022-2025 Bogdan 'bogdro' Drozdowski, bogdro (at) users . sourceforge . net
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

if ( [ -e dist/WipeFreeSpaceGUI2.jar ] ); then
	java -jar -Duser.language=en -Duser.country=US dist/WipeFreeSpaceGUI2.jar
elif ( [ -e WipeFreeSpaceGUI2.jar ] ); then
	java -jar -Duser.language=en -Duser.country=US WipeFreeSpaceGUI2.jar
fi
