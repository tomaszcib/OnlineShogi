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

import java.util.Vector;

/**
 * Extends {@link Piece} class and provides functions that are exclusive for a Shogi in-game King.
 */
public class King extends Piece{
    private int checkCount;
    private Vector<Integer> checkSources = new Vector<Integer>();
    private boolean checkmate;
    private boolean[] safeZone = new boolean[81];

    /**
     * Constructor for the piece.
     * @param player king's owner (-1 or 1)
     * @param pos king's initial position.
     */
    public King(int player, int pos){
        super(player, PieceType.KING, pos);
        this.checkCount = 0;
        this.checkmate = false;
        clearSafeZone();
    }

    /**
     * Checks if the king is checked by an enemy piece.
     * @return if king is checked by an enemy piece or not.
     */
    public boolean isCheck() {
        return checkSources.size() > 0;
    }

    /**
     * Adds new threat to the king by an enemy piece.
     * @param source where the threat is located (0-80).
     */
    public void addCheck(int source) {
        if(!checkSources.contains(source))
            checkSources.add(source);
    }

    /**
     * Returns number of checking enemy pieces.
     * @return number of checking enemy pieces.
     */
    public int getCheckCount(){
        return checkSources.size();
    }

    /**
     * Returns position of the first checking enemy piece.
     * @return position of the first checking enemy piece.
     */
    public int getFirstCheckSource(){
        return checkSources.get(0);
    }

    /**
     * Clears the list of enemy pieces checking the king.
     */
    public void resetCheckCount(){
        checkSources.clear();
    }

    /**
     * Resets the king's safe zone.
     */
    public void clearSafeZone(){
        for(int i = 0; i < 81; i++)
            safeZone[i] = true;
    }

    /**
     * Marks that king can not move onto an i-th field.
     * @param i
     */
    public void setUnsafe(int i){
        if(i >= 0 && i < 81)
            safeZone[i] = false;
    }

    /**
     * Checks if the king is save on an i-th field.
     * @param i possible location for the king.
     * @return true if king is safe in that location, false otherwise.
     */
    public boolean isKingSafe(int i){
        if(i < 0 || i >= 81) return true;
        return safeZone[i];
    }
}
