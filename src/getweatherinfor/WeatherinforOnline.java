package getweatherinfor;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

	public class WeatherinforOnline {
		public static Log log = LogFactory.getLog(WeatherinforOnline.class);
		private String weatherurl;
		private String state;
		private String temNow;
		private String humidity;
		private String weathertext;
		
		public WeatherinforOnline(String hanzi,String cityname){		
			Document doc = null;      //文件
			weatherurl =null;
			state = null;
			temNow = null;
			humidity = null;
			weathertext = null;
		    
			//从URL获取网页内容
			weatherurl = "http://flash.weather.com.cn/wmaps/xml/"+cityname+".xml";  
			try {
				doc = (Document) Jsoup.connect(weatherurl).timeout(5000).get();//设置连接超时为5S.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("网络不稳定，连接天气网站失败!");
				doc = null;
			}
            
			if (doc != null){
				Elements es = doc.getElementsByTag("city");
				for (Element e : es){
					String str1 = e.attr("cityname");
					if(str1.startsWith(hanzi)){
						/*String state1 = e.attr("state1");				
						String state2 = e.attr("state2");
					
						if (state1.equalsIgnoreCase(state2))
						{
							state = state1;
						}
						else
						{
							state = state1+state2;
						}*/
					
			
						state = e.attr("state1");
						temNow = e.attr("temNow");
						weathertext = e.attr("stateDetailed");
						String humiditytemp = e.attr("humidity");
						humidity = humiditytemp.substring(0, humiditytemp.length()-1);
						break;
					}
				}
			}
		}
		
		public String ReturnUrl(){
			return weatherurl;
		}
		public String ReturnSate(){
			return state;
		}
		
		public String ReturnStateDetail(){
			return weathertext;
		}
		
		public String ReturnTemper(){
			return temNow;
		}
		
		public String ReturnHumidity(){
			return humidity;
		}
		
	}

