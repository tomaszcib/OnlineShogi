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

import java.net.URI;
import java.net.URISyntaxException;


/**
 * A language localization class for the game.
 * @author  Tomasz Ciborski
 */
public final class Local {
    /**
     * An array of strings used within the program.
     */
    final public static String[] str = new String[70];
    /**
     * A string containing letter symbols of the Shogi pieces.
     */
    public static String pieceLetterId;
    /**
     * A link to Wikipedia's page on Shogi.
     */
    public static URI shogiRulesWebsite = null;

    /**
     * Sets up initial and final values for the included member strings.
     */
    public static void init(){
        str[0] = "Shogi";
        str[1] = "File";
        str[2] = "Help";
        str[3] = "Host game";
        str[4] = "Join game";
        str[5] = "Disconnect";
        str[6] = "Save game";
        str[7] = "Exit";
        str[8] = "About";
        str[9] = "Rules (Wikipedia)";
        str[12] = "Shogi game files (.gam)";
        str[15] = "Chat";
        str[16] = "History";
        str[17] = "Send";
        str[18] = "Game start";

        str[19] = "Partner left";
        str[20] = "Host or join a game to begin";
        str[21] = "L click to select piece";
        str[22] = "R to move; L on piece to change move mode";
        str[23] = "(normal move)";
        str[24] = "(promoting move)";
        str[25] = "King";
        str[26] = "Golden general";
        str[27] = "Silver general";
        str[28] = "Knight";
        str[29] = "Lance";
        str[30] = "Rook";
        str[31] = "Bishop";
        str[32] = "Pawn";
        str[33] = "Promoted";
        str[34] = "Waiting for your partner to move...";
        str[35] = "Waiting for partner to connect...";
        str[36] = "Joining game...";
        str[37] = "Successfully connected with ";
        str[38] = "Disconnecting...";
        str[39] = "Disconnected";


        str[40] = "Port";
        str[41] = "Your name";
        str[42] = "Start a new game";
        str[43] = "Load game from a file";
        str[44] = "Server moves first";
        str[45] = "Client moves first";
        str[46] = "Random player moves first";
        str[47] = "OK";
        str[48] = "Cancel";
        str[49] = "Host";
        str[50] = "Browse...";

        str[51] = "<html><h2>"+Local.str[0]+"</h2>A Japanese chess game.<br>" +
        "<br>author: Tomasz Ciborski, 2018<br><br><a href=\"mailto:tomaszcib@mat.umk.pl\">tomaszcib@mat.umk.pl</a></html>";

        str[52] = "Your move";
        str[53] = "Your partner's move";

        str[54] = "Do you really want to disconnect?";
        str[55] = "Do you really want to exit?";

        str[57] = "This file already exists? Do you want to overwrite?";
        str[58] = "Could not save/open the selected file.";
        str[59] = "Your name must not be empty!";
        str[60] = "Host must not be empty!";
        str[61] = "File path must not be empty!";
        str[62] = "Could not establish connection.";

        str[63] = "You lost the game!";
        str[64] = "You won the game!";
        str[65] = "Game ended with a draw. No other moves possible.";
        str[66] = "End of game";
        str[67] = "Error";

        str[68] = "Could not retreive game data.";
        str[69] = "Game successfuly saved!";

        pieceLetterId = "KGSNLRBP";

        try {
            shogiRulesWebsite = new URI("https://en.wikipedia.org/wiki/Shogi");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
    private Local(){}
}
