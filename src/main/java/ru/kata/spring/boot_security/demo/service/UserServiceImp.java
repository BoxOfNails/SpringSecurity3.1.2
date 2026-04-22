package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.dto.WebUserDto;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImp implements UserService {
    private UserDao userDao;
    private RoleService roleService;
    private Logger logger = Logger.getLogger(getClass().getName());

    private BCryptPasswordEncoder passwordEncoder;

    public UserServiceImp(UserDao userDao, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleService = roleService;
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
        if (user.getFormRole().equals("admin")) {
            user.setRoles(Stream.of(roleService.findRoleByName("ROLE_USER"), roleService.findRoleByName("ROLE_ADMIN")).collect(Collectors.toSet()));
        } else {
            user.setRoles(Stream.of(roleService.findRoleByName("ROLE_USER")).collect(Collectors.toSet()));
        }
        user.setPassword(passwordEncoder.encode(user.getFormPassword()));

        userDao.save(user);
    }

    @Transactional
    @Override
    public void register(WebUserDto webUserDto) {
        User user = new User();

        user.setUsername(webUserDto.getUsername());
        user.setFormPassword(webUserDto.getPassword());
        user.setFirstName(webUserDto.getFirstName());
        user.setLastName(webUserDto.getLastName());
        user.setEmail(webUserDto.getEmail());

        user.setRoles(Stream.of(roleService.findRoleByName("ROLE_USER")).collect(Collectors.toSet()));

        save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUserName(username);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getRoles());
    }
}
