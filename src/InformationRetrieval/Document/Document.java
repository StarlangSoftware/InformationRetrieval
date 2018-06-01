package InformationRetrieval.Document;

import Corpus.*;
import InformationRetrieval.Index.TermOccurrence;

import java.util.ArrayList;

public class Document {

    private String absoluteFileName;
    private String fileName;
    private Corpus corpus;
    private int docId;

    public Document(String absoluteFileName, String fileName, int docId){
        this.docId = docId;
        this.absoluteFileName = absoluteFileName;
        this.fileName = fileName;
        this.corpus = new Corpus(absoluteFileName);
    }

    public int getDocId(){
        return docId;
    }

    public String getFileName(){
        return fileName;
    }

    public String getAbsoluteFileName(){
        return absoluteFileName;
    }

    public int size(){
        return corpus.numberOfWords();
    }

    ArrayList<TermOccurrence> getTerms(){
        ArrayList<TermOccurrence> terms = new ArrayList<TermOccurrence>();
        int size = 0;
        for (int i = 0; i < corpus.sentenceCount(); i++){
            for (int j = 0; j < corpus.getSentence(i).wordCount(); j++){
                terms.add(new TermOccurrence(corpus.getSentence(i).getWord(j), docId, size));
                size++;
            }
        }
        return terms;
    }

}
