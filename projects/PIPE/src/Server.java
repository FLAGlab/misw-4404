/**
 * Server
 * 
 * This server accepts clients on the port entered as an argument.
 * When a connection is made, a new thread (ServerAction) is spawned 
 * to handle the request so that this class can accept more concurrent 
 * connections 
 * 
 * @author Barry Kearns
 * @date July 2007
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import pipe.common.AnalysisType;
import pipe.common.LoggingHelper;
import pipe.server.CommunicationsManager;
import pipe.server.performancequery.ClientUpdater;
import pipe.server.performancequery.PerformanceQueryServerAction;
import pipe.server.serverCommon.PathsWrapper;
import pipe.server.serverCommon.ServerAction;

public class Server
{
	private static Logger							logger					= Logger.getLogger(Server.class.getName());

	private static ServerSocketChannel				server					= null;
	private static int								maxProcessors			= 24;
	private static final int						noOfAllowedConnections	= 100;
	
	private PathsWrapper							paths;

	private static HashMap<InetAddress, Integer>	sockets					= new HashMap<InetAddress, Integer>();

	public static void main(final String[] args)
	{
		int portNo = 12345;

		// Load the paths to the working directory and the tools
		PathsWrapper paths = new PathsWrapper();

		// each client is assigned a number so multiple client files operate
		// separately
		int clientNo = 0;

		// accept a port number as an argument
		if (args.length == 1)
			portNo = Integer.parseInt(args[0]);

		try
		{
			String loggingFile = paths.getWorkPath() + System.getProperty("file.separator") +
									"javaServer%u.log";

			LoggingHelper.setupFileLogging(loggingFile, Server.logger, Level.INFO);
			Server.logger.setLevel(Level.INFO);
			Server.logger.log(Level.INFO, "Logger log file up");
		}
		catch (IOException e)
		{
			Server.logger.log(Level.WARNING, "Couldn't set up Logging");
			Server.logger.log(Level.WARNING, LoggingHelper.getStackTrace(e));
		}
		catch (SecurityException e)
		{
			Server.logger.log(Level.WARNING, LoggingHelper.getStackTrace(e));
		}
		try
		{
			// start server process
			Server.server = ServerSocketChannel.open();
			CommunicationsManager.safeBind(Server.server, portNo, "Server primary connection"); 
			Server.logger.log(Level.INFO, "Server is running on port " + portNo);
			
			// initialise client updater
			CommunicationsManager.clientUpdater = new ClientUpdater();
			int clientUpdaterPort = CommunicationsManager.clientUpdater.getPort();
			Server.logger.log(Level.INFO, "Client updater is running on port " + clientUpdaterPort);

			while (true)
			{
				SocketChannel clientConnection = null;
				try
				{
					// keep running and accept requests continuously
					clientConnection = Server.server.accept();
					if (Server.isAllowed(clientConnection))
					{

						// spawn a thread to handle each individual request
						Server.logger.log(Level.INFO, "Incoming client request accepted");
						Server.logger.log(	Level.INFO,
											"Spawning a ServerAction thread to deal with the request");

						ObjectInputStream objectInput = new ObjectInputStream(Channels.newInputStream(clientConnection));
						AnalysisType analysisRequested = (AnalysisType) objectInput.readObject();

						Server.logger.log(Level.INFO, analysisRequested +
														" analysis thread being initialised");

						ServerAction processRequest;
						switch (analysisRequested)
						{
							case PASSAGETIME :
							case STEADYSTATE :
							{
								processRequest = new ServerAction(	objectInput,
																	analysisRequested,
																	clientConnection.socket(),
																	clientNo++,
																	paths,
																	Server.sockets);
								break;
							}
							case PERFORMANCEQUERY :
							{
								processRequest = new PerformanceQueryServerAction(	objectInput,
																					clientConnection,
																					clientNo++,
																					paths,
																					Server.sockets);
								break;
							}
							default :
							{
								throw new UnexpectedException("Unsupported AnalysisType Requested");
							}
						}
						String uniqueName = String.valueOf(clientNo);
						Thread requestProcessingThread = new Thread(new ThreadGroup(uniqueName),
																	processRequest);
						requestProcessingThread.start();
						Server.logger.info("ServerAction thread started in thread group " + uniqueName);
					}
					else
					{
						Server.logger.log(	Level.WARNING,
											"Client connection attempted for already connected client");
						clientConnection.close();
					}
				}
				catch (UnexpectedException e)
				{
					Server.logger.log(	Level.WARNING,
										"Analysis type can only be PassageTime, SteadyState, or PerformanceQuery");
					if (clientConnection != null)
						clientConnection.close();
				}
				catch (IOException ioe)
				{
					Server.logger.log(Level.WARNING, "Could not accept client connection on " + portNo, ioe);
					if (clientConnection != null)
						clientConnection.close();
				}
				catch (ClassNotFoundException e)
				{
					Server.logger.log(Level.WARNING, "Unexpected class recieved: ", e);
					if (clientConnection != null)
						clientConnection.close();
				}
			}
		}
		catch (IOException ioe)
		{
			Server.logger.log(Level.WARNING, "Could not listen on port: " + portNo, ioe);
			System.exit(1);
		}
		finally
		{
			for (Handler h : Server.logger.getHandlers())
			{
				h.close();
			}
		}
	}

	private static boolean isAllowed(final SocketChannel connection)
	{
		InetAddress address = connection.socket().getInetAddress();
		Integer noOfConnections = Server.sockets.get(address);
		if (noOfConnections == null)
		{
			noOfConnections = 0;
		}
		if (noOfConnections < Server.noOfAllowedConnections)
		{
			Server.sockets.put(address, ++noOfConnections);
			return true;
		}
		return false;
	}
	
	

	@Override
	protected void finalize() throws Throwable
	{
		Server.server.close();
		super.finalize();
	}
}
