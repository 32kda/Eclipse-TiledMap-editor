/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package tiled.mapeditor.util.cutter;

import java.awt.Dimension;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * @version $Id$
 */
public class BorderTileCutter implements TileCutter
{
    public String getName() {
        return "Border";
    }

    public void setImage(Image image) {
        // TODO Auto-generated method stub
    }

    public Image getNextTile() {
        // TODO Auto-generated method stub
        return null;
    }

    public void reset() {
        // TODO Auto-generated method stub
    }

    public Point getTileDimensions() {
        // TODO Auto-generated method stub
        return null;
    }
}
