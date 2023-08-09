package com.company.connector.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Person implements Serializable {

    String name

    String gender

    String height

    String homeworld
}