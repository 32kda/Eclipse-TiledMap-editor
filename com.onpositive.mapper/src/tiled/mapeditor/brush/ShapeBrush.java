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

package tiled.mapeditor.brush;


import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.MultilayerPlane;
import tiled.util.Converter;
import tiled.view.MapView;

/**
 * @version $Id$
 */
public class ShapeBrush extends AbstractBrush
{
    protected Area shape;
    protected Tile paintTile;

    public ShapeBrush() {
    }

    public ShapeBrush(Area shape) {
        this.shape = shape;
    }

    public ShapeBrush(AbstractBrush sb) {
        super(sb);
        if (sb instanceof ShapeBrush) {
            shape = ((ShapeBrush) sb).shape;
            paintTile = ((ShapeBrush) sb).paintTile;
        }
    }

    /**
     * Makes this brush a circular brush.
     *
     * @param rad the radius of the circular region
     */
    public void makeCircleBrush(double rad) {
        shape = new Area(new Ellipse2D.Double(0, 0, rad * 2, rad * 2));
        resize((int)(rad * 2), (int)(rad * 2), 0, 0);
    }

    /**
     * Makes this brush a rectangular brush.
     *
     * @param r a Rectangle to use as the shape of the brush
     */
    public void makeQuadBrush(Rectangle r) {
        shape = new Area(new Rectangle2D.Double(r.x, r.y, r.width, r.height));
        resize(r.width, r.height, 0, 0);
    }

    public void makePolygonBrush(Polygon p) {
    }

    public void setSize(int size) {
        if (shape.isRectangular()) {
            makeQuadBrush(new Rectangle(0, 0, size, size));
        } else if (!shape.isPolygonal()) {
            makeCircleBrush(size/2);
        } else {
            // TODO: scale the polygon brush
        }
    }

    public void setTile(Tile t) {
        paintTile = t;
    }

    public Tile getTile() {
        return paintTile;
    }

    public Rectangle getBounds() {
        return Converter.AWTRectToSWT(shape.getBounds());
    }

    public Shape getShape() {
        return shape;
    }

    public void drawPreview(GC gc, Point dimension, MapView mv) {
    	PathIterator pathIterator = shape.getPathIterator(null);
    	Path path = Converter.pathIterator2Path(pathIterator);
        gc.fillPath(path);
    }

    public void drawPreview(GC g2d, MapView mv) {
    }

    public boolean equals(Brush brush) {
        return brush instanceof ShapeBrush &&
                ((ShapeBrush) brush).shape.equals(shape);
    }

    public void startPaint(MultilayerPlane mp, int x, int y, int button, int layer) {
        super.startPaint(mp, x, y, button, layer);
    }

    /**
     * Paints the entire area of the brush with the set tile. This brush can
     * affect several layers.
     * @throws Exception
     *
     * @see tiled.mapeditor.brush.Brush#doPaint(int, int)
     */
    public org.eclipse.swt.graphics.Rectangle doPaint(int x, int y) throws Exception
    {
        Rectangle shapeBounds = Converter.AWTRectToSWT(shape.getBounds());
        int centerx = x - shapeBounds.width / 2;
        int centery = y - shapeBounds.height / 2;

        super.doPaint(x, y);

        // FIXME: This loop does not take all edges into account

        for (int layer = 0; layer < numLayers; layer++) {
            TileLayer tl = (TileLayer) affectedMp.getLayer(initLayer + layer);
            if (tl != null) {
                for (int i = 0; i <= shapeBounds.height + 1; i++) {
                    for (int j = 0; j <= shapeBounds.width + 1; j++) {
                        if (shape.contains(j, i)) {
                            tl.setTileAt(j + centerx, i + centery, paintTile);
                        }
                    }
                }
            }
        }

        // Return affected area
        return new Rectangle(
                centerx, centery, shapeBounds.width, shapeBounds.height);
    }
}
