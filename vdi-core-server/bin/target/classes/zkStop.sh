#!/bin/bash
BASEPATH=$1
binFile=$BASEPATH"/bin/zkServer.sh"
$binFile stop
process=`ps aux | grep "zookeeper"`
if [[ -n $process ]]; then
	processId=`echo $process | awk '{print $2}'`
	kill processId
fi

