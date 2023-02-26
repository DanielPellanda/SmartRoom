package room.service.channel;

/**
 * Simple interface for an async msg communication channel
 * @author aricci
 *
 */
public interface CommChannel {
	
	void start();
	
	void close();
	
	void send(String msg);

}