package DeDup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

///////////////////////////////////////
//// *Chunking & Storing Methods* /////
///////////////////////////////////////

public class HashTheChunks {
    // this function returns a file type as a string
    public String getFileExtension(File file) {
        String extension = "";
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
        return extension;
    }

    // read in files through FileInputStream
    public int getFileSize(File file) {
        FileInputStream fis = null;
        int size = 0;
        try {
            fis = new FileInputStream(file);
            // get bytes count
            size = fis.available();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return size;
    }

    public int getChunksize(String extension, int fileSize) {
        int chunkSize = 2048;
        // Chunk size decision
        if(isVideo(extension) || isPDF(extension)){
            if (fileSize > 50000000){  //50mb
                chunkSize = 8192;
            }
            else if(fileSize > 10000000){ //10mb
                chunkSize = 4096;
            }
            else if(fileSize > 1000000){ //1mb
                chunkSize = 2048;
            }
            else if(fileSize > 500000){ //0.5mb
                chunkSize = 512;
            }
            else if(fileSize == 0){ //for empty files
                chunkSize = 0;
            }
            else{
                chunkSize = 64; //for small file
            }
        }
        // chunk size decision
        if((isImage(extension) || isText(extension))){
            if (fileSize > 10000000){ //10mb
                chunkSize = 2048;
            }
            else if(fileSize > 1000000){ //1mb
                chunkSize = 1024;
            }
            else if(fileSize > 500000){ //0.5mb
                chunkSize = 512;
            }
            else if(fileSize == 0){ //for empty files
                chunkSize = 0;
            }
            else{
                chunkSize = 64; // for small file
            }
        }
        return chunkSize;
    }
    // supported video types, can be added
    public boolean isVideo(String extension){
        if(extension.equalsIgnoreCase(".wav") ||
                extension.equalsIgnoreCase(".mov") ||
                extension.equalsIgnoreCase(".mp4") ||
                extension.equalsIgnoreCase(".flv") ||
                extension.equalsIgnoreCase(".avi") ){
            return true;
        }
        return false;
    }
    // supported image types, can be added
    public boolean isImage(String extension){
        if(extension.equalsIgnoreCase(".jpeg") ||
                extension.equalsIgnoreCase(".gif") ||
                extension.equalsIgnoreCase(".png") ||
                extension.equalsIgnoreCase(".jpg")){
            return true;
        }
        return false;
    }

    public boolean isPDF(String extension){
        if(extension.equalsIgnoreCase(".pdf")){
            return true;
        }
        return false;
    }

    public boolean isText(String extension){
        if(extension.equalsIgnoreCase(".txt") ||
        extension.equalsIgnoreCase(".docx") ||
        extension.equalsIgnoreCase(".doc") ||
        extension.equalsIgnoreCase(".rtf")){
            return true;
        }
        return false;
    }

    public long fixedSizeChunk(MyLocker locker, String filePath, int chunkSize) throws NoSuchAlgorithmException, IOException {
        // info for storing the name into locker
        File file = new File(filePath);
        String filename = file.getName();
        int size = getFileSize(file);
        ArrayList<String> fileRetriever = new ArrayList<>();
        LinkedHashSet<String> hashSet = new LinkedHashSet<>();
        long actualStoredSize = 0;

        // chunking starts
        byte[] chunk;
        Path p = FileSystems.getDefault().getPath(filePath);
        byte[] fileBytes = Files.readAllBytes(p);
        // for dedupRatio purpose
        locker.addToOriginalFilesSize(fileBytes.length);
        String hashOfEntireFile = hashTheContent(fileBytes);
        long totalSizeInserted = 0L;

        int currChunkPosition = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (currChunkPosition < fileBytes.length) {
            baos.write(fileBytes, currChunkPosition, chunkSize); // baos.write(byte[] filesByte, offset, len)
            chunk = baos.toByteArray(); // writes into baos and set it to chunk
            String hashOutput = hashTheContent(chunk); // call hashTheContent() to proceed hashing
            fileRetriever.add(hashOutput); // store hashOutput into Retriever Array List for file retrieve purpose
            hashSet.add(hashOutput); // store hashOutput into hashSet for file deletion purpose

            if(!locker.chunkHashExist(hashOutput)) {
                locker.addChunksToDictionary(hashOutput, chunk); // insert into the dictionary in locker
                totalSizeInserted += chunk.length;
                actualStoredSize += chunk.length;
            }

            // reset
            currChunkPosition += chunkSize; // update position to the head of next chunk of bytes
            baos.reset(); // reset the baos for next chunk of bytes insertion

            // If the size of the remaining data is less than the chunkSize
            if (!(currChunkPosition < fileBytes.length - chunkSize)) {
                chunkSize = fileBytes.length - currChunkPosition;
            }
//            System.out.println("chunk size: " + chunk.length + " hash output: " + hashOutput);
        }

        // now insert all file info into the locker
        locker.addFileToLocker(filename, size, hashOfEntireFile, fileRetriever, hashSet, actualStoredSize);
        // some statistics data
        long lockerSize = locker.getcurrLockerSize();
        long updateSize = locker.addToLockerSize(totalSizeInserted);
        long difference = updateSize - lockerSize; // this difference of the actual file bytes stored compare to original's file size

        System.out.println("Current locker size: "+ updateSize); // show current locker size
        System.out.println("Deduped Size: "+ difference); // show this file's size been stored into locker
        System.out.println("Current Locker DeDupped Ratio Utilized: " + locker.getDedupRatio() + "%");
        return updateSize;
    }

    public long dynamicSizeChunk(MyLocker locker, String filePath, int chunkSize) throws IOException, NoSuchAlgorithmException {
        File file = new File(filePath);
        String filename = file.getName();
        int size = getFileSize(file);
        Path p = FileSystems.getDefault().getPath(filePath);
        byte[] fileBytes = Files.readAllBytes(p);
        locker.addToOriginalFilesSize(fileBytes.length);
        ArrayList<String> fileRetriever = new ArrayList<>();
        LinkedHashSet<String> hashSet = new LinkedHashSet<>();
        String hashOfEntireFile = hashTheContent(fileBytes);
        long totalSizeInserted = 0L;
        long actualStoredSize = 0L;

        int largePrimeNumber = 69031;
        int fileBytesLength = fileBytes.length; // length of the file bytes array
        int rkWindowIndex = 0; //keep track of the rabin karp window's index
        int currRKWindowStartIndex = 0;
        int currRKWindowSize = chunkSize;
        byte[] rkWindow = new byte[currRKWindowSize];
        int rkWindowSize = rkWindow.length;
        int polynomial = 1; //polynomial multiplier
        int fileHashIndex = 0; //keep track of the file bytes array index as we iterate through
        int currHashVal = 0; //the hash of the chunked bytes ; will update as rabin karp window shifts
        int fileBytesCounter = 0; //keep track of how many bytes we've iterated through in file bytes array

        // if statement here for the mask
        int mask = decideMask(chunkSize);

        boolean initialChunk = true;

        byte[] chunk;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //Initialize the rabin karp hash window with the first chunk of bytes
        for (int i = 0; i < chunkSize; i++) {
            byte currFileHashByte = fileBytes[fileHashIndex];
            fileHashIndex++;
            if (!initialChunk) {
                currHashVal *= largePrimeNumber;
                currHashVal += currFileHashByte; //add next hash
                polynomial *= largePrimeNumber;

            } else {
                currHashVal += currFileHashByte;
                initialChunk = false;
            }
            rkWindow[rkWindowIndex] = currFileHashByte;
            rkWindowIndex++;
            rkWindowIndex = rkWindowIndex % rkWindowSize;
            baos.write(currFileHashByte);
            fileBytesCounter++;
        }

        while (currRKWindowStartIndex + currRKWindowSize < fileBytesLength) {

            if (fileBytesCounter <= fileBytesLength) {
                byte currFileHashByte = fileBytes[fileHashIndex];
                fileHashIndex++;
                int temp = polynomial * rkWindow[rkWindowIndex];
                currHashVal = currHashVal - temp; //remove the value of hash at beginning of window
                currHashVal *= largePrimeNumber;
                currHashVal += currFileHashByte;
                rkWindow[rkWindowIndex] = currFileHashByte;
                rkWindowIndex++;
                rkWindowIndex = rkWindowIndex % rkWindowSize;
                baos.write(currFileHashByte);
                fileBytesCounter++;
            }

            currRKWindowSize++;

            if ((currHashVal & mask) == 0 || fileBytesCounter == fileBytesLength) {
                chunk = baos.toByteArray();
                baos.reset();
                String hashOutput = hashTheContent(chunk);

                currRKWindowStartIndex += currRKWindowSize;
                currRKWindowSize = 0;

                if(!locker.chunkHashExist(hashOutput)) {
                    locker.addChunksToDictionary(hashOutput, chunk); // insert into the dictionary in locker
                    totalSizeInserted += chunk.length;
                    actualStoredSize += chunk.length;
                }
                fileRetriever.add(hashOutput);
                hashSet.add(hashOutput);
            }

        }
        locker.addFileToLocker(filename, size, hashOfEntireFile, fileRetriever, hashSet, actualStoredSize);
        // some statistics data
        long lockerSize = locker.getcurrLockerSize();
        long updateSize = locker.addToLockerSize(totalSizeInserted);
        long difference = updateSize - lockerSize; // this difference of the actual file bytes stored compare to original's file size

        System.out.println("Current locker size: "+ (updateSize/1000) + "kb"); // show current locker size
        System.out.println("Deduped Size: "+ (difference/1000) + "kb"); // show this file's size been stored into locker
        System.out.println("Current Locker DeDupped Ratio Utilized: "+ locker.getDedupRatio() + "%");
        System.out.println("Current dictionary size: " + (locker.getDictionarySize()/1000) + "kb");
        return updateSize;
    }

    public String hashTheContent(byte[] content) throws NoSuchAlgorithmException {
        // hash the chunks using MessageDigest
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(content);
        byte[] hashedChunk = md.digest(content);

        // turn hashed chunks to string representation
        StringBuffer sb = new StringBuffer();
        for (Byte SHAHash : hashedChunk) {
            sb.append(Integer.toString((SHAHash & 0xff) + 0x100, 16).substring(1));
        }
        String hashOutput = sb.toString();
    return hashOutput;
    }

    public int decideMask(int chunkSize){
        int mask;
        if(chunkSize == 512){
            mask = 1 << 9;
        }
        else if(chunkSize == 2048){
            mask = 1 << 11;
        }
        else if(chunkSize == 4096){
            mask = 1 << 12;
        }
        else if(chunkSize == 8192){
            mask = 1 << 13;
        }
        else {
            mask = 1 << 12;
        }
        mask -= 1;
        return mask;
    }
}
