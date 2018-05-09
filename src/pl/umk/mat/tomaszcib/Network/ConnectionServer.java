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

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Handles in-game connection if the user decided to host a game.
 */
public class ConnectionServer extends Connection{
    private ServerSocket serverSocket;

    /**
     * Runs a TCP listener.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sock = serverSocket.accept();
            createStreams();
            MainWindow.connection.writeToPeer((byte)0);
            MainWindow.setMode(2);
            while (true) {
                readFromPeer();
                if(forceClose) break;
            }
        }
        catch (IOException e) { }
        MainWindow.setMode(0);
    }

    /**
     * Forces disconnection with the client and closes the IO streams and server's socket.
     */
    @Override
    public void close() {
        closeStreams();
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new server socket on a selected port.
     * @param port port on which server should work.
     */
    public ConnectionServer(int port){
        this.port = port;
        serverSocket = null;
        forceClose = false;
    }
}
