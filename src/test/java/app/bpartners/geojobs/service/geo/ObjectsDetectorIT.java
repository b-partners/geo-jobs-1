package app.bpartners.geojobs.service.geo;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.bpartners.geojobs.conf.BucketConf;
import app.bpartners.geojobs.conf.FacadeIT;
import app.bpartners.geojobs.file.BucketCustomizedComponent;
import app.bpartners.geojobs.model.exception.NotImplementedException;
import app.bpartners.geojobs.repository.model.Parcel;
import app.bpartners.geojobs.repository.model.ParcelContent;
import app.bpartners.geojobs.repository.model.TileDetectionTask;
import app.bpartners.geojobs.repository.model.detection.DetectableType;
import app.bpartners.geojobs.repository.model.detection.ParcelDetectionTask;
import app.bpartners.geojobs.repository.model.tiling.Tile;
import app.bpartners.geojobs.service.detection.TileObjectDetector;
import java.io.File;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@Disabled("TODO: fail with 500 internal server error")
public class ObjectsDetectorIT extends FacadeIT {
  private static final String FILE_NAME =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "mockData"
          + File.separator
          + "image-to-detect.jpg";
  @MockBean BucketConf bucketConf;
  @MockBean BucketCustomizedComponent bucketComponent;
  @Autowired TileObjectDetector objectsDetector;

  @Test
  void process_detection_ok() {
    var actual = objectsDetector.apply(detectionTask(), List.of(DetectableType.PATHWAY));

    assertNotNull(actual);
    assertNotNull(actual.getRstImageUrl());
    assertNotNull(actual.getSrcImageUrl());
    assertNotNull(actual.getRstRaw());
  }

  @Test
  void process_detection_multiple_ko() {
    assertThrows(
        NotImplementedException.class,
        () ->
            objectsDetector.apply(
                detectionTask(), List.of(DetectableType.ROOF, DetectableType.PATHWAY)));
  }

  public TileDetectionTask detectionTask() {
    when(bucketComponent.getBucketConf())
        .thenReturn(new app.bpartners.geojobs.file.BucketConf("dummyRegion", "dummyBucketName"));
    when(bucketComponent.download(any(), any())).thenReturn(new File(FILE_NAME));

    var task =
        ParcelDetectionTask.builder()
            .id(String.valueOf(randomUUID()))
            .jobId(String.valueOf(randomUUID()))
            .submissionInstant(Instant.now())
            .parcels(
                List.of(
                    Parcel.builder()
                        .id(randomUUID().toString())
                        .parcelContent(
                            ParcelContent.builder()
                                .id(randomUUID().toString())
                                .tiles(
                                    List.of(
                                        Tile.builder()
                                            .id(randomUUID().toString())
                                            .bucketPath(randomUUID().toString())
                                            .build()))
                                .build())
                        .build()))
            .build();

    return new TileDetectionTask(
        "tileDetectionTask",
        task.getId(),
        task.getParcel().getId(),
        task.getJobId(),
        task.getParcel().getParcelContent().getFirstTile(),
        List.of());
  }
}
