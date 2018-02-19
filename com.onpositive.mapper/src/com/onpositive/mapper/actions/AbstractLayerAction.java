package com.onpositive.mapper.actions;

import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.mapeditor.undo.MapLayerStateEdit;

import com.onpositive.mapper.editors.MapEditor;

public abstract class AbstractLayerAction extends Action implements
		IEditorActionDelegate {
	
	IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(MapEditor.CURRENT_LAYER_PROP)) {
				boolean enabled = calcEnabled();
				setEnabled(enabled);
				if (action != null)
					action.setEnabled(enabled);
			}
		}
	};
	
	protected MapEditor editor;

	protected IAction action;

	protected ColumnViewer viewer;

	@Override
	public void run(IAction action) {
		run();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		setEnabled(calcEnabled());
	}
	
    public boolean calcEnabled() {
    	return editor != null && editor.getCurrentLayerIndex() >= 0;
    }

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof MapEditor) {
			if (action != this)
				this.action = action;
			if (editor != null)
				editor.removePartPropertyListener(propertyChangeListener);
			editor = (MapEditor) targetEditor;
			editor.addPartPropertyListener(propertyChangeListener);
		} else {
			editor = null;
		}
		setEnabled(calcEnabled());
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (action != null)
			action.setEnabled(enabled);
	}
	
    @Override
    public void run() {
    	 // Capture the layers before the operation is executed.
        Map map = editor.getMap();
        Vector<MapLayer> layersBefore = new Vector<MapLayer>(map.getLayerVector());
        int oldIdx = editor.getCurrentLayerIndex();
        doPerformAction();
        int newIdx = editor.getCurrentLayerIndex();
        // Capture the layers after the operation is executed and create the
        // layer state edit instance.
        Vector<MapLayer> layersAfter = new Vector<MapLayer>(map.getLayerVector());
        String text = getText();
        if (action != null )
        	text = action.getText();
        MapLayerStateEdit mapLayerStateEdit =  
                new MapLayerStateEdit(editor, layersBefore, layersAfter,
                                      text,oldIdx,newIdx);
        editor.addEdit(mapLayerStateEdit);
    }

    /**
     * Actually performs the action that modifies the layer configuration.
     */
    protected abstract void doPerformAction();

	public void setLayerViewer(ColumnViewer viewer) {
		this.viewer = viewer;
	}

}
