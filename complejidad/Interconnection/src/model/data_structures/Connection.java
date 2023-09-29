package model.data_structures;

public class Connection 
{
	private int origin;
	
	private int destination;
	
	private String name;
	
	private String cableid;
	
	private String length;
	
	private int rfs;
	
	private String owners;
	
	private Double capacity;
	
	public Connection(int porigin, int pdestination, String pname, String pcable, String plength, int prfs, String powners, Double pcapacity)
	{
		setOrigin(porigin);
		
		setDestination(pdestination);
		
		setName(pname);
		
		setCableid(pcable);
		
		setLength(plength);
		
		setRfs(prfs);
		
		setOwners(powners);
		
		setCapacity(pcapacity);
	}

	public int getOrigin() {
		return origin;
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public int getRfs() {
		return rfs;
	}

	public void setRfs(int rfs) {
		this.rfs = rfs;
	}

	public String getOwners() {
		return owners;
	}

	public void setOwners(String owners) {
		this.owners = owners;
	}

	public Double getCapacity() {
		return capacity;
	}

	public void setCapacity(Double capacity) {
		this.capacity = capacity;
	}

	public String getCableid() {
		return cableid;
	}

	public void setCableid(String cableid) {
		this.cableid = cableid;
	}
}
