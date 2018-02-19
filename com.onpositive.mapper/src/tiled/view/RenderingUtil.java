package tiled.view;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import tiled.core.Tile;

public class RenderingUtil {
    /**
     * This drawing function handles drawing the tile image at the
     * specified zoom level. It will attempt to use a cached copy,
     * but will rescale if the requested zoom does not equal the
     * current cache zoom.
     *
     * @param gc Graphics instance to draw to
     * @param x x-coord to draw tile at
     * @param y y-coord to draw tile at
     * @param zoom Zoom level to draw the tile
     */
    public static void drawRaw(GC gc, Image image, int x, int y) {
        if (image != null) {
        	Rectangle bounds = image.getBounds();
            gc.drawImage(image, x, y - bounds.height);
        } else {
            // TODO: Allow drawing IDs when no image data exists as a
            // config option
        }
    }
    
    /**
     * This drawing function handles drawing the tile image at the
     * specified zoom level. It will attempt to use a cached copy,
     * but will rescale if the requested zoom does not equal the
     * current cache zoom.
     *
     * @param gc Graphics instance to draw to
     * @param x x-coord to draw tile at
     * @param y y-coord to draw tile at
     * @param zoom Zoom level to draw the tile
     */
    public static void drawTile(GC gc, Tile tile , int x, int y) {
    	Image image = tile.getImage();
        if (image != null) {
        	Rectangle bounds = image.getBounds();
            gc.drawImage(image, x, y - bounds.height);
        } else {
            // TODO: Allow drawing IDs when no image data exists as a
            // config option
        }
    }

	public static void drawTile(GC gc, Tile tile, int x, int y, double zoom) {
		// TODO zoom
		drawTile(gc,tile,x,y);
	}
}
