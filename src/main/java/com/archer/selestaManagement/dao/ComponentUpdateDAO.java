package com.archer.selestaManagement.dao;

import com.archer.selestaManagement.entity.Component;

public class ComponentUpdateDAO {
    public enum Action {
        UPDATE,
        DELETE
    }

    private Action action;
    private Component data;

    public ComponentUpdateDAO() {}

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Component getData() {
        return data;
    }

    public void setData(Component data) {
        this.data = data;
    }

}
