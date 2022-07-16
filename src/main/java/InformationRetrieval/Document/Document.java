package InformationRetrieval.Document;

import Corpus.*;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParse;
import MorphologicalAnalysis.FsmParseList;
import MorphologicalDisambiguation.MorphologicalDisambiguator;

import java.util.ArrayList;

public class Document {

    private final String absoluteFileName;
    private final String fileName;
    private final int docId;
    private int size = 0;

    public Document(String absoluteFileName, String fileName, int docId){
        this.docId = docId;
        this.absoluteFileName = absoluteFileName;
        this.fileName = fileName;
    }

    public DocumentText loadDocument(boolean tokenizeDocument){
        DocumentText documentText;
        if (tokenizeDocument){
            documentText = new DocumentText(absoluteFileName, new TurkishSplitter());
        } else {
            documentText = new DocumentText(absoluteFileName);
        }
        size = documentText.numberOfWords();
        return documentText;
    }

    public Corpus normalizeDocument(MorphologicalDisambiguator disambiguator, FsmMorphologicalAnalyzer fsm){
        Corpus corpus = new Corpus(absoluteFileName);
        for (int i = 0; i < corpus.sentenceCount(); i++){
            Sentence sentence = corpus.getSentence(i);
            FsmParseList[] parses = fsm.robustMorphologicalAnalysis(sentence);
            ArrayList<FsmParse> correctParses = disambiguator.disambiguate(parses);
            Sentence newSentence = new Sentence();
            for (FsmParse fsmParse : correctParses){
                newSentence.addWord(new Word(fsmParse.getWord().getName()));
            }
            corpus.addSentence(newSentence);
        }
        size = corpus.numberOfWords();
        return corpus;
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

    public int getSize(){
        return size;
    }

}
