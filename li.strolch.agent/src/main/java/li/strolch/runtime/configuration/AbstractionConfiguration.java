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
package li.strolch.runtime.configuration;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.eitchnet.utils.helper.StringHelper;

public abstract class AbstractionConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(AbstractionConfiguration.class);

	private final String name;
	private final Map<String, String> configurationValues;

	public AbstractionConfiguration(String name, Map<String, String> configurationValues) {
		this.name = name;
		this.configurationValues = configurationValues;
	}

	public String getName() {
		return this.name;
	}

	public boolean hasProperty(String key) {
		return this.configurationValues.containsKey(key);
	}

	public Set<String> getPropertyKeys() {
		return new HashSet<>(this.configurationValues.keySet());
	}

	public String[] getStringArray(String key, String defValue) {
		String value = getValue(key, defValue);
		String[] values = value.split(","); //$NON-NLS-1$
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}

		return values;
	}

	public String getString(String key, String defValue) {
		return getValue(key, defValue);
	}

	public boolean getBoolean(String key, Boolean defValue) {
		String value = this.configurationValues.get(key);
		if (StringHelper.isNotEmpty(value)) {
			if (value.equalsIgnoreCase("true")) //$NON-NLS-1$
				return true;
			else if (value.equalsIgnoreCase("false")) //$NON-NLS-1$
				return false;

			String msg = "Component {0} has non-boolean configuration value for {1} = {2}!"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, this.name, key, value);
			throw new StrolchConfigurationException(msg);
		}

		assertDefValueExist(key, defValue);
		logDefValueUse(key, defValue);
		return defValue;
	}

	public int getInt(String key, int defValue) {
		String value = this.configurationValues.get(key);
		if (StringHelper.isNotEmpty(value)) {

			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				String msg = "Component {0} has non-integer configuration value for {1} = {2}!"; //$NON-NLS-1$
				msg = MessageFormat.format(msg, this.name, key, value);
				throw new StrolchConfigurationException(msg);
			}
		}

		assertDefValueExist(key, defValue);
		logDefValueUse(key, defValue);
		return defValue;
	}

	public long getLong(String key, long defValue) {
		String value = this.configurationValues.get(key);
		if (StringHelper.isNotEmpty(value)) {

			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				String msg = "Component {0} has non-long configuration value for {1} = {2}!"; //$NON-NLS-1$
				msg = MessageFormat.format(msg, this.name, key, value);
				throw new StrolchConfigurationException(msg);
			}
		}

		assertDefValueExist(key, defValue);
		logDefValueUse(key, defValue);
		return defValue;
	}

	public File getConfigFile(String key, String defValue, RuntimeConfiguration configuration) {
		String value = getValue(key, defValue);

		File configFile = new File(configuration.getConfigPath(), value);
		if (!configFile.isFile() || !configFile.canRead()) {
			String msg = "Component {0} requires configuration file for configuration property ''{1}'' which does not exist with value: {2}"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, this.name, key, value);
			throw new StrolchConfigurationException(msg);
		}
		return configFile;
	}

	public File getDataDir(String key, String defValue, RuntimeConfiguration configuration, boolean checkExists) {
		String value = getValue(key, defValue);

		File dataDir = new File(configuration.getDataPath(), value);
		if (checkExists && !dataDir.isDirectory() || !dataDir.canRead()) {
			String msg = "Component {0} requires data directory for configuration property ''{1}'' which does not exist with value: {2}"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, this.name, key, value);
			throw new StrolchConfigurationException(msg);
		}
		return dataDir;
	}

	public File getDataFile(String key, String defValue, RuntimeConfiguration configuration, boolean checkExists) {
		String value = getValue(key, defValue);

		File dataFile = new File(configuration.getDataPath(), value);
		if (checkExists && !dataFile.isFile() || !dataFile.canRead()) {
			String msg = "Component {0} requires data file for configuration property ''{1}'' which does not exist with value: {2}"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, this.name, key, value);
			throw new StrolchConfigurationException(msg);
		}
		return dataFile;
	}

	private String getValue(String key, String defValue) {
		String value = this.configurationValues.get(key);
		if (StringHelper.isEmpty(value)) {
			assertDefValueExist(key, defValue);
			logDefValueUse(key, defValue);
			value = defValue;
		}
		return value;
	}

	private void logDefValueUse(String key, Object defValue) {
		String msg = "{0}: Using default for key {1}={2}"; //$NON-NLS-1$
		msg = MessageFormat.format(msg, this.name, key, defValue);
		logger.info(msg);
	}

	private void assertDefValueExist(String key, Object defValue) {
		if (defValue == null) {
			String msg = "Component {0} is missing the configuration value with key ''{1}''!"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, this.name, key);
			throw new StrolchConfigurationException(msg);
		}
	}
}
