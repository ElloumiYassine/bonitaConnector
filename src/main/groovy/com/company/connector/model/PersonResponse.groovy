package com.company.connector.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class PersonResponse implements Serializable {

    @JsonProperty("results")
    List<Person> persons = []
}