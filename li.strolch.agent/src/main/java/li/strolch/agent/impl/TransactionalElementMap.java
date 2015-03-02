package li.strolch.agent.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import li.strolch.agent.api.ElementMap;
import li.strolch.exception.StrolchException;
import li.strolch.model.StrolchRootElement;
import li.strolch.model.parameter.Parameter;
import li.strolch.model.parameter.StringListParameter;
import li.strolch.model.parameter.StringParameter;
import li.strolch.persistence.api.StrolchDao;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.runtime.StrolchConstants;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 * 
 * @param <T>
 */
public abstract class TransactionalElementMap<T extends StrolchRootElement> implements ElementMap<T> {

	protected abstract StrolchDao<T> getDao(StrolchTransaction tx);

	@Override
	public boolean hasType(StrolchTransaction tx, String type) {
		return getDao(tx).queryTypes().contains(type);
	}

	@Override
	public boolean hasElement(StrolchTransaction tx, String type, String id) {
		return getDao(tx).hasElement(type, id);
	}

	@Override
	public long querySize(StrolchTransaction tx) {
		return getDao(tx).querySize();
	}

	@Override
	public long querySize(StrolchTransaction tx, String type) {
		return getDao(tx).querySize(type);
	}

	@Override
	public T getTemplate(StrolchTransaction tx, String type) {
		return getBy(tx, StrolchConstants.TEMPLATE, type);
	}

	@Override
	public T getBy(StrolchTransaction tx, String type, String id) {
		return getDao(tx).queryBy(type, id);
	}

	protected abstract void assertIsRefParam(Parameter<?> refP);

	@Override
	public T getBy(StrolchTransaction tx, StringParameter refP, boolean assertExists) throws StrolchException {
		assertIsRefParam(refP);
		String type = refP.getUom();
		String id = refP.getValue();
		T element = getBy(tx, type, id);
		if (assertExists && element == null) {
			String msg = "The element for refP {0} with id {1} does not exist!"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, refP.getLocator(), id);
			throw new StrolchException(msg);
		}
		return element;
	}

	@Override
	public List<T> getBy(StrolchTransaction tx, StringListParameter refP, boolean assertExists) throws StrolchException {
		assertIsRefParam(refP);

		List<T> elements = new ArrayList<>();
		String type = refP.getUom();
		List<String> ids = refP.getValue();

		for (String id : ids) {
			T element = getBy(tx, type, id);
			if (element != null) {
				elements.add(element);
			} else if (assertExists) {
				if (assertExists && element == null) {
					String msg = "The element for refP {0} with id {1} does not exist!"; //$NON-NLS-1$
					msg = MessageFormat.format(msg, refP.getLocator(), id);
					throw new StrolchException(msg);
				}
			}
		}

		return elements;
	}

	@Override
	public List<T> getAllElements(StrolchTransaction tx) {
		return getDao(tx).queryAll();
	}

	@Override
	public List<T> getElementsBy(StrolchTransaction tx, String type) {
		return getDao(tx).queryAll(type);
	}

	@Override
	public Set<String> getTypes(StrolchTransaction tx) {
		return getDao(tx).queryTypes();
	}

	@Override
	public Set<String> getAllKeys(StrolchTransaction tx) {
		return getDao(tx).queryKeySet();
	}

	@Override
	public Set<String> getKeysBy(StrolchTransaction tx, String type) {
		return getDao(tx).queryKeySet(type);
	}

	@Override
	public void add(StrolchTransaction tx, T element) {
		getDao(tx).save(element);
	}

	@Override
	public T update(StrolchTransaction tx, T element) {
		getDao(tx).update(element);
		return element;
	}

	@Override
	public void remove(StrolchTransaction tx, T element) {
		getDao(tx).remove(element);
	}

	@Override
	public void addAll(StrolchTransaction tx, List<T> elements) {
		getDao(tx).saveAll(elements);
	}

	@Override
	public List<T> updateAll(StrolchTransaction tx, List<T> elements) {
		getDao(tx).updateAll(elements);
		return elements;
	}

	@Override
	public void removeAll(StrolchTransaction tx, List<T> elements) {
		getDao(tx).removeAll(elements);
	}

	@Override
	public long removeAll(StrolchTransaction tx) {
		return getDao(tx).removeAll();
	}

	@Override
	public long removeAllBy(StrolchTransaction tx, String type) {
		return getDao(tx).removeAllBy(type);
	}
}
