/*
 * Image Annotation API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: latest
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package app.bpartners.annotator.endpoint.rest.api;

import app.bpartners.annotator.endpoint.rest.client.ApiClient;
import app.bpartners.annotator.endpoint.rest.client.ApiException;
import app.bpartners.annotator.endpoint.rest.client.ApiResponse;
import app.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import app.bpartners.annotator.endpoint.rest.model.Job;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

@javax.annotation.processing.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2024-02-26T18:28:50.570918+01:00[Europe/Paris]")
public class AnnotatedJobsApi {
  private final HttpClient memberVarHttpClient;
  private final ObjectMapper memberVarObjectMapper;
  private final String memberVarBaseUri;
  private final Consumer<HttpRequest.Builder> memberVarInterceptor;
  private final Duration memberVarReadTimeout;
  private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
  private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;

  public AnnotatedJobsApi() {
    this(new ApiClient());
  }

  public AnnotatedJobsApi(ApiClient apiClient) {
    memberVarHttpClient = apiClient.getHttpClient();
    memberVarObjectMapper = apiClient.getObjectMapper();
    memberVarBaseUri = apiClient.getBaseUri();
    memberVarInterceptor = apiClient.getRequestInterceptor();
    memberVarReadTimeout = apiClient.getReadTimeout();
    memberVarResponseInterceptor = apiClient.getResponseInterceptor();
    memberVarAsyncResponseInterceptor = apiClient.getAsyncResponseInterceptor();
  }

  protected ApiException getApiException(String operationId, HttpResponse<InputStream> response)
      throws IOException {
    String body = response.body() == null ? null : new String(response.body().readAllBytes());
    String message = formatExceptionMessage(operationId, response.statusCode(), body);
    return new ApiException(response.statusCode(), message, response.headers(), body);
  }

  private String formatExceptionMessage(String operationId, int statusCode, String body) {
    if (body == null || body.isEmpty()) {
      body = "[no body]";
    }
    return operationId + " call failed with: " + statusCode + " - " + body;
  }

  /**
   * create or update an annotated job
   *
   * @param jobId (required)
   * @param crupdateAnnotatedJob (optional)
   * @return Job
   * @throws ApiException if fails to make API call
   */
  public Job crupdateAnnotatedJob(String jobId, CrupdateAnnotatedJob crupdateAnnotatedJob)
      throws ApiException {
    ApiResponse<Job> localVarResponse =
        crupdateAnnotatedJobWithHttpInfo(jobId, crupdateAnnotatedJob);
    return localVarResponse.getData();
  }

  /**
   * create or update an annotated job
   *
   * @param jobId (required)
   * @param crupdateAnnotatedJob (optional)
   * @return ApiResponse&lt;Job&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Job> crupdateAnnotatedJobWithHttpInfo(
      String jobId, CrupdateAnnotatedJob crupdateAnnotatedJob) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder =
        crupdateAnnotatedJobRequestBuilder(jobId, crupdateAnnotatedJob);
    try {
      HttpResponse<InputStream> localVarResponse =
          memberVarHttpClient.send(
              localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      if (localVarResponse.statusCode() / 100 != 2) {
        throw getApiException("crupdateAnnotatedJob", localVarResponse);
      }
      return new ApiResponse<Job>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Job>() {}));
    } catch (IOException e) {
      throw new ApiException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder crupdateAnnotatedJobRequestBuilder(
      String jobId, CrupdateAnnotatedJob crupdateAnnotatedJob) throws ApiException {
    // verify the required parameter 'jobId' is set
    if (jobId == null) {
      throw new ApiException(
          400, "Missing the required parameter 'jobId' when calling crupdateAnnotatedJob");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath =
        "/annotated-jobs/{jobId}".replace("{jobId}", ApiClient.urlEncode(jobId.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(crupdateAnnotatedJob);
      localVarRequestBuilder.method(
          "PUT", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
    } catch (IOException e) {
      throw new ApiException(e);
    }
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
}
