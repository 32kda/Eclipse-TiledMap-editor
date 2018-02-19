package com.onpositive.mapper.adapters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import tiled.core.MapObject;

public class ObjectPropertySource implements IPropertySource {
	
	private final MapObject mapObject;

	public ObjectPropertySource(MapObject mapObject) {
		this.mapObject = mapObject;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Properties properties = mapObject.getProperties();
		Set<String> names = properties.stringPropertyNames();
		IPropertyDescriptor[] result = new IPropertyDescriptor[names.size()];
		int i = 0;
		for (String name:names) {
			result[i++] = new TextPropertyDescriptor(name,properties.getProperty(name));
		}
		Arrays.sort(result,new Comparator<IPropertyDescriptor>() {

			@Override
			public int compare(IPropertyDescriptor o1,
					IPropertyDescriptor o2) {
				return ((String) o1.getId()).compareTo((String) o2.getId());
			}
			
		});
		return result;
	}

	@Override
	public Object getPropertyValue(Object id) {
		Properties properties = mapObject.getProperties();
		return properties.getProperty(id.toString());
	}

	@Override
	public boolean isPropertySet(Object id) {
		Properties properties = mapObject.getProperties();
		return (properties.getProperty(id.toString()) != null);
	}

	@Override
	public void resetPropertyValue(Object id) {
		// Do nothing yet

	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		Properties properties = mapObject.getProperties();
		properties.setProperty(id.toString(),value.toString());

	}

}
