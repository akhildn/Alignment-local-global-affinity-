#!/bin/bash
if [[ $# -lt 1 ]];then
    echo "Not right usage"
fi

javac Sample.java 

while [[ $1 != "" ]]; do
    case $1 in
        -f)
            shift
            FILENAME=$1
            ;;
        -a)
            shift
            ALGORITHM=$1
            ;;
    -m)
            shift
            MATCH=$1
            ;;
        -mmp)
            shift
            MISMATCH=$1
            ;;
        -idp)
            shift
            INDEL=$1
            ;;
        -gi)
            shift
            GAP=$1
            ;;
        -gx)
            shift
            EXT=$1
            ;;
        *)
            usage
            ;;
    esac
    shift
done


if [[ -z ${FILENAME} ]]; then
	echo "Input File name not provided."
fi


if [[ -z ${ALGORITHM} ]]; then
	echo "Algorithm name not provided."
fi

if [[ -z ${MATCH} ]]; then
	echo "Match Score not provided."
fi

if [[ -z ${MISMATCH} ]]; then
	echo "MisMatch Score not provided."
fi

if [[ -z ${INDEL} ]]; then
	echo "Indel Score not provided."
fi

if [[ -z ${GAP} ]]; then
	GAP=0
fi

if [[ -z ${EXT} ]]; then
	EXT=0
fi

java Sample $FILENAME $ALGORITHM $MATCH $MISMATCH $INDEL $GAP $EXT
 
