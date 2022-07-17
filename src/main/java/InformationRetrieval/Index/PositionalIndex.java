package InformationRetrieval.Index;

import Dictionary.WordComparator;
import InformationRetrieval.Document.Document;
import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;
import InformationRetrieval.Query.VectorSpaceModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PositionalIndex {

    private final LinkedHashMap<Integer, PositionalPostingList> positionalIndex;

    public PositionalIndex(){
        positionalIndex = new LinkedHashMap<>();
    }

    public PositionalIndex(String fileName){
        positionalIndex = new LinkedHashMap<>();
        readPositionalPostingList(fileName);
    }

    public PositionalIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, int size, WordComparator comparator){
        this();
        int i, termId, prevDocId;
        TermOccurrence term, previousTerm;
        if (terms.size() > 0){
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            termId = dictionary.getWordIndex(term.getTerm().getName());
            addPosition(termId, term.getDocID(), term.getPosition());
            prevDocId = term.getDocID();
            while (i < terms.size()){
                term = terms.get(i);
                termId = dictionary.getWordIndex(term.getTerm().getName());
                if (termId != -1){
                    if (term.isDifferent(previousTerm, comparator)){
                        addPosition(termId, term.getDocID(), term.getPosition());
                        prevDocId = term.getDocID();
                    } else {
                        if (prevDocId != term.getDocID()){
                            addPosition(termId, term.getDocID(), term.getPosition());
                            prevDocId = term.getDocID();
                        } else {
                            addPosition(termId, term.getDocID(), term.getPosition());
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

    private void readPositionalPostingList(String fileName){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName + "-positionalPostings.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                String[] items = line.split(" ");
                int wordId = Integer.parseInt(items[0]);
                positionalIndex.put(wordId, new PositionalPostingList(br, Integer.parseInt(items[1])));
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String fileName){
        try {
            PrintWriter printWriter = new PrintWriter(fileName + "-positionalPostings.txt", "UTF-8");
            for (Integer key : positionalIndex.keySet()){
                positionalIndex.get(key).writeToFile(printWriter, key);
            }
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPosition(int termId, int docId, int position){
        PositionalPostingList positionalPostingList;
        if (!positionalIndex.containsKey(termId)){
            positionalPostingList = new PositionalPostingList();
        } else {
            positionalPostingList = positionalIndex.get(termId);
        }
        positionalPostingList.add(docId, position);
        positionalIndex.put(termId, positionalPostingList);
    }

    public QueryResult positionalSearch(Query query, TermDictionary dictionary){
        int i, term;
        PositionalPostingList postingResult = null;
        for (i = 0; i < query.size(); i++){
            term = dictionary.getWordIndex(query.getTerm(i).getName());
            if (term != -1){
                if (i == 0){
                    postingResult = positionalIndex.get(term);
                } else {
                    if (postingResult != null){
                        postingResult = postingResult.intersection(positionalIndex.get(term));
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        }
        if (postingResult != null)
            return postingResult.toQueryResult();
        else
            return null;
    }

    public int[] getTermFrequencies(int docId){
        int[] tf;
        int index;
        PositionalPostingList positionalPostingList;
        tf = new int[positionalIndex.size()];
        int i = 0;
        for (Integer key : positionalIndex.keySet()){
            positionalPostingList = positionalIndex.get(key);
            index = positionalPostingList.getIndex(docId);
            if (index != -1){
                tf[i] = positionalPostingList.get(index).size();
            } else {
                tf[i] = 0;
            }
            i++;
        }
        return tf;
    }

    public int[] getDocumentFrequencies(){
        int[] df;
        df = new int[positionalIndex.size()];
        int i = 0;
        for (Integer key : positionalIndex.keySet()){
            df[i] = positionalIndex.get(key).size();
            i++;
        }
        return df;
    }

    public QueryResult rankedSearch(Query query, TermDictionary dictionary, ArrayList<Document> documents, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        int i, j, term, docID, N = documents.size(), tf, df;
        QueryResult result = new QueryResult();
        double[] scores = new double[N];
        PositionalPostingList positionalPostingList;
        for (i = 0; i < query.size(); i++){
            term = dictionary.getWordIndex(query.getTerm(i).getName());
            if (term != -1){
                positionalPostingList = positionalIndex.get(term);
                for (j = 0; j < positionalPostingList.size(); j++){
                    PositionalPosting positionalPosting = positionalPostingList.get(j);
                    docID = positionalPosting.getDocId();
                    tf = positionalPosting.size();
                    df = positionalIndex.get(term).size();
                    if (tf > 0 && df > 0){
                        scores[docID] += VectorSpaceModel.weighting(tf, df, N, termWeighting, documentWeighting);
                    }
                }
            }
        }
        for (i = 0; i < N; i++){
            scores[i] /= documents.get(i).getSize();
            if (scores[i] > 0.0){
                result.add(i, scores[i]);
            }
        }
        result.sort();
        return result;
    }

}
