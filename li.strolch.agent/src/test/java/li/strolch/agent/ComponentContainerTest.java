/*
 * Copyright 2013 Robert von Burg <eitch@eitchnet.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package li.strolch.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.text.MessageFormat;

import li.strolch.agent.api.ComponentContainer;
import li.strolch.agent.api.OrderMap;
import li.strolch.agent.api.ResourceMap;
import li.strolch.agent.api.StrolchAgent;
import li.strolch.model.ModelGenerator;
import li.strolch.model.Order;
import li.strolch.model.Resource;
import li.strolch.persistence.api.OrderDao;
import li.strolch.persistence.api.ResourceDao;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.runtime.StrolchConstants;
import li.strolch.runtime.configuration.RuntimeConfiguration;
import li.strolch.runtime.configuration.model.ResourceGeneratorHandlerTest;
import li.strolch.runtime.configuration.model.ServiceHandlerTest;
import li.strolch.runtime.configuration.model.ServiceResultTest;
import li.strolch.runtime.privilege.PrivilegeHandler;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.eitchnet.privilege.model.Certificate;
import ch.eitchnet.utils.helper.FileHelper;

@SuppressWarnings("nls")
public class ComponentContainerTest {

	public static final String PATH_REALM_CONTAINER = "src/test/resources/realmtest";
	public static final String PATH_TRANSIENT_CONTAINER = "src/test/resources/transienttest";
	public static final String PATH_TRANSACTIONAL_CONTAINER = "src/test/resources/transactionaltest";
	public static final String PATH_CACHED_CONTAINER = "src/test/resources/cachedtest";
	public static final String PATH_EMPTY_CONTAINER = "src/test/resources/emptytest";
	public static final String PATH_MINIMAL_CONTAINER = "src/test/resources/minimaltest";

	public static final String PATH_REALM_RUNTIME = "target/realmtest/";
	public static final String PATH_TRANSIENT_RUNTIME = "target/transienttest/";
	public static final String PATH_TRANSACTIONAL_RUNTIME = "target/transactionaltest/";
	public static final String PATH_CACHED_RUNTIME = "target/cachedtest/";
	public static final String PATH_EMPTY_RUNTIME = "target/emptytest/";

	public static final Logger logger = LoggerFactory.getLogger(ComponentContainerTest.class);

	private static final String TARGET = "target"; //$NON-NLS-1$

	@Test
	public void shouldStartEmptyContainer() {

		try {
			StrolchAgent agent = startContainer(PATH_EMPTY_RUNTIME, PATH_EMPTY_CONTAINER);
			testContainer(agent);
			destroyContainer(agent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Test
	public void shouldStartTransientContainer() {

		try {
			StrolchAgent agent = startContainer(PATH_TRANSIENT_RUNTIME, PATH_TRANSIENT_CONTAINER);
			testContainer(agent);
			destroyContainer(agent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Test
	public void shouldStartTransactionalContainer() {

		try {
			StrolchAgent agent = startContainer(PATH_TRANSACTIONAL_RUNTIME, PATH_TRANSACTIONAL_CONTAINER);
			testPersistenceContainer(agent);
			testElementMaps(agent);
			destroyContainer(agent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Test
	public void shouldStartCachedContainer() {

		try {
			StrolchAgent agent = startContainer(PATH_CACHED_RUNTIME, PATH_CACHED_CONTAINER);
			testPersistenceContainer(agent);
			testElementMaps(agent);
			destroyContainer(agent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Test
	public void shouldStartRealmTestContainer() {

		try {
			StrolchAgent agent = startContainer(PATH_REALM_RUNTIME, PATH_REALM_CONTAINER);
			testContainer(agent);
			destroyContainer(agent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Test
	public void shouldTestRealms() {

		try {
			StrolchAgent agent = startContainer(PATH_REALM_RUNTIME, PATH_REALM_CONTAINER);
			testContainer(agent);
			testRealms(agent);
			destroyContainer(agent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Test
	public void shouldTestMinimal() {

		try {
			StrolchAgent agent = startContainer(PATH_REALM_RUNTIME, PATH_MINIMAL_CONTAINER);

			ComponentContainer container = agent.getContainer();

			ServiceHandlerTest serviceHandler = container.getComponent(ServiceHandlerTest.class);
			ServiceResultTest result = serviceHandler.doService();
			assertEquals(1, result.getResult());

			destroyContainer(agent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public static void testContainer(StrolchAgent agent) {

		ComponentContainer container = agent.getContainer();

		ServiceHandlerTest serviceHandler = container.getComponent(ServiceHandlerTest.class);
		ServiceResultTest result = serviceHandler.doService();
		assertEquals(1, result.getResult());

		ResourceGeneratorHandlerTest resourceGeneratorHandler = container
				.getComponent(ResourceGeneratorHandlerTest.class);
		Resource resource = resourceGeneratorHandler.getTestResource("@testRes", "Test Res", "Test");
		assertNotNull(resource);
		assertEquals("@testRes", resource.getId());
	}

	private static Certificate login(StrolchAgent agent) {
		PrivilegeHandler privilegeHandler = agent.getContainer().getPrivilegeHandler();
		return privilegeHandler.authenticate("test", "test".getBytes());
	}

	public static void testPersistenceContainer(StrolchAgent agent) {

		ComponentContainer container = agent.getContainer();

		ServiceHandlerTest serviceHandler = container.getComponent(ServiceHandlerTest.class);
		ServiceResultTest result = serviceHandler.doService();
		assertEquals(1, result.getResult());

		Certificate certificate = login(agent);
		try (StrolchTransaction tx = container.getRealm(StrolchConstants.DEFAULT_REALM).openTx(certificate, "test")) {
			ResourceDao resourceDao = tx.getPersistenceHandler().getResourceDao(tx);
			resourceDao.save(ModelGenerator.createResource("@testRes0", "Test Res", "Test"));
			Resource queriedRes = resourceDao.queryBy("Test", "@testRes0");
			assertNotNull(queriedRes);
			assertEquals("@testRes0", queriedRes.getId());
			tx.commitOnClose();
		}

		try (StrolchTransaction tx = container.getRealm(StrolchConstants.DEFAULT_REALM).openTx(certificate, "test")) {
			OrderDao orderDao = tx.getPersistenceHandler().getOrderDao(tx);
			orderDao.save(ModelGenerator.createOrder("@testOrder0", "Test Order", "Test"));
			Order queriedOrder = orderDao.queryBy("Test", "@testOrder0");
			assertNotNull(queriedOrder);
			assertEquals("@testOrder0", queriedOrder.getId());
			tx.commitOnClose();
		}
	}

	public static void testElementMaps(StrolchAgent agent) {

		ComponentContainer container = agent.getContainer();

		Certificate certificate = login(agent);
		try (StrolchTransaction tx = container.getRealm(StrolchConstants.DEFAULT_REALM).openTx(certificate, "test")) {
			ResourceMap resourceMap = tx.getResourceMap();
			resourceMap.add(tx, ModelGenerator.createResource("@testRes1", "Test Res", "Test"));
			Resource queriedRes = resourceMap.getBy(tx, "Test", "@testRes1");
			assertNotNull(queriedRes);
			assertEquals("@testRes1", queriedRes.getId());
			tx.commitOnClose();
		}

		try (StrolchTransaction tx = container.getRealm(StrolchConstants.DEFAULT_REALM).openTx(certificate, "test")) {
			OrderMap orderMap = tx.getOrderMap();
			orderMap.add(tx, ModelGenerator.createOrder("@testOrder1", "Test Order", "Test"));
			Order queriedOrder = orderMap.getBy(tx, "Test", "@testOrder1");
			assertNotNull(queriedOrder);
			assertEquals("@testOrder1", queriedOrder.getId());
			tx.commitOnClose();
		}
	}

	public static void testRealms(StrolchAgent agent) {

		ComponentContainer container = agent.getContainer();

		Certificate certificate = login(agent);
		try (StrolchTransaction tx = container.getRealm(StrolchConstants.DEFAULT_REALM).openTx(certificate, "test")) {
			ResourceMap resourceMap = tx.getResourceMap();
			resourceMap.add(tx, ModelGenerator.createResource("@testRes1", "Test Res", "Test"));
			Resource queriedRes = resourceMap.getBy(tx, "Test", "@testRes1");
			assertNotNull(queriedRes);
			assertEquals("@testRes1", queriedRes.getId());

			OrderMap orderMap = tx.getOrderMap();
			orderMap.add(tx, ModelGenerator.createOrder("@testOrder1", "Test Order", "Test"));
			Order queriedOrder = orderMap.getBy(tx, "Test", "@testOrder1");
			assertNotNull(queriedOrder);
			assertEquals("@testOrder1", queriedOrder.getId());
			tx.commitOnClose();
		}

		try (StrolchTransaction tx = container.getRealm("myRealm").openTx(certificate, "test")) {
			ResourceMap resourceMap = tx.getResourceMap();
			Resource myRealmRes = resourceMap.getBy(tx, "TestType", "MyRealmRes");
			assertNotNull(myRealmRes);
			assertEquals("MyRealmRes", myRealmRes.getId());
			Resource otherRealmRes = resourceMap.getBy(tx, "TestType", "OtherRealmRes");
			assertNull(otherRealmRes);

			OrderMap orderMap = tx.getOrderMap();
			Order myRealmOrder = orderMap.getBy(tx, "TestType", "MyRealmOrder");
			assertNotNull(myRealmOrder);
			assertEquals("MyRealmOrder", myRealmOrder.getId());
			Order otherRealmOrder = orderMap.getBy(tx, "TestType", "OtherRealmOrder");
			assertNull(otherRealmOrder);
			tx.commitOnClose();
		}
		try (StrolchTransaction tx = container.getRealm("otherRealm").openTx(certificate, "test")) {
			ResourceMap resourceMap = tx.getResourceMap();
			Resource otherRealmRes = resourceMap.getBy(tx, "TestType", "OtherRealmRes");
			assertNotNull(otherRealmRes);
			assertEquals("OtherRealmRes", otherRealmRes.getId());
			Resource myRealmRes = resourceMap.getBy(tx, "TestType", "MyRealmRes");
			assertNull(myRealmRes);

			OrderMap orderMap = tx.getOrderMap();
			Order otherRealmOrder = orderMap.getBy(tx, "TestType", "OtherRealmOrder");
			assertNotNull(otherRealmOrder);
			assertEquals("OtherRealmOrder", otherRealmOrder.getId());
			Order myRealmOrder = orderMap.getBy(tx, "TestType", "MyRealmOrder");
			assertNull(myRealmOrder);
			tx.commitOnClose();
		}
	}

	public static StrolchAgent startContainer(String rootPath, String configSrc) {
		File rootPathF = new File(rootPath);
		File configSrcF = new File(configSrc);
		mockRuntime(rootPathF, configSrcF);
		return startContainer(rootPathF);
	}

	public static StrolchAgent startContainer(File rootPathF) {
		StrolchAgent agent = new StrolchAgent();
		agent.setup("dev", rootPathF);
		agent.initialize();
		agent.start();

		return agent;
	}

	public static void destroyContainer(StrolchAgent agent) {
		agent.stop();
		agent.destroy();
	}

	public static void mockRuntime(File rootPathF, File rootSrc) {

		if (!rootPathF.getParentFile().getName().equals(TARGET)) {
			String msg = "Mocking path must be in a maven target: {0}"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, rootPathF.getAbsolutePath());
			throw new RuntimeException(msg);
		}

		File configSrc = new File(rootSrc, RuntimeConfiguration.PATH_CONFIG);
		File dataSrc = new File(rootSrc, RuntimeConfiguration.PATH_DATA);

		if (!configSrc.isDirectory() || !configSrc.canRead()) {
			String msg = "Could not find config source in: {0}"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, configSrc.getAbsolutePath());
			throw new RuntimeException(msg);
		}

		if (rootPathF.exists()) {
			logger.info("Deleting all files in " + rootPathF.getAbsolutePath()); //$NON-NLS-1$
			if (!FileHelper.deleteFile(rootPathF, true)) {
				String msg = "Failed to delete {0}"; //$NON-NLS-1$
				msg = MessageFormat.format(msg, rootPathF.getAbsolutePath());
				throw new RuntimeException(msg);
			}
		}

		if (!rootPathF.mkdirs()) {
			String msg = "Failed to create {0}"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, rootPathF.getAbsolutePath());
			throw new RuntimeException(msg);
		}

		File configPathF = new File(rootPathF, RuntimeConfiguration.PATH_CONFIG);
		configPathF.mkdir();

		if (!FileHelper.copy(configSrc.listFiles(), configPathF, false)) {
			String msg = "Failed to copy source configs from {0} to {1}"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, configSrc.getAbsolutePath(), configPathF.getAbsolutePath());
			throw new RuntimeException(msg);
		}

		if (dataSrc.exists()) {
			File dataPathF = new File(rootPathF, RuntimeConfiguration.PATH_DATA);
			dataPathF.mkdir();

			if (!FileHelper.copy(dataSrc.listFiles(), dataPathF, false)) {
				String msg = "Failed to copy source data from {0} to {1}"; //$NON-NLS-1$
				msg = MessageFormat.format(msg, configSrc.getAbsolutePath(), configPathF.getAbsolutePath());
				throw new RuntimeException(msg);
			}
		}
	}
}
