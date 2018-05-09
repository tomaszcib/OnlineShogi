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


package pl.umk.mat.tomaszcib.Network;


import pl.umk.mat.tomaszcib.MainWindow;

import java.io.*;
import java.net.Socket;

/**
 * Class for handling in-game connection and communication process with the partner.
 * Class itself is abstract, to be fully functional, must be declared either as
 * {@link ConnectionServer} or {@link ConnectionClient} within the program.
 */
public abstract class Connection extends Thread{
    int port;
    String host;
    private BufferedReader is = null;
    private PrintStream os = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    String received;
    boolean forceClose;
    Socket sock = null;

    public abstract void run();
    public abstract void close();

    void createStreams(){
        try {
            is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            os = new PrintStream(sock.getOutputStream());
            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());
        }
        catch(IOException e) { e.printStackTrace(); }
    }
    void closeStreams(){
        if(oos != null) writeToPeer((byte)4);
        try {
            if (oos != null) oos.close();
            if (ois != null) ois.close();
            if (sock != null) sock.close();
            forceClose = true;
        } catch(IOException e) { e.printStackTrace();}
    }

    void readFromPeer(){
        try {
            byte msgType = ois.readByte();
            switch(msgType){
                case 0:
                    MainWindow.canvas.currentBoard.loadGameFromStream(ois);
                    MainWindow.canvas.currentBoard.pdata[0].setName((String)ois.readObject());
                    writeToPeer((byte)1,MainWindow.canvas.currentBoard.pdata[1].getName()+"\r");
                    MainWindow.setMode(2);
                    break;
                case 1:
                    MainWindow.canvas.currentBoard.pdata[1].setName((String)ois.readObject());
                    break;
                case 2:
                    int id = (int)ois.readObject();
                    int from = (int)ois.readObject();
                    int to = (int)ois.readObject();
                    boolean shouldPromote = (boolean)ois.readObject();
                    String ctrl = (String)ois.readObject();
                    MainWindow.canvas.doMoveOnCanvas(id,from,to,shouldPromote,false);
                    MainWindow.canvas.currentBoard.updateMoves();
                    MainWindow.canvas.updateLabel();
                    break;

                case 3:
                    MainWindow.panel.a.addMessage((byte)2, (String)ois.readObject());
                    break;
                case 4:
                    close();
                    break;
                case 5:
                    int id2 = (int)ois.readObject();
                    MainWindow.canvas.currentBoard.pdata[id2].timerSecondElapsed
                            = (int)ois.readObject();
                    String ctrl2 = (String)ois.readObject();
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            forceClose = true;
        }

        /* Interpretacja wiadomości */

    }

    /**
     * This method sends an information to the partner, using a socket declared in the same class instance.
     * @param b type of message. In-game communication protocol allows the following types of messages:
     *          <ul>
     *          <li><b>0:</b> a welcoming message sent by a server to a joining client. The message follows the in-game
     *          format of <i>*.gam</i> files and is followed by hosting player's name</li>
     *          <li><b>1:</b> a welcoming message sent by a joining client to a server. Contains client's displayed name.</li>
     *          <li><b>2:</b> message containing information that the player has performed a move.</li>
     *          <li><b>3:</b> chat message.</li>
     *          <li><b>4:</b> disconnection message.</li>
     *          <li><b>5:</b> players' timers synchronization message.</li>
     *          </ul>
     * @param objects various objects depending on type of the message.
     *                <ul>
     *                <li><b>0:</b> no objects</li>
     *                <li><b>1:</b> (String) -> (user's name)</li>
     *                <li><b>2:</b> (int, int, int, boolean, String) ->
     *                (pieceId, source field, target field, should promote after move?, control string)</li>
     *                <li><b>3:</b> (String) -> (chat message to be displayed)</li>
     *                <li><b>4:</b> no objects</li>
     *                <li><b>5:</b> (int, int, String) -> (player ID, value of player's counter, control string)</li>
     *                </ul>
     */
    public void writeToPeer(byte b, Object... objects){
        try {
            oos.writeByte(b);
            if(b == 0) {
                MainWindow.canvas.currentBoard.saveCurrentGameToStream(oos);
                oos.writeObject(MainWindow.canvas.currentBoard.pdata[0].getName()+"\r");
                b = ois.readByte();
                if(b != 1) {System.err.println("Connectione error"); return; }
                MainWindow.canvas.currentBoard.pdata[1].setName((String)ois.readObject());
                MainWindow.setMode(2);
            }
            else
                for(Object o : objects)
                    oos.writeObject(o);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
