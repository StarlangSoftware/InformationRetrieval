package InformationRetrieval.Index;

public class PostingSkip extends Posting{

    private boolean skipAvailable = false;
    private PostingSkip skip = null;
    private PostingSkip next = null;

    public PostingSkip(int Id) {
        super(Id);
    }

    public boolean hasSkip(){
        return skipAvailable;
    }

    public void addSkip(PostingSkip skip){
        skipAvailable = true;
        this.skip = skip;
    }

    public void setNext(PostingSkip next){
        this.next = next;
    }

    public PostingSkip next(){
        return next;
    }

    public PostingSkip getSkip(){
        return skip;
    }
}
