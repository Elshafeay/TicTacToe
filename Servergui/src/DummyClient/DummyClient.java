/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DummyClient;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.json.JSONObject;

public class DummyClient extends JFrame {
    String str;
    Socket s;
    DataInputStream dis;
    PrintStream ps;
    
    public DummyClient() throws IOException{
        s = new Socket("127.0.0.1", 5005);
        dis = new DataInputStream(s.getInputStream());
        ps = new PrintStream(s.getOutputStream());
        new Thread(new Runnable(){
            public void run(){
                    try {
                        JSONObject Sjson = new JSONObject();
                        Sjson.put("code", "LOGIN");
                        Sjson.put("username", "elshafeay");
                        Sjson.put("password", "123456");
                        ps.println(Sjson);
                        JSONObject Rjson=new JSONObject(dis.readLine());
                        System.out.println(Rjson.toString());
                    } catch (IOException ex) {
                        Logger.getLogger(DummyClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        }).start();
    }

    public static void main(String[] args) throws IOException{
        new DummyClient();
    }
}
