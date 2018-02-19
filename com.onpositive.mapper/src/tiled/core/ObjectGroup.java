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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.graphics.Rectangle;

/**
 * A layer containing {@link MapObject map objects}.
 */
public class ObjectGroup extends MapLayer
{
    private LinkedList<MapObject> objects = new LinkedList<MapObject>();
    protected Rectangle pixelBounds = null;

    /**
     * Default constructor.
     */
    public ObjectGroup() {
    }

    /**
     * @param map    the map this object group is part of
     */
    public ObjectGroup(Map map) {
        super(map);
    }

    /**
     * Creates an object group that is part of the given map and has the given
     * origin.
     *
     * @param map    the map this object group is part of
     * @param origx  the x origin of this layer
     * @param origy  the y origin of this layer
     */
    public ObjectGroup(Map map, int origx, int origy) {
        super(map);
        setBounds(new Rectangle(origx, origy, 0, 0));
    }

    /**
     * Creates an object group with a given area. The size of area is
     * irrelevant, just its origin.
     *
     * @param area the area of the object group
     */
    public ObjectGroup(Rectangle area) {
        super(area);
    }

    /**
     * @see MapLayer#rotate(int)
     */
    public void rotate(int angle) {
        // TODO: Implement rotating an object group
    }

    /**
     * @see MapLayer#mirror(int)
     */
    public void mirror(int dir) {
        // TODO: Implement mirroring an object group
    }

    public void mergeOnto(MapLayer other) {
    	copyTo(other);    //Seems there's no difference for copy & merge for ObjectGroup
    }

    public void maskedMergeOnto(MapLayer other, Area mask) {
        // TODO: Figure out what object group should do with this method
    }

    public void copyFrom(MapLayer other) {
    	try {
			if (other instanceof ObjectGroup) {
				for (MapObject object : ((ObjectGroup) other).objects) {
					objects.add(object.clone());
				}
			}
		} catch (CloneNotSupportedException e) {
			// Should never happen
			e.printStackTrace();
		}
    }

    public void maskedCopyFrom(MapLayer other, Area mask) {
        // TODO: Figure out what object group should do with this method
    }

    @Override
	public void maskedCopyFrom(MapLayer other, Rectangle mask) {
		// TODO: Figure out what object group should do with this method
		
	}

	public void copyTo(MapLayer other) {
    	try {
			if (other instanceof ObjectGroup) {
				for (MapObject object : objects) {
					((ObjectGroup) other).objects.add(object.clone());
				}
			}
		} catch (CloneNotSupportedException e) {
			// Should never happen
			e.printStackTrace();
		}
        // TODO: Implement copying to another object group (same as merging)
    }

    /**
     * @see MapLayer#resize(int,int,int,int)
     */
    public void resize(int width, int height, int dx, int dy) {
        // TODO: Translate contained objects by the change of origin
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public Object clone() throws CloneNotSupportedException {
        ObjectGroup clone = (ObjectGroup) super.clone();
        clone.objects = new LinkedList<MapObject>();
        for (MapObject object : objects) {
            final MapObject objectClone = (MapObject) object.clone();
            clone.objects.add(objectClone);
            objectClone.setObjectGroup(clone);
        }
        return clone;
    }

    /**
     * @deprecated
     */
    public MapLayer createDiff(MapLayer ml) {
        return null;
    }

    public void addObject(MapObject o) {
        objects.add(o);
        o.setObjectGroup(this);
    }

    public void removeObject(MapObject o) {
        objects.remove(o);
        o.setObjectGroup(null);
    }
    
    public void removeAllObjects() {
    	for (MapObject object : objects) {
			object.setObjectGroup(this);
		}
    	objects.clear();
    }

    public Iterator<MapObject> getObjects() {
        return objects.iterator();
    }
    
    public int getObjectsCount() {
    	return objects.size();
    }

    public MapObject getObjectAt(int x, int y) {
        for (MapObject obj : objects) {
            // Attempt to get an object bordering the point that has no width
            if (obj.getWidth() == 0 && obj.getX() + bounds.x == x) {
                return obj;
            }

            // Attempt to get an object bordering the point that has no height
            if (obj.getHeight() == 0 && obj.getY() + bounds.y == y) {
                return obj;
            }

            Rectangle rect = new Rectangle(obj.getX() + bounds.x * myMap.getTileWidth(),
                    obj.getY() + bounds.y * myMap.getTileHeight(),
                    obj.getWidth(), obj.getHeight());
            if (rect.contains(x, y)) {
                return obj;
            }
        }
        return null;
    }
    
    public Rectangle getActualObjectRectangle(MapObject obj) {
    	 Rectangle rect = new Rectangle(obj.getX() + bounds.x * myMap.getTileWidth(),
                 obj.getY() + bounds.y * myMap.getTileHeight(),
                 obj.getWidth(), obj.getHeight());
    	 return rect;
    }

    // This method will work at any zoom level, provided you provide the correct zoom factor. It also adds a one pixel buffer (that doesn't change with zoom).
    public MapObject getObjectNear(int x, int y, double zoom) {
        Rectangle2D mouse = new Rectangle2D.Double(x - zoom - 1, y - zoom - 1, 2 * zoom + 1, 2 * zoom + 1);
        Shape shape;

        for (MapObject obj : objects) {
            if (obj.getWidth() == 0 && obj.getHeight() == 0) {
                shape = new Ellipse2D.Double(obj.getX() * zoom, obj.getY() * zoom, 10 * zoom, 10 * zoom);
            } else {
                shape = new Rectangle2D.Double(obj.getX() + bounds.x * myMap.getTileWidth(),
                        obj.getY() + bounds.y * myMap.getTileHeight(),
                        obj.getWidth() > 0 ? obj.getWidth() : zoom,
                        obj.getHeight() > 0 ? obj.getHeight() : zoom);
            }

            if (shape.intersects(mouse)) {
                return obj;
            }
        }

        return null;
    }

    /**
     * Returns {@link ObjectGroup} bounds in pixels (Pixel bounds are needed by some methods, e.g. selection API)
     * @return {@link ObjectGroup} bounds in pixels
     */
	public Rectangle getPixelBounds() {
		if (pixelBounds != null)
			return pixelBounds;
		else if (getMap() != null){
			int tileWidth = getMap().getTileWidth();
			int tileHeight = getMap().getTileHeight();
			return new Rectangle(bounds.x * tileWidth, bounds.y * tileHeight, bounds.width * tileWidth, bounds.height * tileHeight);
		}
		return null;
	}

	/**
	 * Sets {@link ObjectGroup} bounds in pixels
	 * @param pixelBounds {@link ObjectGroup} bounds in pixels. 
	 */
	public void setPixelBounds(Rectangle pixelBounds) {
		this.pixelBounds = pixelBounds;
	}

	/**
	 * Sets {@link ObjectGroup} bounds in pixels
	 * @param x {@link ObjectGroup} bounds in pixels.
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setPixelBounds(int x, int y, int width, int height) {
		this.pixelBounds = new Rectangle(x,y,width,height);;
	}
	
	/**
	 * Translates object group in pixels
	 * @param dx x translation, pixels
	 * @param dy y translation, pixels
	 */
	public void translatePixel(int dx, int dy) {
		this.pixelBounds.x += dx;
		this.pixelBounds.y += dy;
	}
}
