package pipe.modules.clientCommon;

public class ServerInfo
{
	protected String address;
	protected int port;
	
	public ServerInfo(String address, int port)
	{
		this.address = address;
		this.port = port;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public int getPort()
	{
		return port; 
	}
}