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


import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;


/**
 * Class containing chat and history panels displayed in the right part of the {@link pl.umk.mat.tomaszcib.MainWindow}.
 * @author  Tomasz Ciborski
 */
public class TabPanel extends JPanel {
    /**
     * Chat panel (left tab).
     */
    public PanelChat a;
    /**
     * Move history panel (right tab).
     */
    public PanelHistory b;

    /**
     * Called from within {@link pl.umk.mat.tomaszcib.MainWindow}.
     */
    public TabPanel() {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0; c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        JTabbedPane tabbedPane = new JTabbedPane();

        a = new PanelChat();
        b = new PanelHistory();
        tabbedPane.addTab(Local.str[15], a);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab(Local.str[16], b);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        add(tabbedPane, c);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setVisible(true);
    }

}