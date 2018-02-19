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
import java.util.Iterator;
import java.util.Properties;

import javax.swing.SwingConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Composite;

import tiled.core.Map;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.util.AnchoringUtil;
import tiled.util.Converter;

/**
 * An orthographic map view.
 */
public class OrthoMapView extends MapView
{
	protected Polygon propPoly;
//    protected Color selColor;
    
    private static final int OBJECT_FOREGROUND = SWT.COLOR_GRAY;

    /**
     * Creates a new orthographic map view that displays the specified map.
     * @param parent 
     *
     * @param map the map to be displayed by this map view
     */
    public OrthoMapView(Composite parent, Map map) {
        super(parent, map);

        propPoly = new Polygon();
        propPoly.addPoint(0, 0);
        propPoly.addPoint(12, 0);
        propPoly.addPoint(12, 12);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Point tsize = getTileSize();

        if (orientation == SwingConstants.VERTICAL) {
            return (visibleRect.height / tsize.y) * tsize.y;
        }
        else {
            return (visibleRect.width / tsize.x) * tsize.x;
        }
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Point tsize = getTileSize();
        if (orientation == SwingConstants.VERTICAL) {
            return tsize.y;
        }
        else {
            return tsize.x;
        }
    }

    public Point getPreferredSize() {
        Point tsize = getTileSize();

        return new Point(
                map.getWidth() * tsize.x,
                map.getHeight() * tsize.y);
    }

    protected void paintLayer(GC gc, TileLayer layer) {
        // Determine tile size and offset
        Point tsize = getTileSize();
        if (tsize.x <= 0 || tsize.y <= 0) {
            return;
        }
        Polygon gridPoly = createGridPolygon(0, -tsize.y, 0);

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = gc.getClipping();
        int startX = clipRect.x / tsize.x - 1; //-1 for large tiles
        int startY = clipRect.y / tsize.y;
        int endX = (clipRect.x + clipRect.width) / tsize.x + 1;
        int endY = (clipRect.y + clipRect.height) / tsize.y + 3;
        // (endY +2 for high tiles, could be done more properly)

        // Draw this map layer
        for (int y = startY, gy = (startY + 1) * tsize.y;
                y < endY; y++, gy += tsize.y) {
            for (int x = startX, gx = startX * tsize.x;
                    x < endX; x++, gx += tsize.x) {
                Tile tile = layer.getTileAt(x, y);
                if (tile != null) {
                    if (layer instanceof SelectionLayer) {
                    	if (shouldPaintBrushTile())
                    		RenderingUtil.drawTile(gc, tile, gx, gy, zoom);
                        Transform transform = new Transform(getDisplay());
                        transform.translate(gx,gy);
                		gc.setTransform(transform);
                		gc.setAlpha(SEL_HOVER_ALPHA);
                        gc.fillPolygon(Converter.getPolygonArray(gridPoly));
                        gc.setAlpha(OPAQUE);
                        gc.setTransform(null);
                        transform.dispose();
                        //paintEdge(g, layer, gx, gy);
                    }
                    else {
                    	RenderingUtil.drawTile(gc, tile, gx, gy, zoom);
                    }
                }
            }
        }
    }

	protected void paintObjectGroup(GC gc, ObjectGroup og) {
        final Point tsize = getTileSize();
        final Rectangle bounds = og.getBounds();
        Iterator<MapObject> itr = og.getObjects();
        Transform transform = new Transform(getDisplay());
        transform.translate(bounds.x * tsize.x,bounds.y * tsize.y);
		gc.setTransform(transform);
//        gc.translate(
//                bounds.x * tsize.x,
//                bounds.y * tsize.y);

        while (itr.hasNext()) {
            MapObject mo = itr.next();
            double ox = mo.getX() * zoom;
            double oy = mo.getY() * zoom;

            Image objectImage = mo.getImage(zoom);
            if (objectImage != null) {
                gc.drawImage(objectImage, (int) ox, (int) oy);
            }

            int objWidth = mo.getWidth();
			int objHeight = mo.getHeight();
			if (objWidth == 0 || objHeight == 0) {
                gc.setAntialias(SWT.ON);
                gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
                gc.fillOval((int) ox + 1, (int) oy + 1,
                        (int) (10 * zoom), (int) (10 * zoom));
                gc.setBackground(getDisplay().getSystemColor(OBJECT_FOREGROUND));
                gc.fillOval((int) ox, (int) oy,
                        (int) (10 * zoom), (int) (10 * zoom));
                gc.setAntialias(SWT.OFF);
            } else {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
                gc.drawRectangle((int) ox + 1, (int) oy + 1,
                    (int) (objWidth * zoom),
                    (int) (objHeight * zoom));
                gc.setForeground(getDisplay().getSystemColor(OBJECT_FOREGROUND));
                gc.drawRectangle((int) ox, (int) oy,
                    (int) (objWidth * zoom),
                    (int) (objHeight * zoom));
            }
            if (zoom > 0.0625) {
            	if (drawResizeAnchors) {
            		drawAnchors(gc, ox, oy, objWidth, objHeight);
            	}
                final String s = mo.getName() != null ? mo.getName() : "(null)";
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
                gc.drawString(s, (int) (ox - 5) + 1, (int) (oy - 5) + 1);
                gc.setForeground(getDisplay().getSystemColor(OBJECT_FOREGROUND));
                gc.drawString(s, (int) (ox - 5), (int) (oy - 5));
                
            }
        }

//        gc.translate(
//                -bounds.x * tsize.x,
//                -bounds.y * tsize.y);
        gc.setTransform(null);
        transform.dispose();
    }

	protected void drawAnchors(GC gc, double ox, double oy, int objWidth,
			int objHeight) {

		Color oldBackground = gc.getBackground();
		Rectangle[] anchorRects = AnchoringUtil.getAnchorRects((int) ox,(int) oy,objWidth,objHeight);
		for (int i = 0; i < anchorRects.length; i++) {
			Rectangle rectangle = anchorRects[i];
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.fillRectangle(rectangle.x + 1, rectangle.y + 1, rectangle.width, rectangle.height);
			if (i == AnchoringUtil.ANCHOR_LEFT || i == AnchoringUtil.ANCHOR_TOP || i == AnchoringUtil.ANCHOR_RIGHT || i == AnchoringUtil.ANCHOR_BOTTOM)
				gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
			else
				gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
			gc.fillRectangle(rectangle);
		}
		gc.setBackground(oldBackground);
	}
	
    protected void paintGrid(GC gc) {
        // Determine tile size
        Point tsize = getTileSize();
        if (tsize.x <= 0 || tsize.y <= 0) {
            return;
        }

        // Determine lines to draw from clipping rectangle
        Rectangle clipRect = gc.getClipping();
        int startX = clipRect.x / tsize.x * tsize.x;
        int startY = clipRect.y / tsize.y * tsize.y;
        int endX = clipRect.x + clipRect.width;
        int endY = clipRect.y + clipRect.height;

        gc.setLineStyle(SWT.LINE_DASH);
        for (int x = startX; x < endX; x += tsize.x) {
            gc.drawLine(x, clipRect.y, x, clipRect.y + clipRect.height - 1);
        }
        for (int y = startY; y < endY; y += tsize.y) {
            gc.drawLine(clipRect.x, y, clipRect.x + clipRect.width - 1, y);
        }
        gc.setLineStyle(SWT.LINE_SOLID);
    }

    protected void paintCoordinates(GC gc) {
        Point tsize = getTileSize();
        if (tsize.x <= 0 || tsize.y <= 0) {
            return;
        }
        gc.setAntialias(SWT.ON);

        // Determine tile size and offset
//        Font font = new Font("SansSerif", Font.PLAIN, tsize.y / 4);
//        gc.setFont(font);
//        FontRenderContext fontRenderContext = gc.getFontRenderContext();

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = gc.getClipping();
        int startX = clipRect.x / tsize.x;
        int startY = clipRect.y / tsize.y;
        int endX = (clipRect.x + clipRect.width) / tsize.x + 1;
        int endY = (clipRect.y + clipRect.height) / tsize.y + 1;

        // Draw the coordinates
        int gy = startY * tsize.y;
        for (int y = startY; y < endY; y++) {
            int gx = startX * tsize.x;
            for (int x = startX; x < endX; x++) {
                String coords = "(" + x + "," + y + ")";
//                Rectangle2D textSize =
//                        font.getStringBounds(coords, fontRenderContext);
                Point textSize = gc.stringExtent(coords);

                int fx = gx + (int) ((tsize.x - textSize.x) / 2);
                int fy = gy + (int) ((tsize.y + textSize.y) / 2);

                gc.drawString(coords, fx, fy);
                gx += tsize.x;
            }
            gy += tsize.y;
        }
    }


    protected void paintPropertyFlags(GC gc, TileLayer layer) {
        Point tsize = getTileSize();
        if (tsize.x <= 0 || tsize.y <= 0) {
            return;
        }
        gc.setAntialias(SWT.ON);
//        gc.setRenderingHint(RenderingHints.KEY_RENDERING,
//                RenderingHints.VALUE_RENDER_QUALITY);

//        gc.setComposite(AlphaComposite.SrcAtop);
//
//        //gc.setColor(new Color(0.1f, 0.1f, 0.5f, 0.5f));
//        gc.setXORMode(new Color(0.9f, 0.9f, 0.9f, 0.5f));

        // Determine tile size and offset

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = gc.getClipping();
        int startX = clipRect.x / tsize.x;
        int startY = clipRect.y / tsize.y;
        int endX = (clipRect.x + clipRect.width) / tsize.x + 1;
        int endY = (clipRect.y + clipRect.height) / tsize.y + 1;

        int y = startY * tsize.y;

        for (int j = startY; j <= endY; j++) {
            int x = startX * tsize.x;

            for (int i = startX; i <= endX; i++) {
                try {
                    Properties p = layer.getTileInstancePropertiesAt(i, j);
                    if (p != null && !p.isEmpty()) {
                        //gc.drawString( "PROP", x, y );
                        //gc.drawImage(MapView.propertyFlagImage, x + (tsize.width - 12), y, null);
                    	Transform transform = new Transform(getDisplay());
                        transform.translate(x + (tsize.x - 13), y+1);
                		gc.setTransform(transform);
                        gc.drawPolygon(Converter.getPolygonArray(propPoly));
                        gc.setTransform(null);
                        transform.dispose();
                    }
                }
                catch (Exception e) {
                    System.out.print("Exception\n");
                }

                x += tsize.x;
            }
            y += tsize.y;
        }
    }

    public void repaintRegion(Rectangle region) {
        Point tsize = getTileSize();
        if (tsize.x <= 0 || tsize.y <= 0) {
            return;
        }
        int maxExtraHeight =
                (int) (map.getTileHeightMax() * zoom - tsize.y);

        // Calculate the visible corners of the region
        int startX = region.x * tsize.x;
        int startY = region.y * tsize.y - maxExtraHeight;
        int endX = (region.x + region.width) * tsize.x;
        int endY = (region.y + region.height) * tsize.y;

        redraw(startX, startY, endX - startX, endY - startY,true);
    }
    
    public void repaintRegion(Rectangle region, Point brushSize) {
    	Point tsize = getTileSize();
        if (tsize.x <= 0 || tsize.y <= 0) {
            return;
        }
        int maxExtraHeight =
                (int) (map.getTileHeightMax() * zoom - tsize.y);

        // Calculate the visible corners of the region
        int startX = region.x * tsize.x;
        int startY = region.y * tsize.y - maxExtraHeight;
        int endX = (region.x + region.width) * tsize.x;
        int endY = (region.y + region.height) * tsize.y;
        
        if (brushSize.x > tsize.x || brushSize.y > tsize.y) {
	    	startX -= brushSize.x;
	    	endX += brushSize.x;
	    	startY -= brushSize.y;
	    	endY += brushSize.y;
        }
        redraw(startX, startY, endX - startX, endY - startY,true);
    }

    public Point screenToTileCoords(int x, int y) {
        Point tsize = getTileSize();
        return new Point(x / tsize.x, y / tsize.y);
    }

    public Point screenToPixelCoords(int x, int y) {
        return new Point(
                (int) (x / zoom), (int) (y / zoom));
    }

    protected Point getTileSize() {
        return new Point(
                (int) (map.getTileWidth() * zoom),
                (int) (map.getTileHeight() * zoom));
    }

    protected Polygon createGridPolygon(int tx, int ty, int border) {
        Point tsize = getTileSize();

        Polygon poly = new Polygon();
        poly.addPoint(tx - border, ty - border);
        poly.addPoint(tx + tsize.x + border, ty - border);
        poly.addPoint(tx + tsize.x + border, ty + tsize.y + border);
        poly.addPoint(tx - border, ty + tsize.y + border);

        return poly;
    }

    public Point tileToScreenCoords(int x, int y) {
        Point tsize = getTileSize();
        return new Point(x * tsize.x, y * tsize.y);
    }
    
    @Override
	public Point getSnappedVector(Point vector) {
		Point result = new Point(0,0);
		Point tsize = getTileSize();
		result.x = tsize.x * (vector.x / tsize.x);
		result.y = tsize.y * (vector.y / tsize.y);
		return result;
	}
	
	public int getSnappedScalarX(int scalar) {
		Point tsize = getTileSize();
		return tsize.x * (scalar / tsize.x);
	}
	
	public int getSnappedScalarY(int scalar) {
		Point tsize = getTileSize();
		return tsize.y * (scalar / tsize.y);
	}
	
}
