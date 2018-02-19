package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class RemoveObjPointerStateAction extends PointerStateAction {

	@Override
	protected int getPointerState() {
		return MapEditor.PS_REMOVEOBJ;
	}

	@Override
	protected boolean shouldBeEnabled() {
		return mapEditor.isObjectLayerActive();
	}
}
