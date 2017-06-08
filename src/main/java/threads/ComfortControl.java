package threads;

import httpServer.ApiAiListener;
import hueController.Hue;
import hueController.HueException;
import zWaveController.ZWave;

public class ComfortControl extends Thread 
{
	private final float COMFORT_TEMPERATURE = 26;	//gradi
	private final float TOOHOT_TEMPERATURE = 27;	//gradi
	private final int FREQUENCY = 3600;
	
	/*
	RANGE DI LUMINOSITA':
	soggiorno: 150 - 200 lux
	cucina: 200 - 250 lux
	camera da letto: 100 - 150 lux
	studio: 300 lux
	bagno: 100 - 150 lux
	corridoi, scale: 50 - 100 lux
	garage, cantine, soffitte: 50 - 100 lux.
	*/
	
	@Override
	public void run()
	{
		while(true)
		{	
			try
			{
				sleep(FREQUENCY);
				Hue.lightsOff();
			}
			catch (InterruptedException | HueException e)
			{
				e.printStackTrace();
			}
			
			// CONTROLLA TEMPERATURA E DA CORRENTE ALLA PRESA DELLA STUFETTA e ACCENDE LE LUCI CON DIVERSO COLORE
			String temperature = ZWave.getTemperature(ApiAiListener.sensorNodeId);
			float tempNumb= Float.parseFloat(temperature.substring(0, temperature.length()-3));
			
			if(tempNumb < COMFORT_TEMPERATURE)
			{
				try 
				{
					ZWave.plugOn(ApiAiListener.plugNodeId);
					Hue.cold();
				} 
				catch (HueException e) 
				{
					e.printStackTrace();
				}
			}
			/*fittizia, solo per testing */
			if(tempNumb > COMFORT_TEMPERATURE && tempNumb < TOOHOT_TEMPERATURE)
			{
				try 
				{
					Hue.lightsOn();
				} 
				catch (HueException e) 
				{
					e.printStackTrace();
				}
			}
			if(tempNumb>TOOHOT_TEMPERATURE)
			{
				try 
				{
					Hue.hot();
					ZWave.plugOff(ApiAiListener.plugNodeId);
				} 
				catch (HueException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}

			// CONTROLLA LUMINOSTA' E USA FUNZIONI HUE
		/*	String light = ZWave.getLuminosity(ApiAiListener.sensorNodeId);
			int lightInt= Integer.parseInt(light.split(" ")[0]);
			
			if(Hue.on=false)
			{
				if(lightInt < boh)
					Hue.lightsOn();
			}
			else
			{
				if(lightInt < numero)
					Hue.lightsPower(percentage);
			}
				if(lightInt < 3)
					Hue.lightsOn();
			}
			*/
			

	/*	}
	}
}*/