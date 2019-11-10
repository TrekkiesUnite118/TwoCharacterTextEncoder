package com.text.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.text.encoder.utils.FileUtils;

/**
 * TextEncoder class
 * 
 * This will encode the new text based on the newly derived font sheet.
 * 
 * @author TrekkiesUnite118
 *
 */
public class TextEncoder {

    //Output file name.
    private String fileName;
    //Output path
    private String outputFilePath;
    //To Upper Case flag.
    private boolean toUpper = false;
    //Firet value in the font table.
    private int firstValue;
    //Map to store encoding key value pairs.
    private Map<Integer, String> pairEncodingMap; 
    
    private Set<String> delimiterStrings = new HashSet<>();
    
    private Properties properties = null;
   
    public int totalPairs = 0;
    
    public TextEncoder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
    
    public TextEncoder outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }
    
    public TextEncoder toUpper(boolean toUpper) {
        this.toUpper = toUpper;
        return this;
    }
    
    public TextEncoder firstValue(int firstValue) {
        this.firstValue = firstValue;
        return this;
    }
     
    public TextEncoder properties(Properties properties) {
        this.properties = properties;
        return this;
    }
    
    /**
     * Encodes the new text based on the pair encoding map.
     * 
     * @param newText
     * @throws IOException
     */
    public void encodeString(String newText) throws IOException {
        //If necessary convert the string to all upper case.
        if(toUpper) {
            newText = newText.toUpperCase();
        }
        
        //Parse delimiter keys from properties file.
        List<String> delimKeys = new ArrayList<>();
        for(Object key : properties.keySet()) {
            String keyString = (String) key;
            if(keyString.contains("encoder.delimiter.") && keyString.contains(".string")) {
                delimKeys.add(keyString);
            }
        }
        //Add delimiter strings to map for use in creating encoding map.
        for(String key : delimKeys) {
            delimiterStrings.add(properties.getProperty(key));
        }
        
        //Generate Set to hold unique pairs.
        Set<String> pairs = stringToChars(newText);
        //Generate pair encoding map
        pairEncodingMap = createEncodingMap(firstValue, pairs);
        
        //Create Map to store pairs to codes
        Map<String, Integer> codeToPairMap = new HashMap<>();
        for(int i = firstValue; i <= pairEncodingMap.size(); i++) {
            System.out.println(i + " " + pairEncodingMap.get(i));
            codeToPairMap.put(pairEncodingMap.get(i), i);
        }
        //Add delimiter keys and values to code pair map.
        for(String key : delimKeys) {
            codeToPairMap.put(properties.getProperty(key), Integer.valueOf(properties.getProperty(key.replace(".string", ".value"))));
        }
        
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int i = 0;
        String pair = "";
        //Create a byte buffer to hold a 2-byte encoded pair.
        ByteBuffer bb = ByteBuffer.allocate(2);
            i = 0;
            while(i < newText.length()) {
                //If we have enough for a pair, get it from the string.
                if(i + 2 <= newText.length()) {
                    pair = newText.substring(i, i + 2);
                }else {
                    //otherwise just get the letter we have and add a space.
                    pair = newText.substring(i, i + 1) + " ";
                    
                }
                //If the pair isn't empty, write it's 2-byte code to the output stream.
                if(!pair.trim().isEmpty()) {
                   bb.putShort(0, codeToPairMap.get(pair).shortValue());
                   out.write(bb.array());
                }
                i +=2;
            }

        //Write the encoded text to a file.
        FileUtils.writeToFile(out.toByteArray(), fileName, outputFilePath);
    }
    
    /**
     * Breaks the new text into 2 character pairs.
     * 
     * @param newText
     * @return
     */
    private Set<String> stringToChars(String newText) {
        //Get the sequence and line break delimiters.
        String textSequenceDelimiter = properties.getProperty("encoder.delimiter.endSequence.string");
        
        //Break the new text down into individual pieces.
        String[] pieces = newText.split(textSequenceDelimiter);
        Set<String> pairs = new HashSet<>();
        int i = 0;
        String pair = "";
        //Break each piece down into 2 character pairs, and add them to the set.
        for(String p : pieces) {
            i = 0;
            while(i < p.length()) {
              //If we have enough for a pair, get it from the string.
                if(i + 2 <= p.length()) {
                   
                    pair = p.substring(i, i + 2);
                }else {
                    //otherwise just get the letter we have and add a space.
                    pair = p.substring(i, i + 1) + " ";
                    
                }
                //If the pair isn't one of the delimiters and isn't empty, add it to the set.
                if(!delimiterStrings.contains(pair.trim()) && !pair.trim().isEmpty()) {
                    pairs.add(pair);
                    totalPairs++;
                }
                i +=2;
            }
        }
        //Return the set of pairs.
        return pairs;
    }
    
    /**
     * Creates the encoding map.
     * 
     * @param key
     * @param pairSet
     * @return
     */
    private Map<Integer, String> createEncodingMap(int key, Set<String> pairSet) {
        Map<Integer, String> pairEncodingMap = new HashMap<>();
        for(String s : pairSet) {
            System.out.println(key + " " + s);
            pairEncodingMap.put(key, s);
            key++;
        }
        
        return pairEncodingMap;
    }
    
    public void setToUpper(boolean toUpper) {
        this.toUpper = toUpper;
    }

    public void setFirstValue(int firstValue) {
        this.firstValue = firstValue;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public Map<Integer, String> getPairEncodingMap() {
        return pairEncodingMap;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
}
