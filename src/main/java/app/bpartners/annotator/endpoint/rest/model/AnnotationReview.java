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
 * AnnotationReview
 */
@JsonPropertyOrder({
  AnnotationReview.JSON_PROPERTY_ID,
  AnnotationReview.JSON_PROPERTY_COMMENT,
  AnnotationReview.JSON_PROPERTY_ANNOTATION_ID
})
@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-02-26T18:28:50.570918+01:00[Europe/Paris]")
public class AnnotationReview implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_COMMENT = "comment";
  private String comment;

  public static final String JSON_PROPERTY_ANNOTATION_ID = "annotationId";
  private String annotationId;


  public AnnotationReview id(String id) {
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


  public AnnotationReview comment(String comment) {
    this.comment = comment;
    return this;
  }

   /**
   * optional if status is ACCEPTED
   * @return comment
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "optional if status is ACCEPTED")
  @JsonProperty(JSON_PROPERTY_COMMENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getComment() {
    return comment;
  }


  @JsonProperty(JSON_PROPERTY_COMMENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setComment(String comment) {
    this.comment = comment;
  }


  public AnnotationReview annotationId(String annotationId) {
    this.annotationId = annotationId;
    return this;
  }

   /**
   * optional parameter if you want to add a review for a specific annotation and not for the whole batch
   * @return annotationId
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "optional parameter if you want to add a review for a specific annotation and not for the whole batch")
  @JsonProperty(JSON_PROPERTY_ANNOTATION_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getAnnotationId() {
    return annotationId;
  }


  @JsonProperty(JSON_PROPERTY_ANNOTATION_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAnnotationId(String annotationId) {
    this.annotationId = annotationId;
  }


  /**
   * Return true if this AnnotationReview object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnnotationReview annotationReview = (AnnotationReview) o;
    return Objects.equals(this.id, annotationReview.id) &&
        Objects.equals(this.comment, annotationReview.comment) &&
        Objects.equals(this.annotationId, annotationReview.annotationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, comment, annotationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnnotationReview {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
    sb.append("    annotationId: ").append(toIndentedString(annotationId)).append("\n");
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

