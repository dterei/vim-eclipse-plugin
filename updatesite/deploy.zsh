#!/bin/zsh

setopt Err_Exit;

local   in_Version="${1:-0.3.2}"
local   USER="${2:-krischik}"
local   SITE="${USER}@vimplugin.sf.net:/home/groups/v/vi/vimplugin/htdocs/update"

7za a -tzip                                             \
    "features/org.vimplugin.feature_${in_Version}.jar"  \
    "features/org.vimplugin.feature_${in_Version}"

7za a -tzip                                             \
    "org.vimplugin.site_${in_Version}.jar"              \
    "features/org.vimplugin.feature_${in_Version}.jar"  \
    "plugins/org.vimplugin.plugin_${in_Version}.jar"    \
    "site.xml"

ncftpput -z upload.sourceforge.net /incoming "org.vimplugin.site_${in_Version}.jar";

scp "features/org.vimplugin.feature_${in_Version}.jar"  ${SITE}"/features";
scp "plugins/org.vimplugin.plugin_${in_Version}.jar"    ${SITE}"/plugins";
scp "site.xml"                                          ${SITE}

#vim: set nowrap tabstop=8 shiftwidth=4 softtabstop=4 expandtab :
#vim: set textwidth=0 filetype=zsh foldmethod=marker nospell :
