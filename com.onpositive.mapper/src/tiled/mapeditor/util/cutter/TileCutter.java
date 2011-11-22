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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * A generic interface to a class that implements tile cutting behavior.
 *
 * @version $Id$
 */
public interface TileCutter
{
    /**
     * Sets the image that this cutter should cut in tile images.
     * @param tilebmp the image that this cutter should cut
     */
    public void setImage(org.eclipse.swt.graphics.Image tilebmp);

    /**
     * Retrieves the next tile image.
     * @return the next tile image, or <code>null</code> when no more tile
     *         images are available
     */
    public Image getNextTile();

    /**
     * Resets the tile cutter so that the next call to <code>getNextTile</code>
     * will return the first tile.
     */
    void reset();

    /**
     * Returns the default tile dimensions of tiles cut by this cutter.
     * @return the default tile dimensions of tiles cut by this cutter.
     */
    public Point getTileDimensions();

    /**
     * Returns the name of this tile cutter.
     * @return the name of this tile cutter
     */
    public String getName();
}
