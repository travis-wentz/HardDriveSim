package wentzHW03;

/*
 * Simulates different algorithms for servicing requests. Requests are in the form of a multi-dimensional array
 * where the first column is arrival time, second col is track requested, third col is sector requested.
 * Given: 250 tracks; 8 sectors (0-7) per track; start position is 0, 0; only rotates 1 direction and takes 1ms to
 * move to next sector; seek time is 10 +0.1*T ms where T = number of tracks between previous and current.
 */
public class HardDriveSim {
	
	/*
	 * First-come-first-serve algorithm.
	 * Serves requests in order of arrival time.
	 * 
	 */
	protected static void fCFS(int[][] requests){
		int curTrack = 0;
		int curSector = 0;
		double T = 0;
		double turnaround = 0;
		double finishTime = 0;
		int nextSector = 0;
		double[][] times = new double[requests.length][2]; //stores turnaround time and finish time for each request
		
		for(int i = 0; i < requests.length; i++){
			//find time to next track
			T = Math.abs(curTrack - requests[i][1]);
			turnaround = 10 + 0.1 * T;
			//find time to next sector
			nextSector = requests[i][2];
			if(nextSector > curSector){
				turnaround += nextSector - curSector;
			} else if (nextSector < curSector){
				turnaround += 8 - curSector + nextSector;
			}
			finishTime += turnaround;
			times[i][0] = turnaround;
			times[i][1] = finishTime;
			curTrack = requests[i][1];
			curSector = requests[i][2];
		}
		
		stats("FCFS", times);
	}
	
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
	
	protected static void stats(String name, double[][] times){
		double avgTurnaround = 0;
		double avgFinish = 0;
		for(int i = 0; i < times.length; i++){
			avgTurnaround += times[i][0];
			avgFinish += times[i][1];
		}
		avgTurnaround /= times.length;
		avgFinish /= times.length;
		
		System.out.println(name + " average turnaround time: " + avgTurnaround);
		System.out.println(name + " average finish time: " + avgFinish);
	}
}
