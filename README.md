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
* This implementations run by Server.java, Client.java and NetworkGUI.java. Please refer to [III. Work Contribution] from Jiaqi Sun. Section provides detailed explaination on Server/Client interface. Complete instructions please refer to INSTALL.txt in this repo.

6. Develop a Graphical User Interface that displays storage progress (and file allocation statistics) in real time. [5%]
* Working GUI has successfully implemented. Please refer to [III. Work Contribution] from Routian Liu. Section provides detailed explanation on GUI functionalities. 

### Features removed from midterm status report:
1. Implement efficient substring search (i.e. list all locker files that contain a given substring). 
* since we are chunking files by bytes. And then each chunk are stored as hash value. We are not dealing with the characters in the content. Therefore, doing substring search on specific word would not make sense on our implementation. 
2. Ability to efficiently store executables with similar source code.
* We weren't sure about the correct approach to this feature.

### Features added for final working implementation:
1. Ability to store directiories of files as one entity. 
* After completing storing one file successfully. We would like to store multiple files from one directory instead of storing one by one manually. Therefore, a queue was implemented to store all the files in order. The detailed process is explained in the work breakdown section. 
2. Ability to efficiently store similar videos. 
* Originally thought this feature could be hard. But then after the chunking algorithm was completed. It's able to deduplicate videos and store in the locker. Since videos are generally large file, the chunk size would be adjusted under some conditions. The detailed process is explained in the work break down section. 

### relevant references and background materials.
1. https://blog.teamleadnet.com/2012/10/rabin-karp-rolling-hash-dynamic-sized.html?fbclid=IwAR0PhUcACks6zwYk5dGyY2GVNw7T8R6G3_qU1ZTno2PACkdjPdhzxih9wGo
2. https://github.com/YADL/yadl/wiki/Rabin-Karp-for-Variable-Chunking
3. https://medium.com/@glizelataino94/rabin-karp-algorithm-5686ccc0d4e0

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

# III. Work Contribution 

### [Alex Jeffrey Lin] 
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
   
   * To achieve the idea of "rolling hash" on a sliding window: **hashvalue = hashvalue * largeprime + incomingbyte - outgoingbyte * polynomialmultiplier** For details on the specific implementation and example, please refer to [III. Work Contribution] **Gang Wei & Fuyao Wang**
   
   ## **Statistical Protocol**
   
   * Start timer right before executing the functions: Fixed size chunking, Dynamic size chunking, Retrieve, Delete 
timerStart = System.currentTimeMillis(); 
   * End timer right before executing those functions 
timerEnd = System.currentTimeMillis(); 
   * Console will print out the total time elapsed during the execution of those functions. 


### [Routian Liu]
#### **Graphic User Interface(GUI):**

## Functions:

**create locker:** by entering a name, you can create a new locker and load it automatically.

**select locker:** by entering a name, you can load into a locker you have created before.

**store files:** add files in a created locker. For now, we can add video, image, text and pdf files. Besides, selecting multiple files at a time is available.

**delete files:** delete file in current locker.

**retrieve files:** retrieve file from current locker to a specified directory.

**select output path:** select the directory to store retrieved file.

**clear details:** by clicking this button, you can clear the deduplication information.

**upload progress:** when adding multiple files at one time, we can get the upload progress by this progress bar.

## Information:

**locker contents:** display the files stored in our locker, offering convenience for us to retrieve file.

**deduplication details:** display our work in deduplication, such as deduplication size and ratio.

**console:** display current operation and feedback information.


### [Jiaqi Sun]
#### **Server and Client Interface:**

## Network Features:
* The client has a GUI.
* The client can connect to the server and log in to use our deduplicator. Once the client is logged in, the user can do following things:
  1. input a locker name to create a new locker 
  2. input a locker name to load an existed locker
  3. select a single file on the client and send it to the server to store in the current locker
  4. input the file name to retrieve a file in the current locker
  5. input the file name to delete a file in the current locker
  6. check detailed information of all the lockers and files

## Log In System:
* user can only get access to our server and do dedupplication after they log in.
* The server stores a dictionary(implemented by hash map) of **key:** user name and **value:** hash value of password.
* Every time when the server starts a new thread, it will start with a boolean variable to record whether the client is logged in.
* If that boolean variable is false, the server will ask the client to log in first whenever the client wants to do something.
* To log in, the client will send the username and the hash value of password to the server and show the response.

functions:

1. **create locker:** the server use a hash map to manage different lockers. When the user wants to create a locker, the server will check whether there is a locker with same name and create a new locker, put it in the hash map if it's a new locker.
2. **load locker:** the server use that hash map to change current locker into the locker which the user wants to use.
3. **store file:** the client will send the file to the server using a new data output stream. And the server will store that file in the locker with different methods according to the type of the file and then send some details of storing the file.
4. **retrieve file:** the client will send the name of the file to the server. The server will retrieve the file using locker.retrieveTheFile and store it on the server. And then the server will send back the file to the client with some retrieve information.
5. **delete file:** the client will send the name of the file to the server. The server will delete the file using locker.delete.
6. **check detailed information of all the lockers and files:** the server will traverse the hash map of locker to get all the existed locker and send their information back to the client.


### [Gang Wei & Fuyao Wang]

* Implemented dynamic-size chunking algorithm, tested the system and worked on error-handling.
* At the beginning we only have fixed-size chunking algorithm. But when we deal with the problem like: there is totally only 1 or 2 different characters of 2 different files, fixed-size algorithm is not an efficient way. Why? Because when the window goes through this difference, all the rest of it should change. This is absolutely a waste. To have better performance, (efficiency, time complexity) we need to implement an algorithm that could solve this problem. 
* By the concept of Rabin-Karp rolling hash algorithm, that creating content based chunks of a file to detect the changed blocks without doing a full byte-by-byte comparison, which is exactly what we need, we constructed this algorithm that could find the “textual fingerprint” of the difference. 

For example:

In traditional algorithms that hashing the file is like:

[3,1,4,1,5,9,2,…]

Supposing window size is 4:

Hash1: hash[3,1,4,1]

Hash2: hash[1,4,1,5]

Hash3: hash[4,1,5,9]

Hash4: hash[1,5,9,2]

……

But in Rabin-Karp implementation, we reuse previous ones, and add a prime number as weighted number. Why we use prime number? Because prime number can avoid collision.

(p as prime number)

Hash1: p^n * 3 + p^(n-1) * 1 + p^(n-2) * 4 + p^(n-3) * 1

Hash2: Hash1 – p^n * 3 + p*5

……

By this concept, we constructed the “for-loop”
```
/*for (int i = 0; i < chunkSize; i++) {
to perfoms initial polynomial hash on first chunk of size “chunkSize”.
```

```
Then in the “while” condition,
/*while (currRKWindowStartIndex + currRKWindowSize < fileBytesLength) {
```

```
roll the hash through the whole file, looking for a “textual fingerprint”.
```

```
In the “if” condition, 
/*if ((currHashVal & mask) == 0 || fileBytesCounter == fileBytesLength) {
```

```
we reset everything if the character is found, or it is already the last chunk to set window. 
/*if(!locker.chunkHashExist(hashOutput)) {
And if the hash doesn’t exist in the dictionary, we insert it. 
After testing of some cases, we found dynamic-size chunking works ideally as we expected.
```


  


  
  
  
  







