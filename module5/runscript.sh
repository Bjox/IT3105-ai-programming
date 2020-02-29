#!/bin/bash

current=0
for i in $(seq 1 20); do
    ((current=current+1))
    clear
    echo "=== CONFIGURATION 1 === RUN #$current"
    python3 main.py config1.cfg
    clear
done

current=0
for i in $(seq 1 20); do
    ((current=current+1))
    clear
    echo "=== CONFIGURATION 2 === RUN #$current"
    python3 main.py config2.cfg
done

current=0
for i in $(seq 1 20); do
    ((current=current+1))
    clear
    echo "=== CONFIGURATION 3 === RUN #$current"
    python3 main.py config3.cfg
done

current=0
for i in $(seq 1 20); do
    ((current=current+1))
    clear
    echo "=== CONFIGURATION 4 === RUN #$current"
    python3 main.py config4.cfg
done

current=0
for i in $(seq 1 20); do
    ((current=current+1))
    clear
    echo "=== CONFIGURATION 5 === RUN #$current"
    python3 main.py config5.cfg
done
