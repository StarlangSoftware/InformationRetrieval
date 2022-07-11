package InformationRetrieval.Document;

import Dictionary.WordComparator;
import Dictionary.Word;
import InformationRetrieval.Index.*;
import InformationRetrieval.Query.*;
import Math.Matrix;

import java.io.File;
import java.util.*;

public class Collection {
    private final IndexType indexType;
    private TermDictionary dictionary;

    private TermDictionary phraseDictionary;

    private TermDictionary biGramDictionary;

    private TermDictionary triGramDictionary;
    private final ArrayList<Document> documents;
    private IncidenceMatrix incidenceMatrix;
    private InvertedIndex invertedIndex;

    private InvertedIndex biGramIndex;

    private InvertedIndex triGramIndex;
    private PositionalIndex positionalIndex;
    private InvertedIndex phraseIndex;
    private PositionalIndex phrasePositionalIndex;
    private final WordComparator comparator;
    private final String name;
    private final Parameter parameter;

    private void constructIndexes(){
        switch (indexType){
            case INCIDENCE_MATRIX:
                constructIncidenceMatrix();
                break;
            case INVERTED_INDEX:
                ArrayList<TermOccurrence> terms = constructTerms(TermType.TOKEN);
                dictionary = constructDictionary(terms);
                invertedIndex = constructInvertedIndex(dictionary, terms, vocabularySize());
                if (parameter.constructPositionalIndex()){
                    positionalIndex = constructPositionalIndex(dictionary, terms, vocabularySize());
                }
                if (parameter.constructPhraseIndex()){
                    terms = constructTerms(TermType.PHRASE);
                    phraseDictionary = constructDictionary(terms);
                    phraseIndex = constructInvertedIndex(phraseDictionary, terms, phraseSize());
                    if (parameter.constructPositionalIndex()){
                        phrasePositionalIndex = constructPositionalIndex(phraseDictionary, terms, phraseSize());
                    }
                }
                if (parameter.constructKGramIndex()){
                    terms = constructTermsFromDictionary(dictionary, 2);
                    biGramDictionary = constructDictionary(terms);
                    biGramIndex = constructInvertedIndex(biGramDictionary, terms, biGramSize());
                    terms = constructTermsFromDictionary(dictionary, 3);
                    triGramDictionary = constructDictionary(terms);
                    triGramIndex = constructInvertedIndex(triGramDictionary, terms, triGramSize());
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
                    if (parameter.normalizeDocument()){
                        document.normalizeDocument(parameter.getDisambiguator(), parameter.getFsm());
                    }
                    documents.add(document);
                    i++;
                }
            }
        }
        if (parameter.constructFromFile()){
            dictionary = new TermDictionary(comparator, directory);
            invertedIndex = new InvertedIndex(directory, vocabularySize());
            if (parameter.constructPositionalIndex()){
                positionalIndex = new PositionalIndex(directory, vocabularySize());
            }
            if (parameter.constructPhraseIndex()){
                phraseDictionary = new TermDictionary(comparator, directory + "-phrase");
                phraseIndex = new InvertedIndex(directory + "-phrase", phraseSize());
                if (parameter.constructPositionalIndex()){
                    phrasePositionalIndex = new PositionalIndex(directory + "-phrase", phraseSize());
                }
            }
            if (parameter.constructKGramIndex()){
                biGramDictionary = new TermDictionary(comparator, directory + "-biGram");
                triGramDictionary = new TermDictionary(comparator, directory + "-triGram");
                biGramIndex = new InvertedIndex(directory + "-biGram", biGramSize());
                triGramIndex = new InvertedIndex(directory + "-triGram", triGramSize());
            }
        } else {
            constructIndexes();
        }
    }

    public int size(){
        return documents.size();
    }

    public int vocabularySize(){
        return dictionary.size();
    }

    public int biGramSize(){
        return biGramDictionary.size();
    }

    public int triGramSize(){
        return triGramDictionary.size();
    }

    public int phraseSize(){
        return phraseDictionary.size();
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
            if (parameter.constructPositionalIndex()){
                positionalIndex.save(name);
            }
            if (parameter.constructPhraseIndex()){
                phraseDictionary.save(name + "-phrase");
                phraseIndex.save(name + "-phrase");
                if (parameter.constructPositionalIndex()){
                    phrasePositionalIndex.save(name + "-phrase");
                }
            }
            if (parameter.constructKGramIndex()){
                biGramDictionary.save(name + "-biGram");
                triGramDictionary.save(name + "-triGram");
                biGramIndex.save(name + "-biGram");
                triGramIndex.save(name + "-triGram");
            }
        }
    }

    private ArrayList<TermOccurrence> constructTerms(TermType termType){
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        ArrayList<TermOccurrence> docTerms;
        for (Document doc : documents){
            switch (termType){
                case PHRASE:
                    docTerms = doc.getPhrases();
                    break;
                case TOKEN:
                default:
                    docTerms = doc.getTokens();
                    break;
            }
            terms.addAll(docTerms);
        }
        terms.sort(termComparator);
        return terms;
    }

    private ArrayList<TermOccurrence> constructTermsFromDictionary(TermDictionary dictionary, int k){
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        for (int i = 0; i < dictionary.size(); i++){
            String word = dictionary.getWord(i).getName();
            if (word.length() >= k - 1){
                for (int l = -1; l < word.length() - k + 2; l++){
                    String term;
                    if (l == -1){
                        term = "$" + word.substring(0, k - 1);
                    } else {
                        if (l == word.length() - k + 1){
                            term = word.substring(l, l + k - 1) + "$";
                        } else {
                            term = word.substring(l, l + k);
                        }
                    }
                    terms.add(new TermOccurrence(new Word(term), i, l));
                }
            }
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
        terms = constructTerms(TermType.TOKEN);
        dictionary = constructDictionary(terms);
        incidenceMatrix = new IncidenceMatrix(vocabularySize(), documents.size());
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

    private InvertedIndex constructInvertedIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, int size){
        int i, termId, prevDocId;
        TermOccurrence term, previousTerm;
        InvertedIndex invertedIndex;
        invertedIndex = new InvertedIndex(size);
        if (terms.size() > 0){
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            termId = dictionary.getWordIndex(term.getTerm().getName());
            invertedIndex.add(termId, term.getDocID());
            prevDocId = term.getDocID();
            while (i < terms.size()){
                term = terms.get(i);
                termId = dictionary.getWordIndex(term.getTerm().getName());
                if (termId != -1){
                    if (areTermsDifferent(term, previousTerm)){
                        invertedIndex.add(termId, term.getDocID());
                        prevDocId = term.getDocID();
                    } else {
                        if (prevDocId != term.getDocID()){
                            invertedIndex.add(termId, term.getDocID());
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
        return invertedIndex;
    }

    private PositionalIndex constructPositionalIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, int size){
        int i, termId, prevDocId;
        TermOccurrence term, previousTerm;
        PositionalIndex positionalIndex;
        positionalIndex = new PositionalIndex(size);
        if (terms.size() > 0){
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            termId = dictionary.getWordIndex(term.getTerm().getName());
            positionalIndex.addPosition(termId, term.getDocID(), term.getPosition());
            prevDocId = term.getDocID();
            while (i < terms.size()){
                term = terms.get(i);
                termId = dictionary.getWordIndex(term.getTerm().getName());
                if (termId != -1){
                    if (areTermsDifferent(term, previousTerm)){
                        positionalIndex.addPosition(termId, term.getDocID(), term.getPosition());
                        prevDocId = term.getDocID();
                    } else {
                        if (prevDocId != term.getDocID()){
                            positionalIndex.addPosition(termId, term.getDocID(), term.getPosition());
                            prevDocId = term.getDocID();
                        } else {
                            positionalIndex.addPosition(termId, term.getDocID(), term.getPosition());
                        }
                    }
                } else {
                    System.out.println("Error: Term " + term.getTerm().getName() + " does not exist");
                }
                i++;
                previousTerm = term;
            }
        }
        return positionalIndex;
    }

    public VectorSpaceModel getVectorSpaceModel(int docId, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        return new VectorSpaceModel(positionalIndex.getTermFrequencies(docId), positionalIndex.getDocumentFrequencies(), documents.size(), termWeighting, documentWeighting);
    }

    public double cosineSimilarity(Collection collection2, VectorSpaceModel spaceModel1, VectorSpaceModel spaceModel2){
        int index1, index2;
        double sum = 0.0;
        for (index1 = 0; index1 < vocabularySize(); index1++){
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
        for (index = 0; index < vocabularySize(); index++){
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
        for (index1 = 0; index1 < vocabularySize(); index1++){
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
                    case POSITIONAL:return positionalIndex.positionalSearch(query, dictionary);
                    case     RANKED:return positionalIndex.rankedSearch(query, dictionary, documents, termWeighting, documentWeighting);
                    default        :return positionalIndex.rankedSearch(query, dictionary, documents, termWeighting, documentWeighting);
                }
            default				  :
                switch (retrievalType){
                    case    BOOLEAN:return invertedIndex.search(query, dictionary);
                    case POSITIONAL:return positionalIndex.positionalSearch(query, dictionary);
                    case     RANKED:return positionalIndex.rankedSearch(query, dictionary, documents, termWeighting, documentWeighting);
                    default        :return invertedIndex.search(query, dictionary);
            }
        }
    }

}
