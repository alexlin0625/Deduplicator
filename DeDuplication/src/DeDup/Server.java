package DeDup;
import DeDup.MyFile;
import DeDup.MyLocker;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
To use our server, you need to replace final String pathname and final String retrievePath with your own
Run the server first, and then run the client to test our functions.
*/


public class Server {
    private static ServerSocket listener;
    private static class serverThread extends Thread {
        private Socket socket;

        BufferedReader in;
        PrintWriter out;

        private FileInputStream fis;
        private DataOutputStream dos;

        public serverThread(Socket socket) {
            this.socket = socket;
            System.out.println("connecting to a new client");
        }
        public void run() {
            final String pathname = "C:\\Users\\sjqgo\\Desktop\\testFileTransfer1";    //change into your own.
            //The file sent from the client will be stored here.
            final String retrievePath = "C:\\Users\\sjqgo\\Desktop\\testServerFileRetrieve1"; //change into your own.
            final String lockerPath = "C:\\Users\\sjqgo\\Desktop\\serverLocker";
            File serverLocker = new File(lockerPath);
            if(!serverLocker.exists())
                serverLocker.mkdir();
            String username = "";
            // The file retrieved from the locker will be stored here and then sent to the client.
            String lockername = "";
            String filename = "";
            HashTheChunks h = new HashTheChunks();
            boolean logIn=false;


            File dir = new File(pathname);
            if(!dir.exists())
                dir.mkdir();

            dir = new File(retrievePath);
            if(!dir.exists())
                dir.mkdir();
            try {
                HashMap<String,MyLocker> lockers = new HashMap<String, MyLocker>();
                HashMap<String,Integer> userInfo = new HashMap<String, Integer>();
                userInfo.put("EC504",1216985755);
                userInfo.put("username",1216985755);
                userInfo.put("sjq",1216985755);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Hello from the server.");


                String input = "";
                MyLocker locker = new MyLocker("default locker", new HashMap<>(), new ArrayList<>());
                do {
                    input = in.readLine();
                    if(input.equals("logIn")) {

                        username = in.readLine();
                        String passwordHash = in.readLine();
                        if(!userInfo.containsKey(username)) {
                            //System.out.println("no such username");
                            out.println("no");
                            continue;
                        }
                        if(userInfo.get(username).toString().equals(passwordHash)) {
                            //System.out.println("ok");
                            out.println("yes");
                            logIn=true;
                            continue;
                        }
                        else {
                            out.println("no");
                        }
                    }
                    if(input.equals("reset"))
                        continue;
                    if(input.equals("create")) {
                        lockername = in.readLine();
                        if(!logIn) {
                            out.println("log in first please");
                            continue;
                        }
                        //create a new locker
                        File lockerExisit = new File(lockerPath+
                                File.separator+username+File.separator+lockername);
                        if(lockerExisit.exists()) {
                            out.println("you have created the locker nemed "+lockername+" before");
                            continue;
                        }
                        locker = new MyLocker(lockername, new HashMap<>(), new ArrayList<>());
                        lockers.put(lockername,locker);
                        out.println("locker named "+lockername+" created");
                        continue;
                    }
                    else if(input.equals("load")) {
                        String previousLockerName = lockername;
                        lockername = in.readLine();
                        if(!logIn) {
                            out.println("log in first please");
                            continue;
                        }
                        //new a locker
                        try {
                            locker = RunDedup.importLocker(lockerPath+File.separator+username+File.separator+lockername);
                            out.println("locker loaded successfully");
                        } catch (Exception e) {
                            out.println("noLocker");
                            lockername = previousLockerName;
                            continue;
                        };
                        /*if(lockers.containsKey(lockername)) {
                            locker = lockers.get(lockername);
                            out.println("locker "+lockername+" loaded");
                        }
                        else
                            out.println("noLocker");*/
                    }
                    else if(input.equals("retrieve")) {
                        filename = in.readLine();
                        if(!logIn) {
                            out.println("log in first please");
                            continue;
                        }
                        if(locker.sameFileNameExists(filename)) {
                            out.println("go");
                            String path = retrievePath;      //"C:\\Users\\sjqgo\\Desktop\\testServerFileRetrieve";
                            //replace this with your own path, can't be replaced by the final String pathname

                            File file;

                            //retrieve a file
                            //System.out.println("path is "+path);
                            long timerStart = System.currentTimeMillis();
                            locker.retrieveTheFile(filename,path);
                            long timerEnd = System.currentTimeMillis();


                            //System.out.println("prepare to send file to the client");
                            file = new File(path+"\\retrieved_"+filename);


                            DataOutputStream dos=new DataOutputStream(socket.getOutputStream());


                            dos.writeLong(file.length());
                            //System.out.println("send the length"+file.length());
                            dos.writeUTF(file.getName());
                            //System.out.println("send the name"+file.getName());
                            //System.out.println("name："+file.getName());
                            //System.out.println("length："+file.length());
                            int count=-1,sum=0;
                            byte[] buffer=new byte[1024*1024];
                            DataInputStream dis = new DataInputStream(new FileInputStream(file));
                            while((count=dis.read(buffer))!=-1){
                                dos.write(buffer,0,count);
                                sum+=count;
                                //System.out.println("already sent "+sum+" bytes");
                            }
                            //System.out.println("file sent successfully");

                            //System.out.println("file sent successfully to the client");
                            out.println("Time used to retrieve file <<" + filename + ">> is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                            out.println(filename + " file has been retrieved successfully");
                            out.println("-------------------------------------" + "\n");
                            continue;
                        }
                        else {
                            out.println("no");
                            continue;
                        }
                    }
                    else if(input.equals("delete")) {
                        filename = in.readLine();
                        if(!logIn) {
                            out.println("log in first please");
                            continue;
                        }
                        //System.out.println("try to delete "+filename);
                        if(locker.sameFileNameExists(filename)) {
                            //delete a file
                            locker.deleteFile(filename);
                            out.println("delete "+filename+" successfully from the locker "+lockers.get(locker));
                            RunDedup.exportLocker(locker,lockerPath+File.separator+username);
                            out.println("locker saved on the server");
                        }
                        else
                            out.println("no");

                    }

                    else if(input.equals("lockerInfo")) {
                        if(!logIn) {
                            out.println("log in first please");
                            continue;
                        }
                        File serverLockers = new File(serverLocker+File.separator+username);
                        File[] fs = serverLockers.listFiles();	//遍历path下的文件和目录，放在File数组中
                        if(fs.length==0) {
                            out.println("no locker yet");
                            out.println("end");
                            continue;
                        }
                        out.println("---------------------------------");
                        out.println("current user: "+username);
                        out.println("current locker: "+lockername);
                        out.println("total locker number: "+fs.length);
                        for(File f:fs){
                            {
                                //System.out.println(f.getName());
                                try {
                                    MyLocker l = RunDedup.importLocker(f.getAbsolutePath());
                                    out.println("locker name: "+f.getName());
                                    int ii=1;
                                    for(MyFile fshow: l.files) {
                                        out.println("      file "+ii+": "+fshow.getmyFileName()+
                                                "      size: "+fshow.getSize());
                                        ii++;
                                    }
                                } catch (Exception e) {}

                            }
                        }
                        out.println("end");
                        continue;



                    }


                    else if(input.equals("store")) { //store file sent from the client
                        if(!logIn) {
                            out.println("log in first please");
                            continue;
                        }
                        File lockerExisit = new File(lockerPath+
                                File.separator+username);
                        if(lockerExisit.listFiles().length==0) {
                            out.println("noLocker");
                            continue;
                        }
                        File file=new File(pathname);
                        out.println("go");
                        try {
                            int selectFileNum = Integer.parseInt(in.readLine());
                            String[] selectFilePath = new String[selectFileNum];
                            File directory = new File(pathname);
                            if (!directory.exists()) {
                                directory.mkdir();
                            }
                            //start to receive file from the server
                                DataInputStream dis = new DataInputStream(socket.getInputStream());
                                long length=dis.readLong();
                                String name=dis.readUTF();
                                //System.out.println("selectFileNum="+selectFileNum);
                                //System.out.println("length="+length);
                                //System.out.println("name="+name);
                                for(int ii=0;ii<selectFileNum;ii++) {
                                    System.out.println("name="+name);
                                    filename=name;
                                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(pathname+"\\"+name)));
                                    int count=-1,sum=0;
                                    byte[] buffer=new byte[1024*1024];



                                    while ((count = dis.read(buffer)) != -1) {

                                        dos.write(buffer, 0, count);
                                        sum += count;
                                        //System.out.println("received" + sum + "bits");
                                        if (sum == length) {
                                            break;
                                        }

                                    }
                                    dos.flush();
                                    //System.out.println(name + "receive the file");

                                }
                                //receive the file, start to store the file to the locker


                            String filePath = pathname+File.separator+name;

                            file = new File(filePath);
                            filename = file.getName();
                            MyFile fileExisted = locker.searchFileInLocker(filename);

                            // calls getFileSize to get the file size
                            int fileSize = h.getFileSize(file);
                            out.println("File Size : " + (fileSize/1000) + "kb");

                            // calls getFileExtension to get the file type
                            String extension = h.getFileExtension(file);

                            long timerStart;
                            long timerEnd;



                            //Single file storage: Fix sized chunking
                            if (extension != null && (h.isVideo(extension) == true || h.isImage(extension) == true) || h.isPDF(extension) == true) {
                                if (fileExisted != null) {
                                    {
                                        Path p = FileSystems.getDefault().getPath(filePath);
                                        byte[] bytesOfNewFile = Files.readAllBytes(p);
                                        String hashOfNewFile = h.hashTheContent(bytesOfNewFile);
                                        String hashOfExistedFile = fileExisted.getHash();
                                        long sizeOfNewFile = bytesOfNewFile.length;

                                        if (!(hashOfNewFile.equals(hashOfExistedFile) && (sizeOfNewFile == fileExisted.getSize()))) {
                                            timerStart = System.currentTimeMillis();
                                            locker.deleteFile(filename);
                                            //manageFixSizeChunking(locker, filename, filePath, extension, fileSize, h);
                                            out.println("Storing " + filename + " using fixed size chunking");
                                            int chunkSize = h.getChunksize(extension, fileSize);
                                            long dedupedSize = h.fixedSizeChunk(locker, filePath, chunkSize);
                                            out.println("current locker size: "+locker.getcurrLockerSize());
                                            out.println("deduped size: "+dedupedSize);
                                            out.println("current deduplication ratio: "+locker.getDedupRatio());
                                            out.println(filename + " is added successfully");
                                            timerEnd = System.currentTimeMillis();
                                            out.println("Time used to store file <<" + filename + ">> via Fix Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                            out.println("-------------------------------------" + "\n");
                                            RunDedup.exportLocker(locker,lockerPath+File.separator+username);
                                            out.println("locker saved on the server");
                                        }
                                        else {
                                            out.println("Exact same file exists. Nothing is done.");
                                        }
                                    }
                                }
                                else {
                                    timerStart = System.currentTimeMillis();
                                    //manageFixSizeChunking(locker, filename, filePath, extension, fileSize, h);
                                    out.println("Storing " + filename + " using fixed size chunking");
                                    int chunkSize = h.getChunksize(extension, fileSize);
                                    long dedupedSize = h.fixedSizeChunk(locker, filePath, chunkSize);
                                    out.println("current locker size: "+locker.getcurrLockerSize());
                                    out.println("deduped size: "+dedupedSize);
                                    out.println("current deduplication ratio: "+locker.getDedupRatio());
                                    out.println(filename + " is added successfully");
                                    timerEnd = System.currentTimeMillis();
                                    out.println("Time used to store file <<" + filename + ">> via Fix Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                    out.println("-------------------------------------" + "\n");
                                    RunDedup.exportLocker(locker,lockerPath+File.separator+username);
                                    out.println("locker saved on the server");
                                }
                            }


                            //Single file storage: Dynamic sized chunking
                            else if (extension != null && h.isText(extension) == true) {
                                if (fileExisted != null) {
                                    {
                                        Path p = FileSystems.getDefault().getPath(filePath);
                                        byte[] bytesOfNewFile = Files.readAllBytes(p);
                                        String hashOfNewFile = h.hashTheContent(bytesOfNewFile);
                                        String hashOfExistedFile = fileExisted.getHash();
                                        long sizeOfNewFile = bytesOfNewFile.length;

                                        if (!(hashOfNewFile.equals(hashOfExistedFile) && (sizeOfNewFile == fileExisted.getSize()))) {
                                            timerStart = System.currentTimeMillis();
                                            locker.deleteFile(filename);
                                            //manageDynamicSizeChunking(locker, filename, filePath, extension, fileSize, h);
                                            out.println("Storing " + filename + " using fixed size chunking");
                                            int chunkSize = h.getChunksize(extension, fileSize);
                                            long dedupedSize = h.fixedSizeChunk(locker, filePath, chunkSize);
                                            out.println("current locker size: "+locker.getcurrLockerSize());
                                            out.println("deduped size: "+dedupedSize);
                                            out.println("current deduplication ratio: "+locker.getDedupRatio());
                                            out.println(filename + " is added successfully");
                                            timerEnd = System.currentTimeMillis();
                                            out.println("Time used to store file <<" + filename + ">> via Dynamic Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                            out.println("-------------------------------------" + "\n");
                                            RunDedup.exportLocker(locker,lockerPath+File.separator+username);
                                            out.println("locker saved on the server");
                                        }
                                        else {
                                            out.println("Exact same file exists. Nothing is done.");
                                        }
                                    }
                                }
                                else {
                                    timerStart = System.currentTimeMillis();
                                    //manageDynamicSizeChunking(locker, filename, filePath, extension, fileSize, h);
                                    out.println("Storing " + filename + " using fixed size chunking");
                                    System.out.println("Storing " + filename + " using fixed size chunking");
                                    int chunkSize = h.getChunksize(extension, fileSize);
                                    long dedupedSize = h.fixedSizeChunk(locker, filePath, chunkSize);
                                    out.println("current locker size: "+locker.getcurrLockerSize());
                                    out.println("deduped size: "+dedupedSize);
                                    out.println("current deduplication ratio: "+locker.getDedupRatio());
                                    out.println(filename + " is added successfully");
                                    timerEnd = System.currentTimeMillis();
                                    out.println("Time used to store file <<" + filename + ">> via Dynamic Sized Chunking is " + (double) (timerEnd - timerStart)/1000 + " seconds." );
                                    out.println("-------------------------------------" + "\n");
                                    System.out.println("cnmb");
                                    File temp = new File(lockerPath+File.separator+username);
                                    System.out.println("path is"+lockerPath+File.separator+username);
                                    if(!temp.exists())
                                        temp.mkdir();
                                    RunDedup.exportLocker(locker,lockerPath+File.separator+username);
                                    out.println("locker saved on the server");
                                    file.delete();
                                }
                            }










                            out.println("end");
                        } catch (Exception e) {}
//



                    //
                        //
                    }
                    else out.println("reset");


                } while (true);



            } catch (IOException e) {

            }
        }

    }




    public static void main (String[] args) throws Exception{
        listener = new ServerSocket();
        listener.setReuseAddress(true);
        listener.bind(new InetSocketAddress(8888));
        listener.setReuseAddress(true);
        try {
            System.out.println("the Dedup server is running, waiting for the client");
            while (true) {
                new serverThread(listener.accept()).start();
                listener.setReuseAddress(true);
            }
        } catch (BindException bindEx){
            System.out.println("Try to close socket due to port bind issue");
            listener.close();
        }finally {
            //System.out.println("Try to close socket");
            listener.close();
        }
    }
}