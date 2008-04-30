#!/bin/zsh
########################################################### {{{1 ###########
#  Copyright (C) 2005,2006  Martin Krischik (JavaME port)
############################################################################
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
############################################################################
#  $Author: krischik $
#
#  $Revision: 275 $
#  $Date: 2008-04-29 19:29:17 +0200 (Di, 29 Apr 2008) $
#
#  $Id: Canvas.java 275 2008-04-29 17:29:17Z krischik $
#  $HeadURL: $
############################################################ }}}1 ###########

setopt Err_Exit;

local   in_Version="${1:-0.3.2}"
local   USER="${2:-krischik}"
local   SITE="${USER}@vimplugin.sf.net:/home/groups/v/vi/vimplugin/htdocs/update"

7za a -tzip                                             \
    "org.vimplugin_${in_Version}_site.zip"              \
    "features/org.vimplugin.feature_${in_Version}.zip"  \
    "plugins/org.vimplugin.plugin_${in_Version}.jar"    \
    "site.xml"

ncftpput -z upload.sourceforge.net /incoming "org.vimplugin_${in_Version}_site.zip";

scp "features/org.vimplugin.feature_${in_Version}.jar"  ${SITE}"/features";
scp "plugins/org.vimplugin.plugin_${in_Version}.jar"    ${SITE}"/plugins";
scp "site.xml"                                          ${SITE}

#vim: set nowrap tabstop=8 shiftwidth=4 softtabstop=4 expandtab :
#vim: set textwidth=0 filetype=zsh foldmethod=marker nospell :
