package nl.tudelft.sem.template.ta.models;

import lombok.Data;

import java.util.UUID;

@Data
public class AcceptHoursRequestModel {
    private UUID id;
    private boolean accept;
}