import sample.interfaces.Person;
import sample.interfaces.PersonFactory;
import sample.interfaces.PersonUnderlying;

public class Main {
    public static void main(String[] args) {
        PersonUnderlying personUnderlying = () -> "pPanda";
        Person person = PersonFactory.wrapUnderlying(personUnderlying);
        System.out.println(person.hasName());
        System.out.println(person.getNameIfAvailable());
    }
}
