/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.typesystem.references;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.xtext.common.types.JvmTypeParameter;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
@NonNullByDefault
public class StandardTypeParameterSubstitutor extends TypeParameterSubstitutor<Object> {

	public StandardTypeParameterSubstitutor(
			Map<JvmTypeParameter, LightweightMergedBoundTypeArgument> typeParameterMapping, TypeReferenceOwner owner) {
		super(typeParameterMapping, owner);
	}

	@Override
	protected Object createVisiting() {
		return new Object();
	}

}
