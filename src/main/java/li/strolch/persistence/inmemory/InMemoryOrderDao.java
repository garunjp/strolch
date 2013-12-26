package li.strolch.persistence.inmemory;

import java.util.List;

import li.strolch.model.Order;
import li.strolch.model.query.OrderQuery;
import li.strolch.persistence.api.OrderDao;
import li.strolch.runtime.query.inmemory.InMemoryOrderQueryVisitor;
import li.strolch.runtime.query.inmemory.InMemoryQuery;

public class InMemoryOrderDao extends InMemoryDao<Order> implements OrderDao {

	@Override
	public List<Order> doQuery(OrderQuery orderQuery) {
		InMemoryOrderQueryVisitor visitor = new InMemoryOrderQueryVisitor();
		InMemoryQuery<Order> query = visitor.visit(orderQuery);
		return query.doQuery(this);
	}
}
