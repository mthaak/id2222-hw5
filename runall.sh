#!/bin/bash
bash compile.sh
bash run.sh -graph ./graphs/3elt.graph
bash plot.sh "E:/Scala/ID2222/JaBeJa/output/3elt.graph.txt"
bash run.sh -graph ./graphs/add20.graph
bash plot.sh "E:/Scala/ID2222/JaBeJa/output/add20.graph.txt"
bash run.sh -graph ./graphs/twitter.graph
bash plot.sh "E:/Scala/ID2222/JaBeJa/output/twitter.graph.txt"
#start "E:/Scala/ID2222/JaBeJa/output/3elt.graph.txt.png"
