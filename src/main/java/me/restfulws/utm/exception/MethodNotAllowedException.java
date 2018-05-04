package me.restfulws.utm.exception;

public class MethodNotAllowedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MethodNotAllowedException() {
		super("Method not allowed");
	}
	
	public MethodNotAllowedException(String message) {
		super(message);
	}
}