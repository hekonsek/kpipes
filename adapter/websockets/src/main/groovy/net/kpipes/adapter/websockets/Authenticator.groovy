package net.kpipes.adapter.websockets

interface Authenticator {

    Optional<AuthenticationResult> authenticate(Map<String, Object> headers)

}