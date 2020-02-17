package main;

public class Loot extends Logline {
	private String receiver;
	private String gained;

	public Loot(String receiver, String gained) {
		this.receiver = receiver;
		this.gained = gained;
	}

	@Override
	public String toString() {
		return receiver + ";" + gained;
	}

	@Override
	public boolean isPlacedEye() {
		return false;
	}

}
