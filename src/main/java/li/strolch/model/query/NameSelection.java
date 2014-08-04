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
package li.strolch.model.query;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class NameSelection extends StrolchElementSelection {

	private String name;
	private boolean contains;
	private boolean caseInsensitive;

	/**
	 * @param name
	 */
	public NameSelection(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	public boolean isContains() {
		return this.contains;
	}

	public boolean isCaseInsensitive() {
		return this.caseInsensitive;
	}

	public NameSelection contains(boolean contains) {
		this.contains = contains;
		return this;
	}

	public NameSelection caseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = true;
		return this;
	}

	@Override
	public void accept(StrolchElementSelectionVisitor visitor) {
		visitor.visit(this);
	}
}
