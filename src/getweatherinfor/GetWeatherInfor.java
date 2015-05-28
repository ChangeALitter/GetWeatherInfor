package getweatherinfor;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;



	class Mytask extends TimerTask{
		public static Log log = LogFactory.getLog(Mytask.class);
		private JdbcForWeather mydatabase;
		private String driver;
		private String url;
		private String usrname;
		private String passwd;
		
		Mytask(String driverstr, String urlstr, String usrnamestr, String passwdstr)
		{
			driver = driverstr;
			url = urlstr;
			usrname = usrnamestr;
			passwd = passwdstr;
		}
		
		public void run()
		{
			mydatabase = new JdbcForWeather(driver, url, usrname, passwd);
			//把天气数据写入数据库
			String[] citycn = null;
			try {
				citycn = mydatabase.SelectStat("CityName", "weatherinfo");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("从数据库获取需要查询天气的城市信息失败!");
				citycn = null;
			}
			if (null != citycn){
				int k=0;
				int num = citycn.length;
				for (;k < num;k++){
					String[] citytemptoken = citycn[k].split(":");
					Object citytempid = null;
					try {
						citytempid = mydatabase.ReturnCityId("City_ID", citytemptoken[0]);

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						log.error("获取城市id失败!");
						citytempid = null;
					}
					if (citytempid == null)
						continue;
					WeatherinforOnline cityweather = new WeatherinforOnline(citytemptoken[0], citytemptoken[1]);
					String weathurl = cityweather.ReturnUrl();
					String weathtype = cityweather.ReturnSate();
					String weathertext = cityweather.ReturnStateDetail();
					String weathtemper = cityweather.ReturnTemper();
					String weathhumidity = cityweather.ReturnHumidity();
					try {
						if (weathtemper!=null && !weathtemper.equals("暂无实况") && !weathhumidity.equals("暂无实况"))
							mydatabase.UpdateStat(citytempid, citycn[k], weathurl, Integer.parseInt(weathtype), Float.parseFloat(weathtemper), Float.parseFloat(weathhumidity), weathertext);
						else 
							mydatabase.UpdateStat(citytempid, citycn[k], weathurl, weathtype, weathertext);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						log.error("天气类型：字符串转整型失败!");
					} catch (SQLException e) {
						// TODO Auto-generated catch block				
						log.error("更新天气天气信息失败!");
					}
				
				}
			}
			mydatabase.jdbclose();
		}		
	}
	
	

public class GetWeatherInfor {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("log4j.properties");
		GetDatabaseInfor.initDatabaseConn();    	
    	Timer timer =new Timer();
    	timer.scheduleAtFixedRate(new Mytask(GetDatabaseInfor.driver,GetDatabaseInfor.url,GetDatabaseInfor.user,GetDatabaseInfor.password), 1000, 1000);    	
    	
    	ThreadBeatTime threadbeat = new ThreadBeatTime(GetDatabaseInfor.driver,GetDatabaseInfor.url,GetDatabaseInfor.user,GetDatabaseInfor.password,"GetWeatherInfor");
    	Thread heartbeat = new Thread(threadbeat);
    	heartbeat.start();    	       
	}

}
