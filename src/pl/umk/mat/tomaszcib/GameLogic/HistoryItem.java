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

import pl.umk.mat.tomaszcib.GuiAssets.Local;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Data type class for {@link CurrentBoard#history} record. A single HistoryItem contains information on exactly one performed move,
 * as well as recorded state of the board and the player trays after that move has occured.<br><br>
 * Note that while constructing a new entry, we would like to compress the stored data as much as possible.
 * Unnecessary piece records are omitted, and the important ones (player, type, promotion, is-captured?)
 * are stored in a single byte.
 */
public class HistoryItem implements Serializable{
    /**
     * Map indicating which pieces are on a board.
     */
    public Hashtable<Byte, Byte> board = new Hashtable<>();
    /**
     * Two vectors indicating what pieces are in players' trays.
     */
    public Vector<Byte>[] tray = new Vector[2];
    private int moveFrom, moveTo;
    private boolean promotion, drop, capture;
    private byte pieceType;

    /**
     * Constructor for a new HistoryItem entry.<br><br>
     * @param p array of all the pieces in the game
     * @param pieceType type of a moving piece.
     * @param isPromoted if a moving piece is promoted
     * @param moveFrom source field for a move.
     * @param moveTo target field for a move.
     * @param promotion if a piece promoted after a move.
     * @param drop if piece is dropped from a tray.
     * @param capture if piece captures other piece during a move.
     */
    HistoryItem(Piece[] p, int pieceType, boolean isPromoted, int moveFrom, int moveTo, boolean promotion, boolean drop, boolean capture){
        this.pieceType = (byte)pieceType;
        if(isPromoted) this.pieceType |= 128;
        this.capture = capture;
        this.moveFrom = moveFrom;
        this.moveTo = moveTo;
        this.promotion = promotion;
        this.drop = drop;
        tray[0] = new Vector<>();
        tray[1] = new Vector<>();
        for(Piece a : p){
            if(a == null) continue;
            if(a.isInTray())
                tray[a.getOwnKingId()].add(pieceToByte(a));
            else
                board.put((byte)a.getPos(), pieceToByte(a));
        }
    }

    private byte pieceToByte(Piece a){
        byte b = 0;
        b |= a.getType().getValue();
        if(a.getPlayer() == -1) b |= 128;
        if(a.isInTray()) b |= 64;
        if(a.isPromoted()) b |= 32;
        return b;
    }

    /**
     * Decompresses stored piece information.
     * @param b compressed piece.
     * @return decompressed piece, ready to be displayed on a {@link pl.umk.mat.tomaszcib.GuiAssets.BoardCanvas}.
     */
    public static Piece byteToPiece(byte b){
        Piece p = new Piece(0,PieceType.KING,0);
        p.setPlayer((b & 128) == 0 ? 1 : -1);
        p.setInTray((b & 64) != 0);
        p.setPromoted((b & 32) != 0);
        p.setType(PieceType.valueOf(b & 15));
        return p;
    }

    /**
     * DEBUG: converts HistoryItem entry to a String.
     * @return entry converted to a String.
     */
    public String toString(){
        String s = "";
        for(byte b: board.keySet())
            s += (b + " " + board.get(b) + "| ");
        s += "\n";
        for(int i = 0; i < 2; i++) {
            for (int b : tray[i])
                s += (b + " ");
            s += "\n";
        }
        return s;
    }

    private String posToCoords(int pos){
        String p = "";
        p += (char)((pos / 9) + 97);
        p += (char)((pos % 9) + 49);
        return p;
    }

    /**
     * Converts HistoryItem entry to a string that would
     * be displayed in the {@link pl.umk.mat.tomaszcib.GuiAssets.PanelHistory} panel.
     * The function follows the official international Shogi game notation.
     * @param firstHistItem if this is initial entry (aka "Game start") or not.
     * @return formal notation of the move.
     */
    public String toPanel(boolean firstHistItem){
        String s = "";
        if(firstHistItem) {
            s = Local.str[18];
            return s;
        }

        /* Promowany? */
        s += (pieceType & 128) != 0 ? "+" : " ";
        /* Typ figury */
        s += Local.pieceLetterId.charAt(pieceType & 15);
        /* Źródło */
        if(!drop) s += posToCoords(moveFrom);
        if(capture) s += "x";
        else if(drop) s += "*";
        else s += "-";
        s += posToCoords(moveTo);
        if(promotion) s += "+";
        return s;
    }
}
