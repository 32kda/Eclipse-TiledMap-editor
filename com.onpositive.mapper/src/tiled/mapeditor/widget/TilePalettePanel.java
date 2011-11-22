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

package tiled.mapeditor.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import tiled.core.*;
import tiled.mapeditor.util.TileRegionSelectionEvent;
import tiled.mapeditor.util.TileSelectionEvent;
import tiled.mapeditor.util.TileSelectionListener;

/**
 * Displays a tileset and allows selecting a specific tile as well as
 * selecting several tiles for the creation of a stamp brush.
 */
public class TilePalettePanel extends Composite implements 
       TilesetChangeListener, PaintListener
{
    protected static final int SELECTION_ALPHA = 80;
	private static final int TILES_PER_ROW = 4;
    private TileSet tileset;
    private List<TileSelectionListener> tileSelectionListeners;
    private Vector<Tile> tilesetMap;
    private Rectangle selection;
	private static org.eclipse.swt.graphics.Color white;
	private static org.eclipse.swt.graphics.Color gray;
	private org.eclipse.swt.graphics.Color selectionColor;

    /**
     * Constructs an empty tile palette panel.
     */
    public TilePalettePanel(org.eclipse.swt.widgets.Composite parent) {
    	super(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
        tileSelectionListeners = new LinkedList<TileSelectionListener>();
        selectionColor = new org.eclipse.swt.graphics.Color(Display.getDefault(),100, 100, 255);

        MouseListener mouseInputAdapter = new MouseListener() {
        	private Point origin;
        	 
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
				Point point = getTileCoordinates(e.x, e.y);
                Rectangle select = new Rectangle(origin.x, origin.y, 0, 0);
                select = add(select, point.x , point.y);
                if (!select.equals(selection)) {
                    setSelection(select);
                    scrollTileToVisible(point);
                }
                if (selection.width > 0 || selection.height > 0)
                    fireTileRegionSelectionEvent(selection);
				
			}
			
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
				 origin = getTileCoordinates(e.x, e.y);
	                setSelection(new Rectangle(origin.x, origin.y, 0, 0));
	                scrollTileToVisible(origin);
	                Tile clickedTile = getTileAt(origin.x, origin.y);
	                if (clickedTile != null) {
	                    fireTileSelectionEvent(clickedTile);
	                }
				
			}
			
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		};
        addMouseListener(mouseInputAdapter);
        addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        addPaintListener(this);
    }

    /**
     * Adds tile selection listener. The listener will be notified when the
     * user selects a tile.
     *
     * @param listener the listener to add
     */
    public void addTileSelectionListener(TileSelectionListener listener) {
        tileSelectionListeners.add(listener);
    }

    /**
     * Removes tile selection listener.
     *
     * @param listener the listener to remove
     */
    public void removeTileSelectionListener(TileSelectionListener listener) {
        tileSelectionListeners.remove(listener);
    }

    private void fireTileSelectionEvent(Tile selectedTile) {
        TileSelectionEvent event = new TileSelectionEvent(this, selectedTile);
        for (TileSelectionListener listener : tileSelectionListeners) {
            listener.tileSelected(event);
        }
    }

    private void fireTileRegionSelectionEvent(Rectangle selection) {
        TileLayer region = createTileLayerFromRegion(selection);
        TileRegionSelectionEvent event = new TileRegionSelectionEvent(this, region);
        for (TileSelectionListener listener : tileSelectionListeners) {
            listener.tileRegionSelected(event);
        }
    }

    /**
     * Creates a tile layer from a certain region of the tile palette.
     *
     * @param rect the rectangular region from which a tile layer is created
     * @return the created tile layer
     */
    private TileLayer createTileLayerFromRegion(Rectangle rect) {
        TileLayer layer = new TileLayer(rect.width + 1, rect.height + 1);

        // Copy the tiles in the region to the tile layer
        for (int y = rect.y; y <= rect.y + rect.height; y++) {
            for (int x = rect.x; x <= rect.x + rect.width; x++) {
                layer.setTileAt(x - rect.x, y - rect.y, getTileAt(x, y));
            }
        }

        return layer;
    }

    /**
     * Change the tileset displayed by this palette panel.
     *
     * @param tileset the tileset to be displayed by this palette panel
     */
    public void setTileset(TileSet tileset) {
        // Remove any existing listener
        if (this.tileset != null) {
            this.tileset.removeTilesetChangeListener(this);
        }

        this.tileset = tileset;

        // Listen to changes in the new tileset
        if (this.tileset != null) {
            this.tileset.addTilesetChangeListener(this);
        }

        if (tileset != null) tilesetMap = tileset.generateGaplessVector();
        setSize(getPreferredSize());
        redraw();
    }
    
    @Override
    public Point computeSize(int wHint, int hHint) {
    	return getPreferredSize();
    }

    public TileSet getTileset() {
        return tileset;
    }

    public void tilesetChanged(TilesetChangedEvent event) {
        tilesetMap = tileset.generateGaplessVector();
        redraw();
    }

    public void nameChanged(TilesetChangedEvent event, String oldName, String newName) {
    }

    public void sourceChanged(TilesetChangedEvent event, String oldSource, String newSource) {
    }

    /**
     * Converts pixel coordinates to tile coordinates. The returned coordinates
     * are at least 0 and adjusted with respect to the number of tiles per row
     * and the number of rows.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return tile coordinates
     */
    private Point getTileCoordinates(int x, int y) {
        int twidth = tileset.getTileWidth() + 1;
        int theight = tileset.getTileHeight() + 1;
        int tileCount = tilesetMap.size();
        int tilesPerRow = getTilesPerRow();
        int rows = tileCount / tilesPerRow +
                (tileCount % tilesPerRow > 0 ? 1 : 0);

        int tileX = Math.max(0, Math.min(x / twidth, tilesPerRow - 1));
        int tileY = Math.max(0, Math.min(y / theight, rows - 1));

        return new Point(tileX, tileY);
    }

    /**
     * Retrieves the tile at the given tile coordinates. It assumes the tile
     * coordinates are adjusted to the number of tiles per row.
     *
     * @param x x tile coordinate
     * @param y y tile coordinate
     * @return the tile at the given tile coordinates, or <code>null</code>
     *         if the index is out of range
     */
    private Tile getTileAt(int x, int y) {
        int tilesPerRow = getTilesPerRow();
        int tileAt = y * tilesPerRow + x;

        if (tileAt >= tilesetMap.size()) {
            return null;
        } else {
            return tilesetMap.get(tileAt);
        }
    }

    /**
     * Returns the number of tiles to display per row. This gets calculated
     * dynamically unless the tileset specifies this value.
     *
     * @return the number of tiles to display per row, is at least 1
     */
    private int getTilesPerRow() {
        // todo: It should be an option to follow the tiles per row given
        // todo: by the tileset.
        if (tileset.getTilesPerRow() == 0) {
            int twidth = tileset.getTileWidth() + 1;
            return Math.max(1, (getSize().x - 1) / twidth);
        } else {
            return tileset.getTilesPerRow();
        }
    }

    private void setSelection(Rectangle rect) {
        repaintSelection();
        selection = rect;
        repaintSelection();
    }

    private void repaintSelection() {
        if (selection != null) {
            int twidth = tileset.getTileWidth() + 1;
            int theight = tileset.getTileHeight() + 1;

            redraw(selection.x * twidth, selection.y * theight,
                    (selection.width + 1) * twidth + 1,
                    (selection.height + 1) * theight + 1,true);
        }
    }

    private void scrollTileToVisible(Point tile) {
        int twidth = tileset.getTileWidth() + 1;
        int theight = tileset.getTileHeight() + 1;

//        setOrigin(tile.x * twidth, //TODO visible scrolling check
//                tile.y * theight);
        
//        scrollRectToVisible(new Rectangle(
//                tile.x * twidth,
//                tile.y * theight,
//                twidth + 1, theight + 1));
    }
    
    

    public void paint(GC gc) {
        Rectangle clip = gc.getClipping();

        paintBackground(gc);

        if (tileset != null) {
            // Draw the tiles
            int twidth = tileset.getTileWidth() + 1;
            int theight = tileset.getTileHeight() + 1;
            int tilesPerRow = getTilesPerRow();

            int startY = clip.y / theight;
            int endY = (clip.y + clip.height) / theight + 1;
            int tileAt = tilesPerRow * startY;
            int gx;
            int gy = startY * theight;

            for (int y = startY; y < endY; y++) {
                gx = 1;

                for (int x = 0;
                     x < tilesPerRow && tileAt < tilesetMap.size();
                     x++, tileAt++)
                {
                    Tile tile = tilesetMap.get(tileAt);

                    if (tile != null) {
                        tile.drawRaw(gc, gx, gy + theight, 1.0);
                    }
                    gx += twidth;
                }
                gy += theight;
            }

            // Draw the selection
            if (selection != null) {
                
				gc.setForeground(selectionColor);
                gc.drawRectangle(
                        selection.x * twidth, selection.y * theight,
                        (selection.width + 1) * twidth,
                        (selection.height + 1) * theight);
//                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(
//                        AlphaComposite.SRC_ATOP, 0.2f));
                gc.fillRectangle(
                        selection.x * twidth + 1, selection.y * theight + 1,
                        (selection.width + 1) * twidth - 1,
                        (selection.height + 1) * theight - 1);
            }
        }
    }

    /**
     * Draws checkerboard background.
     *
     * @param g the {@link Graphics} instance to draw on
     */
    private static void paintBackground(GC gc) {
        Rectangle clip = gc.getClipping();
        int side = 10;

        int startX = clip.x / side;
        int startY = clip.y / side;
        int endX = (clip.x + clip.width) / side + 1;
        int endY = (clip.y + clip.height) / side + 1;

        if (white == null)
        	white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		gc.setForeground(white);
        gc.fillRectangle(clip.x, clip.y, clip.width, clip.height);

        if (gray == null)
        	gray = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
		gc.setForeground(gray);
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if ((y + x) % 2 == 1) {
                    gc.fillRectangle(x * side, y * side, side, side);
                }
            }
        }
    }

    public Point getPreferredSize() {
        if (tileset == null) {
            return new Point(0, 0);
        }
        else {
            int twidth = tileset.getTileWidth() + 1;
            int theight = tileset.getTileHeight() + 1;
            int tileCount = tilesetMap.size();
            int tilesPerRow = getTilesPerRow();
            int rows = tileCount / tilesPerRow +
                    (tileCount % tilesPerRow > 0 ? 1 : 0);

            return new Point(tilesPerRow * twidth + 1, rows * theight + 1);
        }
    }


    // Scrollable interface

    public Point getPreferredScrollableViewportSize() {
        if (tileset != null) {
            int twidth = tileset.getTileWidth() + 1;
            return new Point(TILES_PER_ROW * twidth + 1, 200);
        } else {
            return new Point(0, 0);
        }
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        if (tileset != null) {
            return tileset.getTileWidth();
        } else {
            return 0;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        if (tileset != null) {
            return tileset.getTileWidth();
        } else {
            return 0;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        // todo: Update when this has become an option
        return tileset == null || tileset.getTilesPerRow() == 0;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    public Rectangle add(Rectangle rect, int newx, int newy) {
        if ((rect.width | rect.height) < 0) {
        	rect.x = newx;
        	rect.y = newy;
        	rect.width = rect.height = 0;
            return rect;
        }
        int x1 = rect.x;
        int y1 = rect.y;
        long x2 = rect.width;
        long y2 = rect.height;
        x2 += x1;
        y2 += y1;
        if (x1 > newx) x1 = newx;
        if (y1 > newy) y1 = newy;
        if (x2 < newx) x2 = newx;
        if (y2 < newy) y2 = newy;
        x2 -= x1;
        y2 -= y1;
        if (x2 > Integer.MAX_VALUE) x2 = Integer.MAX_VALUE;
        if (y2 > Integer.MAX_VALUE) y2 = Integer.MAX_VALUE;
      	return new Rectangle(x1, y1, (int) x2, (int) y2);
    }

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle clip = gc.getClipping();

        paintBackground(gc);

        if (tileset != null) {
            // Draw the tiles
            int twidth = tileset.getTileWidth() + 1;
            int theight = tileset.getTileHeight() + 1;
            int tilesPerRow = getTilesPerRow();

            int startY = clip.y / theight;
            int endY = (clip.y + clip.height) / theight + 1;
            int tileAt = tilesPerRow * startY;
            int gx;
            int gy = startY * theight;

            for (int y = startY; y < endY; y++) {
                gx = 1;

                for (int x = 0;
                     x < tilesPerRow && tileAt < tilesetMap.size();
                     x++, tileAt++)
                {
                    Tile tile = tilesetMap.get(tileAt);

                    if (tile != null) {
                        tile.drawRaw(gc, gx, gy + theight, 1.0);
                    }
                    gx += twidth;
                }
                gy += theight;
            }

            // Draw the selection
            if (selection != null) {
                gc.setForeground(selectionColor);
                gc.drawRectangle(
                        selection.x * twidth, selection.y * theight,
                        (selection.width + 1) * twidth,
                        (selection.height + 1) * theight);
                gc.setBackground(selectionColor);
                gc.setAlpha(SELECTION_ALPHA);
                gc.fillRectangle(
                        selection.x * twidth + 1, selection.y * theight + 1,
                        (selection.width + 1) * twidth - 1,
                        (selection.height + 1) * theight - 1);
            }
        }
		
	}
}
