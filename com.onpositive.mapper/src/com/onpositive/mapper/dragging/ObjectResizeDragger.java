package com.onpositive.mapper.dragging;

import static tiled.util.AnchoringUtil.ANCHOR_BOTTOM;
import static tiled.util.AnchoringUtil.ANCHOR_LB;
import static tiled.util.AnchoringUtil.ANCHOR_LEFT;
import static tiled.util.AnchoringUtil.ANCHOR_LT;
import static tiled.util.AnchoringUtil.ANCHOR_RB;
import static tiled.util.AnchoringUtil.ANCHOR_RIGHT;
import static tiled.util.AnchoringUtil.ANCHOR_RT;
import static tiled.util.AnchoringUtil.ANCHOR_TOP;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.mapeditor.undo.ResizeObjectEdit;
import tiled.util.AnchoringUtil;
import tiled.view.MapView;

import com.onpositive.mapper.editors.MapEditor;

public class ObjectResizeDragger implements IDragger {

	protected final MapView mapView;
	protected Map map;
	protected final MapEditor mapEditor;
	protected Point initialDragLocation = new Point(0, 0);
	protected int dragType = 0;
	protected Rectangle initialBounds;
	protected MapObject targetObject;

	public ObjectResizeDragger(MapEditor mapEditor, MapView mapView) {
		this.mapEditor = mapEditor;
		this.mapView = mapView;
		map = mapView.getMap();
	}

	@Override
	public void handleMove(MouseEvent e) {
		if (mapEditor.getCurrentPointerState() != MapEditor.PS_RESIZEOBJ)
			return;
		MapLayer layer = map.getLayer(mapView.getCurrentLayer());
		mapView.setCursor(Display.getDefault()
				.getSystemCursor(SWT.CURSOR_ARROW));
		if (layer instanceof ObjectGroup) {
			MapObject targetObject = ((ObjectGroup) layer)
					.getObjectAt(e.x, e.y);
			if (targetObject != null) {
				Rectangle bounds = ((ObjectGroup) layer)
						.getActualObjectRectangle(targetObject);
				Rectangle[] anchorRects = AnchoringUtil.getAnchorRects(bounds);
				for (int i = 0; i < anchorRects.length; i++) {
					if (anchorRects[i].contains(e.x, e.y)) {
						if (i == AnchoringUtil.ANCHOR_TOP
								|| i == AnchoringUtil.ANCHOR_BOTTOM) {
							mapView.setCursor(Display.getDefault()
									.getSystemCursor(SWT.CURSOR_SIZEN));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LEFT
								|| i == AnchoringUtil.ANCHOR_RIGHT) {
							mapView.setCursor(Display.getDefault()
									.getSystemCursor(SWT.CURSOR_SIZEWE));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LT
								|| i == AnchoringUtil.ANCHOR_RB) {
							mapView.setCursor(Display.getDefault()
									.getSystemCursor(SWT.CURSOR_SIZENWSE));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LB
								|| i == AnchoringUtil.ANCHOR_RT) {
							mapView.setCursor(Display.getDefault()
									.getSystemCursor(SWT.CURSOR_SIZENESW));
							return;
						}

					}
				}
				if (e.x >= bounds.x && e.x <= bounds.x + 5 && e.y >= bounds.y
						&& e.y <= bounds.y + 5) {
					mapView.setCursor(Display.getDefault().getSystemCursor(
							SWT.CURSOR_SIZENWSE));
				}
			}
		}
	}

	@Override
	public boolean canStartDrag(MouseEvent e) {
		if (mapEditor.getCurrentPointerState() != MapEditor.PS_RESIZEOBJ)
			return false;
		MapLayer layer = map.getLayer(mapView.getCurrentLayer());
		if (layer instanceof ObjectGroup) {
			MapObject targetObject = ((ObjectGroup) layer)
					.getObjectAt(e.x, e.y);
			if (targetObject != null) {
				Rectangle bounds = ((ObjectGroup) layer)
						.getActualObjectRectangle(targetObject);
				Rectangle[] anchorRects = AnchoringUtil.getAnchorRects(bounds);
				for (Rectangle rectangle : anchorRects) {
					if (rectangle.contains(e.x, e.y))
						return true;
				}
			}
		}
		return false;

	}

	@Override
	public void handleDragStart(MouseEvent e) {
		MapLayer layer = map.getLayer(mapView.getCurrentLayer());
		targetObject = ((ObjectGroup) layer).getObjectAt(e.x, e.y);
		if (targetObject != null) {
			Rectangle bounds = ((ObjectGroup) layer)
					.getActualObjectRectangle(targetObject);
			Rectangle targetBounds = targetObject.getBounds();
			initialBounds = new Rectangle(targetBounds.x,targetBounds.y,targetBounds.width,targetBounds.height);
			Rectangle[] anchorRects = AnchoringUtil.getAnchorRects(bounds);
			for (int i = 0; i < anchorRects.length; i++) {
				if (anchorRects[i].contains(e.x, e.y)) {
					initialDragLocation.x = e.x;
					initialDragLocation.y = e.y;
					dragType = i;
				}
			}
		}
	}

	@Override
	public void handleDrag(MouseEvent e) {
		Rectangle newBounds = getNewBounds(e);
		targetObject.setBounds(newBounds);
	}

	protected Rectangle getNewBounds(MouseEvent e) {
		Point delta = new Point(e.x - initialDragLocation.x, e.y - initialDragLocation.y);
		int x = initialBounds.x;
		int y = initialBounds.y;
		int width = initialBounds.width;
		int height = initialBounds.height;
		if (dragType == ANCHOR_LEFT || dragType == ANCHOR_LT || dragType == ANCHOR_LB) {
			delta.x = Math.min(delta.x,width);
			x += delta.x;
			width -= delta.x;
		}
		if (dragType == ANCHOR_TOP || dragType == ANCHOR_LT || dragType == ANCHOR_RT) {
			delta.y = Math.min(delta.y,width);
			y += delta.y;
			height -= delta.y;
		}
		if (dragType == ANCHOR_RIGHT || dragType == ANCHOR_RT || dragType == ANCHOR_RB) {
			if (width + delta.x < 0)
				delta.x = -width;
			width += delta.x;
		}
		if (dragType == ANCHOR_BOTTOM || dragType == ANCHOR_LB || dragType == ANCHOR_RB) {
			if (height + delta.y < 0)
				delta.y = -height;
			height += delta.y;
		}
		Rectangle newBounds = new Rectangle(x,y,width,height);
		if (mapEditor.isSnapToGrid()) {
			if (dragType == ANCHOR_LEFT || dragType == ANCHOR_LT || dragType == ANCHOR_LB) {
				newBounds.x = mapView.getSnappedScalarX(newBounds.x);
				newBounds.width = initialBounds.x + initialBounds.width - newBounds.x;
			} else if (dragType == ANCHOR_RIGHT || dragType == ANCHOR_RT || dragType == ANCHOR_RB) {
				newBounds.width = mapView.getSnappedScalarX(newBounds.width);
			}
			
			if (dragType == ANCHOR_TOP || dragType == ANCHOR_LT || dragType == ANCHOR_RT) {
				newBounds.y = mapView.getSnappedScalarY(newBounds.y);
				newBounds.height = initialBounds.y + initialBounds.height - newBounds.y;
			} else if (dragType == ANCHOR_BOTTOM || dragType == ANCHOR_LB || dragType == ANCHOR_RB) {
				newBounds.height = mapView.getSnappedScalarX(newBounds.height);
			}
		}
		return newBounds;
	}

	@Override
	public boolean canFinishDrag(MouseEvent e) {
		return true;
	}

	@Override
	public void handleDragFinish(MouseEvent e) {
		Rectangle newBounds = getNewBounds(e);
		if (!newBounds.equals(initialBounds)) {
			mapEditor.addEdit(new ResizeObjectEdit(targetObject, initialBounds, newBounds));
			targetObject.setBounds(newBounds);
		}
	}

}
