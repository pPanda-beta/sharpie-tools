package interfaces;

import converters.OptionalFromStringConverter;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

public interface PersonUnderlying {

    java.lang.String getNameIfAvailable();
}
