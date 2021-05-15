#!/bin/sh
i=1
j=2
while [ -f T${j}.gif ] ; do
  echo mv T${j}.gif T${i}.gif
  mv T${j}.gif T${i}.gif
  i=$j
  j=`expr $j + 1`
done