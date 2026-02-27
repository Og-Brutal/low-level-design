package com.rcp;

public class AlphaState implements State {
	@Override
	public State Action(char c) {
		if(!Character.isLetterOrDigit(c) && c!='@') {
			return null;
		}
		else if (c=='@') {
			return new DotState();
		}
		return this;
	}
}

