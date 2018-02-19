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

package tiled.mapeditor.widget;

import tiled.mapeditor.brush.AbstractBrush;

import javax.swing.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;

import java.awt.*;

/**
 * @version $Id$
 */
public class BrushPreview extends Composite implements PaintListener 
{
    private AbstractBrush brush;

    public BrushPreview(Composite parent) {
    	super(parent,SWT.NONE);
        //setPreferredSize(new Dimension(22, 22));
    	addPaintListener(this);
    }

    public void setBrush(AbstractBrush brush) {
        this.brush = brush;
    }

//    public void paint(Graphics graphics) {
//        if (brush != null) {
//            brush.drawPreview((Graphics2D) graphics, getSize(), null);
//        }
//    }

	@Override
	public void paintControl(PaintEvent e) {
        if (brush != null) {
            brush.drawPreview(e.gc, getSize(), null);
        }
	}
}
