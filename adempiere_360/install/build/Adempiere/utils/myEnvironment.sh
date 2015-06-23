#!/bin/sh
#
# myEnvironment defines the variables used for Adempiere
# Do not edit directly - use RUN_setup
#
# $Id: myEnvironmentTemplate.sh,v 1.12 2005/02/21 03:17:21 jjanke Exp $

echo Setting myEnvironment ....
#	Clients only needs
#		ADEMPIERE_HOME
#		JAVA_HOME 
#	Server install needs to change
#		ADEMPIERE_DB_NAME	(for Oracle)
#		passwords
#
#	For a HTML browser, Adempiere will call "netscape <targetURL>"
#	If not in the path, provide a link called netscape to your browser

# 	Homes ...
ADEMPIERE_HOME=/home/augusto/ws/adempiere_360
export ADEMPIERE_HOME
JAVA_HOME=/usr/lib/jvm/java-6-openjdk
export JAVA_HOME

#	Database ...
ADEMPIERE_DB_SERVER=192.168.1.69
export ADEMPIERE_DB_SERVER
ADEMPIERE_DB_USER=adempiere
export ADEMPIERE_DB_USER
ADEMPIERE_DB_PASSWORD=adempiere
export ADEMPIERE_DB_PASSWORD
ADEMPIERE_DB_URL=@ADEMPIERE_DB_URL@
export ADEMPIERE_DB_URL
ADEMPIERE_DB_PORT=jdbc:postgresql://192.168.1.69:5432/adempiereLBR
export ADEMPIERE_DB_PORT

#	Oracle Specifics ...
ADEMPIERE_DB_PATH=@ADEMPIERE_DB_PATH@
export ADEMPIERE_DB_PATH
ADEMPIERE_DB_NAME=@ADEMPIERE_DB_NAME@
export ADEMPIERE_DB_NAME
ADEMPIERE_DB_SYSTEM=@ADEMPIERE_DB_SYSTEM@
export ADEMPIERE_DB_SYSTEM

#	Homes(2)
ADEMPIERE_DB_HOME=$ADEMPIERE_HOME/utils/$ADEMPIERE_DB_PATH
export ADEMPIERE_DB_HOME
JBOSS_HOME=/home/augusto/ws/adempiere_360/jboss
export JBOSS_HOME

#	Apps Server
ADEMPIERE_APPS_SERVER=127.0.0.1
export ADEMPIERE_APPS_SERVER
ADEMPIERE_WEB_PORT=8091
export ADEMPIERE_WEB_PORT
ADEMPIERE_JNP_PORT=1099
export ADEMPIERE_JNP_PORT
#	SSL Settings - see jboss/server/adempiere/deploy/jbossweb.sar/META-INF/jboss-service.xml
ADEMPIERE_SSL_PORT=5443
export ADEMPIERE_SSL_PORT
ADEMPIERE_KEYSTORE=/usr/local/Adempiere/keystore/myKeystore
export ADEMPIERE_KEYSTORE
ADEMPIERE_KEYSTOREPASS=myPassword
export ADEMPIERE_KEYSTOREPASS

#	etc.
ADEMPIERE_FTP_SERVER=@ADEMPIERE_FTP_SERVER@
export ADEMPIERE_FTP_SERVER

#	Java
ADEMPIERE_JAVA=$JAVA_HOME/bin/java
export ADEMPIERE_JAVA
ADEMPIERE_JAVA_OPTIONS="-Xms64M -Xmx512M -DADEMPIERE_HOME=$ADEMPIERE_HOME"
export ADEMPIERE_JAVA_OPTIONS
CLASSPATH="$ADEMPIERE_HOME/lib/Adempiere.jar:$ADEMPIERE_HOME/lib/AdempiereCLib.jar"
export CLASSPATH

if [ $DOLLAR$# -eq 0 ] 
  then
    cp myEnvironment.sh myEnvironment.sav
fi
