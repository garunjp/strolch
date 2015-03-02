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
package li.strolch.agent.api;

import java.util.concurrent.locks.Lock;

import li.strolch.agent.impl.DefaultLockHandler;
import li.strolch.model.Locator;
import li.strolch.model.StrolchRootElement;

/**
 * <p>
 * In Strolch locking of objects is done by keeping a lock for every {@link StrolchRootElement} by using the
 * {@link Locator} to find the lock. Instead of adding a lock to the model, the lock is stored by the
 * {@link LockHandler}.
 * </p>
 * 
 * <p>
 * Since new {@link StrolchRootElement} might not be known by the {@link ElementMap ElementMaps} but you still want to
 * lock an object globally, then locking on the {@link Locator} solves this, as the locator is an immutable object and
 * can easily be created before the actual object exists
 * </p>
 * 
 * <p>
 * See concrete implementations for which concrete locking implementation is used
 * </p>
 * 
 * @see DefaultLockHandler
 * 
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public interface LockHandler {

	/**
	 * Locks the given element by using the element's {@link Locator} and creating a lock on it. Calling lock multiple
	 * times from the same thread will not lock, it is up to the concrete implementation to define if a lock counter is
	 * used
	 * 
	 * @param element
	 *            the element for which a {@link Lock} on its {@link Locator} is to be created and/or locked
	 * 
	 * @throws StrolchLockException
	 */
	public void lock(StrolchRootElement element) throws StrolchLockException;

	/**
	 * <p>
	 * Unlocks the given element by finding the element's lock by its {@link Locator}. It is up to the concrete
	 * implementation to define if unlocking an unlocked element will fail or not. This method might not completely
	 * unlock the element if a lock counter is used and the object was locked multiple times.
	 * </p>
	 * 
	 * <p>
	 * If the lock must be completely released, then use {@link #releaseLock(StrolchRootElement)}
	 * </p>
	 * 
	 * @param element
	 *            the element for which the current/last {@link Lock} is to be unlocked
	 * 
	 * @throws StrolchLockException
	 */
	public void unlock(StrolchRootElement element) throws StrolchLockException;

	/**
	 * Releases the lock on the given element, by unlocking all locks, i.e. after this method is called, no lock will be
	 * held anymore by the current thread
	 * 
	 * @param element
	 *            the element for which the {@link Lock} on the {@link Locator} is to be released
	 * 
	 * @throws StrolchLockException
	 */
	public void releaseLock(StrolchRootElement element) throws StrolchLockException;
}
