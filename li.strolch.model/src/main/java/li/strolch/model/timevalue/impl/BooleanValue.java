/*
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
package li.strolch.model.timevalue.impl;

import java.io.Serializable;

import li.strolch.model.timevalue.ITimeValue;
import li.strolch.model.timevalue.IValue;

/**
 * {@link IValue} implementation to work with Boolean valued {@link ITimeValue} objects
 *
 * @author Martin Smock <smock.martin@gmail.com>
 */
public class BooleanValue implements IValue<Boolean>, Serializable {

	private static final long serialVersionUID = 1L;

	public static final BooleanValue NEUTRAL = new BooleanValue(false);

	private Boolean value;

	public BooleanValue(Boolean value) {
		this.value = value;
	}

	public BooleanValue(boolean value) {
		this.value = Boolean.valueOf(value);
	}

	public BooleanValue(String valueAsString) throws NumberFormatException {
		this.value = Boolean.parseBoolean(valueAsString);
	}

	@Override
	public BooleanValue add(Boolean o) {
		this.value = o;
		return this;
	}

	@Override
	public boolean matches(IValue<Boolean> other) {
		return this.value.equals(other.getValue());
	}

	@Override
	public Boolean getValue() {
		return this.value;
	}

	@Override
	public BooleanValue getInverse() {
		return new BooleanValue(!getValue());
	}

	@Override
	public String getValueAsString() {
		return this.value.toString();
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BooleanValue [value=");
		sb.append(this.value);
		sb.append("]");
		return sb.toString();
	}

	@Override
	public BooleanValue getCopy() {
		return new BooleanValue(this.value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BooleanValue other = (BooleanValue) obj;
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}
}
