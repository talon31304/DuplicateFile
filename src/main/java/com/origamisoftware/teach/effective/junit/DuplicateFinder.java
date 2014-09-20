package com.origamisoftware.teach.effective.junit;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * A simple class that models a file
 *
 * @author Rick Martel
 *
 */

    public class DuplicateFinder {
    private List <String> SourceFileList;
    private String SourceFolder;
    private FileManager Manager;


    /**
     * Creates a new  DuplicateFinder instance.
     *
     * @param manager     A file manager to use to access files.
     * @param sourcePath   The path to the root of the location to be searched for duplicates.
     * @throws java.io.IOException
     */
    public DuplicateFinder(FileManager manager, String sourcePath) throws IOException {
        Manager=manager;
        if (!Manager.isDirectory(sourcePath)) {
            throw (new IOException("Invalid Source Path"));
        }

        SourceFolder=sourcePath;
        UpdateFileList();
    }


    /**
     * Returns List of files contained in the instance's source (or search) location.
     *
     * @return
     */
  public List <String> getFileList() {
    return SourceFileList;
    }

    /**
     * Private method used recursively to build a list of file names with their associated paths
     * adds all files found to the source location file list.
     *
     */
    private  void buildCannonicalFileList(String[] fileNames) {
        for (String file : fileNames) {
            if (Manager.isDirectory(file)) {
                // check sub-folder's files using recursive call.
                buildCannonicalFileList(Manager.listFiles(file));
            } else {
                    SourceFileList.add(file);
            }
        }
    }


    /**
     * Updates list of files contained in the instance's source (or search) location.
     */

    public void UpdateFileList() {
        SourceFileList=new ArrayList<String>();
        buildCannonicalFileList(Manager.listFiles(SourceFolder));
    }

    /**
     * * @param searchFilePath The path of the file to find matches for within list of files at instance's source location.
     * Returns List of matching files contained in the instance's source (or search) location.
     * Will return a list of files that includes itself plus any duplicates found.
     * @return
     */
    public List<String> getMatchesForFile(String searchFilePath){

        String searchFileContent=Manager.getContent(searchFilePath);
        List <String> duplicates=new ArrayList<String>();
        for (String compareFilePath : SourceFileList) {

            String compareFileContent = Manager.getContent(compareFilePath);
            if (compareFileContent.equals(searchFileContent)){
                    duplicates.add(compareFilePath);
            }
        }
        return duplicates;
    }


    /**
     * Returns List of comparison message results for all files at instances source location.
     *
     * @return
     */
    public List<String> buildDuplicateFinderResults(){
        List <String> results=new ArrayList<String>();
        List <String> duplicateFileList=new ArrayList<String>();
        for (String filePath : SourceFileList) {
            if (!duplicateFileList.contains(filePath)) {
                List<String> currentFileDuplicates = getMatchesForFile(filePath);
                String message = "";
                if (currentFileDuplicates.size() > 1) {
                    //file has duplicates
                    message = "Duplicates detected: ";
                    String separator = "";
                    for (String duplicatePath : currentFileDuplicates) {
                        duplicateFileList.add(duplicatePath);
                        message = message + separator + duplicatePath;
                        if (separator == "") {
                            //adds separator after first call
                            separator = ", ";
                        }
                    }
                    message = message + " are duplicates of each other.";
                } else {
                    //no duplicates
                    message = "No duplicates found for " + filePath + ".";
                }
                results.add(message);
            }
        }
        return  results;
    }



}