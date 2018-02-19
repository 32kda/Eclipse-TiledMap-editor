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

import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.MultilayerPlane;
import tiled.core.ObjectGroup;
import tiled.core.TileLayer;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.selection.SelectionLayer;

import com.onpositive.mapper.editors.MapEditor;
import com.onpositive.mapper.painting.ISpecialLayerPainter;
import com.onpositive.mapper.painting.ObjectSelectionLayerPainter;

/**
 * The base class for map views. This is meant to be extended for different tile
 * map orientations, such as orthagonal and isometric.
 * 
 * @version $Id$
 */
public abstract class MapView extends Composite implements PaintListener {
	private static final int UNSELECTED_LAYER_ALPHA = 100;
	public static final int PF_BOUNDARYMODE = 0x02;
	public static final int PF_COORDINATES = 0x04;
	public static final int PF_NOSPECIAL = 0x08;
	
	public static int ZOOM_NORMALSIZE = 5;

	protected Map map;
	protected Brush currentBrush;
	protected int modeFlags;
	protected double zoom = 1.0;
	protected int zoomLevel = ZOOM_NORMALSIZE;

	// Grid properties
	protected boolean showGrid;
	protected boolean antialiasGrid;
	protected Color gridColor;
	protected int gridOpacity = 180;

	protected boolean highlightSelectedLayer = false;

	protected static double[] zoomLevels = { 0.0625, 0.125, 0.25, 0.5, 0.75,
			1.0, 1.5, 2.0, 3.0, 4.0 };

	private static final RGB DEFAULT_BACKGROUND_COLOR = new RGB(64, 64, 64);
	/** The default grid color (black). */
	public static final RGB DEFAULT_GRID_COLOR = new RGB(0, 0, 0);
	protected static final int SEL_HOVER_ALPHA = 50;
	protected static final int OPAQUE = 255;

	protected static Image propertyFlagImage;

	protected Color defaultBgColor;
	protected Color defaultGridColor;
	protected int pointerState;
	protected List<ISpecialLayerPainter> specialLayerPainters = new ArrayList<ISpecialLayerPainter>();
	private int currentLayer = 0;
	
	protected boolean drawResizeAnchors = true;

	/**
	 * Creates a new <code>MapView</code> that displays the specified map.
	 * 
	 * @param map
	 *            the map to be displayed by this map view
	 */
	protected MapView(Composite parent, Map map) {
		super(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		// Setup static bits on first invocation
		if (MapView.propertyFlagImage == null) {
			try {
				// MapView.propertyFlagImage = //TODO
				// Resources.getImage("propertyflag-12.png");
			} catch (Exception e) {
			}
		}

		this.map = map;
		defaultBgColor = new Color(getDisplay(), DEFAULT_BACKGROUND_COLOR);
		defaultGridColor = new Color(getDisplay(), DEFAULT_GRID_COLOR);
		addPaintListener(this);
		// setOpaque(true);
        Point preferredSize = getPreferredSize();
        setBounds(0,0,preferredSize.x,preferredSize.y);
	}

	public void toggleMode(int modeModifier) {
		modeFlags ^= modeModifier;
		redraw();
	}

	public void setMode(int modeModifier, boolean value) {
		if (value) {
			modeFlags |= modeModifier;
		} else {
			modeFlags &= ~modeModifier;
		}
		redraw();
	}

	public boolean getMode(int modeModifier) {
		return (modeFlags & modeModifier) != 0;
	}

	public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
		redraw();
	}

	public void setGridOpacity(int gridOpacity) {
		this.gridOpacity = gridOpacity;
		redraw();
	}

	public void setAntialiasGrid(boolean antialiasGrid) {
		this.antialiasGrid = antialiasGrid;
		redraw();
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
		redraw();
	}

	/**
	 * Sets a new brush. The brush can draw a preview of the change while
	 * editing.
	 * 
	 * @param brush
	 *            the new brush
	 */
	public void setBrush(Brush brush) {
		currentBrush = brush;
	}

	// Zooming

	public boolean zoomIn() {
		if (zoomLevel < zoomLevels.length - 1) {
			setZoomLevel(zoomLevel + 1);
		}

		return zoomLevel < zoomLevels.length - 1;
	}

	public boolean zoomOut() {
		if (zoomLevel > 0) {
			setZoomLevel(zoomLevel - 1);
		}

		return zoomLevel > 0;
	}

	public void setZoom(double zoom) {
		if (zoom > 0) {
			this.zoom = zoom;
			// revalidate();
			setSize(computeSize(-1, -1));
		}
	}

	public void setZoomLevel(int zoomLevel) {
		if (zoomLevel >= 0 && zoomLevel < zoomLevels.length) {
			this.zoomLevel = zoomLevel;
			setZoom(zoomLevels[zoomLevel]);
		}
	}

	public double getZoom() {
		return zoom;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	// Scrolling

	public abstract Point getPreferredSize();

	public Point getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		return getPreferredSize();
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public abstract int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction);

	public abstract int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction);

	/**
	 * Creates a MapView instance that will render the map in the right
	 * orientation.
	 * 
	 * @param p
	 *            the Map to create a view for
	 * @return a suitable instance of a MapView for the given Map
	 * @see Map#getOrientation()
	 */
	public static MapView createViewforMap(Composite parent, Map p) {
		MapView mapView = null;

		int orientation = p.getOrientation();

		if (orientation == Map.ORIENTATION_ISOMETRIC) {
			mapView = new IsoMapView(parent, p);
		} else if (orientation == Map.ORIENTATION_ORTHOGONAL) {
			mapView = new OrthoMapView(parent, p);
			mapView.addSpecialLayerPainter(new ObjectSelectionLayerPainter());
		} else if (orientation == Map.ORIENTATION_HEXAGONAL) {
			mapView = new HexMapView(parent, p);
		} else if (orientation == Map.ORIENTATION_SHIFTED) {
			mapView = new ShiftedMapView(parent, p);
		}

		return mapView;
	}

	// Painting

	/**
	 * Draws all the visible layers of the map. Takes several flags into account
	 * when drawing, and will also draw the grid, and any 'special' layers.
	 * 
	 * @param g
	 *            the GC object to paint to
	 * @see javax.swing.JComponent#paintComponent(Graphics)
	 * @see MapLayer
	 * @see SelectionLayer
	 */
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;

		MapLayer layer;
		Rectangle clip = gc.getClipping();

		gc.setLineWidth(2);

		// Do an initial fill with the background color
		// todo: make background color configurable
		// try {
		// String colorString = displayPrefs.get("backgroundColor", "");
		// g2d.setColor(Color.decode(colorString));
		// } catch (NumberFormatException e) {
		// }
		gc.setBackground(defaultBgColor);
		gc.fillRectangle(clip.x, clip.y, clip.width, clip.height);

		paintSubMap(map, gc, 1.0f);

		if (!getMode(PF_NOSPECIAL)) {
			Iterator<?> li = map.getLayersSpecial();

			while (li.hasNext()) {
				layer = (MapLayer) li.next();
				if (layer.isVisible()) {
					if (layer instanceof SelectionLayer) {
						// g2d.setComposite(AlphaComposite.getInstance(
						// AlphaComposite.SRC_ATOP, 0.3f));
						gc.setForeground(((SelectionLayer) layer)
								.getHighlightColor());
						gc.setBackground(((SelectionLayer) layer)
								.getHighlightColor());
					}
					if (layer instanceof TileLayer)
						paintLayer(gc, (TileLayer) layer);
					else if (layer instanceof ObjectGroup) {
						boolean needRegularPaint = true;
						for (ISpecialLayerPainter painter : specialLayerPainters) {
							needRegularPaint = needRegularPaint && painter.needRegularPaint(layer);
						}
						if (needRegularPaint)
							paintObjectGroup(gc, (ObjectGroup) layer);
						for (ISpecialLayerPainter painter : specialLayerPainters) {
							painter.paintSpecialLayer(gc, layer);
						}
					}
					gc.setAlpha(255);
				}
			}

			// Paint Brush
			// if (currentBrush != null) {
			// currentBrush.drawPreview(g2d, this);
			// }
		}

		// Grid color (also used for coordinates)
		if (gridColor != null)
			gc.setForeground(gridColor);
		else
			gc.setForeground(defaultGridColor);

		if (showGrid) {
			// Grid opacity
			gc.setAlpha(gridOpacity);
			// if (gridOpacity < 255) {
			// g2d.setComposite(AlphaComposite.getInstance(
			// AlphaComposite.SRC_ATOP,
			// (float) gridOpacity / 255.0f));
			// }
			// else {
			// g2d.setComposite(AlphaComposite.SrcOver);
			// }

			// Configure grid antialiasing
			// if (antialiasGrid) {
			// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			// RenderingHints.VALUE_ANTIALIAS_ON);
			// }
			// else {
			// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			// RenderingHints.VALUE_ANTIALIAS_OFF);
			// }

			gc.setLineWidth(1);
			paintGrid(gc);
		}

		if (getMode(PF_COORDINATES)) {
			// g2d.setComposite(AlphaComposite.SrcOver);
			paintCoordinates(gc);
		}

		// if (editor != null && editor.getCurrentLayer() instanceof TileLayer)
		// {
		// g2d.setComposite(AlphaComposite.SrcOver);
		//
		// TileLayer tl = (TileLayer) editor.getCurrentLayer();
		// if (tl != null && tl.isVisible()) {
		// paintPropertyFlags(g2d, tl);
		// }
		// }
	}

	public void paintSubMap(MultilayerPlane m, GC gc, float mapOpacity) {
		Iterator<?> li = m.getLayers();
		MapLayer layer;

		while (li.hasNext()) {
			layer = (MapLayer) li.next();
			if (layer != null) {
				float opacity = layer.getOpacity() * mapOpacity;
				if (layer.isVisible() && opacity > 0.0f) {
					// if (opacity < 1.0f) {
					// g2d.setComposite(AlphaComposite.getInstance(
					// AlphaComposite.SRC_ATOP, opacity));
					// }
					// else {
					// g2d.setComposite(AlphaComposite.SrcOver);
					// }
					if (highlightSelectedLayer
							&& m.getLayer(currentLayer) != layer)
						gc.setAlpha(UNSELECTED_LAYER_ALPHA); //Make unselected layers half-transparent
					if (layer instanceof TileLayer) {
						paintLayer(gc, (TileLayer) layer);
					} else if (layer instanceof ObjectGroup) {
						paintObjectGroup(gc, (ObjectGroup) layer);
					}
					gc.setAlpha(255);
				}
			}
		}
	}

	/**
	 * Draws a TileLayer. Implemented in a subclass.
	 * 
	 * @param g2d
	 *            the graphics context to draw the layer onto
	 * @param layer
	 *            the TileLayer to be drawn
	 */
	protected abstract void paintLayer(GC g2d, TileLayer layer);

	/**
	 * Draws an ObjectGroup. Implemented in a subclass.
	 * 
	 * @param g2d
	 *            the graphics context to draw the object group onto
	 * @param og
	 *            the ObjectGroup to be drawn
	 */
	protected abstract void paintObjectGroup(GC g2d, ObjectGroup og);

	protected void paintEdge(GC g2d, MapLayer layer, int x, int y) {
		/*
		 * Polygon grid = createGridPolygon(x, y, 0); PathIterator itr =
		 * grid.getPathIterator(null); double nextPoint[] = new double[6];
		 * double prevPoint[], firstPoint[];
		 * 
		 * Point p = screenToTileCoords(x, y); int tx = p.x; int ty = p.y;
		 * 
		 * itr.currentSegment(nextPoint); firstPoint = prevPoint = nextPoint;
		 * 
		 * // Top itr.next(); nextPoint = new double[6];
		 * itr.currentSegment(nextPoint); if (layer.getTileAt(tx, ty - 1) ==
		 * null) { g.drawLine( (int)prevPoint[0], (int)prevPoint[1],
		 * (int)nextPoint[0], (int)nextPoint[1]); }
		 * 
		 * // Right itr.next(); prevPoint = nextPoint; nextPoint = new
		 * double[6]; itr.currentSegment(nextPoint); if (layer.getTileAt(tx + 1,
		 * ty) == null) { g.drawLine( (int)prevPoint[0], (int)prevPoint[1],
		 * (int)nextPoint[0], (int)nextPoint[1]); }
		 * 
		 * // Left itr.next(); prevPoint = nextPoint; nextPoint = new double[6];
		 * itr.currentSegment(nextPoint); if (layer.getTileAt(tx, ty + 1) ==
		 * null) { g.drawLine( (int)prevPoint[0], (int)prevPoint[1],
		 * (int)nextPoint[0], (int)nextPoint[1]); }
		 * 
		 * // Bottom if (layer.getTileAt(tx - 1, ty) == null) { g.drawLine(
		 * (int)nextPoint[0], (int)nextPoint[1], (int)firstPoint[0],
		 * (int)firstPoint[1]); }
		 */
	}

	/**
	 * Tells this view a certain region of the map needs to be repainted.
	 * <p>
	 * Same as calling repaint() unless implemented more efficiently in a
	 * subclass.
	 * 
	 * @param region
	 *            the region that has changed in tile coordinates
	 */
	public void repaintRegion(Rectangle region) {
		redraw(region.x, region.y, region.width, region.height, true);
	}

	/**
	 * Draws the map grid.
	 * 
	 * @param g2d
	 *            the graphics context to draw the grid onto
	 */
	protected abstract void paintGrid(GC g2d);

	/**
	 * Draws the coordinates on each tile.
	 * 
	 * @param g2d
	 *            the graphics context to draw the coordinates onto
	 */
	protected abstract void paintCoordinates(GC g2d);

	protected abstract void paintPropertyFlags(GC g2d, TileLayer layer);

	/**
	 * Returns a Polygon that matches the grid around the specified <b>Map</b>.
	 * 
	 * @param tx
	 * @param ty
	 * @param border
	 * @return the created polygon
	 */
	protected abstract Polygon createGridPolygon(int tx, int ty, int border);

	// Conversion functions

	public abstract Point screenToTileCoords(int x, int y);

	/**
	 * Returns the pixel coordinates on the map based on the given screen
	 * coordinates. The map pixel coordinates may be different in more ways than
	 * the zoom level, depending on the projection the view implements.
	 * 
	 * @param x
	 *            x in screen coordinates
	 * @param y
	 *            y in screen coordinates
	 * @return the position in map pixel coordinates
	 */
	public abstract Point screenToPixelCoords(int x, int y);

	/**
	 * Returns the location on the screen of the top corner of a tile.
	 * 
	 * @param x
	 * @param y
	 * @return Point
	 */
	public abstract Point tileToScreenCoords(int x, int y);

	public void setCurrentPointerState(int pointerState) {
		this.pointerState = pointerState;
		drawResizeAnchors = pointerState == MapEditor.PS_RESIZEOBJ;
	}

	protected boolean shouldPaintBrushTile() {
		return /* MapEditor.PS_ADDOBJ == pointerState || */MapEditor.PS_PAINT == pointerState
				|| MapEditor.PS_POUR == pointerState;
	}

	public void setCurrentLayer(int currentLayer) {
		this.currentLayer = currentLayer;
		if (highlightSelectedLayer)
			redraw();
	}

	public boolean isHighlightSelectedLayer() {
		return highlightSelectedLayer;
	}

	public void setHighlightSelectedLayer(boolean highlightSelectedLayer) {
		this.highlightSelectedLayer = highlightSelectedLayer;
	}

	public void repaintRegion(Rectangle redraw, Point brushSize) {
		redraw(redraw.x,redraw.y,redraw.width,redraw.height,true);
	}
	
	public Point getSnappedVector(Point vector) {
		return vector;
	}
	
	public int getSnappedScalarX(int scalar) {
		return scalar;
	}
	
	public int getSnappedScalarY(int scalar) {
		return scalar;
	}

	public Map getMap() {
		return map;
	}

	public int getCurrentLayer() {
		return currentLayer;
	}

	public boolean isDrawResizeAnchors() {
		return drawResizeAnchors;
	}

	public void setDrawResizeAnchors(boolean drawResizeAnchors) {
		this.drawResizeAnchors = drawResizeAnchors;
	}
	
	public void addSpecialLayerPainter(ISpecialLayerPainter layerPainter) {
		specialLayerPainters.add(layerPainter);
	}
	
	public void removeSpecialLayerPainter(ISpecialLayerPainter layerPainter) {
		specialLayerPainters.remove(layerPainter);
	}
}
