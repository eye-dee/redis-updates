package com.redis.model;

import java.util.Objects;

public class MyMsg {

    private String field;
    private String update;

    public MyMsg() {
    }

    public MyMsg(String field, String update) {
        this.field = field;
        this.update = update;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyMsg myMsg = (MyMsg) o;
        return Objects.equals(field, myMsg.field) &&
                Objects.equals(update, myMsg.update);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, update);
    }
}
