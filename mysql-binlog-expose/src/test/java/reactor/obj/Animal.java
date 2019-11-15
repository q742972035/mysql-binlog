package reactor.obj;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-08 16:45
 **/
public class Animal implements NameAcquire,AgeAcquire,IsAnimal {
    private String name;
    private int age;
    private boolean isAnimal;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean isAnimal() {
        return isAnimal;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", isAnimal=" + isAnimal +
                '}';
    }
}
