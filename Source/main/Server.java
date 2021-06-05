package main;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import utilities.*;
import networking.*;

public class Server implements Runnable
{
	private String IP;
	private int port;
	
	private boolean stop = false;
	
	private Set<User> users = ConcurrentHashMap.newKeySet(); //Thread-safe HashSet
	
	/**
	 * Function that searches for and removes inactive User threads (likely from disconnected Users)
	 */
	private final TimerTask removeDeadThreads = new TimerTask() {
		@Override
		public void run() 
		{
			Log.Write(Log.Type.INFO, "User Thread Remover", "Searching for inactive User threads...");
			
			int rmCt = 0;
			
			for (Iterator<User> i = users.iterator(); i.hasNext();) 
			{
			    User user = i.next();
			    
			    if (!user.isAlive()) 
			    {
			        users.remove(user);
			        
			        Log.Write(Log.Type.INFO, "User Thread Remover", String.format("Removing inactive User thread: %s", user.getId()));
			        
			        rmCt++;
			    }
			}
			
			Log.Write(Log.Type.INFO, "User Thread Remover", String.format("Removed %d users.", rmCt));
		}
	};
	
	public Server(String IPin, int portIn)
	{
		IP = IPin;
		port = portIn;
	}
	
	public void shutdown()
	{
		stop = true;
	}
	
	public void releaseUsers()
	{
		for (Iterator<User> i = users.iterator(); i.hasNext();) 
		{
			User user = i.next();
			
			user.disconnectUser("Server shutdown");
			
			users.remove(user);
		}
	}

	@Override
	public void run() 
	{
		try (ServerSocket serverSocket = new ServerSocket(port)) 
		{
			Utils.printDivider();
            System.out.println(String.format("This server is now listening on: \"%s\"", IP + ':' + port));
            Utils.printDivider();
            
            (new Timer()).schedule(removeDeadThreads, 20 * 1000, 40 * 1000); //Run function that deletes dead threads every 40 seconds
 
            while (!stop) 
            {
                Socket userSocket = serverSocket.accept();
                System.out.println(String.format("New user connected from IP: \"%s\"", userSocket.getInetAddress().toString()));
 
                User newUser = new User(userSocket, this);
                users.add(newUser);
                newUser.start();
            }
 
            Log.Write(Log.Type.INFO, "Server", "This server is shutting down.");
            
            releaseUsers();
        } 
		catch (Exception ex) 
		{
			Log.Write(Log.Type.WARNING, "Server", "Server error: " + ex.getMessage());
        }
	}
	
	@Override
	public void finalize()
	{
		releaseUsers();
	}
	
	//--------
	
	public void broadcast(Packet message)
	{
		Log.Write(Log.Type.MESSAGE, "Connected User", message.toString());
		
		for (User user : users)
		{
			user.sendMessage(message);
		}
	}
}
