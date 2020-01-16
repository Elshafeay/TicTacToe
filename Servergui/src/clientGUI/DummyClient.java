/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientGUI;

import clientConnection.Client;
import java.io.IOException;
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
import org.json.JSONException;
import org.json.JSONObject;

public class DummyClient {

    public static void main(String[] args) throws IOException {
        System.out.println("Test Start");
        Client.startConnection();
        new Thread(new Runnable() {
            public void run() {
                JSONObject Sjson = new JSONObject();
                try {
                    Sjson.put("code", "LOGIN");
                    Sjson.put("username", "elshafeay");
                } catch (JSONException ex) {
                    Logger.getLogger(DummyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    Sjson.put("password", "123456");
                } catch (JSONException ex) {
                    Logger.getLogger(DummyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                Client.serverPrintStream.println(Sjson);
                Client.receive();
                System.out.println(Client.Rjson.toString());
                try {
                    Client.closeConnection();
                } catch (IOException ex) {
                    Logger.getLogger(DummyClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }).start();
    }

}
