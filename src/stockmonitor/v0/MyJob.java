package stockmonitor.v0;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.espertech.esper.client.*;

public class MyJob implements Job {
	
	public MyJob() {
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try{
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			String stockCode = dataMap.getString("stockCode");
			EPRuntime cepRT = (EPRuntime)dataMap.get("cepRT");
			
			URL stockUrl = new URL("http://hq.sinajs.cn/?list=" + stockCode);
			BufferedReader stockAPIInfo = new BufferedReader(new InputStreamReader(stockUrl.openStream()));
			String stockAPILine = stockAPIInfo.readLine();
			stockAPIInfo.close();
			
			String [] content = stockAPILine.split("\"");
			if(content[1] == ""){
				//exit(1);
			}
			String [] temp = content[1].split(",");
			String stockName = temp[0];
			Double stockPrice = Double.parseDouble(temp[3]);
		
			Date nowTime = new Date(System.currentTimeMillis());
			StockInfo stockinfo = new StockInfo(stockCode, stockName, stockPrice, nowTime);
			cepRT.sendEvent(stockinfo);
			
			System.out.println("Current price: " + stockPrice + ", " + nowTime);
		}
		catch(MalformedURLException me){
			me.printStackTrace();
		}
		catch(IOException ie){
			ie.printStackTrace();
		}
		
	}//end of execute()

}
