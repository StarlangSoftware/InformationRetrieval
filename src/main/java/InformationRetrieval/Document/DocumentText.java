package InformationRetrieval.Document;

import Corpus.*;
import Dictionary.Word;
import InformationRetrieval.Index.TermOccurrence;
import InformationRetrieval.Index.TermType;

import java.util.ArrayList;
import java.util.HashSet;

public class DocumentText extends Corpus {

    /**
     * Empty constructor for the DocumentText class.
     */
    public DocumentText(){
    }

    /**
     * Another constructor for the DocumentText class. Calls super with the given file name.
     * @param fileName File name of the corpus
     */
    public DocumentText(String fileName){
        super(fileName);
    }

    /**
     * Another constructor for the DocumentText class. Calls super with the given file name and sentence splitter.
     * @param fileName File name of the corpus
     * @param sentenceSplitter Sentence splitter class that separates sentences.
     */
    public DocumentText(String fileName, SentenceSplitter sentenceSplitter){
        super(fileName, sentenceSplitter);
    }

    /**
     * Given the corpus, creates a hash set of distinct terms. If term type is TOKEN, the terms are single word, if
     * the term type is PHRASE, the terms are bi-words.
     * @param termType If term type is TOKEN, the terms are single word, if the term type is PHRASE, the terms are
     *                 bi-words.
     * @return Hash set of terms occurring in the document.
     */
    public HashSet<String> constructDistinctWordList(TermType termType){
        HashSet<String> words = new HashSet<>();
        for (int i = 0; i < sentenceCount(); i++){
            Sentence sentence = getSentence(i);
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

    /**
     * Given the corpus, creates an array of terms occurring in the document in that order. If term type is TOKEN, the
     * terms are single word, if the term type is PHRASE, the terms are bi-words.
     * @param docId Id of the document
     * @param termType If term type is TOKEN, the terms are single word, if the term type is PHRASE, the terms are
     *                 bi-words.
     * @return Array list of terms occurring in the document.
     */
    public ArrayList<TermOccurrence> constructTermList(int docId, TermType termType){
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        int size = 0;
        for (int i = 0; i < sentenceCount(); i++){
            Sentence sentence = getSentence(i);
            for (int j = 0; j < sentence.wordCount(); j++){
                switch (termType){
                    case TOKEN:
                        terms.add(new TermOccurrence(sentence.getWord(j), docId, size));
                        size++;
                        break;
                    case PHRASE:
                        if (j < sentence.wordCount() - 1){
                            terms.add(new TermOccurrence(new Word(sentence.getWord(j).getName() + " " + sentence.getWord(j + 1).getName()), docId, size));
                            size++;
                        }
                }
            }
        }
        return terms;
    }

}
