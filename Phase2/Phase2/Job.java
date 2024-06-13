//*****************************************************************************
// Author: Jeffrey Norris
// Class: CS 3243-900 Operating Systems
// Due Date: 10/6/14
// Description:
//    Creates the Job class that stores information about each job in a more
//    accessable format, and stores the instructions of the job into an array.
//*****************************************************************************

public class Job {
   public static final String RESET = "\u001B[0m";
   public static final String BLACK = "\u001B[30m";
   public static final String RED = "\u001B[31m";
   public static final String GREEN = "\u001B[32m";
   public static final String YELLOW = "\u001B[33m";
   public static final String BLUE = "\u001B[34m";
   public static final String PURPLE = "\u001B[35m";
   public static final String CYAN = "\u001B[36m";
   public static final String WHITE = "\u001B[37m";
   public static final String BRIGHT_BLACK = "\u001B[90m";
   public static final String BRIGHT_RED = "\u001B[91m";
   public static final String BRIGHT_GREEN = "\u001B[92m";
   public static final String BRIGHT_YELLOW = "\u001B[93m";
   public static final String BRIGHT_BLUE = "\u001B[94m";
   public static final String BRIGHT_PURPLE = "\u001B[95m";
   public static final String BRIGHT_CYAN = "\u001B[96m";
   public static final String BRIGHT_WHITE = "\u001B[97m";

   // different variables of each job.
   public int jobNumber;
   public int numberOfLines;
   public int priority;
   public String[] instructions;
   public int add = 0;

   public Job(int num, int line, int prior) {
      jobNumber = num;
      numberOfLines = line;
      priority = prior;
      instructions = new String[line];
   }

   public int getJobNumber() {
      return jobNumber;
   }

   public int getNumberOfLines() {
      return numberOfLines;
   }

   public int getPriority() {
      return priority;
   }

   // Prints out the instructions of the Job.
   public void printInstructions() {
      System.out.println(
            BRIGHT_CYAN + "Job: " + BRIGHT_YELLOW + jobNumber + BRIGHT_CYAN + " Number of Lines: " + BRIGHT_YELLOW +
                  numberOfLines + BRIGHT_CYAN + " Priority: " + BRIGHT_YELLOW + priority + RESET);
      for (int i = 0; i < numberOfLines; i++) {
         if (instructions[i].length() == 0) {
            continue;
         }
         if (instructions[i].charAt(0) == '$')
            System.out.println(BRIGHT_GREEN + instructions[i] + RESET);
         else if (instructions[i].length() > 2 && instructions[i].charAt(0) == 'G' && instructions[i].charAt(1) == 'D')
            System.out.println(BRIGHT_BLUE + instructions[i] + RESET);
         else
            System.out.println(BRIGHT_PURPLE + instructions[i] + RESET);

      }
   }

   // Adds the next instruction and keeps track of where the next one will go.
   public void push(String instr) {
      instructions[add] = instr;
      add++;
   }

} 
 
 
 
 
  
  
  