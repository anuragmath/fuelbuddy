package com.fuel.buddy.web.rest;

import com.fuel.buddy.FuelbuddyApp;

import com.fuel.buddy.domain.Assets;
import com.fuel.buddy.domain.User;
import com.fuel.buddy.repository.AssetsRepository;
import com.fuel.buddy.repository.search.AssetsSearchRepository;

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
 * Test class for the AssetsResource REST controller.
 *
 * @see AssetsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FuelbuddyApp.class)
public class AssetsResourceIntTest {

    private static final String DEFAULT_ASSET_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ASSET_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_MANUFACTURER = "AAAAAAAAAA";
    private static final String UPDATED_MANUFACTURER = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_FUEL_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FUEL_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_ASSET_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_ASSET_IDENTIFIER = "BBBBBBBBBB";

    @Inject
    private AssetsRepository assetsRepository;

    @Inject
    private AssetsSearchRepository assetsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restAssetsMockMvc;

    private Assets assets;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AssetsResource assetsResource = new AssetsResource();
        ReflectionTestUtils.setField(assetsResource, "assetsSearchRepository", assetsSearchRepository);
        ReflectionTestUtils.setField(assetsResource, "assetsRepository", assetsRepository);
        this.restAssetsMockMvc = MockMvcBuilders.standaloneSetup(assetsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assets createEntity(EntityManager em) {
        Assets assets = new Assets()
                .assetType(DEFAULT_ASSET_TYPE)
                .manufacturer(DEFAULT_MANUFACTURER)
                .model(DEFAULT_MODEL)
                .fuelType(DEFAULT_FUEL_TYPE)
                .assetIdentifier(DEFAULT_ASSET_IDENTIFIER);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        assets.setUser(user);
        return assets;
    }

    @Before
    public void initTest() {
        assetsSearchRepository.deleteAll();
        assets = createEntity(em);
    }

    @Test
    @Transactional
    public void createAssets() throws Exception {
        int databaseSizeBeforeCreate = assetsRepository.findAll().size();

        // Create the Assets

        restAssetsMockMvc.perform(post("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assets)))
            .andExpect(status().isCreated());

        // Validate the Assets in the database
        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeCreate + 1);
        Assets testAssets = assetsList.get(assetsList.size() - 1);
        assertThat(testAssets.getAssetType()).isEqualTo(DEFAULT_ASSET_TYPE);
        assertThat(testAssets.getManufacturer()).isEqualTo(DEFAULT_MANUFACTURER);
        assertThat(testAssets.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testAssets.getFuelType()).isEqualTo(DEFAULT_FUEL_TYPE);
        assertThat(testAssets.getAssetIdentifier()).isEqualTo(DEFAULT_ASSET_IDENTIFIER);

        // Validate the Assets in ElasticSearch
        Assets assetsEs = assetsSearchRepository.findOne(testAssets.getId());
        assertThat(assetsEs).isEqualToComparingFieldByField(testAssets);
    }

    @Test
    @Transactional
    public void createAssetsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = assetsRepository.findAll().size();

        // Create the Assets with an existing ID
        Assets existingAssets = new Assets();
        existingAssets.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssetsMockMvc.perform(post("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingAssets)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkAssetTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetsRepository.findAll().size();
        // set the field null
        assets.setAssetType(null);

        // Create the Assets, which fails.

        restAssetsMockMvc.perform(post("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assets)))
            .andExpect(status().isBadRequest());

        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkManufacturerIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetsRepository.findAll().size();
        // set the field null
        assets.setManufacturer(null);

        // Create the Assets, which fails.

        restAssetsMockMvc.perform(post("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assets)))
            .andExpect(status().isBadRequest());

        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkModelIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetsRepository.findAll().size();
        // set the field null
        assets.setModel(null);

        // Create the Assets, which fails.

        restAssetsMockMvc.perform(post("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assets)))
            .andExpect(status().isBadRequest());

        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFuelTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetsRepository.findAll().size();
        // set the field null
        assets.setFuelType(null);

        // Create the Assets, which fails.

        restAssetsMockMvc.perform(post("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assets)))
            .andExpect(status().isBadRequest());

        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAssetIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = assetsRepository.findAll().size();
        // set the field null
        assets.setAssetIdentifier(null);

        // Create the Assets, which fails.

        restAssetsMockMvc.perform(post("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assets)))
            .andExpect(status().isBadRequest());

        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAssets() throws Exception {
        // Initialize the database
        assetsRepository.saveAndFlush(assets);

        // Get all the assetsList
        restAssetsMockMvc.perform(get("/api/assets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assets.getId().intValue())))
            .andExpect(jsonPath("$.[*].assetType").value(hasItem(DEFAULT_ASSET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].manufacturer").value(hasItem(DEFAULT_MANUFACTURER.toString())))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL.toString())))
            .andExpect(jsonPath("$.[*].fuelType").value(hasItem(DEFAULT_FUEL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].assetIdentifier").value(hasItem(DEFAULT_ASSET_IDENTIFIER.toString())));
    }

    @Test
    @Transactional
    public void getAssets() throws Exception {
        // Initialize the database
        assetsRepository.saveAndFlush(assets);

        // Get the assets
        restAssetsMockMvc.perform(get("/api/assets/{id}", assets.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(assets.getId().intValue()))
            .andExpect(jsonPath("$.assetType").value(DEFAULT_ASSET_TYPE.toString()))
            .andExpect(jsonPath("$.manufacturer").value(DEFAULT_MANUFACTURER.toString()))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL.toString()))
            .andExpect(jsonPath("$.fuelType").value(DEFAULT_FUEL_TYPE.toString()))
            .andExpect(jsonPath("$.assetIdentifier").value(DEFAULT_ASSET_IDENTIFIER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAssets() throws Exception {
        // Get the assets
        restAssetsMockMvc.perform(get("/api/assets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAssets() throws Exception {
        // Initialize the database
        assetsRepository.saveAndFlush(assets);
        assetsSearchRepository.save(assets);
        int databaseSizeBeforeUpdate = assetsRepository.findAll().size();

        // Update the assets
        Assets updatedAssets = assetsRepository.findOne(assets.getId());
        updatedAssets
                .assetType(UPDATED_ASSET_TYPE)
                .manufacturer(UPDATED_MANUFACTURER)
                .model(UPDATED_MODEL)
                .fuelType(UPDATED_FUEL_TYPE)
                .assetIdentifier(UPDATED_ASSET_IDENTIFIER);

        restAssetsMockMvc.perform(put("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAssets)))
            .andExpect(status().isOk());

        // Validate the Assets in the database
        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeUpdate);
        Assets testAssets = assetsList.get(assetsList.size() - 1);
        assertThat(testAssets.getAssetType()).isEqualTo(UPDATED_ASSET_TYPE);
        assertThat(testAssets.getManufacturer()).isEqualTo(UPDATED_MANUFACTURER);
        assertThat(testAssets.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testAssets.getFuelType()).isEqualTo(UPDATED_FUEL_TYPE);
        assertThat(testAssets.getAssetIdentifier()).isEqualTo(UPDATED_ASSET_IDENTIFIER);

        // Validate the Assets in ElasticSearch
        Assets assetsEs = assetsSearchRepository.findOne(testAssets.getId());
        assertThat(assetsEs).isEqualToComparingFieldByField(testAssets);
    }

    @Test
    @Transactional
    public void updateNonExistingAssets() throws Exception {
        int databaseSizeBeforeUpdate = assetsRepository.findAll().size();

        // Create the Assets

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAssetsMockMvc.perform(put("/api/assets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(assets)))
            .andExpect(status().isCreated());

        // Validate the Assets in the database
        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAssets() throws Exception {
        // Initialize the database
        assetsRepository.saveAndFlush(assets);
        assetsSearchRepository.save(assets);
        int databaseSizeBeforeDelete = assetsRepository.findAll().size();

        // Get the assets
        restAssetsMockMvc.perform(delete("/api/assets/{id}", assets.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean assetsExistsInEs = assetsSearchRepository.exists(assets.getId());
        assertThat(assetsExistsInEs).isFalse();

        // Validate the database is empty
        List<Assets> assetsList = assetsRepository.findAll();
        assertThat(assetsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAssets() throws Exception {
        // Initialize the database
        assetsRepository.saveAndFlush(assets);
        assetsSearchRepository.save(assets);

        // Search the assets
        restAssetsMockMvc.perform(get("/api/_search/assets?query=id:" + assets.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assets.getId().intValue())))
            .andExpect(jsonPath("$.[*].assetType").value(hasItem(DEFAULT_ASSET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].manufacturer").value(hasItem(DEFAULT_MANUFACTURER.toString())))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL.toString())))
            .andExpect(jsonPath("$.[*].fuelType").value(hasItem(DEFAULT_FUEL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].assetIdentifier").value(hasItem(DEFAULT_ASSET_IDENTIFIER.toString())));
    }
}
