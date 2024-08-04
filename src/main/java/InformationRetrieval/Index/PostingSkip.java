package InformationRetrieval.Index;

/**
 * Augments the postings with skip pointers.
 */
public class PostingSkip extends Posting{

    private boolean skipAvailable = false;
    private PostingSkip skip = null;
    private PostingSkip next = null;

    /**
     * Constructor for the PostingSkip class. Sets the document id.
     * @param Id Document id.
     */
    public PostingSkip(int Id) {
        super(Id);
    }

    /**
     * Checks if this posting has a skip pointer or not.
     * @return True, if this posting has a skip pointer, false otherwise.
     */
    public boolean hasSkip(){
        return skipAvailable;
    }

    /**
     * Adds a skip pointer to the next skip posting.
     * @param skip Next posting to jump.
     */
    public void addSkip(PostingSkip skip){
        skipAvailable = true;
        this.skip = skip;
    }

    /**
     * Updated the skip pointer.
     * @param next New skip pointer
     */
    public void setNext(PostingSkip next){
        this.next = next;
    }

    /**
     * Accessor for the skip pointer.
     * @return Next posting to skip.
     */
    public PostingSkip next(){
        return next;
    }

    public PostingSkip getSkip(){
        return skip;
    }
}
