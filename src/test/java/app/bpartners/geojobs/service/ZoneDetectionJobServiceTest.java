package app.bpartners.geojobs.service;

import static app.bpartners.geojobs.job.model.Status.HealthStatus.*;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.*;
import static app.bpartners.geojobs.repository.model.GeoJobType.DETECTION;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.endpoint.event.model.TaskStatisticRecomputingSubmitted;
import app.bpartners.geojobs.job.model.JobStatus;
import app.bpartners.geojobs.job.model.Status;
import app.bpartners.geojobs.job.model.TaskStatus;
import app.bpartners.geojobs.job.model.statistic.TaskStatistic;
import app.bpartners.geojobs.job.repository.JobStatusRepository;
import app.bpartners.geojobs.repository.ParcelDetectionTaskRepository;
import app.bpartners.geojobs.repository.TileDetectionTaskRepository;
import app.bpartners.geojobs.repository.model.detection.ParcelDetectionTask;
import app.bpartners.geojobs.repository.model.detection.ZoneDetectionJob;
import app.bpartners.geojobs.service.detection.ZoneDetectionJobService;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
public class ZoneDetectionJobServiceTest {
  public static final String JOB_ID = "jobId";
  public static final String JOB2_ID = "job2Id";
  public static final String PARENT_TASK_ID_2 = "taskParentId2";
  public static final String PARENT_TASK_ID_1 = "parentTaskId1";
  public static final String JOB_3_ID = "job3_id";
  private static final String JOB_4_ID = "job4_id";
  private static final String JOB_ID_NOT_FOUND = "job_id_not_found";
  public static final String JOB_5_ID = "job5_id";
  JpaRepository<ZoneDetectionJob, String> jobRepositoryMock = mock();
  JobStatusRepository jobStatusRepositoryMock = mock();
  ParcelDetectionTaskRepository taskRepositoryMock = mock();
  EventProducer eventProducerMock = mock();
  EntityManager entityManagerMock = mock();
  TileDetectionTaskRepository tileDetectionTaskRepositoryMock = mock();
  NotFinishedTaskRetriever<ParcelDetectionTask> notFinishedTaskRetriever =
      new NotFinishedTaskRetriever<>();
  ZoneDetectionJobService subject =
      new ZoneDetectionJobService(
          jobRepositoryMock,
          jobStatusRepositoryMock,
          mock(),
          taskRepositoryMock,
          eventProducerMock,
          mock(),
          mock(),
          mock(),
          mock(),
          mock(),
          mock(),
          tileDetectionTaskRepositoryMock,
          mock(),
          notFinishedTaskRetriever,
          mock());

  @BeforeEach
  void setUp() {
    doNothing().when(entityManagerMock).detach(any());
    subject.setEm(entityManagerMock);
  }

  /*
  @Test
  void retry_failed_tasks_not_found_ko() {
    when(jobRepositoryMock.findById(JOB_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> subject.retryFailedTask(JOB_ID));
  }


      TODO: set when implemented again
  @Test
  void retry_failed_tasks_all_tasks_not_finished_ko() {
    when(jobRepositoryMock.findById(JOB2_ID))
        .thenReturn(Optional.of(ZoneDetectionJob.builder().build()));
    when(taskRepositoryMock.findAllByJobId(JOB2_ID))
        .thenReturn(
            List.of(
                ParcelDetectionTask.builder()
                    .id(PARENT_TASK_ID_2)
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .id(randomUUID().toString())
                                .progression(FINISHED)
                                .jobType(DETECTION)
                                .health(SUCCEEDED)
                                .build()))
                    .build()));
    when(tileDetectionTaskRepositoryMock.findAllByParentTaskId(PARENT_TASK_ID_2))
        .thenReturn(
            List.of(
                TileDetectionTask.builder()
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .id(randomUUID().toString())
                                .progression(FINISHED)
                                .jobType(DETECTION)
                                .health(SUCCEEDED)
                                .build()))
                    .build(),
                TileDetectionTask.builder()
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .id(randomUUID().toString())
                                .progression(FINISHED)
                                .jobType(DETECTION)
                                .health(SUCCEEDED)
                                .build()))
                    .build()));

    assertThrows(BadRequestException.class, () -> subject.retryFailedTask(JOB2_ID));
  }

  TODO: set when implemented again
  @Test
  void dispatch_task_by_success_status_ko() {
    when(jobRepositoryMock.findById(JOB_ID_NOT_FOUND)).thenReturn(Optional.empty());
    when(jobRepositoryMock.findById(JOB_5_ID))
        .thenReturn(
            Optional.of(
                ZoneDetectionJob.builder()
                    .statusHistory(
                        List.of(
                            JobStatus.builder()
                                .progression(FINISHED)
                                .health(SUCCEEDED)
                                .creationDatetime(now())
                                .build()))
                    .build()));

    assertThrows(
        NotFoundException.class, () -> subject.dispatchTasksBySucceededStatus(JOB_ID_NOT_FOUND));
    assertThrows(BadRequestException.class, () -> subject.dispatchTasksBySucceededStatus(JOB_5_ID));
  }

  @Test
  void retry_failed_tasks_ok() {
    when(jobRepositoryMock.findById(JOB_ID)).thenReturn(Optional.of(new ZoneDetectionJob()));
    when(jobRepositoryMock.save(ArgumentMatchers.any()))
        .thenAnswer(
            (Answer<ZoneDetectionJob>)
                invocationOnMock -> {
                  Object[] args = invocationOnMock.getArguments();
                  return (ZoneDetectionJob) args[0];
                });
    when(taskRepositoryMock.findAllByJobId(JOB_ID))
        .thenReturn(
            List.of(
                ParcelDetectionTask.builder()
                    .id(PARENT_TASK_ID_1)
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .jobType(DETECTION)
                                .progression(FINISHED)
                                .health(FAILED)
                                .creationDatetime(now())
                                .build(),
                            TaskStatus.builder()
                                .jobType(DETECTION)
                                .progression(PENDING)
                                .health(RETRYING)
                                .creationDatetime(now())
                                .build()))
                    .build()));
    when(taskRepositoryMock.saveAll(ArgumentMatchers.any()))
        .thenReturn(
            List.of(
                ParcelDetectionTask.builder()
                    .id(PARENT_TASK_ID_1)
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .jobType(DETECTION)
                                .progression(FINISHED)
                                .health(FAILED)
                                .creationDatetime(now())
                                .build(),
                            TaskStatus.builder()
                                .jobType(DETECTION)
                                .progression(PENDING)
                                .health(RETRYING)
                                .creationDatetime(now())
                                .build()))
                    .build()));
    when(tileDetectionTaskRepositoryMock.findAllByParentTaskId(PARENT_TASK_ID_1))
        .thenReturn(
            List.of(
                TileDetectionTask.builder()
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .id(randomUUID().toString())
                                .progression(FINISHED)
                                .jobType(DETECTION)
                                .health(SUCCEEDED)
                                .build()))
                    .build(),
                TileDetectionTask.builder()
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .id(randomUUID().toString())
                                .progression(FINISHED)
                                .jobType(DETECTION)
                                .health(FAILED)
                                .creationDatetime(now())
                                .build()))
                    .build()));

    ZoneDetectionJob actual = subject.retryFailedTask(JOB_ID);

    assertEquals(PROCESSING, actual.getStatus().getProgression());
    assertEquals(RETRYING, actual.getStatus().getHealth());
  }
  @SneakyThrows
  @Test
  void dispatch_task_by_success_status_ok() {
    when(jobRepositoryMock.save(ArgumentMatchers.any()))
        .thenAnswer(
            (Answer<ZoneDetectionJob>)
                invocationOnMock -> {
                  Object[] args = invocationOnMock.getArguments();
                  return (ZoneDetectionJob) args[0];
                });
    when(jobRepositoryMock.findById(JOB_4_ID))
        .thenReturn(
            Optional.of(
                ZoneDetectionJob.builder()
                    .id(JOB_4_ID)
                    .statusHistory(
                        List.of(
                            JobStatus.builder()
                                .progression(PROCESSING)
                                .health(FAILED)
                                .creationDatetime(now())
                                .build()))
                    .build()));
    when(taskRepositoryMock.findById(PARENT_TASK_ID_1))
        .thenReturn(
            Optional.of(
                DetectionTask.builder()
                    .id(PARENT_TASK_ID_1)
                    .parcels(
                        List.of(
                            Parcel.builder()
                                .id(randomUUID().toString())
                                .parcelContent(
                                    ParcelContent.builder()
                                        .id(randomUUID().toString())
                                        .geoServerUrl(new URL("http:/dummy.com"))
                                        .geoServerParameter(new GeoServerParameter())
                                        .feature(new Feature())
                                        .tiles(
                                            List.of(
                                                Tile.builder()
                                                    .id(randomUUID().toString())
                                                    .bucketPath(randomUUID().toString())
                                                    .build()))
                                        .build())
                                .build()))
                    .build()));
    when(taskRepositoryMock.findById(PARENT_TASK_ID_2))
        .thenReturn(
            Optional.of(
                DetectionTask.builder()
                    .id(PARENT_TASK_ID_2)
                    .parcels(
                        List.of(
                            Parcel.builder()
                                .id(randomUUID().toString())
                                .parcelContent(
                                    ParcelContent.builder()
                                        .id(randomUUID().toString())
                                        .geoServerUrl(new URL("http:/dummy.com"))
                                        .geoServerParameter(new GeoServerParameter())
                                        .feature(new Feature())
                                        .tiles(
                                            List.of(
                                                Tile.builder()
                                                    .id(randomUUID().toString())
                                                    .bucketPath(randomUUID().toString())
                                                    .build()))
                                        .build())
                                .build()))
                    .build()));
    when(tileDetectionTaskRepositoryMock.findAllByJobId(JOB_4_ID))
        .thenReturn(
            List.of(
                tileTaskWithStatus(FINISHED, SUCCEEDED, PARENT_TASK_ID_1),
                tileTaskWithStatus(FINISHED, SUCCEEDED, PARENT_TASK_ID_1),
                tileTaskWithStatus(PENDING, UNKNOWN, PARENT_TASK_ID_2),
                tileTaskWithStatus(PENDING, UNKNOWN, PARENT_TASK_ID_2),
                tileTaskWithStatus(FINISHED, FAILED, PARENT_TASK_ID_2),
                tileTaskWithStatus(PROCESSING, UNKNOWN, PARENT_TASK_ID_2)));

    FilteredDetectionJob filteredZoneTilingJobs = subject.dispatchTasksBySucceededStatus(JOB_4_ID);

    var succeededJob = filteredZoneTilingJobs.getSucceededJob();
    var notSucceededJob = filteredZoneTilingJobs.getNotSucceededJob();
    assertEquals(FINISHED, succeededJob.getStatus().getProgression());
    assertEquals(SUCCEEDED, succeededJob.getStatus().getHealth());
    assertEquals(PENDING, notSucceededJob.getStatus().getProgression());
    assertEquals(UNKNOWN, notSucceededJob.getStatus().getHealth());

    var listEventCapture = ArgumentCaptor.forClass(List.class);
    verify(taskRepositoryMock, times(2)).saveAll(listEventCapture.capture());
    var succeededTasks = (List<DetectionTask>) listEventCapture.getAllValues().get(0);
    var notSucceededTasks = (List<DetectionTask>) listEventCapture.getAllValues().get(1);
    assertEquals(1, succeededTasks.size());
    assertTrue(succeededTasks.stream().allMatch(DetectionTask::isSucceeded));
    assertEquals(1, notSucceededTasks.size());
    assertEquals(
        1L,
        notSucceededTasks.stream()
            .filter(
                task ->
                    PENDING.equals(task.getStatus().getProgression())
                        && UNKNOWN.equals(task.getStatus().getHealth()))
            .count());
    var succeededTask = succeededTasks.getFirst();
    var notSucceededTask = notSucceededTasks.getFirst();
    assertEquals(2, succeededTask.getParcel().getParcelContent().getTiles().size());
    assertEquals(4, notSucceededTask.getParcel().getParcelContent().getTiles().size());
  }
  */

  @Test
  void read_task_statistics_ok() {
    when(jobRepositoryMock.findById(JOB_3_ID))
        .thenReturn(
            Optional.of(
                ZoneDetectionJob.builder()
                    .statusHistory(
                        List.of(JobStatus.builder().progression(PROCESSING).health(FAILED).build()))
                    .build()));
    when(taskRepositoryMock.findAllByJobId(JOB_3_ID))
        .thenReturn(
            List.of(
                taskWithStatus(FINISHED, SUCCEEDED),
                taskWithStatus(FINISHED, SUCCEEDED),
                taskWithStatus(PENDING, UNKNOWN),
                taskWithStatus(PENDING, UNKNOWN),
                taskWithStatus(FINISHED, FAILED),
                taskWithStatus(PROCESSING, UNKNOWN)));

    TaskStatistic actual = subject.computeTaskStatistics(JOB_3_ID);

    var eventCapture = ArgumentCaptor.forClass(List.class);
    verify(eventProducerMock, times(1)).accept(eventCapture.capture());
    List<TaskStatisticRecomputingSubmitted> events = eventCapture.getValue();
    var taskStatisticRecomputingEvent = events.getFirst();
    assertEquals(JOB_3_ID, actual.getJobId());
    assertEquals(actual.getJobId(), taskStatisticRecomputingEvent.getJobId());
    assertEquals(FAILED, actual.getActualJobStatus().getHealth());
    assertEquals(PROCESSING, actual.getActualJobStatus().getProgression());
    assertTrue(actual.getTaskStatusStatistics().isEmpty());
  }

  static ParcelDetectionTask taskWithStatus(
      Status.ProgressionStatus progressionStatus, Status.HealthStatus healthStatus) {
    return ParcelDetectionTask.builder()
        .statusHistory(
            List.of(
                TaskStatus.builder()
                    .id(randomUUID().toString())
                    .progression(progressionStatus)
                    .jobType(DETECTION)
                    .health(healthStatus)
                    .build()))
        .build();
  }
}
