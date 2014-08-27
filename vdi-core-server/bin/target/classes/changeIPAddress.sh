#!/bin/bash
OLDIPADDRESS=$1
NEWIPADDRESS=$2
echo $OLDIPADDRESS
echo $NEWIPADDRESS
sed -e s/$OLDIPADDRESS/$NEWIPADDRESS/g /etc/hosts > /tmp/ny && mv -f /tmp/ny /etc/hosts
killall -9 java
killall -9 java
service tomcat6 start