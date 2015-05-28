package getweatherinfor;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class HeartBeat extends TimerTask{
	public static Log log = LogFactory.getLog(HeartBeat.class);
	private JdbcForWeather mydatabase;
	private String driver;
	private String url;
	private String usrname;
	private String passwd;
	private String name;
	
	HeartBeat(String driverstr, String urlstr, String usrnamestr, String passwdstr, String proname)
	{
		driver = driverstr;
		url = urlstr;
		usrname = usrnamestr;
		passwd = passwdstr;
		name = proname;
		
	}
	
    public void run(){
    	mydatabase = new JdbcForWeather(driver, url, usrname, passwd);
    	if (mydatabase != null){
    		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		Date nowtime = new Date();
    		String timestr=dfs.format(nowtime);
    		try {
    			mydatabase.UpdateProTime(name, timestr);
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			log.error("更新心跳时间失败!");
			
    		}
    		mydatabase.jdbclose();
    	}
	}
	
}

public class ThreadBeatTime implements Runnable{
	private HeartBeat heartbeat;
	
	ThreadBeatTime(String driverstr, String urlstr, String usrnamestr, String passwdstr, String proname){
		heartbeat = new HeartBeat(driverstr, urlstr, usrnamestr, passwdstr, proname);
	}
	
	public void run(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(heartbeat, 1000, 1000*60);		
	}

}
