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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * In-game move history panel, embedded in the {@link TabPanel}.
 */
public class PanelHistory extends JPanel implements ActionListener {
    private JButton[] button = new JButton[4];
    /**
     * Visual representation of the registered moves list.
     */
    public JList moveList;
    /**
     * Logical model of the registered moves list.
     */
    public DefaultListModel<String> model;

    /**
     * Constructor for the panel. Called from within {@link TabPanel#TabPanel()} constructor.
     */
    PanelHistory(){
        super(new GridBagLayout());
        ImageIcon[] historyIcon = new ImageIcon[4];
        Dimension iconDim = new Dimension(24,24);
        try{
            BufferedImage img;
            URL res = getClass().getResource("/graphics/toolbar.png");
            if(res != null)
                img = ImageIO.read(res);
            else{
                File f = new File("graphics/toolbar.png");
                img = ImageIO.read(f);
            }
            for(int i = 0; i < 4; i++)
                historyIcon[i] = new ImageIcon(img.getSubimage(24 * i + i + 1, 1, 24, 24));

        } catch (IOException e) {
            e.printStackTrace();
        }
        GridBagConstraints c = new GridBagConstraints();;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;

        for(int i = 0; i < 4; i++){
            c.gridx = i;
            button[i] = new JButton("", historyIcon[i]);
            button[i].setPreferredSize(iconDim);
            button[i].setMaximumSize(iconDim);
            button[i].setMinimumSize(iconDim);
            add(button[i], c);
            button[i].addActionListener(this);
        }

        model = new DefaultListModel<>();
        moveList = new JList(model);
        JScrollPane scrollPane = new JScrollPane(moveList);
        c.gridy = 1;
        c.gridx = 0;
        c.weighty = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.BOTH;
        add(scrollPane, c);
        setVisible(true);
        createActions();
    }

    /**
     * Navigation buttons event handler, sets their custom behaviour.
     * @param e action event.
     */
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == button[0]) moveList.setSelectedIndex(0);
        else if(e.getSource() == button[1]) moveList.setSelectedIndex(moveList.getSelectedIndex() - 1);
        else if(e.getSource() == button[2]) moveList.setSelectedIndex(moveList.getSelectedIndex() + 1);
        else if(e.getSource() == button[3]) moveList.setSelectedIndex(model.getSize() - 1);
    }

    private void createActions(){
        moveList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                MainWindow.changeViewOnBoard(moveList.getSelectedIndex());
                if(moveList.getSelectedIndex() == 0){
                    button[0].setEnabled(false);
                    button[1].setEnabled(false);
                }
                else{
                    button[0].setEnabled(true);
                    button[1].setEnabled(true);
                }
                if(moveList.getSelectedIndex() == moveList.getLastVisibleIndex()){
                    button[2].setEnabled(false);
                    button[3].setEnabled(false);
                }
                else{
                    button[2].setEnabled(true);
                    button[3].setEnabled(true);
                }
            }
        });
    }
}
