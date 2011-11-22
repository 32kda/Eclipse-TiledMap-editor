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

package tiled.view;

import java.awt.Polygon;

import javax.swing.SwingConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import tiled.core.Map;
import tiled.core.ObjectGroup;
import tiled.core.TileLayer;

/**
 * @version $Id$
 */
public class ShiftedMapView extends MapView
{
    private int horSide;       // Length of horizontal sides
    private int verSide;       // Length of vertical sides

    /**
     * Creates a new shifted map view that displays the specified map.
     * @param parent 
     *
     * @param map the map to be displayed by this map view
     */
    public ShiftedMapView(Composite parent, Map map) {
        super(parent, map);

        horSide = 16;
        verSide = 0;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        int unit =
            getScrollableUnitIncrement(visibleRect, orientation, direction);

        if (orientation == SwingConstants.VERTICAL) {
            return (visibleRect.height / unit) * unit;
        } else {
            return (visibleRect.width / unit) * unit;
        }
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Point tsize = getTileSize();
        if (orientation == SwingConstants.VERTICAL) {
            return tsize.y - (tsize.y - (int) (verSide * zoom)) / 2;
        } else {
            return tsize.x - (tsize.x - (int) (horSide * zoom)) / 2;
        }
    }

    public Point getPreferredSize() {
        Point tsize = getTileSize();
        int border = showGrid ? 1 : 0;
        int onceX = (tsize.x - (int)(horSide * zoom)) / 2;
        int repeatX = tsize.x - onceX;
        int onceY = (tsize.y - (int)(verSide * zoom)) / 2;
        int repeatY = tsize.y - onceY;

        return new Point(
                map.getWidth() * repeatX + onceX + border,
                map.getHeight() * repeatY + onceY + border);
    }

    protected void paintLayer(GC gc, TileLayer layer) {
    }

    protected void paintObjectGroup(GC gc, ObjectGroup og) {
    }

    protected void paintGrid(GC gc) {
        // Determine tile size
        Point tsize = getTileSize();
        if (tsize.x <= 0 || tsize.y <= 0) return;
        int onceX = (tsize.x - (int)(horSide * zoom)) / 2;
        int repeatX = tsize.x - onceX;
        int onceY = (tsize.y - (int)(verSide * zoom)) / 2;
        int repeatY = tsize.y - onceY;
        if (repeatX <= 0 || repeatY <= 0) return;

        // Determine lines to draw from clipping rectangle
        Rectangle clipRect = gc.getClipping();
        int startX = clipRect.x / repeatX;
        int startY = clipRect.y / repeatY;
        int endX = (clipRect.x + clipRect.width) / repeatX + 1;
        int endY = (clipRect.y + clipRect.height) / repeatY + 1;
        int p = startY * repeatY;

        // These are temp debug lines not the real grid, draw in light gray
        Color prevColor = gc.getForeground();
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));

        for (int y = startY; y < endY; y++) {
            gc.drawLine(clipRect.x, p, clipRect.x + clipRect.width - 1, p);
            p += repeatY;
        }
        p = startX * repeatX;
        for (int x = startX; x < endX; x++) {
            gc.drawLine(p, clipRect.y, p, clipRect.y + clipRect.height - 1);
            p += repeatX;
        }

        gc.setForeground(prevColor);
    }

    protected void paintCoordinates(GC gc) {
    }

    protected void paintPropertyFlags(GC gc, TileLayer layer) {
        throw new RuntimeException("Not yet implemented");    // todo
    }

    public void repaintRegion(Rectangle region) {
    }

    public Point screenToTileCoords(int x, int y) {
        return new Point(0, 0);
    }

    public Point screenToPixelCoords(int x, int y) {
        // TODO: add proper implementation
        return new Point(0,0);
    }

    protected Point getTileSize() {
        return new Point(
                (int)(map.getTileWidth() * zoom),
                (int)(map.getTileHeight() * zoom));
    }

    protected Polygon createGridPolygon(int tx, int ty, int border) {
        return new Polygon();
    }

    public Point tileToScreenCoords(int x, int y) {
        return new Point(0, 0);
    }
}
