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


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * Cuts tiles from a tileset image according to a regular rectangular pattern.
 * Supports a variable spacing between tiles and a margin around them.
 */
public class BasicTileCutter implements TileCutter {
	private int nextX, nextY;
	private Image image;
	private final int tileWidth;
	private final int tileHeight;
	private final int tileSpacing;
	private final int tileMargin;
	private ImageData baseData;
	private RGB transparentColor;

	public BasicTileCutter(int tileWidth, int tileHeight, int tileSpacing,
			int tileMargin) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.tileSpacing = tileSpacing;
		this.tileMargin = tileMargin;

		reset();
	}

	public String getName() {
		return "Basic";
	}

	public void setImage(Image image) {
		this.image = image;
		this.baseData = image.getImageData();
	}

	public Image getNextTile() {
		Rectangle bounds = image.getBounds();
		if (nextY + tileHeight + tileMargin <= bounds.height) {
			Image tile = new Image(Display.getDefault(), tileWidth, tileHeight);
			GC gc = new GC(tile);
			gc.drawImage(image, nextX, nextY, tileWidth, tileHeight, 0, 0,
					tileWidth, tileHeight);
			gc.dispose();
			ImageData imageData = tile.getImageData();
			if (baseData.getTransparencyType() == SWT.TRANSPARENCY_ALPHA) {
				for (int i = nextX; i < nextX + tileWidth; i++) {
					for (int j = nextY; j < nextY + tileHeight; j++) {
						imageData.setAlpha(i - nextX, j - nextY,
								baseData.getAlpha(i, j)); // TODO WTF here
					}
				}
			} else if (transparentColor != null) {
				int transpPixel = imageData.palette.getPixel(transparentColor);
				int height = imageData.height;
				int width = imageData.width;
				byte[] alphaData = new byte[height * width];
				for (int y = 0; y < height; y++) {
					byte[] alphaRow = new byte[width];
					for (int x = 0; x < width; x++) {
						if (imageData.getPixel(x, y) == transpPixel) {
							alphaRow[x] = 0;
						} else {
							alphaRow[x] = -1;
						}
					}
					System.arraycopy(alphaRow, 0, alphaData, y * width, width);
				}
				imageData.alphaData = alphaData;
			}
			tile.dispose();
			tile = new Image(Display.getDefault(), imageData);
			nextX += tileWidth + tileSpacing;

			if (nextX + tileWidth + tileMargin > bounds.width) {
				nextX = tileMargin;
				nextY += tileHeight + tileSpacing;
			}

			return tile;
		}

		return null;
	}

	public void reset() {
		nextX = tileMargin;
		nextY = tileMargin;
	}

	public Point getTileDimensions() {
		return new Point(tileWidth, tileHeight);
	}

	/**
	 * Returns the spacing between tile images.
	 * 
	 * @return the spacing between tile images.
	 */
	public int getTileSpacing() {
		return tileSpacing;
	}

	/**
	 * Returns the margin around the tile images.
	 * 
	 * @return the margin around the tile images.
	 */
	public int getTileMargin() {
		return tileMargin;
	}

	/**
	 * Returns the number of tiles per row in the tileset image.
	 * 
	 * @return the number of tiles per row in the tileset image.
	 */
	public int getTilesPerRow() {
		return (image.getBounds().width - 2 * tileMargin + tileSpacing)
				/ (tileWidth + tileSpacing);
	}

	@Override
	public void setTransparentColor(RGB transparentColor) {
		this.transparentColor = transparentColor;
	}
}
