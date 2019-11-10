package com.text.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.text.encoder.utils.FileUtils;

/**
 * Font Sheet Generator
 * 
 * This class will take a map representing the character encoding for the text
 * and generate a text file that can be easily copy pasted into an image
 * editor to create a Font Sheet.
 * 
 * @author TrekkiesUnite118
 *
 */
public class FontSheetTextGenerator {

    //File Name
    private String fileName;
    
    //Output path
    private String outputFilePath;
    
    public FontSheetTextGenerator fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
    
    public FontSheetTextGenerator outputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }

    /**
     * Generates the font sheet text file from the Pair Encoding Map.
     * 
     * @param pairEncodingMap
     * @throws IOException
     */
    public void generateFontSheetTextFile(Map<Integer, String> pairEncodingMap) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        /*
         * For each pair in our map, we are going to write it to a String Builder.
         * When we hit 16 characters, we move on to the next line to allow for 
         * easy copy pasting into a tile editor.
         */
        for(int i = 1; i < pairEncodingMap.size(); i++) {
            int iter = 0;
            StringBuilder sb = new StringBuilder();
            while(iter < 16) {
                sb.append(pairEncodingMap.get(i + iter));
                iter++;
                
            }
            sb.append("\n");
            out.write(sb.toString().getBytes());
            i += 15;
        }
        //Write to file.
        FileUtils.writeToFile(out.toByteArray(), fileName, outputFilePath);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }
    
   
}
