package exception;

public class MinimumBalanceException extends Exception {

	private static final long serialVersionUID = -3851053861919429645L;
	
	private String message = null;

    public MinimumBalanceException(String message) {
        super(message);
        this.message = message;
    }
 
    public MinimumBalanceException(Throwable cause) {
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
