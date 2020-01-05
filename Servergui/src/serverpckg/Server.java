/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpckg;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import playerpckg.*;

/**
 *
 * @author Rehab
 */
public class Server {

    volatile boolean runServer = true;
    ServerSocket serverSocket;
    public static ArrayList<Pair<String, Player>> allPlayers = new ArrayList<>();
    public static ArrayList<Pair<Socket, Player>> activePlayersSockets = new ArrayList<>();

    public void startServer() {

        //here allPlayers List must be filled from database
        //allPlayers = DBManager object.getAllPlayers();
        try {
            serverSocket = new ServerSocket(5005);
            while (runServer) {
                Socket s = serverSocket.accept();
                new ConnectionHandler(s);
            }

            serverSocket.close();

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This function should be used to close the serverSocket and all threads,
     * called when close server button is pressed.
     */
    public void closeServer() {
        //Not Yet fully Implemented
        runServer = false;
    }

    class ConnectionHandler extends Thread {

        final static int LOGIN = 1;
        final static int INVITATION = 2;
        final static int LOGOUT = 4;

        DataInputStream dis;
        Socket clientSocket;

        public ConnectionHandler(Socket s) {
            try {
                clientSocket = s;
                dis = new DataInputStream(s.getInputStream());
                start();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {

            while (true) //may need to change it
            {
                try {
                    //Will need to implement the regex here to switch over a string
                    int requestCode = dis.readInt();
                    
                    switch (requestCode) {
                        case LOGIN:
                            ObjectInputStream playerStream = new ObjectInputStream(clientSocket.getInputStream());
                            Player p = (Player) playerStream.readObject();
                            activePlayersSockets.add(new Pair<Socket, Player>(clientSocket, p));
                            allPlayers.get(allPlayers.indexOf(p.getUsername())).getValue().setIsOnline(true);
                            playerStream.close();
                            break;
                            
                        case INVITATION:
                            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                            Player p2 = (Player) ois.readObject();
                            ois.close();
                            Socket player2Socket = new Socket();
                            for (Pair<Socket, Player> entry : activePlayersSockets) {
                                if (entry.getValue().getUsername().equals(p2.getUsername())) {
                                    player2Socket = entry.getKey();
                                    break;
                                }
                            }
                            boolean invitationRes = directInvitation(clientSocket, player2Socket);
                            if (invitationRes == false) {
                                sendInvitationRejection(clientSocket, player2Socket);
                            } else {
                                
                                //start game and send moves
                                
                            }
                            break;
                            
                        case LOGOUT:
                            Player pTemp = activePlayersSockets.get(activePlayersSockets.indexOf(clientSocket)).getValue();
                            activePlayersSockets.remove(activePlayersSockets.indexOf(clientSocket));
                            allPlayers.get(allPlayers.indexOf(pTemp.getUsername())).getValue().setIsOnline(false);
                            dis.close();
                            clientSocket.close();
                            break;
                    }
                } catch (IOException ex) {
                    try {
                        dis.close();
                        clientSocket.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        public boolean directInvitation(Socket p1Socket, Socket p2Socket) {

            DataInputStream dis;
            PrintStream ps;
            boolean invitationResponse = false;

            try {
                dis = new DataInputStream(p2Socket.getInputStream());
                ps = new PrintStream(p2Socket.getOutputStream());
                String message = activePlayersSockets.get(activePlayersSockets.indexOf(p1Socket)).getValue().getFirstName() + " "
                        + activePlayersSockets.get(activePlayersSockets.indexOf(p1Socket)).getValue().getLastName()
                        + " has invited you to play. What do you think?";

                ps.println(message);
                invitationResponse = dis.readBoolean(); //player 2 either accepts or declines it

                dis.close();
                ps.close();

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            return invitationResponse;
        }

        public void sendInvitationRejection(Socket p1Socket, Socket p2Socket) {

            PrintStream ps;
            try {
                ps = new PrintStream(p1Socket.getOutputStream());
                String message = activePlayersSockets.get(activePlayersSockets.indexOf(p2Socket)).getValue().getFirstName() + " "
                        + activePlayersSockets.get(activePlayersSockets.indexOf(p2Socket)).getValue().getLastName()
                        + " has rejected your invitation.";

                ps.println(message);

                ps.close();

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void sendMoves(String p2Username, char gameChar, int index)
        {
            //needs implementation
        }
    }
}
