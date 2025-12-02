package com.bvkm.pms.config;

import com.bvkm.pms.entity.Branch;
import com.bvkm.pms.entity.Firm;
import com.bvkm.pms.entity.Staff;
import com.bvkm.pms.repository.BranchRepository;
import com.bvkm.pms.repository.FirmRepository;
import com.bvkm.pms.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private FirmRepository firmRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.bvkm.pms.repository.ClientRepository clientRepository;

    @Autowired
    private com.bvkm.pms.repository.TaskRepository taskRepository;

    @Override
    public void run(String... args) throws Exception {
        seedData();
    }

    private void seedData() {
        // 1. Create Firm if not exists
        if (firmRepository.count() == 0) {
            Firm firm = new Firm();
            firm.setName("BVKM & Co.");
            firm.setBillingFormat("Standard");
            firmRepository.save(firm);
            System.out.println("Seeded Firm: BVKM & Co.");
        }

        Firm firm = firmRepository.findAll().get(0);

        // 2. Create Branch if not exists
        if (branchRepository.count() == 0) {
            Branch branch = new Branch();
            branch.setName("Head Office");
            branch.setFirm(firm);
            branch.setAddress("123 Main St, City");
            branchRepository.save(branch);
            System.out.println("Seeded Branch: Head Office");
        }

        Branch branch = branchRepository.findAll().get(0);

        // 3. Create Users
        createStaffIfNotFound("admin_user", "admin123", "Admin User", Staff.Role.ADMIN, branch);
        createStaffIfNotFound("manager_user", "manager123", "Manager User", Staff.Role.MANAGER, branch);
        createStaffIfNotFound("staff_user_1", "staff123", "Staff User 1", Staff.Role.STAFF, branch);
        createStaffIfNotFound("staff_user_2", "staff123", "Staff User 2", Staff.Role.STAFF, branch);

        // 4. Create Clients
        if (clientRepository.count() == 0) {
            createClient("Acme Corp", com.bvkm.pms.entity.Client.Category.A);
            createClient("Globex Inc", com.bvkm.pms.entity.Client.Category.B);
            createClient("Soylent Corp", com.bvkm.pms.entity.Client.Category.C);
        }

        // 5. Create Tasks
        if (taskRepository.count() == 0) {
            createTask("Audit", "Accounting");
            createTask("Tax Filing", "Tax");
            createTask("Consulting", "Advisory");
        }
    }

    private void createClient(String name, com.bvkm.pms.entity.Client.Category category) {
        com.bvkm.pms.entity.Client client = new com.bvkm.pms.entity.Client();
        client.setName(name);
        client.setCategory(category);
        clientRepository.save(client);
        System.out.println("Seeded Client: " + name);
    }

    private void createTask(String name, String department) {
        com.bvkm.pms.entity.Task task = new com.bvkm.pms.entity.Task();
        task.setName(name);
        task.setDepartment(department);
        taskRepository.save(task);
        System.out.println("Seeded Task: " + name);
    }

    private void createStaffIfNotFound(String loginId, String password, String name, Staff.Role role, Branch branch) {
        if (staffRepository.findByLoginId(loginId).isEmpty()) {
            Staff staff = new Staff();
            staff.setLoginId(loginId);
            staff.setPasswordHash(passwordEncoder.encode(password));
            staff.setName(name);
            staff.setRole(role);
            staff.setAssignedLocation(branch);
            staffRepository.save(staff);
            System.out.println("Seeded User: " + loginId);
        }
    }
}
