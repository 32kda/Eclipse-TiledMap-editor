package com.onpositive.mapper.actions;


import com.onpositive.mapper.editors.MapEditor;

public class FillPointerStateAction extends PointerStateAction {

	public FillPointerStateAction() {
		super();
	}

	@Override
	protected int getPointerState() {
		return MapEditor.PS_POUR;
	}
	
	@Override
	protected boolean shouldBeEnabled() {
		return !mapEditor.isObjectLayerActive();
	}

}
