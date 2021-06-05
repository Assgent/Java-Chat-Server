package utilities;

import java.util.*;

public class Log 
{
	public enum Type
	{
		WARNING,
		INFO,
		MESSAGE
	}
	
	public static String getLogTypeAsString(Type type)
	{
		switch (type)
		{
			case WARNING:
				return "WARN";
			case INFO:
				return "INFO";
			case MESSAGE:
				return "MESSAGE";
		}
		
		return null;
	}
	
	/**
	 * Writes a log to the System PrintStream with the current date, time, and some details
	 */
	public static void Write(Type type, String source, String message)
	{
		System.out.println(String.format("[%s]-[%s | %s] %s", getLogTypeAsString(type), source, Utils.dateFormatter.format(new Date(System.currentTimeMillis())), message));
	}
}
