package getweatherinfor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JdbcForWeather {
	public static Log log = LogFactory.getLog(JdbcForWeather.class);
	private String driver; //mysql驱动
	private String url;  //mysql连接
	private String usrname; //mysql用户名
	private String passwd;  //mysql密码
	private Connection conn; //获得连接
	private Statement stat; //语句
	
	public JdbcForWeather(String mydriver, String myurl, String myname, String mywd)
	{
		driver = mydriver;
		url = myurl;
		usrname = myname;
		passwd = mywd;		
		try {
			//加载驱动
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			log.error("数据库加载驱动失败！");
		}
		try {
			//建立连接
			conn = DriverManager.getConnection(url, usrname, passwd);
			log.debug("connection successfully!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("建立连接失败!");
			conn = null;
		}
		try {
			//执行命令
			stat = conn.createStatement();			  
		} catch (SQLException e) {
			// TODO Auto-generated catch block
            log.error("创建statement失败!");
            stat = null;
		}
				
	}
	
	public void jdbclose()
	{
		try {
			stat.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("关闭数据库失败!");
		}
	}
	
	public String[] SelectStat(String weathcity,String weathertable) throws SQLException{
		String temp = null;
		String[] cityinfor = null;
		List<String> cityinforall = new ArrayList<String>();
		temp = String.format("select %s from %s", weathcity, weathertable);
		
		int tryconnect=5;
		boolean tranctioncompteled=false;
		do{
			ResultSet result;
			try {
				result = stat.executeQuery(temp);
				while (result.next()){
					cityinforall.add(result.getString(1));
				}
				cityinfor = (String[])cityinforall.toArray(new String[0]);
				result.close();
				tranctioncompteled=true;
			} catch (SQLException sqlEx) {
				// TODO Auto-generated catch block
				String sqlState = sqlEx.getSQLState();
				if ("08S01".equals(sqlState)||"40001".equals(sqlState)){
					if (conn!=null)
						conn.close();
					if (stat!=null)
						stat.close();
					conn = DriverManager.getConnection(url, usrname, passwd);
					stat = conn.createStatement();
					tryconnect--;
				} else{
					tryconnect=0;
					log.error("从数据库获取需要查询天气的城市信息失败!");
				}
			}
		}while(!tranctioncompteled && tryconnect > 0);
			
		return cityinfor;
		
	}
	
	public Object ReturnCityId(String cityid,String cityname) throws SQLException{
		String temp=null;
		Object cityidtemp = null;
		
		temp = String.format("select %s from City_Define where City_Name like \"%s%%\" ", cityid, cityname);
		ResultSet result = stat.executeQuery(temp);
		
		while (result.next()){
			cityidtemp =result.getObject(1);		
		}
		result.close();		
		return cityidtemp;
		
	}
	
	//UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing' WHERE LastName = 'Wilson'
	public void UpdateStat(Object Cityid, String Cityname, String Url, int WeatherType, float Temper, float Humidity, String Weathertxt){
			String temp;
		    temp = String.format("update weatherinfo set Cityid=%d, URL=\"%s\", WeatherType=%d, Temperature=%f, Humidity=%f, Weathertxt=\"%s\" where CityName = \"%s\"", Cityid, Url, WeatherType, Temper, Humidity, Weathertxt, Cityname);			
		
		    try {
				stat.executeUpdate(temp);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("更新数据失败!");
			}
	}
	
	public void UpdateStat(Object Cityid, String Cityname, String Url, String WeatherType, String Weathertxt) throws SQLException{
		String temp;
		
		if (WeatherType.matches("[0,9]+"))
	        temp = String.format("update weatherinfo set Cityid=%d, URL=\"%s\", WeatherType=%d, Weathertxt=\"%s\" where CityName = \"%s\"", Cityid, Url, Integer.parseInt(WeatherType), Weathertxt, Cityname);			
		else
	        temp = String.format("update weatherinfo set Cityid=%d, URL=\"%s\", Weathertxt=\"%s\" where CityName = \"%s\"", Cityid, Url,Weathertxt, Cityname);			
	
	    stat.executeUpdate(temp);
	}
	
	public void UpdateProTime(String name, String timenow) throws SQLException{
		String sql = String.format("update program_monitor set program_heartbeat = \"%s\" where program_name = \"%s\"", timenow, name);
		stat.executeUpdate(sql);
	}
	
			
}
