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

import app.bpartners.annotator.endpoint.rest.OpenapiGenerated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;

/** TaskStatistics */
@JsonPropertyOrder({
  TaskStatistics.JSON_PROPERTY_REMAINING_TASKS_FOR_USER_ID,
  TaskStatistics.JSON_PROPERTY_REMAINING_TASKS,
  TaskStatistics.JSON_PROPERTY_COMPLETED_TASKS_BY_USER_ID,
  TaskStatistics.JSON_PROPERTY_TOTAL_TASKS
})
@OpenapiGenerated
public class TaskStatistics implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String JSON_PROPERTY_REMAINING_TASKS_FOR_USER_ID = "remainingTasksForUserId";
  private Long remainingTasksForUserId;

  public static final String JSON_PROPERTY_REMAINING_TASKS = "remainingTasks";
  private Long remainingTasks;

  public static final String JSON_PROPERTY_COMPLETED_TASKS_BY_USER_ID = "completedTasksByUserId";
  private Long completedTasksByUserId;

  public static final String JSON_PROPERTY_TOTAL_TASKS = "totalTasks";
  private Long totalTasks;

  public TaskStatistics remainingTasksForUserId(Long remainingTasksForUserId) {
    this.remainingTasksForUserId = remainingTasksForUserId;
    return this;
  }

  /**
   * Get remainingTasksForUserId
   *
   * @return remainingTasksForUserId
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_REMAINING_TASKS_FOR_USER_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Long getRemainingTasksForUserId() {
    return remainingTasksForUserId;
  }

  @JsonProperty(JSON_PROPERTY_REMAINING_TASKS_FOR_USER_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRemainingTasksForUserId(Long remainingTasksForUserId) {
    this.remainingTasksForUserId = remainingTasksForUserId;
  }

  public TaskStatistics remainingTasks(Long remainingTasks) {
    this.remainingTasks = remainingTasks;
    return this;
  }

  /**
   * Get remainingTasks
   *
   * @return remainingTasks
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_REMAINING_TASKS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Long getRemainingTasks() {
    return remainingTasks;
  }

  @JsonProperty(JSON_PROPERTY_REMAINING_TASKS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRemainingTasks(Long remainingTasks) {
    this.remainingTasks = remainingTasks;
  }

  public TaskStatistics completedTasksByUserId(Long completedTasksByUserId) {
    this.completedTasksByUserId = completedTasksByUserId;
    return this;
  }

  /**
   * Get completedTasksByUserId
   *
   * @return completedTasksByUserId
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_COMPLETED_TASKS_BY_USER_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Long getCompletedTasksByUserId() {
    return completedTasksByUserId;
  }

  @JsonProperty(JSON_PROPERTY_COMPLETED_TASKS_BY_USER_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCompletedTasksByUserId(Long completedTasksByUserId) {
    this.completedTasksByUserId = completedTasksByUserId;
  }

  public TaskStatistics totalTasks(Long totalTasks) {
    this.totalTasks = totalTasks;
    return this;
  }

  /**
   * Get totalTasks
   *
   * @return totalTasks
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_TOTAL_TASKS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Long getTotalTasks() {
    return totalTasks;
  }

  @JsonProperty(JSON_PROPERTY_TOTAL_TASKS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTotalTasks(Long totalTasks) {
    this.totalTasks = totalTasks;
  }

  /** Return true if this TaskStatistics object is equal to o. */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaskStatistics taskStatistics = (TaskStatistics) o;
    return Objects.equals(this.remainingTasksForUserId, taskStatistics.remainingTasksForUserId)
        && Objects.equals(this.remainingTasks, taskStatistics.remainingTasks)
        && Objects.equals(this.completedTasksByUserId, taskStatistics.completedTasksByUserId)
        && Objects.equals(this.totalTasks, taskStatistics.totalTasks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        remainingTasksForUserId, remainingTasks, completedTasksByUserId, totalTasks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TaskStatistics {\n");
    sb.append("    remainingTasksForUserId: ")
        .append(toIndentedString(remainingTasksForUserId))
        .append("\n");
    sb.append("    remainingTasks: ").append(toIndentedString(remainingTasks)).append("\n");
    sb.append("    completedTasksByUserId: ")
        .append(toIndentedString(completedTasksByUserId))
        .append("\n");
    sb.append("    totalTasks: ").append(toIndentedString(totalTasks)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
