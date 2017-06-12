package threads;

import httpServer.ApiAiListener;
import hueController.Hue;
import hueController.HueException;
import zWaveController.ZWave;

public class ComfortControl extends Thread 
{
	private final float COMFORT_TEMPERATURE = 26;	//gradi
	private final float TOOHOT_TEMPERATURE = 27;	//gradi
	private final int FREQUENCY = 108000;
	private final float DELTA = 1;
	private final float MIN_CONFORT_LUX = 250;
	private final float MAX_CONFORT_LUX = 350;

	
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
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			// SALVA INTENSITA' DELLA LUCE DA SETTARE DOPO
			float lux = Float.parseFloat(ZWave.getLuminosity(ApiAiListener.sensorNodeId).split(" ")[0]);
			  //if((lux < MAX_CONFORT_LUX) && (lux > MIN_CONFORT_LUX))
			
			
			
			
			
			

			// CONTROLLA TEMPERATURA
			String temperature = ZWave.getTemperature(ApiAiListener.sensorNodeId);
			float tempNumb= Float.parseFloat(temperature.substring(0, temperature.length()-3));
			
			if(tempNumb < COMFORT_TEMPERATURE)
			{
				try 
				{
					//accende la presa della stufetta solo se non Ã¨ giÃ  accesa
					float power = Float.parseFloat(ZWave.getPower(ApiAiListener.plugNodeId));

					//se la potenza misurata Ã¨ circa 0, la stufa non Ã¨ ancora accesa quindi la accende
					if(Math.abs(power - 0) <= DELTA)
					{
						ZWave.plugOn(ApiAiListener.plugNodeId);
					}

					//regola intensità della luce solo se si è in auto_mode e se c'è una luce accesa

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