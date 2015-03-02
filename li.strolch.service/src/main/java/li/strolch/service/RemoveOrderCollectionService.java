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
package li.strolch.service;

import java.util.ArrayList;
import java.util.List;

import li.strolch.command.RemoveOrderCollectionCommand;
import li.strolch.model.Locator;
import li.strolch.model.Order;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.service.api.AbstractService;
import li.strolch.service.api.ServiceArgument;
import li.strolch.service.api.ServiceResult;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class RemoveOrderCollectionService extends
		AbstractService<RemoveOrderCollectionService.RemoveOrderCollectionArg, ServiceResult> {

	private static final long serialVersionUID = 1L;

	@Override
	protected ServiceResult getResultInstance() {
		return new ServiceResult();
	}

	@Override
	protected ServiceResult internalDoService(RemoveOrderCollectionArg arg) {

		try (StrolchTransaction tx = openTx(arg.realm)) {

			List<Order> orders = new ArrayList<>(arg.locators.size());
			for (Locator locator : arg.locators) {
				Order resource = tx.findElement(locator);
				orders.add(resource);
			}

			RemoveOrderCollectionCommand command = new RemoveOrderCollectionCommand(getContainer(), tx);
			command.setOrders(orders);

			tx.addCommand(command);
			tx.commitOnClose();
		}

		return ServiceResult.success();
	}

	public static class RemoveOrderCollectionArg extends ServiceArgument {
		private static final long serialVersionUID = 1L;
		public List<Locator> locators;
	}
}
