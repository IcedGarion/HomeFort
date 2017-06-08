package zWaveController;

import de.fh_zwickau.informatik.sensor.IZWayApi;

import de.fh_zwickau.informatik.sensor.ZWayApiHttp;
import de.fh_zwickau.informatik.sensor.model.devices.Device;
import de.fh_zwickau.informatik.sensor.model.devices.DeviceList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ZWave
{
	private static IZWayApi zwayApi;

    public static void init()
    {
    	// RaZberry IP address
        String ipAddress = "172.30.1.137";

        // username and password
        String username = "admin";
        String password = "raz4reti2";

        // create an instance of the Z-Way library; all the params are mandatory (we are not going to use the remote service/id)
        zwayApi = new ZWayApiHttp(ipAddress, 8083, "http", username, password, 0, false, new ZWaySimpleCallback());
    }
    
    
    //SENSOR
    private static List<Device> getSensor(int sensorNodeId)
    {
    	//get all the Z-Wave devices
        DeviceList allDevices = zwayApi.getDevices();
        
        Map<Integer, List<Device>> sensor = allDevices.getDevicesByNodeId(sensorNodeId);	//prende il device numero x e ritorna una mappa in cui c'è solo l'elemento numero x
        List<Device> tmp = sensor.get(sensorNodeId);										//prende dalla mappa questo elemento numero x (una lista di comandi che puoi dare su quel device)
        																					//poi cicla sulla lista per trovare il probe "temperature" o altro
        return tmp;
    }
    
    public static Map<String, String> getSensorMeasurements(int sensorNodeId)
    {
    	List<Device> sensorProbes = getSensor(sensorNodeId);
    	Map<String, String> measures = new HashMap<String, String>();
    	
    	for(Device probe : sensorProbes)
    	{
    		if(probe.getDeviceType().equalsIgnoreCase("SensorMultilevel"))
    		{
    			if(probe.getProbeType().equalsIgnoreCase("temperature"))
    				measures.put("temperature", probe.getMetrics().getLevel() + " " + probe.getMetrics().getScaleTitle());
    			else if(probe.getProbeType().equalsIgnoreCase("luminosity"))
    				measures.put("luminosity", probe.getMetrics().getLevel() + " " + probe.getMetrics().getScaleTitle());
    			else if(probe.getProbeType().equalsIgnoreCase("humidity"))
    				measures.put("humidity", probe.getMetrics().getLevel() + " " + probe.getMetrics().getScaleTitle());
    		}
    		if(probe.getDeviceType().equalsIgnoreCase("SensorBinary"))
    		{
    			if(probe.getProbeType().equalsIgnoreCase("general_purpose"))
    				measures.put("motion",probe.getMetrics().getLevel());
    		}
    	}
    	
    	return measures;
    }
    
    public static String getTemperature(int sensorNodeId)
    {
    	Map<String, String> measures = getSensorMeasurements(sensorNodeId);
    	return measures.get("temperature");
    }
      
    public static String getLuminosity(int sensorNodeId)
    {
    	Map<String, String> measures = getSensorMeasurements(sensorNodeId);
    	return measures.get("luminosity");
    }
    
    public static String getHumidity(int sensorNodeId)
    {

    	Map<String, String> measures = getSensorMeasurements(sensorNodeId);
   	   	return measures.get("humidity");
    }
    
    public static String getMotion(int sensorNodeId)
    {
    	Map<String, String> measures = getSensorMeasurements(sensorNodeId);
    	return measures.get("motion");
    }

    public static String renderSensorMeasurements(int sensorNodeId)
	{
		String text = "", motion = "0";
		Map<String, String> measurements = getSensorMeasurements(sensorNodeId);
		
		text += "temperatura : " + measurements.get("temperature") + "\n";
		text += "luminosita' : " + measurements.get("luminosity") + "\n";
		text += "umidita' : " + measurements.get("humidity") + "\n";
		
		motion = measurements.get("motion");
		if(motion.toLowerCase().startsWith("on"))
			text += "presenza : rilevata \n";
		else
			text += "presenza : nessuna \n";
		
		return text;
	}
    
    
    //PLUGS
    private static List<Device> getPlugDevice(int plugNodeId)
    {
    	//get all the Z-Wave devices
        DeviceList allDevices = zwayApi.getDevices();
        
        Map<Integer, List<Device>> plug = allDevices.getDevicesByNodeId(plugNodeId);	//prende il device numero x e ritorna una mappa in cui c'è solo l'elemento numero x
        List<Device> tmp = plug.get(plugNodeId);										//prende dalla mappa questo elemento numero x (una lista di comandi che puoi dare su quel device)
        																					//poi cicla sulla lista per trovare il probe "temperature" o altro
        return tmp;
    }
    
    public static Map<String, String> getPlugMeasurements(int plugNodeId)
    {
    	//prende tutti i sensori della plug
    	List<Device> plugProbes = getPlugDevice(plugNodeId);
    	Map<String, String> measures = new HashMap<String, String>();
    	
    	for(Device probe : plugProbes)
    	{
    		if(probe.getDeviceType().equalsIgnoreCase("SensorMultilevel"))
    		{
    			if(probe.getProbeType().equalsIgnoreCase("meterElectric_watt"))
    				measures.put("power", probe.getMetrics().getLevel() + " " + probe.getMetrics().getScaleTitle());
    			else if(probe.getProbeType().equalsIgnoreCase("meterElectric_kilowatt_hour"))
    				measures.put("consumption", probe.getMetrics().getLevel() + " " + probe.getMetrics().getScaleTitle());
    		}
    	}
    	
    	return measures;
    }
    
    public static String renderPlugMeasurements(int sensorNodeId)
	{
		String text = "";
		Map<String, String> measurements = getPlugMeasurements(sensorNodeId);
		
		text += "Potenza : " + measurements.get("meterElectric_watt") + "\n";
		text += "Consumo : " + measurements.get("consumption") + "\n";
		
		return text;
	}
    
    public static void plugOn(int plugNodeId)
    {
    	List<Device> plugProbes = getPlugDevice(plugNodeId);
    	
    	for(Device probe : plugProbes)
    	{
    		if(probe.getDeviceType().equalsIgnoreCase("SwitchBinary"))
    		{
    			probe.on();
    		}
    	}
    }
    
    public static void plugOff(int plugNodeId)
    {
    	List<Device> plugProbes = getPlugDevice(plugNodeId);
    	
    	for(Device probe : plugProbes)
    	{
    		if(probe.getDeviceType().equalsIgnoreCase("SwitchBinary"))
    		{
    			probe.off();
    		}
    	}
    }
    
    public static void getAllDevices()
    {
    	//get all the Z-Wave devices
    	DeviceList allDevices = zwayApi.getDevices();
        
        // search all sensors
        for (Device dev : allDevices.getAllDevices())
        {
            if (dev.getDeviceType().equalsIgnoreCase("SensorMultilevel"))
            {
            	 System.out.println("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());

                 if (dev.getProbeType().equalsIgnoreCase("temperature")) 
                 {
                     System.out.println(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " " + dev.getMetrics().getScaleTitle());
                 } 
                 else if (dev.getProbeType().equalsIgnoreCase("meterElectric_watt")) 
                 {
                     System.out.println(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " " + dev.getMetrics().getScaleTitle());
                 } 
                 else 
                 {
                     System.out.println(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " uom: " + dev.getMetrics().getScaleTitle());
                 }
            }
            else if(dev.getDeviceType().equalsIgnoreCase("SwitchBinary"))
            {
            	System.out.println("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
            }
            else if(dev.getDeviceType().equalsIgnoreCase("SensorBinary"))
            {
            	System.out.println("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
            }
        }
    }

    /*
    public static void turnAllOff()
    {
    	DeviceList allDevices = zwayApi.getDevices();
    	
        // search again all power outlets
        for (Device dev : allDevices.getAllDevices()) {
            if (dev.getDeviceType().equalsIgnoreCase("SwitchBinary")) {
                logger.debug("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
                // turn it off
                logger.info("Turn device " + dev.getNodeId() + " off...");
                dev.off();
            }
        }
    }


    */

}