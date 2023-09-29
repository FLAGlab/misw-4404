package model.data_structures;

public class Landing implements Comparable <Landing>
{
	private String landingId;
	
	private String id;
	
	private String name;
	
	private String pais;
	
	private double latitude;
	
	private double longitude;
	
	private String codigo; 
	
	public Landing(String plandingid, String pid, String pname, String ppais, double platitude, double plongitude)
	{
		setLandingId(plandingid);
		setId(pid);
		setName(pname);
		setPais(ppais);
		setLatitude(platitude);
		setLongitude(plongitude);
		setCodigo("");
	}

	public String getLandingId() {
		return landingId;
	}

	public void setLandingId(String landingId) {
		this.landingId = landingId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public int compareTo(Landing o) 
	{
		System.out.print("holis");
		return 0;
	}

	public String getPais() 
	{
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
