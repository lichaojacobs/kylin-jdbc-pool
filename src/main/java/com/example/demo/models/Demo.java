package com.example.demo.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author lichao
 * @date 2017/11/12
 */
@Data
public class Demo {

  @SerializedName("device_id")
  private String device;
  private String deviceType;
  private String eventType;
  private String eventDate;
}
