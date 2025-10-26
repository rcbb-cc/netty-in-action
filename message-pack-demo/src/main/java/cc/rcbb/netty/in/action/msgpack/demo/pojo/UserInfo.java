package cc.rcbb.netty.in.action.msgpack.demo.pojo;

import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * UserInfo
 *
 * @author rcbb.cc
 * @date 2025/10/26
 * @since 1.0.0
 */
@Message
public class UserInfo implements Serializable {

    private Integer age;

    private String name;

    public UserInfo() {
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
