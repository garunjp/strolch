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

import li.strolch.exception.StrolchException;
import li.strolch.model.Tags;
import li.strolch.model.visitor.ParameterVisitor;

import org.w3c.dom.Element;

import ch.eitchnet.utils.helper.StringHelper;
import ch.eitchnet.utils.iso8601.ISO8601FormatFactory;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class DurationParameter extends AbstractParameter<Long> {

	public static final String TYPE = "Duration"; //$NON-NLS-1$
	private static final long serialVersionUID = 0L;

	private Long value;

	/**
	 * Empty constructor
	 */
	public DurationParameter() {
		//
	}

	/**
	 * Default Constructor
	 *
	 * @param id
	 * @param name
	 * @param value
	 */
	public DurationParameter(String id, String name, Long value) {
		super(id, name);
		setValue(value);
	}

	/**
	 * DOM Constructor
	 *
	 * @param element
	 */
	public DurationParameter(Element element) {
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
		return ISO8601FormatFactory.getInstance().formatDuration(this.value);
	}

	@Override
	public Long getValue() {
		return this.value;
	}

	@Override
	public void setValue(Long value) {
		validateValue(value);
		this.value = value;
	}

	@Override
	public String getType() {
		return DurationParameter.TYPE;
	}

	@Override
	public DurationParameter getClone() {
		DurationParameter clone = new DurationParameter();

		super.fillClone(clone);

		clone.setValue(this.value);

		return clone;
	}

	@Override
	public <U> U accept(ParameterVisitor visitor) {
		return visitor.visitDurationParam(this);
	}

	public static Long parseFromString(String valueS) {
		return ISO8601FormatFactory.getInstance().getDurationFormat().parse(valueS);
	}
}
