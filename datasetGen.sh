#!/bin/sh
k=2
l=100
n=10
while [ $n -le 30 ] 
do 
	r=1
	while [ $r -le 14 ]
	do
		m=$(expr $r \* $n)
		mkdir "dataset/"$n"_"$r"_"$m
		echo "mkdir dataset/"$n"_"$r"_"$m
		i=1
		j=1
		while [ $i -le 50 ]
		do
			perl DTPgen.pl $k $n $m $l $j > \
				"dataset/"$n"_"$r"_"$m"/"$n"_"$r"_"$m"_"$i".tsat"
			echo "perl DTPgen.pl "$k" "$n" "$m" "$l" "$j" > "\
				"dataset/"$n"_"$r"_"$m"/"$n"_"$r"_"$m"_"$i".tsat"
			i=$(expr $i + 1)
			j=$(expr $j + 100)
		done
		r=$(expr $r + 1)
	done
	n=$(expr $n + 5)
done

