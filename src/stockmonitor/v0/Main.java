package stockmonitor.v0;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.espertech.esper.client.*;

public class Main {

	public static void main(String[] args) throws Exception {
		
		System.out.print("please input the stock code: ");
		Scanner input = new Scanner(System.in);
		final String stockCode = input.next();
		input.close();
		
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
		System.out.println("Your stock name: " + stockName);
		
		//esper configuration
		Configuration cepConfig = new Configuration();
		cepConfig.addEventType("Stock", StockInfo.class.getName());
		
		EPServiceProvider cep = EPServiceProviderManager.getDefaultProvider(cepConfig);
		EPRuntime cepRT = cep.getEPRuntime();
		
		EPAdministrator cepAdmin = cep.getEPAdministrator();
		EPStatement cepStatement = cepAdmin.createEPL("select * from " + 
				  									  "Stock(curPrice > 2.85) ");
		cepStatement.addListener(new CEPListener());
		
		//Quartz configuration
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		JobDataMap map = new JobDataMap();
		map.put("stockCode", stockCode);
		map.put("cepRT", cepRT);
		
		JobDetail job = newJob(MyJob.class)
				.withIdentity("job1", "group1")
				.usingJobData(map)
				.build();
	
		Trigger trigger1 = newTrigger()
				.withIdentity("trigger1", "group1-morning")
				.withSchedule( cronSchedule("0/5 30-59 9 ? * MON-FRI") )
				.forJob("job1", "group1")
				.build();
		Trigger trigger2 = newTrigger()
				.withIdentity("trigger2", "group1-morning")
				.withSchedule( cronSchedule("0/5 * 10 ? * MON-FRI") )
				.forJob("job1", "group1")
				.build();
		Trigger trigger3 = newTrigger()
				.withIdentity("trigger3", "group1-morning")
				.withSchedule( cronSchedule("0/5 0-29 11 ? * MON-FRI") )
				.forJob("job1", "group1")
				.build();
		Trigger trigger4 = newTrigger()
				.withIdentity("trigger4", "group2-afternoon")
				.withSchedule( cronSchedule("0/5 * 13-21 ? * MON-FRI") )
				.forJob("job1", "group1")
				.build();
		
		Set<Trigger> triggerset = new TreeSet<Trigger>(); //using a set to contain all the triggers for the only job
		triggerset.add(trigger1);
		triggerset.add(trigger2);
		triggerset.add(trigger3);
		triggerset.add(trigger4);
		
		scheduler.start();
		scheduler.scheduleJob(job, triggerset, false);
		//Thread.sleep(600000); 
		//scheduler.shutdown();

	}//end of main()

}
