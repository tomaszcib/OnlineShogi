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
 * A dialog window accessed through {@link Menu} "Host game" item. Allows the user to host a new game in the program.
 */
public class DialogHost extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner portSelect;
    private JTextField nameField;
    private JRadioButton gameMode0;
    private JRadioButton gameMode1;
    private JButton fileOpen;
    private JTextField filePath;
    private JRadioButton starts0;
    private JRadioButton starts1;
    private JRadioButton starts2;
    private JLabel l1;
    private JLabel l0;
    private JPanel rightPanel;
    private JPanel leftPanel;
    private ButtonGroup gameModeGroup;
    private ButtonGroup startGroup;
    private JFileChooser openFileChooser = new JFileChooser();

    /**
     * Gets value from the "Port" spinner.
     * @return port used for game hosting.
     */
    public int getPort(){
        return (int)portSelect.getValue();
    }

    /**
     * Gets player's name.
     * @return hosting player's name.
     */
    public String getName(){
        return nameField.getText();
    }

    /**
     * Determines if player wants to start a new game or load data from an existing <i>.gam</i> file.
     * @return 0 if player starts a new game; 1 if loads existing one.
     */
    public int getMode(){
        return gameMode0.isSelected() ? 0 : 1;
    }

    /**
     * Returns ID of starting player.
     * @return -1 if hosting player starts; 1 if joining one.
     */
    public int getStartingPlayer(){
        return starts0.isSelected() ? 1 : (starts1.isSelected() ? -1 : (Math.random() >= 0.5 ? 1 : -1));
    }

    /**
     * Returns absolute path to a selected <i>.gam</i> file.
     * @return absolute path to a <i>.gam</i> file.
     */
    public String getFilePath(){
        return filePath.getText();
    }

    /**
     * Constructor for the window. Called from within {@link MainWindow} class.
     */
    public DialogHost() {
        Local.init();
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setSize(600,250);
        getRootPane().setDefaultButton(buttonOK);

        setTitle(Local.str[3]);
        l0.setText(Local.str[40]);
        l1.setText(Local.str[41]);
        gameMode0.setText(Local.str[42]);
        gameMode1.setText(Local.str[43]);
        starts0.setText(Local.str[44]);
        starts1.setText(Local.str[45]);
        starts2.setText(Local.str[46]);
        buttonOK.setText(Local.str[47]);
        buttonCancel.setText(Local.str[48]);
        fileOpen.setText(Local.str[50]);
        portSelect.setModel(new SpinnerNumberModel(1500,0,65535,1));
        nameField.setText(System.getProperty("user.name"));

        gameModeGroup = new ButtonGroup();
        gameModeGroup.add(gameMode0);
        gameModeGroup.add(gameMode1);
        startGroup = new ButtonGroup();
        startGroup.add(starts0);
        startGroup.add(starts1);
        startGroup.add(starts2);
        createListeners();

        gameMode0.setSelected(true);
        starts2.setSelected(true);
        fileOpen.setEnabled(false);
        filePath.setEnabled(false);
        openFileChooser.setFileFilter(MainWindow.gameFileFilter);
        openFileChooser.setAcceptAllFileFilterUsed(false);
        openFileChooser.addChoosableFileFilter(MainWindow.gameFileFilter);
        //openFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
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

        fileOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(openFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    filePath.setText(openFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        gameMode0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                starts0.setEnabled(true);
                starts1.setEnabled(true);
                starts2.setEnabled(true);
                filePath.setEnabled(false);
                fileOpen.setEnabled(false);
            }
        });
        gameMode1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                starts0.setEnabled(false);
                starts1.setEnabled(false);
                starts2.setEnabled(false);
                filePath.setEnabled(true);
                fileOpen.setEnabled(true);
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
        else if(getMode() == 1 && getFilePath().isEmpty())
            JOptionPane.showMessageDialog(null, Local.str[61], Local.str[67], 0);
        else {
            MainWindow.doHostGame();
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }
}
