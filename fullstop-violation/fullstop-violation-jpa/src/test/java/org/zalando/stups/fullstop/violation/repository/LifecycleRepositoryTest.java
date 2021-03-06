package org.zalando.stups.fullstop.violation.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.stups.fullstop.violation.EmbeddedPostgresJpaConfig;
import org.zalando.stups.fullstop.violation.entity.ApplicationEntity;
import org.zalando.stups.fullstop.violation.entity.LifecycleEntity;
import org.zalando.stups.fullstop.violation.entity.VersionEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by gkneitschel.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EmbeddedPostgresJpaConfig.class)
@Transactional
public class LifecycleRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private LifecycleRepository lifecycleRepository;

    @PersistenceContext
    private EntityManager em;

    private ApplicationEntity application1;

    private ApplicationEntity application2;

    private VersionEntity version1;

    private VersionEntity version2;

    private LifecycleEntity lifecycleEntity1;

    private LifecycleEntity lifecycleEntity2;

    private ApplicationEntity savedApplication1;

    private LifecycleEntity savedLifecycleEntity1;

    private LifecycleEntity savedLifecycleEntity2;

    private VersionEntity savedVersion1;

    @Before
    public void setUp() throws Exception {
        // First version
        version1 = new VersionEntity();
        version1.setName("0.0.1");
        savedVersion1 = versionRepository.save(version1);
        // Second version
        version2 = new VersionEntity();
        version2.setName("0.9.3-SNAPSHOT");
        versionRepository.save(version2);

        // Add all versions
        List<VersionEntity> versionEntities = newArrayList(version1, version2);

        // Build Application1
        application1 = new ApplicationEntity();
        application1.setName("Application");
        application1.setVersionEntities(versionEntities);

        savedApplication1 = applicationRepository.save(application1);

        //Build first lifecycle
        lifecycleEntity1 = new LifecycleEntity();
        lifecycleEntity1.setRegion("eu-west-1");
        lifecycleEntity1.setEventDate(new DateTime(2015, 6, 23, 8, 14));
        lifecycleEntity1.setVersionEntity(version1);
        lifecycleEntity1.setApplicationEntity(application1);
        savedLifecycleEntity1 = lifecycleRepository.save(lifecycleEntity1);

        // Build second lifecycle
        lifecycleEntity2 = new LifecycleEntity();
        lifecycleEntity2.setRegion("eu-east-1");
        lifecycleEntity2.setVersionEntity(version1);
        lifecycleEntity2.setApplicationEntity(application1);
        savedLifecycleEntity2 = lifecycleRepository.save(lifecycleEntity2);

        em.flush();
        em.clear();
    }

    @Test
    public void testLifecycleHasVersion() throws Exception {
        LifecycleEntity lce = lifecycleRepository.findOne(savedLifecycleEntity1.getId());
        assertThat(lce.getVersionEntity().getName()).isEqualTo(savedVersion1.getName());
    }

    @Test
    public void testLifecycleHasApplication() throws Exception {
        LifecycleEntity lce = lifecycleRepository.findOne(savedLifecycleEntity1.getId());
        assertThat(lce.getApplicationEntity().getId()).isEqualTo(savedApplication1.getId());
    }

    @Test
    public void testInstanceBootTime() throws Exception {
        DateTime now = DateTime.now();

        LifecycleEntity lifecycleEntity12 = new LifecycleEntity();
        lifecycleEntity12.setInstanceBootTime(now);
        lifecycleEntity12.setInstanceId("i-12345");
        lifecycleEntity12.setApplicationEntity(application1);
        lifecycleEntity12.setVersionEntity(version1);

        LifecycleEntity saveLifecycleEntity = lifecycleRepository.save(lifecycleEntity12);

        assertThat(saveLifecycleEntity.getInstanceBootTime()).isEqualTo(now);
        assertThat(lifecycleRepository.findAll()).hasSize(3);

    }

    @Test
    public void TestFindByAppId() throws Exception{
        ApplicationEntity app1 = new ApplicationEntity("App1");
        ApplicationEntity app2 = new ApplicationEntity("App2");

        VersionEntity vers1 = new VersionEntity("1.0");
        versionRepository.save(vers1);
        VersionEntity vers2 = new VersionEntity("2.0");
        versionRepository.save(vers2);

        List<VersionEntity> versionEntities = newArrayList(vers1,vers2);
        app1.setVersionEntities(versionEntities);
        applicationRepository.save(app1);
        app2.setVersionEntities(versionEntities);
        applicationRepository.save(app2);

        LifecycleEntity lifecycleEntity1 = new LifecycleEntity();
        lifecycleEntity1.setApplicationEntity(app1);
        lifecycleEntity1.setVersionEntity(vers1);
        lifecycleRepository.save(lifecycleEntity1);

        LifecycleEntity lifecycleEntity2 = new LifecycleEntity();
        lifecycleEntity2.setApplicationEntity(app1);
        lifecycleEntity2.setVersionEntity(vers2);
        lifecycleRepository.save(lifecycleEntity2);

        LifecycleEntity lifecycleEntity3 = new LifecycleEntity();
        lifecycleEntity3.setApplicationEntity(app2);
        lifecycleEntity3.setVersionEntity(vers1);
        lifecycleRepository.save(lifecycleEntity3);

        LifecycleEntity lifecycleEntity4 = new LifecycleEntity();
        lifecycleEntity4.setApplicationEntity(app2);
        lifecycleEntity4.setVersionEntity(vers2);
        lifecycleRepository.save(lifecycleEntity4);

        LifecycleEntity lifecycleEntity5 = new LifecycleEntity();
        lifecycleEntity5.setApplicationEntity(app1);
        lifecycleEntity5.setVersionEntity(vers2);
        lifecycleRepository.save(lifecycleEntity5);

        List<LifecycleEntity> applications = lifecycleRepository.findByApplicationName("App1");
        assertThat(applications).hasSize(3);
        assertThat(applications.get(1).getVersionEntity().getName()).isEqualTo(applications.get(2).getVersionEntity().getName());
    }
}
