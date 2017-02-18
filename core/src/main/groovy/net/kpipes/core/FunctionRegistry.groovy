package net.kpipes.core

interface FunctionRegistry {

    Object service(String id)

    def <T> T service(Class<T> type)

}