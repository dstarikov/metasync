package org.cloudguard.node;

import java.io.File;
import java.util.List;

public class DiskNode extends Node {
    // TODO implement this

    /**
     *
     * @return list of file names in root folder
     */
    public List<String> getFileNames() {
        return null;
    }

    /**
     * Read file
     *
     * @param fileName
     * @return File
     */
    public File read(String fileName) {
        return null;
    }

    /**
     * Write file
     *
     * @param fileName
     * @param file
     * @return result
     */
    public boolean write(String fileName, File file) {
        return false;
    }
}
