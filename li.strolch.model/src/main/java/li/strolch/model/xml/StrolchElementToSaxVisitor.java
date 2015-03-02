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
package li.strolch.model.xml;

import static li.strolch.model.StrolchModelConstants.INTERPRETATION_NONE;
import static li.strolch.model.StrolchModelConstants.UOM_NONE;

import java.util.Set;
import java.util.SortedSet;

import li.strolch.model.GroupedParameterizedElement;
import li.strolch.model.Order;
import li.strolch.model.ParameterBag;
import li.strolch.model.Resource;
import li.strolch.model.StrolchElement;
import li.strolch.model.Tags;
import li.strolch.model.parameter.Parameter;
import li.strolch.model.timedstate.StrolchTimedState;
import li.strolch.model.timevalue.ITimeValue;
import li.strolch.model.timevalue.IValue;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ch.eitchnet.utils.iso8601.ISO8601FormatFactory;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public abstract class StrolchElementToSaxVisitor {

	protected ContentHandler contentHandler;

	protected StrolchElementToSaxVisitor(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	protected AttributesImpl attributesFor(StrolchElement element) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(null, null, Tags.ID, Tags.CDATA, element.getId());
		attributes.addAttribute(null, null, Tags.NAME, Tags.CDATA, element.getName());
		attributes.addAttribute(null, null, Tags.TYPE, Tags.CDATA, element.getType());
		return attributes;
	}

	protected AttributesImpl attributesFor(Order order) {
		AttributesImpl attributes = attributesFor((StrolchElement) order);
		attributes.addAttribute(null, null, Tags.STATE, Tags.CDATA, order.getState().name());
		attributes.addAttribute(null, null, Tags.DATE, Tags.CDATA,
				ISO8601FormatFactory.getInstance().formatDate(order.getDate()));
		return attributes;
	}

	protected AttributesImpl attributesFor(Parameter<?> parameter) {
		AttributesImpl attributes = attributesFor((StrolchElement) parameter);
		attributes.addAttribute(null, null, Tags.VALUE, Tags.CDATA, parameter.getValueAsString());

		if (!UOM_NONE.equals(parameter.getUom())) {
			attributes.addAttribute(null, null, Tags.UOM, Tags.CDATA, parameter.getUom());
		}
		if (!INTERPRETATION_NONE.equals(parameter.getInterpretation())) {
			attributes.addAttribute(null, null, Tags.INTERPRETATION, Tags.CDATA, parameter.getInterpretation());
		}
		if (parameter.isHidden()) {
			attributes.addAttribute(null, null, Tags.HIDDEN, Tags.CDATA, Boolean.toString(parameter.isHidden()));
		}
		if (parameter.getIndex() != 0) {
			attributes.addAttribute(null, null, Tags.INDEX, Tags.CDATA, Integer.toString(parameter.getIndex()));
		}

		return attributes;
	}

	private Attributes attributesFor(StrolchTimedState<IValue<?>> state) {
		AttributesImpl attributes = attributesFor((StrolchElement) state);

		if (!UOM_NONE.equals(state.getUom())) {
			attributes.addAttribute(null, null, Tags.UOM, Tags.CDATA, state.getUom());
		}
		if (!INTERPRETATION_NONE.equals(state.getInterpretation())) {
			attributes.addAttribute(null, null, Tags.INTERPRETATION, Tags.CDATA, state.getInterpretation());
		}
		if (state.isHidden()) {
			attributes.addAttribute(null, null, Tags.HIDDEN, Tags.CDATA, Boolean.toString(state.isHidden()));
		}
		if (state.getIndex() != 0) {
			attributes.addAttribute(null, null, Tags.INDEX, Tags.CDATA, Integer.toString(state.getIndex()));
		}

		return attributes;
	}

	private Attributes attributesFor(ITimeValue<IValue<?>> value) {
		AttributesImpl attributes = new AttributesImpl();
		ISO8601FormatFactory df = ISO8601FormatFactory.getInstance();
		attributes.addAttribute(null, null, Tags.TIME, Tags.CDATA, df.formatDate(value.getTime()));
		attributes.addAttribute(null, null, Tags.VALUE, Tags.CDATA, value.getValue().getValueAsString());
		return attributes;
	}

	protected void toSax(GroupedParameterizedElement parameterizedElement) throws SAXException {
		Set<String> bagKeySet = parameterizedElement.getParameterBagKeySet();
		for (String bagKey : bagKeySet) {
			ParameterBag parameterBag = parameterizedElement.getParameterBag(bagKey);
			this.contentHandler.startElement(null, null, Tags.PARAMETER_BAG, attributesFor(parameterBag));

			Set<String> parameterKeySet = parameterBag.getParameterKeySet();
			for (String paramKey : parameterKeySet) {
				Parameter<?> parameter = parameterBag.getParameter(paramKey);
				this.contentHandler.startElement(null, null, Tags.PARAMETER, attributesFor(parameter));
				this.contentHandler.endElement(null, null, Tags.PARAMETER);
			}

			this.contentHandler.endElement(null, null, Tags.PARAMETER_BAG);
		}
	}

	protected void toSax(Resource resource) throws SAXException {
		toSax((GroupedParameterizedElement) resource);

		Set<String> stateKeySet = resource.getTimedStateKeySet();
		for (String stateKey : stateKeySet) {
			StrolchTimedState<IValue<?>> state = resource.getTimedState(stateKey);
			this.contentHandler.startElement(null, null, Tags.TIMED_STATE, attributesFor(state));

			SortedSet<ITimeValue<IValue<?>>> values = state.getTimeEvolution().getValues();
			for (ITimeValue<IValue<?>> value : values) {
				this.contentHandler.startElement(null, null, Tags.VALUE, attributesFor(value));
				this.contentHandler.endElement(null, null, Tags.VALUE);
			}
			this.contentHandler.endElement(null, null, Tags.TIMED_STATE);
		}
	}
}
