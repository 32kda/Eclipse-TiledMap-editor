package tiled.io;

import java.io.File;
import java.io.IOException;


/**
 * Simple adater class to use libtiled IO with TilEd-Java based API 
 * @author 32kda
 */
public class PluggableMapWriter extends TMXMapWriter implements PluggableMapIO {

    /**
     * @see tiled.io.PluggableMapIO#getFilter()
     */
    public String getFilter() throws Exception {
        return "*.tmx,*.tsx,*.tmx.gz";
    }

    public String getPluginPackage() {
        return "Tiled internal TMX reader/writer";
    }

    public String getDescription() {
        return
            "The core Tiled TMX format writer\n" +
            "\n" +
            "Tiled Map Editor, (c) 2004-2008\n" +
            "Adam Turk\n" +
            "Bjorn Lindeijer";
    }

    public String getName() {
        return "Default Tiled XML (TMX) map writer";
    }

    public boolean accept(File pathname) {
        try {
            String path = pathname.getCanonicalPath();
            if (path.endsWith(".tmx") || path.endsWith(".tsx") || path.endsWith(".tmx.gz")) {
                return true;
            }
        } catch (IOException e) {}
        return false;
    }

    public void setLogger(PluginLogger logger) {
    }

}
