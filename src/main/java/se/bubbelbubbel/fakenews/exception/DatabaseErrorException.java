package se.bubbelbubbel.fakenews.exception;

public class DatabaseErrorException extends Exception {

	public DatabaseErrorException() {
	}

	public DatabaseErrorException(String msg) {
		super(msg);
	}

}
