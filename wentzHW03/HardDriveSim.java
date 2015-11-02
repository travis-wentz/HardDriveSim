package wentzHW03;

import java.util.ArrayList;

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
		double curTime = 0;
		double trackTime = 0;
		double sectorTime = 0;
		double turnaround = 0;
		double finishTime = 0;
		double[][] times = new double[requests.length][2]; //stores turnaround time and finish time for each request
		
		curTime = requests[0][0]; //waiting for the first job
		for(int i = 0; i < requests.length; i++){
			//find time to next track
			trackTime = 10 + 0.1 * (Math.abs(curTrack - requests[i][1]));
			//find time to next sector
			if(requests[i][2] > curSector){
				sectorTime = requests[i][2] - curSector;
			} else if (requests[i][2] < curSector){
				sectorTime = 8 - curSector + requests[i][2];
			}
			//find finish time
			if(requests[i][0] > curTime){
				finishTime = (requests[i][0] + trackTime + sectorTime);
			} else {
				finishTime = (curTime + trackTime + sectorTime);
			}
			turnaround = finishTime - requests[i][0];
			times[i][0] = turnaround;
			times[i][1] = finishTime;
			curTime = finishTime;
			curTrack = requests[i][1];
			curSector = requests[i][2];
		}
		stats("FCFS", times);
	}
	
	/*
	 * Shortest-seek-time-first finds the closest request and serves it.
	 */
	protected static void sSTF(int[][] req){
		boolean done = false;
		int finJobs = 0;
		int curTrack = 0;
		int curSector = 0;
		int winningId = 0;
		double curTime = 0;
		double trackTime = 0;
		double sectorTime = 0;
		double turnaround = 0;
		double finishTime = 0;
		ArrayList<Request> requests = new ArrayList<Request>();
		ArrayList<Request> buffer = new ArrayList<Request>();
		double[][] times = new double[req.length][2]; //stores turnaround time and finish time for each request

		//creating request objects and adding them to an array list
		for(int i = 0; i < req.length; i++){
			Request requ = new Request(i, req[i][0], req[i][1], req[i][2]);
			requests.add(i, requ);
		}
		
		curTime = req[0][0]; //waiting for first request
		while(!done){
			double bestTime = 5000000;
			//add current requests to buffer
			for(int j = 0; j < requests.size(); j++){
				if(!requests.get(j).inBuffer()){
					requests.get(j).addToBuffer();
					buffer.add(requests.get(j));
				}
			}
			
			//find closest request
			for(int j = 0; j < buffer.size(); j++){
				Request curReq = buffer.get(j);
				//find time to next track
				trackTime = 10 + 0.1 * (Math.abs(curTrack - curReq.getTrack()));
				//find time to next sector
				if(curReq.getSector() > curSector){
					sectorTime = curReq.getSector() - curSector;
				} else if (curReq.getSector() < curSector){
					sectorTime = 8 - curSector + curReq.getSector();
				}
				if(trackTime + sectorTime < bestTime){
					bestTime = trackTime + sectorTime;
					winningId = curReq.getId();
				}
			}
			finishTime = curTime + bestTime;
			turnaround = finishTime - requests.get(winningId).getArrival();
			times[finJobs][0] = turnaround;
			times[finJobs][1] = finishTime;
			curTime = finishTime;
			curTrack = requests.get(winningId).getTrack();
			curSector = requests.get(winningId).getSector();
			//delete job from buffer
			for(int j = 0; j < buffer.size(); j++){
				if(buffer.get(j).getId() == winningId){
					buffer.remove(j);
				}
			}
			finJobs++;
			if(finJobs >= requests.size()){
				done = true;
			}
		}
		stats("SSTF", times);
	}
	
	/*
	 * Look moves one direction as long as there are requests that way, then reverses when there are not
	 */
	protected static void look(int[][] req){
		boolean done = false;
		boolean movingRight = true;
		int finJobs = 0;
		int curTrack = 0;
		int curSector = 0;
		int winningId = 0;
		double curTime = 0;
		double trackTime = 0;
		double sectorTime = 0;
		double turnaround = 0;
		double finishTime = 0;
		Request curReq;
		ArrayList<Request> requests = new ArrayList<Request>();
		ArrayList<Request> buffer = new ArrayList<Request>();
		double[][] times = new double[req.length][2]; //stores turnaround time and finish time for each request

		//creating request objects and adding them to an array list
		for(int i = 0; i < req.length; i++){
			Request requ = new Request(i, req[i][0], req[i][1], req[i][2]);
			requests.add(i, requ);
		}
		
		curTime = req[0][0]; //waiting for first request
		while(!done){
			//add current requests to buffer
			for(int j = 0; j < requests.size(); j++){
				if(!requests.get(j).inBuffer()){
					requests.get(j).addToBuffer();
					buffer.add(requests.get(j));
				}
			}
			//decide direction
			if(movingRight){
				boolean keepRight = false;
				for(int j = 0; j < buffer.size(); j++){
					if(buffer.get(j).getTrack() >= curTrack){
						keepRight = true;
					}
				}
				if(!keepRight){
					movingRight = false;
				}
			} else {
				boolean keepLeft = false;
				for(int j = 0; j < buffer.size(); j++){
					if(buffer.get(j).getTrack() <= curTrack){
						keepLeft = true;
					}
				}
				if(!keepLeft){
					movingRight = true;
				}
			}
			int trackDist = 5000000;
			//find next request in correct direction
			if(movingRight){
				for(int j = 0; j < buffer.size(); j++){
					curReq = buffer.get(j);
					//if request is to the right
					if(curReq.getTrack() > curTrack){
						//find time to next track
						if((curReq.getTrack() - curTrack) < trackDist){
							trackDist = curReq.getTrack() - curTrack;
							winningId = curReq.getId();
						}
					}
				}
			} else {
				for(int j = 0; j < buffer.size(); j++){
					curReq = buffer.get(j);
					//if request is to the left
					if(curReq.getTrack() < curTrack){
						//find time to next track
						if((curTrack - curReq.getTrack()) < trackDist){
							trackDist = curTrack - curReq.getTrack();
							winningId = curReq.getId();
						}
					}
				}
			}
			curReq = requests.get(winningId);
			//find time to next track
			trackTime = 10 + 0.1 * (Math.abs(curTrack - curReq.getTrack()));
			//find time to next sector
			if(curReq.getSector() > curSector){
				sectorTime = curReq.getSector() - curSector;
			} else if (curReq.getSector() < curSector){
				sectorTime = 8 - curSector + curReq.getSector();
			}
			finishTime = curTime + trackTime + sectorTime;
			turnaround = finishTime - curReq.getArrival();
			times[finJobs][0] = turnaround;
			times[finJobs][1] = finishTime;
			curTime = finishTime;
			curTrack = requests.get(winningId).getTrack();
			curSector = requests.get(winningId).getSector();
			//delete job from buffer
			for(int j = 0; j < buffer.size(); j++){
				if(buffer.get(j).getId() == winningId){
					buffer.remove(j);
				}
			}
			finJobs++;
			if(finJobs >= requests.size()){
				done = true;
			}
		}
		stats("LOOK", times);
	}
	
	/*
	 * Similar to look except that it only reads while moving to the right (that is, toward larger numbered tracks)
	 */
	protected static void cLook(int[][] req){
		boolean done = false;
		boolean movingRight = true;
		int finJobs = 0;
		int curTrack = 0;
		int curSector = 0;
		int winningId = 0;
		double curTime = 0;
		double trackTime = 0;
		double sectorTime = 0;
		double turnaround = 0;
		double finishTime = 0;
		Request curReq;
		ArrayList<Request> requests = new ArrayList<Request>();
		ArrayList<Request> buffer = new ArrayList<Request>();
		double[][] times = new double[req.length][2]; //stores turnaround time and finish time for each request
		
		//creating request objects and adding them to an array list
		for(int i = 0; i < req.length; i++){
			Request requ = new Request(i, req[i][0], req[i][1], req[i][2]);
			requests.add(i, requ);
		}
		
		curTime = req[0][0]; //waiting for first request
		while(!done){
			//add current requests to buffer
			for(int j = 0; j < requests.size(); j++){
				if(!requests.get(j).inBuffer()){
					requests.get(j).addToBuffer();
					buffer.add(requests.get(j));
				}
			}
			//decide direction
			if(movingRight){
				boolean keepRight = false;
				for(int j = 0; j < buffer.size(); j++){
					if(buffer.get(j).getTrack() >= curTrack){
						keepRight = true;
					}
				}
				if(!keepRight){
					movingRight = false;
				}
			}
			int trackDist = 5000000;
			//find next request in correct direction
			if(movingRight){
				for(int j = 0; j < buffer.size(); j++){
					curReq = buffer.get(j);
					//if request is to the right
					if(curReq.getTrack() > curTrack){
						//find time to next track
						if((curReq.getTrack() - curTrack) < trackDist){
							trackDist = curReq.getTrack() - curTrack;
							winningId = curReq.getId();
						}
					}
				}
			} else {
				//find smallest track
				int smTrack = 50000;
				for(int j = 0; j < buffer.size(); j++){
					curReq = buffer.get(j);
					if(curReq.getTrack() < smTrack){
						smTrack = curReq.getTrack();
						winningId = curReq.getId();
					}
				}
			}
			curReq = requests.get(winningId);
			//find time to next track
			trackTime = 10 + 0.1 * (Math.abs(curTrack - curReq.getTrack()));
			//find time to next sector
			if(curReq.getSector() > curSector){
				sectorTime = curReq.getSector() - curSector;
			} else if (curReq.getSector() < curSector){
				sectorTime = 8 - curSector + curReq.getSector();
			}
			finishTime = curTime + trackTime + sectorTime;
			turnaround = finishTime - curReq.getArrival();
			times[finJobs][0] = turnaround;
			times[finJobs][1] = finishTime;
			curTime = finishTime;
			curTrack = requests.get(winningId).getTrack();
			curSector = requests.get(winningId).getSector();
			//delete job from buffer
			for(int j = 0; j < buffer.size(); j++){
				if(buffer.get(j).getId() == winningId){
					buffer.remove(j);
				}
			}
			finJobs++;
			if(finJobs >= requests.size()){
				done = true;
			}
		}
		stats("C-LOOK", times);
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
	
	protected static void print(double[][] array){
		for(int i = 0; i < array.length; i++){
			for(int j = 0; j < array[0].length; j++){
				System.out.print(array[i][j] + "      ");
			}
			System.out.println();
		}
	}
	
	protected static void stats(String name, double[][] times){
		double avgTurnaround = 0;
		double avgFinish = 0;
		double variance = 0;
		double stdDev;
		for(int i = 0; i < times.length; i++){
			avgTurnaround += times[i][0];
			avgFinish += times[i][1];
		}
		avgTurnaround /= times.length;
		avgFinish /= times.length;
		for(int i = 0; i < times.length; i++){
			variance += Math.pow(avgTurnaround - times[i][0],2);
		}
		variance /= times.length;
		stdDev = Math.sqrt(variance);

		System.out.println("---------------------------------------------------");
		System.out.println(name + " total time: " + times[(times.length - 1)][1]);
		System.out.println(name + " average turnaround time: " + avgTurnaround);
		System.out.println(name + " average finish time: " + avgFinish);
		System.out.println(name + " variance: " + variance);
		System.out.println(name + " standard deviation: " + stdDev);
		System.out.println("---------------------------------------------------");
	}
}
