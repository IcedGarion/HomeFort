package threads;

import httpServer.ApiAiListener;
import hueController.Hue;
import hueController.HueException;
import zWaveController.ZWave;

public class ComfortControl extends Thread 
{
	private final float COMFORT_TEMPERATURE = 26;
	private final int FREQUENCY = 3000/*108000*/;
	private final float DELTA_GRADES = 1;
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
		int lightPower;
		float plugPower;
		
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
			lightPower = computeBestPower();  

			// CONTROLLA TEMPERATURA
			String temperature = ZWave.getTemperature(ApiAiListener.sensorNodeId);
			float tempNumb= Float.parseFloat(temperature.substring(0, temperature.length()-3));
			
			//temperatura troppo bassa
			if(tempNumb < (COMFORT_TEMPERATURE - DELTA_GRADES))
			{
				try 
				{
					//accende la presa della stufetta solo se non Ë gi‡† accesa
					plugPower = Float.parseFloat(ZWave.getPower(ApiAiListener.plugNodeId));

					//se la potenza misurata √® circa 0, la stufa non √® ancora accesa quindi la accende
					if(plugPower <= 0)
					{
						ZWave.plugOn(ApiAiListener.plugNodeId);
					}

					//regola intensit‡ della luce solo se si Ë in auto_mode e se c'Ë una luce accesa
					if(ApiAiListener.autoMode && Hue.isOn)
					{
						//POWER CALCOLATO PRIMA! Hue.lightsPower(lightPower);
						Hue.cold();
					}
				} 
				catch (HueException e) 
				{
					e.printStackTrace();
				}
			}
			//temperatura troppo alta
			else if(tempNumb > (COMFORT_TEMPERATURE + DELTA_GRADES))
			{
				try 
				{
					//accende la presa della stufetta solo se non √® gi√† accesa
					plugPower = Float.parseFloat(ZWave.getPower(ApiAiListener.plugNodeId));

					//se la potenza misurata Ë < 0, la stufa Ë gi‡ spenta
					if(plugPower >= 0)
					{
						ZWave.plugOff(ApiAiListener.plugNodeId);
					}
					
					if(ApiAiListener.autoMode && Hue.isOn)
					{
						//POWER CALCOLATO PRIMA! Hue.lightsPower(lightPower);
						Hue.hot();
					}
				} 
				catch (HueException e) 
				{
					e.printStackTrace();
				}
			}
			//temperatura ok
			else
			{
				try 
				{
					if(ApiAiListener.autoMode && Hue.isOn)
					{
						//Hue.LightsPower(lightsPower);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public int computeBestPower()
	{
		float lux = Float.parseFloat(ZWave.getLuminosity(ApiAiListener.sensorNodeId).split(" ")[0]);
		
		return 0;
	}
}