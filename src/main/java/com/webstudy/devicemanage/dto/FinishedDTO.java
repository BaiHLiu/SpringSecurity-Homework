package com.webstudy.devicemanage.dto;

import com.webstudy.devicemanage.repository.UserRepository;

public class FinishedDTO {
    private Long finishedTime;

    public Long getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Long finishedTime) {
        this.finishedTime = finishedTime;
    }

}
