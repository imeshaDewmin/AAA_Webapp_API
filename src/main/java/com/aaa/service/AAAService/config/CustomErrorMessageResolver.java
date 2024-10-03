package com.aaa.service.AAAService.config;

import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import com.aaa.service.AAAService.utilities.ResponseCode;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomErrorMessageResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof UsernameOrEmailAlreadyExistedException usernameOrEmailAlreadyExistedException) {
            ResponseCode responseCode = usernameOrEmailAlreadyExistedException.getResponseCode();
            return buildGraphQLError(
                    ErrorType.BAD_REQUEST,
                    responseCode.getMessage(),
                    Map.of(
                            "errorCode", responseCode,
                            "message", responseCode.getMessage()
                    )
            );
        }

        return buildGraphQLError(
                ErrorType.INTERNAL_ERROR,
                "An unexpected error occurred",
                Map.of("errorCode", "INTERNAL_ERROR")
        );
    }

    private GraphQLError buildGraphQLError(ErrorType errorType, String message, Map<String, Object> extensions) {
        return GraphQLError.newError()
                .errorType(errorType)
                .message(message)
                .extensions(extensions)
                .build();
    }
}
