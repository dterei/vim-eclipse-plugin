#!/bin/bash

USER=bastl
SITE="$USER@vimplugin.sf.net:/home/groups/v/vi/vimplugin/htdocs/update"

scp "features/org.vimplugin.feature_$1.jar" ${SITE}"/features"
scp "plugins/org.vimplugin.plugin_$1.jar" ${SITE}"/plugins"
scp "site.xml" ${SITE}
