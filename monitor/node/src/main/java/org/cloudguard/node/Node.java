package org.cloudguard.node;

import java.io.File;
import java.util.List;

/**
 * Node is an abstract local representation of some cloud storage service.
 *
 */
public abstract class Node {
    /**
     *
     * @return list of file names in root folder
     */
    public abstract List<String> getFileNames();

    /**
     * Read file
     *
     * @param fileName
     * @return File
     */
    public abstract File read(String fileName);

    /**
     * Write file
     *
     * @param fileName
     * @param file
     * @return result
     */
    public abstract boolean write(String fileName, File file);
}
