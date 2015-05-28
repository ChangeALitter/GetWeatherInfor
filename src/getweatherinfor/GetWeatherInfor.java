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
			//����������д�����ݿ�
			String[] citycn = null;
			try {
				citycn = mydatabase.SelectStat("CityName", "weatherinfo");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("�����ݿ��ȡ��Ҫ��ѯ�����ĳ�����Ϣʧ��!");
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
						log.error("��ȡ����idʧ��!");
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
						if (weathtemper!=null && !weathtemper.equals("����ʵ��") && !weathhumidity.equals("����ʵ��"))
							mydatabase.UpdateStat(citytempid, citycn[k], weathurl, Integer.parseInt(weathtype), Float.parseFloat(weathtemper), Float.parseFloat(weathhumidity), weathertext);
						else 
							mydatabase.UpdateStat(citytempid, citycn[k], weathurl, weathtype, weathertext);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						log.error("�������ͣ��ַ���ת����ʧ��!");
					} catch (SQLException e) {
						// TODO Auto-generated catch block				
						log.error("��������������Ϣʧ��!");
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
