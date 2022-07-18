package InformationRetrieval.Index;

import Dictionary.WordComparator;
import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

public class InvertedIndex {
    private final TreeMap<Integer, PostingList> index;

    public InvertedIndex(){
        index = new TreeMap<>();
    }
    public InvertedIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, int size, WordComparator comparator){
        this();
        int i, termId, prevDocId;
        TermOccurrence term, previousTerm;
        if (terms.size() > 0){
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            termId = dictionary.getWordIndex(term.getTerm().getName());
            add(termId, term.getDocID());
            prevDocId = term.getDocID();
            while (i < terms.size()){
                term = terms.get(i);
                termId = dictionary.getWordIndex(term.getTerm().getName());
                if (termId != -1){
                    if (term.isDifferent(previousTerm, comparator)){
                        add(termId, term.getDocID());
                        prevDocId = term.getDocID();
                    } else {
                        if (prevDocId != term.getDocID()){
                            add(termId, term.getDocID());
                            prevDocId = term.getDocID();
                        }
                    }
                } else {
                    System.out.println("Error: Term " + term.getTerm().getName() + " does not exist");
                }
                i++;
                previousTerm = term;
            }
        }
    }

    private void readPostingList(String fileName){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName + "-postings.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                String[] items = line.split(" ");
                int wordId = Integer.parseInt(items[0]);
                line = br.readLine();
                index.put(wordId, new PostingList(line));
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InvertedIndex(String fileName){
        index = new TreeMap<>();
        readPostingList(fileName);
    }

    public void save(String fileName){
        try {
            PrintWriter printWriter = new PrintWriter(fileName + "-postings.txt", "UTF-8");
            for (Integer key : index.keySet()){
                index.get(key).writeToFile(printWriter, key);
            }
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(int termId, int docId){
        PostingList postingList;
        if (!index.containsKey(termId)){
            postingList = new PostingList();
        } else {
            postingList = index.get(termId);
        }
        postingList.add(docId);
        index.put(termId, postingList);
    }

    public QueryResult search(Query query, TermDictionary dictionary){
        int i, termIndex;
        PostingList result;
        PostingListComparator comparator = new PostingListComparator();
        ArrayList<PostingList> queryTerms = new ArrayList<>();
        for (i = 0; i < query.size(); i++){
            termIndex = dictionary.getWordIndex(query.getTerm(i).getName());
            if (termIndex != -1){
                queryTerms.add(index.get(termIndex));
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
