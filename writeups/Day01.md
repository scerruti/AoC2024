# [--- Day 1: Historian Hysteria ---](https://adventofcode.com/2024/day/1)

## Summary

Day 1 challenges students to recognize distance between two points as absolute value, construct a summation algorithm for each part. This simple implementation uses sorted lists for part 1 and a frequency map for part 2. Students can recognize a summation algorithm for parts 1 and 2.

## Topics
1. `Scanner`
1. `Math` class
1. `ArrayList` (sorting)
1. Parallel lists
1. Regular and Enhanced `for` Loops
1. Frequency map using a `HashMap`

## Deviations from the AP CS A Java Subset

Because we are using I/O we need to make use of __Exception Handling__. 

The construction of the frequency map makes use of `merge`. A simpler implementation might look like:
```java
if (rightLocationFrequencyMap != null) {
    if (rightLocationFrequencyMap.containsKey(rightLocationID)) {
        rightLocationFrequencyMap.put(rightLocationID, rightLocationFrequencyMap.get(rightLocationID) + 1);
    } else {
        rightLocationFrequencyMap.put(rightLocationID, 1);
    }
}
```

## Part 1
Old hats may be concerned about loading data into memory and sorting for this challenge, but since it's day 1, that's probably okay. The availability of `sort` for `ArrayList` makes it a natural for today but the algorithm could be implemented with arrays. 

#### Algorithm
* Read the data into parallel lists
* Sort the lists into natural order
* Compute the distance (absolute value of difference) for the pair of values
* Sum the distances

## Part 2
Rather than using a frequency map, students may choose to implement a count alogrithm on the right list. For clarity this would best be implemented inside a separate method. This would eliminate the use of a map altogether.

This code demonstrates that lists can be modified inside methods, specifically `readData`. There is a discussion here that can be had between languages with mutable objects and languages with immutable objects. Refer them to the Scala docs, [Concrete Immutable Collection Classes](https://docs.scala-lang.org/overviews/collections-2.13/concrete-immutable-collection-classes.html), for examples.

### Alogrithm

* Read the left data into a list and the right into a frequency map
* Iterate through the list and lookup each value in the frequency table to find the number of occurrences 
* Compute similarity by multiplying those two values
* Sum the similarities