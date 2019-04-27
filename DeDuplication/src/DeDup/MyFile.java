package DeDup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MyFile implements Serializable {
    // initialize the components
    private String fileName; // file's name
    private int size; // file's size in bytes
    private String hash; // hash of the entire file
    private ArrayList<String> fileRetriever; // chunks of the file, reconstruct usage
    private LinkedHashSet<String> hashSet;
    private long ActualStoredSize;

    // set up the constructors
    protected MyFile(String fileName, int size, String hash, ArrayList<String> fileRetriever, LinkedHashSet<String> hashSet, long ActualStoredSize) {
        this.fileName = fileName;
        this.size = size;
        this.hash = hash;
        this.fileRetriever = fileRetriever;
        this.hashSet = hashSet;
        this.ActualStoredSize = ActualStoredSize;
    }

    protected String getmyFileName(){
        return this.fileName;
    }

    protected ArrayList<String> getFileRetriever(){
        return this.fileRetriever;
    }

    protected int getSize(){
        return this.size;
    }

    protected String getHash() {
        return this.hash;
    }

    protected LinkedHashSet<String> getHashSet() {
        return this.hashSet;
    }

    protected long getActualStoredSize(){
        return this.ActualStoredSize;
    }
}
