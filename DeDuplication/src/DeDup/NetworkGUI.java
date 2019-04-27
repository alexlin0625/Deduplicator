package DeDup;

import java.awt.*;
import javax.swing.*;
import javax.swing.JScrollPane;

public class NetworkGUI extends JFrame {
    public static void main(String args[]) {
        NetworkGUI demo = new NetworkGUI();
    }

    int messageNum = 0;


    public void updateInfo(String s) {
        if(this.messageNum>=50) {
            this.jtServerInfo.setText("");
            this.messageNum=0;
        }
        this.messageNum++;
        this.jtServerInfo.append(s+"\n");
    }

    public NetworkGUI() {
        init();
        this.setSize(800,800);
        this.setVisible(true);
    }
    public void init() {
        title = new JPanel();
        title.setBorder(BorderFactory.createEtchedBorder());
        title.add(new JLabel("Dedup client"));
        username = new JTextField("username");
        password = new JPasswordField("password");
        login = new JButton("log in");
        contact = new JPanel();
        contact.setBorder(BorderFactory.createEtchedBorder());
        contact.add(new JLabel("contact us by sjq@bu.edu"));
        space = new JPanel();
        space2 = new JPanel();
        space3 = new JPanel();
        space4 = new JPanel();
        jlLockerName = new JPanel();
        jlLockerName.setBorder(BorderFactory.createEtchedBorder());
        jlLockerName.add(new JLabel("locker name"));
        jtLockerNameInput = new JTextField();
        jlLockerInfo = new JPanel();
        jlLockerInfo.setBorder(BorderFactory.createEtchedBorder());
        JLabel lockerInfo = new JLabel("current locker: no locker yet");
        jlLockerInfo.add(lockerInfo);
        jbCreateLocker = new JButton("create a locker");
        jbLoadLocker = new JButton("load a locker");
        jbLockerInfoDisplay = new JButton("check locker info");
        jlFileName = new JPanel();
        jlFileName.setBorder(BorderFactory.createEtchedBorder());
        jlFileName.add(new JLabel("for EC504"));
        jtFileNameInput = new JTextField();
        jpFileInfo = new JPanel();
        jpFileInfo.setBorder(BorderFactory.createEtchedBorder());
        JLabel fileInfo = new JLabel("Alex, Fuyao Wang, Gang Wang, Jiaqi Sun, Ruotian Liu ");
        jpFileInfo.add(fileInfo);
        jbInsert = new JButton("select a file on the client to store");
        jbRetrieve = new JButton("retrieve a file");
        jbDelete = new JButton("delete a file");
        jtServerInfo.setEditable(false);
        JScrollPane serverInfoScroll = new JScrollPane(jtServerInfo,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        serverInfoScroll.setPreferredSize(new Dimension(400,250));
        GridBagLayout layout = new GridBagLayout();
        this.setLocation(600,100);
        this.setLayout(layout);

        this.add(title);

        this.add(username);
        this.add(password);
        this.add(login);
        this.add(contact);


        this.add(space);

        this.add(jtLockerNameInput);
        this.add(jbCreateLocker);
        this.add(jbLoadLocker);
        this.add(jbLockerInfoDisplay);

        this.add(space2);


        this.add(jtFileNameInput);
        this.add(jbRetrieve);
        this.add(jbDelete);
        this.add(jbInsert);

        //this.add(jtServerInfo);
        this.add(serverInfoScroll);





        this.add(jlFileName);

        this.add(jpFileInfo);






        GridBagConstraints s= new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;


        s.gridwidth=0;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(title, s);

        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(username, s);
        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(password, s);
        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(login, s);
        s.gridwidth=0;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(contact, s);
        s.gridwidth=0;
        s.weightx = 0;
        s.weighty=0.00;
        layout.setConstraints(space, s);
        s.gridwidth=0;
        s.weightx = 0;
        s.weighty=0.00;
        layout.setConstraints(space2, s);



        s.gridwidth=2;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jlLockerName, s);
        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jtLockerNameInput, s);
        s.gridwidth=0;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jlLockerInfo, s);
        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jbCreateLocker, s);
        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jbLoadLocker, s);
        s.gridwidth=0;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jbLockerInfoDisplay, s);
        s.gridwidth=2;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jlFileName, s);
        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jtFileNameInput, s);
        s.gridwidth=0;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jpFileInfo, s);

        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jbRetrieve, s);
        s.gridwidth=3;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jbDelete, s);
        s.gridwidth=0;
        s.weightx = 0;
        s.weighty=0;
        layout.setConstraints(jbInsert, s);




        s.gridwidth=0;
        s.weightx = 1;
        s.weighty=1;
        layout.setConstraints(serverInfoScroll, s);
    }
    JPanel title;
    JTextField username;
    JPasswordField password;
    JButton login;
    JPanel contact;
    JPanel space;
    JPanel space2;
    JPanel space3;
    JPanel space4;
    JPanel jlLockerName;
    JTextField jtLockerNameInput;
    JPanel jlLockerInfo;
    JButton jbCreateLocker;
    JButton jbLoadLocker;
    JButton jbLockerInfoDisplay;
    JPanel jlFileName;
    JTextField jtFileNameInput;
    JPanel jpFileInfo;
    JButton jbInsert;
    JButton jbRetrieve;
    JButton jbDelete;
    JTextArea jtServerInfo = new JTextArea(8, 60);
    JScrollPane scrollV = new JScrollPane (jtServerInfo);
    JScrollPane scrollH = new JScrollPane (jtServerInfo);




}