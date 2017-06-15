package controlThreads;

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
			String temperature = ZWave.getTemperature();
			float tempNumb= Float.parseFloat(temperature.substring(0, temperature.length()-3));
			
			//temperatura troppo bassa
			if(tempNumb < (COMFORT_TEMPERATURE - DELTA_GRADES))
			{
				try 
				{
					//accende la presa della stufetta solo se non � gi� accesa
					plugPower = Float.parseFloat(ZWave.getPower());

					//se la potenza misurata è circa 0, la stufa non è ancora accesa quindi la accende
					if(plugPower <= 0)
					{
						ZWave.plugOn();
					}

					//regola intensit� della luce solo se si � in auto_mode e se c'� una luce accesa
					if(LightControl.autoMode && Hue.isOn)
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
					//accende la presa della stufetta solo se non è già accesa
					plugPower = Float.parseFloat(ZWave.getPower());

					//se la potenza misurata � < 0, la stufa � gi� spenta
					if(plugPower >= 0)
					{
						ZWave.plugOff();
					}
					
					if(LightControl.autoMode && Hue.isOn)
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
					if(LightControl.autoMode && Hue.isOn)
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
		//prende la luce togliendo unità di misura
		float lux = Float.parseFloat(ZWave.getLuminosity().split(" ")[0]);
		
		return 0;
	}
}