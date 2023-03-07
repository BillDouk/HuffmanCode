package org.hua.huffmancode;

import java.util.NoSuchElementException;

/**
 *
 * @author it21921,it21909,it21954
 * @param <E>
 */
public class MyArrayMinHeap<E extends Comparable<E>> implements MinHeap<E>{
    
    public static final int DEFAULT_CAPACITY = 127;
    
    private E[] array;
    private int size;
    
    public MyArrayMinHeap() {
        this.size = 0;
        this.array = (E[]) new Comparable[DEFAULT_CAPACITY+1];
    }
    
    public MyArrayMinHeap(E[] array) {
        int n = array.length;
        
        this.array = (E[]) new Comparable[n+1];
        this.size = n;
        
        for(int i = 0; 1< n; i++){
            this.array[i+1] = array[i];
        }
        
        for(int i = this.array.length / 2; i > 0; i--){
            fixdown(i);
        }
    }
    //insert to heap
    @Override
    public void insert(E elem) {
        if (size + 1 >= array.length) {
            doubleCapacity();
        }
        
        array[++size] = elem;
        fixup(size);
    }
    //find min from heap
    @Override
    public E findMin() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
        
        return array[1];
    }

    @Override
    //delete min from heap
    public E deleteMin() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
        
        E result = array[1];
                
        array[1] = array[size];
        array[size] = null;
        size--;
        fixdown(1);
        
        return result;
    }

    @Override
    //check if heap is empty
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    //return size of heap
    public int size() {
        return size;
    }

    @Override
    //clear heap
    public void clear() {
        for(int i = 1; i<=size; i++) {
            array[i] = null;
        }
        size = 0;
    }
    //swap 2 values
    private void swap(int i, int j) {
        E tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
    //fixup
    private void fixup(int k) {
        while(k > 1 && array[k].compareTo(array[k/2]) < 0) {
            swap(k, k/2);
            k = k/2;
        }
    }
    //fixdown
    private void fixdown(int k) {
        while(2*k <= size) {
            int j = 2*k;
            if (j+1 <= size && array[j+1].compareTo(array[j]) < 0) {
                j++;
            }
            
            if(array[k].compareTo(array[j]) <= 0) {
                break;
            }
            swap(k,j);
            
            k=j;
        }
    }
    //if array size is greater than capacity, make a new capacity
    private void doubleCapacity() {
        int newCapacity = (array.length-1)*2;
        E[] newArray = (E[]) new Comparable[newCapacity + 1];
        
        for(int i = 1; i<=size; i++) {
            newArray[i] = array[i];
        }
        
        array = newArray;
    }
}
