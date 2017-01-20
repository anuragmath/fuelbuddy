package com.fuel.buddy.web.rest;

import com.fuel.buddy.FuelbuddyApp;

import com.fuel.buddy.domain.Fuel;
import com.fuel.buddy.repository.FuelRepository;
import com.fuel.buddy.repository.search.FuelSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FuelResource REST controller.
 *
 * @see FuelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FuelbuddyApp.class)
public class FuelResourceIntTest {

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    @Inject
    private FuelRepository fuelRepository;

    @Inject
    private FuelSearchRepository fuelSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restFuelMockMvc;

    private Fuel fuel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FuelResource fuelResource = new FuelResource();
        ReflectionTestUtils.setField(fuelResource, "fuelSearchRepository", fuelSearchRepository);
        ReflectionTestUtils.setField(fuelResource, "fuelRepository", fuelRepository);
        this.restFuelMockMvc = MockMvcBuilders.standaloneSetup(fuelResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fuel createEntity(EntityManager em) {
        Fuel fuel = new Fuel()
                .price(DEFAULT_PRICE)
                .location(DEFAULT_LOCATION)
                .type(DEFAULT_TYPE)
                .status(DEFAULT_STATUS);
        return fuel;
    }

    @Before
    public void initTest() {
        fuelSearchRepository.deleteAll();
        fuel = createEntity(em);
    }

    @Test
    @Transactional
    public void createFuel() throws Exception {
        int databaseSizeBeforeCreate = fuelRepository.findAll().size();

        // Create the Fuel

        restFuelMockMvc.perform(post("/api/fuels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fuel)))
            .andExpect(status().isCreated());

        // Validate the Fuel in the database
        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeCreate + 1);
        Fuel testFuel = fuelList.get(fuelList.size() - 1);
        assertThat(testFuel.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testFuel.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testFuel.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testFuel.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Fuel in ElasticSearch
        Fuel fuelEs = fuelSearchRepository.findOne(testFuel.getId());
        assertThat(fuelEs).isEqualToComparingFieldByField(testFuel);
    }

    @Test
    @Transactional
    public void createFuelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fuelRepository.findAll().size();

        // Create the Fuel with an existing ID
        Fuel existingFuel = new Fuel();
        existingFuel.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFuelMockMvc.perform(post("/api/fuels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingFuel)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = fuelRepository.findAll().size();
        // set the field null
        fuel.setPrice(null);

        // Create the Fuel, which fails.

        restFuelMockMvc.perform(post("/api/fuels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fuel)))
            .andExpect(status().isBadRequest());

        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLocationIsRequired() throws Exception {
        int databaseSizeBeforeTest = fuelRepository.findAll().size();
        // set the field null
        fuel.setLocation(null);

        // Create the Fuel, which fails.

        restFuelMockMvc.perform(post("/api/fuels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fuel)))
            .andExpect(status().isBadRequest());

        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = fuelRepository.findAll().size();
        // set the field null
        fuel.setType(null);

        // Create the Fuel, which fails.

        restFuelMockMvc.perform(post("/api/fuels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fuel)))
            .andExpect(status().isBadRequest());

        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = fuelRepository.findAll().size();
        // set the field null
        fuel.setStatus(null);

        // Create the Fuel, which fails.

        restFuelMockMvc.perform(post("/api/fuels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fuel)))
            .andExpect(status().isBadRequest());

        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFuels() throws Exception {
        // Initialize the database
        fuelRepository.saveAndFlush(fuel);

        // Get all the fuelList
        restFuelMockMvc.perform(get("/api/fuels?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fuel.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    public void getFuel() throws Exception {
        // Initialize the database
        fuelRepository.saveAndFlush(fuel);

        // Get the fuel
        restFuelMockMvc.perform(get("/api/fuels/{id}", fuel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(fuel.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    public void getNonExistingFuel() throws Exception {
        // Get the fuel
        restFuelMockMvc.perform(get("/api/fuels/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFuel() throws Exception {
        // Initialize the database
        fuelRepository.saveAndFlush(fuel);
        fuelSearchRepository.save(fuel);
        int databaseSizeBeforeUpdate = fuelRepository.findAll().size();

        // Update the fuel
        Fuel updatedFuel = fuelRepository.findOne(fuel.getId());
        updatedFuel
                .price(UPDATED_PRICE)
                .location(UPDATED_LOCATION)
                .type(UPDATED_TYPE)
                .status(UPDATED_STATUS);

        restFuelMockMvc.perform(put("/api/fuels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFuel)))
            .andExpect(status().isOk());

        // Validate the Fuel in the database
        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeUpdate);
        Fuel testFuel = fuelList.get(fuelList.size() - 1);
        assertThat(testFuel.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testFuel.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testFuel.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFuel.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Fuel in ElasticSearch
        Fuel fuelEs = fuelSearchRepository.findOne(testFuel.getId());
        assertThat(fuelEs).isEqualToComparingFieldByField(testFuel);
    }

    @Test
    @Transactional
    public void updateNonExistingFuel() throws Exception {
        int databaseSizeBeforeUpdate = fuelRepository.findAll().size();

        // Create the Fuel

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFuelMockMvc.perform(put("/api/fuels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fuel)))
            .andExpect(status().isCreated());

        // Validate the Fuel in the database
        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteFuel() throws Exception {
        // Initialize the database
        fuelRepository.saveAndFlush(fuel);
        fuelSearchRepository.save(fuel);
        int databaseSizeBeforeDelete = fuelRepository.findAll().size();

        // Get the fuel
        restFuelMockMvc.perform(delete("/api/fuels/{id}", fuel.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean fuelExistsInEs = fuelSearchRepository.exists(fuel.getId());
        assertThat(fuelExistsInEs).isFalse();

        // Validate the database is empty
        List<Fuel> fuelList = fuelRepository.findAll();
        assertThat(fuelList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFuel() throws Exception {
        // Initialize the database
        fuelRepository.saveAndFlush(fuel);
        fuelSearchRepository.save(fuel);

        // Search the fuel
        restFuelMockMvc.perform(get("/api/_search/fuels?query=id:" + fuel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fuel.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }
}
