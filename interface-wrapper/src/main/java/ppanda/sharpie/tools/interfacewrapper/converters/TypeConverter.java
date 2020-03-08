package ppanda.sharpie.tools.interfacewrapper.converters;

public interface TypeConverter<DeclaredT, OriginalT> {

    DeclaredT convertFrom(OriginalT original);
}


