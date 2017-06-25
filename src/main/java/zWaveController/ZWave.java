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
	public static int sensorNodeId = 5;
	public static int plugNodeId = 19;	//3 � safety home
	private static IZWayApi zwayApi;
	private static String ipAddress = "172.30.1.137"; 
	private static String username = "admin";
    private static String password = "raz4reti2";
    
    public static void init()
    {
    	// create an instance of the Z-Way library; all the params are mandatory (we are not going to use the remote service/id)
        zwayApi = new ZWayApiHttp(ipAddress, 8083, "http", username, password, 0, false, new ZWaySimpleCallback());
    }
    
    
    //SENSOR
    private static List<Device> getSensor()
    {
    	//get all the Z-Wave devices
        DeviceList allDevices = zwayApi.getDevices();
        
        Map<Integer, List<Device>> sensor = allDevices.getDevicesByNodeId(sensorNodeId);	//prende il device numero x e ritorna una mappa in cui c'è solo l'elemento numero x
        List<Device> tmp = sensor.get(sensorNodeId);										//prende dalla mappa questo elemento numero x (una lista di comandi che puoi dare su quel device)
        																					//poi cicla sulla lista per trovare il probe "temperature" o altro
        return tmp;
    }
    
    public static Map<String, String> getSensorMeasurements()
    {
    	List<Device> sensorProbes = getSensor();
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
    
    public static String getTemperature()
    {
    	Map<String, String> measures = getSensorMeasurements();
    	return measures.get("temperature");
    }
      
    public static String getLuminosity()
    {
    	Map<String, String> measures = getSensorMeasurements();
    	return measures.get("luminosity");
    }
    
    public static String getHumidity()
    {

    	Map<String, String> measures = getSensorMeasurements();
   	   	return measures.get("humidity");
    }
    
    public static String getMotion()
    {
    	Map<String, String> measures = getSensorMeasurements();
    	return measures.get("motion");
    }

    public static String renderSensorMeasurements()
	{
		String text = "", motion = "0";
		Map<String, String> measurements = getSensorMeasurements();
		
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
    private static List<Device> getPlugDevice()
    {
    	//get all the Z-Wave devices
        DeviceList allDevices = zwayApi.getDevices();
        
        Map<Integer, List<Device>> plug = allDevices.getDevicesByNodeId(plugNodeId);	//prende il device numero x e ritorna una mappa in cui c'è solo l'elemento numero x
        List<Device> tmp = plug.get(plugNodeId);										//prende dalla mappa questo elemento numero x (una lista di comandi che puoi dare su quel device)
        																					//poi cicla sulla lista per trovare il probe "temperature" o altro
        return tmp;
    }
    
    public static Map<String, String> getPlugMeasurements()
    {
    	//prende tutti i sensori della plug
    	List<Device> plugProbes = getPlugDevice();
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
    
    public static String renderPlugMeasurements()
	{
		String text = "";
		Map<String, String> measurements = getPlugMeasurements();
		
		text += "Potenza : " + measurements.get("power") + "\n";
		text += "Consumo : " + measurements.get("consumption") + "\n";
		
		return text;
	}

	public static String getPower()
	{
		Map<String, String> measures = getPlugMeasurements();
		return measures.get("power");
	}

	public static String getConsumption()
	{
		Map<String, String> measures = getPlugMeasurements();
		return measures.get("consumption");
	}
    
    public static void plugOn()
    {
    	List<Device> plugProbes = getPlugDevice();
    	
    	for(Device probe : plugProbes)
    	{
    		if(probe.getDeviceType().equalsIgnoreCase("SwitchBinary"))
    		{
    			probe.on();
    		}
    	}
    }
    
    public static void plugOff()
    {
    	List<Device> plugProbes = getPlugDevice();
    	
    	for(Device probe : plugProbes)
    	{
    		if(probe.getDeviceType().equalsIgnoreCase("SwitchBinary"))
    		{
    			probe.off();
    		}
    	}
    }
}