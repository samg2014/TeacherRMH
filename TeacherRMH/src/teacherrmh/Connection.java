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

/**
 * 
 * @author Sam
 */
public class Connection {

    Socket socket;
    BufferedReader input;
    String username;
    Thread thread, thread2;
    private volatile boolean running = true;
    
    public Connection(Socket s, int n) {
        socket = s;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
        }
        thread = new Thread() {
            @Override
            public void run() {
                while (running) {
                    try {
                        String read = input.readLine();
                        System.out.println(read);
                        processInput(read);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        thread2 = new Thread() {
            @Override
            public void run() {
                while (running) {
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println(" ");
                    } catch (Exception ex) {
                        System.out.println("DISCONNECTED");
                    }
                    //System.out.println("1st check: " + MainClass.textField.getText().indexOf(username));
                    if (MainClass.removeFirstInLine) {
                        
                        if (MainClass.textField.getText().indexOf(username) == 0) {
                            PrintWriter out = null;
                            try {
                                out = new PrintWriter(socket.getOutputStream(), true);
                            } catch (IOException ex) {
                            }
                            out.println("DOWN");
                            MainClass.removeFirstInLine = false;
                        }
                    }
                }
            }
        };
        thread2.start();
    }

    public void processInput(String in) {
        String txt = MainClass.textField.getText();
        if (in.equals("DOWN")) {
            txt = txt.substring(0, txt.indexOf(username + "-")) + txt.substring(txt.indexOf(username + "-") + username.length() + 3);
            MainClass.textField.setText(txt);
        }
        if (in.equals("UP")) {
            txt = txt + username + "-\n\r";
            MainClass.textField.setText(txt);
        }
        if (in.equals("QUIT")) {
            try {
                running = false;
                socket.close();
                
            } catch (IOException ex) {
            }
        }
        
        if (in.equals("GRADEME"))
        {
            txt = txt + "-" + username + "\n\r";
            MainClass.textField.setText(txt);
        }
        
        if (in.equals("NOGRADE"))
        {
            txt = txt.substring(0, txt.indexOf("-" + username)) + txt.substring(txt.indexOf("-" + username) + username.length() + 2);
            MainClass.textField.setText(txt);
        }
        if (in.contains("USERNAME:")) {
            username = in.substring(in.indexOf(":") + 1);
        }
    }
}
