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

import java.io.Serializable;
import java.util.Vector;

/**
 * Information for a single in-game piece.
 */
public class Piece implements Serializable{
    private int player; //-1 or 1
    private PieceType type;
    private int pos;
    private boolean promoted;
    private boolean inTray;

    /**
     * List of valid target fields the piece can move into.
     */
    public Vector<Integer> validMoves = new Vector<>();

    /**
     * Returns a type of the piece.
     * @return a type of the piece.
     */
    public PieceType getType() {
        return type;
    }

    /**
     * Sets a type of the piece.
     * @param type a target type of the piece.
     */
    public void setType(PieceType type) {
        this.type = type;
    }

    /**
     * Gets owner of the piece.
     * @return -1 or 1.
     */
    public int getPlayer() {
        return player;
    }

    /**
     * Sets owner of the piece.
     * @param player -1 or 1.
     */
    public void setPlayer(int player) {
        this.player = player;
    }

    /**
     * Gets current position of the piece.
     * @return current position of the piece.
     */
    public int getPos() {
        return pos;
    }

    /**
     * Sets position of the piece.
     * @param pos target position of the piece.
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     * Constructor for creating a new piece of certain properties. All created pieces start as unpromoted.
     * @param player owner of a piece.
     * @param type type of a piece.
     * @param pos position of a piece.
     */
    public Piece(int player, PieceType type, int pos){
        super();
        this.player = player;
        this.type = type;
        this.pos = pos;
        this.promoted = false;
    }

    /**
     * Checks if piece is promoted.
     * @return true if piece is promoted, false otherwise.
     */
    public boolean isPromoted(){
        return promoted;
    }

    /**
     * Checks if the piece can be promoted in the indicated field.
     * @param at field for checking if promotion is allowed.
     * @return true if piece is promotable, false otherwise
     */
    public boolean isPromotable(int at){
        if(promoted) return false;
        if(type == PieceType.KING || type == PieceType.GOLDEN_GENERAL) return false;
        if((player == 1 && at > 53) || (player == -1 && at < 27))
            return true;
        return false;
    }

    /**
     * Marks the piece as promoted or not.
     * @param promoted promotion state.
     */
    public void setPromoted(boolean promoted){
        this.promoted = promoted;
    }

    /**
     * Gets ID of the allied king.
     * @return 0 (for player -1) or 1 (for player 1)
     */
    public int getOwnKingId(){
        return player == 1 ? 1 : 0;
    }

    /**
     * Checks if the piece is captured in a tray.
     * @return true if the piece is captured, false otherwise.
     */
    public boolean isInTray() {
        return inTray;
    }

    /**
     * Makrs the piece as captured or not.
     * @param inTray true if the piece is captured, false otherwise.
     */
    public void setInTray(boolean inTray) {
        this.inTray = inTray;
    }

}
