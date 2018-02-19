package tiled.mapeditor.undo;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.onpositive.mapper.editors.MapEditor;

import tiled.core.MapLayer;
import tiled.mapeditor.resources.Resources;

/**
 * Unused for now
 * @author 32kda
 */
public class AddLayerEdit extends AbstractOperation {
	
	private final MapEditor editor;
	private final MapLayer layer;
	private final int prevSelectedIndex;

	public AddLayerEdit(MapEditor editor, MapLayer layer, int prevSelectedIndex) {
		super(Resources.getString("action.layer.add.name"));
		this.editor = editor;
		this.layer = layer;
		this.prevSelectedIndex = prevSelectedIndex;
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
		editor.getMap().addLayer(layer);
		editor.setCurrentLayer(editor.getMap().getTotalLayers() - 1);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		editor.getMap().removeLayer(layer);
		editor.setCurrentLayer(prevSelectedIndex);
		return Status.OK_STATUS;
	}

}
