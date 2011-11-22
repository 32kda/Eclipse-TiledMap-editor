package com.onpositive.mapper.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.Page;

import tiled.mapeditor.widget.TabbedTilesetsPane;

import com.onpositive.mapper.editors.MapEditor;

public class TilesetPage extends Page implements IPage {

	protected final MapEditor mapEditor;
	protected Composite composite;

	public TilesetPage(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new FillLayout());
		TabbedTilesetsPane pane = new TabbedTilesetsPane(mapEditor,composite);
		pane.setMap(mapEditor.getMap());
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
