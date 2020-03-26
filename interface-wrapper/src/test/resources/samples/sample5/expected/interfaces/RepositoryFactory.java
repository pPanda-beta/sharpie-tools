package interfaces;

import converters.OptionalFromIntegerConverter;
import converters.OptionalFromStringConverter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

public class RepositoryFactory {

    private static class RepositoryImpl implements Repository {

        public Person findPerson() {
            return interfaces.PersonFactory.wrapUnderlying(underlyingRepositoryUnderlying.findPerson());
        }

        final transient RepositoryUnderlying underlyingRepositoryUnderlying;

        public RepositoryImpl(RepositoryUnderlying underlyingRepositoryUnderlying) {
            this.underlyingRepositoryUnderlying = underlyingRepositoryUnderlying;
        }
    }

    public static Repository wrapUnderlying(RepositoryUnderlying underlyingRepositoryUnderlying) {
        return new RepositoryImpl(underlyingRepositoryUnderlying);
    }
}
