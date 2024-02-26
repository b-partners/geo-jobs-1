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


package app.bpartners.annotator.endpoint.rest.model;

import java.util.Objects;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import app.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * AnnotatedTask
 */
@JsonPropertyOrder({
  AnnotatedTask.JSON_PROPERTY_ID,
  AnnotatedTask.JSON_PROPERTY_ANNOTATOR_ID,
  AnnotatedTask.JSON_PROPERTY_FILENAME,
  AnnotatedTask.JSON_PROPERTY_ANNOTATION_BATCH
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-02-26T18:28:50.570918+01:00[Europe/Paris]")
public class AnnotatedTask implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_ANNOTATOR_ID = "annotatorId";
  private String annotatorId;

  public static final String JSON_PROPERTY_FILENAME = "filename";
  private String filename;

  public static final String JSON_PROPERTY_ANNOTATION_BATCH = "annotationBatch";
  private AnnotationBatch annotationBatch;


  public AnnotatedTask id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getId() {
    return id;
  }


  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setId(String id) {
    this.id = id;
  }


  public AnnotatedTask annotatorId(String annotatorId) {
    this.annotatorId = annotatorId;
    return this;
  }

   /**
   * Get annotatorId
   * @return annotatorId
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_ANNOTATOR_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getAnnotatorId() {
    return annotatorId;
  }


  @JsonProperty(JSON_PROPERTY_ANNOTATOR_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAnnotatorId(String annotatorId) {
    this.annotatorId = annotatorId;
  }


  public AnnotatedTask filename(String filename) {
    this.filename = filename;
    return this;
  }

   /**
   * Get filename
   * @return filename
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_FILENAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFilename() {
    return filename;
  }


  @JsonProperty(JSON_PROPERTY_FILENAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFilename(String filename) {
    this.filename = filename;
  }


  public AnnotatedTask annotationBatch(AnnotationBatch annotationBatch) {
    this.annotationBatch = annotationBatch;
    return this;
  }

   /**
   * Get annotationBatch
   * @return annotationBatch
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_ANNOTATION_BATCH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public AnnotationBatch getAnnotationBatch() {
    return annotationBatch;
  }


  @JsonProperty(JSON_PROPERTY_ANNOTATION_BATCH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAnnotationBatch(AnnotationBatch annotationBatch) {
    this.annotationBatch = annotationBatch;
  }


  /**
   * Return true if this AnnotatedTask object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnnotatedTask annotatedTask = (AnnotatedTask) o;
    return Objects.equals(this.id, annotatedTask.id) &&
        Objects.equals(this.annotatorId, annotatedTask.annotatorId) &&
        Objects.equals(this.filename, annotatedTask.filename) &&
        Objects.equals(this.annotationBatch, annotatedTask.annotationBatch);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, annotatorId, filename, annotationBatch);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnnotatedTask {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    annotatorId: ").append(toIndentedString(annotatorId)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    annotationBatch: ").append(toIndentedString(annotationBatch)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

