package app.bpartners.geojobs.service.event;

import static app.bpartners.geojobs.endpoint.rest.controller.ZoneDetectionJobControllerIT.someDetectionTask;
import static app.bpartners.geojobs.job.model.Status.HealthStatus.UNKNOWN;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.PENDING;
import static app.bpartners.geojobs.repository.model.GeoJobType.DETECTION;
import static app.bpartners.geojobs.service.DetectionTaskServiceIT.detectedTile;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.bpartners.geojobs.conf.FacadeIT;
import app.bpartners.geojobs.job.model.TaskStatus;
import app.bpartners.geojobs.repository.DetectableObjectConfigurationRepository;
import app.bpartners.geojobs.repository.DetectedTileRepository;
import app.bpartners.geojobs.repository.DetectionTaskRepository;
import app.bpartners.geojobs.repository.ZoneDetectionJobRepository;
import app.bpartners.geojobs.repository.model.Parcel;
import app.bpartners.geojobs.repository.model.detection.*;
import app.bpartners.geojobs.service.detection.DetectionMapper;
import app.bpartners.geojobs.service.detection.DetectionResponse;
import app.bpartners.geojobs.service.detection.TileObjectDetector;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DetectionTaskConsumerIT extends FacadeIT {
  public static final String JOB_ID = "JOB_ID";
  public static final String DETECTION_TASK_ID = "detection_task1";
  @MockBean TileObjectDetector objectDetector;
  @MockBean DetectionMapper detectionMapper;
  @Autowired DetectionTaskConsumer subject;
  @Autowired DetectableObjectConfigurationRepository objectConfigurationRepository;
  @Autowired DetectionTaskRepository detectionTaskRepository;
  @Autowired ZoneDetectionJobRepository jobRepository;
  @Autowired DetectedTileRepository detectedTileRepository;

  @BeforeEach
  void setUp() {
    when(objectDetector.apply(any(), any())).thenReturn(DetectionResponse.builder().build());
    when(detectionMapper.toDetectedTile(any(), any(), any(), any())).thenReturn(someDetectedTile());
    jobRepository.save(ZoneDetectionJob.builder().id(JOB_ID).build());
    detectionTaskRepository.save(
        DetectionTask.builder()
            .id(DETECTION_TASK_ID)
            .parcels(
                List.of(
                    Parcel.builder().id("parcel1Id").build(),
                    Parcel.builder().id("parcel2Id").build()))
            .statusHistory(
                List.of(
                    TaskStatus.builder()
                        .id(randomUUID().toString())
                        .progression(PENDING)
                        .jobType(DETECTION)
                        .health(UNKNOWN)
                        .build()))
            .submissionInstant(Instant.now())
            .build());
    objectConfigurationRepository.save(
        DetectableObjectConfiguration.builder()
            .id("detectableObjectConfigurationId1")
            .confidence(0.70)
            .objectType(DetectableType.ROOF)
            .detectionJobId(JOB_ID)
            .build());
  }

  @Test
  void accept_ok() {
    var before = detectedTileRepository.findAllByJobId(JOB_ID);

    subject.accept(someDetectionTask(JOB_ID, DETECTION_TASK_ID));

    var actual = detectedTileRepository.findAllByJobId(JOB_ID);
    assertTrue(before.isEmpty());
    assertEquals(
        List.of(someDetectedTile()),
        actual.stream().peek(tile -> tile.setCreationDatetime(null)).toList());
  }

  private static DetectedTile someDetectedTile() {
    return detectedTile(JOB_ID, "tileId", "parcel1Id", "detectedObject1Id", 0.98);
  }
}