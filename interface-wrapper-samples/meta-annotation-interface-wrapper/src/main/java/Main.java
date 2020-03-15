import interfaces.Person;
import interfaces.PersonFactory;
import interfaces.PersonUnderlying;

public class Main {
    public static void main(String[] args) {
        PersonUnderlying personUnderlying = new PersonUnderlying() {
            @Override public String getNameIfAvailable() {
                return "pPanda";
            }

            @Override public Float getAge() {
                return 25.7f;
            }
        };
        Person person = PersonFactory.wrapUnderlying(personUnderlying);
        System.out.println(person.hasName());
        System.out.println(person.getNameIfAvailable());
        System.out.println(person.getAge());
    }
}
