package interfaces;

import annotations.OptionalStringWrapper;
import java.util.Optional;


@javax.annotation.Resource(name = "UnderlyingInterfaceAsResource")
public interface PersonUnderlying {

    java.lang.String getNameIfAvailable();
}
