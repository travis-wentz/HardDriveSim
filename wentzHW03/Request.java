package wentzHW03;

public class Request {
	private int id;
	private int arrival;
	private int track;
	private int sector;
	private boolean inBuffer = false;
	
	public Request(int id, int arrival, int track, int sector){
		this.id = id;
		this.arrival = arrival;
		this.track = track;
		this.sector = sector;
	}
	
	protected int getId(){
		return id;
	}
	
	protected int getArrival(){
		return arrival;
	}
	
	protected int getTrack(){
		return track;
	}
	
	protected int getSector(){
		return sector;
	}
	
	protected boolean inBuffer(){
		return inBuffer;
	}
	
	protected void addToBuffer(){
		inBuffer = true;
	}
}
