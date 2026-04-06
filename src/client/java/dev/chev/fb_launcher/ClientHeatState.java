package dev.chev.fb_launcher;

public final class ClientHeatState {

	private static int heat;
	private static int overheatedTicks;

	private ClientHeatState() {}

	public static void update(int h, int overheated) {
		heat = h;
		overheatedTicks = overheated;
	}

	public static void clear() {
		heat = 0;
		overheatedTicks = 0;
	}

	public static int heat() {
		return heat;
	}

	public static int overheatedTicks() {
		return overheatedTicks;
	}
}
