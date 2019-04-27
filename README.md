# DeDuplicator

## **Bugs Reported**
## I. Program wasn't portable
#### 1. Our locker can now be **loaded back** with exact same content once you terminates the program. Specifically solved by implementing "serializable". Load consists two choices as following:
* default: 

  default path is your current working directory [DeDuplication] which means all created locker would be saved in this path. Locker could be loaded by inserting locker's name. 
  ![alt text](https://github.com/alexlin0625/Deduplicator/blob/master/Pictures/load%20locker%20from%20default%20path.JPG)

* import: 

  You can import locker from **specified** path that stores your exported locker.
  ![alt text](https://github.com/alexlin0625/Deduplicator/blob/master/Pictures/import%20locker%20from%20specified%20path.JPG)

#### 2. Our locker can now be **imported** from default path or specified path. 
* as shown above, user can input specified path to import the locker without losing any data. 

#### 3. Our locker can now be **exported** to the specified path and can be imported from another machine to use without losing any data.

* When you do finish, user will be asked to whether export locker or not. locker can be exported to specified path then prints out the locker's current information.

  ![alt text](https://github.com/alexlin0625/Deduplicator/blob/master/Pictures/export%20locker%20to%20specified%20path.JPG)

#### 4. Our locker is now portable.
* Exported out the locker when finished, a file type locker is then created. It can be easily transferred and loaded back successfully from another machine. 

#### 5. Impot & Output methods implemented in RunDeDup.java 
  ![alt text](https://github.com/alexlin0625/Deduplicator/blob/master/Pictures/Import%20%26%20Export%20method.JPG)


## II. Output path for retrival is different with Mac OS and Windows.
* Forward or Backward slash for path format is now fixed for both **Mac OS**(/) and **Windows**(\\) users. 
* This problem is now fixed with File API: **File.separator**.
  ![alt text](https://github.com/alexlin0625/Deduplicator/blob/master/Pictures/Output%20path%20format%20bug%20fix%20for%20Mac%26Windows.JPG)


## III. ChunkSize decision returns Null exception case for small and empty file.
* ChunkSize method is now modified to deal with as small as 64 byes file and empty file cases.
  ![alt text](https://github.com/alexlin0625/Deduplicator/blob/master/Pictures/Chunksize%20decision%20bug%20fix%20for%20small%20size%20files.JPG)


## IV. Other error handling cases from user experience
* We added more error handling cases to improve user experience. Errors handle cases such as invalid inputs for some of our operating cases.  


