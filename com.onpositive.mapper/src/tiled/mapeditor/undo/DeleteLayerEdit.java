package tiled.mapeditor.undo;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.onpositive.mapper.editors.MapEditor;

import tiled.core.MapLayer;

/**
 * Unused for now
 * @author 32kda
 */
public class DeleteLayerEdit extends AbstractOperation {
	
	private final MapEditor editor;
	private final MapLayer layer;
	private final int layerIndex;

	public DeleteLayerEdit(MapEditor editor, MapLayer layer, int layerIndex) {
		super("action.layer.delete.name");
		this.editor = editor;
		this.layer = layer;
		this.layerIndex = layerIndex;
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
		editor.getMap().removeLayer(layer);
		int totalLayers = editor.getMap().getTotalLayers();
		 if (layerIndex == totalLayers - 1) {
             editor.setCurrentLayer(totalLayers - 2);
         }
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		editor.getMap().addLayer(layerIndex, layer);
		editor.setCurrentLayer(layerIndex);
		return Status.OK_STATUS;
	}

}
