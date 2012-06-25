/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse protected License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.typesystem.references;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeParameter;

import com.google.common.collect.Maps;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 * TODO JavaDoc, toString
 * TODO implement as member function on LightweightTypeReference
 */
@NonNullByDefault
public abstract class TypeParameterSubstitutor<Visiting> extends TypeReferenceVisitorWithParameterAndNonNullResult<Visiting, LightweightTypeReference> {
		
	private final Map<JvmTypeParameter, LightweightMergedBoundTypeArgument> typeParameterMapping;
	private final TypeReferenceOwner owner;

	public TypeParameterSubstitutor(Map<JvmTypeParameter, LightweightMergedBoundTypeArgument> typeParameterMapping, TypeReferenceOwner owner) {
		this.owner = owner;
		this.typeParameterMapping = Maps.newLinkedHashMap(typeParameterMapping);
	}
	
	protected Map<JvmTypeParameter, LightweightMergedBoundTypeArgument> getTypeParameterMapping() {
		return typeParameterMapping;
	}
	
	protected void enhanceMapping(Map<JvmTypeParameter, LightweightMergedBoundTypeArgument> typeParameterMapping) {
		this.typeParameterMapping.putAll(typeParameterMapping);
	}
	
	protected TypeReferenceOwner getOwner() {
		return owner;
	}
	
	@Override
	protected LightweightTypeReference doVisitFunctionTypeReference(FunctionTypeReference reference, Visiting visiting) {
		if (reference.isResolved() && reference.isOwnedBy(getOwner()))
			return reference;
		FunctionTypeReference result = new FunctionTypeReference(getOwner(), reference.getType());
		for(LightweightTypeReference parameterType: reference.getParameterTypes()) {
			result.addParameterType(parameterType.accept(this, visiting));
		}
		for(LightweightTypeReference typeArgument: reference.getTypeArguments()) {
			result.addTypeArgument(typeArgument.accept(this, visiting));
		}
		LightweightTypeReference returnType = reference.getReturnType();
		if (returnType != null) {
			result.setReturnType(returnType.accept(this, visiting));
		}
		return result;
	}
	
	@Override
	protected LightweightTypeReference doVisitParameterizedTypeReference(ParameterizedTypeReference reference, Visiting visiting) {
		if (reference.isResolved() && reference.isOwnedBy(getOwner()))
			return reference;
		JvmType type = reference.getType();
		if (type instanceof JvmTypeParameter) {
			LightweightTypeReference boundTypeArgument = getBoundTypeArgument(reference, (JvmTypeParameter) type, visiting);
			if (boundTypeArgument != null)
				return boundTypeArgument;
		}
		ParameterizedTypeReference result = new ParameterizedTypeReference(getOwner(), reference.getType());
		for(LightweightTypeReference argument: reference.getTypeArguments()) {
			result.addTypeArgument(argument.accept(this, visiting));
		}
		return result;
	}

	@Nullable
	protected LightweightTypeReference getBoundTypeArgument(ParameterizedTypeReference reference, JvmTypeParameter type,
			Visiting visiting) {
		LightweightMergedBoundTypeArgument boundTypeArgument = typeParameterMapping.get(type);
		if (boundTypeArgument != null && boundTypeArgument.getTypeReference() != reference) {
			return boundTypeArgument.getTypeReference().accept(this, visiting);
		}
		return null;
	}
		
	@Override
	protected LightweightTypeReference doVisitWildcardTypeReference(WildcardTypeReference reference, Visiting visiting) {
		if (reference.isResolved() && reference.isOwnedBy(getOwner()))
			return reference;
		WildcardTypeReference result = new WildcardTypeReference(getOwner());
		LightweightTypeReference lowerBound = reference.getLowerBound();
		if (lowerBound != null) {
			result.setLowerBound(lowerBound.accept(this, visiting));
		}
		for(LightweightTypeReference upperBound: reference.getUpperBounds()) {
			result.addUpperBound(upperBound.accept(this, visiting));
		}
		return result;
	}
	
	@Override
	protected LightweightTypeReference doVisitArrayTypeReference(ArrayTypeReference reference, Visiting visiting) {
		if (reference.isResolved() && reference.isOwnedBy(getOwner()))
			return reference;
		LightweightTypeReference component = reference.getComponentType().accept(this, visiting);
		return new ArrayTypeReference(getOwner(), component);
	}
	
	@Override
	protected LightweightTypeReference doVisitAnyTypeReference(AnyTypeReference reference, Visiting visiting) {
		return reference;
	}
	
	@Override
	protected LightweightTypeReference doVisitCompoundTypeReference(CompoundTypeReference reference, Visiting visiting) {
		if (reference.isResolved() && reference.isOwnedBy(getOwner()))
			return reference;
		CompoundTypeReference result = new CompoundTypeReference(getOwner(), reference.isSynonym());
		for(LightweightTypeReference component: reference.getComponents()) {
			reference.addComponent(component.accept(this, visiting));
		}
		return result;
	}

	public LightweightTypeReference substitute(LightweightTypeReference original) {
		if (typeParameterMapping.isEmpty())
			return original;
		return original.accept(this, createVisiting());
	}
	
	protected abstract Visiting createVisiting();
}