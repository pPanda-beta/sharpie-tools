package ppanda.sharpie.tools.interfacewrapper.converters;

import java.util.concurrent.Callable;

public interface LazyTypeConverter<DeclaredT, OriginalT> {

    DeclaredT convertFrom(Callable<OriginalT> original);
}


