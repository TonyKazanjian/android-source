package com.bloc.recursion;

import java.util.*;

public class RecursionUtils extends Object {
    /*
     * findMaxRecursively
     *
     * Takes a list of numbers and finds the largest among them
     * using recursive calls.
     *
     * @param numbers a list of numbers, can be odd or even numbered
     * @return the largest number in the list
     *
     * Hint: your base case may be a comparison of 2 numbers
     */
    public static final int findMaxRecursively(List<Integer> numbers) {
        /************************************************
         *  ASSIGNMENT:
         *  Implement this method, the return value must
         *  change
        /************************************************/

        if(numbers.size() == 1)
         return numbers.get(0);
     
    List<Integer> bottomHalf = numbers.subList(0,numbers.size()/2);
     int bottom = findMaxRecursively(bottomHalf);

     List<Integer> topHalf = numbers.subList(numbers.size()/2,numbers.size());
     int top = findMaxRecursively(topHalf);

     return top>bottom?top:bottom;

         }
        
}

