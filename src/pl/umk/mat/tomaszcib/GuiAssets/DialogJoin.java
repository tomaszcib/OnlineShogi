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
import java.awt.event.*;

/**
 *  A dialog window accessed through {@link Menu} "Join game" item. Allows the user to join an existing hosted game.
 */
public class DialogJoin extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField hostField;
    private JSpinner portSelect;
    private JPanel lowerPanel;
    private JPanel upperPanel;
    private JLabel l0;
    private JLabel l1;
    private JLabel l2;
    private JTextField nameField;

    /**
     * Returns name or address of a host user wants to connect to.
     * @return host name or address.
     */
    public String getHost(){
        return hostField.getText();
    }

    /**
     * Returns port number from "Port" spinner.
     * @return target host port.
     */
    public int getPort(){
        return (int)portSelect.getValue();
    }

    /**
     * Returns name of the joining player.
     * @return name of the joining player.
     */
    public String getName(){
        return nameField.getText();
    }

    /**
     * Constructor for the window. Called from within {@link MainWindow} class.
     */
    public DialogJoin() {
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setSize(400,120);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(Local.str[4]);
        l0.setText(Local.str[49]);
        l1.setText(Local.str[41]);
        l2.setText(Local.str[40]);


        portSelect.setModel(new SpinnerNumberModel(1500,0,65535,1));
        nameField.setText(System.getProperty("user.name"));
        hostField.setText("localhost");

        createListeners();
    }

    private void createListeners(){
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    private void onOK() {
        if(getName().isEmpty())
            JOptionPane.showMessageDialog(null, Local.str[59], Local.str[67], 0);
        else if(getHost().isEmpty())
            JOptionPane.showMessageDialog(null,Local.str[60],Local.str[67],0);
        else {
            MainWindow.doJoinGame();
            dispose();
        }
    }
    private void onCancel() {
        dispose();
    }

}
