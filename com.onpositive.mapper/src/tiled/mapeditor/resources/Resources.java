package tiled.mapeditor.resources;

import java.util.ResourceBundle;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class Resources {
	
	// The resource bundle used by this class
    private static final ResourceBundle resourceBundle =
            ResourceBundle.getBundle(
                    Resources.class.getPackage().getName() + ".resources.gui");

    // Prevent instanciation
    private Resources() {
    }

    /**
     * Retrieves a string from the resource bundle in the default locale.
     *
     * @param key the key for the desired string
     * @return the string for the given key
     */
    public static String getString(String key) {
        return resourceBundle.getString(key);
    }
	
	public static Image getIcon(String id) {
		return new Image(Display.getDefault(),Resources.class.getResourceAsStream(id));
	}
}
