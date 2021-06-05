//Server

package main;

import settings.*;
import utilities.*;

import java.io.*;
import java.util.*;

public class Main 
{
	public static final BufferedReader stdIn = new BufferedReader(new
	        InputStreamReader(System.in));
	public static final Settings settings = new Settings(Settings.FILENAME);
	
	public static void main(String[] args) 
	{
		try
		{
			//=================================================== Loads the server IP/domain and connection port from a settings file. Will also prompt the user for missing settings
			
			Map<String, String> currentSettings = settings.read();
			
			String tmp = currentSettings.get(Settings.IP);
			
			String IP = tmp.equals("null") ? getMissingSetting(Settings.IP) : tmp;
			currentSettings.put(Settings.IP, IP);
			
			tmp = currentSettings.get(Settings.PORT);
			
			String port = tmp.equals("null") ? getMissingSetting(Settings.PORT) : tmp;
			currentSettings.put(Settings.PORT, port);
			
			settings.write(currentSettings);
			
			//===================================================
			
			Server server = new Server(IP, Integer.parseInt(port)); //Instantiates the server with the specified parameters
			
			Runtime.getRuntime().addShutdownHook(new Thread()
		    {
				@Override
		    	public void run()
		    	{
		    		Log.Write(Log.Type.INFO, "Main", "Shutting down program... Releasing all connections.");
		    		
		    		server.releaseUsers();
		    	}
		    });
			
			server.run();
		}
		catch (Exception e)
		{
			Utils.showCriticalError(e);
		}
	}
	
	private static String getMissingSetting(String setting) throws IOException
	{
		System.out.print(String.format("Enter the server %s: ", setting));
		return stdIn.readLine();
	}
}
