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
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 *
 * @author Sam
 */
public class MainClass {
    
    public static JTextArea textField;
    private static char ch = 'd';
    private static int num = 0, numSocks;
    private static ServerSocket ss;
    public static boolean removeFirstInLine = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        textField = new JTextArea();
        textField.setEditable(false);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                ch = event.getKeyChar();
                if (ch == ' ') {
                    textField.setText("");
                }
            }
        });
        Font font = new Font("Verdana", Font.BOLD, 24);
        textField.setFont(font);
        JFrame jframe = new JFrame();
        jframe.setSize(400, 600);
        jframe.add(textField);
        
        jframe.setVisible(true);
        
        jframe.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    ss.close();
                } catch (IOException ex) {
                }
            }
        });
        
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ss = new ServerSocket(42421);
                } catch (BindException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                }
                while (true) {
                    try {
                        Socket socket = ss.accept();
                        Connection con = new Connection(socket, numSocks);
                        numSocks++;
                    } catch (Exception e) {
                    }
                }
            }
        };
        thread.start();
    }
}
