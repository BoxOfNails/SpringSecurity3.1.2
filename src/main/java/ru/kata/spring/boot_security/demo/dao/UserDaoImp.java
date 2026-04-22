package ru.kata.spring.boot_security.demo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

@Repository
public class UserDaoImp implements UserDao {
    private EntityManager entityManager;
    public UserDaoImp(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<User> findAllUsers() {
        //create a query
        TypedQuery<User> theQuery = entityManager.createQuery(
                "select user from User user " +
                        "join fetch user.roles", User.class);

        // execute query and get result list
        List<User> users = theQuery.getResultList();

        // return the results
        return users;
    }

    @Override
    public User findById(int theId) {
        User theUser = entityManager.find(User.class, theId);
        return theUser;
    }

    @Override
    public User save(User theUser) {
        User dbUser = entityManager.merge(theUser);
        return dbUser;
    }

    @Override
    public void deleteById(int theId) {
        User theUser = entityManager.find(User.class, theId);
        entityManager.remove(theUser);
    }

    @Override
    public User findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "select user from User user " +
                        "join fetch user.roles " +
                        "where user.username=:uName and user.enabled=true", User.class);
        query.setParameter("uName", username);
        User user = null;
        try {
            user = query.getSingleResult();
        } catch (Exception e) {
            user = null;
        }
        return user;
    }
}
