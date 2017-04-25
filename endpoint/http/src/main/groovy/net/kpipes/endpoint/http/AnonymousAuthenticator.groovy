package net.kpipes.endpoint.http

class AnonymousAuthenticator implements Authenticator {

    @Override
    Optional<AuthenticationResult> authenticate(Map<String, Object> headers) {
        def username = headers.username
        def password = headers.password
        if(username == 'anonymous' && password == 'anonymous') {
            Optional.of(new AuthenticationResult('anonymous', 'anonymous'))
        } else {
            Optional.empty()
        }
    }

}
