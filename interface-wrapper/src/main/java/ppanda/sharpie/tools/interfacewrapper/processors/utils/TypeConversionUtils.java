package ppanda.sharpie.tools.interfacewrapper.processors.utils;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.ResolvedTypeVariable;
import com.github.javaparser.resolution.types.ResolvedWildcard;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.javaparser.resolution.types.ResolvedWildcard.UNBOUNDED;

public class TypeConversionUtils {
    public static boolean isConvertible(Type subType, Type superType) {
        return isConvertible(subType.asClassOrInterfaceType(), superType.asClassOrInterfaceType());
    }

    public static boolean isConvertible(ClassOrInterfaceType subType, ClassOrInterfaceType superType) {
        ResolvedReferenceType resolvedSubType = subType.resolve();
        ResolvedReferenceType resolvedSuperType = superType.resolve();

        return isConvertible(resolvedSubType, resolvedSuperType);
    }

    private static boolean isConvertible(ResolvedReferenceType resolvedSubType,
        ResolvedReferenceType resolvedSuperType) {
        boolean eq = isEqualOrSubtypeOf(resolvedSubType, resolvedSuperType);
        boolean hTA = haveConvertibleTypeArgs(resolvedSubType, resolvedSuperType);
        return eq && hTA;
    }

    private static boolean isEqualOrSubtypeOf(ResolvedReferenceType resolvedSubType,
        ResolvedReferenceType resolvedSuperType) {
        boolean isSameType = resolvedSubType.getQualifiedName().equals(resolvedSuperType.getQualifiedName());
        boolean superTypeIsOneAncestorOfSubType = resolvedSubType.getAllAncestors().contains(resolvedSuperType);

        return isSameType || superTypeIsOneAncestorOfSubType;
    }

    private static boolean haveConvertibleTypeArgs(ResolvedReferenceType subType,
        ResolvedReferenceType superType) {
        List<ResolvedType> subtypeTypeParams = subType.typeParametersValues();
        List<ResolvedType> supertypeTypeParams = superType.typeParametersValues();

        return Streams.zip(subtypeTypeParams.stream(), supertypeTypeParams.stream(), (subtypeTypeParam, supertypeTypeParam) -> {
            if (subtypeTypeParam.isReferenceType() && supertypeTypeParam.isTypeVariable()) {
                return canSubstitute(subtypeTypeParam.asReferenceType(), supertypeTypeParam.asTypeVariable());
            }
            if (subtypeTypeParam.isReferenceType() && supertypeTypeParam.isWildcard()) {
                return canSubstitute(subtypeTypeParam.asReferenceType(), supertypeTypeParam.asWildcard());
            }
            if (subtypeTypeParam.isReferenceType() && supertypeTypeParam.isReferenceType()) {
                return isConvertible(subtypeTypeParam.asReferenceType(), supertypeTypeParam.asReferenceType());
            }
            return false;
        })
            .allMatch(x -> x);

    }

    private static boolean canSubstitute(ResolvedReferenceType referenceType, ResolvedTypeVariable typeVariable) {
        List<ResolvedReferenceType> extendsBounds = typeVariable.asTypeParameter()
            .getBounds()
            .stream()
            .filter(ResolvedTypeParameterDeclaration.Bound::isExtends)
            .map(ResolvedTypeParameterDeclaration.Bound::getType)
            .map(ResolvedType::asReferenceType)
            .collect(Collectors.toList());

        if (extendsBounds.isEmpty()) {
            return true;
        }

        List<ResolvedReferenceType> ancestors = referenceType.getAllAncestors();
        return extendsBounds.stream().anyMatch(ancestors::contains);
    }

    private static boolean canSubstitute(ResolvedReferenceType referenceType, ResolvedWildcard wildcardTypeBound) {
        if (UNBOUNDED.equals(wildcardTypeBound)) {
            return true;
        }
        ResolvedReferenceType boundType = wildcardTypeBound.getBoundedType().asReferenceType();
        return referenceType.getAllAncestors().contains(boundType);
    }
}
