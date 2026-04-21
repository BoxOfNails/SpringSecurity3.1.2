package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.dto.WebUser;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImp implements UserService {
    private UserDao userDao;
    private RoleDao roleDao;

    private BCryptPasswordEncoder passwordEncoder;

    public UserServiceImp(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Transactional(readOnly = true)
    @Override
    public User findById(int theId) {
        return userDao.findById(theId);
    }

    @Transactional
    @Override
    public void deleteById(int theId) {
        userDao.deleteById(theId);
    }

    @Transactional(readOnly = true)
    @Override
    public User findByUserName(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    @Override
    public void save(User user) {
        user.setEnabled(true);
        switch(user.getFormRole()) {
            case "user":
                user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_USER")));
                break;
            case "admin":
                user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_USER"), roleDao.findRoleByName("ROLE_ADMIN")));
                break;
        }
        user.setPassword(passwordEncoder.encode(user.getFormPassword()));
        userDao.save(user);
    }

    @Transactional
    @Override
    public void register(WebUser webUser) {
        User user = new User();

        user.setUsername(webUser.getUsername());
        user.setPassword(passwordEncoder.encode(webUser.getPassword()));
        user.setFirstName(webUser.getFirstName());
        user.setLastName(webUser.getLastName());
        user.setEmail(webUser.getEmail());
        user.setEnabled(true);

        user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_USER")));

        userDao.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getRoles());
    }
}
