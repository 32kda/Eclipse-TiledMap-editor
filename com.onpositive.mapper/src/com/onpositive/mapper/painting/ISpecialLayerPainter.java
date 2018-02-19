package com.onpositive.mapper.painting;

import org.eclipse.swt.graphics.GC;

import tiled.core.MapLayer;

/**
 * Interface for special layer painters which can be registered for map views
 * @author 32kda
 *
 */
public interface ISpecialLayerPainter {
	
	public boolean needRegularPaint(MapLayer layer);

	public void paintSpecialLayer(GC gc, MapLayer layer);
	
}
