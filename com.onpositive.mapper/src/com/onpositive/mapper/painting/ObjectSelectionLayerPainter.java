package com.onpositive.mapper.painting;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.onpositive.mapper.ui.UIUtil;

import tiled.core.MapLayer;
import tiled.core.ObjectGroup;

public class ObjectSelectionLayerPainter implements ISpecialLayerPainter {

	@Override
	public void paintSpecialLayer(GC gc, MapLayer layer) {
		if (layer instanceof ObjectGroup) {
			Rectangle bounds = ((ObjectGroup)layer).getPixelBounds();
			gc.setAlpha(75);
			gc.setBackground(UIUtil.SELECTION_FILL_COLOR);
			gc.fillRectangle(bounds);
			gc.setAlpha(255);
			gc.setLineWidth(3);
			gc.setLineStyle(SWT.LINE_DASH);
			gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			gc.drawRectangle(bounds.x + 1, bounds.y + 1, bounds.width, bounds.height);
			gc.setForeground(UIUtil.SELECTION_FRAME_COLOR);
			gc.drawRectangle(bounds);
			gc.setLineWidth(1);
			gc.setLineStyle(SWT.LINE_SOLID);
		}
	}

	@Override
	public boolean needRegularPaint(MapLayer layer) {
		return !(layer instanceof ObjectGroup); 
	}

}
