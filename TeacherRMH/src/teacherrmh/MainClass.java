package teacherrmh;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.util.ArrayList;

/**
*
* @author Sam
* @author Joey
* @author Ameen
* @author Peter
*/
public class MainClass {
    //textField: the area where requests are displayed

    public static JTextArea textField;
    //ch: nothing for now
    private static char ch = 'd';
    //adds button to lower hand
    private static JButton lowerButton;
    //numSocks: the number of connections currently accepted
    private static int numSocks;
    //ss: the ServerSocket that runs the server
    private static ServerSocket ss;
    //removeFirstInLine: for the potential removal of the first name in the list
    public static boolean removeFirstInLine = false;
    //the arraylist for student connections
    public static ArrayList <Connection> connectionList = new ArrayList <Connection> ();

    /**
* @param args the command line arguments
*/
    public static void main(String[] args) {
        //Initialize the button to lower hands
        lowerButton = new JButton("Lower first hand!");
        //Initialize the text area
        textField = new JTextArea();
        //User can't edit the text area
        textField.setEditable(false);
        
        //Add action for when button is pressed
        lowerButton.addActionListener(new ActionListener() {
            //When the button is pressed, do this
            public void actionPerformed(ActionEvent e) {
                // Does this by searching for new line (if any) and removes the line
                int indexToRemove = textField.getText().indexOf("\n\r")+2;
                if (indexToRemove>1) {
                    String newText = textField.getText().substring( indexToRemove );
                    textField.setText(newText);
                    /*//Declare PrintWriter for this thread
PrintWriter out = null;
try {
//Attempt to initialize the PrintWriter for this thread
out = new PrintWriter(socket.getOutputStream(), true);
} catch (IOException ex) {
}
if (stateAssist == false) {
//If there is no help request:
//Send command to the server to put hand up
out.println("UP");
//Update the button
assistButton.setText("Hand is UP");
//Update the variable
stateAssist = true;
}*/
                }
            }
        });
        
        //For the potential of custom controls for the teacher, using key, not currently in use
        textField.addKeyListener(new KeyAdapter() {
            //In the event that a key is pressed
            public void keyPressed(KeyEvent event) {
                //get the char and store it in ch
                ch = event.getKeyChar();
                //If the space bar was pressed
                if (ch == ' ') {
                    //Potentially clear the entire text area
                    //textField.setText("");
                }
            }
        });
        //Set button size
        lowerButton.setSize(400, 50);
        //Set font to be bold and 24 size, verdana
        Font font = new Font("Verdana", Font.BOLD, 24);
        textField.setFont(font);
        //Declare and initialize JFrame
        JFrame jframe = new JFrame();
        //Set size of jframe
        //400, 600
        //Initialize JPanel
        JPanel panel = new JPanel();
        //Things added top to bottom
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        
        JLabel label = null;
        ImageIcon i = new ImageIcon(new MainClass().getClass().getResource("/teacherImage.jpg"));
        label = new JLabel(i);
        jframe.setSize(400, 600);
        jframe.setResizable(false);
        //Add the text area to the frame
        panel.add(label);
        //Add button to panel
        panel.add(lowerButton);
        panel.add(textField);
        jframe.add(panel);
        //show the frame
        jframe.setVisible(true);

        //listen to actions that happen to the window
        jframe.addWindowListener(new WindowAdapter() {
            //When the window is closed
            public void windowClosing(WindowEvent e) {
                //try to close the ServerSocket, which leaves the port open
                try {
                    ss.close();
                } catch (IOException ex) {
                }
            }
        });
        //Thread for operating the server
        Thread thread = new Thread() {
            public void run() {
                
                //Try to initialize the server on port 42421
                try {
                    ss = new ServerSocket(42421);
                } catch (BindException e) {
                } catch (IOException e) {
                }
                //Accept connections forever, or until the application is closed
                while (true) {
                    Socket socket = null;
                    try {
                        //Accept an incoming connection and store it to socket
                        socket = ss.accept();
                    } catch (IOException ex) {
                    }
                    //Create a new Connection with socket
                    Connection con = new Connection(socket, numSocks);
                    // adding the connection for student connections
                    connectionList.add(con);
                    //numSocks increases, keeping track of a new connection
                    numSocks++;
                }
            }
        };
        //Starts the thread
        thread.start();
    }
}