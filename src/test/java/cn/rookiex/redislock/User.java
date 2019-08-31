package cn.rookiex.redislock;

/**
 * @Author : Rookiex
 * @Date : 2019/08/27
 * @Describe :
 */
public class User {
    private int id;
    private String name;
    private int age = 10;

    public User() {
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
