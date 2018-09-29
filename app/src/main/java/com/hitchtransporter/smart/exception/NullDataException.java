package com.hitchtransporter.smart.exception;

@SuppressWarnings("serial")
public class NullDataException extends Exception{

	@Override
	public String toString() {
		return "com.diwaween.smart.Exception:NullDataException :No meaning of passing null data to intent";
	}
}
