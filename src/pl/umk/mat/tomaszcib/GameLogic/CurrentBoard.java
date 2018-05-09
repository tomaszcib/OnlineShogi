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
import pl.umk.mat.tomaszcib.MainWindow;

import javax.swing.*;
import java.io.*;
import java.util.Vector;

import static java.lang.Math.abs;

/**
 * This class delivers logical representation of the game - location of pieces, information on pieces themselves,
 * move history registry, player data and trays of captured enemy pieces.
 * @author  Tomasz Ciborski
 */
public class CurrentBoard {

    /**
     * A registry of moves performed since beginning of the game.
     */
    public Vector<HistoryItem> history = new Vector<HistoryItem>();
    /**
     * An array informing which pieces occupy which parts of the board.
     * <br><br>An ID of a piece is stored on an i-th index
     * if i-th field contains piece of that ID or -1, if the field is unoccupied.
     * Has fixed size of 81.
     */
    public int[] squarePieceId = new int[81];
    /**
     * An array providing information of all the pieces in the current game. Has fixed size of 40.
     */
    public Piece[] piece = new Piece[40];
    /**
     * Data for both parties participating in the game. Has fixed size of 2.
     */
    public PlayerData[] pdata = new PlayerData[2];
    private MoveGenerator moveGenerator = new MoveGenerator();
    private int curPlayer = 1;
    /**
     * Information if the current game has ended. Possible values are:
     * <ul>
     *     <li><b>0:</b> the game has note ended yet.</li>
     *     <li><b>-1:</b> player -1 ("server") has won</li>
     *     <li><b>1:</b> player 1 ("client") has won</li>
     *     <li><b>2:</b> the game has ended with a draw</li>
     * </ul>
     */
    public int gameEnded = 0;

    /**
     * Indicates whose turn it is now.
     * @return -1 or 1, depending whose turn it is now.
     */
    public int getCurPlayer(){
        return curPlayer;
    }

    private class RegenerateMovesThread extends Thread{
        public void run(){
            /* Timers handling */
            int curPlayerId = curPlayer == -1 ? 0 : 1;
            if(!pdata[curPlayerId].timer.isAlive() && MainWindow.getMode() == 2) {
                pdata[curPlayerId].timer.start();
            }
            pdata[curPlayerId].timer.suspend();
            curPlayer = -curPlayer;
            curPlayerId = curPlayer == -1 ? 0 : 1;
            MainWindow.canvas.updateLabel();
            if(!pdata[curPlayerId].timer.isAlive() && MainWindow.getMode() == 2) {
                pdata[curPlayerId].timer.start();
            }

            /* Move generation process */
            for(int i = 0; i < 2; i++) {
                ((King) piece[i]).clearSafeZone();
                ((King) piece[i]).resetCheckCount();
            }
            Piece tmp = piece[7];
            for(int i = 0; i < 40; i++)
                piece[i].validMoves.clear();
            for(int i = 2; i < 40; i++)
                if(!piece[i].isInTray())
                    moveGenerator.generateMoves(piece, pdata, squarePieceId, i);
            for(int i = 0; i < 2; i++) {
                moveGenerator.generateMoves(piece, pdata, squarePieceId, i);
            }
            for(int i = 2; i < 40; i++)
                if(!piece[i].isInTray())
                    moveGenerator.validateMoves(i);
            for(int i = 0; i < 2; i++)
                moveGenerator.generateDrops(i == 0 ? -1 : 1, pdata[i]);
            for(int i = 2; i < 40; i++)
                if(piece[i].isInTray())
                    for(int j = 0; j < 81; j++)
                        if((pdata[piece[i].getOwnKingId()].tray.drop[j] & (1 << piece[i].getType().getValue()) )!= 0)
                            piece[i].validMoves.add(j);

            /* Check if game has ended */

            /*for(int i = 0; i < 81; i++){
                System.out.printf("%3d", ((King)piece[0]).isKingSafe(i) ? 1 : 0);
                if(i % 9 == 8) System.out.println();
            }*/
            gameEnded = 2;
            for(int i = 0; i < 40; i++){
                if(piece[i].getPlayer() != curPlayer) continue;
                if(!piece[i].validMoves.isEmpty()){
                    gameEnded = 0;
                    break;
                }
            }
            if(gameEnded == 2) {
                if (((King) piece[curPlayer == -1 ? 0 : 1]).isCheck()) {
                    gameEnded = curPlayer;
                    MainWindow.canvas.setCheckmatePos(piece[curPlayer == -1 ? 0 : 1].getPos());
                }
                pdata[curPlayerId].timer.suspend();
            }
            else pdata[curPlayerId].timer.resume();
            MainWindow.setWhoseMove();
        }
    }

    /**
     * A thread which regenerates lists of possible moves for each piece in the game (either on the board
     * or captured in the tray). If no moves are possible for a currently moving player, game changes
     * its status and is marked as having been ended.<br><br>
     * This thread is run after every move on the board.
     */
    public RegenerateMovesThread regenerateMovesThread = new RegenerateMovesThread();

    /**
     * Writes {@link #piece}, {@link #history}, {@link #squarePieceId}, {@link #pdata} to the selected stream.
     * This method is used for online communication with a partner (providing them with all the needed data
     * after they join a game) or saving the current game to a file.
     * @param oos target stream (socket or file stream)
     */
    public void saveCurrentGameToStream(ObjectOutputStream oos){
        try {
            oos.writeObject(piece);
            oos.writeObject(history);
            oos.writeObject(squarePieceId);
            oos.writeObject(pdata[0].tray.pieceId);
            oos.writeObject(pdata[1].tray.pieceId);
            oos.writeObject(pdata[0].timerSecondElapsed);
            oos.writeObject(pdata[1].timerSecondElapsed);
            oos.writeInt(curPlayer);
        }
        catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, Local.str[68], Local.str[67], 0);
        }
    }

    /**
     * Loads {@link #piece}, {@link #history}, {@link #squarePieceId}, {@link #pdata} from the selected stream.
     * This method is used for online communication with a partner (receiving all the needed data from the server
     * upon joining the game) or loading a game from a file.
     * @param ois source input stream (socket or file stream)
     */
    public void loadGameFromStream(ObjectInputStream ois){
        try{
            piece = (Piece[])ois.readObject();
            history = (Vector<HistoryItem>)ois.readObject();
            squarePieceId = (int[])ois.readObject();
            pdata[0].tray.pieceId = (Vector<Integer>)ois.readObject();
            pdata[1].tray.pieceId = (Vector<Integer>)ois.readObject();
            pdata[0].timerSecondElapsed = (int)ois.readObject();
            pdata[1].timerSecondElapsed = (int)ois.readObject();
            curPlayer = ois.readInt();
            //System.out.println(curPlayer);
            MainWindow.panel.b.model.clear();
            int i = 0;
            for(HistoryItem hi : history) {
                MainWindow.panel.b.model.addElement((i == 0 ? "" : i + ". ") + hi.toPanel(i == 0));
                i++;
            }
            MainWindow.panel.b.moveList.setSelectedIndex(MainWindow.panel.b.model.getSize() - 1);
            pdata[0].updateTimerLabel();
            pdata[1].updateTimerLabel();
        }
        catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, Local.str[68], Local.str[67], 0);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, Local.str[68], Local.str[67], 0);
        }
    }

    /**
     * This method executes a single move of a piece within the game.
     * @param pieceId an ID of moving piece (0-39)
     * @param from source field (0-80 if moving on the board or other if dropping from tray)
     * @param to target field
     * @param shouldPromote flag indicating if a piece should be promoted after executing the move.
     *                      Note that certain pieces will never promote, and some will always
     *                      promote if proper conditions are met, regardless of this flag.
     * @return flag indicating if this move somehow involved one of the trays (either piece capture or drop).
     * The value is an information for proper repainting of {@link pl.umk.mat.tomaszcib.GuiAssets.BoardCanvas}.
     */
    public boolean execMove(int pieceId, int from, int to, boolean shouldPromote){
        Piece p = piece[pieceId];
        Piece target;
        boolean involvesTray = false;
        boolean oldPromoted = p.isPromoted();
        boolean wasInTray = p.isInTray();
        boolean captures = false;
        int playerId = p.getPlayer() == -1 ? 0 : 1;
        /* Dropping piece from tray */
        if(p.isInTray()){
            for(int i = 0; i < pdata[playerId].tray.pieceId.size(); i++)
                if(pdata[playerId].tray.pieceId.get(i) == pieceId){
                    involvesTray = true;
                    pdata[playerId].tray.pieceId.remove(i);
                    break;
                }
            squarePieceId[to] = pieceId;
            p.setInTray(false);
            p.setPos(to);
        }
        /* Move on a board */
        else{
            /* If we capture enemy piece */
            if(squarePieceId[to] != -1){
                target = piece[squarePieceId[to]];
                target.setPromoted(false);
                target.setPlayer(p.getPlayer());
                pdata[playerId].tray.pieceId.add(squarePieceId[to]);
                target.setPos(81 + squarePieceId[to]);
                target.setInTray(true);
                involvesTray = true;
                captures = true;
            }
            squarePieceId[to] = pieceId;
            squarePieceId[from] = -1;
            p.setPos(to);
            /* Promotion handling. Pawns and lances always promote in the last row,
            knights - in the last two rows.
             */
            if((p.isPromotable(to) || p.isPromotable(from)) && shouldPromote)
                p.setPromoted(true);
            if (((p.getPlayer() == -1 && to < 18) || (p.getPlayer() == 1 && to > 62))
                    && p.getType() ==  PieceType.KNIGHT)
                p.setPromoted(true);
            else if (((p.getPlayer() == -1 && to < 9) || (p.getPlayer() == 1 && to > 71))
                    && (p.getType() ==  PieceType.PAWN || p.getType() == PieceType.LANCE))
                p.setPromoted(true);
        }

        /* Update the history registry */
        history.add(new HistoryItem(piece, p.getType().getValue(), oldPromoted, from, to,
                !oldPromoted && p.isPromoted(), wasInTray && !p.isInTray() ,captures));
        return involvesTray;
    }

    /**
     * Creates new instance of {@link RegenerateMovesThread} and runs it.
     */
    public void updateMoves(){
        regenerateMovesThread = new RegenerateMovesThread();
        regenerateMovesThread.start();
    }


    /**
     * A variant of constructor used for starting a completely new game. All the pieces are placed in their original
     * positions. Used if "Start a new game" option is selected in the
     * {@link pl.umk.mat.tomaszcib.GuiAssets.DialogHost} window.
     * @param startingPlayer which player moves first
     */
    public CurrentBoard(int startingPlayer){
        int player, pos;
        PieceType type;
        pdata[0] = new PlayerData();
        pdata[1] = new PlayerData();
        pdata[0].setId(0);
        pdata[1].setId(1);
        pdata[0].updateTimerLabel();
        pdata[1].updateTimerLabel();
        for(int i = 0; i < 81; i++) {
            squarePieceId[i] = -1;
        }
        for(int i = 0; i < 40; i++){
            player = i % 2 == 0 ? -1 : 1;
            if(i < 2) {
                pos = player == 1 ? 4 : 76;
                piece[i] = new King(player, pos);
                squarePieceId[pos] = i;
                continue;
            }
            else if(i < 18){
                type = PieceType.valueOf((i + 2) / 4);
                pos = player == 1 ? 4 : 76;
                if(i % 4 < 2) pos += i / 4;
                else pos -= i / 4 + 1;
            }
            else if(i < 36){
                type = PieceType.PAWN;
                pos = player == 1 ? i / 2 + 9 : i / 2 + 45;
            }
            else if(i < 38) {
                type = PieceType.ROOK;
                pos = player == 1 ? 10 : 70;
            }
            else{
                type = PieceType.BISHOP;
                pos = player == 1 ? 16 : 64;
            }
            piece[i] = new Piece(player, type, pos);
            squarePieceId[pos] = i;
        }
        curPlayer = startingPlayer;
        history.add(new HistoryItem(piece,0,false,0,0,false,false,false));
    }

    /**
     * A variant of constructor designated for loading game assets (pieces, history, etc.) from a file.
     * Used if "Load game from a file" option is selected in the {@link pl.umk.mat.tomaszcib.GuiAssets.DialogHost} window.
     * @param fromFile source <i>.gam</i> file.
     */
    public CurrentBoard(String fromFile){
        pdata[0] = new PlayerData();
        pdata[1] = new PlayerData();
        try(FileInputStream fis = new FileInputStream(fromFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);){
                loadGameFromStream(ois);
                curPlayer = -curPlayer;
            pdata[0].setId(0);
            pdata[1].setId(1);
            pdata[0].updateTimerLabel();
            pdata[1].updateTimerLabel();
        }
        catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, Local.str[58], Local.str[67], 0);
        }
    }

}
