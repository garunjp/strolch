package li.strolch.agent.impl;

import static li.strolch.model.StrolchModelConstants.INTERPRETATION_ORDER_REF;
import static li.strolch.model.StrolchModelConstants.UOM_NONE;

import java.text.MessageFormat;
import java.util.List;

import li.strolch.agent.api.OrderMap;
import li.strolch.exception.StrolchException;
import li.strolch.model.Order;
import li.strolch.model.OrderVisitor;
import li.strolch.model.parameter.Parameter;
import li.strolch.model.query.OrderQuery;
import li.strolch.persistence.api.OrderDao;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.persistence.inmemory.InMemoryOrderDao;

public class CachedOrderMap extends CachedElementMap<Order> implements OrderMap {

	private OrderDao cachedDao;

	public CachedOrderMap() {
		super();
		this.cachedDao = new InMemoryOrderDao();
	}

	@Override
	protected void assertIsRefParam(Parameter<?> refP) {

		String interpretation = refP.getInterpretation();
		if (!interpretation.equals(INTERPRETATION_ORDER_REF)) {
			String msg = "{0} is not an Order reference as its interpretation is not {1} it is {2}"; //$NON-NLS-1$
			throw new StrolchException(MessageFormat.format(msg, refP.getLocator(), INTERPRETATION_ORDER_REF,
					interpretation));
		}

		if (refP.getUom().equals(UOM_NONE)) {
			String msg = "{0} is not an Order reference as its UOM is not set to a type!"; //$NON-NLS-1$
			throw new StrolchException(MessageFormat.format(msg, refP.getLocator()));
		}
	}

	@Override
	protected OrderDao getDbDao(StrolchTransaction tx) {
		return tx.getPersistenceHandler().getOrderDao(tx);
	}

	@Override
	protected OrderDao getCachedDao() {
		return this.cachedDao;
	}

	@Override
	public <U> List<U> doQuery(StrolchTransaction tx, OrderQuery query, OrderVisitor<U> orderVisitor) {
		return getCachedDao().doQuery(query, orderVisitor);
	}
}
