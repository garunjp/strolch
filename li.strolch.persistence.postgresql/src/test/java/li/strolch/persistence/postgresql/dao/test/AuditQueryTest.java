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
package li.strolch.persistence.postgresql.dao.test;

import static li.strolch.persistence.postgresql.dao.test.CachedDaoTest.CONFIG_SRC;
import static li.strolch.persistence.postgresql.dao.test.CachedDaoTest.DB_PASSWORD;
import static li.strolch.persistence.postgresql.dao.test.CachedDaoTest.DB_STORE_PATH_DIR;
import static li.strolch.persistence.postgresql.dao.test.CachedDaoTest.DB_URL;
import static li.strolch.persistence.postgresql.dao.test.CachedDaoTest.DB_USERNAME;
import static li.strolch.persistence.postgresql.dao.test.CachedDaoTest.RUNTIME_PATH;
import static li.strolch.persistence.postgresql.dao.test.CachedDaoTest.dropSchema;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import li.strolch.agent.api.AuditTrail;
import li.strolch.agent.api.StrolchRealm;
import li.strolch.model.ModelGenerator;
import li.strolch.model.Tags;
import li.strolch.model.audit.AccessType;
import li.strolch.model.audit.Audit;
import li.strolch.model.audit.AuditQuery;
import li.strolch.persistence.api.AbstractTransaction;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.persistence.postgresql.PostgreSqlAuditQueryVisitor;
import li.strolch.runtime.StrolchConstants;
import li.strolch.testbase.runtime.RuntimeMock;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.eitchnet.privilege.model.Certificate;
import ch.eitchnet.utils.StringMatchMode;
import ch.eitchnet.utils.collections.DateRange;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class AuditQueryTest {

	private static final Logger logger = LoggerFactory.getLogger(AuditQueryTest.class);
	private static RuntimeMock runtimeMock;

	private static Date past;
	private static Date earlier;
	private static Date current;
	private static Date later;
	private static Date future;

	@BeforeClass
	public static void beforeClass() throws Exception {

		dropSchema(DB_URL, DB_USERNAME, DB_PASSWORD);

		File rootPath = new File(RUNTIME_PATH);
		File configSrc = new File(CONFIG_SRC);
		runtimeMock = new RuntimeMock();
		runtimeMock.mockRuntime(rootPath, configSrc);
		new File(rootPath, DB_STORE_PATH_DIR).mkdir();
		runtimeMock.startContainer();

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2000, 1, 1);
		past = cal.getTime();
		cal.set(2000, 4, 1);
		earlier = cal.getTime();
		cal.set(2000, 6, 1);
		current = cal.getTime();
		cal.set(2000, 8, 1);
		later = cal.getTime();
		cal.set(2000, 11, 1);
		future = cal.getTime();

		Certificate cert = runtimeMock.getPrivilegeHandler().authenticate("test", "test".getBytes());
		StrolchRealm realm = runtimeMock.getRealm(StrolchConstants.DEFAULT_REALM);
		int i = 0;
		try (StrolchTransaction tx = realm.openTx(cert, "test")) {
			((AbstractTransaction) tx).setSuppressAudits(true);
			AuditTrail auditTrail = tx.getAuditTrail();

			Audit randomAudit;
			randomAudit = ModelGenerator.randomAudit();
			randomAudit.setId(i++);
			randomAudit.setUsername("earlier");
			randomAudit.setDate(earlier);
			randomAudit.setAccessType(AccessType.CREATE);
			randomAudit.setAction("create");
			randomAudit.setElementAccessed(randomAudit.getAccessType().name());
			auditTrail.add(tx, randomAudit);

			randomAudit = ModelGenerator.randomAudit();
			randomAudit.setId(i++);
			randomAudit.setDate(current);
			randomAudit.setUsername("current");
			randomAudit.setAccessType(AccessType.READ);
			randomAudit.setAction("read");
			randomAudit.setElementAccessed(randomAudit.getAccessType().name());
			auditTrail.add(tx, randomAudit);

			randomAudit = ModelGenerator.randomAudit();
			randomAudit.setId(i++);
			randomAudit.setDate(later);
			randomAudit.setUsername("later");
			randomAudit.setAccessType(AccessType.UPDATE);
			randomAudit.setAction("update");
			randomAudit.setElementAccessed(randomAudit.getAccessType().name());
			auditTrail.add(tx, randomAudit);

			randomAudit = ModelGenerator.randomAudit();
			randomAudit.setId(i++);
			randomAudit.setDate(current);
			randomAudit.setUsername("current");
			randomAudit.setAccessType(AccessType.DELETE);
			randomAudit.setAction("delete");
			randomAudit.setElementAccessed(randomAudit.getAccessType().name());
			auditTrail.add(tx, randomAudit);

			randomAudit = ModelGenerator.randomAudit();
			randomAudit.setId(i++);
			randomAudit.setDate(current);
			randomAudit.setUsername("current");
			randomAudit.setAccessType(AccessType.CREATE);
			randomAudit.setAction("create");
			randomAudit.setElementAccessed(randomAudit.getAccessType().name());
			auditTrail.add(tx, randomAudit);

			tx.commitOnClose();
		}
	}

	@AfterClass
	public static void afterClass() {
		runtimeMock.destroyRuntime();
	}

	public Connection openConn() throws SQLException {
		String url = "jdbc:postgresql://localhost/testdb";
		String username = "testuser";
		String password = "test";
		Connection connection = DriverManager.getConnection(url, username, password);
		connection.setAutoCommit(false);
		return connection;
	}

	@Test
	public void shouldQueryTypeAndDateRange() throws SQLException {
		AuditQuery query = new AuditQuery(Tags.AUDIT, new DateRange().from(earlier, true).to(later, true));
		performQuery(query, Arrays.asList("0", "1", "2", "3", "4"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(current, true).to(current, true));
		performQuery(query, Arrays.asList("1", "3", "4"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(current, true));
		performQuery(query, Arrays.asList("1", "2", "3", "4"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().to(current, true));
		performQuery(query, Arrays.asList("0", "1", "3", "4"));

		query = new AuditQuery(Tags.RESOURCE, new DateRange().from(past, true).to(future, true));
		performQuery(query, Arrays.<String> asList());
	}

	@Test
	public void shouldQueryAudits() throws SQLException {
		AuditQuery query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.action().accessTypes(AccessType.CREATE, AccessType.READ);
		performQuery(query, Arrays.asList("0", "1", "4"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.action().accessTypes(AccessType.CREATE);
		performQuery(query, Arrays.asList("0", "4"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.action().accessTypes(AccessType.CREATE, AccessType.READ)
				.actions(StringMatchMode.EQUALS_CASE_SENSITIVE, "create", "read");
		performQuery(query, Arrays.asList("0", "1", "4"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.action().accessTypes(AccessType.CREATE, AccessType.READ)
				.actions(StringMatchMode.EQUALS_CASE_SENSITIVE, "read");
		performQuery(query, Arrays.asList("1"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.element().elementsAccessed(StringMatchMode.CONTAINS_CASE_INSENSITIVE, "crea");
		performQuery(query, Arrays.asList("0", "4"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.element().elementsAccessed(StringMatchMode.CONTAINS_CASE_SENSITIVE, "crea");
		performQuery(query, Arrays.<String> asList());

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.element().elementsAccessed(StringMatchMode.EQUALS_CASE_INSENSITIVE, "create");
		performQuery(query, Arrays.asList("0", "4"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.identity().usernames(StringMatchMode.EQUALS_CASE_INSENSITIVE, "earlier");
		performQuery(query, Arrays.asList("0"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.identity().usernames(StringMatchMode.EQUALS_CASE_INSENSITIVE, "earlier", "later");
		performQuery(query, Arrays.asList("0", "2"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.identity().usernames(StringMatchMode.EQUALS_CASE_INSENSITIVE, "earlier")
				.firstnames(StringMatchMode.CONTAINS_CASE_INSENSITIVE, "enn");
		performQuery(query, Arrays.asList("0"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.identity().usernames(StringMatchMode.EQUALS_CASE_INSENSITIVE, "earlier")
				.firstnames(StringMatchMode.CONTAINS_CASE_INSENSITIVE, "enn")
				.lastnames(StringMatchMode.CONTAINS_CASE_INSENSITIVE, "kennedy");
		performQuery(query, Arrays.asList("0"));

		query = new AuditQuery(Tags.AUDIT, new DateRange().from(past, true).to(future, true));
		query.identity().firstnames(StringMatchMode.CONTAINS_CASE_INSENSITIVE, "enn")
				.lastnames(StringMatchMode.CONTAINS_CASE_INSENSITIVE, "kennedy");
		performQuery(query, Arrays.asList("0", "1", "2", "3", "4"));
	}

	private void performQuery(AuditQuery query, List<String> expected) throws SQLException {
		PostgreSqlAuditQueryVisitor visitor = new PostgreSqlAuditQueryVisitor("id");
		query.accept(visitor);
		List<String> ids = queryIds(visitor);
		assertEquals(new HashSet<>(expected), new HashSet<>(ids));
	}

	private List<String> queryIds(PostgreSqlAuditQueryVisitor visitor) throws SQLException {
		String sql = visitor.getSql();
		logger.info("\n" + sql);
		List<String> ids = new ArrayList<>();
		try (Connection con = openConn()) {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				visitor.setValues(ps);

				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					ids.add(rs.getString(1));
				}
			}
		}

		return ids;
	}
}
