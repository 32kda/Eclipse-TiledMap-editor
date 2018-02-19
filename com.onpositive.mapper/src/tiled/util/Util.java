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

package tiled.util;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * Various utility functions.
 */
public class Util
{
    /**
     * This function converts an <code>int</code> integer array to a
     * <code>byte</code> array. Each integer element is broken into 4 bytes and
     * stored in the byte array in litte endian byte order.
     *
     * @param integers an integer array
     * @return a byte array containing the values of the int array. The byte
     *         array is 4x the length of the integer array.
     */
    public static byte[] convertIntegersToBytes (int[] integers) {
        if (integers != null) {
            byte[] outputBytes = new byte[integers.length * 4];

            for(int i = 0, k = 0; i < integers.length; i++) {
                int integerTemp = integers[i];
                for(int j = 0; j < 4; j++, k++) {
                    outputBytes[k] = (byte)((integerTemp >> (8 * j)) & 0xFF);
                }
            }
            return outputBytes;
        } else {
            return null;
        }
    }

    /**
     * This utility function will check the specified string to see if it
     * starts with one of the OS root designations. (Ex.: '/' on Unix, 'C:' on
     * Windows)
     *
     * @param filename a filename to check for absolute or relative path
     * @return <code>true</code> if the specified filename starts with a
     *         filesystem root, <code>false</code> otherwise.
     */
    public static boolean checkRoot(String filename) {
        File[] roots = File.listRoots();

        for (File root : roots) {
            try {
                String canonicalRoot = root.getCanonicalPath().toLowerCase();
                if (filename.toLowerCase().startsWith(canonicalRoot)) {
                    return true;
                }
            } catch (IOException e) {
                // Do we care?
            }
        }

        return false;
    }
    
    /**
     * Returns the relative path from one file to the other. The function
     * expects absolute paths, relative paths will be converted to absolute
     * using the working directory.
     *
     * @param from the path of the origin file
     * @param to   the path of the destination file
     * @return     the relative path from origin to destination
     */
    public static String getRelativePath(String from, String to) {
        if(!(new File(to)).isAbsolute())
            return to;
        
        File fromFile = new File(from);
        if (fromFile.exists() && !fromFile.isDirectory())
        	fromFile = fromFile.getParentFile();
        // Make the two paths absolute and unique
        try {
            from = fromFile.getCanonicalPath();
            to = new File(to).getCanonicalPath();
        } catch (IOException e) {
        }

        File toFile = new File(to);
        Vector<String> fromParents = new Vector<String>();
        Vector<String> toParents = new Vector<String>();

        // Iterate to find both parent lists
        while (fromFile != null) {
            fromParents.add(0, fromFile.getName());
            fromFile = fromFile.getParentFile();
        }
        while (toFile != null) {
            toParents.add(0, toFile.getName());
            toFile = toFile.getParentFile();
        }

        // Iterate while parents are the same
        int shared = 0;
        int maxShared = Math.min(fromParents.size(), toParents.size());
        for (shared = 0; shared < maxShared; shared++) {
            String fromParent = fromParents.get(shared);
            String toParent = toParents.get(shared);
            if (!fromParent.equals(toParent)) {
                break;
            }
        }

        // Append .. for each remaining parent in fromParents
        StringBuffer relPathBuf = new StringBuffer();
        for (int i = shared; i < fromParents.size() - 1; i++) {
            relPathBuf.append(".." + File.separator);
        }

        // Add the remaining part in toParents
        for (int i = shared; i < toParents.size() - 1; i++) {
            relPathBuf.append(toParents.get(i) + File.separator);
        }
        relPathBuf.append(new File(to).getName());
        String relPath = relPathBuf.toString();

        // Turn around the slashes when path is relative
        try {
            String absPath = new File(relPath).getCanonicalPath();

            if (!absPath.equals(relPath)) {
                // Path is not absolute, turn slashes around
                // Assumes: \ does not occur in file names
                relPath = relPath.replace('\\', '/');
            }
        } catch (IOException e) {
        }

        return relPath;
    }
    
    /**
     * Gets absolute path from relative path
     * @param baseDir Base directory
     * @param relFilePath Relative path. If it is already absolute path, returns it
     * @return Absolute file path
     */
    public static String getAbsoluteFromRelative(String baseDir, String relFilePath) {
    	 String newPath = relFilePath;
         if (! new File(relFilePath).isAbsolute()) {
         	File dir = new File(baseDir);
         	while(relFilePath.startsWith("..") && relFilePath.length() > 2) {
         		dir = dir.getParentFile();
         		relFilePath = relFilePath.substring(3);
         	}
             newPath = dir.getAbsolutePath() + File.separator + relFilePath;
         }
		return newPath;
    }
}
