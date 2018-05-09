/**
 * Shogi - a simple online multiplayer game of Japanese chess.
 * Copyright (C) 2018 Tomasz Ciborski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package pl.umk.mat.tomaszcib.GuiAssets;

import pl.umk.mat.tomaszcib.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * In-game chat panel, embedded in the {@link TabPanel}.
 */
public class PanelChat extends JPanel {
    private JTextArea box;
    private JTextArea msg;
    private JScrollPane boxScrl, msgScrl;
    private JButton sendButton;
    private GridBagLayout mainLayout;

    /**
     * Constructor for the panel. Called from within {@link TabPanel#TabPanel()} constructor.
     */
    PanelChat(){
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;

        box = new JTextArea();
        boxScrl = new JScrollPane(box);
        c.gridx = 0;
        c.gridy = 0;
        add(boxScrl, c);
        box.setEditable(false);
        box.setLineWrap(true);

        c.weighty = 0.1;
        c.gridwidth = 1;
        msg = new JTextArea();
        msgScrl = new JScrollPane(msg);
        c.gridy = 1;
        add(msgScrl, c);
        msg.setLineWrap(true);
        msg.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) { }
            public void keyPressed(KeyEvent keyEvent) { }
            public void keyReleased(KeyEvent keyEvent) {
                if(!msg.getText().isEmpty() && keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                    sendMessage();
            }
        });

        c.fill = GridBagConstraints.HORIZONTAL;
        sendButton = new JButton(Local.str[17]);
        c.gridy = 2;
        c.gridx = 0;
        c.weightx = 0.1;
        c.weighty = 0;
        add(sendButton,c);
        setVisible(true);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!msg.getText().isEmpty())
                    sendMessage();
            }
        });
    }

    /**
     * Function called from within {@link MainWindow#setMode(int)}.
     * Sets availability of the chat depending on the mode of the game
     * @param mode target mode to switch to.
     */
    public void setChatMode(int mode){
        if(mode < 2){
            msg.setEnabled(false);
            sendButton.setEnabled(false);
        }
        else{
            msg.setEnabled(true);
            sendButton.setEnabled(true);
        }
    }

    private void sendMessage(){
        String s = msg.getText() + "\r";
        addMessage((byte)1, s);
        MainWindow.connection.writeToPeer((byte)3,s);
        msg.setText("");
    }

    /**
     * Adds a new message to the chat display box.
     * @param type type of the message. Valid values of this variable are:
     *             <ul>
     *             <li><b>0: </b>system message.</li>
     *             <li><b>1:</b> own message.</li>
     *             <li><b>2:</b> message from the partner.</li>
     *             </ul>
     * @param m content of the message.
     */
    public void addMessage(byte type, String m){
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        if(type == 1)
            timeStamp = MainWindow.canvas.currentBoard.pdata[MainWindow.canvas.getPlayerPov() == -1 ? 0 : 1].getName()
                    + " " + timeStamp;
        else if(type == 2)
            timeStamp = MainWindow.canvas.currentBoard.pdata[MainWindow.canvas.getPlayerPov() == -1 ? 1 : 0].getName()
                    + " " + timeStamp;
        /* Systemowe */
        if(type == 0) m = "[" + timeStamp + ": " + m + "]\n";
        /* Własne */
        else {
            m = "[" + timeStamp + "]:\n" + m;
        }
        box.append(m + "\n");
    }
}
