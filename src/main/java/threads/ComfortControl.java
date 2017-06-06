package threads;

import httpServer.ApiAiListener;
import hueController.Hue;
import zWaveController.ZWave;

public class ComfortControl extends Thread 
{
	private final float COMFORT_TEMPERATURE = 26;
	private final int FREQUENCY = 1000;
	
	@Override
	public void run()
	{
		while(true)
		{	
			try
			{
				sleep(FREQUENCY);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			// CONTROLLA TEMPERATURA E DA CORRENTE ALLA PRESA DELLA STUFETTA
			String temperature = ZWave.getTemperature(ApiAiListener.sensorNodeId);
			float tempNumb= Float.parseFloat(temperature.substring(0, temperature.length()-2));
			
			if(tempNumb < COMFORT_TEMPERATURE)
			{
				ZWave.plugOn(ApiAiListener.plugNodeId);
				//Hue.cold();
			}
			else
				ZWave.plugOff(ApiAiListener.plugNodeId);
			
			/*if(tempNumb>30)
			{
				try 
				{
					Hue.hot();
				} 
				catch (HueException e) 
				{
					e.printStackTrace();
				}
			}*/
			
			/*
			// CONTROLLA LUMINOSTA' E USA FUNZIONI HUE
			String light = ZWave.getLight(ApiAiListener.sensorNodeId);
			int lightInt= Integer.parseInt(light.split(" ")[0]);
			
			if(Hue.on=false)
			{
				if(lightInt < 3)
					Hue.lightsOn();
			}
			*/
		}
	}
}