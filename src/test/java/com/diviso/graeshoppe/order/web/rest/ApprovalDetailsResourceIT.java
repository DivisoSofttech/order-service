package com.diviso.graeshoppe.order.web.rest;

import com.diviso.graeshoppe.order.OrderApp;
import com.diviso.graeshoppe.order.config.TestSecurityConfiguration;
import com.diviso.graeshoppe.order.domain.ApprovalDetails;
import com.diviso.graeshoppe.order.repository.ApprovalDetailsRepository;
import com.diviso.graeshoppe.order.repository.search.ApprovalDetailsSearchRepository;
import com.diviso.graeshoppe.order.service.ApprovalDetailsService;
import com.diviso.graeshoppe.order.service.dto.ApprovalDetailsDTO;
import com.diviso.graeshoppe.order.service.mapper.ApprovalDetailsMapper;
import com.diviso.graeshoppe.order.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static com.diviso.graeshoppe.order.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ApprovalDetailsResource} REST controller.
 */
@SpringBootTest(classes = {OrderApp.class, TestSecurityConfiguration.class})
public class ApprovalDetailsResourceIT {

    private static final Instant DEFAULT_ACCEPTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACCEPTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPECTED_DELIVERY = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPECTED_DELIVERY = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DECISION = "AAAAAAAAAA";
    private static final String UPDATED_DECISION = "BBBBBBBBBB";

    @Autowired
    private ApprovalDetailsRepository approvalDetailsRepository;

    @Autowired
    private ApprovalDetailsMapper approvalDetailsMapper;

    @Autowired
    private ApprovalDetailsService approvalDetailsService;

    /**
     * This repository is mocked in the com.diviso.graeshoppe.order.repository.search test package.
     *
     * @see com.diviso.graeshoppe.order.repository.search.ApprovalDetailsSearchRepositoryMockConfiguration
     */
    @Autowired
    private ApprovalDetailsSearchRepository mockApprovalDetailsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restApprovalDetailsMockMvc;

    private ApprovalDetails approvalDetails;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ApprovalDetailsResource approvalDetailsResource = new ApprovalDetailsResource(approvalDetailsService);
        this.restApprovalDetailsMockMvc = MockMvcBuilders.standaloneSetup(approvalDetailsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApprovalDetails createEntity(EntityManager em) {
        ApprovalDetails approvalDetails = new ApprovalDetails()
            .acceptedAt(DEFAULT_ACCEPTED_AT)
            .expectedDelivery(DEFAULT_EXPECTED_DELIVERY)
            .decision(DEFAULT_DECISION);
        return approvalDetails;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApprovalDetails createUpdatedEntity(EntityManager em) {
        ApprovalDetails approvalDetails = new ApprovalDetails()
            .acceptedAt(UPDATED_ACCEPTED_AT)
            .expectedDelivery(UPDATED_EXPECTED_DELIVERY)
            .decision(UPDATED_DECISION);
        return approvalDetails;
    }

    @BeforeEach
    public void initTest() {
        approvalDetails = createEntity(em);
    }

    @Test
    @Transactional
    public void createApprovalDetails() throws Exception {
        int databaseSizeBeforeCreate = approvalDetailsRepository.findAll().size();

        // Create the ApprovalDetails
        ApprovalDetailsDTO approvalDetailsDTO = approvalDetailsMapper.toDto(approvalDetails);
        restApprovalDetailsMockMvc.perform(post("/api/approval-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(approvalDetailsDTO)))
            .andExpect(status().isCreated());

        // Validate the ApprovalDetails in the database
        List<ApprovalDetails> approvalDetailsList = approvalDetailsRepository.findAll();
        assertThat(approvalDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        ApprovalDetails testApprovalDetails = approvalDetailsList.get(approvalDetailsList.size() - 1);
        assertThat(testApprovalDetails.getAcceptedAt()).isEqualTo(DEFAULT_ACCEPTED_AT);
        assertThat(testApprovalDetails.getExpectedDelivery()).isEqualTo(DEFAULT_EXPECTED_DELIVERY);
        assertThat(testApprovalDetails.getDecision()).isEqualTo(DEFAULT_DECISION);

        // Validate the ApprovalDetails in Elasticsearch
        verify(mockApprovalDetailsSearchRepository, times(1)).save(testApprovalDetails);
    }

    @Test
    @Transactional
    public void createApprovalDetailsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = approvalDetailsRepository.findAll().size();

        // Create the ApprovalDetails with an existing ID
        approvalDetails.setId(1L);
        ApprovalDetailsDTO approvalDetailsDTO = approvalDetailsMapper.toDto(approvalDetails);

        // An entity with an existing ID cannot be created, so this API call must fail
        restApprovalDetailsMockMvc.perform(post("/api/approval-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(approvalDetailsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ApprovalDetails in the database
        List<ApprovalDetails> approvalDetailsList = approvalDetailsRepository.findAll();
        assertThat(approvalDetailsList).hasSize(databaseSizeBeforeCreate);

        // Validate the ApprovalDetails in Elasticsearch
        verify(mockApprovalDetailsSearchRepository, times(0)).save(approvalDetails);
    }


    @Test
    @Transactional
    public void getAllApprovalDetails() throws Exception {
        // Initialize the database
        approvalDetailsRepository.saveAndFlush(approvalDetails);

        // Get all the approvalDetailsList
        restApprovalDetailsMockMvc.perform(get("/api/approval-details?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(approvalDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].acceptedAt").value(hasItem(DEFAULT_ACCEPTED_AT.toString())))
            .andExpect(jsonPath("$.[*].expectedDelivery").value(hasItem(DEFAULT_EXPECTED_DELIVERY.toString())))
            .andExpect(jsonPath("$.[*].decision").value(hasItem(DEFAULT_DECISION)));
    }
    
    @Test
    @Transactional
    public void getApprovalDetails() throws Exception {
        // Initialize the database
        approvalDetailsRepository.saveAndFlush(approvalDetails);

        // Get the approvalDetails
        restApprovalDetailsMockMvc.perform(get("/api/approval-details/{id}", approvalDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(approvalDetails.getId().intValue()))
            .andExpect(jsonPath("$.acceptedAt").value(DEFAULT_ACCEPTED_AT.toString()))
            .andExpect(jsonPath("$.expectedDelivery").value(DEFAULT_EXPECTED_DELIVERY.toString()))
            .andExpect(jsonPath("$.decision").value(DEFAULT_DECISION));
    }

    @Test
    @Transactional
    public void getNonExistingApprovalDetails() throws Exception {
        // Get the approvalDetails
        restApprovalDetailsMockMvc.perform(get("/api/approval-details/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateApprovalDetails() throws Exception {
        // Initialize the database
        approvalDetailsRepository.saveAndFlush(approvalDetails);

        int databaseSizeBeforeUpdate = approvalDetailsRepository.findAll().size();

        // Update the approvalDetails
        ApprovalDetails updatedApprovalDetails = approvalDetailsRepository.findById(approvalDetails.getId()).get();
        // Disconnect from session so that the updates on updatedApprovalDetails are not directly saved in db
        em.detach(updatedApprovalDetails);
        updatedApprovalDetails
            .acceptedAt(UPDATED_ACCEPTED_AT)
            .expectedDelivery(UPDATED_EXPECTED_DELIVERY)
            .decision(UPDATED_DECISION);
        ApprovalDetailsDTO approvalDetailsDTO = approvalDetailsMapper.toDto(updatedApprovalDetails);

        restApprovalDetailsMockMvc.perform(put("/api/approval-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(approvalDetailsDTO)))
            .andExpect(status().isOk());

        // Validate the ApprovalDetails in the database
        List<ApprovalDetails> approvalDetailsList = approvalDetailsRepository.findAll();
        assertThat(approvalDetailsList).hasSize(databaseSizeBeforeUpdate);
        ApprovalDetails testApprovalDetails = approvalDetailsList.get(approvalDetailsList.size() - 1);
        assertThat(testApprovalDetails.getAcceptedAt()).isEqualTo(UPDATED_ACCEPTED_AT);
        assertThat(testApprovalDetails.getExpectedDelivery()).isEqualTo(UPDATED_EXPECTED_DELIVERY);
        assertThat(testApprovalDetails.getDecision()).isEqualTo(UPDATED_DECISION);

        // Validate the ApprovalDetails in Elasticsearch
        verify(mockApprovalDetailsSearchRepository, times(1)).save(testApprovalDetails);
    }

    @Test
    @Transactional
    public void updateNonExistingApprovalDetails() throws Exception {
        int databaseSizeBeforeUpdate = approvalDetailsRepository.findAll().size();

        // Create the ApprovalDetails
        ApprovalDetailsDTO approvalDetailsDTO = approvalDetailsMapper.toDto(approvalDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApprovalDetailsMockMvc.perform(put("/api/approval-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(approvalDetailsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ApprovalDetails in the database
        List<ApprovalDetails> approvalDetailsList = approvalDetailsRepository.findAll();
        assertThat(approvalDetailsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ApprovalDetails in Elasticsearch
        verify(mockApprovalDetailsSearchRepository, times(0)).save(approvalDetails);
    }

    @Test
    @Transactional
    public void deleteApprovalDetails() throws Exception {
        // Initialize the database
        approvalDetailsRepository.saveAndFlush(approvalDetails);

        int databaseSizeBeforeDelete = approvalDetailsRepository.findAll().size();

        // Delete the approvalDetails
        restApprovalDetailsMockMvc.perform(delete("/api/approval-details/{id}", approvalDetails.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ApprovalDetails> approvalDetailsList = approvalDetailsRepository.findAll();
        assertThat(approvalDetailsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ApprovalDetails in Elasticsearch
        verify(mockApprovalDetailsSearchRepository, times(1)).deleteById(approvalDetails.getId());
    }

    @Test
    @Transactional
    public void searchApprovalDetails() throws Exception {
        // Initialize the database
        approvalDetailsRepository.saveAndFlush(approvalDetails);
        when(mockApprovalDetailsSearchRepository.search(queryStringQuery("id:" + approvalDetails.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(approvalDetails), PageRequest.of(0, 1), 1));
        // Search the approvalDetails
        restApprovalDetailsMockMvc.perform(get("/api/_search/approval-details?query=id:" + approvalDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(approvalDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].acceptedAt").value(hasItem(DEFAULT_ACCEPTED_AT.toString())))
            .andExpect(jsonPath("$.[*].expectedDelivery").value(hasItem(DEFAULT_EXPECTED_DELIVERY.toString())))
            .andExpect(jsonPath("$.[*].decision").value(hasItem(DEFAULT_DECISION)));
    }
}
