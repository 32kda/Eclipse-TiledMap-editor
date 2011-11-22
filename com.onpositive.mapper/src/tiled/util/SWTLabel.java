package tiled.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SWTLabel extends Label {
	
	public SWTLabel(Composite parent, String text) {
		this(parent, SWT.NONE);
		setText(text);
	}

	public SWTLabel(Composite parent, int style) {
		super(parent, style);
	}

}
