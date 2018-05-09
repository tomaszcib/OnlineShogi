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

package pl.umk.mat.tomaszcib.GuiAssets;

import pl.umk.mat.tomaszcib.GameLogic.CurrentBoard;
import pl.umk.mat.tomaszcib.GameLogic.HistoryItem;
import pl.umk.mat.tomaszcib.GameLogic.Piece;
import pl.umk.mat.tomaszcib.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

/**
 * This class delivers a graphical front-end interface for the game. This includes
 * board, pieces and trays for keeping captured pieces.<br><br>
 * All the elements can be manipulated using mouse (left and right clicks are valid)
 * {@link MainWindow} class uses {@link BoardCanvas} as central widget of the program.
 * @author  Tomasz Ciborski
 */

public class BoardCanvas extends Canvas implements MouseListener, MouseMotionListener {

    /**
     * The most important part of this class - a logical representation of the board.
     */
    public CurrentBoard currentBoard;
    /**
     * Class for loading and keeping in-game textures.
     */
    public TextureLoader textureLoader;
    private Rectangle2D tr;
    private Rectangle[] square = new Rectangle[81];
    private Rectangle[] traySquare = new Rectangle[40];
    private boolean[] movable = new boolean[81];
    private int clicked = -1, oldClicked = -1, selected = -1;
    private int enemyLastMovedFrom = -1, enemyLastMovedTo = -1;
    private int checkmatePos = -1;
    private int trayClicked = -1;
    private boolean promoMode;
    private int playerPov;
    private int historyPreview = 0;
    private boolean regeneratingCanvas = false;

    /**
     * Sets the {@link #historyPreview} variable to i. This method is used by
     * {@link MainWindow#changeViewOnBoard(int)}
     * @param i
     */
    public void setHistoryPreview(int i){
        historyPreview = i;
    }

    /**
     * Checks if the user is previewing one of the records in the game history
     * or displaying the latest move.
     * @return
     */
    private boolean viewingHistory(){
        return historyPreview != currentBoard.history.size() - 1;
    }
    private void updateHistory(){
        MainWindow.panel.b.model.addElement( currentBoard.history.size() - 1 + ". " +
                currentBoard.history.get(currentBoard.history.size()-1).toPanel(false));
        MainWindow.panel.b.moveList.setSelectedIndex(currentBoard.history.size() - 1);
    }

    /**
     * Sets point-of-view variable. POV is used to correctly redraw the board,
     * so that the user will always see their pieces indicating top edge of the board.
     * @param pov desired value of POV (-1 or 1)
     */
    public void setPlayerPov(int pov){
        playerPov = pov;
        clicked = -1; oldClicked = -1; selected = -1;
        enemyLastMovedFrom = -1; enemyLastMovedTo = -1;
        checkmatePos = -1;
        trayClicked = -1;
        repaint();
    }

    /**
     * Get value of point-of-view variable.
     * @return value of point-of-view variable.
     */
    public int getPlayerPov(){
        return playerPov;
    }

    /**
     * A constructor for the {@link BoardCanvas} class instance. Used only
     * on the program startup. Creates a new {@link #currentBoard},
     * sets up textures and mouse listeners for the canvas.
     * @param playerPov
     */
    public BoardCanvas(int playerPov){
        this.playerPov = playerPov;
        currentBoard = new CurrentBoard(-1);

        textureLoader = new TextureLoader();
        tr = new Rectangle2D.Double(0,0,40,50);
        regenerateCanvas();

        setBackground(Color.white);

        MainWindow.panel.b.model.addElement("Game start");

        addMouseMotionListener(this);
        addMouseListener(this);
        updateLabel();
        repaint();
    }

    /**
     * Regenerates current canvas. Depending on point-of-view all the rectangular
     * fields of the board and the trays are placed in proper position.<br><br>
     * We need to be sure that players with id -1 and 1 (aka "server" and "client")
     * will have fields i9 and a0 as their bottom-right corner, respectively.
     * The player always has the bottom of the two trays as their container
     * for captured enemy pieces.
     */
    public void regenerateCanvas(){
        for(int i = 0; i < 81; i++) {
            if(playerPov == -1)
                square[i] = new Rectangle(45 + i % 9 * 45, 50 + i / 9 * 55, 45, 55);
            else
                square[i] = new Rectangle(45+(80 - i) % 9 * 45, 50 + (80 - i) / 9 * 55, 45, 55);
        }

        for(int i = 0; i < 40; i++) {
            if (i < 20)
                traySquare[i] = new Rectangle(480 + i % 5 * 32,
                        (playerPov == -1 ? 335 : 100) + i / 5 * 40,
                        32, 40);
            else
                traySquare[i] = new Rectangle(480 + (i - 20) % 5 * 32,
                        (playerPov == -1 ? 100 : 335) + (i - 20) / 5 * 40,
                        32, 40);
        }
        regeneratingCanvas = true;
        repaint();
    }

    /**
     *  Updates selection and hint labels on the bottom panel of the {@link MainWindow}. Function
     *  is called only if there is the player's turn.
     */
    public void updateLabel(){

        if(playerPov != currentBoard.getCurPlayer()){
            MainWindow.labelPanel.setSelectionLabel("");
            MainWindow.labelPanel.setHintLabel(Local.str[34]);
        }
        else if(selected == -1) {
            MainWindow.labelPanel.setSelectionLabel("");
            MainWindow.labelPanel.setHintLabel(Local.str[21]);
        }
        else{
            String sToDisplay = "";
            Piece p = currentBoard.piece[selected];
            if(p.isPromoted())
                sToDisplay += (Local.str[33] + " ");
            sToDisplay += (Local.str[p.getType().getValue() + 25] + " ");
            sToDisplay += (Local.str[promoMode ? 24 : 23]);
            MainWindow.labelPanel.setHintLabel(Local.str[22]);
            MainWindow.labelPanel.setSelectionLabel(sToDisplay);
        }
    }

    /* Unmarks piece if selected, repaints the board */
    private void doDeselect(){
        if (selected != -1) {
            for (int j : currentBoard.piece[selected].validMoves) {
                movable[j] = false;
                repaintSquare(j);
            }
            if(!currentBoard.piece[selected].isInTray())
                movable[currentBoard.piece[selected].getPos()] = false;
            repaintSquare(clicked);
            repaintSquare(oldClicked);
            repaintSquare(selected);
            updateLabel();
        }
        if(trayClicked != -1) {
            trayClicked = -1;
            repaintTray(0);
        }
        clicked = -1;
        selected = -1;
    }

    /* Detects what field of part of the tray is clicked by the player */
    private int whereClicked(int x, int y){
        if(x >= 480){
            x -= 480;
            if(y >= 335){
                y -= 335;
                x /= 32; y /= 40;
                if(playerPov == -1)
                    return y * 5 + x;
                return (20 + y * 5 + x);
            }
            else{
                y -= 100;
                x /= 32; y /= 40;
                if(playerPov == -1)
                    return (20 + y * 5 + x);
                return y * 5 + x;
            }
        }
        else{
            x -= 45; y -= 50;
            x /= 45; y /= 55;
            if(playerPov == 1)
                return 80 - (y * 9 + x);
            return(y * 9 + x);
        }
    }

    /* Marks all valid moves for selected piece */
    private void marktAllMovableSquares(){
        for (int j : currentBoard.piece[selected].validMoves) {
            movable[j] = true;
            repaintSquare(j);
        }
    }

    /**
     * Mouse event handler.<br> A mouse click is ignored if game has been ended or
     * if there is no partner to play with or the user is currently reviewing
     * one of the history entries or the player is waiting for the partner to move.<br><br>
     * Left click is used to select user's pieces from the board or their tray or
     * change moving mode from normal to promoted.<br>
     * Right click is used to move a previously selected piece.
     * @param e captured mouse event.
     */
    public void mouseReleased(MouseEvent e) {
        if(((Thread) currentBoard.regenerateMovesThread).isAlive() || viewingHistory() || MainWindow.getMode() < 2
                || currentBoard.gameEnded != 0 || playerPov != currentBoard.getCurPlayer())
            return;
        if(e.getButton() == 1) { ;
            doDeselect();
            /* Clicked on board */
            if(e.getX() < 480) {
                int i = whereClicked(e.getX(), e.getY());
                if(currentBoard.squarePieceId[i] != -1 &&
                        currentBoard.piece[currentBoard.squarePieceId[i]].getPlayer() == -currentBoard.getCurPlayer())
                    return;
                if(i == oldClicked || i == clicked)
                    promoMode = !promoMode;
                clicked = i;
                if(currentBoard.squarePieceId[i] == -1) {
                    doDeselect();
                    promoMode = false;
                }
                else {
                    selected = currentBoard.squarePieceId[i];
                    marktAllMovableSquares();
                }
                updateLabel();
                repaintSquare(clicked);
                repaintSquare(oldClicked);
                oldClicked = clicked;
            }
            /* Clicked on tray */
            else{
                int i = whereClicked(e.getX(), e.getY());
                if(i < 20 && i < currentBoard.pdata[0].tray.pieceId.size()){
                    if(currentBoard.getCurPlayer() == 1) return;
                    trayClicked = i;
                    selected = currentBoard.pdata[0].tray.pieceId.get(trayClicked);
                    marktAllMovableSquares();
                    repaintTray(0);
                }
                else if(i - 20 < currentBoard.pdata[1].tray.pieceId.size()){
                    if(currentBoard.getCurPlayer() == -1) return;
                    trayClicked = i - 20;
                    selected = currentBoard.pdata[1].tray.pieceId.get(trayClicked);
                    marktAllMovableSquares();
                    repaintTray(0);
                }
                updateLabel();

            }
        }
        /* Right click - if move valid - execute it, if not, ignore and deselect the piece */
        else if(e.getButton() == 3){
            if(selected != -1) {
                boolean validMove = false;
                int i = whereClicked(e.getX(), e.getY());
                for (int j : currentBoard.piece[selected].validMoves) {
                    if (i == j) {
                        MainWindow.connection.writeToPeer((byte)2, selected, oldClicked, j, promoMode, "DONE");
                        MainWindow.connection.writeToPeer((byte)5,
                                playerPov == -1 ? 0 : 1, currentBoard.pdata[playerPov == -1 ? 0 : 1].timerSecondElapsed, "SYNC");
                        doMoveOnCanvas(selected, oldClicked, j, promoMode, false);
                        promoMode = false;
                        validMove = true;
                        break;
                    }
                }
                doDeselect();
                if(validMove) currentBoard.updateMoves();
            }
        }
    }

    /**
     * Method called from within {@link BoardCanvas} class. Prompts up an information dialog
     * indicating if player has lost, won or tied and marks king figure position if checkmate
     * has occured.
     * @param pos position of checkmated king or -1 if game ended with a draw.
     */
    public void setCheckmatePos(int pos){
        checkmatePos = pos;
        if(currentBoard.gameEnded == 2)
            JOptionPane.showMessageDialog(null,Local.str[65],Local.str[66],1);
        else if(currentBoard.gameEnded == playerPov)
            JOptionPane.showMessageDialog(null,Local.str[63],Local.str[66],1);
        else JOptionPane.showMessageDialog(null,Local.str[64],Local.str[66],1);
        MainWindow.setWhoseMove();
        repaintBoard();
    }

    /**
     * Method used to execute a valid move on a board, either by the user or their partner.
     * This method interacts with the logical model of the board stored in the {@link #currentBoard} variable.
     * @param id id of a moving piece (0-40)
     * @param from source field id (0-80 if a piece moves on board or other if is dropped from tray)
     * @param to target field id (0-80)
     * @param shouldPromote if piece should promote after the move
     * @param doneByUser (currently unused)
     */
    public void doMoveOnCanvas(int id, int from, int to, boolean shouldPromote, boolean doneByUser){
        boolean captures = currentBoard.execMove(id, from, to, shouldPromote);
        historyPreview++;
        updateHistory();
        repaintSquare(from);
        repaintSquare(to);
        if(captures) repaintTray(0);
        trayClicked = -1;
        oldClicked = -1;
        if(!doneByUser){
            enemyLastMovedFrom = from;
            enemyLastMovedTo = to;
        }
        else{
            enemyLastMovedFrom = -1;
            enemyLastMovedTo = -1;
        }
    }

    /* Repaints a single board field */
    private void repaintSquare(int i){
        if(playerPov == -1)
            repaint(45 + i % 9 * 45, 50 + i / 9 * 55, 45, 55);
        else
            repaint(45 + (80 - i) % 9 * 45, 50 + (80 - i) / 9 * 55, 45, 55);
    }
    /* Repaints trays */
    private void repaintTray(int i){
        repaint(480, 100, 160, 160);
        repaint(480,335,160,160);
    }
    /* Repaints whole board */
    private void repaintBoard(){
        repaint(45,50,405,545);
    }

    /* Unused mouse event handlers */
    public void mouseMoved(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseDragged(MouseEvent e) { }

    public void paint(Graphics g) { update(g); }

    /**
     * Redraws the canvas (board and tray).
     * @param g
     */
    public void update(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Dimension dim = getSize();

        int w = (int) dim.getWidth();
        int h = (int) dim.getHeight();
        // clear canvas if we regenerate it on game initialization
        if(regeneratingCanvas) {
            g2.setPaint(Color.white);
            g2.fillRect(0, 0, w, h);
            regeneratingCanvas = false;
        }
        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1.0f));
        /* Letters and numbers for field coordinates */
        for(int i = 0; i < 9; i++){
            g2.drawString(Character.toString((char)(playerPov == -1 ? 97+i : 105-i)), 20, 83 + i * 55);
            g2.drawString(Character.toString((char)(playerPov == -1 ? 49+i : 57-i)), 63 + i * 45, 30);
        }
        /* Normal painting mode */
        if(!viewingHistory()) {
            /* Paint board first */
            for (int i = 0; i < 81; i++) {
                g2.draw(square[i]);
                if (i == clicked) {
                    g2.setPaint(promoMode ? Color.decode("#dd0000") : Color.black);
                    g2.fill(square[i]);
                } else {
                    g2.setPaint(movable[i] ? Color.decode("#eeffee") :
                            (i == enemyLastMovedTo || i == enemyLastMovedFrom ? Color.decode("#b3e6ff") : textureLoader.boardTexture));
                    g2.fill(square[i]);
                    g2.setPaint(Color.black);
                    g2.draw(square[i]);
                }
                if(currentBoard.gameEnded != 0 && i == checkmatePos){
                    g2.setPaint(Color.decode("#003399"));
                    g2.fill(square[i]);
                    g2.setPaint(Color.black);
                    g2.draw(square[i]);
                }
                if (currentBoard.squarePieceId[i] != -1) {
                    if (playerPov == -1)
                        g2.drawImage(textureLoader.getTexture(currentBoard.piece[currentBoard.squarePieceId[i]], playerPov, false),
                                48 + i % 9 * 45, 53 + i / 9 * 55, 40, 50, null);
                    else
                        g2.drawImage(textureLoader.getTexture(currentBoard.piece[currentBoard.squarePieceId[i]], playerPov, false),
                                48 + (80 - i) % 9 * 45, 53 + (80 - i) / 9 * 55, 40, 50, null);
                }
            }
            /* Paint trays */
            int notfill = playerPov == -1 ? trayClicked : trayClicked + 20;
            if(notfill == 19 && playerPov == 1) notfill = -1;
            if(notfill >= 0 && notfill < 40){
                g2.setPaint(Color.black);
                g2.fill(traySquare[notfill]);
            }
            g2.setPaint(textureLoader.trayTexture);
            for (int i = 0; i < 40; i++) {
                //g2.setPaint(textureLoader.trayTexture);
                //g2.fill(traySquare[i]);
                if (i != notfill) g2.fill(traySquare[i]);
                if (i < 20 && i < currentBoard.pdata[0].tray.pieceId.size()) {
                    g2.drawImage(textureLoader.getTexture(currentBoard.piece[currentBoard.pdata[0].tray.pieceId.get(i)], playerPov, true),
                            480 + i % 5 * 32,
                            (playerPov == -1 ? 335 : 100) + i / 5 * 40, 32, 40, null);
                } else if (i >= 20 && i - 20 < currentBoard.pdata[1].tray.pieceId.size()) {
                    g2.drawImage(textureLoader.getTexture(currentBoard.piece[currentBoard.pdata[1].tray.pieceId.get(i - 20)], playerPov, true),
                            480 + (i - 20) % 5 * 32,
                            (playerPov == -1 ? 100 : 335) + (i - 20) / 5 * 40, 32, 40, null);
                }
            }
        }
        /* Viewing history mode */
        else{
            HistoryItem item = currentBoard.history.get(historyPreview);
            g2.setPaint(Color.black);
            for(int i = 0; i < 81; i++){
                g2.setPaint(textureLoader.boardTexture);
                g2.fill(square[i]);
                if(i < 40) g2.fill(traySquare[i]);
                g2.setPaint(Color.black);
                g2.draw(square[i]);
            }

            for(byte b : item.board.keySet()){
                if(playerPov == -1)
                    g2.drawImage(textureLoader.getTexture(HistoryItem.byteToPiece(item.board.get(b)),
                            playerPov, false), 48 + b % 9 * 45,
                            53 + b / 9 * 55, 40, 50, null);
                else
                    g2.drawImage(textureLoader.getTexture(HistoryItem.byteToPiece(item.board.get(b)), playerPov,false),
                            48 + (80 - b) % 9 * 45, 53 + (80 - b) / 9 * 55,
                            40, 50, null);
            }
            for(int i = 0; i < item.tray[0].size(); i++){
                g2.drawImage(textureLoader.getTexture(HistoryItem.byteToPiece(item.tray[0].get(i)), playerPov, true),
                        480 + i % 5 * 32,
                        (playerPov == -1 ? 335 : 100) + i / 5 * 40, 32, 40, null);
            }
            for(int i = 0; i < item.tray[1].size(); i++){
                g2.drawImage(textureLoader.getTexture(HistoryItem.byteToPiece(item.tray[1].get(i)), playerPov, true),
                        480 + i % 5 * 32,
                        (playerPov == -1 ? 100 : 335) + i / 5 * 40, 32, 40, null);
            }
        }
        /* Draw 3d rectangles for more fancy look. */
        g2.setPaint(Color.decode("#835C3B"));
        for(int i = -2; i < 2; i++) {
            g2.draw3DRect(480 + i, 335 + i, 160, 160, true);
            g2.draw3DRect(480 + i, 100 + i, 160, 160, true);
        }
        for(int i = 3; i >= -4; i--){
            g2.draw3DRect(41 + i, 46 + i, 414, 504, true);
        }
    }
}
