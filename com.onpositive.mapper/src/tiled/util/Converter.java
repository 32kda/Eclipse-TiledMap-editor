package tiled.util;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.PathIterator;

import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class Converter {
	public static Rectangle SWTRectToAWT(org.eclipse.swt.graphics.Rectangle original) {
		return new Rectangle(original.x,original.y,original.width,original.height);
	}
	
	public static org.eclipse.swt.graphics.Rectangle AWTRectToSWT(Rectangle original) {
		return new org.eclipse.swt.graphics.Rectangle(original.x,original.y,original.width,original.height);
	}
	
	/**
	 * Converts a java 2d path iterator to a SWT path.
	 * 
	 * @param iter
	 * @return
	 */
	public static Path pathIterator2Path(PathIterator iter)
	{
		float [] coords = new float[6]; 
		
		Path path = new Path(Display.getDefault());
		
		while (!iter.isDone())
		{
			int type = iter.currentSegment(coords);

			switch (type)
			{
				case	PathIterator.SEG_MOVETO:
						path.moveTo(coords[0], coords[1]);
						break;

				case	PathIterator.SEG_LINETO:
						path.lineTo(coords[0],coords[1]);
						break;

				case	PathIterator.SEG_CLOSE:
						path.close();
						break;

				case	PathIterator.SEG_QUADTO:
						path.quadTo(coords[0],coords[1],coords[2],coords[3]);
						break;

				case	PathIterator.SEG_CUBICTO:
						path.cubicTo(coords[0], coords[1],coords[2],coords[3],coords[4],coords[5]);
						break;
			}
				
			iter.next();
		}
		return path;
	}
	
	   /**
     * Adds a point, specified by the integer arguments {@code newx,newy}
     * to the bounds of this {@code Rectangle}.
     * <p>
     * If this {@code Rectangle} has any dimension less than zero,
     * the rules for <a href=#NonExistant>non-existant</a>
     * rectangles apply.
     * In that case, the new bounds of this {@code Rectangle} will
     * have a location equal to the specified coordinates and
     * width and height equal to zero.
     * <p>
     * After adding a point, a call to <code>contains</code> with the 
     * added point as an argument does not necessarily return
     * <code>true</code>. The <code>contains</code> method does not 
     * return <code>true</code> for points on the right or bottom 
     * edges of a <code>Rectangle</code>. Therefore, if the added point 
     * falls on the right or bottom edge of the enlarged 
     * <code>Rectangle</code>, <code>contains</code> returns 
     * <code>false</code> for that point.
     * If the specified point must be contained within the new
     * {@code Rectangle}, a 1x1 rectangle should be added instead:
     * <pre>
     *     r.add(newx, newy, 1, 1);
     * </pre>
     * @param newx the X coordinate of the new point
     * @param newy the Y coordinate of the new point
     */
    public static void add(org.eclipse.swt.graphics.Rectangle original, int newx, int newy) {
        if ((original.width | original.height) < 0) {
            original.x = newx;
            original.y = newy;
            original.width = original.height = 0;
            return;
        }
        int x1 = original.x;
        int y1 = original.y;
        long x2 = original.width;
        long y2 = original.height;
        x2 += x1;
        y2 += y1;
        if (x1 > newx) x1 = newx;
        if (y1 > newy) y1 = newy;
        if (x2 < newx) x2 = newx;
        if (y2 < newy) y2 = newy;
        x2 -= x1;
        y2 -= y1;
        if (x2 > Integer.MAX_VALUE) x2 = Integer.MAX_VALUE;
        if (y2 > Integer.MAX_VALUE) y2 = Integer.MAX_VALUE;
        original.x = x1;
        original.y = y1;
        original.width = (int) x2;
        original.height = (int) y2;
    }

	public static int[] getPolygonArray(Polygon grid) {
		int[] points = new int[grid.npoints * 2];
		for (int i = 0; i < grid.npoints; i++) {
			points[i*2] = grid.xpoints[i];
			points[i*2 + 1] = grid.ypoints[i];
		}
		return points;
	}
	
    /**
     * Translates this <code>Rectangle</code> the indicated distance,
     * to the right along the X coordinate axis, and 
     * downward along the Y coordinate axis.
     * @param dx the distance to move this <code>Rectangle</code> 
     *                 along the X axis
     * @param dy the distance to move this <code>Rectangle</code> 
     *                 along the Y axis
     * @see       java.awt.Rectangle#setLocation(int, int)
     * @see       java.awt.Rectangle#setLocation(java.awt.Point)
     */
    public static void translate(org.eclipse.swt.graphics.Rectangle rect, int dx, int dy) {
        int oldv = rect.x;
        int newv = oldv + dx;
        if (dx < 0) {
            // moving leftward
            if (newv > oldv) {
                // negative overflow
                // Only adjust width if it was valid (>= 0).
                if (rect.width >= 0) {
                    // The right edge is now conceptually at
                    // newv+width, but we may move newv to prevent
                    // overflow.  But we want the right edge to
                    // remain at its new location in spite of the
                    // clipping.  Think of the following adjustment
                    // conceptually the same as:
                    // width += newv; newv = MIN_VALUE; width -= newv;
                	rect.width += newv - Integer.MIN_VALUE;
                    // width may go negative if the right edge went past
                    // MIN_VALUE, but it cannot overflow since it cannot
                    // have moved more than MIN_VALUE and any non-negative
                    // number + MIN_VALUE does not overflow.
                }
                newv = Integer.MIN_VALUE;
            }
        } else {
            // moving rightward (or staying still)
            if (newv < oldv) {
                // positive overflow
                if (rect.width >= 0) {
                    // Conceptually the same as:
                    // width += newv; newv = MAX_VALUE; width -= newv;
                	rect.width += newv - Integer.MAX_VALUE;
                    // With large widths and large displacements
                    // we may overflow so we need to check it.
                    if (rect.width < 0) rect.width = Integer.MAX_VALUE;
                }
                newv = Integer.MAX_VALUE;
            }
        }
        rect.x = newv;

        oldv = rect.y;
        newv = oldv + dy;
        if (dy < 0) {
            // moving upward
            if (newv > oldv) {
                // negative overflow
                if (rect.height >= 0) {
                	rect.height += newv - Integer.MIN_VALUE;
                    // See above comment about no overflow in this case
                }
                newv = Integer.MIN_VALUE;
            }
        } else {
            // moving downward (or staying still)
            if (newv < oldv) {
                // positive overflow
                if (rect.height >= 0) {
                	rect.height += newv - Integer.MAX_VALUE;
                    if (rect.height < 0) rect.height = Integer.MAX_VALUE;
                }
                newv = Integer.MAX_VALUE;
            }
        }
        rect.y = newv;
    }
    
    public static int RGBtoInt(RGB rgb) {
    	return 65536 * rgb.red + 256 * rgb.green + rgb.blue;

    }
    
    public static RGB intToRGB(int colorInt) {
    	int blue =  colorInt & 255;
        int	green = (colorInt >> 8) & 255;
        int red =   (colorInt >> 16) & 255;
		return new RGB(red,green,blue);
    }

}
