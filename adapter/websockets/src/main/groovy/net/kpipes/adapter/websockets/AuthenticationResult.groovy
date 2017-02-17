package net.kpipes.adapter.websockets

class AuthenticationResult {

    String username

    String tenant

    AuthenticationResult(String username, String tenant) {
        this.username = username
        this.tenant = tenant
    }

    String username() {
        return username
    }

    String tenant() {
        return tenant
    }

}
