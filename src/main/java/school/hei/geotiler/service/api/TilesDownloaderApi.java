package school.hei.geotiler.service.api;

import static java.nio.file.Files.createTempFile;
import static school.hei.geotiler.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import school.hei.geotiler.file.FileUnzipper;
import school.hei.geotiler.file.FileWriter;
import school.hei.geotiler.model.exception.ApiException;
import school.hei.geotiler.repository.model.geo.Parcel;

@Component
public class TilesDownloaderApi {
  private final ObjectMapper om;
  private final String tilesDownloaderApiURl;
  private final FileWriter fileWriter;
  private final FileUnzipper fileUnzipper;

  public TilesDownloaderApi(
      @Value("${tiles.downloader.api.url}") String tilesDownloaderApiURl,
      ObjectMapper om,
      FileWriter fileWriter,
      FileUnzipper fileUnzipper) {
    this.om = om;
    this.tilesDownloaderApiURl = tilesDownloaderApiURl;
    this.fileWriter = fileWriter;
    this.fileUnzipper = fileUnzipper;
  }

  public File downloadTiles(Parcel parcel) {
    RestTemplate restTemplate = new RestTemplate();
    MultipartBodyBuilder bodies = new MultipartBodyBuilder();
    bodies.part("server", new FileSystemResource(getServerInfoFile(parcel)));
    bodies.part("geojson", getGeojson(parcel));
    MultiValueMap<String, HttpEntity<?>> multipartBody = bodies.build();
    HttpEntity<MultiValueMap<String, HttpEntity<?>>> request = new HttpEntity<>(multipartBody);
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(tilesDownloaderApiURl)
            .queryParam("zoom_size", parcel.getFeature().getZoom());

    ResponseEntity<byte[]> responseEntity =
        restTemplate.postForEntity(builder.toUriString(), request, byte[].class);

    if (responseEntity.getStatusCode().value() == 200) {
      try {
        return unzip(fileWriter.apply(responseEntity.getBody(), null), parcel);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    throw new ApiException(SERVER_EXCEPTION, "Server error");
  }

  private File unzip(File downloadedTiles, Parcel parcel) throws IOException {
    ZipFile asZipFile = new ZipFile(downloadedTiles);
    String layer = parcel.getGeoServerParameter().getLayers();
    Path unzippedPath = fileUnzipper.apply(asZipFile, layer);
    return unzippedPath.toFile();
  }

  @SneakyThrows
  public File getServerInfoFile(Parcel parcel) {
    var geoServerParameter = parcel.getGeoServerParameter();
    String geoServerUrl = String.valueOf(parcel.getGeoServerUrl());
    String service = geoServerParameter.getService();
    String request = geoServerParameter.getRequest();
    String layers = geoServerParameter.getLayers();
    String styles = geoServerParameter.getStyles();
    String format = geoServerParameter.getFormat();
    String transparent = String.valueOf(geoServerParameter.getTransparent());
    String version = geoServerParameter.getVersion();
    String width = String.valueOf(geoServerParameter.getWidth());
    String height = String.valueOf(geoServerParameter.getHeight());
    String srs = geoServerParameter.getSrs();

    Map<String, Object> serverInfo = new HashMap<>();
    serverInfo.put("url", geoServerUrl);

    Map<String, Object> serverParameter = new HashMap<>();
    serverParameter.put("service", service);
    serverParameter.put("request", request);
    serverParameter.put("layers", layers);
    serverParameter.put("styles", styles);
    serverParameter.put("format", format);
    serverParameter.put("transparent", transparent);
    serverParameter.put("version", version);
    serverParameter.put("width", width);
    serverParameter.put("height", height);
    serverParameter.put("srs", srs);

    serverInfo.put("parameter", serverParameter);
    serverInfo.put("concurrency", 1);

    Path serverInfoPath = createTempFile(tempFileParcelPrefix(parcel) + "_server", "json");
    File file = serverInfoPath.toFile();
    om.writeValue(file, serverInfo);

    return file;
  }

  @SneakyThrows
  public FileSystemResource getGeojson(Parcel parcel) {
    Map<String, Object> feature = new HashMap<>();
    feature.put("type", "Feature");
    feature.put("geometry", parcel.getFeature().getGeometry());

    var featuresList = new ArrayList<>();
    featuresList.add(feature);

    Map<String, Object> featureCollection = new HashMap<>();
    featureCollection.put("type", "FeatureCollection");
    featureCollection.put("features", featuresList);

    Path geojsonPath = createTempFile(tempFileParcelPrefix(parcel), "geojson");
    File geojsonFile = geojsonPath.toFile();
    om.writeValue(geojsonFile, featureCollection);

    return new FileSystemResource(geojsonFile);
  }

  private static String tempFileParcelPrefix(Parcel parcel) {
    return "parcel_" + parcel.getId();
  }
}
