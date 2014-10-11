package services;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Order;
import models.enums.OrderStatus;

import org.springframework.stereotype.Service;

import repos.AdminRepository;
import repos.OrderRepository;
import utils.MD5Utils;

@Named
@Singleton
@Service(value = "adminService")
public class AdminService {
    @Inject
    private AdminRepository adminRepository;
    @Inject
    private OrderRepository orderRepository;

    public Admin findByAdminNameAndAdminPassword(String adminName, String password) {
        Iterator<Admin> iter = adminRepository.findByAdminNameAndAdminPassword(adminName,
                MD5Utils.md5(password)).iterator();
        while (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    public Admin findByAdminName(String adminName) {
        if (adminName == null) {
            return null;
        }
        Iterator<Admin> iter = adminRepository.findByAdminName(adminName).iterator();
        while (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    public Admin save(Admin admin) {
        admin.adminPassword = MD5Utils.md5(admin.adminPassword);
        return adminRepository.save(admin);
    }

    public Iterable<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findOne(Long id) {
        return adminRepository.findOne(id);
    }

    public void delete(Long id) {
        adminRepository.delete(id);
    }

    public Iterator<Order> findOrdersUnHandled() {
        return orderRepository.findByStatusIn(OrderStatus.unhandledStatus()).iterator();
    }
}
