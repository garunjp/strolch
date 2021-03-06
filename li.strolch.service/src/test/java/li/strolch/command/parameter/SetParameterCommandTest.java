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
package li.strolch.command.parameter;

import li.strolch.agent.api.ComponentContainer;
import li.strolch.command.AbstractRealmCommandTest;
import li.strolch.model.Locator;
import li.strolch.model.parameter.Parameter;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.service.api.Command;

import org.junit.Before;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class SetParameterCommandTest extends AbstractRealmCommandTest {

	private Locator locator;
	private String valueAsString;

	@Before
	public void before() {
		this.locator = Locator.valueOf("Resource/Ball/yellow/Bag/parameters/owner");
		this.valueAsString = "someOtherDude";
	}

	@Override
	protected Command getCommandInstance(ComponentContainer container, StrolchTransaction tx) {

		Parameter<?> parameter = tx.findElement(this.locator);

		SetParameterCommand command = new SetParameterCommand(container, tx);
		command.setValueAsString(this.valueAsString);
		command.setParameter(parameter);
		return command;
	}
}
