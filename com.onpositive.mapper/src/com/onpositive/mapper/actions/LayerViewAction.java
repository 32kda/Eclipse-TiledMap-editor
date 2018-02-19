/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;

import com.onpositive.mapper.editors.MapEditor;

/**
 * Provides a common abstract class for actions that modify the layer
 * configuration. It makes sure the undo/redo information is properly
 * maintained.
 *
 * todo: These actions will need to listen to changing of the current selected
 * todo: layer index as well as changes to the opened map. Action should always
 * todo: be disabled when no map is opened. More specific checks should be
 * todo: included in subclasses.
 *
 * @version $Id$
 */
public class LayerViewAction extends SelectionProviderAction
{

    protected final AbstractLayerAction layerAction;

	public LayerViewAction(final AbstractLayerAction layerAction, ISelectionProvider provider,
                                  MapEditor editor, String name, String description)
    {
        super(provider, name);
		this.layerAction = layerAction;
		layerAction.addPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(IAction.ENABLED))
					setEnabled(layerAction.isEnabled());
			}
		});
        setDescription(description);
        layerAction.setDescription(description);
        layerAction.setText(name);
        layerAction.setActiveEditor(layerAction,editor);
        if (provider instanceof ColumnViewer) {
        	layerAction.setLayerViewer((ColumnViewer)provider);
        }
        setEnabled(layerAction.calcEnabled());
    }

    public LayerViewAction(AbstractLayerAction layerAction, ISelectionProvider provider,
    		MapEditor editor,String name, String description, ImageDescriptor icon)
    {
        this(layerAction, provider, editor, name, description);
        setImageDescriptor(icon);
    }
    
    @Override
    public void run() {
    	layerAction.run();
    }
    
    @Override
    public void selectionChanged(IStructuredSelection selection) {
    	setEnabled(layerAction.calcEnabled());
    }
    
}
