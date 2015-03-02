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
public class StrolchTypeNavigation implements Navigation {

	private String type;

	public StrolchTypeNavigation(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	@Override
	public void accept(QueryVisitor visitor) {
		accept((StrolchElementSelectionVisitor) visitor);
	}

	public void accept(StrolchElementSelectionVisitor visitor) {
		visitor.visit(this);
	}
}
