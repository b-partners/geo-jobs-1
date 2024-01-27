package app.bpartners.geojobs.service.geo.tiling;

import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.endpoint.event.gen.TilingTaskCreated;
import app.bpartners.geojobs.endpoint.event.gen.ZoneTilingJobCreated;
import app.bpartners.geojobs.endpoint.event.gen.ZoneTilingJobStatusChanged;
import app.bpartners.geojobs.model.exception.NotFoundException;
import app.bpartners.geojobs.repository.model.geo.Parcel;
import app.bpartners.geojobs.repository.model.geo.tiling.TilingTask;
import app.bpartners.geojobs.repository.model.geo.tiling.ZoneTilingJob;
import app.bpartners.geojobs.service.ZoneJobService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ZoneTilingJobService extends ZoneJobService<TilingTask, ZoneTilingJob> {

  public ZoneTilingJobService(
      JpaRepository<ZoneTilingJob, String> repository, EventProducer eventProducer) {
    super(repository, eventProducer);
  }

  @Override
  public ZoneTilingJob create(ZoneTilingJob job) {
    var saved = super.create(job);
    eventProducer.accept(List.of(new ZoneTilingJobCreated(saved)));
    return saved;
  }

  public List<Parcel> getAJobParcel(String jobId) {
    Optional<ZoneTilingJob> zoneTilingJob = repository.findById(jobId);
    if (zoneTilingJob.isPresent()) {
      return zoneTilingJob.get().getTasks().stream().map(TilingTask::getParcel).toList();
    }
    throw new NotFoundException("The job is not found");
  }

  public void fireTasks(ZoneTilingJob job) {
    job.getTasks().forEach(task -> eventProducer.accept(List.of(new TilingTaskCreated(task))));
  }

  @Override
  public ZoneTilingJob refreshStatus(ZoneTilingJob job) {
    var refreshed = super.refreshStatus(job);

    if (!refreshed.getStatus().equals(job.getStatus())) {
      eventProducer.accept(
          List.of(ZoneTilingJobStatusChanged.builder().oldJob(job).newJob(refreshed).build()));
    }
    return refreshed;
  }
}