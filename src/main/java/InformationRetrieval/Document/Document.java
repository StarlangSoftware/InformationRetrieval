package InformationRetrieval.Document;

import Corpus.*;
import Dictionary.Word;
import InformationRetrieval.Index.TermOccurrence;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParse;
import MorphologicalAnalysis.FsmParseList;
import MorphologicalDisambiguation.MorphologicalDisambiguator;

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
        corpus = new Corpus(absoluteFileName);
    }

    public void normalizeDocument(MorphologicalDisambiguator disambiguator, FsmMorphologicalAnalyzer fsm){
        Corpus tmpCorpus = new Corpus();
        for (int i = 0; i < corpus.sentenceCount(); i++){
            Sentence sentence = corpus.getSentence(i);
            FsmParseList[] parses = fsm.robustMorphologicalAnalysis(sentence);
            ArrayList<FsmParse> correctParses = disambiguator.disambiguate(parses);
            Sentence newSentence = new Sentence();
            for (FsmParse fsmParse : correctParses){
                newSentence.addWord(new Word(fsmParse.getWord().getName()));
            }
            tmpCorpus.addSentence(newSentence);
        }
        corpus = tmpCorpus;
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
