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
import java.net.Socket;

/**
 * Handles in-game connection if the user decided to join a game.
 */
public class ConnectionClient extends Connection{

    /**
     * Runs a TCP listener.
     */
    @Override
    public void run() {
        try {
            sock = new Socket(host, port);
            createStreams();
            while(true){
                readFromPeer();
                if(forceClose) break;
            }
        } catch (IOException e) { }
        MainWindow.setMode(0);
    }

    /**
     * Forces disconnection from the server and closes the IO streams.
     */
    @Override
    public void close() {
        closeStreams();
    }

    /**
     * Creates new connection to the selected host and port.
     * @param host target host name or address.
     * @param port target port.
     */
    public ConnectionClient(String host, int port){
        this.host = host;
        this.port = port;
        forceClose = false;
    }
}
