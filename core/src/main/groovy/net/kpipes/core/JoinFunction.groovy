package net.kpipes.core

import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.core.function.FunctionInitializer
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.commons.lang3.NotImplementedException
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes

import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore

class JoinFunction implements EventMappingFunction, FunctionInitializer {

    private final BrokerAdmin brokerAdmin

    JoinFunction(BrokerAdmin brokerAdmin) {
        this.brokerAdmin = brokerAdmin
    }

    @Override
    void initialize(PipeBuilder pipeBuilder, PipeDefinition pipeDefinition) {
        def joinEvent = pipeDefinition.functionConfiguration().onEvent as String
        brokerAdmin.ensureTopicExists(joinEvent)
        def joinEventTable = pipeBuilder.@sourceTables[joinEvent]
        if(joinEventTable == null) {
            def table = pipeBuilder.@builder.table(joinEvent, joinEvent)
            pipeBuilder.@sourceTables[joinEvent] = table
        }
    }

    @Override
    Map<String, Object> onEvent(Event event) {
        def config = event.config()
        def onEvent = config.onEvent as String
        def onProperty = config.onProperty as String
        def streams = event.kafkaStreams()
        def key = event.body()[onProperty] as String
        def host = streams.metadataForKey(onEvent, key, Serdes.String().serializer())
        if(host.host() == 'unavailable') {
            def store = streams.store(onEvent, keyValueStore())
            def joinedEventBytes = store.get(key) as Bytes
            def joinedEvent = event.serviceRegistry().service(EventEncoder).decode(joinedEventBytes)
            event.body()[onProperty] = joinedEvent
        } else {
            throw new NotImplementedException('Remote invocation is not supported yet.')
        }
        event.body()
    }

}