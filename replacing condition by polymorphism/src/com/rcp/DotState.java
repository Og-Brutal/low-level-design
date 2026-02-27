package com.rcp;
//f233043@nu420.pk
public class DotState implements State {
	@Override
	public State Action(char c) {
		if(!Character.isLetterOrDigit(c) && c!='.') {
			return null;
		}
		else if (c=='.') {
			return new LastState();
		}
		return this;
	}
}
