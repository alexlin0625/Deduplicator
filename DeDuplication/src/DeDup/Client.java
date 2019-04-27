package DeDup;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client {

/*to use our client, you need to change the SERVER_IP to your server ip, which you can get by ifconfig
or ipconfig on the server computer
you need to change the final String pathname into your own, where the file retrieved in the server will be sent
back to the client and then stored here
*/


    public static void main(String[] args) throws IOException {
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        NetworkGUI GUI = new NetworkGUI();
        final String SERVER_IP = "192.168.56.1";      //change into your own server IP
        int messageNum=0;
        final String pathname="C:\\Users\\sjqgo\\Desktop\\testFileReceive1";     //change into your own.
        // The file sent from the server will be stored here
        socket = new Socket(SERVER_IP, 8888);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        GUI.updateInfo(in.readLine());
        GUI.updateInfo("please log in to use our deduplicator");


        File dir = new File(pathname);
        if(!dir.exists())
            dir.mkdir();
        GUI.jbCreateLocker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(GUI.jtLockerNameInput.getText().length()==0) {
                    GUI.updateInfo("please input the locker name you want to create");
                    return;
                }
                out.println("create");
                String lockername = GUI.jtLockerNameInput.getText();
                out.println(lockername);
                String ans;
                try {
                    ans = in.readLine();
                    //System.out.println(ans);
                    GUI.updateInfo(ans);
                    GUI.updateInfo("-------------------------------------");
                } catch (IOException e1) {

                }
            }
        });
        GUI.jbLoadLocker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(GUI.jtLockerNameInput.getText().length()==0) {
                    GUI.updateInfo("please input the locker name you want to load");
                    return;
                }
                out.println("load");
                String lockername = GUI.jtLockerNameInput.getText();
                out.println(lockername);
                String ans;
                try {

                    ans = in.readLine();
                    if(ans.equals("noLocker")) {
                        GUI.updateInfo("no such locker yet, please create one first");
                        return;
                    }
                    else if(ans.equals("log in first please")) {
                        GUI.updateInfo("log in first please");
                        return;
                    }
                    else {
                        GUI.updateInfo("current locker: "+lockername);
                        GUI.updateInfo("-------------------------------------");
                        return;
                    }
                } catch (IOException e2) {

                }
            }
        });
        GUI.jbInsert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] selectFileName = new String[50];
                    String[] selectFilePath = new String[50];
                    int selectFileNum = 0;

                    //get filename[] and filepath

                    JFrame jf = new JFrame();
                    JFileChooser addChooser = new JFileChooser();
                    addChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    //该方法设置为true允许选择多个文件
                    addChooser.setMultiSelectionEnabled(true);
                    int returnval = addChooser.showOpenDialog(jf);
                    if (returnval == JFileChooser.APPROVE_OPTION) {
                        File[] files = addChooser.getSelectedFiles();
                        String str = "";
                        selectFileNum = files.length;
                        selectFileName = new String[selectFileNum];
                        selectFilePath = new String[selectFileNum];
                        int i = 0;
                        for (File file : files) {
                            //System.out.println("filename: "+file.getName());
                            selectFileName[i] = file.getName();
                            str = file.getPath();
                            selectFilePath[i] = str;
                            i++;
                            //System.out.println("str: "+str);
                        }
                        if(i>1) {
                            GUI.updateInfo("you can only choose one file");
                            return;
                        }

                    }







                    //end
                    /*if(GUI.jtFileNameInput.getText().length()==0) {
                        GUI.updateInfo("please input the file name you want to store");
                        return;
                    }*/
                    out.println("store");
                    String ans = in.readLine();
                    if(ans.equals("reset"))
                        return;
                    if(ans.equals("log in first please")) {
                        GUI.updateInfo(ans);
                        return;
                    }
                    if(ans.equals("noLocker")) {
                        GUI.updateInfo("please create or load a locker first");
                        return;
                    }
                    out.println(selectFileNum);

                    //start to send the file to the server
                    File file;
                    for (int i = 0; i < selectFileNum; i++) {
                        //prepare to send file
                        file = new File(selectFilePath[i]);
                        DataInputStream dis=new DataInputStream(new FileInputStream(file));
                        DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
                        dos.writeLong(file.length());
                        dos.writeUTF(file.getName());
                        //System.out.println("name："+file.getName());
                        //System.out.println("length："+file.length());
                        int count=-1,sum=0;
                        byte[] buffer=new byte[1024*1024];
                        while((count=dis.read(buffer))!=-1){
                            dos.write(buffer,0,count);
                            sum+=count;
                            //System.out.println("already sent "+sum+" bytes");
                        }
                        System.out.println("file sent successfully");


                        dos.flush();
                        while(true) {
                            ans = in.readLine();
                            if(ans.equals("end"))
                                break;
                            if(ans.equals("go"))
                                continue;
                            if(ans.equals("reset"))
                                return;
                            //System.out.println(ans);
                            GUI.updateInfo(ans);
                        }


                    }
                    return;


                } catch (Exception e1) {}}
        });
        GUI.jbRetrieve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(GUI.jtFileNameInput.getText().length()==0) {
                    GUI.updateInfo("please input the file name you want to retrieve");
                    return;
                }
                out.println("retrieve");
                out.println(GUI.jtFileNameInput.getText());
                String ans;
                try {
                    ans = in.readLine();
                    if(ans.equals("log in first please")) {
                        GUI.updateInfo("log in first please");
                        return;
                    }
                    if(ans.equals("reset"))
                        return;
                    if(ans.equals("no")) {
                        GUI.updateInfo("no such file in the locker");
                        out.println("reset");
                        return;
                    }

                    File file;

                    //try to receive file from the server

                    //System.out.println("prepare to receive the file from server");
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    //System.out.println("new dis!");
                    long length=dis.readLong();
                    String name=dis.readUTF();
                    //System.out.println("length="+length);
                    //System.out.println("name="+name);
                    //System.out.println("name="+name);
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(pathname+"\\"+name)));
                    int count=-1,sum=0;
                    byte[] buffer=new byte[1024*1024];
                    while ((count = dis.read(buffer)) != -1) {
                        dos.write(buffer, 0, count);
                        sum += count;
                        //System.out.println("receive" + sum + "bits");
                        if (sum == length) {
                            break;
                        }
                    }
                    dos.flush();
                    //System.out.println(name + "received");
                    ans = in.readLine();
                    GUI.updateInfo(ans);
                    ans = in.readLine();
                    GUI.updateInfo(ans);
                    ans = in.readLine();
                    GUI.updateInfo(ans);
                    return;
                } catch (IOException e1) {};

                String path=pathname;

            }
        });

        GUI.jbDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(GUI.jtFileNameInput.getText().length()==0) {
                    GUI.updateInfo("please input the file name you want to delete");
                    return;
                }
                out.println("delete");
                out.println(GUI.jtFileNameInput.getText());
                String ans;
                try {
                    ans = in.readLine();
                    if (ans.equals("no")) {
                        GUI.updateInfo("no such files in the locker");
                        return;
                    } else if (ans.equals("log in first please")){
                        GUI.updateInfo(ans);
                        return;
                    }

                    GUI.updateInfo(ans);
                } catch (IOException e1) {};
            }
        });



        GUI.jbLockerInfoDisplay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("show locker info!");

                out.println("lockerInfo");
                String ans;
                try {
                    GUI.jtServerInfo.setText("");
                    GUI.messageNum=0;
                    while(true) {
                        ans = in.readLine();
                        if(ans.equals("end"))
                            break;
                        if(ans.equals("log in first please")) {
                            GUI.updateInfo(ans);
                            return;
                        }
                        if(ans.equals("reset"))
                            return;
                        else
                            GUI.updateInfo(ans);
                    }
                } catch (Exception e1) {};
            }
        });




        GUI.login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("try to log in!");
                String username = GUI.username.getText();
                //System.out.println(username);
                char[] password = GUI.password.getPassword();
                int hash = (new String(GUI.password.getPassword())).hashCode();
                //System.out.println(password);
                //System.out.println(hash);
                out.println("logIn");
                out.println(username);
                out.println(hash);
                String ans;
                try {
                    ans = in.readLine();
                    if(ans.equals("yes")) {
                        GUI.updateInfo("logged into the server");
                    }
                    else {
                        GUI.updateInfo("incorrect user information, please try again");
                    }
                } catch (Exception e1) {};
            }
        });

    }

}