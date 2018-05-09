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

package pl.umk.mat.tomaszcib.GameLogic;

import pl.umk.mat.tomaszcib.MainWindow;

import java.util.Vector;

/**
 * A class containing basic information on an in-game player.
 */
public class PlayerData{
    private String name;
    /**
     * A player's tray of captured enemy pieces.
     */
    public Tray tray = new Tray();

    /**
     * Returns player's in-game name.
     * @return player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets player's name
     * @param name player's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Tray class as used for {@link #tray} variable.
     */
    public class Tray {
        /**
         * A vector of captured pieces IDs. Treated as reference to the {@link CurrentBoard#piece} array.
         */
        public Vector<Integer> pieceId = new Vector<Integer>();
        /**
         * A table containing information which types of pieces can be dropped on various board fields.
         */
        public byte[] drop = new byte[81];
    }
    private int id;
    /** Sets ID of the player. The ID is treated as a reference for other objects.
     * @param id value of a user's ID, 0 or 1.
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Total number of seconds the player has been playing for.
     */
    public int timerSecondElapsed = 0;
    /**
     * Individual time counter for the player.
     */
    public Timer timer = new Timer();

    /**
     * Calls {@link pl.umk.mat.tomaszcib.GuiAssets.LabelPanel#setTimer(int, int)} and updates the proper label
     * on the window's bottom panel.
     */
    public void updateTimerLabel(){
        MainWindow.labelPanel.setTimer(id, timerSecondElapsed);
    }

    /**
     * Simple time counting thread. Timer is uptated once every 1 second and synchronization with the partner
     * occurs once in 6 seconds.
     */
    public class Timer extends Thread{
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    sleep(1000);
                    timerSecondElapsed++;
                    updateTimerLabel();
                    if(timerSecondElapsed % 30 == 29)
                        MainWindow.connection.writeToPeer((byte)5, id,timerSecondElapsed, "SYNC");
                } catch (InterruptedException e) {
                    //no need to print stack trace, this exception will most likely occur while exiting the program
                    //e.printStackTrace();
                }

            }
        }
    }


}
