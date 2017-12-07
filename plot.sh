#!/bin/bash

#check for installation of parallel-rsync

if [ "$#" -ne 1 ] ; then
	echo "Data file not supplied."
	echo "Usage ./plot {data-file.txt}"
	exit
fi

"C:\Program Files\gnuplot\bin\gnuplot.exe" -e "filename='$1'" graph.gnuplot

#xdg-open graph.png
