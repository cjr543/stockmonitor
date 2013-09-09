package stockmonitor.v0;

import com.espertech.esper.client.*;

public class CEPListener implements UpdateListener {

	@Override
	public void update(EventBean[] newData, EventBean[] oldData) {
		System.out.println("Event received: " + newData[0].getUnderlying());
	}

}
