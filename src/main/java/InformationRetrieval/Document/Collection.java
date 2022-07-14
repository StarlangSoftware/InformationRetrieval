package InformationRetrieval.Document;

import Corpus.*;
import Dictionary.WordComparator;
import Dictionary.Word;
import InformationRetrieval.Index.*;
import InformationRetrieval.Query.*;
import Math.Matrix;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                    Document document = new Document(file.getAbsolutePath(), file.getName(), i, !parameter.loadIndexesFromFile() && parameter.constructIndexInMemory(), parameter.tokenizeDocument());
                    if (parameter.normalizeDocument() && !parameter.loadIndexesFromFile()){
                        document.normalizeDocument(parameter.getDisambiguator(), parameter.getFsm());
                    }
                    documents.add(document);
                    i++;
                }
            }
        }
        if (parameter.loadIndexesFromFile()){
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
            if (parameter.constructIndexInMemory()){
                constructIndexesInMemory();
            } else {
                constructIndexesInDisk();
            }
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

    private void constructIndexesInDisk(){
        HashSet<String> wordList = constructDistinctWordList(TermType.TOKEN);
        dictionary = constructDictionaryFromDistinctWordList(wordList);
        constructInvertedIndexInDisk(dictionary, TermType.TOKEN);
        if (parameter.constructPhraseIndex()){
            wordList = constructDistinctWordList(TermType.PHRASE);
            phraseDictionary = constructDictionaryFromDistinctWordList(wordList);
            constructInvertedIndexInDisk(phraseDictionary, TermType.PHRASE);
        }
        if (parameter.constructKGramIndex()){
            constructKGramIndex();
        }
    }

    private void constructIndexesInMemory(){
        switch (indexType){
            case INCIDENCE_MATRIX:
                constructIncidenceMatrix();
                break;
            case INVERTED_INDEX:
                ArrayList<TermOccurrence> terms = constructTerms(TermType.TOKEN);
                dictionary = constructDictionaryFromTerms(terms);
                invertedIndex = constructInvertedIndex(dictionary, terms, vocabularySize());
                if (parameter.constructPositionalIndex()){
                    positionalIndex = constructPositionalIndex(dictionary, terms, vocabularySize());
                }
                if (parameter.constructPhraseIndex()){
                    terms = constructTerms(TermType.PHRASE);
                    phraseDictionary = constructDictionaryFromTerms(terms);
                    phraseIndex = constructInvertedIndex(phraseDictionary, terms, phraseSize());
                    if (parameter.constructPositionalIndex()){
                        phrasePositionalIndex = constructPositionalIndex(phraseDictionary, terms, phraseSize());
                    }
                }
                if (parameter.constructKGramIndex()){
                    constructKGramIndex();
                }
                break;
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

    private HashSet<String> constructDistinctWordList(Corpus corpus, TermType termType){
        HashSet<String> words = new HashSet<>();
        for (int i = 0; i < corpus.sentenceCount(); i++){
            Sentence sentence = corpus.getSentence(i);
            for (int j = 0; j < sentence.wordCount(); j++){
                switch (termType){
                    case TOKEN:
                        words.add(sentence.getWord(j).getName());
                        break;
                    case PHRASE:
                        if (j < sentence.wordCount() - 1){
                            words.add(sentence.getWord(j).getName() + " " + sentence.getWord(j + 1).getName());
                        }
                }
            }
        }
        return words;
    }

    private HashSet<String> constructDistinctWordList(TermType termType){
        HashSet<String> words = new HashSet<>();
        for (Document doc : documents){
            Corpus corpus = doc.loadDocument(parameter.tokenizeDocument());
            words.addAll(constructDistinctWordList(corpus, termType));
        }
        return words;
    }

    private TermDictionary constructDictionaryFromDistinctWordList(HashSet<String> words){
        TermDictionary dictionary = new TermDictionary(parameter.getWordComparator());
        ArrayList<Word> wordList = new ArrayList<>();
        for (String word : words){
            wordList.add(new Word(word));
        }
        wordList.sort(comparator);
        for (Word term : wordList){
            dictionary.addTerm(term);
        }
        return dictionary;
    }

    private TermDictionary constructDictionaryFromTerms(ArrayList<TermOccurrence> terms){
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
        dictionary = constructDictionaryFromTerms(terms);
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

    private boolean finishedCombination(int[] currentIdList){
        for (int id : currentIdList){
            if (id != -1){
                return false;
            }
        }
        return true;
    }

    private ArrayList<Integer> selectIdFromCombination(int[] currentIdList){
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

    private void combineMultipleInvertedIndexesInDisk(String name, int blockCount){
        BufferedReader[] files;
        int[] currentIdList;
        PostingList[] currentPostingLists;
        currentIdList = new int[blockCount];
        currentPostingLists = new PostingList[blockCount];
        files = new BufferedReader[blockCount];
        try{
            PrintWriter printWriter = new PrintWriter(name + "-postings.txt", "UTF-8");
            for (int i = 0; i < blockCount; i++){
                files[i] = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("tmp-" + i + "-postings.txt")), StandardCharsets.UTF_8));
                String line = files[i].readLine();
                String[] items = line.split(" ");
                currentIdList[i] = Integer.parseInt(items[0]);
                line = files[i].readLine();
                currentPostingLists[i] = new PostingList(line);
            }
            while (!finishedCombination(currentIdList)){
                ArrayList<Integer> indexesToCombine = selectIdFromCombination(currentIdList);
                PostingList mergedPostingList = currentPostingLists[indexesToCombine.get(0)];
                for (int i = 1; i < indexesToCombine.size(); i++){
                    mergedPostingList = mergedPostingList.union(currentPostingLists[indexesToCombine.get(i)]);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void constructInvertedIndexInDisk(TermDictionary dictionary, TermType termType){
        int i = 0, blockCount = 0;
        InvertedIndex invertedIndex = new InvertedIndex(dictionary.size());
        for (Document doc : documents){
            if (i < parameter.getBlockSize()){
                i++;
            } else {
                invertedIndex.save("tmp-" + blockCount);
                invertedIndex = new InvertedIndex(dictionary.size());
                blockCount++;
                i = 0;
            }
            Corpus corpus = doc.loadDocument(parameter.tokenizeDocument());
            HashSet<String> wordList = constructDistinctWordList(corpus, termType);
            for (String word : wordList){
                int termId = dictionary.getWordIndex(word);
                invertedIndex.add(termId, doc.getDocId());
            }
        }
        if (i != 0){
            invertedIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultipleInvertedIndexesInDisk(name, blockCount);
        } else {
            combineMultipleInvertedIndexesInDisk(name + "-phrase", blockCount);
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

    private void constructKGramIndex(){
        ArrayList<TermOccurrence> terms = constructTermsFromDictionary(dictionary, 2);
        biGramDictionary = constructDictionaryFromTerms(terms);
        biGramIndex = constructInvertedIndex(biGramDictionary, terms, biGramSize());
        terms = constructTermsFromDictionary(dictionary, 3);
        triGramDictionary = constructDictionaryFromTerms(terms);
        triGramIndex = constructInvertedIndex(triGramDictionary, terms, triGramSize());
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
                }
        }
        return new QueryResult();
    }

}
