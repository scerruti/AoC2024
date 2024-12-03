# [--- Day 2: Red-Nosed Reports ---](https://adventofcode.com/2024/day/1)

## Summary

I can use day 2 to reinforce the problem-solving process and the importance of planning before coding. I lost hours in my attempt to gut through the assignment rather than deconstructing it into smaller pieces and tackling them in independently.

## Topics 
1. Arrays, copying arrays
2. Compound Boolean Expressions
3. Methods that return boolean

## Part 1

Part 1 is fairly straightforward and easier to solve without thinking. This is a pitfall in Advent of Code, it encourages you to shortcut through the design process.

#### Algorithm
* Read one line of the data at a time and build an array of `int`s to represent a report.
* For each report, if the levels (elements) are ascending or descending, count it as a safe report.
* Report the number of safe reports.

## Part 2

For part two, safe reports are reports that are safe or safe with one and only one element removed.

A naive algorithm could run the algorithm for part 1 and if the report is unsafe it could remove one element at a time from the report until it found a safe report. However, there are three cases that need to be considered when we remove elements from a list. We could remove the first element, the last element or a middle element. 


### Alogrithm

* Read the left data into a list and the right into a frequency map
* Iterate through the list and lookup each value in the frequency table to find the number of occurrences 
* Compute similarity by multiplying those two values
* Sum the similarities