import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

public class TemperatureSensorServer extends UnicastRemoteObject implements
		TemperatureSensor, Runnable {

	private volatile double temp;
	private ArrayList<TemperatureListener> list = new ArrayList<TemperatureListener>();

	
	//Set the initial temprature to 98.0 using thr constructor for the server
	public TemperatureSensorServer() throws java.rmi.RemoteException {
		temp = 98.0;
	}

	//Getter for the temparature
	public double getTemperature() throws java.rmi.RemoteException {
		return temp;
	}

	//Adding a listner to the list
	public void addTemperatureListener(TemperatureListener listener)
			throws java.rmi.RemoteException {
		System.out.println("adding listener -" + listener);
		list.add(listener);
	} 

	//Removing a listner from the list
	public void removeTemperatureListener(TemperatureListener listener)
			throws java.rmi.RemoteException {
		System.out.println("removing listener -" + listener);
		list.remove(listener);
	}

	//Method use to change the temparature randomly 
	public void run() {
		Random r = new Random();
		for (;;) {
			try {
				// Sleep for a random amount of time
				int duration = r.nextInt() % 10000 + 200;
				// Check to see if negative, if so, reverse
				if (duration < 0) {
					duration = duration * -1;
					Thread.sleep(duration);
				}
			} catch (InterruptedException ie) {
			}

			// Get a number, to see if temp goes up or down
			int num = r.nextInt();
			if (num < 0) {
				temp += 0.5;
			} else {
				temp -= 0.5;
			}

			// Notify registered listeners
			notifyListeners();
		}
	}

	private void notifyListeners() {
		// TO DO: Notify every listener in the registered list if there is a change in the temperature
		try{
			for(TemperatureListener listener : list){
				listener.temperatureChanged(temp);
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

	}

	public static void main(String[] args) {

	   System.setProperty("java.security.policy", "file:allowall.policy");
 

		System.out.println("Loading temperature service");

		try {
			TemperatureSensorServer sensor = new TemperatureSensorServer();
			String registry = "localhost";

			String registration = "rmi://" + registry + "/TemperatureSensor";

			//Binding the sensor to the registry
			Naming.rebind(registration, sensor);

			//Thread to run the sensor
			Thread thread = new Thread(sensor);

			//Starting of the thread
			thread.start();
			  
		} catch (RemoteException re) {
			System.err.println("Remote Error - " + re);
		} catch (Exception e) {
			System.err.println("Error - " + e);
		}

	}

}