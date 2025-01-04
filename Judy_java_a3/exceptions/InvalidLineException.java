package exceptions;

public class InvalidLineException extends Exception {
    public InvalidLineException(){
        super();
    }

    public InvalidLineException(String message) {
        super(message);
    }
}

