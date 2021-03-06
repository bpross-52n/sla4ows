package org.ifgi.sla.monitor;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.sun.jersey.api.client.ClientResponse.Status;

@SuppressWarnings("serial")
public class MonitorServlet extends HttpServlet
{
	protected static Logger LOGGER = Logger.getLogger(MonitorServlet.class);

	protected Scheduler mScheduler;

	@Override
	public void init() throws ServletException
	{
		int interval = Configuration.instance().getInterval();

		try
		{
			mScheduler = StdSchedulerFactory.getDefaultScheduler();

			JobDetail job = new JobDetail("MonitorJob", "MonitorJobGroup", MonitorJob.class);
			SimpleTrigger trigger = new SimpleTrigger("myTrigger", null, new Date(), null, SimpleTrigger.REPEAT_INDEFINITELY, interval);
			mScheduler.scheduleJob(job, trigger);

			mScheduler.start();
		}
		catch (SchedulerException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}

	@Override
	public void destroy()
	{
		super.destroy();
		try
		{
			mScheduler.shutdown();
		}
		catch (SchedulerException e)
		{
			LOGGER.error(e);
		}
	}
}
