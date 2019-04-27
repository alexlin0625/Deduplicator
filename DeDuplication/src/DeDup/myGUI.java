package DeDup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import static DeDup.RunDedup.*;

public class myGUI extends JPanel {
    public static void main(String[] args) {


        JFrame frame = new JFrame("myGUI");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 1000);
        frame.setLocation(200, 0);
        JComponent newContentPane = new myGUI();
        newContentPane.setMinimumSize(new Dimension(1500,1000));
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);
        frame.pack();
        frame.setVisible(true);
    }

    public myGUI() {
        JPanel leftpanel = new JPanel(new BorderLayout());
        leftpanel.setBorder(BorderFactory.createRaisedBevelBorder());
        leftpanel.setPreferredSize(new Dimension(1100, 1000));
        leftpanel.setBorder(BorderFactory.createLineBorder(Color.green, 4));

        JPanel rightpanel = new JPanel(new BorderLayout());
        rightpanel.setPreferredSize(new Dimension(400, 1000));
        currentLocker = new JTextArea("Current Locker:"+"\n");
        currentLocker.setBorder(BorderFactory.createLineBorder(Color.green, 4));
        currentLocker.setFont(font);
        currentLocker.setPreferredSize(new Dimension(400,150));
        console.setFont(font3);
        console.setLineWrap(true);
        JScrollPane Scrollconsole = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Scrollconsole.setPreferredSize(new Dimension(400,850));
        rightpanel.add(currentLocker,BorderLayout.NORTH);
        rightpanel.add(Scrollconsole,BorderLayout.SOUTH);


        JPanel toppanel = new JPanel();
        toppanel.setLayout(new GridLayout(1, 3));
        toppanel.setPreferredSize(new Dimension(1100, 150));
        JButton selectlockerB = new JButton(SelectLocker);
        selectlockerB.setFont(font);
        selectlockerB.setActionCommand(SelectLocker);
        selectlockerB.addActionListener(new selectLockerListener());
        JButton createlockerB = new JButton(CreateLocker);
        createlockerB.setActionCommand(CreateLocker);
        createlockerB.addActionListener(new createLockerListener());
        createlockerB.setFont(font);
        selectlockerB.setBorder(BorderFactory.createRaisedBevelBorder());
        createlockerB.setBorder(BorderFactory.createRaisedBevelBorder());
        JButton cleardetailsB = new JButton(clearDetails);
        cleardetailsB.setFont(font);
        cleardetailsB.setActionCommand(clearDetails);
        cleardetailsB.addActionListener(new clearDetailsListener());
        toppanel.add(selectlockerB);
        toppanel.add(createlockerB);
        toppanel.add(cleardetailsB);


        JPanel midpanel = new JPanel();
        midpanel.setLayout(new GridLayout(1, 2));
        midpanel.setPreferredSize(new Dimension(1100, 600));
        contents.setBackground(Color.cyan);
        contents.setLineWrap(true);
        contents.setFont(font3);
        contents.setEditable(false);
        JScrollPane Scrollcontents = new JScrollPane(contents, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        details.setBackground(Color.yellow);
        details.setLineWrap(true);
        details.setFont(font3);
        details.setEditable(false);
        JScrollPane Scrolldetails = new JScrollPane(details, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        midpanel.add(Scrollcontents);
        midpanel.add(Scrolldetails);

        JPanel bottompanel = new JPanel();
        bottompanel.setLayout(new GridLayout(3, 1));
        bottompanel.setPreferredSize(new Dimension(1100, 250));
        JPanel progressPanel =  new JPanel();
        progressPanel.setLayout(new GridLayout(2, 1));
        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        JLabel progressLabel = new JLabel("upload progress");
        progressPanel.add(progressLabel);
        progressPanel.add(progressBar);
        bottompanel.add(progressPanel);

        JPanel filepanel = new JPanel();
        filepanel.setLayout(new GridLayout(1, 3));
        JButton storefileB = new JButton(StoreFile);
        storefileB.setActionCommand(StoreFile);
        storefileB.addActionListener(new storeFileListener());
        storefileB.setFont(font2);
        storefileB.setBorder(BorderFactory.createRaisedBevelBorder());
        JButton deletefileB = new JButton(DeleteFile);
        deletefileB.setFont(font2);
        deletefileB.setBorder(BorderFactory.createRaisedBevelBorder());
        deletefileB.setActionCommand(DeleteFile);
        deletefileB.addActionListener(new deleteFileListener());
        JButton retrievefileB = new JButton(RetrieveFile);
        retrievefileB.setActionCommand(RetrieveFile);
        retrievefileB.addActionListener(new retrieveFileListener());
        retrievefileB.setFont(font2);
        retrievefileB.setBorder(BorderFactory.createRaisedBevelBorder());
        filepanel.add(storefileB);
        filepanel.add(deletefileB);
        filepanel.add(retrievefileB);
        bottompanel.add(filepanel);

        JPanel pathpanel = new JPanel();
        pathpanel.setLayout(new BorderLayout());
        JButton selectpathB = new JButton(SelectOutputPath);
        selectpathB.setPreferredSize(new Dimension(300, 100));
        selectpathB.setFont(font3);
        selectpathB.setBorder(BorderFactory.createRaisedBevelBorder());
        selectpathB.setActionCommand(SelectOutputPath);
        selectpathB.addActionListener(new selectOutputListener());
        pathpanel.add(selectpathB, BorderLayout.WEST);
        JLabel pathL = new JLabel(" Retrieved file Path:  ");
        pathL.setFont(font3);
        pathL.setPreferredSize(new Dimension(300, 100));
        pathpanel.add(pathL, BorderLayout.CENTER);
        //output.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        output.setFont(font3);
        output.setPreferredSize(new Dimension(500, 100));
        output.setEditable(false);
        pathpanel.add(output, BorderLayout.EAST);
        bottompanel.add(pathpanel);


        leftpanel.add(toppanel, BorderLayout.NORTH);
        leftpanel.add(midpanel, BorderLayout.CENTER);
        leftpanel.add(bottompanel, BorderLayout.SOUTH);

        add(leftpanel, BorderLayout.WEST);
        add(rightpanel, BorderLayout.EAST);
    }

    private static void ConsoleDisplay(String s) {
        console.append(s + "\n");
        console.setCaretPosition(0);
    }
    class selectOutputListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(SelectOutputPath)){
                ConsoleDisplay("selecting output path:");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showDialog(fileChooser, "select");
                String selectedPath = fileChooser.getSelectedFile().getPath() ;
                outputpath = selectedPath;
                ConsoleDisplay("Output path is selected as " + selectedPath);
                output.setText(outputpath);
            }
        }
    }
    class storeFileListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(StoreFile)) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setMultiSelectionEnabled(true);
                ConsoleDisplay("storing file(s):");
                fileChooser.showDialog(fileChooser, StoreFile);
                chosenFiles = fileChooser.getSelectedFiles();
                int i = 1;
                try {
                    for(File currFile : chosenFiles){
                        int l = chosenFiles.length;
                        String filename = currFile.getName();
                        String currFilepath = currFile.getAbsolutePath();
                        MyFile fileExisted = locker.searchFileInLocker(filename);
                        h = new HashTheChunks();
                        int fileSize = h.getFileSize(currFile);
                        details.append("File Name: " + filename + "\n");
                        details.append("File Size: " + fileSize + "\n");

                        String extension = h.getFileExtension(currFile);
                        details.append("File Extension: " + extension + "\n");
                        if (extension != null && (h.isVideo(extension) == true || h.isImage(extension) == true) || h.isPDF(extension) == true) {
                            if (fileExisted != null) {
                                ConsoleDisplay(filename + " is already in locker." + " start deduplication");
                                // start referencing the chunks
                            } else {
                                long timerStart;
                                long timerEnd;
                                ConsoleDisplay("Storing " + filename + " using fixed size chunking" );
                                timerStart = System.currentTimeMillis();
                                int chunkSize = h.getChunksize(extension, fileSize);
                                long lockerSize = locker.getcurrLockerSize();
                                long updateSize = h.fixedSizeChunk(locker, currFilepath, chunkSize);

                                long difference = updateSize - lockerSize;
                                details.append("Current locker size: " + updateSize +"\n");
                                details.append("Deduped Size: " + difference+ "\n");
                                details.append("Current DeDuplication Ratio: "+locker.getDedupRatio()+"%"+ "\n");
                                ConsoleDisplay(filename + " is added successfully");
                                timerEnd = System.currentTimeMillis();
                                details.append("Time used to store file <<" + filename + ">> is " + (double) (timerEnd - timerStart)/1000 + " seconds."+ "\n"+ "\n");
                                contents.append(filename + "\n");
                                exportLocker(locker,lockerPath);

                            }
                        } else if (extension != null && h.isText(extension) == true){
                            if (fileExisted != null) {
                                ConsoleDisplay(filename + " is already in locker." + " start deduplication");
                                // start referencing the chunks
                            } else {
                                long timerStart;
                                long timerEnd;
                                ConsoleDisplay("Storing " + filename + " using dynamic size chunking"  );
                                timerStart = System.currentTimeMillis();
                                int chunkSize = h.getChunksize(extension, fileSize);
                                long lockerSize = locker.getcurrLockerSize();
                                long updateSize = h.dynamicSizeChunk(locker, currFilepath, chunkSize);

                                long difference = updateSize - lockerSize;
                                details.append("Current locker size: "+ updateSize+ "\n");
                                details.append("Deduped Size: "+difference+ "\n");
                                details.append("Current DeDuplication Ratio: "+locker.getDedupRatio()+"%"+ "\n");
                                details.append("Current dictionary size: " +locker.getDictionarySize()+ "\n");
                                ConsoleDisplay(filename + " is added successfully");
                                timerEnd = System.currentTimeMillis();
                                details.append("Time used to store file <<" + filename + ">> is " + (double) (timerEnd - timerStart)/1000 + " seconds."+ "\n"+ "\n");
                                contents.append(filename + "\n");
                                exportLocker(locker,lockerPath);

                            }
                        }
                        progressBar.setMaximum(l);
                        progressBar.setValue(i);
                        i+=1;

                    }
                }catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }

    class retrieveFileListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(RetrieveFile)) {
                String FileName = JOptionPane.showInputDialog("Please Enter name of the file you want to retrieve:");
                    if (locker.sameFileNameExists(FileName)) {
                        long timerStart;
                        long timerEnd;
                        timerStart = System.currentTimeMillis();
                        try {
                            locker.retrieveTheFile(FileName,outputpath);
                            timerEnd = System.currentTimeMillis();
                            ConsoleDisplay(FileName + " file has been retrieved successfully");
                            ConsoleDisplay("Time used to retrieve file <<" + FileName + ">> is " + (double) (timerEnd - timerStart)/1000 + " seconds.");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }


                    } else {
                        ConsoleDisplay("Could not find file you want to retrieve.");
                    }
            }
        }
    }


    class deleteFileListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(DeleteFile)) {
                String FileName = JOptionPane.showInputDialog("Please Enter name of the file you want to delete:");
                if (locker.sameFileNameExists(FileName)) {
                    locker.deleteFile(FileName);
                    ConsoleDisplay(FileName + " file is deleted from file locker.");
                   // String content = contents.getText();
                 //   content = content.replace(FileName+"\n","");
                  //  contents.setText(content);
                    contents.setText("Locker contents: "+ "\n");
                    for(MyFile file : locker.files){
                        contents.append(file.getmyFileName() + "\n");
                    }
                    try {
                        exportLocker(locker,lockerPath);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    ConsoleDisplay("Could not find file you want to delete.");
                }

            }
        }
    }
    class createLockerListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(CreateLocker)){
                ConsoleDisplay("creating locker:");
                String lockerName = JOptionPane.showInputDialog("Please input a name:");
                if(lockerName.equalsIgnoreCase("")){
                    return;
                }
                else{
                    locker = new MyLocker(lockerName, new HashMap<>(), new ArrayList<>());
                    ConsoleDisplay("please specify locker address:");
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    fileChooser.showDialog(fileChooser, "select");
                    lockerPath = fileChooser.getSelectedFile().getPath();
                    try {
                        exportLocker(locker,lockerPath);

                    }catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    currentLocker.setText("Current Locker: " +"\n" +lockerName);
                    contents.setText("Locker contents: "+ "\n");
                    for(MyFile file : locker.files){
                        contents.append(file.getmyFileName() + "\n");
                    }
                }


            }
        }
    }

    class selectLockerListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(SelectLocker)) {
                ConsoleDisplay("loading locker:");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showDialog(fileChooser, "select");

                lockerPath = fileChooser.getSelectedFile().getPath() ;
                try{
                    locker = importLocker(lockerPath);
                    String lockerName = lockerPath.substring(lockerPath.lastIndexOf(File.separator) + 1);
                    lockerPath = lockerPath.replace(File.separator+lockerName,"");
                    ConsoleDisplay("successfully load locker: " + lockerName);
                    currentLocker.setText("Current Locker: " +"\n"+ lockerName);
                    contents.setText("Locker contents: "+ "\n");
                    for(MyFile file : locker.files){
                        contents.append(file.getmyFileName() + "\n");
                    }
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                    ConsoleDisplay("Could not find that locker.");
                }
                catch (ClassNotFoundException e1){
                    e1.printStackTrace();

                }


            }
        }
    }

    class clearDetailsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals(clearDetails)) {
                details.setText("Deduplication Details:"+ "\n");
            }
        }
    }




    private static JTextField output = new JTextField("C:\\Users\\41410\\Desktop\\output");
    private static JTextArea console = new JTextArea();
    private static String StoreFile = "Store File";
    private static String clearDetails = "Clear Details";
    private static String CreateLocker = "Create Locker";
    private static String RetrieveFile = "Retrieve File";
    private static String DeleteFile = "Delete File";
    private static String SelectOutputPath = "Select Retrieved file Path";
    private static String SelectLocker = "Select Locker";
    private static JFileChooser fileChooser = new JFileChooser();
    private static File[] chosenFiles;
    private static String FilePath;
    private static JTextArea contents = new JTextArea("Locker Contents:"+"\n");
    private static JTextArea details = new JTextArea("Deduplictaion Details:"+"\n");
    private static Font font = new Font("Arial", Font.BOLD, 30);
    private static Font font2 = new Font("Arial", Font.BOLD, 20);
    private static Font font3 = new Font("Arial", Font.BOLD, 15);
    private static JTextArea currentLocker;
    private static MyLocker locker;
    private static HashTheChunks h;
    private static JProgressBar progressBar;
    private static String outputpath = "C:\\Users\\41410\\Desktop\\output";
    private static HashMap<String,MyLocker> lockers = new HashMap<>();
    private static String lockerPath;
}

