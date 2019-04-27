package DeDup;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;
import java.util.*;

public class RunDedup {

    ////////////////////////////////////////
    ////// *Complete User Interface* ///////
    //*Run java RunDedup.java in cmd line*//
    ////////////////////////////////////////

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException{
        Scanner user_input = new Scanner(System.in).useDelimiter("\n");
        HashTheChunks h = new HashTheChunks();
        String input;

        System.out.println("Create new locker [create] || Load existing locker [load] || Terminate [end]");
        input = user_input.next();
        while (!(input.equalsIgnoreCase("create")) && !(input.equalsIgnoreCase("load")) && !(input.equalsIgnoreCase("end"))) {
            System.out.println("Invalid input. Please try again. \nCreate new locker [create] || Load existing locker [load] || Terminate [end]");
            input = user_input.next();
        }

        MyLocker locker = null;
        String lockerName = "";
        if(input.equalsIgnoreCase("create")){
            System.out.println("Please enter a name for your new Locker: ");
            lockerName = user_input.next();
            locker = new MyLocker(lockerName, new HashMap<>(), new ArrayList<>());
            System.out.println("Locker " + lockerName + " is created!");
        }
        else if(input.equalsIgnoreCase("load")) {
            System.out.println("Loading Locker from default path [default] || Import Locker from specified path [import]");
            input = user_input.next();

            while (!input.equalsIgnoreCase("default") && !input.equalsIgnoreCase("import")) {
                System.out.println("Invalid input. Please try again. \n" + "Loading Locker from default path [default] || Import Locker from specified path [import]");
                input = user_input.next();
            }
            if (input.equalsIgnoreCase("default")) {
                boolean validImportLockerName = false;
                while (validImportLockerName == false) {
                    System.out.println("Please enter the name of the existing locker: ");
                    input = user_input.next();
                    lockerName = input;
                    try {
                        locker = importSerializedLocker(lockerName);
                        validImportLockerName = true;
                        System.out.println(lockerName + " loaded successfully!");
                    } catch (Exception e) {
                        System.out.println("Locker " + lockerName + " does not exist. Please try again.");
                    }
                }
            } else if (input.equalsIgnoreCase("import")) {
                boolean validImportLocker = false;
                System.out.println("Please enter valid path of the Locker you are importing: ");
                while (validImportLocker == false) {
                    input = user_input.next();
                    File importPath = new File(input);
                    while (importPath.isDirectory()) {
                        System.out.println("Import path is invalid. Please try again. \n" + "Please enter valid path of the Locker you are importing:");
                        input = user_input.next();
                        importPath = new File(input);
                    }
                    lockerName = input.substring(input.lastIndexOf(File.separator) + 1);
                    try {
                        locker = importLocker(input);
                        validImportLocker = true;
                        System.out.println(lockerName + " imported successfully!");
                    } catch (Exception e) {
                        System.out.println("Locker " + lockerName + " does not exist. Please try again. \n" + "Please enter valid path of the Locker you are importing:");
                    }
                }
            }
        }
        else if(input.equalsIgnoreCase("end")){
            return;
        }

        // choose operations to operate locker
        while (!user_input.equals("finish")) {
            do {
                System.out.println("Please enter store, retrieve, delete, check files, or finish: ");
                input = user_input.next();
                if (locker.checkEmpty()) {
                    if (input.equalsIgnoreCase("retrieve") || input.equalsIgnoreCase("delete") || input.equalsIgnoreCase("check files")) {
                        System.out.println("Locker is currently empty! Please store a file first");
                        input = "";
                    }
                }
            } while (input.isEmpty()
                    && !input.equalsIgnoreCase("store")
                    && !input.equalsIgnoreCase("retrieve")
                    && !input.equalsIgnoreCase("delete")
                    && !input.equalsIgnoreCase("check files")
                    && !input.equalsIgnoreCase("finish"));

            if (input.equalsIgnoreCase("store")) {
                do {
                    System.out.println("Are you storing multiple files in a directory or individual file? [multiple/single]");
                    input = user_input.next();
                    // for doing single or directory
                } while (!input.equalsIgnoreCase("multiple") && !input.equalsIgnoreCase("single"));

                //Single file storage
                if (input.equalsIgnoreCase("single")) {
                    System.out.println("Please enter path of the file");
                    String filePath = user_input.next();
                    File file = new File(filePath);
                    String filename = file.getName();
                    MyFile fileExisted = locker.searchFileInLocker(filename);

                    // calls getFileSize to get the file size
                    int fileSize = h.getFileSize(file);
                    System.out.println("File Size : " + (fileSize/1000) + "kb");

                    // calls getFileExtension to get the file type
                    String extension = h.getFileExtension(file);

                    long timerStart;
                    long timerEnd;
                    //Single file storage: Fix sized chunking
                    if (extension != null && (h.isVideo(extension) == true || h.isImage(extension) == true) || h.isPDF(extension) == true) {
                        if (fileExisted != null) {
                            System.out.println("File name: " + filename + " exists in locker. Would you like to replace the file? (Y/N)");
                            input = user_input.next();
                            if (input.equalsIgnoreCase("Y")) {
                                Path p = FileSystems.getDefault().getPath(filePath);
                                byte[] bytesOfNewFile = Files.readAllBytes(p);
                                String hashOfNewFile = h.hashTheContent(bytesOfNewFile);
                                String hashOfExistedFile = fileExisted.getHash();
                                long sizeOfNewFile = bytesOfNewFile.length;

                                if (!(hashOfNewFile.equals(hashOfExistedFile) && (sizeOfNewFile == fileExisted.getSize()))) {
                                    timerStart = System.currentTimeMillis();
                                    locker.deleteFile(filename);
                                    manageFixSizeChunking(locker, filename, filePath, extension, fileSize, h);
                                    timerEnd = System.currentTimeMillis();
                                    System.out.println("Time used to store file <<" + filename + ">> via Fix Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                    System.out.println("-------------------------------------" + "\n");
                                } else {
                                    System.out.println("Exact same file exists. Nothing is done.");
                                }
                            }
                        } else {
                            timerStart = System.currentTimeMillis();
                            manageFixSizeChunking(locker, filename, filePath, extension, fileSize, h);
                            timerEnd = System.currentTimeMillis();
                            System.out.println("Time used to store file <<" + filename + ">> via Fix Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                            System.out.println("-------------------------------------" + "\n");
                        }
                    }
                    //Single file storage: Dynamic sized chunking
                    else if (extension != null && h.isText(extension) == true) {
                        if (fileExisted != null) {
                            System.out.println("File name: " + filename + " exists in locker. Would you like to replace the file? (Y/N)");
                            input = user_input.next();
                            if (input.equalsIgnoreCase("Y")) {
                                Path p = FileSystems.getDefault().getPath(filePath);
                                byte[] bytesOfNewFile = Files.readAllBytes(p);
                                String hashOfNewFile = h.hashTheContent(bytesOfNewFile);
                                String hashOfExistedFile = fileExisted.getHash();
                                long sizeOfNewFile = bytesOfNewFile.length;

                                if (!(hashOfNewFile.equals(hashOfExistedFile) && (sizeOfNewFile == fileExisted.getSize()))) {
                                    timerStart = System.currentTimeMillis();
                                    locker.deleteFile(filename);
                                    manageDynamicSizeChunking(locker, filename, filePath, extension, fileSize, h);
                                    timerEnd = System.currentTimeMillis();
                                    System.out.println("Time used to store file <<" + filename + ">> via Dynamic Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                    System.out.println("-------------------------------------" + "\n");
                                } else {
                                    System.out.println("Exact same file exists. Nothing is done.");
                                }
                            }
                        } else {
                            timerStart = System.currentTimeMillis();
                            manageDynamicSizeChunking(locker, filename, filePath, extension, fileSize, h);
                            timerEnd = System.currentTimeMillis();
                            System.out.println("Time used to store file <<" + filename + ">> via Dynamic Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                            System.out.println("-------------------------------------" + "\n");
                        }
                    }
                }
                //Multiple file storage
                else if (input.equalsIgnoreCase("multiple")) {
                    Queue<File> storeQueue = new LinkedList<>();
                    System.out.println("Please enter path of the directory");
                    String directoryPath = user_input.next();
                    File directoryFile = new File(directoryPath);
                    if (!directoryFile.isDirectory()) {
                        System.out.println("This is not a directory");
                    } else {
                        for (File file : directoryFile.listFiles()) {
                            storeQueue.offer(file);
                        }
                        while (!storeQueue.isEmpty()) {
                            File currFile = storeQueue.poll();
                            String filename = currFile.getName();
                            String currFilepath = currFile.getAbsolutePath();
                            MyFile fileExisted = locker.searchFileInLocker(filename);

                            int fileSize = h.getFileSize(currFile);
                            System.out.println("File Size : " + fileSize);

                            String extension = h.getFileExtension(currFile);

                            long timerStart;
                            long timerEnd;
                            //Multiple file storage: Fixed size chunking
                            if (extension != null && (h.isVideo(extension) == true || h.isImage(extension) == true) || h.isPDF(extension) == true) {
                                if (fileExisted != null) {
                                    System.out.println("File name: " + filename + " exists in locker. Would you like to replace the file? (Y/N)");
                                    input = user_input.next();
                                    if (input.equalsIgnoreCase("Y")) {
                                        Path p = FileSystems.getDefault().getPath(currFilepath);
                                        byte[] bytesOfNewFile = Files.readAllBytes(p);
                                        String hashOfNewFile = h.hashTheContent(bytesOfNewFile);
                                        String hashOfExistedFile = fileExisted.getHash();
                                        long sizeOfNewFile = bytesOfNewFile.length;

                                        if (!(hashOfNewFile.equals(hashOfExistedFile) && (sizeOfNewFile == fileExisted.getSize()))) {
                                            timerStart = System.currentTimeMillis();
                                            locker.deleteFile(filename);
                                            manageFixSizeChunking(locker, filename, currFilepath, extension, fileSize, h);
                                            timerEnd = System.currentTimeMillis();
                                            System.out.println("Time used to store file <<" + filename + ">> via Fix Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                            System.out.println("-------------------------------------" + "\n");
                                        } else {
                                            System.out.println("Exact same file exists. Nothing is done.");
                                        }
                                    }
                                } else {
                                    timerStart = System.currentTimeMillis();
                                    manageFixSizeChunking(locker, filename, currFilepath, extension, fileSize, h);
                                    timerEnd = System.currentTimeMillis();
                                    System.out.println("Time used to store file <<" + filename + ">> via Fix Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                    System.out.println("-------------------------------------" + "\n");
                                }
                            }
                            //Multiple file storage: Dynamic size chunking
                            else if (extension != null && h.isText(extension) == true) {
                                if (fileExisted != null) {
                                    System.out.println("File name: " + filename + " exists in locker. Would you like to replace the file? (Y/N)");
                                    input = user_input.next();
                                    if (input.equalsIgnoreCase("Y")) {
                                        Path p = FileSystems.getDefault().getPath(currFilepath);
                                        byte[] bytesOfNewFile = Files.readAllBytes(p);
                                        String hashOfNewFile = h.hashTheContent(bytesOfNewFile);
                                        String hashOfExistedFile = fileExisted.getHash();
                                        long sizeOfNewFile = bytesOfNewFile.length;

                                        if (!(hashOfNewFile.equals(hashOfExistedFile) && (sizeOfNewFile == fileExisted.getSize()))) {
                                            timerStart = System.currentTimeMillis();
                                            locker.deleteFile(filename);
                                            manageDynamicSizeChunking(locker, filename, currFilepath, extension, fileSize, h);
                                            timerEnd = System.currentTimeMillis();
                                            System.out.println("Time used to store file <<" + filename + ">> via Dynamic Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                            System.out.println("-------------------------------------" + "\n");
                                        } else {
                                            System.out.println("Exact same file exists. Nothing is done.");
                                        }
                                    }
                                } else {
                                    timerStart = System.currentTimeMillis();
                                    manageDynamicSizeChunking(locker, filename, currFilepath, extension, fileSize, h);
                                    timerEnd = System.currentTimeMillis();
                                    System.out.println("Time used to store file <<" + filename + ">> via Dynamic Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                    System.out.println("-------------------------------------" + "\n");
                                }
                            }
                        }
                    }
                }

                } else if (input.equalsIgnoreCase("retrieve")) {
                    long timerStart;
                    long timerEnd;
                    System.out.println("Enter the path in which you want to store the retrieved file. ");

                    String outputPath = user_input.next();
                    File rPath = new File(outputPath);
                    while (!rPath.isDirectory()) {
                        System.out.println("The path you entered is invalid. Please try again. ");
                        outputPath = user_input.next();
                        rPath = new File(outputPath);
                    }
                    System.out.println("Enter name of the file you want to retrieve");
                    String retrievalFileName;
                    do {
                        retrievalFileName = user_input.next();
                        if (locker.sameFileNameExists(retrievalFileName)) {
                            timerStart = System.currentTimeMillis();
                            locker.retrieveTheFile(retrievalFileName, outputPath);
                            timerEnd = System.currentTimeMillis();
                            System.out.println("Time used to retrieve file <<" + retrievalFileName + ">> is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                            System.out.println(retrievalFileName + " file has been retrieved successfully");
                            System.out.println("-------------------------------------" + "\n");
                        } else {
                            System.out.println("Could not find file you want to retrieve. Please try again.");
                        }
                    }
                    while (!locker.sameFileNameExists(retrievalFileName));

                } else if (input.equalsIgnoreCase("delete")) {
                    long timerStart;
                    long timerEnd;
                    System.out.println("Enter name of the file you want to delete.");
                    String deletionFileName;
                    do {
                        deletionFileName = user_input.next();
                        if (locker.sameFileNameExists(deletionFileName)) {
                            timerStart = System.currentTimeMillis();
                            locker.deleteFile(deletionFileName);
                            timerEnd = System.currentTimeMillis();
                            System.out.println("Time used to delete file <<" + deletionFileName + ">> is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                            System.out.println(deletionFileName + " file deleted from file locker.");
                            System.out.println("-------------------------------------" + "\n");
                        } else {
                            System.out.println("Could not find file you want to delete.");
                        }
                    }
                    while (deletionFileName.equals("finish"));

                } else if (input.equalsIgnoreCase("check files")) {
                    locker.printFilesInLocker();

                } else if (input.equalsIgnoreCase("finish")) {
                    System.out.println("Would you like to export your current locker <" + lockerName + ">? [Y/N]");
                    input = user_input.next();
                    while(!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")){
                        System.out.println("Input invalid. Please try again. \n" + "Would you like to export your current locker <" + lockerName + ">? [Y/N]");
                        input = user_input.next();
                    }
                    if(input.equalsIgnoreCase("Y")){
                        System.out.println("Please enter a valid path to store the exported locker.");
                        input = user_input.next();
                        String exportPath = input;
                        File exportedPath = new File(exportPath);
                        while(!exportedPath.isDirectory()){
                            System.out.println("Invalid path. Please try again.");
                            exportPath = user_input.next();
                            exportedPath = new File(exportPath);
                        }
                        exportLocker(locker, exportPath);
                        System.out.println("Locker has been successfully exported into the path: " + exportPath);
                    }
                    exportSerializedLocker(locker);
                    locker.printMyLockerStats();
                    user_input.close();
                    break;
                }
        }
        user_input.close();
    }

    ///////////////////////////////////////
    ////////// *Chunking Helpers* /////////
    ///////////////////////////////////////

    public static void manageFixSizeChunking(MyLocker locker, String filename, String filePath, String extension, int fileSize, HashTheChunks h) throws IOException, NoSuchAlgorithmException{
        System.out.println("Storing " + filename + " using fixed size chunking");
        int chunkSize = h.getChunksize(extension, fileSize);
        h.fixedSizeChunk(locker, filePath, chunkSize);
        System.out.println(filename + " is added successfully");
    }

    public static void manageDynamicSizeChunking(MyLocker locker, String filename, String filePath, String extension, int fileSize, HashTheChunks h) throws IOException, NoSuchAlgorithmException {
        System.out.println("Storing " + filename + " using dynamic size chunking");
        int chunkSize = h.getChunksize(extension, fileSize);
        h.dynamicSizeChunk(locker, filePath, chunkSize);
        System.out.println(filename + " is added successfully");
    }

    ///////////////////////////////////////
    ////// *Import & Export Methods* //////
    ///////////////////////////////////////

    protected static void exportSerializedLocker(MyLocker locker) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(locker.getLockerName()));
        oos.writeObject(locker);
    }

    protected static MyLocker importSerializedLocker(String lockerName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(lockerName));
        MyLocker loadedLocker = (MyLocker) ois.readObject();
        return loadedLocker;
    }

    protected static void exportLocker(MyLocker locker, String exportPath) throws IOException{
        FileOutputStream fos = new FileOutputStream(exportPath + File.separator + locker.getLockerName());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(locker);
    }

    protected static MyLocker importLocker(String importPath) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(importPath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        MyLocker loadedLocker = (MyLocker) ois.readObject();
        return loadedLocker;
    }
}
