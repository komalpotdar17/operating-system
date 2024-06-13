//*****************************************************************************
// Author: Jeffrey Norris
// Class: CS 3243-900 Operating Systems
// Due Date: 10/6/14
// Description:
//    Creates the class ReadFile that takes in a file path as a String. Then goes
//    through the text file and returns it to be stored into an array.
//*****************************************************************************

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class ReadFile {
    private String path;

    public ReadFile(String file_path) {
        path = file_path;
    }

    // ------------------------------------------------------
    // Goes through the given text file and store it line by
    // line into an array.
    // ------------------------------------------------------
    public String[] Openfile() throws IOException {
        FileReader fr = new FileReader(path);
        BufferedReader textReader = new BufferedReader(fr);

        int numberOfLines = readLines();
        String[] textData = new String[numberOfLines];

        int i;

        for (i = 0; i < numberOfLines; i++) {
            textData[i] = textReader.readLine();
        }

        textReader.close();
        return textData;
    }

    // ------------------------------------------------------
    // Figures out how many lines are in the text file so
    // an array can be sized to hold them exactly.
    // ------------------------------------------------------
    int readLines() throws IOException {
        FileReader file_to_read = new FileReader(path);
        BufferedReader bf = new BufferedReader(file_to_read);

        String aLine;
        int numberOfLines = 0;

        while ((aLine = bf.readLine()) != null) {
            numberOfLines++;
        }
        bf.close();

        return numberOfLines;
    }
}