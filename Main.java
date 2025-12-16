/** 
 * Main.java
 * 
 * This file is the main entry point for the Zipf project.
 * You will make only minor changes to this file.
 * Make small changes and test frequently.
 * 
 * TODO: Follow the instructions in the comments, in particlar all todo items.
 * See the "Problems" tab below to review each item.
 */
package src.main.java;

import java.io.File;
import java.awt.event.*;
import java.awt.BorderLayout;
import javax.swing.*;
import java.util.*;

class Main extends JFrame {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 500;

    private Chart chart = null;

    public static void main(String[] args) throws InterruptedException {
        boolean console = false;

        Main program = new Main();

        if (args.length > 0) {
            if (args[0] != null && args[0].equals("--console")) {
                console = true;
            }
        }

        if (console) {
            // Part 1: console based
            program.run();
        }
        else {
            // Part 2: showGUI()
            program.showGUI();
        }
    }

    /* Part 1 */
    private void run() {
    
        // TODO: prompt the user for a file
        System.out.println("Input a File:");
        Scanner console = new Scanner(System.in);
        String hardcodedfile = "small/simple.txt";
        // try{ 
        //     File file = new File(console.nextLine());

        // }
        // catch (FileNotFoundException e){
             
        // }

        // Time the execution (be sure to do this after the file prompt and before processing)
        
        System.out.println("Running in console mode.");

        long startTime = System.nanoTime(); // Note printing can slow things down!
        Map zipfmap = new HashMap();
        Zipf zipthingy = new Zipf();
        System.out.println(zipthingy.processFolder(new File("larger")));
        // TODO: process the file using the Zipf object and print the Map yourself.
        
        // TODO: Once that works, convert the Map to List<WordCount> and printResults.
        
        // TODO: Once that works, convert the List<WordCount> to List<WordFreq> and printFreq.
    
        // EXTRA: Process files on separate threads (need to measure time in a different way)
    
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        System.out.println("Execution time: " + duration + " ms");
    }
    
    private void showGUI() throws InterruptedException {
        System.out.println("Running in GUI mode.");

        // Starts the UI Thread and creates the the UI in that thread.
        // Uses a Lambda Expression to call the createFrame method.
        // Use theGUI as the semaphore object
        SwingUtilities.invokeLater(() -> createFrame(this));

        // Have the main thread wait for the GUI Thread to be done
        // creating the frame and all panels.
        synchronized (this) {
            this.wait();
        }

        // Main Thread ends right away!
        System.out.println("Main thread is terminating.");
    }

    /**
     * Create the main JFrame.
     * 
     * @param semaphore The object to notify when complete
     */
    private void createFrame(Object semaphore) {
        // Sets the title found in the Title Bar of the JFrame
        this.setTitle("Zipf's Law");
        // Sets the size of the main Window
        this.setSize(WIDTH, HEIGHT);
        // Allows the application to properly close when the
        // user clicks on the Red-X.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addMenuBar();

        // We will use a layout manager so that our jPanel
        // will grow/shrink to fit the size of the jFrame
        this.setLayout(new BorderLayout());

        // create and add our JPanel
        chart = new Chart();
        // Position the chart to take the whole area by using BorderLayout.CENTER
        this.add(chart, BorderLayout.CENTER);
        chart.setVisible(true);

        // set up a keyboard listener
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                chart.keyReleased(ke.getKeyCode());
            }
        });
        // Set this JFrame to be visible, too
        this.setVisible(true);

        // tell the main thread that we are done creating our dialogs
        synchronized (semaphore) {
            semaphore.notify();
        }
    }

    /**
     * Add one menu option to allow the user initiate picking a directory.
     */
    private void addMenuBar() {

        JMenuBar bar = new JMenuBar();
        // Add the menu bar to the JFrame
        this.setJMenuBar(bar);

        // Add more top-level menu options for the specific animation panel
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        JMenuItem item = new JMenuItem("Pick dir...", 'P');
        item.addActionListener(e -> pickDir());
        menu.add(item);

        bar.add(menu);
    }

    // The EventHandler for File->pick dir...
    // Prompts the user to pick a folder/directory using the FileChooser dialog.
    private void pickDir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
        int retValue = chooser.showDialog(this, "Select");
        if (retValue == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            if (folder != null && folder.exists() && folder.isDirectory()) {
                // You may process the folder on a separate thread
                // as part of independent work
                Zipf zipf = new Zipf();
                List<WordFreq> wordFreq = zipf.processFolder(folder);

                // give this data to our chart
                chart.setData(wordFreq);
            }
        }
    }

    // Outputs to the console the full list of words and count.
    // This method is helpful for debugging.
    private void printResults(List<WordCount> results) {
        for (WordCount word : results) {
            System.out.println(word);
        }
    }

    // Outputs to the console the full list of words and frequency.
    // This method is helpful for debugging.
    private void printFreq(List<WordFreq> wordFreq) {
        for (WordFreq wf : wordFreq) {
            System.out.println(wf);
        }
    }

}
