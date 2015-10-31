package wentzHW03;

public class HardDriveSim {
	/*
	 * creates random requests and adds them to the array in order of arrival time
	 */
	protected static int[][] createRandomRequests(int numRequests){
		int[][] requests = new int[numRequests][3];
		int minIndex = numRequests;
		for(int i = 0; i < numRequests; i++){
			boolean inserted = false;
			int index = numRequests - 1;
			int arrivalTime = (int)(Math.random() * 100);
			int track = (int)(Math.random() * 250);
			int sector = (int)(Math.random() * 8);
			while(!inserted){
				if(index < minIndex){
					requests[index][0] = arrivalTime;
					requests[index][1] = track;
					requests[index][2] = sector;
					minIndex = index;
					inserted = true;
				} else if(arrivalTime > requests[index][0]){
					//from minIndex to index, move all back 1 space
					//then insert new line
					for(int j = minIndex; j <= index; j++){
						requests[j-1][0] = requests[j][0];
						requests[j-1][1] = requests[j][1];
						requests[j-1][2] = requests[j][2];
					}
					requests[index][0] = arrivalTime;
					requests[index][1] = track;
					requests[index][2] = sector;
					minIndex--;
					inserted = true;
				} else {
					index--;
				}
			}
		}
		return requests;
	}
	
	protected static void print(int[][] array){
		for(int i = 0; i < array.length; i++){
			for(int j = 0; j < array[0].length; j++){
				System.out.print(array[i][j] + "      ");
			}
			System.out.println();
		}
		//System.out.println("-------------------------------");
	}
}
