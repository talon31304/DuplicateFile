package com.origamisoftware.teach.effective.junit;

import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



/**
 * JUnit tests for the <CODE></CODE>DuplicateFinder</CODE> class
 *
 * @author Rick Martel
 */
public class DuplicateFinderTest {
    private FileManager MockedFileManager;
    private String [] SubFolderFiles,RootFolderFilesWithDuplicates,
            AllFilesWithDuplicates,AllDuplicateFiles;
    private String  Root,PathEmpty,Path123A,Path123B,Path123Sub,Path456,PathSub,
    ContentEmpty,Content123A,Content123B,Content123Sub,Content456;

    /**
     * Setting up values for tests.
     */
    @Before
    public void setup() {
        Root="C:\\test";
        PathEmpty="C:\\test\\Empty.txt";
        Path123A="C:\\test\\File123A.txt";
        Path123B="C:\\test\\File123B.txt";
        Path123Sub="C:\\test\\Sub\\File123SUB.txt";
        Path456="C:\\test\\File456.txt";
        PathSub="C:\\test\\Sub";
        ContentEmpty="t";
        Content123A="123";
        Content123B="123";
        Content123Sub="123";
        Content456="456";

        SubFolderFiles =new String[]{Path123Sub};
        RootFolderFilesWithDuplicates =new String[]{PathEmpty,Path123A,Path123B,Path456,PathSub};
        AllFilesWithDuplicates=new String[]{PathEmpty,Path123A,Path123B,Path123Sub,Path456};
        AllDuplicateFiles=new String[]{Path123A,Path123B,Path123Sub};


        MockedFileManager = mock(FileManager.class);
        when(MockedFileManager.isDirectory(Root)).thenReturn(true);
        when(MockedFileManager.isDirectory(PathSub)).thenReturn(true);
        when(MockedFileManager.isDirectory(PathEmpty)).thenReturn(false);
        when(MockedFileManager.isDirectory(Path123A)).thenReturn(false);
        when(MockedFileManager.isDirectory(Path123B)).thenReturn(false);
        when(MockedFileManager.isDirectory(Path123Sub)).thenReturn(false);
        when(MockedFileManager.isDirectory(Path456)).thenReturn(false);
        when(MockedFileManager.listFiles(PathSub)).thenReturn(SubFolderFiles);
        when(MockedFileManager.listFiles(Root)).thenReturn(RootFolderFilesWithDuplicates);
        when(MockedFileManager.getContent(Path123Sub)).thenReturn(Content123Sub);
        when(MockedFileManager.getContent(Path123A)).thenReturn(Content123A);
        when(MockedFileManager.getContent(Path123B)).thenReturn(Content123B);
        when(MockedFileManager.getContent(Path456)).thenReturn(Content456);
        when(MockedFileManager.getContent(PathEmpty)).thenReturn(ContentEmpty);
    }



    /**
     * Validating Constructor functionality
     */
    @Test
    public void testDuplicateFinderConstruction() {
        //pass folder to search should succeed
       try {
           DuplicateFinder finder = new DuplicateFinder(MockedFileManager, Root);
       }
       catch (IOException ex) {
           fail("Encountered an IO Exception");
       }
     }
    /**
     * Validating GetFileList Functionality- Is it finding all files in source folder plus subfolder?
     */
    @Test
    public void testGetFileList() {
        try {
            DuplicateFinder finder = new DuplicateFinder(MockedFileManager, Root);

        List<String> duplicates=finder.getFileList();
        List<String> testList=Arrays.asList(AllFilesWithDuplicates);
        Collections.sort(duplicates);
        Collections.sort(testList);
        assertEquals("Expected file list to match list that has some duplicate files", testList, duplicates);
        }
        catch (IOException ex) {
            fail("Encountered an IO Exception");
        }
    }

    /**
     * Validating getMatchesForFile Functionality- Is it finding all matches a files with duplicates?
     */
    @Test
    public void testGetMatchesForFileSuccess() {
        try {
            DuplicateFinder finder = new DuplicateFinder(MockedFileManager, Root);
            List<String> duplicates=finder.getMatchesForFile(Path123A);
            List<String> testList=Arrays.asList(AllDuplicateFiles);
            Collections.sort(duplicates);
            Collections.sort(testList);
            assertEquals("Expected file list to include all duplicate files and nothing more", testList, duplicates);
        }
        catch (IOException ex) {
            fail("Encountered an IO Exception");

        }
    }

    /**
     * Validating getMatchesForFile Functionality- Is it finding only
     * the search file itself for a file without duplicates?
     */
    @Test
    public void testGetMatchesForFileFail() {
        try {
            DuplicateFinder finder = new DuplicateFinder(MockedFileManager, Root);

            List<String> duplicates=finder.getMatchesForFile(Path456);
            List<String> testList=Arrays.asList(new String[]{Path456});

            assertEquals("Expected file list to be to have initial search file only", testList, duplicates);
        }
        catch (IOException ex) {
            fail("Encountered an IO Exception");
        }
    }


    /**
     * Validating BuildDuplicateFinderResults Functionality
     * - Is it correctly reporting results for non-duplicates?
     */

    @Test
    public void testBuildDuplicateFinderResultsNonDuplicates(){

        try {
            DuplicateFinder finder = new DuplicateFinder(MockedFileManager, Root);
            List<String> results=finder.buildDuplicateFinderResults();
            for (String result : results) {
                if (result.contains(Path456) || result.contains(PathEmpty)) {
                    //Correctly detected non-duplicate
                    assertTrue(result.contains("No duplicates found for"));
                }
            }
        }
        catch (IOException ex) {
            fail("Encountered an IO Exception");
        }
    }

    /**
     * Validating BuildDuplicateFinderResults Functionality
     * - Is it correctly reporting results for duplicates?
     */

    @Test
    public void testBuildDuplicateFinderResultsDuplicates(){

        try {
            DuplicateFinder finder = new DuplicateFinder(MockedFileManager, Root);
            List<String> results=finder.buildDuplicateFinderResults();
            for (String result : results) {
                if (result.contains(Path123A)|| result.contains(Path123B) || result.contains(Path123Sub))
                {
                    //Correctly detected all duplicates
                    assertTrue(result.contains("Duplicates detected:")
                            && result.contains(Path123A)
                            && result.contains(Path123B)
                            && result.contains(Path123Sub));
                }
            }
        }
        catch (IOException ex) {
            fail("Encountered an IO Exception");
        }
    }

    /**
     * Validating BuildDuplicateFinderResults Functionality
     * - Is it returning the correct number of results with no unexpected results?
     */
    @Test
    public void testBuildDuplicateFinderResultsNoExtras(){

        try {
            DuplicateFinder finder = new DuplicateFinder(MockedFileManager, Root);
            List<String> results=finder.buildDuplicateFinderResults();
            for (String result : results) {
                if (!(result.contains(Path456) || result.contains(PathEmpty)|| result.contains(Path123A)
                        || result.contains(Path123B) || result.contains(Path123Sub)))
                {
                    fail("Did not expect any other results");
                }
            }
            assertEquals("Expected three results.",results.size(),3);
        }
        catch (IOException ex) {
            fail("Encountered an IO Exception");
        }
    }



}



