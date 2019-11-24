package main;

public class PlacedInTotal implements Comparable<PlacedInTotal> {
	private String name;
	private int sure;
	private int maybe;

	public PlacedInTotal(String name, int sure, int maybe) {
		super();
		this.name = name;
		this.sure = sure;
		this.maybe = maybe;
	}

	@Override
	public String toString() {
		return name + ": " + sure + " eyes placed" + ((maybe > 0) ? ", + (" + maybe + " unsure)" : "");
	}

	@Override
	public int compareTo(PlacedInTotal o) {
		if (o.sure == sure)
			return 0;
		return o.sure < sure ? -1 : 1;
	}
}
