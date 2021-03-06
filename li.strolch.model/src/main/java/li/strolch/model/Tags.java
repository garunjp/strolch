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

@SuppressWarnings("nls")
public class Tags {

	public static final String CDATA = "CDATA";
	public static final String ID = "Id";
	public static final String NAME = "Name";
	public static final String TYPE = "Type";
	public static final String DATE = "Date";
	public static final String STATE = "State";
	public static final String VALUE = "Value";
	public static final String TIME = "Time";
	public static final String INTERPRETATION = "Interpretation";
	public static final String UOM = "Uom";
	public static final String HIDDEN = "Hidden";
	public static final String INDEX = "Index";
	public static final String PARAMETER = "Parameter";
	public static final String TIMED_STATE = "TimedState";
	public static final String PARAMETERIZED_ELEMENT = "ParameterizedElement";
	public static final String RESOURCE = "Resource";
	public static final String ORDER = "Order";
	public static final String PARAMETER_BAG = "ParameterBag";
	public static final String STROLCH_MODEL = "StrolchModel";
	public static final String INCLUDE_FILE = "IncludeFile";
	public static final String FILE = "file";
	public static final String BAG = "Bag";
	public static final String AUDIT = "Audit";

	public class Audit {
		public static final String ID = Tags.ID;

		public static final String USERNAME = "Username";
		public static final String FIRSTNAME = "Firstname";
		public static final String LASTNAME = "Lastname";
		public static final String DATE = "Date";

		public static final String ELEMENT_TYPE = "ElementType";
		public static final String ELEMENT_SUB_TYPE = "ElementSubType";
		public static final String ELEMENT_ACCESSED = "ElementAccessed";
		public static final String NEW_VERSION = "NewVersion";

		public static final String ACTION = "Action";
		public static final String ACCESS_TYPE = "AccessType";
	}
}
