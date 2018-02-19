package tiled.util;

import org.eclipse.swt.graphics.Rectangle;

public class AnchoringUtil {
	
	public static final int RESIZE_ANCHOR_SIZE = 5;
	private static int I = 0;
	public static final int ANCHOR_LEFT = I++;
	public static final int ANCHOR_TOP = I++;
	public static final int ANCHOR_RIGHT = I++;
	public static final int ANCHOR_BOTTOM = I++;
	public static final int ANCHOR_LT = I++;
	public static final int ANCHOR_LB = I++;
	public static final int ANCHOR_RT = I++;
	public static final int ANCHOR_RB = I++;
	
	public static Rectangle[] getAnchorRects(int ox, int oy, int objWidth,
			int objHeight) {
		int ltX = ox;
		int ltY = oy;
		int rtX = (ox + objWidth);
		int rtY = oy;
		int lbX = ox;
		int lbY = (oy + objHeight);
		int rbX = (ox + objWidth);
		int rbY = (oy + objHeight);
		
		int tX = ltX + objWidth / 2;
		int tY = ltY;
		int lX = ltX;
		int lY = ltY + objHeight / 2;
		int rX = rtX;
		int rY = ltY + objHeight / 2;
		int bX = ltX + objWidth / 2;
		int bY = lbY;
		
		Rectangle[] result = new Rectangle[8];
		
		result[ANCHOR_LT] = new Rectangle((int) (ltX - RESIZE_ANCHOR_SIZE), (ltY - RESIZE_ANCHOR_SIZE), RESIZE_ANCHOR_SIZE * 2, RESIZE_ANCHOR_SIZE * 2);
		result[ANCHOR_RT] = new Rectangle((int) (rtX - RESIZE_ANCHOR_SIZE), (rtY - RESIZE_ANCHOR_SIZE), RESIZE_ANCHOR_SIZE * 2, RESIZE_ANCHOR_SIZE * 2);
		result[ANCHOR_RB] = new Rectangle((int) (rbX - RESIZE_ANCHOR_SIZE), (rbY - RESIZE_ANCHOR_SIZE), RESIZE_ANCHOR_SIZE * 2, RESIZE_ANCHOR_SIZE * 2);
		result[ANCHOR_LB] = new Rectangle((int) (lbX - RESIZE_ANCHOR_SIZE), (lbY - RESIZE_ANCHOR_SIZE), RESIZE_ANCHOR_SIZE * 2, RESIZE_ANCHOR_SIZE * 2);
		result[ANCHOR_LEFT] = new Rectangle((int) (lX - RESIZE_ANCHOR_SIZE), (lY - RESIZE_ANCHOR_SIZE), RESIZE_ANCHOR_SIZE * 2, RESIZE_ANCHOR_SIZE * 2);
		result[ANCHOR_RIGHT] = new Rectangle((int) (rX - RESIZE_ANCHOR_SIZE), (rY - RESIZE_ANCHOR_SIZE), RESIZE_ANCHOR_SIZE * 2, RESIZE_ANCHOR_SIZE * 2);
		result[ANCHOR_TOP] = new Rectangle((int) (tX - RESIZE_ANCHOR_SIZE), (tY - RESIZE_ANCHOR_SIZE), RESIZE_ANCHOR_SIZE * 2, RESIZE_ANCHOR_SIZE * 2);
		result[ANCHOR_BOTTOM] = new Rectangle((int) (bX - RESIZE_ANCHOR_SIZE), (bY - RESIZE_ANCHOR_SIZE), RESIZE_ANCHOR_SIZE * 2, RESIZE_ANCHOR_SIZE * 2);
		return result;
	}
	
	public static Rectangle[] getAnchorRects(Rectangle objRect) {
		return getAnchorRects(objRect.x, objRect.y, objRect.width, objRect.height);
	}
}
