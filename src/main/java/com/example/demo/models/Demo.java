package com.example.demo.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * Created by lichao on 2017/6/28.
 */
@Data
public class Demo {

  @SerializedName("device_id")
  private String device;
  private String deviceType;
  private String eventType;
  private String eventDate;
}
