/*
 *  Tiled Map Editor, (c) 2004-2008
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package tiled.core;

import java.util.Properties;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import tiled.util.Converter;
import tiled.util.Util;

import com.onpositive.mapper.MapperPlugin;

/**
 * An object occupying an {@link ObjectGroup}.
 */
public class MapObject implements Cloneable
{
    private static final String NOTIFICATION_ERROR_PATH = "icons/notification_error.png";
	private Properties properties = new Properties();
    private ObjectGroup objectGroup;
    private Rectangle bounds = new Rectangle(0,0,0,0);
    private String name = "Object";
    private String type = "";
    private String imageSource = "";
    private Image image;
    private Image scaledImage;

    public MapObject(int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);
    }

    public MapObject clone() throws CloneNotSupportedException {
        MapObject clone = (MapObject) super.clone();
        clone.bounds = new Rectangle(bounds.x,bounds.y,bounds.width,bounds.height);
        clone.properties = (Properties) properties.clone();
        return clone;
    }

    /**
     * @return the object group this object is part of
     */
    public ObjectGroup getObjectGroup() {
        return objectGroup;
    }

    /**
     * Sets the object group this object is part of. Should only be called by
     * the object group.
     *
     * @param objectGroup the object group this object is part of
     */
    public void setObjectGroup(ObjectGroup objectGroup) {
        this.objectGroup = objectGroup;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String source) {
//    	File sourceFile = new File(source);
//    	String sourcePath = source;
//    	if (!sourceFile.isAbsolute()) {
//    		File baseFile = new File(objectGroup.getMap().getFilename());
//    		if (baseFile.exists()) {
//    			File file = new File(baseFile.getParentFile(),source);
//    			source = fi
//    		}
//    	}
        if (imageSource.equals(source))
            return;

        imageSource = source;

        loadImage(imageSource);

        scaledImage = null;
    }

	protected void loadImage(String source) {
		// Attempt to read the image
        if (source.length() > 0) {
        	try {
        		image = new Image(Display.getDefault(),source);
        	} catch (Exception e) {
        		image = AbstractUIPlugin.imageDescriptorFromPlugin(MapperPlugin.PLUGIN_ID,NOTIFICATION_ERROR_PATH).createImage();
        	}
        } else {
            image = null;
        }
	}

    /**
     * Returns the image to be used when drawing this object. This image is
     * scaled to the size of the object.
     *
     * @param zoom the requested zoom level of the image
     * @return the image to be used when drawing this object
     */
    public org.eclipse.swt.graphics.Image getImage(double zoom) {
        if (image == null)
            return null;

        final int zoomedWidth = (int) (getWidth() * zoom);
        final int zoomedHeight = (int) (getHeight() * zoom);
        if (scaledImage == null || scaledImage.getBounds().width != zoomedWidth
                || scaledImage.getBounds().height != zoomedHeight)
        {
            scaledImage = new Image(Display.getDefault(), image.getImageData().scaledTo(zoomedWidth, zoomedHeight));
        }

        return scaledImage;
    }

    public int getX() {
        return bounds.x;
    }

    public void setX(int x) {
        bounds.x = x;
    }
    
    public void setLocation(Point location) {
    	bounds.x = location.x;
    	bounds.y = location.y;
    }
    
    public void setLocation(int x, int y) {
    	bounds.x = x;
    	bounds.y = y;
    }

    public int getY() {
        return bounds.y;
    }

    public void setY(int y) {
        bounds.y = y;
    }

    public void translate(int dx, int dy) {
        Converter.translate(bounds, dx, dy);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWidth() {
        return bounds.width;
    }

    public void setWidth(int width) {
        bounds.width = width;
    }

    public void setHeight(int height) {
        bounds.height = height;
    }

    public int getHeight() {
        return bounds.height;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties p) {
        properties = p;
    }

    public String toString() {
        return type + " (" + getX() + "," + getY() + ")";
    }

	public void setImageSource(String basePath, String source) {
        imageSource = source;
        loadImage(Util.getAbsoluteFromRelative(basePath,source));
        scaledImage = null;
	}

	public void setBounds(int x, int y, int width, int height) {
		this.bounds = new Rectangle(x,y,width,height);
	}
}
