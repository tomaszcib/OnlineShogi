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

import static java.lang.Math.abs;

/**
 * Class used for generating and validating moves for various in-game pieces, either on the board or the trays.
 */
public class MoveGenerator {

    private int[] squarePieceId = new int[81];

    private Piece[] piece = new Piece[40];
    //private PlayerData[] pdata = new PlayerData[2];
    byte[] drop = new byte[81];
    Piece p;
    private int movId;
    private int enemyKing;

    /* Checks if the field has ally piece */
    private boolean hasAlly(int i){
        if(i >= 81 || i < 0) return true;
        if(squarePieceId[i] == -1) return false;
        return p.getPlayer() == piece[squarePieceId[i]].getPlayer();
    }
    /* Checks if the field has enemy piece */
    private boolean hasEnemy(int i){
        if(i >= 81 || i < 0) return false;
        if(squarePieceId[i] == -1) return false;
        return p.getPlayer() != piece[squarePieceId[i]].getPlayer();
    }
    /* Checks if a and b are not separated by a board edge. */
    private boolean isNotEdge(int a, int b){
        if ((a % 9 == 0 && b % 9 == 8 ) || (b % 9 == 0 && a % 9 == 8)
                || (a < 9 && b < 0) || (a > 71 && b > 80))
            return false;
        return true;
    }

    /* Checks if we can mark a move from a to b as possibly valid */
    private boolean addIfValid(int a, int b, int mustBeRow){
        /*if(p.getType() == PieceType.GOLDEN_GENERAL && p.getPlayer() == -1){
            System.out.printf("%d -> " + ownKingChecked(piece[movId].getPos(), b, (p.getType()).getValue()), b);
            System.out.println();
        }*/
        if(a == b || b / 9 != mustBeRow || hasAlly(b)
                || ownKingChecked(piece[movId].getPos(), b, (p.getType()).getValue())) {
            if(hasAlly(b) && b / 9 == mustBeRow) ((King) piece[enemyKing]).setUnsafe(b);
            return false;
        }
        if(isNotEdge(a,b)) {
            p.validMoves.add(b);
            ((King) piece[enemyKing]).setUnsafe(b);
            /*if(hasEnemy(b) && squarePieceId[b] == enemyKing)
                ((King) piece[enemyKing]).addCheck();*/
            if(piece[enemyKing].getPos() == b) {
                ((King) piece[enemyKing]).addCheck(p.getPos());
                //System.out.println("Check from: " + p.getPos());
            }
        }
        return true;
    }

    /* Checks if our movement will leave our king in check */
    private boolean ownKingChecked(int a, int b, int movingPieceId){
        int tmp, chk, dir, row, column, posKing;
        /* If we move a king, we need to check if we'll end up in a safe zone */
        if(movingPieceId == 0){
            int typeArr[];
            posKing = a;
            dir = -piece[movingPieceId].getPlayer();
            if(!((King) piece[movId]).isKingSafe(b)) return true;
            for(int i = -1; i < 2; i++){
                if(hasEnemy(b+i) && piece[squarePieceId[b+i]].getType() == PieceType.KING)
                    return true;
                if(hasEnemy(b+9+i) && piece[squarePieceId[b+9+i]].getType() == PieceType.KING)
                    return true;
                if(hasEnemy(b-9+i) && piece[squarePieceId[b-9+i]].getType() == PieceType.KING)
                    return true;
            }
            return false;
        }
        /* If other piece - we need to check if we uncovered our king */
        else posKing = piece[p.getOwnKingId()].getPos();
        /* Horizontal uncover - we seek for enemy rook in the row */
        if(a/9 == posKing/9 && b/9 != posKing/9){
            dir = (a%9 > posKing%9 ? 1 : -1);
                tmp = posKing;
                row = a / 9;
                while ((tmp + dir) / 9 == row) {
                    chk = tmp + dir;
                    if (hasAlly(chk) && chk != a) break;
                    else if(hasEnemy(chk) && piece[squarePieceId[chk]].getType() != PieceType.ROOK) break;
                    else if (hasEnemy(chk)
                            && piece[squarePieceId[chk]].getType() == PieceType.ROOK)
                        return true;
                    tmp += dir;
                }
        }
        /* Vertical uncover - we seek for enemy rook or lance in the column*/
        else if(a%9 == posKing%9 && b%9 != posKing%9){
            dir = (a/9 > posKing/9 ? 1 : -1);
                tmp = posKing;
                column = a % 9;
                while ((tmp + 9 * dir) >= 0 && (tmp + 9 * dir) < 81) {
                    chk = tmp + 9 * dir;
                    if (hasAlly(chk) && chk != a) break;
                    else if(hasEnemy(chk) && (piece[squarePieceId[chk]].getType() != PieceType.ROOK
                            || (piece[squarePieceId[chk]].getType() != PieceType.LANCE &&
                        piece[squarePieceId[chk]].isPromoted()))) break;
                    else if (hasEnemy(chk)
                            && (piece[squarePieceId[chk]].getType() == PieceType.ROOK ||
                            (dir == -piece[squarePieceId[chk]].getPlayer()
                                    && piece[squarePieceId[chk]].getType() == PieceType.LANCE
                            && !piece[squarePieceId[chk]].isPromoted())))
                        return true;
                    tmp += (9 * dir);
                }
        }

        /* Diagonal uncover - we seek for enemy bishop on the line */
        else if(abs(posKing/9 - a/9) == abs(posKing%9 - a%9)
                && abs(posKing/9 - b/9) != abs(posKing%9 - b%9)){
            if(a/9 > posKing/9)
                dir = a%9 > posKing%9 ? 10 : 8;
            else dir = a%9 > posKing%9 ? -8 : -10;
             tmp = posKing;
             while (tmp >= 0 && tmp < 81) {
                 chk = tmp + dir;
                 if ((hasAlly(chk) && chk != a) || !isNotEdge(tmp, chk))
                     break;
                 else if(hasEnemy(chk) && piece[squarePieceId[chk]].getType() != PieceType.BISHOP)
                     break;
                 if (hasEnemy(chk) && piece[squarePieceId[chk]].getType() == PieceType.BISHOP)
                     return true;
                 tmp += dir;
             }
         }
        return false;
    }

    /* If king is in check we shall hide him. This function checks if check is broken as a result of our move */
    private boolean isKingStillInDanger(int a, int b, King ownKing){
        int pos = ownKing.getPos();
        int enemyPos = ownKing.getFirstCheckSource();
        int tmp = pos;
        int dir, chk;
        /* We can't hide from enemy knight, unless we capture it */
        if(piece[squarePieceId[enemyPos]].getType() == PieceType.KNIGHT
                && !piece[squarePieceId[enemyPos]].isPromoted()
                && b != enemyPos)
            return true;
        /* Horizontal checking */
        if((pos/9 == enemyPos/9)){
            dir = enemyPos > pos ? 1 : -1;
            while(tmp/9 == pos/9){
                chk = tmp + dir;
                if(chk == b) return false;
                if(chk == enemyPos) return true;
                tmp = chk;
            }
            return true;
        }
        /* Vertical or diagonal checking */
        else{
            if(enemyPos%9 == pos%9){
                dir = enemyPos > pos ? 9 : -9;
            }
            else if(enemyPos/9 < pos/9) {
                if (enemyPos % 9 < pos % 9) dir = -10;
                else dir = -8;
            }
            else{
                if(enemyPos % 9 < pos % 9) dir = 8;
                else dir = 10;
            }
            while(tmp >= 0 && tmp < 81){
                chk = tmp + dir;
                if(chk == b) return false;
                if(chk == enemyPos) return true;
                tmp = chk;
            }
            return true;
        }
    }

    /**
     * Sieve previously calculated moves and remove the invalid ones (eg. the ones which would uncover a king
     * or not prevent king's check)
     * @param forWhom piece ID for move validation.
     */
    public void validateMoves(int forWhom){
        p = piece[forWhom];
        int ownKing = piece[forWhom].getOwnKingId();
        int j = 0;

        if(!((King)piece[ownKing]).isCheck() || ((King) piece[ownKing]).getCheckCount() != 1){
            if(((King) piece[ownKing]).getCheckCount() > 1) piece[forWhom].validMoves.clear();
            return;
        }
        while(j < piece[forWhom].validMoves.size()){
            if(piece[forWhom].validMoves.get(j) == ((King) piece[ownKing]).getFirstCheckSource() ||
            !(isKingStillInDanger(0,piece[forWhom].validMoves.get(j), ((King)piece[ownKing])))) j++;
            else piece[forWhom].validMoves.remove(j);
        }
    }

    /* Generate valid moves for rook */
    private void generateRook(){
        int column = p.getPos() % 9;
        int row = p.getPos() / 9;
        int tmp = p.getPos();
        int chk;
        /* King  */
        boolean kingDangeredLine = false;
        while((tmp + 1) / 9 == row){
            chk = tmp + 1;
            ((King) piece[enemyKing]).setUnsafe(chk);
            if(hasAlly(chk)) break;
            if(!kingDangeredLine) addIfValid(tmp,chk,row);
            if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
            else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;
            tmp = chk;
        }
        tmp = p.getPos();
        kingDangeredLine = false;
        while((tmp - 1) / 9 == row){
            chk = tmp - 1;
            ((King) piece[enemyKing]).setUnsafe(chk);
            if(hasAlly(chk)) break;
            if(!kingDangeredLine) addIfValid(tmp,chk,row);
            if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
            else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;
            tmp = chk;
        }
        tmp = p.getPos();
        kingDangeredLine = false;
        while(tmp + 9 < 81){
            chk = tmp + 9;
            ((King) piece[enemyKing]).setUnsafe(chk);
            if(hasAlly(chk)) break;
            if(!kingDangeredLine) addIfValid(tmp,chk,tmp/9+1);
            if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
            else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;
            tmp = chk;
        }
        tmp = p.getPos();
        kingDangeredLine = false;
        while(tmp -9 >= 0){
            chk = tmp - 9;
            ((King) piece[enemyKing]).setUnsafe(chk);
            if(hasAlly(chk)) break;
            if(!kingDangeredLine) addIfValid(tmp,chk,tmp/9-1);
            if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
            else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;
            tmp = chk;
        }
    }


    /* Generate valid moves for bishop */
    private void generateBishop(){
        int tmp = p.getPos();
        int chk;
        boolean kingDangeredLine = false;
        while(tmp + 10 < 81){
            chk = tmp + 10;
            if(kingDangeredLine && isNotEdge(tmp, chk))
                ((King) piece[enemyKing]).setUnsafe(chk);
            if(hasAlly(chk) || !addIfValid(tmp, chk, tmp / 9 + 1)) {
                ((King) piece[enemyKing]).setUnsafe(chk);
                break;
            }
            if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
            else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;

            tmp = chk;
        }
        tmp = p.getPos();
        kingDangeredLine = false;
        while(tmp - 10 >= 0){
            chk = tmp - 10;
            if(kingDangeredLine && isNotEdge(tmp, chk))
                ((King) piece[enemyKing]).setUnsafe(chk);
            if(hasAlly(chk) || !addIfValid(tmp, chk, tmp / 9 - 1)) {
                ((King) piece[enemyKing]).setUnsafe(chk);
                break;
            }
            if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
            else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;

            tmp = chk;
        }
        tmp = p.getPos();
        kingDangeredLine = false;
        while(tmp + 8 < 81){
            chk = tmp + 8;
            if(kingDangeredLine && isNotEdge(tmp, chk))
                ((King) piece[enemyKing]).setUnsafe(chk);
            else if(hasAlly(chk) || !addIfValid(tmp, chk, tmp / 9 + 1)) {
                ((King) piece[enemyKing]).setUnsafe(chk);
                break;
            }
            if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
            else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;

            tmp = chk;
        }
        tmp = p.getPos();
        kingDangeredLine = false;
        while(tmp - 8 >= 0){
            chk = tmp - 8;
            if(kingDangeredLine && isNotEdge(tmp, chk))
                ((King) piece[enemyKing]).setUnsafe(chk);
            if(hasAlly(chk) || !addIfValid(tmp, chk, tmp / 9 - 1)){
                ((King) piece[enemyKing]).setUnsafe(chk);
                break;
            }
            if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
            else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;

            tmp = chk;
        }
    }

    /* Generate valid moves for golden general */
    private void generateGoldenGeneral(){
        int row = p.getPos() / 9;
        addIfValid(p.getPos(), p.getPos() + 9, row + 1);
        addIfValid(p.getPos(), p.getPos() - 9, row - 1);
        addIfValid(p.getPos(), p.getPos() - 1, row);
        addIfValid(p.getPos(), p.getPos() + 1, row);
        addIfValid(p.getPos(), p.getPos() + 10 * p.getPlayer(), row + p.getPlayer());
        addIfValid(p.getPos(), p.getPos() + 8 * p.getPlayer(), row + p.getPlayer());
    }

    /* Generate valid moves for silver general */
    private void generateSilverGeneral(){
        int row = p.getPos() / 9;
        addIfValid(p.getPos(), p.getPos() - 8, row - 1);
        addIfValid(p.getPos(), p.getPos() + 8, row + 1);
        addIfValid(p.getPos(), p.getPos() + 10, row +1);
        addIfValid(p.getPos(), p.getPos() - 10, row - 1);
        addIfValid(p.getPos(), p.getPos() + 9 * p.getPlayer(), row + p.getPlayer());
    }

    /**
     * Generates list of technically possible moves for a selected piece.
     * Note that this function is only probabilistic -
     * some of the generated moves may be removed later on (see {@link #validateMoves(int)}).
     * @param pieces array of all pieces in the game.
     * @param players array of data of the in-game players.
     * @param squarePieceIds map of pieces location on the board
     * @param forWhom ID of a piece we generate moves for.
     */
    public void generateMoves(Piece[] pieces, PlayerData[] players, int[] squarePieceIds, int forWhom){
        piece = pieces;
        //pdata = players;
        squarePieceId = squarePieceIds;

        p = piece[forWhom];
        movId = forWhom;
        enemyKing = p.getPlayer() == 1 ? 0 : 1;
        //Piece is on a board
        int row = p.getPos() / 9;
        if(p.getPos() < 81){
            // king
            if(p.getType() == PieceType.KING) {
                for (int i = -1; i <= 1; i++) {
                    addIfValid(p.getPos(), p.getPos() + i, row);
                    addIfValid(p.getPos(), p.getPos() - 9 + i, row - 1);
                    addIfValid(p.getPos(), p.getPos() + 9 + i, row + 1);
                }
            }
            // golden general
            else if(p.getType() == PieceType.GOLDEN_GENERAL) generateGoldenGeneral();
                // silver general
            else if(p.getType() == PieceType.SILVER_GENERAL) {
                if(p.isPromoted()) generateGoldenGeneral();
                else generateSilverGeneral();
            }
            // knight
            else if(p.getType() == PieceType.KNIGHT) {
                if(p.isPromoted()) generateGoldenGeneral();
                else {
                    int tmp = p.getPos() + 17 * p.getPlayer();
                    addIfValid(p.getPos(), tmp, row + 2 * p.getPlayer());
                    tmp = p.getPos() + 19 * p.getPlayer();
                    addIfValid(p.getPos(), tmp, row + 2 * p.getPlayer());

                }
            }
            // lance
            else if(p.getType() == PieceType.LANCE) {
                if(p.isPromoted()) generateGoldenGeneral();
                else {
                    int player = p.getPlayer();
                    int tmp = p.getPos();
                    int chk;
                    boolean kingDangeredLine = false;
                    while(tmp >= 0 && tmp < 81){
                        chk = tmp + 9 * player;
                        ((King) piece[enemyKing]).setUnsafe(chk);
                        if(hasAlly(chk)) break;
                        if(!kingDangeredLine) addIfValid(tmp, chk, tmp / 9 + player);
                        if(hasEnemy(chk) && squarePieceId[chk] == enemyKing) kingDangeredLine = true;
                        else if(hasEnemy(chk) && squarePieceId[chk] != enemyKing) break;
                        tmp = chk;
                    }
                }
            }
            // rook or bishop
            else if(p.getType().getValue() < 7){
                if(p.getType() == PieceType.ROOK) generateRook();
                else generateBishop();
                if(p.isPromoted()){
                    generateGoldenGeneral();
                    generateSilverGeneral();
                }
            }
            // pawn
            else if(p.getType() == PieceType.PAWN){
                if(p.isPromoted()) generateGoldenGeneral();
                else addIfValid(p.getPos(), p.getPos() + 9 * p.getPlayer(), row + p.getPlayer());
            }
        }
    }

    /**
     * Generates map of possible drops on a board for selected player.
     * @param player -1 or 1.
     * @param pdata reference to selected player's data.
     */
    public void generateDrops(int player, PlayerData pdata){
        Piece p;
        int enemyKing = player == -1 ? 1 : 0;
        int ownKing = player == -1 ? 0 : 1;
        int squareFrontEnemyKing = piece[enemyKing].getPos() + 9 * -player;
        boolean potentialMate = false;
        boolean[] colAlreadyHasPawn = new boolean[9];
        for(int i = 0; i < 9; i++) colAlreadyHasPawn[i] = false;
        for(int i = 0; i < 81; i++) pdata.tray.drop[i] = 0;

        for(int i = 18; i < 36; i ++)
            if(!piece[i].isPromoted() && !piece[i].isInTray() && piece[i].getPlayer() == player)
                colAlreadyHasPawn[piece[i].getPos() % 9] = true;

        /* We can't hide king with a drop if there are multiple checking enemies */
        if(((King) piece[ownKing]).getCheckCount() > 1){
            for(int i = 0; i < 81; i++) pdata.tray.drop[i] = (byte)0;
            return;
        }
        /* If there's only one threat, we can hide our king behind a drop */
        else if(((King) piece[ownKing]).getCheckCount() == 1) {
            for (int i = 0; i < 81; i++) {
                if (isKingStillInDanger(0, i, ((King) piece[ownKing])) || squarePieceId[i] != -1)
                    pdata.tray.drop[i] = (byte) 0;
                else pdata.tray.drop[i] = (byte) 255;
            }
            //return;
        }
        else
            for(int i = 0; i < 81; i++)
                pdata.tray.drop[i] = (byte)255;
        for(int i = 0; i < 81; i++){
            /* Can't drop - field already occupied */
            if(squarePieceId[i] != -1)
                pdata.tray.drop[i] = (byte)0;
            /* Can't drop pawn or lance in the last row */
            else if((player == -1 && i < 9) || (player == 1 && i > 71)) {
                pdata.tray.drop[i] &= ~(1 << PieceType.PAWN.getValue());
                pdata.tray.drop[i] &= ~(1 << PieceType.LANCE.getValue());
            }
            /* Can't drop knight in the last two rows */
            if((player == -1 && i < 18) || (player == 1 && i > 62))
                pdata.tray.drop[i] &= ~(1 << PieceType.KNIGHT.getValue());
            /* Can't place two unpromoted pawns in one column */
            if(colAlreadyHasPawn[i%9]) pdata.tray.drop[i] &= ~(1 << PieceType.PAWN.getValue());

        }
        /* We cen't checkmate king with dropping pawn. */
        if(piece[enemyKing].validMoves.size() == 0) {
            potentialMate = true;
            for (int i = player == -1 ? 0 : 1; i < 40; i += 2) {
                if (!piece[i].isInTray())
                    for (int j : piece[i].validMoves)
                        if(j == squareFrontEnemyKing) {
                            potentialMate = false;
                            break;
                        }
                if(!potentialMate) break;
            }
        }
        if(potentialMate) pdata.tray.drop[squareFrontEnemyKing] &= ~(1 << PieceType.PAWN.getValue());
    }

}
