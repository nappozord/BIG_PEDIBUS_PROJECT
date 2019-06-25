package com.alessandro.napoletano.springbootoauth2demov2.model.child;

import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class TempForChildDeafult {
    private StopLine stop_going;
    private StopLine stop_return;
}
