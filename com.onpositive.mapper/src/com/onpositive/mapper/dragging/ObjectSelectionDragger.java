package com.onpositive.mapper.dragging;

import java.util.Iterator;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.onpositive.mapper.editors.MapEditor;

import tiled.core.MapLayer;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.core.ObjectSelectionLayer;

public class ObjectSelectionDragger implements IDragger {
	
	private static int SELECT_MODE = 0;
	private static int MOVE_MODE = 1;
	
	protected Point initialDragLocation = new Point(0, 0);
	protected Point initialLayerLocation = new Point(0, 0);
	private final MapEditor editor;
	private ObjectSelectionLayer selectionLayer;
	private int currentMode = SELECT_MODE;
	
	public ObjectSelectionDragger(MapEditor editor) {
		this.editor = editor;
	}

	@Override
	public void handleMove(MouseEvent e) {
		// Do nothing
	}

	@Override
	public boolean canStartDrag(MouseEvent e) {
		if (editor.getCurrentPointerState() == MapEditor.PS_MARQUEE || 
			(editor.getCurrentPointerState() == MapEditor.PS_MOVEOBJ && clickedSelectionLayer(e)))
		{
			return editor.getCurrentLayer() instanceof ObjectGroup;
		}
		return false;
	}

	@Override
	public void handleDragStart(MouseEvent e) {
		initialDragLocation.x = e.x;
		initialDragLocation.y = e.y;
		currentMode = SELECT_MODE;
		if (clickedSelectionLayer(e)) {
			currentMode = MOVE_MODE;
			initialLayerLocation.x = selectionLayer.getPixelBounds().x;
			initialLayerLocation.y = selectionLayer.getPixelBounds().y;
			selectionLayer.reinitMove();
		} if (currentMode == SELECT_MODE) {
			if (selectionLayer != null) {
				editor.getMap().removeLayerSpecial(selectionLayer);
			}
			selectionLayer = new ObjectSelectionLayer();
			editor.getMap().addLayerSpecial(selectionLayer);
			selectionLayer.setPixelBounds(new Rectangle(e.x,e.y,1,1));
		}
	}
	
	protected boolean clickedSelectionLayer(MouseEvent e) {
		Iterator<MapLayer> layersSpecial = editor.getMap().getLayersSpecial();
		for (Iterator<MapLayer> iterator = layersSpecial; iterator.hasNext();) {
			MapLayer layer = iterator.next();
			if (layer == selectionLayer && ((ObjectGroup) layer).getPixelBounds().contains(initialDragLocation.x,initialDragLocation.y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleDrag(MouseEvent e) {
		if (currentMode == SELECT_MODE) {
			setSelectionBounds(e);
		} else {
			Point translation = new Point(e.x - initialDragLocation.x, e.y - initialDragLocation.y);
			if (editor.isSnapToGrid()) {
				translation = editor.getSnappedVector(translation);
			}
			selectionLayer.moveObjects(translation);
			Rectangle bounds = selectionLayer.getPixelBounds();
			bounds.x = initialLayerLocation.x + translation.x;
			bounds.y = initialLayerLocation.y + translation.y;
			selectionLayer.setPixelBounds(bounds);
		}
	}

	protected void setSelectionBounds(MouseEvent e) {
		int x = initialDragLocation.x;
		int y = initialDragLocation.y;
		int width = e.x - initialDragLocation.x;
		int height = e.y - initialDragLocation.y;
		if (width < 0) {
			x = Math.max(0,x+width);
			width = -width;
		}
		if (height < 0) {
			y = Math.max(0,y+height);
			height = -height;
		}
		selectionLayer.setPixelBounds(new Rectangle(x,y,width,height));
	}

	@Override
	public boolean canFinishDrag(MouseEvent e) {
		return true;
	}

	@Override
	public void handleDragFinish(MouseEvent e) {
		if (currentMode == SELECT_MODE) {
			setSelectionBounds(e);
			selectionLayer.maskedCopyFrom(editor.getCurrentLayer(), selectionLayer.getPixelBounds());
			Iterator<MapObject> objects = selectionLayer.getObjects();
			if (!objects.hasNext()) {
				MapObject object = ((ObjectGroup)editor.getCurrentLayer()).getObjectAt(e.x,e.y);
				if (object == null) {
					editor.getMap().removeLayerSpecial(selectionLayer);
					return;
				} else {
					selectionLayer.addObject(object);
					selectionLayer.setPixelBounds(object.getBounds());
					objects = selectionLayer.getObjects(); //To avoid concurrent modification
				}
				
			}
			Rectangle origBounds = selectionLayer.getPixelBounds();
			int left = origBounds.x + origBounds.width;
			int right = origBounds.x;
			int top = origBounds.y + origBounds.height;
			int bottom = origBounds.y;
			for (; objects.hasNext();) {
				MapObject object = objects.next();
				Rectangle rect = object.getBounds();
				if (rect.x < left) 
					left = rect.x;
				if (rect.y < top)
					top = rect.y;
				if (rect.x + rect.width > right)
					right = rect.x + rect.width;
				if (rect.y + rect.height > bottom)
					bottom = rect.y + rect.height;
			}
			selectionLayer.setPixelBounds(new Rectangle(left,top,right - left, bottom - top));
			editor.fireObjectSelectionChanged();
		} else if (currentMode == MOVE_MODE) {
			Point translation = new Point(e.x - initialDragLocation.x, e.y - initialDragLocation.y);
			if (editor.isSnapToGrid()) {
				translation = editor.getSnappedVector(translation);
			}
			editor.addEdit(selectionLayer.commitMove(translation));
		}
	}

}
