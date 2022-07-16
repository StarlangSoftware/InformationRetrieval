package InformationRetrieval.Document;

import Corpus.*;
import Dictionary.Word;
import InformationRetrieval.Index.TermOccurrence;
import InformationRetrieval.Index.TermType;

import java.util.ArrayList;
import java.util.HashSet;

public class DocumentText extends Corpus {

    public DocumentText(String fileName){
        super(fileName);
    }

    public DocumentText(String fileName, SentenceSplitter sentenceSplitter){
        super(fileName, sentenceSplitter);
    }

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

    public ArrayList<TermOccurrence> constructTermList(Document doc, TermType termType){
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        int size = 0;
        for (int i = 0; i < sentenceCount(); i++){
            Sentence sentence = getSentence(i);
            for (int j = 0; j < sentence.wordCount(); j++){
                switch (termType){
                    case TOKEN:
                        terms.add(new TermOccurrence(sentence.getWord(j), doc.getDocId(), size));
                        size++;
                        break;
                    case PHRASE:
                        if (j < sentence.wordCount() - 1){
                            terms.add(new TermOccurrence(new Word(sentence.getWord(j).getName() + " " + sentence.getWord(j + 1).getName()), doc.getDocId(), size));
                            size++;
                        }
                }
            }
        }
        return terms;
    }

}
