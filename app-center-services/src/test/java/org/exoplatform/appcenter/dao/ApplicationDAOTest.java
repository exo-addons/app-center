package org.exoplatform.appcenter.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import org.exoplatform.appcenter.entity.ApplicationEntity;
import org.exoplatform.appcenter.entity.FavoriteApplicationEntity;
import org.exoplatform.container.*;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.naming.InitialContextInitializer;

public class ApplicationDAOTest {

  private PortalContainer container;

  @BeforeClass
  @SuppressWarnings("deprecation")
  public static void startDB() {
    RootContainer rootContainer = RootContainer.getInstance();
    InitialContextInitializer initializer = rootContainer.getComponentInstanceOfType(InitialContextInitializer.class);
    initializer.recall(); // NOSONAR
  }

  @Before
  public void setup() {
    container = PortalContainer.getInstance();
    assertNotNull(container);
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
  }

  @After
  public void teardown() {
    ApplicationDAO service = ExoContainerContext.getService(ApplicationDAO.class);
    service.deleteAll();

    RequestLifeCycle.end();
    container.stop();
    container = null;
    ExoContainerContext.setCurrentContainer(null);
  }

  @Test
  public void testServiceInitialized() {
    ApplicationDAO service = ExoContainerContext.getService(ApplicationDAO.class);
    assertNotNull(service);

    ApplicationEntity applicationEntity = new ApplicationEntity(null,
                                                                "title",
                                                                "url",
                                                                5L,
                                                                "description",
                                                                true,
                                                                false,
                                                                "permissions");
    ApplicationEntity storedEntity = service.create(applicationEntity);
    assertNotNull(storedEntity);
    assertNotNull(storedEntity.getId());
    assertEquals(applicationEntity.getTitle(), storedEntity.getTitle());
    assertEquals(applicationEntity.getUrl(), storedEntity.getUrl());
    assertEquals(applicationEntity.getImageFileId(), storedEntity.getImageFileId());
    assertEquals(applicationEntity.getDescription(), storedEntity.getDescription());
    assertEquals(applicationEntity.isActive(), storedEntity.isActive());
    assertEquals(applicationEntity.isByDefault(), storedEntity.isByDefault());
    assertEquals(applicationEntity.getPermissions(), storedEntity.getPermissions());

    // Test setters
    applicationEntity = new ApplicationEntity();
    applicationEntity.setId(null);
    applicationEntity.setTitle("title");
    applicationEntity.setUrl("url");
    applicationEntity.setImageFileId(5L);
    applicationEntity.setDescription("description");
    applicationEntity.setActive(false);
    applicationEntity.setByDefault(true);
    applicationEntity.setPermissions("permissions");

    storedEntity = service.create(applicationEntity);
    assertNotNull(storedEntity);
    assertNotNull(storedEntity.getId());
    assertEquals(applicationEntity.getTitle(), storedEntity.getTitle());
    assertEquals(applicationEntity.getUrl(), storedEntity.getUrl());
    assertEquals(applicationEntity.getImageFileId(), storedEntity.getImageFileId());
    assertEquals(applicationEntity.getDescription(), storedEntity.getDescription());
    assertEquals(applicationEntity.isActive(), storedEntity.isActive());
    assertEquals(applicationEntity.isByDefault(), storedEntity.isByDefault());
    assertEquals(applicationEntity.getPermissions(), storedEntity.getPermissions());
  }

  @Test
  public void testFindApplications() {
    ApplicationDAO service = ExoContainerContext.getService(ApplicationDAO.class);
    assertNotNull(service);

    ApplicationEntity applicationEntity = new ApplicationEntity(null,
                                                                "title",
                                                                "url",
                                                                5L,
                                                                "description",
                                                                true,
                                                                false,
                                                                "permissions");
    applicationEntity = service.create(applicationEntity);
    ApplicationEntity applicationEntity2 = new ApplicationEntity(null,
                                                                 "title2",
                                                                 "url2",
                                                                 5L,
                                                                 "description2",
                                                                 true,
                                                                 false,
                                                                 "permissions");
    applicationEntity2 = service.create(applicationEntity2);

    List<ApplicationEntity> applications = service.getApplications("title", 0, 10);
    assertNotNull(applications);
    assertEquals(2, applications.size());
    assertEquals(applicationEntity.getId(), applications.get(0).getId());

    applications = service.getApplications("title*", 0, 10);
    assertNotNull(applications);
    assertEquals(2, applications.size());
    assertEquals(applicationEntity.getId(), applications.get(0).getId());
    assertEquals(applicationEntity2.getId(), applications.get(1).getId());

    applications = service.getApplications("title*", 1, 2);
    assertNotNull(applications);
    assertEquals(1, applications.size());
    assertEquals(applicationEntity2.getId(), applications.get(0).getId());

    applications = service.getApplications("title2", 0, 10);
    assertNotNull(applications);
    assertEquals(1, applications.size());
    assertEquals(applicationEntity2.getId(), applications.get(0).getId());
  }

  @Test
  public void testGetApplicationByTitleOrUrl() {
    ApplicationDAO applicationDAO = ExoContainerContext.getService(ApplicationDAO.class);
    assertNotNull(applicationDAO);
    FavoriteApplicationDAO favoriteApplicationDAO = ExoContainerContext.getService(FavoriteApplicationDAO.class);
    assertNotNull(favoriteApplicationDAO);

    ApplicationEntity applicationEntity = new ApplicationEntity(null,
                                                                "title",
                                                                "url",
                                                                5L,
                                                                "description",
                                                                true,
                                                                false,
                                                                "permissions");
    applicationDAO.create(applicationEntity);
    ApplicationEntity applicationEntity2 = new ApplicationEntity(null,
                                                                 "title2",
                                                                 "url2",
                                                                 5L,
                                                                 "description2",
                                                                 true,
                                                                 false,
                                                                 "permissions");
    applicationDAO.create(applicationEntity2);

    ApplicationEntity foundEntity = applicationDAO.getApplicationByTitleOrUrl("title", "url");
    assertNotNull(foundEntity);
    assertEquals(applicationEntity.getId(), foundEntity.getId());

    foundEntity = applicationDAO.getApplicationByTitleOrUrl("title2", "url");
    assertNotNull(foundEntity);
    assertEquals(applicationEntity.getId(), foundEntity.getId());

    foundEntity = applicationDAO.getApplicationByTitleOrUrl("title", "url2");
    assertNotNull(foundEntity);
    assertEquals(applicationEntity.getId(), foundEntity.getId());

    foundEntity = applicationDAO.getApplicationByTitleOrUrl("title2", "url2");
    assertNotNull(foundEntity);
    assertEquals(applicationEntity2.getId(), foundEntity.getId());

    foundEntity = applicationDAO.getApplicationByTitleOrUrl("title3", "url3");
    assertNull(foundEntity);
  }

  @Test
  public void testGetFavoriteApps() {
    ApplicationDAO applicationDAO = ExoContainerContext.getService(ApplicationDAO.class);
    assertNotNull(applicationDAO);
    FavoriteApplicationDAO favoriteApplicationDAO = ExoContainerContext.getService(FavoriteApplicationDAO.class);
    assertNotNull(favoriteApplicationDAO);

    ApplicationEntity applicationEntity = new ApplicationEntity(null,
                                                                "title",
                                                                "url",
                                                                5L,
                                                                "description",
                                                                true,
                                                                false,
                                                                "permissions");
    applicationEntity = applicationDAO.create(applicationEntity);

    ApplicationEntity applicationEntity2 = new ApplicationEntity(null,
                                                                 "title2",
                                                                 "url2",
                                                                 5L,
                                                                 "description2",
                                                                 true,
                                                                 false,
                                                                 "permissions");
    applicationEntity2 = applicationDAO.create(applicationEntity2);

    ApplicationEntity applicationEntity3 = new ApplicationEntity(null,
                                                                 "title2",
                                                                 "url2",
                                                                 5L,
                                                                 "description2",
                                                                 true,
                                                                 true,
                                                                 "permissions");
    applicationEntity3 = applicationDAO.create(applicationEntity3);

    favoriteApplicationDAO.create(new FavoriteApplicationEntity(applicationEntity, "testuser"));
    favoriteApplicationDAO.create(new FavoriteApplicationEntity(applicationEntity, "testuser2"));
    favoriteApplicationDAO.create(new FavoriteApplicationEntity(applicationEntity, "testuser3"));
    favoriteApplicationDAO.create(new FavoriteApplicationEntity(applicationEntity, "testuser4"));

    favoriteApplicationDAO.create(new FavoriteApplicationEntity(applicationEntity2, "testuser"));
    favoriteApplicationDAO.create(new FavoriteApplicationEntity(applicationEntity2, "testuser3"));

    List<ApplicationEntity> favorites = applicationDAO.getFavoriteActiveApps("testuser");
    assertNotNull(favorites);
    assertEquals(3, favorites.size());

    favorites = applicationDAO.getFavoriteActiveApps("testuser2");
    assertNotNull(favorites);
    assertEquals(2, favorites.size());

    favorites = applicationDAO.getFavoriteActiveApps("fake");
    assertNotNull(favorites);
    assertEquals(1, favorites.size());
    assertEquals(applicationEntity3.getId(), favorites.get(0).getId());
  }

}
