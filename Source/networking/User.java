//Server

package networking;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import main.*;
import utilities.*;

public class User extends Thread
{
	private Socket socket;
	private Server server;
	
	private InputStream in;
	private OutputStream out;
	
	private boolean stopThread = false;
	
	public User(Socket userSocketIn, Server serverIn)
	{
		socket = userSocketIn;
		server = serverIn;
	}
	
	@Override
	public void run() 
	{
        try 
        {
            in = socket.getInputStream();
            out = socket.getOutputStream();
 
            sendMessage(new Packet("Welcome to the server!")); 
            
            recieveMessages();
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        	disconnectUser(e.toString());
        }
    }
	
	//----------
	
	private void printStatus(String message)
	{
		Log.Write(Log.Type.WARNING, String.format("User Thread %s", super.getId()), message);
	}
	
	public void stopThread(String reason)
	{
		printStatus("Stopping thread - Reason: " + reason);
		
		stopThread = true;
	}
	
	public void disconnectUser()
	{
		sendMessage(Packet.getDisconnectionPacket());
		
		try 
		{
			socket.close();
		} 
		catch (IOException e) 
		{
			//pass
		}
		
		stopThread("User disconnected.");
	}
	
	public void disconnectUser(String reason)
	{
		sendMessage(Packet.getDisconnectionPacket());
		stopThread("User disconnected - Reason: " + reason);
	}
	
	public void sendMessage(Packet message)
	{
		try 
		{
			out.write(message.getBytes());
		} 
		catch (IOException e) 
		{
			stopThread(e.toString());
		}
	}
	
	private void recieveMessages() throws RuntimeException
	{
		PacketReader reciever = new PacketReader(new DataInputStream(in));
		
		CompletableFuture.runAsync(reciever);
		
		while (!stopThread)
		{
			Packet newPacket = reciever.next();
			
			if (newPacket == null)
			{
				continue;
			}
			else if (newPacket.getType() == Packet.Type.DISCONNECT_FROM_SERVER)
			{
				reciever.stop();
				stopThread("User disconnected.");
			}
			else
			{
				server.broadcast(newPacket);
			}
		}
	}
	
	@Override
	public int hashCode()
	{
		return (int)super.getId();
	}
}
