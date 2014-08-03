package exception;

public class NotEnoughBalanceException extends Exception {

	private static final long serialVersionUID = -4694371434945185254L;
	
	private String message = null;

    public NotEnoughBalanceException(String message) {
        super(message);
        this.message = message;
    }
 
    public NotEnoughBalanceException(Throwable cause) {
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
