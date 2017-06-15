package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Writer extends Thread
{
	public final static String LIGHTS_TIMES_FILE = "resources/lightsTimes";
	private boolean reset;
	private String start, end;
	private PrintWriter writer;
	
	public void reset(boolean reset)
	{
		this.reset = reset;
	}
	
	public void setData(String start, String end)
	{
		this.start = start;
		this.end = end;
	}
	
	@Override
	public void run()
	{
		try
		{
			if(reset)
			{
				writer = new PrintWriter(LIGHTS_TIMES_FILE);
				writer.write("");
				writer.close();
				
				return;
			}
			
			//trasforma inizio e fine in milisecondi, a partire da 00:00 e salva i due valori in un file
			long startMillisec, endMillisec;
			writer = new PrintWriter(new BufferedWriter(new FileWriter(LIGHTS_TIMES_FILE, true)));
			String tmpStart[] = start.split(":");
			String tmpEnd[] = end.split(":");
			long startHour = Long.parseLong(tmpStart[0]);
			long startMin = Long.parseLong(tmpStart[1]);
			long endHour = Long.parseLong(tmpEnd[0]);
			long endMin = Long.parseLong(tmpEnd[1]);
			
			startMillisec = (startMin * 60) + (startHour * 60 * 60);
			endMillisec = (endMin * 60) + (endHour * 60 * 60);
			
			if(endMillisec <= startMillisec)
			{
				long tmp = startMillisec;
				startMillisec = endMillisec;
				endMillisec = tmp;
			}
			
			writer.println(startMillisec + ", " + endMillisec);
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
}
