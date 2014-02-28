/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package teacherrmh;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
/**
 *
 * @author Sam
 */
public class MainClass {
    //textField: the area where requests are displayed
    public static JTextArea textField;
    //ch: nothing for now
    private static char ch = 'd';
    //numSocks: the number of connections currently accepted
    private static int numSocks;
    //ss: the ServerSocket that runs the server
    private static ServerSocket ss;
    //removeFirstInLine: for the potential removal of the first name in the list
    public static boolean removeFirstInLine = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Initialize the text area
        textField = new JTextArea();
        //User can't edit the text area
        textField.setEditable(false);
        //For the potential of custom controls for the teacher, using key, not currently in use
        textField.addKeyListener(new KeyAdapter() {
            @Override
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
        BufferedImage img;
        ImageIcon i;
        i = new ImageIcon(new MainClass().getClass().getResource("/image.jpg"));
        //Image image = Toolkit.getDefaultToolkit().getImage("\\src\\image.jpg");
        label = new JLabel(i);
        jframe.setSize(400, 600);
        jframe.setResizable(false);
        //Add the text area to the frame
        //panel.add(label);
        panel.add(textField);
        jframe.add(panel);
        //show the frame
        jframe.setVisible(true);

        //listen to actions that happen to the window
        jframe.addWindowListener(new WindowAdapter() {
            @Override
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
            @Override
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
                    //Num socks increases
                    numSocks++;
                }
            }
        };
        //Starts the thread
        thread.start();
    }

}
