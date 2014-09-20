package com.origamisoftware.teach.effective.junit;

/**
 * A simple interface for a file manager
 *
 * @author Rick Martel
 *
 */
public interface FileManager {
    String getContent(String Path);
    boolean  isDirectory(String Path);
    String [] listFiles(String Path);
}
