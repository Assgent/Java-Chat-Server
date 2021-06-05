//Server

package networking;

import java.util.*;

import utilities.*;

/**
 * A class which defines the communication protocol between Server and Client.
 */
public class Packet 
{
	public static final Byte[] HEADER = { 1, 21, 0, 22, 68, 47, 64, 127 };
	public static final Byte[] SPACER = { 22, 12, 0, 5 };
	public static final Byte[] FOOTER = { 126, 6, 15, 18, 22, 72, 0, 4 };
	
	public static final Byte ENDING_BYTE = FOOTER[FOOTER.length - 1];
	
	public enum Type
	{
		MESSAGE,
		ENCRYPTED_MESSAGE,
		DISCONNECT_FROM_SERVER
	}
	
	public static Byte[] getPacketTypeInBytes(Type type)
	{
		switch (type)
		{
			case MESSAGE:
				return new Byte[] {2, 7, 14, 16, 22};
			case ENCRYPTED_MESSAGE:
				return new Byte[] {5, 16, 6, 2, 112};
			case DISCONNECT_FROM_SERVER:
				return new Byte[] {17, 1, 3, 15, 126};
		}
		
		return null;
	}
	
	public static Packet getDisconnectionPacket()
	{
		Packet packet = new Packet();
		
		packet.appendArrayToList(HEADER);
		packet.appendArrayToList(getPacketTypeInBytes(Type.DISCONNECT_FROM_SERVER));
		packet.appendArrayToList(FOOTER);
		
		return packet;
	}
	
	//============================================================================
	
	private List<Byte> packetBytes;
	
	/**
	 * Creates a new, empty Packet 
	 */
	public Packet()
	{
		packetBytes = new ArrayList<Byte>();
	}
	
	/**
	 * Creates a new Packet from a String
	 */
	public Packet(String message)
	{
		packetBytes = new ArrayList<Byte>();
		
		//==================================================== 
		appendArrayToList(HEADER);
		
		appendArrayToList(getPacketTypeInBytes(Type.MESSAGE)); //Packet type
		
		appendArrayToList(SPACER);
		
		for (byte b : message.getBytes()) //Message
			packetBytes.add((Byte)b);
		
		appendArrayToList(FOOTER);
		//====================================================
	}	
	
	/**
	 * Creates a new Packet from the byte array representation of a Packet
	 */
	public Packet(byte[] data)
	{
		packetBytes = Utils.byteArrayToList(data);
	}
	
	/**
	 * Creates a new Packet from the List representation of a Packet
	 */
	public Packet(List<Byte> data)
	{
		packetBytes = new ArrayList<Byte>(data);
	}
	
	//--------
	
	public void appendArrayToList(Byte[] bytes)
	{
		packetBytes.addAll(Arrays.asList(bytes));
	}
	
	//--------
	
	/**
	 * Returns the Packet length
	 */
	public int length()
	{
		return packetBytes.size();
	}
	
	/**
	 * Checks if the Packet is legitimate. If it is, the function returns "true".
	 */
	public boolean isValid()
	{
		Object[] packetHeader = Arrays.copyOfRange(packetBytes.toArray(), 0, 8);
		Object[] packetFooter = Arrays.copyOfRange(packetBytes.toArray(), packetBytes.size() - 8, packetBytes.size());
		
		return Arrays.equals(packetHeader, (Object[])HEADER) && Arrays.equals(packetFooter, (Object[])FOOTER);
	}
	
	/**
	 * Finds & returns the packet type
	 */
	public Type getType()
	{
		Object[] encodedType = Arrays.copyOfRange(packetBytes.toArray(), 8, 13);
		
		for (Type type : Type.values())
		{
			if (Arrays.equals(encodedType, getPacketTypeInBytes(type)))
				return type;
		}
		
		return null;
	}
	
	/**
	 * Returns a byte[] representation of the packet
	 */
	public byte[] getBytes() 
	{
		return Utils.byteListToArray(packetBytes);
	}
	
	/**
	 * Finds & returns a String which is encoded within the packet, if available. If the Packet contains an encrypted String, the method will attempt to decrypt the string. 
	 * 
	 * Returns null if no message is found.
	 */
	@Override
	public String toString()
	{
		Type type = getType();
		
		if (type == Type.MESSAGE)
		{
			return new String(Utils.arrayToByteArray(Arrays.copyOfRange(packetBytes.toArray(), 17, packetBytes.size() - 8)));
		}
		else if (type == Type.ENCRYPTED_MESSAGE)
		{
			return String.format("[Indecipherable message of length %d bytes]", length());
		}
		
		return null;
	}
}
