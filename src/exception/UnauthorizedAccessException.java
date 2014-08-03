package exception;

public class UnauthorizedAccessException extends Exception {

	private static final long serialVersionUID = -8548069819245092136L;
	
	private String message = null;

    public UnauthorizedAccessException(String message) {
        super(message);
        this.message = message;
    }
 
    public UnauthorizedAccessException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }

}
