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
package li.strolch.model;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public enum State {

	CREATED("Created"), //$NON-NLS-1$
	OPEN("Open"), //$NON-NLS-1$
	EXECUTION("Execution"), //$NON-NLS-1$
	CLOSED("Closed"); //$NON-NLS-1$

	private String state;

	/**
	 * @param state
	 */
	private State(String state) {
		this.state = state;
	}

	/**
	 * @return
	 */
	public String getStateName() {
		return this.state;
	}
}
