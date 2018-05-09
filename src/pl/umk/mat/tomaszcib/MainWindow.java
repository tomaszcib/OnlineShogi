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

package pl.umk.mat.tomaszcib;

import pl.umk.mat.tomaszcib.GameLogic.CurrentBoard;
import pl.umk.mat.tomaszcib.GuiAssets.*;
import pl.umk.mat.tomaszcib.GuiAssets.Menu;
import pl.umk.mat.tomaszcib.Network.Connection;
import pl.umk.mat.tomaszcib.Network.ConnectionClient;
import pl.umk.mat.tomaszcib.Network.ConnectionServer;

import java.awt.*;
import java.awt.event.*;

import java.applet.Applet;
import java.io.File;
import javax.swing.filechooser.FileFilter;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * This is the main class for the Shogi computer program. MainWindow class creates, handles and contains
 * all the information for the program, it is the only one with the runnable {@link #main(String[])} function.
 *
 * @author  Tomasz Ciborski
 * @version 1.0
 * @since   2018-04-20
 */
public class MainWindow extends JApplet{
    /**
     * Panel at the bottom of the window.
     */
    static public LabelPanel labelPanel = new LabelPanel();
    static private JLabel upLabel = new JLabel();
    /**
     * Right-side panel.
     */
    static public TabPanel panel;
    /**
     * Central widget of the program - visual representation of the board.
     */
    static public BoardCanvas canvas;
    private static Menu menu;
    private static Dimension panelDim = new Dimension(220,-1);
    /**
     * Can be either {@link ConnectionServer} or {@link ConnectionClient}
     * depending on user's choice.
     */
    static public Connection connection;
    static private int mode = 0;
    /**
     * A filter applied to file choosers in the program. Only <i>*.gam</i>
     * files should be displayed in both "Open" and "Save" windows.
     */
    static public FileFilter gameFileFilter;
    /**
     * An instance of {@link DialogHost} class.
     */
    static public DialogHost dialogHost;
    /**
     * An instance of {@link DialogJoin} class.
     */
    static public DialogJoin dialogJoin;
    /**
     * A dialog window allowing user to save current game into a <i>*.gam</i> file.
     */
    static public JFileChooser gameFileChooser = new JFileChooser() {
        @Override
        public void approveSelection() {
            File f = getSelectedFile();
            if (f.exists() && getDialogType() == SAVE_DIALOG) {
                int result = JOptionPane.showConfirmDialog(this,
                        Local.str[57], Local.str[6], JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION)
                    super.approveSelection();
                else return;
            }
            super.approveSelection();
        }
    };
    /**
     * This method is called from the {@link DialogHost#onOK()}.
     * Creates new {@link Canvas} and {@link ConnectionServer} instances.
     */
    public static void doHostGame(){
        if(dialogHost.getMode() == 0)
            canvas.currentBoard = new CurrentBoard(dialogHost.getStartingPlayer());
        else
            canvas.currentBoard = new CurrentBoard(dialogHost.getFilePath());
        canvas.currentBoard.pdata[0].setName(dialogHost.getName());
        connection = new ConnectionServer(dialogHost.getPort());
        connection.start();
        setMode(1);
        canvas.setPlayerPov(-1);
        canvas.currentBoard.updateMoves();
    }

    /**
     * This method is called from the {@link DialogJoin#onOK()}.
     * Creates new {@link ConnectionClient} instance.
     */
    public static void doJoinGame(){
        canvas.currentBoard.pdata[1].setName(dialogJoin.getName());
        connection = new ConnectionClient(dialogJoin.getHost(),dialogJoin.getPort());
        setMode(1);
        canvas.setPlayerPov(1);
        connection.start();
    }

    /**
     * Prompts up a dialog window asking user whether to leave the program or not.
     * Closes the game if user has selected "Yes".
     */
    public static void doCloseGame(){
        if(JOptionPane.showConfirmDialog(null, Local.str[55], Local.str[7],
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == 0)
            System.exit(0);
    }

    private static void SystemMsg(String s){
        panel.a.addMessage((byte)0, s);
    }


    /**
     * Updates contents of the label on the top of the window.
     */
    public static void setWhoseMove(){
        int msgId;
        if(canvas.currentBoard.gameEnded == 2)
            msgId = 65;
        else if(canvas.currentBoard.gameEnded != 0){
            msgId = (canvas.currentBoard.gameEnded == canvas.getPlayerPov() ?
                    63 : 64);
        }
        else msgId = canvas.getPlayerPov() == canvas.currentBoard.getCurPlayer() ? 52 : 53;
        upLabel.setText(Local.str[msgId]);
    }

    /**
     * This method changes availability of certain elements of the program,
     * depending on a selected mode.
     * @param mode desired mode. Mode is precisely defined:
     * <ul>
     *     <li><b>0:</b> no active connection, user can either host or join a game</li>
     *     <li><b>1:</b> program is waiting for a partner to connect or attempting
     *             to connect to the server</li>
     *     <li><b>2:</b> connection with the partner has been established.</v></li>
     * </ul>
     */
    public static void setMode(int mode) {
        int oldMode = MainWindow.mode;
        int msgId = 0;
        int curPlayerId = canvas.currentBoard.getCurPlayer() == -1 ? 0 : 1;
        if(oldMode != 2) upLabel.setText("");
        if(mode == 0){
            canvas.currentBoard.pdata[0].timer.suspend();
            canvas.currentBoard.pdata[1].timer.suspend();
            labelPanel.setHintLabel("");
        }
        if(mode == 0 && oldMode == 0)
            msgId = 20;
        else if(mode == 1 && oldMode == 2)
            msgId = 38;
        else if(mode == 0 && oldMode == 1)
            msgId = 39;
        else if(mode == 0 && oldMode == 2)
            msgId = 19;
        else if(mode == 1 && connection instanceof ConnectionClient)
            msgId = 36;
        else if(mode == 1 && connection instanceof ConnectionServer)
            msgId = 35;
        else if(mode == 2 && oldMode != 2) {
            msgId = 37;
            canvas.regenerateCanvas();
            canvas.repaint();
            labelPanel.updatePlayers();
            setWhoseMove();
            if(!canvas.currentBoard.pdata[curPlayerId].timer.isAlive())
                canvas.currentBoard.pdata[curPlayerId].timer.start();
        }
        else if(oldMode == 2) return;
        if(mode < 2 && canvas.currentBoard.pdata[curPlayerId].timer.isAlive())
            canvas.currentBoard.pdata[curPlayerId].timer.interrupt();
        String s = msgId == 37 ? (Local.str[37] +
                canvas.currentBoard.pdata[connection instanceof ConnectionServer ? 1 : 0].getName()) : Local.str[msgId];

        labelPanel.setSelectionLabel(s);
        SystemMsg(s);
        MainWindow.mode = mode;
        menu.setMenuMode(mode);
        panel.a.setChatMode(mode);
    }

    /**
     * Gets value of {@link #mode}.
     * @return int value of {@link #mode} variable.
     */
    public static int getMode(){
        return mode;
    }


    /**
     * Initializes {@link MainWindow} class members - their appearance, dimensions,
     * position etc.
     */
    public void init(){
        System.setProperty("java.class.path", System.getProperty("java.class.path") + ":./graphics");
        Toolkit.getDefaultToolkit().setDynamicLayout(false);
        getContentPane().setLayout(new BorderLayout());

        panel = new TabPanel();
        panel.setMaximumSize(panelDim);
        panel.setMinimumSize(panelDim);
        panel.setPreferredSize(panelDim);

        canvas = new BoardCanvas(-1);
        getContentPane().add(canvas);
        getContentPane().add("East", panel);

        menu = new Menu();
        setJMenuBar(menu);

        gameFileFilter = new FileNameExtensionFilter(Local.str[12], "gam");
        gameFileChooser.setFileFilter(gameFileFilter);
        gameFileChooser.setAcceptAllFileFilterUsed(false);
        gameFileChooser.addChoosableFileFilter(MainWindow.gameFileFilter);
        //gameFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        dialogHost = new DialogHost();
        dialogJoin = new DialogJoin();

        Dimension dimension = new Dimension(-1,25);
        upLabel.setHorizontalAlignment(JLabel.CENTER);
        upLabel.setSize(dimension);
        upLabel.setPreferredSize(dimension);
        upLabel.setMinimumSize(dimension);
        getContentPane().add("North", upLabel);

        getContentPane().add("South",labelPanel);

        setMode(0);
    }

    /**
     * Primary and only main function of the program. Creates and launches
     * {@link MainWindow} applet.
     * @param s parameter is ignored.
     */
    public static void main(String s[]) {
        Local.init();
        Frame f = new Frame(Local.str[0]);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doCloseGame();
            }
        });
        Applet applet = new MainWindow();
        f.add("Center", applet);
        applet.init();
        f.setIconImage(canvas.textureLoader.getTexture(canvas.currentBoard.piece[0],-1,false));
        f.pack();
        f.setSize(new Dimension(900, 670));
        f.setResizable(false);
        f.show();
    }

    /**
     * Updates {@link #canvas} to display i-th element of {@link CurrentBoard#history} on a board.
     */
    public static void changeViewOnBoard(int i){
        canvas.setHistoryPreview(i);
        canvas.repaint();
    }
}

