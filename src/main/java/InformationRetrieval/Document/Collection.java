package InformationRetrieval.Document;

import Dictionary.WordComparator;
import InformationRetrieval.Index.*;
import InformationRetrieval.Query.*;
import Math.Matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Collection {
    private IndexType indexType;
    private TermDictionary dictionary;
    private ArrayList<Document> documents;
    private IncidenceMatrix incidenceMatrix;
    private InvertedIndex invertedIndex;
    private WordComparator comparator;

    private HashMap<String, String> constructRootList(String fileName){
        HashMap<String, String> rootList = new HashMap<>();
        try {
            Scanner input = new Scanner(new File(fileName));
            while (input.hasNextLine()){
                String line = input.nextLine();
                String[] items = line.split(" ");
                if (items.length == 2){
                    rootList.put(items[0], items[1]);
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return rootList;
    }

    private void constructIndex(){
        switch (indexType){
            case INCIDENCE_MATRIX:
                constructIncidenceMatrix();
                break;
            case INVERTED_INDEX:
                constructInvertedIndex();
                break;
        }
    }

    public Collection(String directory, IndexType indexType, WordComparator comparator){
        int i = 0;
        this.indexType = indexType;
        this.comparator = comparator;
        documents = new ArrayList<Document>();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    documents.add(new Document(file.getAbsolutePath(), file.getName(), i));
                    i++;
                }
            }
        }
        constructIndex();
    }

    public Collection(String directory, IndexType indexType, WordComparator comparator, String rootFile){
        int i = 0;
        HashMap<String, String> rootList = constructRootList(rootFile);
        this.indexType = indexType;
        this.comparator = comparator;
        documents = new ArrayList<Document>();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    documents.add(new Document(file.getAbsolutePath(), file.getName(), i, rootList));
                    i++;
                }
            }
        }
        constructIndex();
    }

    public Collection(String directory, String fileList, IndexType indexType, WordComparator comparator){
        Scanner input;
        String fileName;
        int i = 0;
        this.indexType = indexType;
        this.comparator = comparator;
        documents = new ArrayList<Document>();
        try {
            input = new Scanner(new File(fileList));
            while (input.hasNext()){
                fileName = input.next();
                documents.add(new Document(directory + fileName, fileName, i));
                i++;
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        constructIndex();
    }

    public Collection(String directory, String fileList, IndexType indexType, WordComparator comparator, String rootFile){
        Scanner input;
        String fileName;
        int i = 0;
        HashMap<String, String> rootList = constructRootList(rootFile);
        this.indexType = indexType;
        this.comparator = comparator;
        documents = new ArrayList<Document>();
        try {
            input = new Scanner(new File(fileList));
            while (input.hasNext()){
                fileName = input.next();
                documents.add(new Document(directory + fileName, fileName, i, rootList));
                i++;
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        constructIndex();
    }

    public int size(){
        return documents.size();
    }

    public Document getDocument(int index){
        return documents.get(index);
    }

    private ArrayList<TermOccurrence> constructDictionary(){
        int i;
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<TermOccurrence>();
        ArrayList<TermOccurrence> docTerms;
        TermOccurrence term, previousTerm;
        dictionary = new TermDictionary(comparator);
        for (Document doc : documents){
            docTerms = doc.getTerms();
            terms.addAll(docTerms);
        }
        Collections.sort(terms, termComparator);
        if (terms.size() > 0){
            term = terms.get(0);
            dictionary.addTerm(term.getTerm());
            previousTerm = term;
            i = 1;
            while (i < terms.size()){
                term = terms.get(i);
                if (!term.getTerm().getName().equals(previousTerm.getTerm().getName())){
                    dictionary.addTerm(term.getTerm());
                }
                i++;
                previousTerm = term;
            }
        }
        return terms;
    }

    private void constructIncidenceMatrix(){
        int termId, i;
        ArrayList<TermOccurrence> terms;
        TermOccurrence term, previousTerm;
        terms = constructDictionary();
        incidenceMatrix = new IncidenceMatrix(dictionary.size(), documents.size());
        if (terms.size() > 0){
            termId = 0;
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            incidenceMatrix.set(termId, term.getDocID());
            while (i < terms.size()){
                term = terms.get(i);
                if (!term.getTerm().getName().equals(previousTerm.getTerm().getName())){
                    termId++;
                }
                incidenceMatrix.set(termId, term.getDocID());
                i++;
                previousTerm = term;
            }
        }
    }

    private void constructInvertedIndex(){
        int i, termId, prevDocId;
        ArrayList<TermOccurrence> terms;
        TermOccurrence term, previousTerm;
        terms = constructDictionary();
        invertedIndex = new InvertedIndex(dictionary.size());
        if (terms.size() > 0){
            termId = 0;
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            invertedIndex.add(termId, term.getDocID());
            invertedIndex.addPosition(termId, term.getDocID(), term.getPosition());
            prevDocId = term.getDocID();
            while (i < terms.size()){
                term = terms.get(i);
                if (!term.getTerm().getName().equals(previousTerm.getTerm().getName())){
                    termId++;
                    invertedIndex.add(termId, term.getDocID());
                    invertedIndex.addPosition(termId, term.getDocID(), term.getPosition());
                    prevDocId = term.getDocID();
                } else {
                    if (prevDocId != term.getDocID()){
                        invertedIndex.add(termId, term.getDocID());
                        invertedIndex.addPosition(termId, term.getDocID(), term.getPosition());
                        prevDocId = term.getDocID();
                    } else {
                        invertedIndex.addPosition(termId, term.getDocID(), term.getPosition());
                    }
                }
                i++;
                previousTerm = term;
            }
        }
    }

    public VectorSpaceModel getVectorSpaceModel(int docId, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        return new VectorSpaceModel(invertedIndex.getTermFrequencies(docId), invertedIndex.getDocumentFrequencies(), documents.size(), termWeighting, documentWeighting);
    }

    public double cosineSimilarity(Collection collection2, VectorSpaceModel spaceModel1, VectorSpaceModel spaceModel2){
        int index1, index2;
        double sum = 0.0;
        for (index1 = 0; index1 < dictionary.size(); index1++){
            if (spaceModel1.get(index1) > 0.0){
                index2 = collection2.dictionary.getWordIndex(dictionary.getWord(index1).getName());
                if (index2 != -1 && spaceModel2.get(index2) > 0.0){
                    sum += spaceModel1.get(index1) * spaceModel2.get(index2);
                }
            }
        }
        return sum;
    }

    private double cosineSimilarity(VectorSpaceModel spaceModel1, VectorSpaceModel spaceModel2){
        int index;
        double sum = 0.0;
        for (index = 0; index < dictionary.size(); index++){
            sum += spaceModel1.get(index) * spaceModel2.get(index);
        }
        return sum;
    }

    public Matrix cosineSimilarity(TermWeighting termWeighting, DocumentWeighting documentWeighting){
        Matrix result = new Matrix(size(), size());
        VectorSpaceModel[] models;
        models = new VectorSpaceModel[documents.size()];
        for (int i = 0; i < documents.size(); i++){
            models[i] = getVectorSpaceModel(i, termWeighting, documentWeighting);
        }
        for (int i = 0; i < documents.size(); i++){
            for (int j = 0; j < documents.size(); j++){
                result.setValue(i, j, cosineSimilarity(models[i], models[j]));
            }
        }
        return result;
    }

    public ArrayList<String> sharedWordList(Collection collection2, VectorSpaceModel spaceModel1, VectorSpaceModel spaceModel2){
        int index1, index2;
        ArrayList<String> list = new ArrayList<String>();
        for (index1 = 0; index1 < dictionary.size(); index1++){
            if (spaceModel1.get(index1) > 0.0){
                index2 = collection2.dictionary.getWordIndex(dictionary.getWord(index1).getName());
                if (index2 != -1 && spaceModel2.get(index2) > 0.0){
                    list.add(dictionary.getWord(index1).getName());
                }
            }
        }
        return list;
    }

    public QueryResult searchCollection(Query query, RetrievalType retrievalType, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        switch (indexType){
            case INCIDENCE_MATRIX:
                return incidenceMatrix.search(query, dictionary);
            case   INVERTED_INDEX:
                switch (retrievalType){
                    case    BOOLEAN:return invertedIndex.search(query, dictionary);
                    case POSITIONAL:return invertedIndex.positionalSearch(query, dictionary);
                    case     RANKED:return invertedIndex.rankedSearch(query, dictionary, documents, termWeighting, documentWeighting);
                    default        :return invertedIndex.rankedSearch(query, dictionary, documents, termWeighting, documentWeighting);
                }
            default				  :
                switch (retrievalType){
                    case    BOOLEAN:return invertedIndex.search(query, dictionary);
                    case POSITIONAL:return invertedIndex.positionalSearch(query, dictionary);
                    case     RANKED:return invertedIndex.rankedSearch(query, dictionary, documents, termWeighting, documentWeighting);
                    default        :return invertedIndex.search(query, dictionary);
            }
        }
    }

}
