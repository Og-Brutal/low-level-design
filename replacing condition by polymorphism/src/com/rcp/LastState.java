	package com.rcp;
	
	public class LastState implements State {
		@Override
		public State Action(char c) {
			System.out.println(c);
			if(!Character.isAlphabetic(c)) {
				return null;
			}
			return this;
		}
	}
