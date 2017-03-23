package net.kpipes.core

interface ServiceRegistry {

    Object service(String id)

    def <T> T service(Class<T> type)

    def <T> List<T> services(Class<T> type)

    void stop()
}