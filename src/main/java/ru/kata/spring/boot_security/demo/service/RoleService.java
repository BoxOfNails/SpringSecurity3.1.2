package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;

public interface RoleService {
    public Role findRoleByName(String theRoleName);
}
