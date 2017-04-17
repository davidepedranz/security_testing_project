# Pixy tool
These are the commands runned to analyze the vulnerable PHP application.
```bash
# run pixy
pixy.pl -a -A -g -y xss ../original/php/schoolmate/index.php

# move the files produced to the dot folder

# convert the dot files into images
mkdir -p jpg
for f in dot/*.dot; do
    r=$(echo $f | sed 's/^\(.*\/\)\(.*\)\..*$/\2/')
    dot -Tjpg $f > jpg/$r.jpg
done
```