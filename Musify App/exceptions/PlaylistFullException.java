package exceptions;

public class PlaylistFullException extends Exception {
    public PlaylistFullException(){
        super();
    }

    public PlaylistFullException(String message) {
        super(message);
    }
}
