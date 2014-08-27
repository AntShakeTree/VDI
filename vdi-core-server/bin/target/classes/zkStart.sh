#!/bin/bash
BASEPATH=$1
DATA=$2
MYID=$3
confFile=$BASEPATH"/conf/zoo.cfg"
binFile=$BASEPATH"/bin/zkServer.sh"
function createMyId(){
	myidFilePath=`grep "dataDir" $confFile | awk -F'=' '{print $2}'`
	if [[ -n $myidFilePath ]]; then
		mkdir $myidFilePath
		echo $MYID > $myidFilePath"/myid"
	fi
}
function splitStr(){
	local data=$1
	local i=1
	while [[ 1 -eq 1 ]]; do
        server=`echo $data | cut -d';' -f$i`
        if [[ -z $server ]]; then
            break
        fi
        servers[$i]=$server
        i=`expr $i + 1`
	done
}
createMyId
sed -i /^server.[0-9]/d $confFile
splitStr $DATA
for server in ${servers[*]}; do
	echo $server >> $confFile
done
process=`ps aux | grep "zookeeper"`
if [[ -n $process ]]; then
	processId=`echo $process | awk '{print $2}'`
	kill processId
fi
$binFile restart

