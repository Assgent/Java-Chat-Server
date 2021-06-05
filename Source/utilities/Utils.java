//Server

package utilities;

import java.text.*;
import java.util.*;

public class Utils 
{
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
	
	//====================================================================================================
	
	/**
	 * Displays a critical error in the console and exits the program
	*/
	public static void showCriticalError(Exception e)
    {
		System.out.print(String.format("%s\n\nThis program will now exit.", e.toString()));
		System.exit(1);
    }
	
	//====================================================================================================
	
	/**
	 * Prints a horizontal line.
	*/
	public static void printDivider()
    {
		System.out.println("======================================================");
    }
	
	//====================================================================================================
	
	/**
	 * Converts a List<Byte> to a byte[].
	*/
	public static byte[] byteListToArray(List<Byte> byteList) //Why is there no native support for this???
    {
		int size = byteList.size(); 
		
		byte[] byteArray = new byte[size];
		
		for (int i = 0; i < size; i++)
		{
			byteArray[i] = byteList.get(i);
		}
		
		return byteArray;
    }
	
	/**
	 * Converts a byte[] to a List<Byte>.
	*/
	public static List<Byte> byteArrayToList(byte[] byteArray) //Why is there no native support for this???
    {
		ArrayList<Byte> byteList = new ArrayList<Byte>();
		
		for (byte b : byteArray)
		{
			byteList.add(b);
		}
		
		return byteList;
    }
	
	/**
	 * Converts a Byte[] to a byte[].
	*/
	public static byte[] arrayToByteArray(Object[] array) //Why is there no native support for this???
	{
		int size = array.length; 
		
		byte[] byteArray = new byte[size];
		
		for (int i = 0; i < size; i++)
		{
			byteArray[i] = (Byte)array[i];
		}
		
		return byteArray;
	}
}
