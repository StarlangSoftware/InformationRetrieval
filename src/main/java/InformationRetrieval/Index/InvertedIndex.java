package InformationRetrieval.Index;

import InformationRetrieval.Document.Document;
import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;
import InformationRetrieval.Query.VectorSpaceModel;

import java.util.ArrayList;
import java.util.Collections;

public class InvertedIndex {
    private PostingList[] index;
    private PositionalPostingList[] positionalIndex;
    private int dictionarySize;

    public InvertedIndex(int dictionarySize){
        this.dictionarySize = dictionarySize;
        index = new PostingList[dictionarySize];
        positionalIndex = new PositionalPostingList[dictionarySize];
        for (int i = 0; i < dictionarySize; i++){
            index[i] = new PostingList();
        }
        for (int i = 0; i < dictionarySize; i++){
            positionalIndex[i] = new PositionalPostingList();
        }
    }

    public void add(int termId, int docId){
        index[termId].add(docId);
    }

    public void addPosition(int termId, int docId, int position){
        positionalIndex[termId].add(docId, position);
    }

    public QueryResult search(Query query, TermDictionary dictionary){
        int i, termIndex;
        PostingList result;
        PostingListComparator comparator = new PostingListComparator();
        ArrayList<PostingList> queryTerms = new ArrayList<PostingList>();
        for (i = 0; i < query.size(); i++){
            termIndex = dictionary.getWordIndex(query.getTerm(i).getName());
            if (termIndex != -1){
                queryTerms.add(index[termIndex]);
            }
        }
        Collections.sort(queryTerms, comparator);
        result = queryTerms.get(0);
        for (i = 1; i < queryTerms.size(); i++){
            result = result.intersection(queryTerms.get(i));
        }
        return result.toQueryResult();
    }

    public QueryResult positionalSearch(Query query, TermDictionary dictionary){
        int i, term;
        PositionalPostingList postingResult = null;
        for (i = 0; i < query.size(); i++){
            term = dictionary.getWordIndex(query.getTerm(i).getName());
            if (term != -1){
                if (i == 0){
                    postingResult = positionalIndex[term];
                } else {
                    if (postingResult != null){
                        postingResult = postingResult.intersection(positionalIndex[term]);
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
        tf = new int[dictionarySize];
        for (int i = 0; i < dictionarySize; i++){
            positionalPostingList = positionalIndex[i];
            index = positionalPostingList.getIndex(docId);
            if (index != -1){
                tf[i] = positionalPostingList.get(index).size();
            } else {
                tf[i] = 0;
            }
        }
        return tf;
    }

    public int[] getDocumentFrequencies(){
        int[] df;
        df = new int[dictionarySize];
        for (int i = 0; i < dictionarySize; i++){
            df[i] = positionalIndex[i].size();
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
                positionalPostingList = positionalIndex[term];
                for (j = 0; j < positionalPostingList.size(); j++){
                    PositionalPosting positionalPosting = positionalPostingList.get(j);
                    docID = positionalPosting.getDocId();
                    tf = positionalPosting.size();
                    df = positionalIndex[term].size();
                    if (tf > 0 && df > 0){
                        scores[docID] += VectorSpaceModel.weighting(tf, df, N, termWeighting, documentWeighting);
                    }
                }
            }
        }
        for (i = 0; i < N; i++){
            scores[i] /= documents.get(i).size();
            if (scores[i] > 0.0){
                result.add(i, scores[i]);
            }
        }
        result.sort();
        return result;
    }

}
