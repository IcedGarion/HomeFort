package threads;

import httpServer.ApiAiListener;
import hueController.Hue;
import zWaveController.ZWave;
/*
public class ComfortControl extends Thread 
{
	public ComfortControl()
	{}
	
	@Override
	public void run()
	{
		while(true)
		{	
			// CONTROLLA TEMPERATURA E DA CORRENTE ALLA PRESA DELLA STUFETTA
			String temperature = ZWave.getTemperature(ApiAiListener.sensorNodeId);
			float tempNumb= Integer.parseInt(temperature.substring(0, temperature.length()-2));
			
			if(tempNumb < 26)
			{
				//funzione del garion
			}
			
			// CONTROLLA LUMINOSTA' E USA FUNZIONI HUE
			String light = ZWave.getLight(ApiAiListener.sensorNodeId);
			int lightInt= Integer.parseInt(light.split(" ")[0]);
			
			if(Hue.on=false)
			{
				/*if(lightInt < /*boh)
				/*	Hue.lightsOn();
			}
			/*else
			{
				if(lightInt < /*numero*//*)/*
					Hue.lightsPower(percentage);
			}
		}
	}
}*/
