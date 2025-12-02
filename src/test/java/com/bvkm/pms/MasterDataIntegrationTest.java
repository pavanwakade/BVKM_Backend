package com.bvkm.pms;

import com.bvkm.pms.entity.*;
import com.bvkm.pms.payload.request.LoginRequest;
import com.bvkm.pms.payload.response.JwtResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MasterDataIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtToken;
    private static String firmId;
    private static String branchId;
    private static String staffId;
    private static String clientId;
    private static String taskId;

    @Test
    @Order(1)
    public void testLoginAndGetToken() throws Exception {
        // Assuming admin_test was created in ApiIntegrationTest or exists
        // If not, we might need to register it again or rely on DB state.
        // For safety, let's try to register again, ignoring 400.
        LoginRequest registerRequest = new LoginRequest();
        registerRequest.setUsername("admin_master_test");
        registerRequest.setPassword("admin123");

        mockMvc.perform(post("/api/auth/register-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin_master_test");
        loginRequest.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(responseContent, JwtResponse.class);
        jwtToken = jwtResponse.getToken();
    }

    @Test
    @Order(2)
    public void testCreateFirm() throws Exception {
        Firm firm = new Firm();
        firm.setName("Test Firm");
        firm.setBillingFormat("Standard");

        MvcResult result = mockMvc.perform(post("/api/firms")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Firm"))
                .andReturn();

        Firm createdFirm = objectMapper.readValue(result.getResponse().getContentAsString(), Firm.class);
        firmId = createdFirm.getId().toString();
    }

    @Test
    @Order(3)
    public void testCreateBranch() throws Exception {
        Branch branch = new Branch();
        branch.setName("Test Branch");
        branch.setAddress("123 Test St");

        // Set Firm ID reference
        Firm firmRef = new Firm();
        firmRef.setId(java.util.UUID.fromString(firmId));
        branch.setFirm(firmRef);

        MvcResult result = mockMvc.perform(post("/api/branches")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(branch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Branch"))
                .andReturn();

        Branch createdBranch = objectMapper.readValue(result.getResponse().getContentAsString(), Branch.class);
        branchId = createdBranch.getId().toString();
    }

    @Test
    @Order(4)
    public void testCreateStaff() throws Exception {
        Staff staff = new Staff();
        staff.setName("Test Staff");
        staff.setLoginId("staff_test_user_" + System.currentTimeMillis());
        staff.setPasswordHash("password123"); // Controller should hash this
        staff.setRole(Staff.Role.STAFF);

        Branch branchRef = new Branch();
        branchRef.setId(java.util.UUID.fromString(branchId));
        staff.setAssignedLocation(branchRef);

        MvcResult result = mockMvc.perform(post("/api/staff")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Staff"))
                .andReturn();

        Staff createdStaff = objectMapper.readValue(result.getResponse().getContentAsString(), Staff.class);
        staffId = createdStaff.getId().toString();
    }

    @Test
    @Order(5)
    public void testCreateClient() throws Exception {
        Client client = new Client();
        client.setName("Test Client");
        client.setCategory(Client.Category.A);

        Staff staffRef = new Staff();
        staffRef.setId(java.util.UUID.fromString(staffId));
        client.setAssignedStaff(staffRef);

        MvcResult result = mockMvc.perform(post("/api/clients")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Client"))
                .andReturn();

        Client createdClient = objectMapper.readValue(result.getResponse().getContentAsString(), Client.class);
        clientId = createdClient.getId().toString();
    }

    @Test
    @Order(6)
    public void testCreateTask() throws Exception {
        Task task = new Task();
        task.setName("Test Task");
        task.setDepartment("Audit");

        MvcResult result = mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andReturn();

        Task createdTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);
        taskId = createdTask.getId().toString();
    }

    @Test
    @Order(7)
    public void testGetAllEndpoints() throws Exception {
        mockMvc.perform(get("/api/firms").header("Authorization", "Bearer " + jwtToken)).andExpect(status().isOk());
        mockMvc.perform(get("/api/branches").header("Authorization", "Bearer " + jwtToken)).andExpect(status().isOk());
        mockMvc.perform(get("/api/staff").header("Authorization", "Bearer " + jwtToken)).andExpect(status().isOk());
        mockMvc.perform(get("/api/clients").header("Authorization", "Bearer " + jwtToken)).andExpect(status().isOk());
        mockMvc.perform(get("/api/tasks").header("Authorization", "Bearer " + jwtToken)).andExpect(status().isOk());
    }
}
