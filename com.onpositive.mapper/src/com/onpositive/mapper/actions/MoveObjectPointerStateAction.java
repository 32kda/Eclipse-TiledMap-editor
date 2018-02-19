package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class MoveObjectPointerStateAction extends PointerStateAction {

	@Override
	protected int getPointerState() {
		return MapEditor.PS_MOVEOBJ;
	}
	
	@Override
	protected boolean shouldBeEnabled() {
		return mapEditor.isObjectLayerActive();
	}

}
