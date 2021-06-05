//Server

package networking;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class which listens for new, incoming raw data and returns it as individual packets.
 */
public class PacketReader implements Iterator<Packet>, Runnable
{
	private final Queue<Packet> packets = new LinkedBlockingDeque<Packet>(); //Thread safe LinkedList
	
	private DataInputStream in;
	private boolean stop = false;
	
	public PacketReader(DataInputStream stream)
	{
		in = stream;
	}
	
	//================
	
	@Override
	public boolean hasNext() 
	{
		return packets.size() > 0;
	}

	@Override
	public Packet next() 
	{
		return packets.poll();
	}
	
	//================
	
	public void stop()
	{
		stop = true;
	}

	@Override
	public void run()
	{
		try
		{
			while (!stop)
			{
				Byte b = (byte)in.read();
				
				if (b != -1)
				{
					List<Byte> tmpList = new ArrayList<Byte>();
					
					tmpList.add(b);
					
					while (true)
					{
						b = (byte)in.read();
						
						if (b == -1 || b == Packet.ENDING_BYTE) 
						{
							tmpList.add(b);
							break;
						}
						
						tmpList.add(b);
					}
					
					Packet newPacket = new Packet(tmpList);
					
					if (newPacket.isValid())
						packets.add(newPacket);
				}
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.toString());
		}
	}

}
