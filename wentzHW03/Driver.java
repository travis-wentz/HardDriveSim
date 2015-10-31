package wentzHW03;

public class Driver {
	public static void main(String[] args) {
		int[][] requests = new int[50][3];
		requests = HardDriveSim.createRandomRequests(50);
		HardDriveSim.print(requests);
	}
}
