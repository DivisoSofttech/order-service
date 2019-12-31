package com.diviso.graeshoppe.order.web.rest;

import com.diviso.graeshoppe.order.OrderApp;
import com.diviso.graeshoppe.order.config.TestSecurityConfiguration;
import com.diviso.graeshoppe.order.domain.DeliveryInfo;
import com.diviso.graeshoppe.order.repository.DeliveryInfoRepository;
import com.diviso.graeshoppe.order.repository.search.DeliveryInfoSearchRepository;
import com.diviso.graeshoppe.order.service.DeliveryInfoService;
import com.diviso.graeshoppe.order.service.dto.DeliveryInfoDTO;
import com.diviso.graeshoppe.order.service.mapper.DeliveryInfoMapper;
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
 * Integration tests for the {@link DeliveryInfoResource} REST controller.
 */
@SpringBootTest(classes = {OrderApp.class, TestSecurityConfiguration.class})
public class DeliveryInfoResourceIT {

    private static final String DEFAULT_DELIVERY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_TYPE = "BBBBBBBBBB";

    private static final Double DEFAULT_DELIVERY_CHARGE = 1D;
    private static final Double UPDATED_DELIVERY_CHARGE = 2D;

    private static final String DEFAULT_DELIVERY_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_NOTES = "BBBBBBBBBB";

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private DeliveryInfoMapper deliveryInfoMapper;

    @Autowired
    private DeliveryInfoService deliveryInfoService;

    /**
     * This repository is mocked in the com.diviso.graeshoppe.order.repository.search test package.
     *
     * @see com.diviso.graeshoppe.order.repository.search.DeliveryInfoSearchRepositoryMockConfiguration
     */
    @Autowired
    private DeliveryInfoSearchRepository mockDeliveryInfoSearchRepository;

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

    private MockMvc restDeliveryInfoMockMvc;

    private DeliveryInfo deliveryInfo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DeliveryInfoCommandResource deliveryInfoResource = new DeliveryInfoCommandResource(deliveryInfoService);
        this.restDeliveryInfoMockMvc = MockMvcBuilders.standaloneSetup(deliveryInfoResource)
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
    public static DeliveryInfo createEntity(EntityManager em) {
        DeliveryInfo deliveryInfo = new DeliveryInfo()
            .deliveryType(DEFAULT_DELIVERY_TYPE)
            .deliveryCharge(DEFAULT_DELIVERY_CHARGE)
            .deliveryNotes(DEFAULT_DELIVERY_NOTES);
        return deliveryInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeliveryInfo createUpdatedEntity(EntityManager em) {
        DeliveryInfo deliveryInfo = new DeliveryInfo()
            .deliveryType(UPDATED_DELIVERY_TYPE)
            .deliveryCharge(UPDATED_DELIVERY_CHARGE)
            .deliveryNotes(UPDATED_DELIVERY_NOTES);
        return deliveryInfo;
    }

    @BeforeEach
    public void initTest() {
        deliveryInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createDeliveryInfo() throws Exception {
        int databaseSizeBeforeCreate = deliveryInfoRepository.findAll().size();

        // Create the DeliveryInfo
        DeliveryInfoDTO deliveryInfoDTO = deliveryInfoMapper.toDto(deliveryInfo);
        restDeliveryInfoMockMvc.perform(post("/api/delivery-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deliveryInfoDTO)))
            .andExpect(status().isCreated());

        // Validate the DeliveryInfo in the database
        List<DeliveryInfo> deliveryInfoList = deliveryInfoRepository.findAll();
        assertThat(deliveryInfoList).hasSize(databaseSizeBeforeCreate + 1);
        DeliveryInfo testDeliveryInfo = deliveryInfoList.get(deliveryInfoList.size() - 1);
        assertThat(testDeliveryInfo.getDeliveryType()).isEqualTo(DEFAULT_DELIVERY_TYPE);
        assertThat(testDeliveryInfo.getDeliveryCharge()).isEqualTo(DEFAULT_DELIVERY_CHARGE);
        assertThat(testDeliveryInfo.getDeliveryNotes()).isEqualTo(DEFAULT_DELIVERY_NOTES);

        // Validate the DeliveryInfo in Elasticsearch
        verify(mockDeliveryInfoSearchRepository, times(1)).save(testDeliveryInfo);
    }

    @Test
    @Transactional
    public void createDeliveryInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = deliveryInfoRepository.findAll().size();

        // Create the DeliveryInfo with an existing ID
        deliveryInfo.setId(1L);
        DeliveryInfoDTO deliveryInfoDTO = deliveryInfoMapper.toDto(deliveryInfo);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryInfoMockMvc.perform(post("/api/delivery-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deliveryInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DeliveryInfo in the database
        List<DeliveryInfo> deliveryInfoList = deliveryInfoRepository.findAll();
        assertThat(deliveryInfoList).hasSize(databaseSizeBeforeCreate);

        // Validate the DeliveryInfo in Elasticsearch
        verify(mockDeliveryInfoSearchRepository, times(0)).save(deliveryInfo);
    }


    @Test
    @Transactional
    public void getAllDeliveryInfos() throws Exception {
        // Initialize the database
        deliveryInfoRepository.saveAndFlush(deliveryInfo);

        // Get all the deliveryInfoList
        restDeliveryInfoMockMvc.perform(get("/api/delivery-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deliveryInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].deliveryType").value(hasItem(DEFAULT_DELIVERY_TYPE)))
            .andExpect(jsonPath("$.[*].deliveryCharge").value(hasItem(DEFAULT_DELIVERY_CHARGE.doubleValue())))
            .andExpect(jsonPath("$.[*].deliveryNotes").value(hasItem(DEFAULT_DELIVERY_NOTES)));
    }
    
    @Test
    @Transactional
    public void getDeliveryInfo() throws Exception {
        // Initialize the database
        deliveryInfoRepository.saveAndFlush(deliveryInfo);

        // Get the deliveryInfo
        restDeliveryInfoMockMvc.perform(get("/api/delivery-infos/{id}", deliveryInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(deliveryInfo.getId().intValue()))
            .andExpect(jsonPath("$.deliveryType").value(DEFAULT_DELIVERY_TYPE))
            .andExpect(jsonPath("$.deliveryCharge").value(DEFAULT_DELIVERY_CHARGE.doubleValue()))
            .andExpect(jsonPath("$.deliveryNotes").value(DEFAULT_DELIVERY_NOTES));
    }

    @Test
    @Transactional
    public void getNonExistingDeliveryInfo() throws Exception {
        // Get the deliveryInfo
        restDeliveryInfoMockMvc.perform(get("/api/delivery-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDeliveryInfo() throws Exception {
        // Initialize the database
        deliveryInfoRepository.saveAndFlush(deliveryInfo);

        int databaseSizeBeforeUpdate = deliveryInfoRepository.findAll().size();

        // Update the deliveryInfo
        DeliveryInfo updatedDeliveryInfo = deliveryInfoRepository.findById(deliveryInfo.getId()).get();
        // Disconnect from session so that the updates on updatedDeliveryInfo are not directly saved in db
        em.detach(updatedDeliveryInfo);
        updatedDeliveryInfo
            .deliveryType(UPDATED_DELIVERY_TYPE)
            .deliveryCharge(UPDATED_DELIVERY_CHARGE)
            .deliveryNotes(UPDATED_DELIVERY_NOTES);
        DeliveryInfoDTO deliveryInfoDTO = deliveryInfoMapper.toDto(updatedDeliveryInfo);

        restDeliveryInfoMockMvc.perform(put("/api/delivery-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deliveryInfoDTO)))
            .andExpect(status().isOk());

        // Validate the DeliveryInfo in the database
        List<DeliveryInfo> deliveryInfoList = deliveryInfoRepository.findAll();
        assertThat(deliveryInfoList).hasSize(databaseSizeBeforeUpdate);
        DeliveryInfo testDeliveryInfo = deliveryInfoList.get(deliveryInfoList.size() - 1);
        assertThat(testDeliveryInfo.getDeliveryType()).isEqualTo(UPDATED_DELIVERY_TYPE);
        assertThat(testDeliveryInfo.getDeliveryCharge()).isEqualTo(UPDATED_DELIVERY_CHARGE);
        assertThat(testDeliveryInfo.getDeliveryNotes()).isEqualTo(UPDATED_DELIVERY_NOTES);

        // Validate the DeliveryInfo in Elasticsearch
        verify(mockDeliveryInfoSearchRepository, times(1)).save(testDeliveryInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingDeliveryInfo() throws Exception {
        int databaseSizeBeforeUpdate = deliveryInfoRepository.findAll().size();

        // Create the DeliveryInfo
        DeliveryInfoDTO deliveryInfoDTO = deliveryInfoMapper.toDto(deliveryInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryInfoMockMvc.perform(put("/api/delivery-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deliveryInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DeliveryInfo in the database
        List<DeliveryInfo> deliveryInfoList = deliveryInfoRepository.findAll();
        assertThat(deliveryInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DeliveryInfo in Elasticsearch
        verify(mockDeliveryInfoSearchRepository, times(0)).save(deliveryInfo);
    }

    @Test
    @Transactional
    public void deleteDeliveryInfo() throws Exception {
        // Initialize the database
        deliveryInfoRepository.saveAndFlush(deliveryInfo);

        int databaseSizeBeforeDelete = deliveryInfoRepository.findAll().size();

        // Delete the deliveryInfo
        restDeliveryInfoMockMvc.perform(delete("/api/delivery-infos/{id}", deliveryInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DeliveryInfo> deliveryInfoList = deliveryInfoRepository.findAll();
        assertThat(deliveryInfoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the DeliveryInfo in Elasticsearch
        verify(mockDeliveryInfoSearchRepository, times(1)).deleteById(deliveryInfo.getId());
    }

    @Test
    @Transactional
    public void searchDeliveryInfo() throws Exception {
        // Initialize the database
        deliveryInfoRepository.saveAndFlush(deliveryInfo);
        when(mockDeliveryInfoSearchRepository.search(queryStringQuery("id:" + deliveryInfo.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(deliveryInfo), PageRequest.of(0, 1), 1));
        // Search the deliveryInfo
        restDeliveryInfoMockMvc.perform(get("/api/_search/delivery-infos?query=id:" + deliveryInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deliveryInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].deliveryType").value(hasItem(DEFAULT_DELIVERY_TYPE)))
            .andExpect(jsonPath("$.[*].deliveryCharge").value(hasItem(DEFAULT_DELIVERY_CHARGE.doubleValue())))
            .andExpect(jsonPath("$.[*].deliveryNotes").value(hasItem(DEFAULT_DELIVERY_NOTES)));
    }
}
