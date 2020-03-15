package converters;

import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;

public class DoubleFromFloatConverter implements TypeConverter<Double, Float> {

    @Override public Double convertFrom(Float original) {
        return Double.valueOf(original);
    }
}
