package teacherrmh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    
    private PrintWriter out;

    public Connection(Socket s, int n) {
        //this socket is the one that represents the connection accepted in MainClass
        socket = s;
        try {
            out = new PrintWriter(s.getOutputStream(),true);
        } catch (IOException ex) {
        }
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
        
        if (in.contains("REMOTEDOWN"))
        {
            MainClass.lowerButton.doClick();
        }
    }
    
    public void send(String s)
    {
        out.println(s);
    }
}