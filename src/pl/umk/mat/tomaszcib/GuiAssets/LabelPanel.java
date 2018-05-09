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

/**
 * This class represents {@link MainWindow#labelPanel}, the panel at the bottom of the window.
 */
public class LabelPanel extends JPanel{
    private Label[] label = new Label[5];
    private FlowLayout layout = new FlowLayout();
    private JSeparator[] separator = new JSeparator[5];
    private Dimension sepDim = new Dimension(5,20);

    /**
     * Constructor for the panel. Called from within {@link MainWindow} class.
     */
    public LabelPanel(){
        layout.setAlignment(0);
        setLayout(layout);
        for(int i = 0; i < 5; i++) {
            label[i] = new Label("");
            if(i > 1)
                label[i].setAlignment(1);
            separator[i] = new JSeparator(SwingConstants.VERTICAL);
        }
        label[0].setPreferredSize(new Dimension(200,20));
        add(label[0]);
        separator[0].setPreferredSize(new Dimension(5,20));
        label[1].setPreferredSize(new Dimension(300,20));
        add(separator[0]);
        add(label[1]);
        separator[1].setPreferredSize(new Dimension(5,20));
        add(separator[1]);
        label[2].setPreferredSize(new Dimension(50,20));
        add(label[2]);
        separator[2].setPreferredSize(sepDim);
        add(separator[2]);
        label[3].setPreferredSize(new Dimension(200,20));
        add(label[3]);
        separator[3].setPreferredSize(sepDim);
        add(separator[3]);
        label[4].setPreferredSize(new Dimension(50,20));
        add(label[4]);
        setVisible(true);
    }

    /**
     * Changes displayed value of one of the timers.
     * @param which 0 or 1, which timer should be updated.
     * @param time time in seconds, later to be formatted into mm:ss.
     */
    public void setTimer(int which, int time){
        label[which == 0 ? 2 : 4].setText(String.format("%02d:%02d", time / 60, time % 60));
    }

    /**
     * Changes text of the most left label of the panel.
     * @param s text to be displayed.
     */
    public void setSelectionLabel(String s){
        label[0].setText(s);
    }

    /**
     * Changes text of the second label of the panel.
     * @param s text to be displayed.
     */
    public void setHintLabel(String s){
        label[1].setText(s);
    }

    /**
     * Gets players names from {@link pl.umk.mat.tomaszcib.GameLogic.CurrentBoard#pdata} and displays them in one of
     * the fields on the panel.
     */
    public void updatePlayers(){
        label[3].setText(MainWindow.canvas.currentBoard.pdata[0].getName() + " vs. " +
        MainWindow.canvas.currentBoard.pdata[1].getName());
    }
}
