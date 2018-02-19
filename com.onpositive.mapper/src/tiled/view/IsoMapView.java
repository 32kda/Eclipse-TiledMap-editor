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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import tiled.core.Map;
import tiled.core.ObjectGroup;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.util.Converter;
import tiled.util.Orientation;

/**
 * @version $Id$
 */
public class IsoMapView extends MapView
{
    /**
     * Creates a new isometric map view that displays the specified map.
     *
     * @param map the map to be displayed by this map view
     */
    public IsoMapView(Composite parent, Map map) {
        super(parent, map);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Point tsize = getTileSize();
        if (orientation == Orientation.VERTICAL) {
            return (visibleRect.height / tsize.y) * tsize.y;
        } else {
            return (visibleRect.width / tsize.x) * tsize.x;
        }
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Point tsize = getTileSize();
        if (orientation == Orientation.VERTICAL) {
            return tsize.y;
        } else {
            return tsize.x;
        }
    }

    protected void paintLayer(GC gc, TileLayer layer) {
        // Turn anti alias on for selection drawing
        gc.setAntialias(SWT.ON);

        Rectangle clipRect = gc.getClipping();
        Point tileSize = getTileSize();
        int tileStepY = tileSize.y / 2 == 0 ? 1 : tileSize.y / 2;
        Polygon gridPoly = createGridPolygon(0, -tileSize.y, 0);

        Point rowItr = screenToTileCoords(clipRect.x, clipRect.y);
        rowItr.x--;
        Point drawLoc = tileToScreenCoords(rowItr.x, rowItr.y);
        drawLoc.x -= tileSize.x / 2;
        drawLoc.y += tileSize.y;

        // Determine area to draw from clipping rectangle
        int columns = clipRect.width / tileSize.x + 3;
        int rows = (clipRect.height + (int)(map.getTileHeightMax() * zoom)) /
            tileStepY + 4;

        // Draw this map layer
        for (int y = 0; y < rows; y++) {
            Point columnItr = new Point(rowItr.x,rowItr.y);

            for (int x = 0; x < columns; x++) {
                Tile tile = layer.getTileAt(columnItr.x, columnItr.y);

                if (tile != null) {
                    if (layer instanceof SelectionLayer) {
                    	if (shouldPaintBrushTile())
                    		RenderingUtil.drawTile(gc, tile, drawLoc.x, drawLoc.y, zoom);
                        //Polygon gridPoly = createGridPolygon(
                                //drawLoc.x, drawLoc.y - tileSize.height, 0);
                        gridPoly.translate(drawLoc.x, drawLoc.y - tileSize.y / 2);
                        gc.setAlpha(SEL_HOVER_ALPHA);
                        gc.fillPolygon(Converter.getPolygonArray(gridPoly));
                        gridPoly.translate(-drawLoc.x, -(drawLoc.y - tileSize.y / 2));
                        gc.setAlpha(OPAQUE);
                        //paintEdge(g2d, layer, drawLoc.x, drawLoc.y);
                    } else {
                        RenderingUtil.drawTile(gc, tile, drawLoc.x, drawLoc.y, zoom);
                    }
                }

                // Advance to the next tile
                columnItr.x++;
                columnItr.y--;
                drawLoc.x += tileSize.x;
            }

            // Advance to the next row
            if ((y & 1) > 0) {
                rowItr.x++;
                drawLoc.x += tileSize.x / 2;
            } else {
                rowItr.y++;
                drawLoc.x -= tileSize.x / 2;
            }
            drawLoc.x -= columns * tileSize.x;
            drawLoc.y += tileStepY;
        }
    }

    protected void paintObjectGroup(GC g2d, ObjectGroup og) {
        // TODO: Implement objectgroup painting for IsoMapView
    }

    protected void paintGrid(GC g2d) {
        Point tileSize = getTileSize();
        Rectangle clipRect = g2d.getClipping();

        clipRect.x -= tileSize.x / 2;
        clipRect.width += tileSize.x;
        clipRect.height += tileSize.y / 2;

        int startX = Math.max(0, screenToTileCoords(clipRect.x, clipRect.y).x);
        int startY = Math.max(0, screenToTileCoords(
                    clipRect.x + clipRect.width, clipRect.y).y);
        int endX = Math.min(map.getWidth(), screenToTileCoords(
                    clipRect.x + clipRect.width,
                    clipRect.y + clipRect.height).x);
        int endY = Math.min(map.getHeight(), screenToTileCoords(
                    clipRect.x, clipRect.y + clipRect.height).y);

        for (int y = startY; y <= endY; y++) {
            Point start = tileToScreenCoords(startX, y);
            Point end = tileToScreenCoords(endX, y);
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
        for (int x = startX; x <= endX; x++) {
            Point start = tileToScreenCoords(x, startY);
            Point end = tileToScreenCoords(x, endY);
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    protected void paintCoordinates(GC g2d) {
        g2d.setAntialias(SWT.ON);

        Rectangle clipRect = g2d.getClipping();
        Point tileSize = getTileSize();
        int tileStepY = tileSize.y / 2 == 0 ? 1 : tileSize.y / 2;
//        Font font = new Font(g2d.getDevice(), new FontData("SansSerif", tileSize.y / 4,SWT.NONE)); //TODO font
//        g2d.setFont(font);

        Point rowItr = screenToTileCoords(clipRect.x, clipRect.y);
        rowItr.x--;
        Point drawLoc = tileToScreenCoords(rowItr.x, rowItr.y);
        drawLoc.y += tileSize.y / 2;

        // Determine area to draw from clipping rectangle
        int columns = clipRect.width / tileSize.x + 3;
        int rows = clipRect.height / tileStepY + 4;

        // Draw the coordinates
        for (int y = 0; y < rows; y++) {
            Point columnItr = new Point(rowItr.x,rowItr.y);

            for (int x = 0; x < columns; x++) {
                if (map.contains(columnItr.x, columnItr.y)) {
                    String coords =
                        "(" + columnItr.x + "," + columnItr.y + ")";
//                    Rectangle2D textSize =
//                        font.getStringBounds(coords, fontRenderContext);
                    Point textSize = g2d.stringExtent(coords);

                    int fx = drawLoc.x - (int)(textSize.x / 2);
                    int fy = drawLoc.y + (int)(textSize.y / 2);

                    g2d.drawString(coords, fx, fy);
                }

                // Advance to the next tile
                columnItr.x++;
                columnItr.y--;
                drawLoc.x += tileSize.x;
            }

            // Advance to the next row
            if ((y & 1) > 0) {
                rowItr.x++;
                drawLoc.x += tileSize.x / 2;
            } else {
                rowItr.y++;
                drawLoc.x -= tileSize.x / 2;
            }
            drawLoc.x -= columns * tileSize.x;
            drawLoc.y += tileStepY;
        }
        g2d.setAntialias(SWT.OFF);
    }

    protected void paintPropertyFlags(GC g2d, TileLayer layer) {
        throw new RuntimeException("Not yet implemented");    // todo
    }

    public void repaintRegion(Rectangle region) {
        Point tileSize = getTileSize();
        int maxExtraHeight =
            (int)(map.getTileHeightMax() * zoom) - tileSize.y;

        int mapX1 = region.x;
        int mapY1 = region.y;
        int mapX2 = mapX1 + region.width;
        int mapY2 = mapY1 + region.height;

        int x1 = tileToScreenCoords(mapX1, mapY2).x;
        int y1 = tileToScreenCoords(mapX1, mapY1).y - maxExtraHeight;
        int x2 = tileToScreenCoords(mapX2, mapY1).x;
        int y2 = tileToScreenCoords(mapX2, mapY2).y;

        redraw(x1, y1, x2 - x1, y2 - y1, true);
    }
    
    public void repaintRegion(Rectangle region, Point brushSize) {
        Point tileSize = getTileSize();
        int maxExtraHeight =
            (int)(map.getTileHeightMax() * zoom) - tileSize.y;

        int mapX1 = region.x;
        int mapY1 = region.y;
        int mapX2 = mapX1 + region.width;
        int mapY2 = mapY1 + region.height;

        int x1 = tileToScreenCoords(mapX1, mapY2).x;
        int y1 = tileToScreenCoords(mapX1, mapY1).y - maxExtraHeight;
        int x2 = tileToScreenCoords(mapX2, mapY1).x;
        int y2 = tileToScreenCoords(mapX2, mapY2).y;

        redraw(x1, y1, x2 - x1, y2 - y1,true);
    }
    

    public Point getPreferredSize() {
        Point tileSize = getTileSize();
        int border = showGrid ? 1 : 0;
        int mapSides = map.getHeight() + map.getWidth();

        return new Point(
                (mapSides * tileSize.x) / 2 + border,
                (mapSides * tileSize.y) / 2 + border);
    }

    /**
     * Returns the coordinates of the tile at the given screen coordinates.
     */
    public Point screenToTileCoords(int x, int y) {
        Point tileSize = getTileSize();
        double r = getTileRatio();

        // Translate origin to top-center
        x -= map.getHeight() * (tileSize.x / 2);
        int mx = y + (int)(x / r);
        int my = y - (int)(x / r);

        // Calculate map coords and divide by tile size (tiles assumed to
        // be square in normal projection)
        return new Point(
                (mx < 0 ? mx - tileSize.y : mx) / tileSize.y, //TODO maybe x?
                (my < 0 ? my - tileSize.y : my) / tileSize.y);
    }

    public Point screenToPixelCoords(int x, int y) {
        return new Point(x,y);
    }

    protected Polygon createGridPolygon(int tx, int ty, int border) {
        Point tileSize = getTileSize();
        tileSize.x -= border * 2;
        tileSize.y -= border * 2;

        Polygon poly = new Polygon();
        poly.addPoint(tx + tileSize.x / 2 + border, ty + border);
        poly.addPoint(tx + tileSize.x, ty + tileSize.y / 2 + border);
        poly.addPoint(tx + tileSize.x / 2 + border,
                ty + tileSize.y + border);
        poly.addPoint(tx + border, ty + tileSize.y / 2 + border);
        return poly;
    }

    protected Point getTileSize() {
        return new Point(
                (int)(map.getTileWidth() * zoom),
                (int)(map.getTileHeight() * zoom));
    }

    protected double getTileRatio() {
        return (double)map.getTileWidth() / (double)map.getTileHeight();
    }

    /**
     * Returns the location on the screen of the top corner of a tile.
     */
    public Point tileToScreenCoords(int x, int y) {
        Point tileSize = getTileSize();
        int originX = (map.getHeight() * tileSize.x) / 2;
        return new Point(
                ((x - y) * tileSize.x / 2) + originX,
                ((x + y) * tileSize.y / 2));
    }
    
    @Override
    public Point getSnappedVector(Point vector) {
    	Point tilePoint = screenToTileCoords(vector.x, vector.y);
    	return tileToScreenCoords(tilePoint.x, tilePoint.y);
    }
}
