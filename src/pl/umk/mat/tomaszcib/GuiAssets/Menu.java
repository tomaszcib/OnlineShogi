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

import pl.umk.mat.tomaszcib.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Custom JMenuBar class for the game {@link MainWindow}. Creates menus, menu items and action listeners for them.
 * @author Tomasz Ciborski
 */
public class Menu extends JMenuBar {
    private JMenu[] menu = new JMenu[2];
    private JMenuItem[] menuItem = new JMenuItem[7];

    /**
     * Main and only constructor. Called from within {@link MainWindow} class on program startup.
     */
    public Menu(){
        for(int i = 0; i < 2; i++) {
            menu[i] = new JMenu(Local.str[i + 1]);
            this.add(menu[i]);
        }
        for(int i = 0; i < 7; i++)
            menuItem[i] = new JMenuItem(Local.str[i + 3]);
        menu[0].add(menuItem[0]);
        menu[0].add(menuItem[1]);
        menu[0].addSeparator();
        menu[0].add(menuItem[2]);
        menu[0].addSeparator();
        menu[0].add(menuItem[3]);
        menu[0].addSeparator();
        menu[0].add(menuItem[4]);
        menu[1].add(menuItem[5]);
        menu[1].add(menuItem[6]);

        /* Host game */
        menuItem[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
        menuItem[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MainWindow.dialogHost.show();
            }
        });

        /* Join game */
        menuItem[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK));
        menuItem[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MainWindow.dialogJoin.show();
            }
        });

        /* Disconnect */
        menuItem[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
        menuItem[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(JOptionPane.showConfirmDialog(null, Local.str[54], Local.str[5],
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                    MainWindow.setMode(1);
                    MainWindow.connection.close();
                }
                //MainWindow.setMode(0);
            }
        });

        /* Save game */
        menuItem[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menuItem[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(MainWindow.gameFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                    String s = MainWindow.gameFileChooser.getSelectedFile().getAbsolutePath();
                    try(FileOutputStream fos = new FileOutputStream(s);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        ObjectOutputStream oos = new ObjectOutputStream(bos);){
                        MainWindow.canvas.currentBoard.saveCurrentGameToStream(oos);
                        JOptionPane.showMessageDialog(null,Local.str[69], Local.str[6], 1);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, Local.str[58], Local.str[67], 0);
                    }

                }

            }
        });
        /* Exit game */
        menuItem[4].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MainWindow.doCloseGame();
            }
        });

        /* Game info */
        menuItem[5].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, Local.str[51],
                        Local.str[0] + " - " + Local.str[8], 1);
            }
        });
        /* Game rules */
        menuItem[6].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(Local.shogiRulesWebsite);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Function called from within {@link MainWindow#setMode(int)} function. Sets availability of certain
     * elements of the menu depending on the current mode of the program.
     * @param mode target mode to switch to.
     */
    public void setMenuMode(int mode){
        if(mode == 0){
            menuItem[0].setEnabled(true);
            menuItem[1].setEnabled(true);
            menuItem[2].setEnabled(false);
        }
        if(mode == 1 || mode == 2){
            menuItem[0].setEnabled(false);
            menuItem[1].setEnabled(false);
            menuItem[2].setEnabled(true);
        }
    }

}
