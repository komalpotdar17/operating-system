import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class YashOS {
   // ANSI escape codes for colors
   public static final String RESET = "\u001B[0m";
   public static final String RED = "\u001B[31m";
   public static final String GREEN = "\u001B[32m";
   public static final String YELLOW = "\u001B[33m";
   public static final String BLUE = "\u001B[34m";
   public static final String BRIGHT_WHITE = "\u001B[97m";

   // Constants for TTL and TLL
   public static final long TTL = 60000; // Total Time Limit: 60 seconds
   public static final int TLL = 10000; // Total Line Limit: 1000 lines

   // Constants for paging
   public static final int PAGE_SIZE = 4096; // Page size: 4KB
   public static final int NUM_FRAMES = 1024; // Number of frames: 1024
   public static final int NUM_PAGES = NUM_FRAMES; // Number of pages

   public static void main(String[] args) {
      // Hardcode the file path here.
      String file_name = "ugradPart2.txt";

      // Initialize TTC and LLC
      long ttc = 0;
      int llc = 0;

      // Simulate a page table
      int[] pageTable = new int[NUM_PAGES];

      // Initialize RAM as an array of frames
      int[] ram = new int[NUM_FRAMES];

      // Initialize a queue for page replacement (FIFO)
      Queue<Integer> pageQueue = new LinkedList<>();

      try {
         // Record start time
         long startTime = System.currentTimeMillis();

         ReadFile file = new ReadFile(file_name);
         String[] allLines = file.Openfile();
         int size = allLines.length;

         // Starts the HDD to fill with jobs.
         ArrayList<Job> hdd = new ArrayList<>();
         int numberOfJobs = 0;

         // Breaks the text file into separate Jobs.
         for (int i = 0; i < size; i++) {
            String jobInfo = allLines[i];
            String[] data = jobInfo.split(",");
            if (data.length < 3) {
               // Handle the case where the line does not contain enough comma-separated values
               // System.out.println(RED + "Invalid line format: " + jobInfo + RESET);
               continue; // Skip processing this line
            }
            hdd.add(new Job(Integer.parseInt(data[0].substring(4)), Integer.parseInt(data[1].trim()),
                  Integer.parseInt(data[2].trim())));
            i++;
            for (int j = 0; j < hdd.get(numberOfJobs).getNumberOfLines(); j++) {
               hdd.get(numberOfJobs).push(allLines[i]);
               i++;
               llc++; // Increment LLC for each line processed
               // Check if LLC exceeds TLL
               if (llc > TLL) {
                  throw new RuntimeException("Exceeded Total Line Limit (TLL)");
               }
            }
            i--;
            numberOfJobs++;
         }

         // ------------------------------------------------------
         // Sends the Jobs to RAM depending on which LTS algorithm
         // is chosen.
         // ------------------------------------------------------

         ArrayList<Job> ramJobs = new ArrayList<>();

         System.out.println(BRIGHT_WHITE + "================================================" + RESET);
         System.out.println(YELLOW + "Sent to RAM by FIFO Algorithm" + RESET);
         System.out.println(BRIGHT_WHITE + "================================================\n" + RESET);
         ramJobs = ltsFIFO(hdd);
         for (Job job : ramJobs) {
            loadJobIntoRAM(job, pageTable, ram, pageQueue);
            job.printInstructions();
         }

         System.out.println(BRIGHT_WHITE + "\n================================================" + RESET);
         System.out.println(YELLOW + "Sent to RAM by SJF Algorithm" + RESET);
         System.out.println(BRIGHT_WHITE + "================================================\n" + RESET);
         ramJobs = ltsSJF(hdd);
         for (Job job : ramJobs) {
            loadJobIntoRAM(job, pageTable, ram, pageQueue);
            job.printInstructions();
         }

         System.out.println(BRIGHT_WHITE + "\n================================================" + RESET);
         System.out.println(YELLOW + "Sent to RAM by Priority Algorithm" + RESET);
         System.out.println(BRIGHT_WHITE + "================================================\n" + RESET);
         ramJobs = ltsPriority(hdd);
         for (Job job : ramJobs) {
            loadJobIntoRAM(job, pageTable, ram, pageQueue);
            job.printInstructions();
         }

         // Record end time
         long endTime = System.currentTimeMillis();

         // Calculate TTC
         ttc = endTime - startTime;

         // Output TTC
         System.out.println(BRIGHT_WHITE + "\nTotal Time Counter (TTC): " + ttc + " milliseconds" + RESET);

         // Check if TTC exceeds TTL
         if (ttc > TTL) {
            throw new RuntimeException("Exceeded Total Time Limit (TTL)");
         }

      } catch (IOException e) {
         System.out.println(RED + e.getMessage() + RESET);
      } catch (RuntimeException e) {
         System.out.println(RED + e.getMessage() + RESET);
      }
   }

   // ------------------------------------------------------
   // Different Long Term Scheduler algorithms.
   // ------------------------------------------------------

   // Sorts jobs on their job number so that the first job put in is the first job
   // out
   public static ArrayList<Job> ltsFIFO(ArrayList<Job> arr) {
      arr.sort((j1, j2) -> Integer.compare(j1.getJobNumber(), j2.getJobNumber()));
      return arr;
   }

   // Sorts jobs based on the number of lines, so that the shortest jobs are run
   // first.
   // Also sorts by priority, so that two Jobs of the same size, the higher
   // priority
   // will be run first.
   public static ArrayList<Job> ltsSJF(ArrayList<Job> arr) {
      arr.sort((j1, j2) -> {
         if (j1.getNumberOfLines() == j2.getNumberOfLines()) {
            return Integer.compare(j1.getPriority(), j2.getPriority());
         }
         return Integer.compare(j1.getNumberOfLines(), j2.getNumberOfLines());
      });
      return arr;
   }

   // Sorts jobs based on their priority so the highest priority will run first.
   // Also sort by size of job, so that two Jobs with the same priority, the
   // shortest will go before.
   public static ArrayList<Job> ltsPriority(ArrayList<Job> arr) {
      arr.sort((j1, j2) -> {
         if (j1.getPriority() == j2.getPriority()) {
            return Integer.compare(j1.getNumberOfLines(), j2.getNumberOfLines());
         }
         return Integer.compare(j1.getPriority(), j2.getPriority());
      });
      return arr;
   }

   // Method to load a job into RAM with paging
   public static void loadJobIntoRAM(Job job, int[] pageTable, int[] ram, Queue<Integer> pageQueue) {
      Random rand = new Random();
      int jobSize = job.getNumberOfLines(); // Number of lines in the job
      int numPagesNeeded = (int) Math.ceil((double) jobSize / PAGE_SIZE); // Calculate number of pages needed

      for (int i = 0; i < numPagesNeeded; i++) {
         int virtualPage = rand.nextInt(NUM_PAGES); // Generate a random virtual page number
         int physicalFrame = pageTable[virtualPage]; // Get the physical frame corresponding to the virtual page

         // Check if the page is in RAM
         if (physicalFrame == 0) {
            // Page fault detected
            if (!pageQueue.isEmpty()) {
               // Perform page replacement (FIFO)
               int replacedPage = pageQueue.poll(); // Get the page to be replaced (FIFO)
               pageTable[replacedPage] = 0; // Clear the corresponding entry in the page table
            }
            // Load the page into RAM
            physicalFrame = rand.nextInt(NUM_FRAMES); // Generate a random physical frame number
            pageTable[virtualPage] = physicalFrame; // Update the page table entry
            ram[physicalFrame] = 1; // Mark the frame as occupied
         }

         // Add the page to the page queue (FIFO)
         pageQueue.offer(virtualPage);
      }
   }
}
