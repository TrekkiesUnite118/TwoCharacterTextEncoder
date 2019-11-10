package com.text.encoder.utils;

/**
 * Class to represent a basic pointer. 
 * @author TrekkiesUnite118
 *
 */
public class Pointer {

    //Index in the pointer table
    private int index;
    //Value of the pointer.
    private int value;
    
    /**
     * Getter for Index.
     * @return
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Setter for Index.
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }
    
    /**
     * Getter for Value
     * @return
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Setter for Value
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
    }
    
    /**
     * Constructor
     * @param index
     * @param value
     */
    public Pointer(int index, int value) {
        this.index = index;
        this.value = value;
    }
    
}
