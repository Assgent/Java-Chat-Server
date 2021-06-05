//Server

package settings;

import java.io.*;
import java.util.*;

import utilities.Utils;

public class Settings 
{
	public static final String FILENAME = "settings.txt";
	
	public static final String IP = "IP";
	public static final String PORT = "Port";
	
	//============================================================================
	
	private File file;
	
	public Settings(String filename)
	{
		file = new File(filename);
		
		if (!file.exists())
			createNewSettingsFile();
	}
	
	//--------
	
	/**
	 * Creates a new settings file with default settings
	 */
	public void createNewSettingsFile()
	{
		try 
		{
			file.delete();
			file.createNewFile();
			
			HashMap<String, String> newSettings = new HashMap<String, String>();
			newSettings.put(Settings.IP, "null");
			newSettings.put(Settings.PORT, "null");
			
			write(newSettings);
		} 
		catch (Exception e) 
		{
			Utils.showCriticalError(e);
		}
	}
	
	/**
	 * Reads all settings and returns them as a Map
	 */
	public Map<String, String> read() throws Exception
	{
		Map<String, String> settings = new HashMap<String, String>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.charAt(0) != '#')
				{
					String[] tokens = line.split(":");
					settings.put(tokens[0], tokens[1]);
				}
			}
		} 
		
		return settings;
	}
	
	/**
	 * Writes a map into the settings file. This function overwrites any existing content in the file.
	 */
	public void write(Map<String, String> newSettings) throws Exception
	{
		try (PrintWriter writer = new PrintWriter(new FileWriter(file)))
		{
			for (Map.Entry<String, String> entry : newSettings.entrySet())
			{
				writer.printf("%s:%s\n", entry.getKey(), entry.getValue());
			}
		}
	}
}
