package com.deve.timeschedule.exception;

public class InvalidTriggerException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidTriggerException(String message) {
		super("Invalid trigger exception: reason - " + message);
	}
}
