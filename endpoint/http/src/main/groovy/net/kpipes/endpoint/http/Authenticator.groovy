package net.kpipes.endpoint.http

interface Authenticator {

    Optional<AuthenticationResult> authenticate(Map<String, Object> headers)

}