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

import li.strolch.model.timevalue.IValue;
import li.strolch.model.timevalue.IValueChange;

/**
 * @author Martin Smock <smock.martin@gmail.com>
 */
@SuppressWarnings("rawtypes")
public class ValueChange<T extends IValue> implements IValueChange<T>, Serializable {

	private static final long serialVersionUID = 1L;

	protected Long time;
	protected T value;
	protected String stateId;

	/**
	 * @param time
	 *            the time the change applies
	 * @param value
	 *            the value to be applied
	 */
	public ValueChange(final Long time, final T value) {
		this.time = time;
		this.value = value;
	}

	/**
	 * @param time
	 *            the time the change applies
	 * @param value
	 *            the value to be applied
	 * @param stateId
	 *            the id of the state the change applies to
	 */
	public ValueChange(final Long time, final T value, final String stateId) {
		this.time = time;
		this.value = value;
		this.stateId = stateId;
	}

	@Override
	public Long getTime() {
		return this.time;
	}
	
	public void setTime(Long time) {
		this.time = time;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getValue() {
		return (T) this.value.getCopy();
	}
	
	public void setValue(T value) {
		this.value = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IValueChange<T> getInverse() {
		return new ValueChange(this.time, this.value.getInverse());
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
		ValueChange<?> other = (ValueChange<?>) obj;
		if (this.time == null) {
			if (other.time != null) {
				return false;
			}
		} else if (!this.time.equals(other.time)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.time == null) ? 0 : this.time.hashCode());
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ValueChange [time=");
		sb.append(this.time);
		sb.append(", value=");
		sb.append(this.value);
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String getStateId() {
		return stateId;
	}

	@Override
	public void setStateId(String id) {
		this.stateId = id;
	}

}
