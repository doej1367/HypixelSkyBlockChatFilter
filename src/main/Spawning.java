package main;

import java.util.ArrayList;

public class Spawning {
	private ArrayList<PlacedEye> spawners = new ArrayList<>();

	public void add(PlacedEye e) {
		spawners.add(e);
	}

	public void calcPlacingProbabilities() {
		if (isCleanSpawn())
			for (PlacedEye e : spawners)
				e.setProbability(1.0);
		else {
			PlacedEye last = null;
			for (PlacedEye e : spawners) {
				if (last == null) {
					e.setProbability(e.getNumber());
				} else {
					if (last.getNumber() + 1 <= e.getNumber()) {
						e.setProbability(e.getNumber() - last.getNumber());
					} else {
						e.setProbability(1.0);
						int tmp_size = spawners.indexOf(last);
						for (int i = 0; i <= tmp_size; i++) {
							PlacedEye tmp = spawners.get(i);
							tmp.setProbability((last.getNumber() - e.getNumber() + 1.0) / last.getNumber());
						}
					}
				}
				last = e;
			}
		}
	}

	public int getRemovedEyesCount() {
		return spawners.size() - 8;
	}

	/**
	 * A clean spawn is a spawn without eyes being removed and placed again
	 * 
	 * @return
	 */
	public boolean isCleanSpawn() {
		return spawners.size() == 8;
	}
}
