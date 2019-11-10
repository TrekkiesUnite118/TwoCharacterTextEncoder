package com.text.encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.text.encoder.utils.FileUtils;
import com.text.encoder.utils.Pointer;

/**
 * Pointer Calculator
 * 
 * Reads in the original text and pointer table, along with the new text.
 * It then calculates and generates a new pointer table.
 * 
 * @author TrekkiesUnite118
 *
 */
public class PointerCalculator {

    //Original Pointer file name
    private String inputPointerFileName;
    //Original text file name.
    private String inputOrigTextFileName;
    //Output Directory
    private String outputFilePath;
    //Output path for text pieces.
    private String pointerTextOutputFilePath;
    
    //First Pointer value
    private int startingPointer;
    //Last Pointer Value
    private int finalPointer;
    
    //Flag for generating text pieces.
    private boolean generatePointerTextPieces = false;
    private Properties properties = null;
    
    //Map to store pointers
    public Map<Integer, Pointer> pointerMap = new HashMap<>();
    //Map to store pointers in order.
    public Map<Integer, Pointer> sortedPointerMap = new HashMap<>();
    
    public PointerCalculator inputPointerFileName(String inputPointerFileName) {
        this.inputPointerFileName = inputPointerFileName;
        return this;
    }
    
    public PointerCalculator inputOrigTextFileName(String inputOrigTextFileName) {
        this.inputOrigTextFileName = inputOrigTextFileName;
        return this;
    }
    
    public PointerCalculator pointerTextOutputFilePath(String pointerTextOutputFilePath) {
        this.pointerTextOutputFilePath = pointerTextOutputFilePath;
        return this;
    }
    
    public PointerCalculator outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    public PointerCalculator startingPointer(int startingPointer) {
        this.startingPointer = startingPointer;
        return this;
    }
    
    public PointerCalculator finalPointer(int finalPointer) {
        this.finalPointer = finalPointer;
        return this;
    }
    
    public PointerCalculator generatePointerTextPieces(boolean generatePointerTextPieces) {
        this.generatePointerTextPieces = generatePointerTextPieces;
        return this;
    }
    
    public PointerCalculator properties(Properties properties) {
        this.properties = properties;
        return this;
    }
    
    /**
     * Calculates the new pointer values based on the newly encoded text.
     * 
     * It takes in the new text data and splits it into it's individual
     * text sequences. Then it calcluates the new pointer values and writes 
     * them to a file.
     * 
     * @param newText
     * @throws IOException
     */
    public void calculatePointers(String newText) throws IOException {
        //Parse the original pointers.
        parsePointers();
        //Get the delimiter.
        String endSeqDelim = properties.getProperty("encoder.delimiter.endSequence.string"); 
        //Break the text into it's individual pieces.
        String[] pieces = newText.split(endSeqDelim);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //Allocate a 4 byte buffer to represent a pointer value.
        ByteBuffer bb = ByteBuffer.allocate(4);
        int newPointer = startingPointer;
       
        //Map to store pointers
        Map<Integer, Pointer> pointers = new HashMap<>();
        //Map to store pointer combos
        Map<Integer, Integer> pointerCombos = new HashMap<>();
        
        /*
         * Pointer combos represent a special edge case scenario. 
         * Sometimes you might encounter a pointer table where the value
         * in the table actually points to a combination of text
         * sequences instead of just a single sequence.
         * 
         * Pointer combos represent this behavior. They can be defined
         * in the properties file and this code will parse them in.
         */
        for(Object key : properties.keySet()) {
            String keyString = (String) key;
            if(keyString.contains("encoder.pointer.combination.")) {
                String[] keyPieces = keyString.split("encoder.pointer.combination.");
                Integer value = Integer.valueOf(keyPieces[1]);                
                pointerCombos.put(value, Integer.valueOf(properties.getProperty(keyString)));
            }
        }
        
        //Put the first pointer in the map.      
        pointers.put(0, new Pointer(0, startingPointer));
        
        int pointerKey = 1;
        for(int i = 0; i < pieces.length; i++) {
            /*
             * If i represents a pointer combo, combine the text sequences for the specified
             * number of combinations and use that to calcuate the next pointer.
             */
            if(pointerCombos.keySet().contains(i)) {
                int iter = pointerCombos.get(i);
                
                for(int j = 1; j <= iter; j++) {
                    if(j != iter) {
                        newPointer = newPointer + pieces[i].length() + 2;
                        i++;
                    } else {
                        //For the last sequence we don't add 2 or increment i.
                        newPointer = newPointer + pieces[i].length(); 
                    }
                }                  
            } else {
                //if it's not a combo just add the length of the piece nad add 2.
                newPointer += pieces[i].length() + 2;
            }
            //Add the new pointer to the map and increment the pointerKey.
            pointers.put(pointerKey, new Pointer(i + 1, newPointer));
            pointerKey++;
        }
        
        //Set the index of the new pointers to match those of the original pointers.
        for(int i = 1; i < sortedPointerMap.size(); i++) {
            pointers.get(i).setIndex(sortedPointerMap.get(i).getIndex());
        }
        
        //Write the new pointers to the output stream
        for(int i = 0; i < pointers.size(); i++) {
            for(Pointer p : pointers.values()) {
                if(p.getIndex() == i) {
                    bb.putInt(0, p.getValue());
                    out.write(bb.array());
                }
            }
        }
        
        //Write the new pointer table to a file.
        FileUtils.writeToFile(out.toByteArray(), "textpointers.bin", outputFilePath);
    }
    
    /**
     * Parses the original pointers and will write the text pieces out to files
     * if desired.
     * 
     * @throws IOException
     */
    private void parsePointers() throws IOException {
        
        //Read the original pointers to a byte array.
        File pointerFile = new File(inputPointerFileName);
        byte[] pointerBytes = Files.readAllBytes(pointerFile.toPath());
        
        int i = 0;
        int j = 0;
        int index = 0;
        while( i < pointerBytes.length) {
            //Read 4 bytes out of the Header Array into a temporary buffer.
            ByteBuffer bb = ByteBuffer.wrap(pointerBytes, i, 4);
            Pointer p = new Pointer(index, bb.getInt());
            index++;
            i +=4;
            
            pointerMap.put(p.getValue(), p);
            
        }
        //Sort the pointers in numerical order.
        for(int iter = 0; iter <= finalPointer; iter++) {
            if(pointerMap.containsKey(iter)) {
                sortedPointerMap.put(j, pointerMap.get(iter));
                j++;
            }
        }
        //If we want to print out the text pieces
        if(generatePointerTextPieces) {
            //Read the original text file into a byte array.
            File text = new File(inputOrigTextFileName);
            byte[] textBytes = Files.readAllBytes(text.toPath());
            
            int textIter = 0;
            int pointerIter = 0;
            
            while (textIter < textBytes.length) {
              //Calcuate the pointer sizes of the original pointers.
                Pointer pointer = sortedPointerMap.get(pointerIter);
                int size = 0;
                if(sortedPointerMap.containsKey(pointerIter + 1)) {
                    Pointer pointer2 = sortedPointerMap.get(pointerIter + 1);
                    size = pointer2.getValue() - pointer.getValue();
                } else {
                    size = (textBytes.length + startingPointer) - pointer.getValue();
                }
                
                //Using the size get copy that many bytes into the new byte array.
                byte[] textPiece = new byte[size];
                copyBytes(textBytes, textPiece, textIter +4, 0, size);
                
              
                //write out to a file.
                if(pointerTextOutputFilePath == null && pointerTextOutputFilePath.isEmpty()) {
                    pointerTextOutputFilePath = outputFilePath;
                }
                FileUtils.writeToFile(textPiece, pointerIter + ".bin", pointerTextOutputFilePath);
                
                pointerIter++;
                textIter += size;
            }
        }
        
    }
    
    public int getFinalPointer() {
        return finalPointer;
    }

    public void setFinalPointer(int finalPointer) {
        this.finalPointer = finalPointer;
    }

    public Map<Integer, Pointer> getPointerMap() {
        return pointerMap;
    }

    public void setPointerMap(Map<Integer, Pointer> pointerMap) {
        this.pointerMap = pointerMap;
    }

    public int getStartingPointer() {
        return startingPointer;
    }

    public Map<Integer, Pointer> getSortedPointerMap() {
        return sortedPointerMap;
    }
    
    public void setInputPointerFileName(String inputPointerFileName) {
        this.inputPointerFileName = inputPointerFileName;
    }

    public void setInputOrigTextFileName(String inputOrigTextFileName) {
        this.inputOrigTextFileName = inputOrigTextFileName;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public void setStartingPointer(int startingPointer) {
        this.startingPointer = startingPointer;
    }

    public void setSortedPointerMap(Map<Integer, Pointer> sortedPointerMap) {
        this.sortedPointerMap = sortedPointerMap;
    }

    public void setPointerTextOutputFilePath(String pointerTextOutputFilePath) {
        this.pointerTextOutputFilePath = pointerTextOutputFilePath;
    }

    public void setGeneratePointerTextPieces(boolean generatePointerTextPieces) {
        this.generatePointerTextPieces = generatePointerTextPieces;
    }

    /**
     * Copies bytes from one byte array to another.
     * 
     * @param from
     * @param to
     * @param startFrom
     * @param startTo
     * @param size
     */
    private static void copyBytes(byte[] from, byte[] to, int startFrom , int startTo, int size) {
        
        for(int i = startFrom; i <= startFrom + size ; i++) {
            if(i < from.length && startTo < to.length) {
                //System.out.println("Copying Bytes...");
                to[startTo] = from[i];
                startTo++;
            }
        }
        
    }
}
