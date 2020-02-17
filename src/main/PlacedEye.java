package main;

public class PlacedEye extends Logline {
	private String name;
	private int number;
	private double probability = 0.0;

	public PlacedEye(String name, int number) {
		this.setName(name);
		this.setNumber(number);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public boolean isPlacedEye() {
		return true;
	}
}
