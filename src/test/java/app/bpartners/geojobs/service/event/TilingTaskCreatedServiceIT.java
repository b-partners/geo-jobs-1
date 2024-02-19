package app.bpartners.geojobs.service.event;

import static app.bpartners.geojobs.file.FileHashAlgorithm.SHA256;
import static app.bpartners.geojobs.job.model.Status.HealthStatus.FAILED;
import static app.bpartners.geojobs.job.model.Status.HealthStatus.SUCCEEDED;
import static app.bpartners.geojobs.job.model.Status.HealthStatus.UNKNOWN;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.FINISHED;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.PENDING;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.PROCESSING;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.bpartners.geojobs.conf.FacadeIT;
import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.endpoint.event.gen.TilingTaskCreated;
import app.bpartners.geojobs.endpoint.event.gen.TilingTaskFailed;
import app.bpartners.geojobs.endpoint.event.gen.TilingTaskSucceeded;
import app.bpartners.geojobs.endpoint.event.gen.ZoneTilingJobStatusChanged;
import app.bpartners.geojobs.endpoint.rest.controller.ZoneTilingController;
import app.bpartners.geojobs.endpoint.rest.model.Feature;
import app.bpartners.geojobs.endpoint.rest.model.GeoServerParameter;
import app.bpartners.geojobs.file.BucketComponent;
import app.bpartners.geojobs.file.FileHash;
import app.bpartners.geojobs.job.model.TaskStatus;
import app.bpartners.geojobs.repository.TilingTaskRepository;
import app.bpartners.geojobs.repository.ZoneTilingJobRepository;
import app.bpartners.geojobs.repository.model.Parcel;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import app.bpartners.geojobs.repository.model.tiling.ZoneTilingJob;
import app.bpartners.geojobs.service.tiling.TilesDownloader;
import app.bpartners.geojobs.service.tiling.TilingTaskService;
import app.bpartners.geojobs.service.tiling.TilingTaskStatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@Slf4j
class TilingTaskCreatedServiceIT extends FacadeIT {
  @Autowired TilingTaskCreatedService subject;
  @Autowired ZoneTilingController zoneTilingController;
  @MockBean BucketComponent bucketComponent;
  @MockBean TilesDownloader tilesDownloader;
  @Autowired TilingTaskRepository tilingTaskRepository;
  @Autowired TilingTaskService tilingTaskService;
  @Autowired ZoneTilingJobRepository zoneTilingJobRepository;
  @MockBean EventProducer eventProducer;
  @Autowired ObjectMapper om;
  @Autowired TilingTaskStatusService tilingTaskStatusService;
  @Autowired TilingTaskSucceededService tilingTaskSucceededService;
  @Autowired TilingTaskFailedService tilingTaskFailedService;

  private Feature lyonFeature;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    when(tilesDownloader.apply(any()))
        .thenAnswer(
            i ->
                Paths.get(this.getClass().getClassLoader().getResource("mockData/lyon").toURI())
                    .toFile());
    when(bucketComponent.upload(any(), any())).thenReturn(new FileHash(SHA256, "mock"));

    lyonFeature =
        om.readValue(
                """
                    { "type": "Feature",
                      "properties": {
                        "code": "69",
                        "nom": "Rhône",
                        "id": 30251921,
                        "CLUSTER_ID": 99520,
                        "CLUSTER_SIZE": 386884 },
                      "geometry": {
                        "type": "MultiPolygon",
                        "coordinates": [ [ [
                            [ 4.459648282829194, 45.904988912620688 ],
                            [ 4.464709510872551, 45.928950368349426 ],
                            [ 4.490816965688656, 45.941784543770964 ],
                            [ 4.510354299995861, 45.933697132664598 ],
                            [ 4.518386257467152, 45.912888345521047 ],
                            [ 4.496344031095243, 45.883438201401809 ],
                            [ 4.479593950305621, 45.882900828315755 ],
                            [ 4.459648282829194, 45.904988912620688 ] ] ] ] } }""",
                Feature.class)
            .zoom(10)
            .id("feature_1_id");
  }

  private ZoneTilingJob aZTJ(String jobId) {
    return ZoneTilingJob.builder()
        .id(jobId)
        .zoneName("mock")
        .emailReceiver("mock@hotmail.com")
        .build();
  }

  @SneakyThrows
  private TilingTask aZTT(String jobId, String taskId) {
    return TilingTask.builder()
        .id(taskId)
        .jobId(jobId)
        .parcel(
            Parcel.builder()
                .id(randomUUID().toString())
                .creationDatetime(now())
                .geoServerParameter(new GeoServerParameter().layers("grand-lyon"))
                .feature(lyonFeature)
                .build())
        .statusHistory(
            List.of(
                TaskStatus.builder()
                    .id(randomUUID().toString())
                    .taskId(taskId)
                    .progression(PENDING)
                    .health(UNKNOWN)
                    .build()))
        .build();
  }

  @SneakyThrows
  private TilingTask aZTT_processing(String jobId, String taskId) {
    return TilingTask.builder()
        .id(taskId)
        .jobId(jobId)
        .parcel(
            Parcel.builder()
                .id(randomUUID().toString())
                .geoServerParameter(new GeoServerParameter().layers("grand-lyon"))
                .feature(
                    om.readValue(
                            """
                                { "type": "Feature",
                                  "properties": {
                                    "code": "69",
                                    "nom": "Rhône",
                                    "id": 30251921,
                                    "CLUSTER_ID": 99520,
                                    "CLUSTER_SIZE": 386884 },
                                  "geometry": {
                                    "type": "MultiPolygon",
                                    "coordinates": [ [ [
                                      [ 4.459648282829194, 45.904988912620688 ],
                                      [ 4.464709510872551, 45.928950368349426 ],
                                      [ 4.490816965688656, 45.941784543770964 ],
                                      [ 4.510354299995861, 45.933697132664598 ],
                                      [ 4.518386257467152, 45.912888345521047 ],
                                      [ 4.496344031095243, 45.883438201401809 ],
                                      [ 4.479593950305621, 45.882900828315755 ],
                                      [ 4.459648282829194, 45.904988912620688 ] ] ] ] } }""",
                            Feature.class)
                        .zoom(10)
                        .id("feature_1_id"))
                .build())
        .statusHistory(
            List.of(
                TaskStatus.builder()
                    .id(randomUUID().toString())
                    .taskId(taskId)
                    .progression(PROCESSING)
                    .health(UNKNOWN)
                    .build()))
        .build();
  }

  @Test
  void unzip_and_upload_ok() {
    String jobId = randomUUID().toString();
    ZoneTilingJob job =
        zoneTilingJobRepository.save(
            ZoneTilingJob.builder()
                .id(jobId)
                .zoneName("mock")
                .emailReceiver("mock@hotmail.com")
                .build());
    String taskId = randomUUID().toString();
    TilingTask toCreate =
        TilingTask.builder()
            .id(taskId)
            .jobId(job.getId())
            .parcel(
                Parcel.builder()
                    .geoServerParameter(new GeoServerParameter().layers("grand-lyon"))
                    .id(randomUUID().toString())
                    .build())
            .statusHistory(
                List.of(
                    TaskStatus.builder()
                        .id(randomUUID().toString())
                        .taskId(taskId)
                        .progression(PENDING)
                        .health(UNKNOWN)
                        .build()))
            .build();
    TilingTask created = tilingTaskRepository.save(toCreate);
    TilingTaskCreated createdEventPayload = TilingTaskCreated.builder().task(created).build();

    subject.accept(createdEventPayload);

    int numberOfDirectoryToUpload = 1;
    verify(bucketComponent, times(numberOfDirectoryToUpload)).upload(any(), any(String.class));
  }

  @Test
  void send_tilingSucceed_on_success() {
    String jobId = randomUUID().toString();
    zoneTilingJobRepository.save(aZTJ(jobId));
    String taskId = randomUUID().toString();
    TilingTask toCreate = aZTT(jobId, taskId);
    TilingTask created = tilingTaskRepository.save(toCreate);
    TilingTaskCreated createdEventPayload = TilingTaskCreated.builder().task(created).build();

    subject.accept(createdEventPayload);

    var eventsCaptor = ArgumentCaptor.forClass(List.class);
    verify(eventProducer, times(2)).accept(eventsCaptor.capture());
    var sentEvents = eventsCaptor.getAllValues().stream().flatMap(List::stream).toList();
    assertEquals(2, sentEvents.size());
    var tilingTaskSucceeded = (TilingTaskSucceeded) sentEvents.get(1);
    assertEquals(2, tilingTaskSucceeded.getTask().getParcel().getTiles().size());
  }

  @SneakyThrows
  @Test
  void concurrently_tile() {
    var callersNb = 40;
    var executorService = newFixedThreadPool(callersNb);
    var callables = new ArrayList<Callable<Boolean>>();
    String jobId = randomUUID().toString();
    zoneTilingJobRepository.save(aZTJ(jobId));
    for (int i = 0; i < callersNb; i++) {
      String taskId = randomUUID().toString();
      TilingTask toCreate = aZTT(jobId, taskId);
      TilingTask created = tilingTaskRepository.save(toCreate);
      TilingTaskCreated createdEventPayload = TilingTaskCreated.builder().task(created).build();
      callables.add(() -> isTilingEventHandledSuccessfully(createdEventPayload));
    }

    var futures = executorService.invokeAll(callables);

    var succeeded =
        futures.stream()
            .map(TilingTaskCreatedServiceIT::getFutureBoolean)
            .filter(result -> result)
            .count();
    assertEquals(callersNb, succeeded);
  }

  @SneakyThrows
  @Test
  void concurrently_save_tasks() {
    /*******************************************************************/
    /**************** Concurrently create new tasks...  ****************/
    /*******************************************************************/
    var callersNb = 40;
    var executorService = newFixedThreadPool(callersNb);
    List<Callable<Boolean>> callables = new ArrayList<>();
    String jobId = randomUUID().toString();
    zoneTilingJobRepository.save(aZTJ(jobId));
    for (int i = 0; i < callersNb; i++) {
      String taskId = randomUUID().toString();
      TilingTask toCreate = aZTT(jobId, taskId);
      callables.add(() -> isTaskSavedSuccessfully(toCreate));
    }

    var futures = executorService.invokeAll(callables);

    assertFuturesAllSucceed(futures);

    /*******************************************************************/
    /*********** ...then concurrently make them all fail! **************/
    /*******************************************************************/
    var savedTasks = tilingTaskRepository.findAllByJobId(jobId);
    callables =
        savedTasks.stream()
            .map(
                task ->
                    (Callable<Boolean>)
                        () ->
                            isTaskSavedSuccessfully(
                                task.toBuilder()
                                    .statusHistory(
                                        List.of(
                                            TaskStatus.builder()
                                                .progression(FINISHED)
                                                .health(FAILED)
                                                .build()))
                                    .build()))
            .collect(toList());

    futures = executorService.invokeAll(callables);

    assertFuturesAllSucceed(futures);
  }

  private static void assertFuturesAllSucceed(List<Future<Boolean>> futures) {
    var succeeded =
        futures.stream()
            .map(TilingTaskCreatedServiceIT::getFutureBoolean)
            .filter(result -> result)
            .count();
    assertEquals(futures.size(), succeeded);
  }

  private Boolean isTilingEventHandledSuccessfully(TilingTaskCreated createdEventPayload) {
    try {
      subject.accept(createdEventPayload);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Boolean isTaskSavedSuccessfully(TilingTask tilingTask) {
    try {
      tilingTaskService.save(tilingTask);
      return true;
    } catch (Exception e) {
      log.error("Task saving failed, task={}", tilingTask, e);
      return false;
    }
  }

  @SneakyThrows
  private static Boolean getFutureBoolean(Future<Boolean> future) {
    return future.get();
  }

  @Test
  void send_tilingTaskSucceeded_and_jobStatusChanged_after_task_is_processed_successfully() {
    String jobId = randomUUID().toString();
    zoneTilingJobRepository.save(aZTJ(jobId));
    String taskId = randomUUID().toString();
    TilingTask toCreate = aZTT(jobId, taskId);
    TilingTask created = tilingTaskRepository.save(toCreate);
    TilingTaskCreated createdEventPayload = TilingTaskCreated.builder().task(created).build();

    subject.accept(createdEventPayload);

    var eventsCaptor = ArgumentCaptor.forClass(List.class);
    verify(eventProducer, times(2)).accept(eventsCaptor.capture());
    var events = eventsCaptor.getAllValues();
    assertEquals(2, events.size());
    // Job status changed
    var jobStatusChanged = ((ZoneTilingJobStatusChanged) events.get(0).get(0));
    assertEquals(PENDING, jobStatusChanged.getOldJob().getStatus().getProgression());
    assertEquals(UNKNOWN, jobStatusChanged.getOldJob().getStatus().getHealth());
    assertEquals(PROCESSING, jobStatusChanged.getNewJob().getStatus().getProgression());
    assertEquals(UNKNOWN, jobStatusChanged.getNewJob().getStatus().getHealth());
    // Task is notified as finished in event...
    var taskStatusInEvent = ((TilingTaskSucceeded) events.get(1).get(0)).getTask().getStatus();
    assertEquals(FINISHED, taskStatusInEvent.getProgression());
    assertEquals(SUCCEEDED, taskStatusInEvent.getHealth());
    // ... but job remains processing in db
    var jobStatusInDb = zoneTilingJobRepository.findById(jobId).get().getStatus();
    assertEquals(PROCESSING, jobStatusInDb.getProgression());
    assertEquals(UNKNOWN, jobStatusInDb.getHealth());
  }

  @Test
  void task_processing_to_pending_ko() {
    String jobId = randomUUID().toString();
    zoneTilingJobRepository.save(aZTJ(jobId));
    String taskId = randomUUID().toString();
    TilingTask toCreate = aZTT_processing(jobId, taskId);
    TilingTask created = tilingTaskRepository.save(toCreate);
    List<TaskStatus> statuses =
        tilingTaskRepository.findById(created.getId()).orElseThrow().getStatusHistory().stream()
            .toList();

    List<TaskStatus> statusesAfterFailedStatusTransition =
        tilingTaskRepository.findById(created.getId()).orElseThrow().getStatusHistory().stream()
            .toList();

    assertEquals(statusesAfterFailedStatusTransition, statuses);
    assertFalse(statuses.isEmpty());
    assertFalse(statusesAfterFailedStatusTransition.isEmpty());
  }

  @Test
  void get_ztj_tiles_after_successful_first_attempt() {
    String jobId = randomUUID().toString();
    zoneTilingJobRepository.save(aZTJ(jobId));
    TilingTask toCreate = aZTT(jobId, randomUUID().toString());
    TilingTask created = tilingTaskRepository.save(toCreate);
    TilingTaskCreated ztjCreated = TilingTaskCreated.builder().task(created).build();

    subject.accept(ztjCreated);
    var eventsCaptor = ArgumentCaptor.forClass(List.class);
    verify(eventProducer, times(2)).accept(eventsCaptor.capture());
    var events = eventsCaptor.getAllValues();
    var taskSucceeded = (TilingTaskSucceeded) events.get(1).get(0);
    tilingTaskSucceededService.accept(taskSucceeded);
    List<app.bpartners.geojobs.endpoint.rest.model.Parcel> parcels =
        zoneTilingController.getZTJParcels(jobId);

    assertEquals(1, parcels.size());
    assertEquals(2, parcels.get(0).getTiles().size());
  }

  @Test
  void get_ztj_tiles_after_successful_second_attempt() {
    String jobId = randomUUID().toString();
    zoneTilingJobRepository.save(aZTJ(jobId));
    TilingTask toCreate = aZTT(jobId, randomUUID().toString());
    TilingTask created = tilingTaskRepository.save(toCreate);
    TilingTaskCreated ztjCreated = TilingTaskCreated.builder().task(created).build();

    // first attempt
    reset(tilesDownloader);
    when(tilesDownloader.apply(any())).thenThrow(new RuntimeException());
    subject.accept(ztjCreated);
    var eventsCaptor = ArgumentCaptor.forClass(List.class);
    verify(eventProducer, times(2)).accept(eventsCaptor.capture());
    var events = eventsCaptor.getAllValues();
    var taskFailed = (TilingTaskFailed) events.get(1).get(0);

    // second attempt
    reset(tilesDownloader);
    when(tilesDownloader.apply(any()))
        .thenAnswer(
            i ->
                Paths.get(this.getClass().getClassLoader().getResource("mockData/lyon").toURI())
                    .toFile());
    reset(eventProducer);
    tilingTaskFailedService.accept(taskFailed);
    eventsCaptor = ArgumentCaptor.forClass(List.class);
    verify(eventProducer, times(1)).accept(eventsCaptor.capture());
    events = eventsCaptor.getAllValues();
    var taskSucceeded = (TilingTaskSucceeded) events.get(0).get(0);
    tilingTaskSucceededService.accept(taskSucceeded);
    List<app.bpartners.geojobs.endpoint.rest.model.Parcel> parcels =
        zoneTilingController.getZTJParcels(jobId);

    assertEquals(1, parcels.size());
    assertEquals(2, parcels.get(0).getTiles().size());
  }
}
