/*
* Author: Logan Pettit
* Class: 4280 Compilers Professor Janikow
* Description: The main file to read input from the command line, if a file is provided use that as input
* if no file is provided allow keyboard input. */


import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        BufferedReader reader;
        Filter filter = new Filter();
        testTree tree = new testTree();
        Parser parser = new Parser();
        CodeGenerator codeGen = new CodeGenerator();

        String filename = "";
        String filebase = "";
        String line = "";
        String input;
        int lineCheck = 0;

        if (args.length > 1){
            System.out.println("INVOCATION ERROR: Provide 1 file as input or no file for keyboard input");
        }
        /*Set file extension to read from*/
        else if (args.length == 1) {
            filebase = args[0];
            filename = filebase + ".fs19";
        }
        /*If no file is provided*/
        else{

            try {
                System.out.println("Begin writing program end input pressing ENTER first then CTRL + D: ");

                /*Save the standard output stream*/
                PrintStream standard = System.out;

                /*Set output stream to the temp file*/
                PrintStream ofSteam = new PrintStream(new File("temp.fs19"));
                System.setOut(ofSteam);

                /*Read file input until a null line is provided,
                * this works differently on the command line vs ide*/
                try {
                    Scanner sc = new Scanner(System.in);

                   do {
                    input = sc.nextLine();
                    System.out.println(input);

                   } while(sc.hasNext());

                }catch (NoSuchElementException e){
                    System.setOut(standard);
                    System.out.println("EMPTY FILE ERROR: the file you are attempting to use as input is empty");
                    System.exit(-3);
                }

                System.setOut(standard);
                filename = "temp.fs19";
                filebase = "kb";

            }
            catch (IOException e){
                System.out.println("IOException: temporary output file cannot be open.");
                System.exit(-1);
            }

        }


        /*Try to catch a file not found*/
        try {
            reader = new BufferedReader(new FileReader(filename));

            /* read each line of file, filter the lines as they are read */
            while (line != null) {
                lineCheck += 1;
                line = reader.readLine();
                filter.putFileContentsInList(line);
            }

            /*Since first line will not be null due to eof check
             * if the file contains only 1 line*/
            if (lineCheck == 1) {
                System.out.println("EMPTY FILE ERROR: The file you are attempting to read from is empty");
                System.exit(-3);
            }
        } catch (IOException e) {
            System.out.println("IOException: File could not be found or open.");
            System.exit(-1);

        }


        /*parse data return tree and print*/
        tree = parser.starParser(filter.getCharacterArrayList());

        /*Preorder tree printer*/
        tree.printPreorder();

        /*Check static semantics of tree*/
        tree.checkTree();

        codeGen.fileName = filebase + ".asm";
        codeGen.startCodeGeneration(tree.root);
    }
}
