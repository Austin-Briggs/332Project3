/**
 * Austin Briggs and Nick Evans
 * CSE 332 AB
 * Project 3B
 * 
 * Lock is used to prevent multiple thread access to one resource
 */
public class Lock {
	private boolean isLocked = false;
	
	//acquire the lock
	public synchronized void lock() {
		//wait for the lock to be released and then acquire it
		while (isLocked)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		isLocked = true;
	}
	
	//release the lock
	public synchronized void unlock() {
		//release the lock and notify that the lock is available 
		isLocked = false;
		notify();
	}
}
