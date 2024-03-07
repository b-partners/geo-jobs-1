package app.bpartners.geojobs.repository.model.detection;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HumanDetectionJob {
  @Id private String id;
  private String annotationJobId;

  @JoinColumn(referencedColumnName = "id")
  private String zoneDetectionJobId;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "human_detection_job_id")
  private List<DetectedTile> inDoubtTiles;
}
