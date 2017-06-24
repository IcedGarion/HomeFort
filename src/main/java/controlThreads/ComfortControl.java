package controlThreads;

import httpServer.ApiAiListener;
import hueController.Hue;
import zWaveController.ZWave;

public class ComfortControl extends Thread 
{
	private final float DELTA_GRADES = 1;                   /*da usare poi +- 4*/
	private final int NORMAL_FREQUENCY = 3000;				/*da usare poi 108000*/
	private final int HIGH_FREQUENCY = 500;
	private int FREQ;
	private final int POWER_STEP = 15;
	private final float MIN_COMFORT_LUX = 250;
	private final float MAX_COMFORT_LUX = 350;	
	
	@Override
	public void run()
	{
		int lightPower, currentInnerLux;
		float plugPower;
		FREQ = HIGH_FREQUENCY;
		
		while(true)
		{
			try
			{
				/* ALL'INIZIO FREQ ALTA, CAMBIA POWER PASSETTO PER PASSETTO:
				 * QUANDO ARRIVA A UN VALORE OK, FREQUENCY RALLENTA PERCHÈ A POSTO.
				 * SE POI SI ACCORGE CHE CAMBIA QUALCOSA, TORNA A ALTO PER SISTEMARE
				 */
				sleep(FREQ);
				
				// SALVA INTENSITA' DELLA LUCE DA SETTARE DOPO
				currentInnerLux = Integer.parseInt(ZWave.getLuminosity().split(" ")[0]);
				lightPower = computeBestPower(currentInnerLux);  

				// CONTROLLA TEMPERATURA
				float tempNumb= Float.parseFloat(ZWave.getTemperature().split(" ")[0]);
			
				//temperatura troppo bassa
				if(tempNumb < (ApiAiListener.COMFORT_TEMPERATURE - DELTA_GRADES))
				{
				
					//accende la presa della stufetta solo se non � gi� accesa
					plugPower = Float.parseFloat(ZWave.getPower().split(" ")[0]);


					//se la potenza misurata è circa 0, la stufa non è ancora accesa quindi la accende
					if(plugPower <= 0)
					{
						ZWave.plugOn();
					}

					//regola intensit� della luce solo se si � in auto_mode e se c'� una luce accesa
					if(Hue.isOn)
					{
						Hue.lightsPower(lightPower);
						Hue.cold();			
					}
				}
				//temperatura troppo alta
				else if(tempNumb > (ApiAiListener.COMFORT_TEMPERATURE + DELTA_GRADES))
				{
						//spegne la presa della stufetta solo se non è già spenta
						plugPower = Float.parseFloat(ZWave.getPower().split(" ")[0]);
						
						//se la potenza misurata � < 0, la stufa � gi� spenta
						if(plugPower > 0)
						{	
							ZWave.plugOff();
						}
						
						if(Hue.isOn)
						{
							Hue.lightsPower(lightPower);
							Hue.hot();	
						}
				}
				//temperatura ok
				else
				{	
					if(Hue.isOn)
					{
						Hue.lightsPower(lightPower);
						Hue.white();
					}
				}
				
			}
			catch (Exception e)
			{
				System.out.println("Probably no connection with ZWave");
				e.printStackTrace();
			}
		}
	}
		
	public int computeBestPower(int innerLux) throws Exception
	{
		int ret = 0, currentPower;
		
		//PRIMA COSA: MOVIMENTO
		//cerca se c'è movimento in stanza
		String motion = ZWave.getMotion().toLowerCase();
		
		//se non c'è movimento spegne
		if(motion.equals("off"))
		{
			FREQ = NORMAL_FREQUENCY;
			return 0;
		}
		
		//c'e' gia' luce giusta?
		if(innerLux <= MAX_COMFORT_LUX && innerLux >= MIN_COMFORT_LUX)	
		{
			//se si, tiene lo stesso valore di power lux corrente
			currentPower = Hue.getLightPower();
			FREQ = NORMAL_FREQUENCY;	
			return currentPower;
		}
		
		//altrimenti aumenta o diminuisce di una piccola percentuale il power:
		//aggiunge o toglie dal power corrente
		FREQ = HIGH_FREQUENCY;
		currentPower = Hue.getLightPower();
		if(innerLux < MIN_COMFORT_LUX)
			ret = currentPower + POWER_STEP;
		else if(innerLux > MAX_COMFORT_LUX)
			ret = currentPower - POWER_STEP;

		return (ret >= Hue.MAX_HUE_POWER ? Hue.MAX_HUE_POWER : ret);
	}
}