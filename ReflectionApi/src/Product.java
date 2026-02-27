
public class Product {
	private String name;
	private String passport;
	private int id;
	
	
	public Product() {}
	
	public Product(String name,String passport,int id) {
		this.name=name;
		this.passport=passport;
		this.id=id;
	}
	
	public void  show() {
		System.out.println("Showing something");
	}
	
	{
		System.out.println("in instance block calls when class is instanciated");
	}
	
	static
	{
		System.out.println("in static block calls when class is loaded");
	}
	public static void stat() {
		System.out.println("Static function called");
	}
}
