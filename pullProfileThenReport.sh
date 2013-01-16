#!/bin/bash

ADB_PATH=
GPROF_PATH=

find_adb()
{	
	
	FOUND=$(find ~/ -path "*/platform-tools/adb")
	NUM_FOUND=$(find ~/ -path "*/platform-tools/adb" | wc -l)
	if [ "$NUM_FOUND" !=  "1" ]; then
		echo "ERROR: more/less than 1 results found for adb"
		return 1
	else
		ADB_PATH="$FOUND"
		return 0
	fi

}

find_gprof()
{
	FIND_STR="*/toolchains/arm-linux-androideabi-4.4.3/prebuilt/linux-x86/bin/arm-linux-androideabi-gprof"
	FOUND=$(find ~/ -path "$FIND_STR")
	NUM_FOUND=$(find ~/ -path "$FIND_STR" | wc -l)
	if [ "$NUM_FOUND" != "1" ]; then
		echo "ERROR: more/less than 1 results found for gprop"
		return 1
	else
		GPROF_PATH="$FOUND"
		return 0
	fi
}

exec_cmds()
{
	$ADB_PATH pull /sdcard/gmon.out .
	if [ $? -eq 0 ]; then
		$GPROF_PATH obj/local/armeabi-v7a/libdesmumeneon.so > report.txt
	else
		echo "aborting..."
		return 1
	fi
}

run() 
{
	if ! find_adb; then return 1; fi
	if ! find_gprof; then return 1; fi
	echo ADB="$ADB_PATH"
	echo GPROF="$GPROF_PATH"
	exec_cmds
	return 0
}

run


