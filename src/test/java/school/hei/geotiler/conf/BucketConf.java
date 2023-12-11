package school.hei.geotiler.conf;

import org.springframework.test.context.DynamicPropertyRegistry;
import school.hei.geotiler.PojaGenerated;

@PojaGenerated
public class BucketConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.s3.bucket", () -> "dummy-bucket");
  }
}
