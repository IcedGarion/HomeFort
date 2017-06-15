package controlThreads;

import httpServer.ApiAiListener;
import hueController.Hue;
import util.Writer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class LightControl extends Thread
{
    private final int FREQUENCY = 3000/*108000*/;
    private final String LIGHTS_TIMES_FILE = Writer.LIGHTS_TIMES_FILE;
    public static boolean autoMode;
    //autoMode: dice che deve intervenire comfortControl e LightControl (quindi regola luci come vuole)
  	//viene impostata con un comando apposito, e inoltre se utente accende o spegne la luce viene disattivata! (+ avvisa utente)
  	
    public LightControl()
    {
    	autoMode = false;
    }
    
    @Override
    public void run()
    {
        BufferedReader reader;
        long currentMillisec, startMillisec, endMillisec;
        String line;
        String tmp[];
        boolean on = false;

        //utente attiva o disattiva modalit� automatica luci!
        //ogni tanto legge da file e controlla se e' in un'ora in cui puo' accendere
        try
        {
            while(true)	
            {	
            	//continua a ciclare finch� nn viene inserita la modalita' automatica
            	sleep(FREQUENCY);
            	if(! autoMode)
            		continue;

            	//prende l'ora attuale (in millisecondi) e controlla riga per riga nel file di configurazione, se c'e' un orario di accensione
            	currentMillisec = getCurrentMs();
            	reader = new BufferedReader(new FileReader(LIGHTS_TIMES_FILE));
            	on = false;
            	while((line = reader.readLine()) != null)
            	{
            		tmp = line.split(", ");
            		startMillisec = Long.parseLong(tmp[0]);
            		endMillisec = Long.parseLong(tmp[1]);
                
            		//controlla se e' nel range...
            		if((startMillisec <= currentMillisec) && (endMillisec >= currentMillisec))
            		{	
            			on = true;
            			if(! Hue.isOn)
            				Hue.lightsOn();		
            		}
            	}
            	
            	//se non trova neanche una riga di intervallo di accensione che corrisponde all'ora attuale, spegne (se accesa)
            	if((! on) && Hue.isOn)
            		Hue.lightsOff();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private long getCurrentMs()
    {
    	ZonedDateTime now = ZonedDateTime.now();
    	ZonedDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
    	Duration duration = Duration.between(midnight, now);

    	return duration.getSeconds();
    }
}
