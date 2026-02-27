package main.java.com.apm.observers;

public interface IObserveable {
	public boolean addObserver(IObserver observer);
	public boolean removeObserver();
	public void update();
}
