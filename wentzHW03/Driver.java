package wentzHW03;

public class Driver {
	public static void main(String[] args) {
		int[][] requests = new int[50][3];
		int[][] hisRequests = {
				{0, 54, 0},
				{23, 132, 6},
				{26, 29, 2},
				{29, 23, 1},
				{35, 198, 7},
				{45, 170, 5},
				{57, 180, 3},
				{83, 78, 4},
				{88, 73, 5},
				{95, 249, 7}
		};
		
		System.out.println("For Given Data:");
		HardDriveSim.fCFS(hisRequests);
		HardDriveSim.sSTF(hisRequests);
		
		requests = HardDriveSim.createRandomRequests(50);
		System.out.println();
		System.out.println();
		System.out.println("For Randomly Generated Data:");
		HardDriveSim.fCFS(requests);
		HardDriveSim.sSTF(requests);
	}
}
