package ru.sparural.utils.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import okhttp3.*;
import org.slf4j.Logger;
import ru.sparural.utils.RestTemplateConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Vorobyev Vyacheslav
 */
public class RestTemplate {

    private static final String DEFAULT_CONTENT_TYPE = RestTemplateConstants.MediaTypes.APPLICATION_JSON_UTF_8;

    private final OkHttpClient okHttpClient;
    private final JacksonObjectMapperUtils jacksonObjectMapperUtils;
    private final Logger logger;

    public RestTemplate(ObjectMapper objectMapper, Logger logger) {
        this.jacksonObjectMapperUtils = new JacksonObjectMapperUtils(objectMapper);
        this.logger = logger;
        okHttpClient = new OkHttpClient().newBuilder().build();
    }

    public RestTemplate(Logger logger) {
        this(new ObjectMapper(), logger);
    }

    public JacksonObjectMapperUtils getJsonConverter() {
        return jacksonObjectMapperUtils;
    }

    public RequestBuilder request() {
        return new RequestBuilder();
    }

    private Request.Builder requestWithHeadersBuilder(Map<String, String> headers) {
        var builder = new Request.Builder();
        headers.forEach(builder::addHeader);
        return builder;
    }

    private <T> RequestBody buildRequestBody(T body, String mediaType) {
        RequestBody reqBody;
        if (body != null)
            reqBody = RequestBody.create(MediaType.get(mediaType),
                    jacksonObjectMapperUtils.convertToByte(body));
        else reqBody = RequestBody.create(null, new byte[0]);
        return reqBody;
    }

    private <T> Request buildPostRequest(String url, T body, Map<String, String> headers) {
        var requestBody = buildRequestBody(body, getContentType(headers));
        return requestWithHeadersBuilder(headers).post(requestBody).url(url).build();
    }

    private <T> Request buildPutRequest(String url, T body, Map<String, String> headers) {
        var reqBody = buildRequestBody(body, getContentType(headers));
        return requestWithHeadersBuilder(headers).put(reqBody).url(url).build();
    }

    private String getContentType(Map<String, String> headers) {
        return headers.getOrDefault(RestTemplateConstants.HeadersKeys.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
    }

    public class RequestBuilder {
        private Map<String, String> headers;
        private Consumer<RestResponse> failureCallback;
        private MessageProcessor messageProcessor;

        public RequestBuilder() {
            failureCallback = System.out::println;
            this.headers = new HashMap<>();
            messageProcessor = new DefaultMessageProcessor() {
                @Override
                public void onSuccess(RestResponse response) {
                }
            };
        }

        public RequestBuilder withHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestBuilder addHeader(String key, String value) {
            if (this.headers != null)
                this.headers.put(key, value);
            return this;
        }

        public RequestBuilder withContentType(String type) {
            this.headers.put(RestTemplateConstants.HeadersKeys.CONTENT_TYPE, type);
            return this;
        }

        /**
         * Use withAuthorization(String authType, String token)
         */
        @Deprecated
        public RequestBuilder withAuthorizationBearer(String token) {
            this.headers.put(RestTemplateConstants.HeadersKeys.AUTHORIZATION, "Bearer " + token);
            return this;
        }

        public RequestBuilder withAuthorizationHeader(String authType, String value) {
            this.headers.put(RestTemplateConstants.HeadersKeys.AUTHORIZATION, authType + " " + value);
            return this;
        }

        public RequestBuilder withCaptchaToken(String captchaToken) {
            this.headers.put(RestTemplateConstants.HeadersKeys.X_CAPTCHA_RESPONSE, captchaToken);
            return this;
        }

        public RequestBuilder withFailureCallback(Consumer<RestResponse> failureCallback) {
            this.failureCallback = failureCallback;
            return this;
        }

        public RequestBuilder withMessageProcessor(MessageProcessor messageProcessor) {
            this.messageProcessor = messageProcessor;
            return this;
        }

        public <T> RestTemplateBuilder<T> setResponseType(Class<T> toType) {
            return new ClassRefConverterRequestBuilder<>(toType, headers, failureCallback);
        }

        public <T> RestTemplateBuilder<T> setResponseType(TypeReference<T> toType) {
            return new TypeRefConverterRequestBuilder<>(toType, headers, failureCallback);
        }

        public void get(String url) {
            var req = requestWithHeadersBuilder(headers).url(url).get().build();
            defaultRequest(req, messageProcessor);
        }

        public void post(String url) {
            var reqBody = RequestBody.create(null, new byte[0]);
            var req = requestWithHeadersBuilder(headers).url(url).post(reqBody).build();
            defaultRequest(req, messageProcessor);
        }

        public <T> void post(String url, T body) {
            var req = buildPostRequest(url, body, headers);
            defaultRequest(req, messageProcessor);
        }

        private void defaultRequest(Request request, MessageProcessor messageProcessor) {
            var resp = defaultHttpRequest(okHttpClient, request, logger);
            if (successResponseCondition(resp.getCode())) {
                messageProcessor.onSuccess(resp);
            } else messageProcessor.onFailure(resp);
        }
    }

    private class TypeRefConverterRequestBuilder<R> extends DefaultRestTemplateBuilder<R> {
        private final TypeReference<R> typeReference;

        public TypeRefConverterRequestBuilder(TypeReference<R> typeReference,
                                              Map<String, String> headers,
                                              Consumer<RestResponse> failureCallback) {
            super(headers, failureCallback);
            this.typeReference = typeReference;
        }

        @Override
        protected Function<byte[], R> getConvertFunction() {
            return body -> jacksonObjectMapperUtils.convert(body, typeReference);
        }
    }

    private class ClassRefConverterRequestBuilder<R> extends DefaultRestTemplateBuilder<R> {
        private final Class<R> cl;

        public ClassRefConverterRequestBuilder(Class<R> cl, Map<String, String> headers,
                                               Consumer<RestResponse> failureCallback) {
            super(headers, failureCallback);
            this.cl = cl;
        }

        protected Function<byte[], R> getConvertFunction() {
            return body -> jacksonObjectMapperUtils.convert(body, cl);
        }
    }

    private class DefaultRestTemplateBuilder<X> extends RestTemplateBuilder<X> {
        public DefaultRestTemplateBuilder(Map<String, String> headers, Consumer<RestResponse> failureCallback) {
            super(headers, failureCallback);
        }

        @Override
        protected Function<byte[], X> getConvertFunction() {
            return null;
        }
    }

    @Getter
    public abstract class RestTemplateBuilder<X> {
        protected Map<String, String> headers;
        protected Consumer<RestResponse> failureCallback;
        protected Function<RestResponse, RuntimeException> convertExceptionHandler;

        public RestTemplateBuilder(Map<String, String> headers, Consumer<RestResponse> failureCallback) {
            this.headers = headers;
            this.failureCallback = failureCallback;
            convertExceptionHandler = x -> {
                throw new RuntimeException(x.toString());
            };
        }

        public RestTemplateBuilder<X> withConvertException(Function<RestResponse, RuntimeException> convertExceptionHandler) {
            this.convertExceptionHandler = convertExceptionHandler;
            return this;
        }

        public Optional<X> getForEntity(String url) {
            var req = requestWithHeadersBuilder(headers).url(url).get().build();
            return defaultConvertRequest(req, failureCallback, convertExceptionHandler);
        }

        /**
         * Use {@link RequestBuilder#withContentType(String)} combined with
         * {@link RestTemplateBuilder#postForEntity(String, Object)} instead
         */
        @Deprecated
        public <R> Optional<X> postForEntity(String url, R body, String mediaType) {
            this.headers.put(RestTemplateConstants.HeadersKeys.CONTENT_TYPE, mediaType);
            var req = buildPostRequest(url, body, this.headers);
            return defaultConvertRequest(req, failureCallback, convertExceptionHandler);
        }

        public <R> Optional<X> postForEntity(String url, R body) {
            var req = buildPostRequest(url, body, this.headers);
            return defaultConvertRequest(req, failureCallback, convertExceptionHandler);
        }

        public Optional<X> postForEntity(String url) {
            var req = buildPostRequest(url, null, this.headers);
            return defaultConvertRequest(req, failureCallback, convertExceptionHandler);
        }

        public <R> Optional<X> putForEntity(String url, R body) {
            var req = buildPutRequest(url, body, this.headers);
            return defaultConvertRequest(req, failureCallback, convertExceptionHandler);
        }

        public Optional<X> putForEntity(String url) {
            var req = buildPutRequest(url, null, this.headers);
            return defaultConvertRequest(req, failureCallback, convertExceptionHandler);
        }

        protected abstract Function<byte[], X> getConvertFunction();

        private Optional<X> defaultConvertRequest(Request request,
                                                  Consumer<RestResponse> consumer,
                                                  Function<RestResponse, RuntimeException> convertExceptionHandler) {
            var resp = defaultHttpRequest(okHttpClient, request, logger);
            if (successResponseCondition(resp.getCode())) {
                try {
                    return Optional.of(getConvertFunction().apply(resp.getBody()));
                } catch (Exception e) {
                    throw convertExceptionHandler.apply(resp);
                }
            } else {
                consumer.accept(resp);
            }
            return Optional.empty();
        }
    }

    private boolean successResponseCondition(Integer code) {
        return code >= 200 && code < 300;
    }

    private RestResponse defaultHttpRequest(OkHttpClient client, Request request, Logger logger) {
        var logId = UUID.randomUUID().toString();
        logger.debug("REST HTTP REQUEST: {} |\n\t details: {}", logId, request);
        var call = client.newCall(request);
        Response resp;
        try {
            resp = call.execute();
            byte[] responseBody = resp.body() != null ? resp.body().bytes() : new byte[0];
            var code = resp.code();
            var headers = resp.headers().toMultimap();
            var response = new RestResponse(code, resp.message(), responseBody);
            response.setHeaders(headers);
            logger.debug("REST HTTP RESPONSE: {} |\n\t details:\n\t\tstatus: {}\n\t\theaders: {}\n\t\tbody: {}", logId, code, headers, new String(responseBody));
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}