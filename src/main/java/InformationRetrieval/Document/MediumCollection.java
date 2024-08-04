package InformationRetrieval.Document;

import InformationRetrieval.Index.*;

import java.util.ArrayList;
import java.util.HashSet;

public class MediumCollection extends DiskCollection{

    /**
     * Constructor for the MediumCollection class. In medium collections, dictionary is kept in memory and indexes are
     * stored in the disk and don't fit in memory in their construction phase and usage phase. For that reason, in their
     * construction phase, multiple disk reads and optimizations are needed.
     * @param directory Directory where the document collection resides.
     * @param parameter Search parameter
     */
    public MediumCollection(String directory, Parameter parameter) {
        super(directory, parameter);
        constructIndexesInDisk();
    }

    /**
     * Given the document collection, creates a hash set of distinct terms. If term type is TOKEN, the terms are single
     * word, if the term type is PHRASE, the terms are bi-words. Each document is loaded into memory and distinct
     * word list is created. Since the dictionary can be kept in memory, all operations can be done in memory.
     * @param termType If term type is TOKEN, the terms are single word, if the term type is PHRASE, the terms are
     *                 bi-words.
     * @return Hash set of terms occurring in the document collection.
     */
    private HashSet<String> constructDistinctWordList(TermType termType){
        HashSet<String> words = new HashSet<>();
        for (Document doc : documents){
            DocumentText documentText = doc.loadDocument();
            words.addAll(documentText.constructDistinctWordList(termType));
        }
        return words;
    }

    /**
     * In block sort based indexing, the indexes are created in a block wise manner. They do not fit in memory, therefore
     * documents are read one by one. According to the search parameter, inverted index, positional index, phrase
     * indexes, N-Gram indexes are constructed in disk.
     */
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

    /**
     * In block sort based indexing, the inverted index is created in a block wise manner. It does not fit in memory,
     * therefore documents are read one by one. For each document, the terms are added to the inverted index. If the
     * number of documents read are above the limit, current partial inverted index file is saved and new inverted index
     * file is open. After reading all documents, we combine the inverted index files to get the final inverted index
     * file.
     * @param dictionary Term dictionary.
     * @param termType If term type is TOKEN, the terms are single word, if the term type is PHRASE, the terms are
     *                 bi-words.
     */
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
        if (!documents.isEmpty()){
            invertedIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultipleInvertedIndexesInDisk(name, "", blockCount);
        } else {
            combineMultipleInvertedIndexesInDisk(name + "-phrase", "", blockCount);
        }
    }

    /**
     * In block sort based indexing, the positional index is created in a block wise manner. It does not fit in memory,
     * therefore documents are read one by one. For each document, the terms are added to the positional index. If the
     * number of documents read are above the limit, current partial positional index file is saved and new positional
     * index file is open. After reading all documents, we combine the posiitonal index files to get the final
     * positional index file.
     * @param dictionary Term dictionary.
     * @param termType If term type is TOKEN, the terms are single word, if the term type is PHRASE, the terms are
     *                 bi-words.
     */
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
        if (!documents.isEmpty()){
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
