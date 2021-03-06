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

import li.strolch.model.Order;
import li.strolch.model.Resource;
import li.strolch.model.xml.StrolchElementListener;
import li.strolch.persistence.api.OrderDao;
import li.strolch.persistence.api.ResourceDao;
import li.strolch.persistence.api.StrolchTransaction;

public class StoreToDaoElementListener implements StrolchElementListener {

	private StrolchTransaction tx;
	private ResourceDao resourceDao;
	private OrderDao orderDao;

	public StoreToDaoElementListener(StrolchTransaction tx) {
		this.tx = tx;
		this.resourceDao = tx.getPersistenceHandler().getResourceDao(this.tx);
		this.orderDao = tx.getPersistenceHandler().getOrderDao(this.tx);
	}

	@Override
	public void notifyResource(Resource resource) {
		this.resourceDao.save(resource);
	}

	@Override
	public void notifyOrder(Order order) {
		this.orderDao.save(order);
	}
}