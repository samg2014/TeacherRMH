/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package teacherrmh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sam
 */
public class Connection {

    //socket: the Socket that this connection is connected to
    Socket socket;
    Socket teacherToStudentSocket;
    //for reading from the socket
    BufferedReader input;
    //The username of the user on the other end of this connection
    String username;
    //The two threads that this connection uses
    Thread thread, thread2;
    //Is this connection still operating
    private volatile boolean running = true;

    public Connection(Socket s, int n) {
        //this socket is the one that represents the connection accepted in MainClass
        socket = s;
        //try to initialize the BufferedReader for this socket
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
        }
        //Thread for accepting commands fromt the client
        thread = new Thread( new Runnable(){
            public void run() {
                //while this connection is still operating
                while (running) {
                    String read = null;
                    //Read input from the socket
                    try {
                        read = input.readLine();
                    } catch (IOException ex) {
                    }
                    //Process the input read
                    processInput(read);
                    System.out.println(read);
                }
            }
        });
        //Start the thread
        thread.start();
        //Thread for sending the command for putting a client's hand down
        //Not currently functional
        //No comments on this for now
        thread2 = new Thread( new Runnable() {
            public void run() {
                System.out.println("Made thread");
                while (running) {
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        //out.println(" ");
                    } catch (Exception ex) {
                        System.out.println("DISCONNECTED");
                    }
                    //System.out.println("1st check: " + MainClass.textField.getText().indexOf(username));
                    if (MainClass.removeFirstInLine) {
                        System.out.println("Remove first");
                        if (MainClass.textField.getText().indexOf(username) == 4) {
                            System.out.println("Want to tell student down");
                            PrintWriter out = null;
                            try {
                                out = new PrintWriter(teacherToStudentSocket.getOutputStream(), true);
                                System.out.println("Told student down");
                            } catch (IOException ex) {
                            }
                            out.println("OWND");
                            MainClass.removeFirstInLine = false;
                        }
                    }
                }
            }
        });
        thread2.start();
    }
    
    public String getName (){
        return username;
    }
    
    public void processInput(String in) {
        //Get the text that is currently in the text field
        String txt = MainClass.textField.getText();

        //If the command for putting hand down is recieved
        if (in.equals("DOWN")) {
            //Find and remove the user's name if it begins with 'ASSIST - '
            try {
                txt = txt.substring(0, txt.indexOf("A - " + username)) + txt.substring(txt.indexOf("A - " + username) + username.length() + 6);
            }
            catch (StringIndexOutOfBoundsException e) {}
            //Update the text
            MainClass.textField.setText(txt);
        }

        //If the command for raising hand is recieved
        if (in.equals("UP")) {
            //Add the user's name with the ASSIST tag at the end
            txt = txt + "A - " + username + "\n\r";
            //Update the text
            MainClass.textField.setText(txt);
        }

        //If the user closes their application
        if (in.equals("QUIT")) {
            try {
                //Stop the thread from running
                running = false;
                //Close the socket
                socket.close();
            } catch (IOException ex) {
            }
        }

        //If the command for requesting help is recieved
        if (in.equals("GRADEME")) {
            //Add the user's name with the GRADE tag at the beginning
            txt = txt + "G - " + username + "\n\r";
            //Update the text
            MainClass.textField.setText(txt);
        }

        //If the command for retracting grading help is recieved
        if (in.equals("NOGRADE")) {
            //Remove the user's name if it has a 'GRADE -' at the beginning
            try {
                txt = txt.substring(0, txt.indexOf("G - " + username)) + txt.substring(txt.indexOf("G - " + username) + username.length() + 6);
            }
            catch (StringIndexOutOfBoundsException e) {}
            //Update the text
            MainClass.textField.setText(txt);
        }
        
        //If recieving the username
        if (in.contains("USERNAME:")) {
            //Set the variable username to the name recieved
            username = in.substring(in.indexOf(":") + 1);
        }
        
        if (in.contains("IPADDRESS:")) {
            System.out.println("Got IPA");
            while (true) {
                try {
                    System.out.println("Trying to make socket");
                    teacherToStudentSocket = new Socket( in.substring(in.indexOf(":") + 1) , 42422);
                    System.out.println("Made socket");
                    break;
                } catch (UnknownHostException ex) {
                } catch (IOException ex) {
                }
            }
        }
    }
}
