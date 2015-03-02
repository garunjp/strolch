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
package li.strolch.model.parameter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import li.strolch.exception.StrolchException;
import li.strolch.model.Tags;
import li.strolch.model.visitor.ParameterVisitor;

import org.w3c.dom.Element;

import ch.eitchnet.utils.helper.StringHelper;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class StringListParameter extends AbstractParameter<List<String>> implements ListParameter<String> {

	public static final String TYPE = "StringList"; //$NON-NLS-1$
	private static final long serialVersionUID = 1L;

	protected List<String> value;

	/**
	 * Empty constructor
	 */
	public StringListParameter() {
		//
	}

	/**
	 * Default constructor
	 *
	 * @param id
	 * @param name
	 * @param value
	 */
	public StringListParameter(String id, String name, List<String> value) {
		super(id, name);

		setValue(value);
	}

	/**
	 * DOM Constructor
	 *
	 * @param element
	 */
	public StringListParameter(Element element) {
		super.fromDom(element);

		String valueS = element.getAttribute(Tags.VALUE);
		if (StringHelper.isEmpty(valueS)) {
			String msg = MessageFormat.format("No value defined for {0}", this.id); //$NON-NLS-1$
			throw new StrolchException(msg);
		}

		setValue(parseFromString(valueS));
	}

	@Override
	public String getValueAsString() {
		if (this.value.isEmpty()) {
			return StringHelper.EMPTY;
		}

		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = this.value.iterator();
		while (iter.hasNext()) {

			sb.append(iter.next());

			if (iter.hasNext()) {
				sb.append(VALUE_SEPARATOR);
			}
		}

		return sb.toString();
	}

	@Override
	public List<String> getValue() {
		return new ArrayList<>(this.value);
	}

	@Override
	public void setValue(List<String> value) {
		validateValue(value);
		if (this.value == null) {
			this.value = new ArrayList<>(value.size());
		}
		this.value.clear();
		this.value.addAll(value);
	}

	@Override
	public void addValue(String value) {
		this.value.add(value);
	}

	@Override
	public boolean removeValue(String value) {
		return this.value.remove(value);
	}

	@Override
	public void clearValue() {
		this.value.clear();
	}

	@Override
	public boolean isValueEmpty() {
		return this.value.isEmpty();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public StringListParameter getClone() {
		StringListParameter clone = new StringListParameter();

		super.fillClone(clone);

		clone.setValue(this.value);

		return clone;
	}

	@Override
	public <U> U accept(ParameterVisitor visitor) {
		return visitor.visitStringListParam(this);
	}

	public static List<String> parseFromString(String value) {
		if (value.isEmpty()) {
			return Collections.emptyList();
		}

		String[] valueArr = value.split(VALUE_SEPARATOR);
		List<String> values = new ArrayList<>();
		for (String val : valueArr) {
			values.add(val.trim());
		}
		return values;
	}
}
