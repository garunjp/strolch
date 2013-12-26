package li.strolch.runtime.agent.impl;

import li.strolch.model.Order;
import li.strolch.model.query.OrderQuery;
import li.strolch.persistence.api.OrderDao;
import li.strolch.persistence.api.PersistenceHandler;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.runtime.agent.api.OrderMap;

public class TransactionalOrderMap extends TransactionalElementMap<Order, OrderQuery> implements OrderMap {

	public TransactionalOrderMap(String realm, PersistenceHandler persistenceHandler) {
		super(realm, persistenceHandler);
	}

	@Override
	protected OrderDao getDao(StrolchTransaction tx) {
		return tx.getOrderDao();
	}
}
