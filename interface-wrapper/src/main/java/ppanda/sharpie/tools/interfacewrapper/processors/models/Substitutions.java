package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

//TODO: This should be replaced by ResolvedType::replaceTypeVariables or ResolvedTypeParametersMap::replaceAll
class Substitutions {
    private Map<ResolvedTypeParameterDeclaration, ResolvedReferenceType> typeParamToInferredTypes;

    public Substitutions(
        Map<ResolvedTypeParameterDeclaration, ResolvedReferenceType> typeParamToInferredTypes) {
        this.typeParamToInferredTypes = typeParamToInferredTypes;
    }

    public ResolvedReferenceType applyOn(ResolvedType target) {
        ResolvedType result = target;

        for (Map.Entry<ResolvedTypeParameterDeclaration, ResolvedReferenceType> entry : typeParamToInferredTypes.entrySet()) {
            ResolvedTypeParameterDeclaration typeParamInConverterClass = entry.getKey();
            ResolvedReferenceType substitutedValueAsPerRequestedDeclaredTp = entry.getValue();
            result = result.replaceTypeVariables(typeParamInConverterClass, substitutedValueAsPerRequestedDeclaredTp);
        }

        return result.asReferenceType();
    }

    public static Substitutions infer(ResolvedType template, ResolvedType requested) {
        Map<ResolvedTypeParameterDeclaration, ResolvedReferenceType> typeVarToInferredTypes = inferTypeMap(template, requested);
        return new Substitutions(typeVarToInferredTypes);
    }

    private static Map<ResolvedTypeParameterDeclaration, ResolvedReferenceType> inferTypeMap(ResolvedType template,
        ResolvedType requested) {
        Map<ResolvedTypeParameterDeclaration, ResolvedType> typeMapInTemplate = getTypeParamMap(template);
        Map<ResolvedTypeParameterDeclaration, ResolvedType> typeMapInRequested = getTypeParamMap(requested);
        Map<ResolvedTypeParameterDeclaration, ResolvedReferenceType> typeVarToInferredTypes = new HashMap<>();

        typeMapInTemplate.forEach((key, typeParamValueInTemplate) -> {
            ResolvedType typeParamInRequested = typeMapInRequested.get(key);

            if (typeParamValueInTemplate.isTypeVariable()) {
                typeVarToInferredTypes.put(typeParamValueInTemplate.asTypeParameter(),
                    typeParamInRequested.asReferenceType());
                return;
            }

            Map<ResolvedTypeParameterDeclaration, ResolvedReferenceType> inferredTypesInNextLevel =
                inferTypeMap(typeParamValueInTemplate, typeParamInRequested);
            typeVarToInferredTypes.putAll(inferredTypesInNextLevel);
        });

        return typeVarToInferredTypes;
    }

    private static Map<ResolvedTypeParameterDeclaration, ResolvedType> getTypeParamMap(ResolvedType template) {
        return template.asReferenceType().getTypeParametersMap()
            .stream().collect(toMap(pair -> pair.a, pair -> pair.b));
    }
}
