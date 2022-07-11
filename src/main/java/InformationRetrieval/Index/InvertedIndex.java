package InformationRetrieval.Index;

import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class InvertedIndex {
    private final PostingList[] index;
    private final int dictionarySize;

    public InvertedIndex(int dictionarySize){
        this.dictionarySize = dictionarySize;
        index = new PostingList[dictionarySize];
        for (int i = 0; i < dictionarySize; i++){
            index[i] = new PostingList();
        }
    }

    private void readPostingList(String fileName){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName + "-postings.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            int wordId = 0;
            while (line != null){
                String[] items = line.split(" ");
                if (wordId == Integer.parseInt(items[0])){
                    index[wordId] = new PostingList();
                    line = br.readLine();
                    String[] ids = line.split(" ");
                    int numberOfPostings = Integer.parseInt(items[1]);
                    if (ids.length == numberOfPostings){
                        for (String id : ids){
                            int docId = Integer.parseInt(id);
                            index[wordId].add(docId);
                        }
                    } else {
                        System.out.println("Mismatch in the number of postings for word " + wordId);
                    }
                } else {
                    System.out.println("Word Id's do not follow for word " + wordId);
                }
                wordId++;
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InvertedIndex(String fileName, int dictionarySize){
        this.dictionarySize = dictionarySize;
        index = new PostingList[dictionarySize];
        readPostingList(fileName);
    }

    public void save(String fileName){
        try {
            PrintWriter printWriter = new PrintWriter(fileName + "-postings.txt", "UTF-8");
            for (int i = 0; i < dictionarySize; i++){
                printWriter.write(i + " " + index[i].size() + "\n");
                printWriter.write(index[i].toString());
            }
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(int termId, int docId){
        index[termId].add(docId);
    }

    public QueryResult search(Query query, TermDictionary dictionary){
        int i, termIndex;
        PostingList result;
        PostingListComparator comparator = new PostingListComparator();
        ArrayList<PostingList> queryTerms = new ArrayList<>();
        for (i = 0; i < query.size(); i++){
            termIndex = dictionary.getWordIndex(query.getTerm(i).getName());
            if (termIndex != -1){
                queryTerms.add(index[termIndex]);
            }
        }
        queryTerms.sort(comparator);
        result = queryTerms.get(0);
        for (i = 1; i < queryTerms.size(); i++){
            result = result.intersection(queryTerms.get(i));
        }
        return result.toQueryResult();
    }

}
