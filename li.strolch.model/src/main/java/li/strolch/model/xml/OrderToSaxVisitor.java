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

import java.text.MessageFormat;

import li.strolch.model.Order;
import li.strolch.model.OrderVisitor;
import li.strolch.model.Tags;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class OrderToSaxVisitor extends StrolchElementToSaxVisitor implements OrderVisitor<Void> {

	public OrderToSaxVisitor(ContentHandler contentHandler) {
		super(contentHandler);
	}

	@Override
	public Void visit(Order order) {
		try {

			this.contentHandler.startElement(null, null, Tags.ORDER, attributesFor(order));
			toSax(order);
			this.contentHandler.endElement(null, null, Tags.ORDER);

		} catch (SAXException e) {
			String msg = "Failed to transform Order {0} to XML due to {1}"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, order.getLocator(), e.getMessage());
			throw new RuntimeException(msg, e);
		}

		return null;
	}
}
