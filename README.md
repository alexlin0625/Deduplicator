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
  * **Store (Single file / Directory):**
  
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
  
  
  
  
  
  
  







