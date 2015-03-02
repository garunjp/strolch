package li.strolch.service.executor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import li.strolch.agent.api.ComponentContainer;
import li.strolch.agent.api.StrolchComponent;
import li.strolch.exception.StrolchException;
import li.strolch.runtime.configuration.ComponentConfiguration;
import li.strolch.service.api.Service;
import li.strolch.service.api.ServiceArgument;
import li.strolch.service.api.ServiceHandler;
import li.strolch.service.api.ServiceResult;
import ch.eitchnet.privilege.model.Certificate;

/**
 * The {@link ServiceExecutionHandler} is used to perform long running services so that no singletons etc. are required.
 * 
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class ServiceExecutionHandler extends StrolchComponent {

	private Map<String, ServiceExecutionStatus> serviceContextMap;
	private BlockingQueue<ServiceContext<? extends ServiceArgument, ? extends ServiceResult>> queue;

	private Thread thread;
	private volatile boolean interrupted;

	public ServiceExecutionHandler(ComponentContainer container, String componentName) {
		super(container, componentName);
	}

	@Override
	public void initialize(ComponentConfiguration configuration) {

		this.serviceContextMap = Collections.synchronizedMap(new HashMap<>());
		this.queue = new LinkedBlockingQueue<>();

		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!interrupted) {
						doService(queue.take());
					}
				} catch (InterruptedException ex) {
					logger.error(ex.getLocalizedMessage());
				}
			}
		}, "ServiceExecutor");
		this.thread.setDaemon(true);

		super.initialize(configuration);
	}

	private <T extends ServiceArgument, U extends ServiceResult> void doService(ServiceContext<T, U> svcCtx) {
		if (this.interrupted)
			return;

		String serviceName = svcCtx.service.getClass().getName();
		ServiceExecutionStatus status = this.serviceContextMap.get(serviceName);
		status.started();
		ServiceHandler svcHandler = getContainer().getComponent(ServiceHandler.class);
		U svcResult = svcHandler.doService(svcCtx.certificate, svcCtx.service, svcCtx.argument);
		status.setResult(svcResult);
	}

	@Override
	public void start() {
		this.thread.start();
		super.start();
	}

	@Override
	public void stop() {

		if (this.thread != null) {
			this.thread.interrupt();
			try {
				this.thread.join(2000l);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}

		super.stop();
	}

	@Override
	public void destroy() {
		this.thread = null;
		super.destroy();
	}

	public ServiceExecutionStatus getStatus(Class<?> clazz) {
		ServiceExecutionStatus status = this.serviceContextMap.get(clazz.getName());
		if (status == null)
			return new ServiceExecutionStatus(clazz.getName());
		return status;
	}

	public <T extends ServiceArgument, U extends ServiceResult> ServiceExecutionStatus doService(
			Certificate certificate, Service<T, U> service, T argument) {

		String serviceName = service.getClass().getName();

		if (this.serviceContextMap.containsKey(serviceName)) {
			ServiceExecutionStatus serviceExecutionStatus = this.serviceContextMap.get(serviceName);
			if (!serviceExecutionStatus.isDone()) {
				throw new StrolchException("A service with name " + serviceName + " is already running!");
			}
		}

		ServiceContext<T, U> svcCtx = new ServiceContext<T, U>(certificate, service, argument);
		try {
			ServiceExecutionStatus status = new ServiceExecutionStatus(serviceName);
			this.serviceContextMap.put(serviceName, status);
			this.queue.put(svcCtx);
			Thread.sleep(20l);
			return status;
		} catch (InterruptedException e) {
			this.serviceContextMap.remove(serviceName);
			throw new StrolchException("Failed to register service context: " + e.getMessage(), e);
		}
	}

	public class ServiceContext<T extends ServiceArgument, U extends ServiceResult> {

		private Certificate certificate;
		private Service<T, U> service;
		private T argument;

		public ServiceContext(Certificate certificate, Service<T, U> service, T argument) {
			this.certificate = certificate;
			this.service = service;
			this.argument = argument;
		}
	}
}
