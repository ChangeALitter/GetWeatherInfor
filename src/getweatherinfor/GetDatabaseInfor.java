package getweatherinfor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetDatabaseInfor {
	public static Log log = LogFactory.getLog(GetDatabaseInfor.class);
	public static String ip="127.0.0.1";
	public static String port="3306";
	public static String user="root";
	public static String password="root";
	public static String databasename="websql";
	public static String url="";
	public static String driver="com.mysql.jdbc.Driver";
		
    //初始化数据库配置
	public static void initDatabaseConn(){
		Properties properties = null;
		try {
			properties = new Properties();
			InputStream in = new BufferedInputStream(new FileInputStream("config.properties"));
			properties.load(in);
			//properties.load(GetDatabaseInfor.class.getClassLoader().getResourceAsStream("D:\\eclipse\\DeviceRunStatus\\config.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block

			log.error("加载数据库配置文件失败!");
			properties = null;
		}
		if (null != properties){
			ip= properties.getProperty("IP");
			port = properties.getProperty("Port");
			user = properties.getProperty("User");
			password = properties.getProperty("Password");
			databasename = properties.getProperty("DatabaseName");
			driver = properties.getProperty("Driver");
		}
		url = "jdbc:mysql://"+ip+":"+port+"/"+databasename;
	}
	/*public static void main(String args[]){
		GetDatabaseInfor.initDatabaseConn();
		System.out.println(GetDatabaseInfor.ip);
		
	}*/

}

