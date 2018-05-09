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

import java.util.HashMap;
import java.util.Map;

/**
 * An enum class for storing possible types of the in-game pieces.
 */
public enum PieceType {
    KING(0),
    GOLDEN_GENERAL(1),
    SILVER_GENERAL(2),
    KNIGHT(3),
    LANCE(4),
    ROOK(5),
    BISHOP(6),
    PAWN(7);

    private int value;
    private static Map map = new HashMap<>();

    private PieceType(int value){
        this.value = value;
    }

    static {
        for(PieceType pieceType : PieceType.values())
            map.put(pieceType.value, pieceType);
    }

    /**
     * Converts int to PieceType.
     * @param pieceType value to be converted.
     * @return converted value.
     */
    public static PieceType valueOf(int pieceType){
        return (PieceType) map.get(pieceType);
    }

    /**
     * Converts PieceType to int.
     * @return converted value.
     */
    public int getValue(){
        return value;
    }

}
