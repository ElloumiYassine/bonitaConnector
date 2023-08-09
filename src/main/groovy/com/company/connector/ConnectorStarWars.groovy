package com.company.connector

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import groovy.util.logging.Slf4j
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Slf4j
class ConnectorStarWars extends AbstractConnector {

    def static final DEFAULT_INPUT = "defaultInput"
    def static final DEFAULT_OUTPUT = "defaultOutput"
    
    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/connector-starwars.def)
     * You should:
     * - validate that mandatory inputs are presents
     * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
     */
    def static final NAME_INPUT = "name"
    def static final URL_INPUT = "url"
    @Override
    void validateInputParameters() throws ConnectorValidationException {
        checkMandatoryStringInput(NAME_INPUT)
        checkMandatoryStringInput(URL_INPUT)
    }
    
    def checkMandatoryStringInput(inputName) throws ConnectorValidationException {
        def value = getInputParameter(inputName)
        if (value in String) {
            if (!value) {
                throw new ConnectorValidationException(this, "Mandatory parameter '$inputName' is missing.")
            }
        } else {
            throw new ConnectorValidationException(this, "'$inputName' parameter must be a String")
        }
    }
    
    /**
     * Core method:
     * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
     * - Set the output of the connector execution. If outputs are not set, connector fails.
     */
    def static final PERSON_OUTPUT = "person"

    @Override
    void executeBusinessLogic() throws ConnectorException {
        def name = getInputParameter(NAME_INPUT)
        log.info "$NAME_INPUT : $name"
        // Retrieve the retrofit service created during the connect phase, call the 'person' endpoint with the name parameter
        def response = getService().person(name).execute()
        if (response.isSuccessful()) {
            def persons = response.body.getPersons()
            if (!persons.isEmpty()) {
                def person = persons[0]
                setOutputParameter(PERSON_OUTPUT, person)
            } else {
                throw new ConnectorException("$name not found")
            }
        } else {
            throw new ConnectorException(response.message())
        }
    }
    
    /**
     * [Optional] Open a connection to remote server
     */

    def StarWarsService service

    @Override
    void connect() throws ConnectorException {
        def httpClient = createHttpClient(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        service = createService(httpClient getInputParameter(URL_INPUT))
    }

    static OkHttpClient createHttpClient(okhttp3.Interceptor... interceptors) {
        def clientBuilder = new OkHttpClient.Builder()
        if (interceptors) {
            interceptors.each { clientBuilder.interceptors().add(it) }
        }
        clientBuilder.build()
    }

    static StarWarsService createService(OkHttpClient client, String baseUrl) {
        new Retrofit().Builder()
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
                .create(StarWarsService.class)
    }

    /**
     * [Optional] Close connection to remote server
     */
    @Override
    void disconnect() throws ConnectorException{}
}
