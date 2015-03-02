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
package li.strolch.agent.api;

import java.util.List;
import java.util.Set;

import li.strolch.exception.StrolchException;
import li.strolch.model.StrolchRootElement;
import li.strolch.model.parameter.Parameter;
import li.strolch.model.parameter.StringListParameter;
import li.strolch.model.parameter.StringParameter;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.runtime.StrolchConstants;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public interface ElementMap<T extends StrolchRootElement> {

	public boolean hasType(StrolchTransaction tx, String type);

	public boolean hasElement(StrolchTransaction tx, String type, String id);

	public long querySize(StrolchTransaction tx);

	public long querySize(StrolchTransaction tx, String type);

	/**
	 * Returns the element with the type "Template" and the id = type
	 * 
	 * @param tx
	 *            the open {@link StrolchTransaction}
	 * @param type
	 *            The template id to return
	 * @return the template, or null if it does not exist
	 */
	public T getTemplate(StrolchTransaction tx, String type);

	/**
	 * Retrieves the element with the given type and id, or null if it does not exist
	 * 
	 * @param tx
	 *            the open transaction
	 * @param type
	 *            the type of the element to retrieve
	 * @param id
	 *            the id of the element to retrieve
	 * 
	 * @return the element with the type and id, or null if it does not exist
	 */
	public T getBy(StrolchTransaction tx, String type, String id);

	/**
	 * Returns the element which is referenced by the given {@link StringParameter}. A reference {@link Parameter} must
	 * have its interpretation set to the element type being referenced e.g. s
	 * {@link StrolchConstants#INTERPRETATION_ORDER_REF} and the UOM must be set to the element's type and the value is
	 * the id of the element
	 * 
	 * @param tx
	 *            the {@link StrolchTransaction} instance
	 * @param refP
	 *            the {@link StringParameter} which references an element
	 * @param assertExists
	 *            if true, and element does not exist, then a {@link StrolchException} is thrown
	 * 
	 * @return the element found, or null if it does not exist
	 * 
	 * @throws StrolchException
	 *             if the {@link StringParameter} is not a properly configured as a reference parameter
	 */
	public T getBy(StrolchTransaction tx, StringParameter refP, boolean assertExists) throws StrolchException;

	/**
	 * Returns all elements which are referenced by the given {@link StringListParameter}. A reference {@link Parameter}
	 * must have its interpretation set to the element type being referenced e.g. s
	 * {@link StrolchConstants#INTERPRETATION_ORDER_REF} and the UOM must be set to the element's type and the value is
	 * the id of the element
	 * 
	 * @param tx
	 *            the {@link StrolchTransaction} instance
	 * @param refP
	 *            the {@link StringListParameter} which references an element
	 * @param assertExists
	 *            if true, and element does not exist, then a {@link StrolchException} is thrown
	 * 
	 * @return the list of elements found, or the empty list if they do not exist. <b>Note:</b> Any missing elements are
	 *         not returned!
	 * 
	 * @throws StrolchException
	 *             if the {@link StringParameter} is not a properly configured as a reference parameter
	 */
	public List<T> getBy(StrolchTransaction tx, StringListParameter refP, boolean assertExists) throws StrolchException;

	public List<T> getAllElements(StrolchTransaction tx);

	public List<T> getElementsBy(StrolchTransaction tx, String type);

	public Set<String> getTypes(StrolchTransaction tx);

	public Set<String> getAllKeys(StrolchTransaction tx);

	public Set<String> getKeysBy(StrolchTransaction tx, String type);

	public void add(StrolchTransaction tx, T element);

	public void addAll(StrolchTransaction tx, List<T> elements);

	public T update(StrolchTransaction tx, T element);

	public List<T> updateAll(StrolchTransaction tx, List<T> elements);

	public void remove(StrolchTransaction tx, T element);

	public void removeAll(StrolchTransaction tx, List<T> elements);

	public long removeAll(StrolchTransaction tx);

	public long removeAllBy(StrolchTransaction tx, String type);
}
