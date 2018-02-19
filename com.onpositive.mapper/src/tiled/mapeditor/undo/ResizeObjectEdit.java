package tiled.mapeditor.undo;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Rectangle;

import tiled.core.MapObject;
import tiled.mapeditor.resources.Resources;

public class ResizeObjectEdit extends AbstractOperation {

	private final MapObject mapObject;
	private final Rectangle oldBounds;
	private final Rectangle newBounds;

	public ResizeObjectEdit(MapObject mapObject, Rectangle oldBounds, Rectangle newBounds) {
		super(Resources.getString("action.object.resize.name"));
		this.mapObject = mapObject;
		this.oldBounds = oldBounds;
		this.newBounds = newBounds;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// Do nothing; Necessary changes already done from main code
		return null;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		mapObject.setBounds(newBounds);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		mapObject.setBounds(oldBounds);
		return Status.OK_STATUS;
	}

}
