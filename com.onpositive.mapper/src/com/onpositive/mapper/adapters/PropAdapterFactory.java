package com.onpositive.mapper.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import tiled.core.MapObject;

public class PropAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (!IPropertySource.class.isAssignableFrom(adapterType))
			return null;
		if (adaptableObject instanceof MapObject) {
			return new ObjectPropertySource((MapObject) adaptableObject);
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		// TODO Auto-generated method stub
		return null;
	}

}
