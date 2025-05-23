package ru.kata.spring.boot_security.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

@Component
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager entityManager;
    public UserDaoImpl() {
    }

    @Autowired
    RoleRepository roleRepo;

    @Override
    public void saveUser(User user) {
        Role roleUser = findRoleByRoleName("ROLE_USER");
        user.addRole(roleUser);
        entityManager.persist(user);
    }

    @Override
    public List <User> getAllUsers() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void updateUser(User user) {
        entityManager.merge(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = entityManager.find(User.class, id);
        entityManager.remove(user);
    }

    @Override
    @Transactional
    public User findByUsername(String username) throws UsernameNotFoundException  {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class);
        query.setParameter("name", username);
        if (username == null) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = query.getSingleResult();
        user.getRoles().size();
        return user;
    }

    @Override
    public Set<Role> getRolesByIds(List<Integer> ids) {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r WHERE r.id IN :ids", Role.class);
        query.setParameter("ids", ids);
        return new java.util.HashSet<>(query.getResultList());
    }

    @Override
    public Role findRoleByRoleName(String name) {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r WHERE r.roleName = :roleName", Role.class);
        query.setParameter("roleName", name);
        return query.getSingleResult();
    }


    public List<Role> getRoles() {
        return roleRepo.findAll();
    }
}
