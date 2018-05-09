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

import pl.umk.mat.tomaszcib.GameLogic.Piece;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Class for texture and in-game graphics management.
 */
public class TextureLoader{
    private BufferedImage img = null;
    private Rectangle2D tr;
    private AffineTransform tx, scaleTray;
    private AffineTransformOp op;
    public TexturePaint boardTexture, trayTexture;
    private BufferedImage[] texture = new BufferedImage[32];

    TextureLoader(){
        tr = new Rectangle2D.Double(0,0,40,50);
        tx = new AffineTransform();
        tx.rotate(3.14,20,22.5);
        op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        scaleTray = new AffineTransform();
        scaleTray.scale(0.8,0.8);

        try{
            URL res = getClass().getResource("/graphics/ShogiPieces.png");
            if(res != null)
                img = ImageIO.read(res);
            else{
                File f = new File("graphics/ShogiPieces.png");
                img = new BufferedImage(329,93,BufferedImage.TYPE_4BYTE_ABGR);
                img = ImageIO.read(f);
            }
            for(int i = 0; i < 16; i++){
                texture[i] = img.getSubimage(40 * (i % 8) + (i % 8) + 1,
                        45 * (i / 8) + (i / 8) + 1, 40, 45);
                texture[i + 16] = op.filter(texture[i], null);
            }
            boardTexture = new TexturePaint(texture[8], tr);
            trayTexture = new TexturePaint(texture[9], tr);
            boardTexture = trayTexture;

        }
        catch(IOException e){
            e.printStackTrace();
        }
        op = new AffineTransformOp(scaleTray, AffineTransformOp.TYPE_BILINEAR);

    }

    /**
     * Returns an image for a selected in-game piece.
     * @param p selected piece data.
     * @param pov current {@link pl.umk.mat.tomaszcib.GameLogic.CurrentBoard}'s point-ov-view.
     * @param tray if piece is captured in the tray or not.
     * @return an image representation of a selected piece.
     */
    public BufferedImage getTexture(Piece p, int pov, boolean tray){
        int val = p.getPlayer() == pov ? 0 : 16;
        val += (p.isPromoted() ? 8 : 0);
        val += (p.getType().getValue());
        return tray ? op.filter(texture[val], null) : texture[val];
    }

}
