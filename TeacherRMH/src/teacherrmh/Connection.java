/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package teacherrmh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Sam
 */
public class Connection {

    //socket: the Socket that this connection is connected to
    Socket socket;
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
        thread = new Thread() {
            @Override
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
                }
            }
        };
        //Start the thread
        thread.start();
        //Thread for sending the command for putting a client's hand down
        //Not currently functional
        //No comments on this for now
//        thread2 = new Thread() {
//            @Override
//            public void run() {
//                while (running) {
//                    try {
//                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                        out.println(" ");
//                    } catch (Exception ex) {
//                        System.out.println("DISCONNECTED");
//                    }
//                    //System.out.println("1st check: " + MainClass.textField.getText().indexOf(username));
//                    if (MainClass.removeFirstInLine) {
//
//                        if (MainClass.textField.getText().indexOf(username) == 0) {
//                            PrintWriter out = null;
//                            try {
//                                out = new PrintWriter(socket.getOutputStream(), true);
//                            } catch (IOException ex) {
//                            }
//                            out.println("DOWN");
//                            MainClass.removeFirstInLine = false;
//                        }
//                    }
//                }
//            }
//        };
//        thread2.start();
    }

    public void processInput(String in) {
        //Get the text that is currently in the text field
        String txt = MainClass.textField.getText();

        //If the command for putting hand down is recieved
        if (in.equals("DOWN")) {
            //Find and remove the user's name if it begins with 'ASSIST - '
            txt = txt.substring(0, txt.indexOf("ASSIST - " + username)) + txt.substring(txt.indexOf("ASSIST - " + username) + username.length() + 11);
            //Update the text
            MainClass.textField.setText(txt);
        }

        //If the command for raising hand is recieved
        if (in.equals("UP")) {
            //Add the user's name with the ASSIST tag at the end
            txt = txt + "ASSIST - " + username + "\n\r";
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
            txt = txt + "GRADE - " + username + "\n\r";
            //Update the text
            MainClass.textField.setText(txt);
        }

        //If the command for retracting grading help is recieved
        if (in.equals("NOGRADE")) {
            //Remove the user's name if it has a 'GRADE -' at the beginning
            txt = txt.substring(0, txt.indexOf("GRADE - " + username)) + txt.substring(txt.indexOf("GRADE - " + username) + username.length() + 10);
            //Update the text
            MainClass.textField.setText(txt);
        }
        
        //If recieving the username
        if (in.contains("USERNAME:")) {
            //Set the variable username to the name recieved
            username = in.substring(in.indexOf(":") + 1);
        }
    }
}
