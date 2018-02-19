package tiled.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import tiled.mapeditor.undo.MoveObjectsEdit;

public class ObjectSelectionLayer extends ObjectGroup implements ISelectionLayer{
	
	protected Map<MapObject,Point> initialObjLocations = new HashMap<MapObject, Point>();

	@Override
	public void maskedCopyFrom(MapLayer other, Rectangle mask) {
    	if (!(other instanceof ObjectGroup)) 
    		return;
    	for (Iterator<MapObject> iterator = ((ObjectGroup) other).getObjects(); iterator.hasNext();) {
    		MapObject object = (MapObject) iterator.next();
			Rectangle actualRect = ((ObjectGroup) other).getActualObjectRectangle(object);
			if (mask.contains(actualRect.x,actualRect.y) && mask.contains(actualRect.x + actualRect.width, actualRect.y + actualRect.height)) {
				addObject(object);
				Rectangle curBounds = object.getBounds();
				initialObjLocations.put(object, new Point(curBounds.x,curBounds.y));
			}
		}
	}
	
	public void reinitMove() {
		initialObjLocations.clear();
		for (Iterator<MapObject> iterator = getObjects(); iterator.hasNext();) {
			MapObject object = iterator.next();
			Rectangle curBounds = object.getBounds();
			initialObjLocations.put(object, new Point(curBounds.x,curBounds.y));			
		}
	}
	
	public void moveObjects(Point translation) {
		for(Iterator<MapObject> iterator = getObjects();iterator.hasNext();) {
			MapObject mapObject = iterator.next();
			Point initialPoint = initialObjLocations.get(mapObject);
			mapObject.setLocation(initialPoint.x + translation.x, initialPoint.y + translation.y);
		}
	}
	
	public AbstractOperation commitMove(Point translation) {
		MapObject[] movedObjects = new MapObject[getObjectsCount()];
		int i = 0;
		for(Iterator<MapObject> iterator = getObjects();iterator.hasNext();) {
			MapObject mapObject = iterator.next();
			Point initialPoint = initialObjLocations.get(mapObject);
			mapObject.setLocation(initialPoint.x + translation.x, initialPoint.y + translation.y);
			movedObjects[i++] = mapObject;
		}
		MoveObjectsEdit moveEdit = new MoveObjectsEdit(movedObjects,translation);
		moveEdit.setSelectionLayer(this);
		return moveEdit;
		
	}

}
