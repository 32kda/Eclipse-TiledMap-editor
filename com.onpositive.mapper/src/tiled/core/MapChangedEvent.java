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

package tiled.core;

import java.util.EventObject;

/**
 * @version $Id$
 */
public class MapChangedEvent extends EventObject
{
	public static final int DEFAULT = 0;
	public static final int LAYER_CHANGE = 1;
	
	public final int type;
	
    public MapChangedEvent(Map map) {
        super(map);
        type = DEFAULT;
    }
    
    public MapChangedEvent(Map map, int type) {
        super(map);
        this.type = type;
    }

    public Map getMap() {
        return (Map) getSource();
    }
}
