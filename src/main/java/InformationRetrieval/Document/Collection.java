package InformationRetrieval.Document;

import Dictionary.WordComparator;
import InformationRetrieval.Index.*;
import InformationRetrieval.Query.*;
import Math.Matrix;

import java.io.File;
import java.util.*;

public class Collection {
    private final IndexType indexType;
    private TermDictionary dictionary;

    private TermDictionary biWordDictionary;
    private final ArrayList<Document> documents;
    private IncidenceMatrix incidenceMatrix;
    private InvertedIndex invertedIndex;

    private InvertedIndex biWordIndex;
    private final WordComparator comparator;
    private final String name;

    private final Parameter parameter;

    private void constructIndex(){
        switch (indexType){
            case INCIDENCE_MATRIX:
                constructIncidenceMatrix();
                break;
            case INVERTED_INDEX:
                constructInvertedIndex(false);
                if (parameter.isBiWordIndex()){
                    constructInvertedIndex(true);
                }
                break;
        }
    }

    public Collection(String directory,
                      Parameter parameter){
        int i = 0;
        this.name = directory;
        this.indexType = parameter.getIndexType();
        this.comparator = parameter.getWordComparator();
        this.parameter = parameter;
        documents = new ArrayList<>();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            Arrays.sort(listOfFiles);
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    Document document = new Document(file.getAbsolutePath(), file.getName(), i);
                    if (parameter.isNormalizeDocument()){
                        document.normalizeDocument(parameter.getDisambiguator(), parameter.getFsm());
                    }
                    documents.add(document);
                    i++;
                }
            }
        }
        if (parameter.isFromFile()){
            dictionary = new TermDictionary(comparator, directory);
            invertedIndex = new InvertedIndex(directory, dictionary.size());
            if (parameter.isBiWordIndex()){
                biWordDictionary = new TermDictionary(comparator, directory + "-biWord");
                biWordIndex = new InvertedIndex(directory + "-biWord", biWordDictionary.size());
            }
        } else {
            constructIndex();
        }
    }

    public int size(){
        return documents.size();
    }

    public int vocabularySize(){
        return dictionary.size();
    }

    public Document getDocument(int index){
        return documents.get(index);
    }

    private boolean areTermsDifferent(TermOccurrence previousTerm, TermOccurrence currentTerm){
        return previousTerm.getTerm().getName().hashCode() != currentTerm.getTerm().getName().hashCode();
    }

    public void save(){
        if (indexType == IndexType.INVERTED_INDEX){
            dictionary.save(name);
            invertedIndex.save(name);
            if (parameter.isBiWordIndex()){
                biWordDictionary.save(name + "-biWord");
                biWordIndex.save(name + "-biWord");
            }
        }
    }

    private ArrayList<TermOccurrence> constructTerms(boolean biWord){
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        ArrayList<TermOccurrence> docTerms;
        for (Document doc : documents){
            if (biWord){
                docTerms = doc.getBiWordTerms();
            } else {
                docTerms = doc.getTerms();
            }
            terms.addAll(docTerms);
        }
        terms.sort(termComparator);
        return terms;
    }

    private TermDictionary constructDictionary(ArrayList<TermOccurrence> terms){
        int i;
        TermOccurrence term, previousTerm;
        TermDictionary dictionary;
        dictionary = new TermDictionary(comparator);
        if (terms.size() > 0){
            term = terms.get(0);
            dictionary.addTerm(term.getTerm());
            previousTerm = term;
            i = 1;
            while (i < terms.size()){
                term = terms.get(i);
                if (areTermsDifferent(term, previousTerm)){
                    dictionary.addTerm(term.getTerm());
                }
                i++;
                previousTerm = term;
            }
        }
        return dictionary;
    }

    private void constructIncidenceMatrix(){
        int i;
        ArrayList<TermOccurrence> terms;
        TermOccurrence term;
        terms = constructTerms(false);
        dictionary = constructDictionary(terms);
        incidenceMatrix = new IncidenceMatrix(dictionary.size(), documents.size());
        if (terms.size() > 0){
            term = terms.get(0);
            i = 1;
            incidenceMatrix.set(dictionary.getWordIndex(term.getTerm().getName()), term.getDocID());
            while (i < terms.size()){
                term = terms.get(i);
                incidenceMatrix.set(dictionary.getWordIndex(term.getTerm().getName()), term.getDocID());
                i++;
            }
        }
    }

    private void constructInvertedIndex(boolean biWord){
        int i, termId, prevDocId;
        TermOccurrence term, previousTerm;
        TermDictionary dictionary;
        InvertedIndex invertedIndex;
        ArrayList<TermOccurrence> terms = constructTerms(biWord);
        dictionary = constructDictionary(terms);
        invertedIndex = new InvertedIndex(dictionary.size());
        if (terms.size() > 0){
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            termId = dictionary.getWordIndex(term.getTerm().getName());
            invertedIndex.add(termId, term.getDocID());
            invertedIndex.addPosition(termId, term.getDocID(), term.getPosition());
            prevDocId = term.getDocID();
            while (i < terms.size()){
                term = terms.get(i);
                termId = dictionary.getWordIndex(term.getTerm().getName());
                if (areTermsDifferent(term, previousTerm)){
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
        if (biWord){
            biWordIndex = invertedIndex;
            biWordDictionary = dictionary;
        } else {
            this.invertedIndex = invertedIndex;
            this.dictionary = dictionary;
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
        ArrayList<String> list = new ArrayList<>();
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
