package DeDup;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class MyLocker implements Serializable {
    private final String lockerName;
    private HashMap<String, byte[]> dictionary;
    protected ArrayList<MyFile> files;
    private long lockerSize;
    private long OriginalTotalfilesSize;

    // set up the constructors
    protected MyLocker(String lockerName, HashMap<String, byte[]> dictionary, ArrayList<MyFile> files) {
        this.lockerName = lockerName;
        this.dictionary = dictionary;
        this.files = files;
        this.lockerSize = 0L;

    }
    // check if locker is empty
    protected boolean checkEmpty(){
        return this.files.isEmpty();
    }

    // call this function in HashTheChunks.java to store file's info after processed chunks
    protected void addFileToLocker(String fileName, int size, String hash, ArrayList<String> fileRetriever, LinkedHashSet<String> hashSet, long actualStoredSize) {
        this.files.add(new MyFile(fileName, size, hash, fileRetriever, hashSet, actualStoredSize));
    }

    // call this function in HashTheChunks.java to store chunks of file into dictionary after processed chunks
    protected void addChunksToDictionary(String hashOutput, byte[] chunk) {
        this.dictionary.put(hashOutput, chunk);
    }

    // check if the current chunk exists in dictionary. If not, we store it.
    protected boolean chunkHashExist (String hashOutput){
        if(this.dictionary.containsKey(hashOutput)) return true;
        return false;
    }

    // retrieve method
    protected boolean retrieveTheFile(String filename, String outputPath) throws IOException {
        MyFile fileToBeRetrieved = searchFileInLocker(filename);
        ArrayList<String> retrievedFileArrayList = fileToBeRetrieved.getFileRetriever();
        // File.separator for cross-platform file path format for Mac OS and windows users
        File file = new File(outputPath + File.separator +"retrieved_"+filename);
        FileOutputStream fos = new FileOutputStream(file);
        for (int i = 0; i < retrievedFileArrayList.size(); i++) {
            fos.write(this.dictionary.get(retrievedFileArrayList.get(i)));
        }
        fos.close();
        return true;
    }

    // search for the file, called in retrieveTheFile
    protected MyFile searchFileInLocker(String filename){
        // Identify if filename already exists
        for (MyFile file : this.files){
            if (filename.equalsIgnoreCase(file.getmyFileName())){
                return file;
            }
        }
        return null;
    }

    // check if locker contain a file with same name
    protected boolean sameFileNameExists(String fileName){
        for(MyFile f : this.files){
            if(fileName.equalsIgnoreCase(f.getmyFileName())){
                return true;
            }
        }
        return false;
    }

    // file deletion
    protected boolean deleteFile(String fileName){
        MyFile f = searchFileInLocker(fileName);
        if(f == null){
            System.out.println("File not found.");
            return false;
        }

        for(String reconstructorString : f.getFileRetriever()){
            for(MyFile ff : this.files){
                if(ff != f){
                    if(!(ff.getHashSet().contains(reconstructorString))) {
                        this.dictionary.remove(reconstructorString);
                    }
                }
            }
        }
        deductSizeFromChunkLocker(f.getActualStoredSize());
        deductFromLockerSize(f.getSize());
        this.files.remove(f);
        System.out.println("Current locker size is now: " + (getcurrLockerSize()/1000) +"kb");
        return true;
    }

    ///////////////////////////////////////
    // **Locker Stats checking methods* ///
    ///////////////////////////////////////

    protected String getLockerName(){
        return this.lockerName;
    }

    protected long getcurrLockerSize(){
        return this.lockerSize;
    }

    protected long getOriginalTotalfilesSize(){
        return this.OriginalTotalfilesSize;
    }

    protected void addToOriginalFilesSize(int size){
        this.OriginalTotalfilesSize += size;
    }

    protected void deductSizeFromChunkLocker(long size) { this.OriginalTotalfilesSize -= size; }

    protected long addToLockerSize(long size){
        long currSize = this.lockerSize += size;
        return currSize;
    }

    protected void deductFromLockerSize(long size) { this.lockerSize -= size; }

    protected float getDedupRatio() {
        float division = (float)getcurrLockerSize() / (float)getOriginalTotalfilesSize();
        float ratio = 100 - (division*100);
        return ratio;
    }

    protected int getDictionarySize(){
        return this.dictionary.size();
    }

    protected void printFilesInLocker(){
        System.out.println("====== Current files in your file locker ======");
        for(MyFile file : this.files){
            System.out.println("---    " + file.getmyFileName() + "   (original size: " + (file.getSize()/1000) + "kb)" + "   (deduped size: " + (file.getActualStoredSize()/1000) + "kb)" + "    ---");
        }
    }
    protected void printMyLockerStats(){
        int filecounter = 1;
        System.out.println("\n" + "\n");
        System.out.println("***************************");
        System.out.println("==== Locker Name ====");
        System.out.println(this.getLockerName() + "\n");
        System.out.println("==== Size of Locker ====");
        System.out.println("Original files size: " + (this.getOriginalTotalfilesSize() / 1000) + "kb");
        System.out.println("Deduped size: " + (this.getcurrLockerSize() / 1000) +"kb"+ "\n");
        System.out.println("==== Files ====");
        System.out.println("Number of files: " + files.size() + "\n");
        for(MyFile file : this.files){
            System.out.println(filecounter + ".  " + file.getmyFileName());
            System.out.println("Original Size: " + (file.getSize()/ 1000) + "kb");
            System.out.println("Deduped Size: " + (file.getActualStoredSize() / 1000) + "kb");
            System.out.println();
            filecounter++;
        }
        System.out.println("=== Deduplication Ratio === ");
        System.out.println(getDedupRatio() + "%");
        System.out.println("***************************");
    }
}



















