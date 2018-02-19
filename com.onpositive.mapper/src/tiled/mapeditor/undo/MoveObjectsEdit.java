package tiled.mapeditor.undo;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Point;

import tiled.core.MapObject;
import tiled.core.ObjectSelectionLayer;
import tiled.mapeditor.resources.Resources;

public class MoveObjectsEdit extends AbstractOperation {
	
    private final MapObject[] mapObjects;
    private final Point moveDist;
	private ObjectSelectionLayer objectSelectionLayer;

    public MoveObjectsEdit(MapObject[] mapObjects, Point moveDist) {
    	super(Resources.getString("action.object.move.multiple.name"));
        this.mapObjects = mapObjects;
        this.moveDist = moveDist;
    }
    
    public MoveObjectsEdit(MapObject mapObject, Point moveDist) {
    	super(Resources.getString("action.object.move.name"));
        this.mapObjects = new MapObject[]{mapObject};
        this.moveDist = moveDist;
    }
    
    public String getPresentationName() {
        return Resources.getString("action.object.move.name");
    }
    
    @Override
    public boolean canExecute() {
    	return false;
    }

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// Do nothing
		return null;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		for (MapObject mapObject : mapObjects) {
			mapObject.translate(moveDist.x, moveDist.y);
		}
		if (objectSelectionLayer != null)
			objectSelectionLayer.translatePixel(moveDist.x,moveDist.y);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		for (MapObject mapObject : mapObjects) {
			mapObject.translate(-moveDist.x, -moveDist.y);
		}
		if (objectSelectionLayer != null)
			objectSelectionLayer.translatePixel(-moveDist.x,-moveDist.y);
        return Status.OK_STATUS;
	}

	public void setSelectionLayer(ObjectSelectionLayer objectSelectionLayer) {
		this.objectSelectionLayer = objectSelectionLayer;
	}

}
