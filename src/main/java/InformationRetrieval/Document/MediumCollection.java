package InformationRetrieval.Document;

import InformationRetrieval.Index.*;

import java.util.ArrayList;
import java.util.HashSet;

public class MediumCollection extends DiskCollection{

    public MediumCollection(String directory, Parameter parameter) {
        super(directory, parameter);
        constructIndexesInDisk();
    }

    private HashSet<String> constructDistinctWordList(TermType termType){
        HashSet<String> words = new HashSet<>();
        for (Document doc : documents){
            DocumentText documentText = doc.loadDocument();
            words.addAll(documentText.constructDistinctWordList(termType));
        }
        return words;
    }

    private void constructIndexesInDisk(){
        HashSet<String> wordList = constructDistinctWordList(TermType.TOKEN);
        dictionary = new TermDictionary(comparator, wordList);
        constructInvertedIndexInDisk(dictionary, TermType.TOKEN);
        if (parameter.constructPositionalIndex()){
            constructPositionalIndexInDisk(dictionary, TermType.TOKEN);
        }
        if (parameter.constructPhraseIndex()){
            wordList = constructDistinctWordList(TermType.PHRASE);
            phraseDictionary = new TermDictionary(comparator, wordList);
            constructInvertedIndexInDisk(phraseDictionary, TermType.PHRASE);
            if (parameter.constructPositionalIndex()){
                constructPositionalIndexInDisk(phraseDictionary, TermType.PHRASE);
            }
        }
        if (parameter.constructNGramIndex()){
            constructNGramIndex();
        }
    }

    private void constructInvertedIndexInDisk(TermDictionary dictionary, TermType termType){
        int i = 0, blockCount = 0;
        InvertedIndex invertedIndex = new InvertedIndex();
        for (Document doc : documents){
            if (i < parameter.getDocumentLimit()){
                i++;
            } else {
                invertedIndex.save("tmp-" + blockCount);
                invertedIndex = new InvertedIndex();
                blockCount++;
                i = 0;
            }
            DocumentText documentText = doc.loadDocument();
            HashSet<String> wordList = documentText.constructDistinctWordList(termType);
            for (String word : wordList){
                int termId = dictionary.getWordIndex(word);
                invertedIndex.add(termId, doc.getDocId());
            }
        }
        if (documents.size() != 0){
            invertedIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultipleInvertedIndexesInDisk(name, "", blockCount);
        } else {
            combineMultipleInvertedIndexesInDisk(name + "-phrase", "", blockCount);
        }
    }

    private void constructPositionalIndexInDisk(TermDictionary dictionary, TermType termType){
        int i = 0, blockCount = 0;
        PositionalIndex positionalIndex = new PositionalIndex();
        for (Document doc : documents){
            if (i < parameter.getDocumentLimit()){
                i++;
            } else {
                positionalIndex.save("tmp-" + blockCount);
                positionalIndex = new PositionalIndex();
                blockCount++;
                i = 0;
            }
            DocumentText documentText = doc.loadDocument();
            ArrayList<TermOccurrence> terms = documentText.constructTermList(doc.getDocId(), termType);
            for (TermOccurrence termOccurrence : terms){
                int termId = dictionary.getWordIndex(termOccurrence.getTerm().getName());
                positionalIndex.addPosition(termId, termOccurrence.getDocID(), termOccurrence.getPosition());
            }
        }
        if (documents.size() != 0){
            positionalIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultiplePositionalIndexesInDisk(name, blockCount);
        } else {
            combineMultiplePositionalIndexesInDisk(name + "-phrase", blockCount);
        }
    }

}
