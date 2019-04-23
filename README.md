# Deduplicator

# I. Documentation

### Project Description 
Design and implement an efficient data storage locker that utilizes deduplication. The locker should be able to accept files and store them with a minimum storage by storing some common data blocks only once. The file should also be retrieved from the locker and all content remain the same without any corruption.

### group members 
Alex Jeffrey Lin  [ajlin@bu.edu]

Routian Liu  [rtliu@bu.edu]

Jiaqi Sun  [sjq@bu.edu]

Gang Wei  [wg0502@bu.edu]

Fuyao Wang  [fuyao@bu.edu]

### a high-level description of the implementation, with particular emphasis on the design decisions related to data structures and algorithms
Please refer to [III. Work Contribution] from Alex Jeffrey Lin. Section contains complete explainations on data structure and algorithm implementations for this program.

### a list of the features that have been implemented:
1. Ability to store directories of files as one entity. [5%]
* In the command line user interface. When the store option is picked, user will be asked to choose single insert or multiple insert. For choosing the multiple insert, you are able to give the path of the directory which contains all the files you want to store into locker. These files will be inqueued and store each file's info one by one to the locker. 

2. Ability to efficiently store similar images. [5%]
* Locker is able to dedup images via fixed sized chunking and store into locker. Chunk size is adjusted based on the image's size and type. Please refer to [III. Work Contribution] from Alex Jeffrey Lin. Section provides detailed explanation on fixed sized chunking.

3. Ability to efficiently store similar videos. [10%]
* Locker is able to dedup videos via fixed sized chunking and store into locker. Chunk size is adjusted based on the video's size and type. Please refer to [III. Work Contribution] from Alex Jeffrey Lin. Section provides detailed explanation on fixed sized chunking.

4. Implement file deletion from your locker. [5%]
* Please refer to [III. Work Contribution] from Alex Jeffrey Lin. Section provides complete instructions and detailed explainations on data structure and algorithm implementations.

5. Develop networked access to your locker. [5%]

6. Develop a Graphical User Interface that displays storage progress (and file allocation statistics) in real time. [5%]

### relevant references and background materials.

# II. Code
### Working code
```
package DeDup;
```
#### Command Line Interface
1. RunDedup.java
2. MyLocker.java
3. MyFile.java
4. HashTheChunks.java
#### Graphical User Interface
5. myGUI.java
#### Server and Client Interface
6. NetworkGUI.java
7. Client.java
8. Server.java

### Run
Please refer to [INSTALL.txt] in this repo. 


### All testing code utilized to observe the correctness of your code.


# III. Work Contribution 

### Alex Jeffrey Lin: 
#### Command Line Interface run down:
* The program will promp the user if they want to create a new locker when it is run. It will ask to choose a name for the locker.
* It will then give the user the option to either: **Store** files into locker; **Retrieve** existing file from locker; **Delete** existing file from locker; Check existing files in locker; **Finish** will end the program and print locker statistics.
  ## **Store (Single file / Directory):**
  
  1. **If same file name already exists in locker:** 
  
  * For every file being stored, the program will check if a same name file already exists in the current locker, it will ask  if the user would like to replace that existing file with the new one being inserted.

  * If user chooses "**Y**": It will first check if the hash of both files are the same. To be safe that there aren't collisions between the hashes, we take an additional step to check if the length of both files are the same. Upon checking both properties, we can be sure that both files are either the same or not. If both files turn out to be the exact same, then nothing is done. However if the files contains different content, then we first delete the existing file from file locker, along with its chunks from the dictionary (only delete the chunks that aren't referenced from other files); then we go through the process of the pre-determined fixed-size chunking or dynamic-sized chunking algorithm, and store the new file.

  * If user chooses "**N**": Nothing is done.
  
  2. **Single file storage:**
  
  * User will be asked to provide the path of the file being stored. Depending on the type of the file being stored, it will decide whether to proceed with Fixed Size chunking or Dynamic Size chunking.
  
  * Check if same file name exists: if it does, proceed according to its conditions. 
  
  3. **Multiple file storage from directory:**
  
  * User will be asked to provide the path of the directory to be stored. The program will detect if path is a valid directory.
  
  * Load in all files in the directory and store in a queue to be processed.
  
  * For every file in the directory, it will go through the same decisions as storing a single file.
  
  
  ## **Retrieve:**
  1. The program will not allow user to retrieve files if the locker is empty. Will prompt user to store a file in order to retrieve.
  
  2. Prompt the user to enter the path of a directory in which the user wants to save the retrieved file. Will detect if path is a valid directory.
  
  3. Prompt the user to enter the name of the file to be retrieved. Search the locker for the file:
  * **If file does not exist** in locker, it will be made known to the user
  * **If file exists in locker**, will find the file's information, and use its references from its "file retriever" to map to its corresponding chunks in the dictionary. The file will be reconstructed and stored into user's selected output path.
  
  ## **Delete:**
  
  1. The program will not allow user to delete files if the locker is empty. Will prompt user to store a file in order to delete.
  2. Prompt the user to enter the name of the file to be deleted. Search the locker for the file:
   * **If file does not exist** in locker, it will be made known to the user
   * **If file exists in locker**, it will first remove all of the file's references and chunks from the dictionary only if no other file in the locker uses those same references and chunks.
   * Remove the MyFile object from the file locker. Update locker size.
   
  ## **Check files:**
  
  1. The program will not allow user to check for current files in locker if the locker is empty. Will prompt user to store a file in order to retrieve.
  2. Display the name of all files that's been successfully stored in locker, along with the file's original size and the actual size stored after deduplication.
  
  ## **Finish:**
  
  1. Display locker statistics:
  * Locker name
  * Size of locker
  * Files in locker
  * Deduplication ratio
  
  
  #### Data Structures and Algorithm Implementation:
  
  ## **HashSet:**
  Data structure: LinkedHashSet<String> hashSet
  
  1. This HashSet will store the references each file used, just like the fileRetriever. There won't be any duplicated references in the HashSet, but that's okay, since we only need to know if a particular file contains the reference we are attempting to delete.
  2. A HashSet is used during the deletion of a file from the locker. If we want to delete a file in the locker, we would have to delete all that file's chunks and its references, however we should avoid deleting the references that were also used in other files. As long as the reference we're attempting to delete is not in the HashSet of any other files, then that reference along with its chunk can be deleted from the dictionary.
  3. In an ArrayList, to find a value can take up to O(n) time. In comparison lookup in a HashSet only takes O(1) time, and this is especially useful when looking for a specific reference. Instead of traversing through an ArrayList looking for the reference one by one, a HashSet can directly find the reference easily.
  
  ## **Dictionary**
  Data structure: HashMap<String, byte[]> dictionary
  
  1. A HashMap will store the "hashOutput" as the key, and "chunk" as the value. "hashOutput" is obtained by hashing its respective "chunk" value using an SHA hash function. This dictionary is essential to file storage and file retrieval, since it will be storing all of our incoming file's data.
  2. When storing a new file, there's a conditional such that if the chunk already exists in the dictionary, it will not be saved again. There's no need to save the same information twice. This property is in essence the most important feature in data deduplication.
  3.  Time complexity:
    * HashMap allows O(1) time for insertion, lookup, deletion.
  
  ## **FileRetriever**
  Data structure: ArrayList<String> fileRetriever
  
  1. An ArrayList will store the references of each file. Each reference is obtained by hashing each chunk using an SHA (Secure Hash Algorithm) hash function. These hash values will later be used to reference the chunks we previously stored in the dictionary Hashmap for file reconstruction. The key point of using a file retriever is that we no longer need to store all the duplicated data from the new file, all we need to store is a reference so that it can later be used to reconstruct the file during retrieval.
  2. Time complexity"
    * ArrayList allows O(1) time for insertion, O(1) for access, and O(n) for retrieval, based on file size n.
    
   ## **Fixed size chunking**
   Fixed size chunking involves determining a chunk size (in bytes) and segmenting files into those block sizes. Then those chunks are stored in a Hashmap with the key being the "hashOutput" of the chunked data, and the value being the actual chunk itself represented as bytes in a byte array.
   
   * The chunk size is determined by the file type and size. For example, videos are generally larger files, therefore we have to adjust the chunk size to improve efficiency.
   * Create a new MyFile object to be stored at the end of the chunking process.
   * Turn the incoming file into an array of bytes so it can be easily split into chunks. A ByteArrayOutputStream is used because it's easy to specify how many bytes we'd like to split the byte array into. We will continuously place the chunk of bytes from the byte array into the ByteArrayOutputStream, and the chunk back into a byte array to process.
   * Every byte[] chunk will be hashed using an SHA hash function("hashOutput"). First decide whether or not to place the "hashOutput" and byte[] chunk into the dictionary; If the "hashOutput"(reference) already exists in the dictionary(meaning we've stored the same chunk previously/deduplication), we only need to add the "hashOutput" into the "fileRetriever" and "hashSet"; If the "hashOutput" doesn't exist, add the "hashOutput" with the byte[] chunk into the dictionary.
   * Reset the ByteArrayOutputStream for the next iteration.
   * When all bytes in the file has been processed, we can add the new MyFile object into the locker.
   
   ## **Dynamic size chunking**
   Dynamic size chunking involves using algorithms to determine a dynamic block size. The difference between Fixed size chunking and Dynamic size chunking is that when storing two very similar files with a slight difference, the former method will not be able to detect duplicated value because the bytes are shifted when a small deleteion or insertion is made; However the latter will be able to change the chunk size based on patterns in data so they are of variable length. Since the chunk boundaries shift along with the data patterns, duplicates can still be found. 
   
   * To achieve the idea of dynamic size chunking, we used the Rabin-Karp Rolling Hash algorithm. Essentially, the technique is to compute the hash value at every byte position in the file. Algorithm uses a sliding window that scans over the data bytes and computes a hash value at each point. The hash value at position "i" can be computed from the hash value at position "i-1": **H(X(i,n)) = H(X(i-1, n) + Xi - X(i-n)** where "n" is the size of the window (chunkSize) and X(i, n) is the window at position "i". While this equation is only calculating the hash value of the initial window, it does not calculate the hash value of the next window after sliding. 
   
   * To achieve the idea of "rolling hash" on a sliding window: **hashvalue = hashvalue * largeprime + incomingbyte - outgoingbyte * polynomialmultiplier** For details on the specific implementation and example, please refer to [III. Work Contribution] Gang Wei & Fuyao Wang
   
   ## **Statistical Protocol**
   
   * Start timer right before executing the functions: Fixed size chunking, Dynamic size chunking, Retrieve, Delete 
timerStart = System.currentTimeMillis(); 
   * End timer right before executing those functions 
timerEnd = System.currentTimeMillis(); 
   * Console will print out the total time elapsed during the execution of those functions. 


  








  


  
  
  
  







