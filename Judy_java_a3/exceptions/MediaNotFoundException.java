package exceptions;

public class MediaNotFoundException extends Exception {
    public MediaNotFoundException(){
        super();
    }

    public MediaNotFoundException(String message) {
        super(message);
    }
}
