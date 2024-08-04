package InformationRetrieval.Document;

import InformationRetrieval.Index.PositionalPostingList;
import InformationRetrieval.Index.PostingList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DiskCollection extends AbstractCollection{

    public DiskCollection(String directory, Parameter parameter) {
        super(directory, parameter);
    }

    /**
     * In single pass in memory indexing, the index files are merged to get the final index file. This method
     * checks if all parallel index files are combined or not.
     * @param currentIdList Current pointers for the terms in parallel index files. currentIdList[0] is the current term
     *                     in the first index file to be combined, currentIdList[2] is the current term in the second
     *                     index file to be combined etc.
     * @return True, if all merge operation is completed, false otherwise.
     */
    private boolean notCombinedAllIndexes(int[] currentIdList){
        for (int id : currentIdList){
            if (id != -1){
                return true;
            }
        }
        return false;
    }

    /**
     * In single pass in memory indexing, the index files are merged to get the final index file. This method
     * identifies the indexes whose terms to be merged have the smallest term id. They will be selected and
     * combined in the next phase.
     * @param currentIdList Current pointers for the terms in parallel index files. currentIdList[0] is the current term
     *                     in the first index file to be combined, currentIdList[2] is the current term in the second
     *                     index file to be combined etc.
     * @return An array list of indexes for the index files, whose terms to be merged have the smallest term id.
     */
    private ArrayList<Integer> selectIndexesWithSmallestTermIds(int[] currentIdList){
        ArrayList<Integer> result = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        for (int id : currentIdList){
            if (id != -1 && id < min){
                min = id;
            }
        }
        for (int i = 0; i < currentIdList.length; i++){
            if (currentIdList[i] == min){
                result.add(i);
            }
        }
        return result;
    }

    /**
     * In single pass in memory indexing, the index files are merged to get the final index file. This method
     * implements the merging algorithm. Reads the index files in parallel and at each iteration merges the posting
     * lists of the smallest term and put it to the merged index file. Updates the pointers of the indexes accordingly.
     * @param name Name of the collection.
     * @param tmpName Temporary name of the index files.
     * @param blockCount Number of index files to be merged.
     */
    protected void combineMultipleInvertedIndexesInDisk(String name, String tmpName, int blockCount){
        BufferedReader[] files;
        int[] currentIdList;
        PostingList[] currentPostingLists;
        currentIdList = new int[blockCount];
        currentPostingLists = new PostingList[blockCount];
        files = new BufferedReader[blockCount];
        try{
            PrintWriter printWriter = new PrintWriter(name + "-postings.txt", "UTF-8");
            for (int i = 0; i < blockCount; i++){
                files[i] = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("tmp-" + tmpName + i + "-postings.txt")), StandardCharsets.UTF_8));
                String line = files[i].readLine();
                String[] items = line.split(" ");
                currentIdList[i] = Integer.parseInt(items[0]);
                line = files[i].readLine();
                currentPostingLists[i] = new PostingList(line);
            }
            while (notCombinedAllIndexes(currentIdList)){
                ArrayList<Integer> indexesToCombine = selectIndexesWithSmallestTermIds(currentIdList);
                PostingList mergedPostingList = currentPostingLists[indexesToCombine.get(0)];
                for (int i = 1; i < indexesToCombine.size(); i++){
                    mergedPostingList = mergedPostingList.merge(currentPostingLists[indexesToCombine.get(i)]);
                }
                mergedPostingList.writeToFile(printWriter, currentIdList[indexesToCombine.get(0)]);
                for (int i : indexesToCombine) {
                    String line = files[i].readLine();
                    if (line != null) {
                        String[] items = line.split(" ");
                        currentIdList[i] = Integer.parseInt(items[0]);
                        line = files[i].readLine();
                        currentPostingLists[i] = new PostingList(line);
                    } else {
                        currentIdList[i] = -1;
                    }
                }
            }
            for (int i = 0; i < blockCount; i++){
                files[i].close();
            }
            printWriter.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * In single pass in memory indexing, the index files are merged to get the final index file. This method
     * implements the merging algorithm. Reads the index files in parallel and at each iteration merges the positional
     * posting lists of the smallest term and put it to the merged index file. Updates the pointers of the indexes accordingly.
     * @param name Name of the collection.
     * @param blockCount Number of index files to be merged.
     */
    protected void combineMultiplePositionalIndexesInDisk(String name, int blockCount){
        BufferedReader[] files;
        int[] currentIdList;
        PositionalPostingList[] currentPostingLists;
        currentIdList = new int[blockCount];
        currentPostingLists = new PositionalPostingList[blockCount];
        files = new BufferedReader[blockCount];
        try{
            PrintWriter printWriter = new PrintWriter(name + "-positionalPostings.txt", "UTF-8");
            for (int i = 0; i < blockCount; i++){
                files[i] = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("tmp-" + i + "-positionalPostings.txt")), StandardCharsets.UTF_8));
                String line = files[i].readLine();
                String[] items = line.split(" ");
                currentIdList[i] = Integer.parseInt(items[0]);
                currentPostingLists[i] = new PositionalPostingList(files[i], Integer.parseInt(items[1]));
            }
            while (notCombinedAllIndexes(currentIdList)){
                ArrayList<Integer> indexesToCombine = selectIndexesWithSmallestTermIds(currentIdList);
                PositionalPostingList mergedPostingList = currentPostingLists[indexesToCombine.get(0)];
                for (int i = 1; i < indexesToCombine.size(); i++){
                    mergedPostingList = mergedPostingList.merge(currentPostingLists[indexesToCombine.get(i)]);
                }
                mergedPostingList.writeToFile(printWriter, currentIdList[indexesToCombine.get(0)]);
                for (int i : indexesToCombine) {
                    String line = files[i].readLine();
                    if (line != null) {
                        String[] items = line.split(" ");
                        currentIdList[i] = Integer.parseInt(items[0]);
                        currentPostingLists[i] = new PositionalPostingList(files[i], Integer.parseInt(items[1]));
                    } else {
                        currentIdList[i] = -1;
                    }
                }
            }
            for (int i = 0; i < blockCount; i++){
                files[i].close();
            }
            printWriter.close();
        } catch (IOException ignored) {
        }
    }

}
