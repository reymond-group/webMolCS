#!/bin/bash

#############################################################
##MAKE SEPERATE DIRECTORY FOR EACH JOB, name of JOB=$1
#############################################################

name=DB
size=300

#mkdir MQNPROP DATS MC TRANS PARTMAPS ASFMAPS BINS M_ColoredPoints simToQuery1 simToQuery2 maxSimToRef ActualValues
####################################################################################
####################################################################################

#6) make partial maps
for i in TRANS/${name}*
do
java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.G_CreatePartMaps PARTMAPS/$(basename $i) $name.totminmax $size $i
done
####################################################################################
#7) merge maps to big ones
for i in simToQuery1 ActualValues simToQuery2 maxSimToRef
do
java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.H_MapMerger ASFMAPS/${name}_merge.$i.asf PARTMAPS/${name}*.$i.*
done
####################################################################################
#Do the ASF for freuqnecy map
#awk '{print $1,$2,$3,$6,0,$6}' ASFMAPS/${name}_merge.hac.asf >> ASFMAPS/${name}_merge.frequency.asf
####################################################################################
# 9 BINNING)
for i in $(seq 0 50 $size)
do

start=$i
max=$(($start+50))

if [ $max -gt $size ]
then
break;

fi

java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.I_Binning BINS $name.totminmax $start $max $size $size $size TRANS/*

done
####################################################################################
# 10) Merge Average Files
java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.J_MergeAverageFiles $name.tmpAVG X_*.avg
####################################################################################
java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.M_ColorCoding -i ASFMAPS/${name}_merge.maxSimToRef.asf -o M_ColoredPoints/maxSimToRef.binsColor -m 1,0.2,4,0,1,0 -c 240,360

java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.M_ColorCoding -i ASFMAPS/${name}_merge.simToQuery1.asf -o M_ColoredPoints/simToQuery1.binsColor -m 1,0,4,0,1,0 -c 240,360

java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.M_ColorCoding -i ASFMAPS/${name}_merge.simToQuery2.asf -o M_ColoredPoints/simToQuery2.binsColor -m 1,0,4,0,1,0 -c 240,360

#Get min and max from file for actual vals
HIGH=$(awk '{print $NF}' SMI/*.smi | awk 'BEGIN{FS=";"} {print $2}' |  sort -n -r | head -n 1)
LOW=$(awk '{print $NF}' SMI/*.smi | awk 'BEGIN{FS=";"} {print $2}' |  sort -n | head -n 1)

java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.M_ColorCoding -i ASFMAPS/${name}_merge.ActualValues.asf -o M_ColoredPoints/ActualValues.binsColor -m $HIGH,$LOW,4000000,0,1,0 -c 240,360
####################################################################################
for i in simToQuery1 ActualValues simToQuery2 maxSimToRef
do
java -Xmx4000M -cp ../../dist/SimMapPortalGenRator.jar ThreeDOrganizer2.N_finalDataFiles M_ColoredPoints/${i}.binsColor $name.tmpAVG ${i}/$name ${i}/$name $name
done
####################################################################################
rm X_*.avg
####################################################################################
for prop in simToQuery1 ActualValues simToQuery2 maxSimToRef
do

##A) COORDINATES
coord=$(awk '{printf $2"_"$3"_"$4";"}' $prop/DB.VIS | awk '{print substr($1, 1, length($0)-1)}')
awk '{print $2"_"$3"_"$4}' $prop/DB.VIS >> tmp1.txt

##B) COLOR
color=$(awk '{printf $5"_"$6"_"$7";"}' $prop/DB.VIS | awk '{print substr($1, 1, length($0)-1)}')

##C) ASF
asf=$(awk '{printf $1"_"$2"_"$3";"}' $prop/DB.ASF | awk '{print substr($1, 1, length($0)-1)}')

##D) Compounds
for bins1 in $(ls BINS)
do

for bins2 in $(ls BINS/$bins1/)
do
cpd=$(awk '{printf $2"_"}' BINS/$bins1/$bins2 |  awk '{print substr($1, 1, length($0)-1)}')
echo $bins1"_"$bins2 $cpd >> tmp2.txt
done

done

for crd in $(cat tmp1.txt)
do
grep -w $crd tmp2.txt >> tmp3.txt
done

cpd=$(awk '{printf $2";"}' tmp3.txt | awk '{print substr($1, 1, length($0)-1)}')

## E) AVG COMPOUNDS
awk '{print $1}' MQNPROP/DB.smi > tmp4.txt
java -cp ../../dist/SimMapPortalGenRator.jar tools.aid01 tmp4.txt DB.AVG tmp5.txt

avgCPD=$(awk '{printf $0";"}' tmp5.txt | awk '{print substr($1, 1, length($0)-1)}')

echo $coord >> $prop.data
echo $color >> $prop.data
echo $asf >> $prop.data
echo $cpd >> $prop.data
echo $avgCPD >> $prop.data

rm tmp*.txt
echo $prop

done

java -cp ../../dist/SimMapPortalGenRator.jar KMST.KMST simToQuery1.data tree.data
############################################################################
