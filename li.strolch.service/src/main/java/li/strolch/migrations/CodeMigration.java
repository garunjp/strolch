/*
 * Copyright 2015 Robert von Burg <eitch@eitchnet.ch>
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
package li.strolch.migrations;

import java.io.File;

import li.strolch.agent.api.ComponentContainer;
import ch.eitchnet.privilege.model.Certificate;
import ch.eitchnet.utils.Version;

public class CodeMigration extends Migration {

	public CodeMigration(String realm, Version version, File dataFile) {
		super(realm, version, dataFile);
	}

	public CodeMigration(String realm, Version version) {
		super(realm, version, null);
	}

	@Override
	public void migrate(ComponentContainer container, Certificate certificate) {
		logger.info("[" + this.realm + "] Running no-op migration " + this.version);
	}
}
