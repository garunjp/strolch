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
package li.strolch.tutorialwebapp.postinitializer;

import li.strolch.agent.impl.ComponentContainerImpl;
import li.strolch.agent.impl.SimplePostInitializer;
import li.strolch.rest.AgentRef;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 * 
 */
public class PostInitializer extends SimplePostInitializer {

	/**
	 * @param container
	 * @param componentName
	 */
	public PostInitializer(ComponentContainerImpl container, String componentName) {
		super(container, componentName);
	}

	@Override
	public void start() {

		logger.info("Initializing RestfulServices...");
		AgentRef.getInstance().init(getContainer().getAgent());

		logger.info("Started PostInitializer."); //$NON-NLS-1$

		super.start();
	}
}
