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
package li.strolch.agent.impl;

import java.util.List;
import java.util.Set;

import li.strolch.agent.api.AuditTrail;
import li.strolch.model.audit.Audit;
import li.strolch.model.audit.AuditQuery;
import li.strolch.model.audit.AuditVisitor;
import li.strolch.persistence.api.StrolchTransaction;
import ch.eitchnet.utils.collections.DateRange;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class NoStrategyAuditTrail implements AuditTrail {

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public boolean hasAudit(StrolchTransaction tx, String type, Long id) {
		return false;
	}

	@Override
	public long querySize(StrolchTransaction tx, DateRange dateRange) {
		return 0;
	}

	@Override
	public long querySize(StrolchTransaction tx, String type, DateRange dateRange) {
		return 0;
	}

	@Override
	public Set<String> getTypes(StrolchTransaction tx) {
		return null;
	}

	@Override
	public Audit getBy(StrolchTransaction tx, String type, Long id) {
		return null;
	}

	@Override
	public List<Audit> getAllElements(StrolchTransaction tx, String type, DateRange dateRange) {
		return null;
	}

	@Override
	public void add(StrolchTransaction tx, Audit audit) {
		//
	}

	@Override
	public void addAll(StrolchTransaction tx, List<Audit> audits) {
		//
	}

	@Override
	public Audit update(StrolchTransaction tx, Audit audit) {
		return null;
	}

	@Override
	public List<Audit> updateAll(StrolchTransaction tx, List<Audit> audits) {
		return null;
	}

	@Override
	public void remove(StrolchTransaction tx, Audit audit) {
		//
	}

	@Override
	public void removeAll(StrolchTransaction tx, List<Audit> audits) {
		//
	}

	@Override
	public long removeAll(StrolchTransaction tx, String type, DateRange dateRange) {
		return 0;
	}

	@Override
	public <U> List<U> doQuery(StrolchTransaction tx, AuditQuery query, AuditVisitor<U> auditVisitor) {
		return null;
	}
}
