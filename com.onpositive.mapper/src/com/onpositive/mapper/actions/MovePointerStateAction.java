package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class MovePointerStateAction extends PointerStateAction {

	public MovePointerStateAction() {
		super();
	}

	@Override
	protected int getPointerState() {
		return MapEditor.PS_MOVE;
	}
	
	@Override
	protected boolean shouldBeEnabled() {
		return !mapEditor.isObjectLayerActive();
	}

}
