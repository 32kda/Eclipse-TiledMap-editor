package com.onpositive.mapper.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.IPage;

import tiled.core.Map;

import com.onpositive.mapper.views.TilesetPage;
import com.onpositive.mapper.views.TilesetView;

public abstract class TilesetAction extends Action  implements IViewActionDelegate{

	protected TilesetView tilesetView;

	public TilesetAction() {
		super();
	}

	public TilesetAction(String text) {
		super(text);
	}

	public TilesetAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	public TilesetAction(String text, int style) {
		super(text, style);
	}

	@Override
	public void init(IViewPart view) {
		if (view instanceof TilesetView) {
			tilesetView = (TilesetView) view;
		}
	}
	
	@Override
	public void run(IAction action) {
		IPage currentPage = tilesetView.getCurrentPage();
		if (currentPage instanceof TilesetPage) {
			runForMap(action,((TilesetPage) currentPage).getMapEditor().getMap());
		}
	}

	protected abstract void runForMap(IAction action, Map map);

}