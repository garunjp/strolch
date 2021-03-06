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
package li.strolch.persistence.xml.model;

import li.strolch.model.Resource;
import li.strolch.model.Tags;
import ch.eitchnet.xmlpers.api.PersistenceContext;
import ch.eitchnet.xmlpers.api.PersistenceContextFactory;
import ch.eitchnet.xmlpers.objref.IdOfSubTypeRef;
import ch.eitchnet.xmlpers.objref.ObjectRef;
import ch.eitchnet.xmlpers.objref.ObjectReferenceCache;

public class ResourceContextFactory implements PersistenceContextFactory<Resource> {

	@Override
	public PersistenceContext<Resource> createCtx(ObjectRef objectRef) {
		PersistenceContext<Resource> ctx = new PersistenceContext<>(objectRef);
		ctx.setParserFactory(new ResourceParserFactory());
		return ctx;
	}

	@Override
	public PersistenceContext<Resource> createCtx(ObjectReferenceCache objectRefCache, Resource t) {
		IdOfSubTypeRef objectRef = objectRefCache.getIdOfSubTypeRef(Tags.RESOURCE, t.getType(), t.getId());
		PersistenceContext<Resource> ctx = createCtx(objectRef);
		ctx.setObject(t);
		return ctx;
	}
}