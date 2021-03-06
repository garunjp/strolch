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
package li.strolch.agent.impl;

import ch.eitchnet.privilege.handler.SystemUserAction;
import ch.eitchnet.privilege.model.PrivilegeContext;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class StartRealms implements SystemUserAction {

	private final DefaultRealmHandler defaultRealmHandler;

	/**
	 * @param defaultRealmHandler
	 */
	StartRealms(DefaultRealmHandler defaultRealmHandler) {
		this.defaultRealmHandler = defaultRealmHandler;
	}

	@Override
	public void execute(PrivilegeContext privilegeContext) {
		for (String realmName : this.defaultRealmHandler.getRealms().keySet()) {
			InternalStrolchRealm realm = this.defaultRealmHandler.getRealms().get(realmName);
			realm.start(privilegeContext);
		}
	}
}