package app.bpartners.geojobs.service.event;

import static java.util.UUID.randomUUID;

import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.endpoint.event.gen.HumanDetectionJobCreatedFailed;
import app.bpartners.geojobs.repository.DetectedTileRepository;
import app.bpartners.geojobs.repository.HumanDetectionJobRepository;
import app.bpartners.geojobs.repository.model.detection.DetectedTile;
import app.bpartners.geojobs.repository.model.detection.HumanDetectionJob;
import app.bpartners.geojobs.service.annotator.AnnotationService;
import app.bpartners.geojobs.service.detection.DetectionTaskService;
import app.bpartners.geojobs.service.detection.ZoneDetectionJobService;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ZoneDetectionJobAnnotationProcessor implements Consumer<String> {
  private final AnnotationService annotationService;
  private final DetectionTaskService detectionTaskService;
  private final DetectedTileRepository detectedTileRepository;
  private final HumanDetectionJobRepository humanDetectionJobRepository;
  private final EventProducer eventProducer;
  private final ZoneDetectionJobService zoneDetectionJobService;

  @Override
  public void accept(String zoneDetectionJobId) {
    String humanDetectionJobId = randomUUID().toString();
    String inDoubtHumanDetectionJobId = randomUUID().toString();
    String annotationJobId = randomUUID().toString();
    String inDoubtAnnotationJobId = randomUUID().toString();

    var humanJob = zoneDetectionJobService.getHumanZdjFromZdjId(zoneDetectionJobId);
    var humanZDJId = humanJob.getId();

    List<DetectedTile> detectedTiles = detectedTileRepository.findAllByJobId(zoneDetectionJobId);
    List<DetectedTile> inDoubtTiles =
        detectionTaskService.findInDoubtTilesByJobId(zoneDetectionJobId, detectedTiles).stream()
            .peek(detectedTile -> detectedTile.setHumanDetectionJobId(humanDetectionJobId))
            .toList();
    List<DetectedTile> tilesWithoutObject =
        detectedTiles.stream()
            .filter(detectedTile -> detectedTile.getDetectedObjects().isEmpty())
            .toList();
    if (inDoubtTiles.isEmpty() && !tilesWithoutObject.isEmpty()) {
      log.error(
          "Any in doubt tiles detected from ZoneDetectionJob(id={})."
              + " {} tiles without detected objects are still sent.",
          zoneDetectionJobId,
          tilesWithoutObject.size());
    }
    HumanDetectionJob savedHumanDetectionJob =
        humanDetectionJobRepository.save(
            HumanDetectionJob.builder()
                .id(humanDetectionJobId)
                .annotationJobId(annotationJobId)
                .detectedTiles(inDoubtTiles)
                .zoneDetectionJobId(humanZDJId)
                .build());
    HumanDetectionJob savedHumanDetectionJobWithoutTile =
        humanDetectionJobRepository.save(
            HumanDetectionJob.builder()
                .id(inDoubtHumanDetectionJobId)
                .annotationJobId(inDoubtAnnotationJobId)
                .detectedTiles(tilesWithoutObject)
                .zoneDetectionJobId(humanZDJId)
                .build());

    savedHumanDetectionJob.setDetectedTiles(inDoubtTiles); // TODO: check if still necessary
    savedHumanDetectionJobWithoutTile.setDetectedTiles(
        tilesWithoutObject); // TODO: check if still necessary
    detectedTileRepository.saveAll(
        Stream.of(inDoubtTiles, tilesWithoutObject)
            .flatMap(List::stream)
            .toList()); // TODO: check if still necessary
    try {
      annotationService.createAnnotationJob(savedHumanDetectionJob);
    } catch (Exception e) {
      eventProducer.accept(
          List.of(
              HumanDetectionJobCreatedFailed.builder()
                  .humanDetectionJobId(savedHumanDetectionJob.getId())
                  .attemptNb(1)
                  .build()));
      return;
    }
    try {
      annotationService.createAnnotationJob(savedHumanDetectionJobWithoutTile);
    } catch (Exception e) {
      eventProducer.accept(
          List.of(
              HumanDetectionJobCreatedFailed.builder()
                  .humanDetectionJobId(savedHumanDetectionJobWithoutTile.getId())
                  .attemptNb(1)
                  .build()));
      return;
    }
    log.info(
        "HumanDetectionJob {} created, annotations sent to bpartners-annotation-api",
        savedHumanDetectionJob);
  }
}