#!/bin/sh

JAVA=java

#
# AnalyticsOps installation
#
AO_INSTALL=$( cd "$( dirname "$0" )/.." && pwd )

#
# Redefining java.io.tmpdir because cdp4j is attempting to create the folder cdp4j into that directory.
# This might lead to access rights issue when using several AOShell users on the machine. Ensure to have
# a different java.io.tmpdir for each user.
#
AO_USER=$(id -nu)
AO_TMPDIR=/tmp/$AO_USER

mkdir -p "$AO_TMPDIR"

#
# Use the existing env. AO_JAVA_OPTS otherwise default to -Xmx512m.
# Handy when run from a Docker to set the JVM memory (and others).
#
if [ -z "$AO_JAVA_OPTS" ]; then
  AO_JAVA_OPTS="-Xmx512m"
fi

#
# JAVA setup
#
AO_JAVA_OPTS_EX="-Djava.io.tmpdir=$AO_TMPDIR"

#
# exec: Docker keep PID=1 to make a clean Docker stop
#
exec $JAVA $AO_JAVA_OPTS $AO_JAVA_OPTS_EX -cp "$AO_INSTALL/lib/*" ic3.analyticsops.shell.AOShell