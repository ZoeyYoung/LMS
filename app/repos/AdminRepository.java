package repos;

import models.Admin;

import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Long> {

    Iterable<Admin> findByAdminNameAndAdminPassword(String adminName, String password);

    Iterable<Admin> findByAdminName(String adminName);

}
